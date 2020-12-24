package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Transformation editor GUI
 * 
 * Must be exactly 190 by 70 (with extra 12 on top for labels)
 */
public class GuiTransformations extends GuiElement
{
    public GuiTrackpadElement tx;
    public GuiTrackpadElement ty;
    public GuiTrackpadElement tz;
    public GuiTrackpadElement sx;
    public GuiTrackpadElement sy;
    public GuiTrackpadElement sz;
    public GuiTrackpadElement rx;
    public GuiTrackpadElement ry;
    public GuiTrackpadElement rz;
    public GuiToggleElement one;

    public GuiTransformations(Minecraft mc)
    {
        super(mc);

        this.tx = new GuiTrackpadElement(mc, (value) -> this.setT(value, this.ty.value, this.tz.value)).block();
        this.tx.tooltip(IKey.lang("mclib.gui.transforms.x"));
        this.ty = new GuiTrackpadElement(mc, (value) -> this.setT(this.tx.value, value, this.tz.value)).block();
        this.ty.tooltip(IKey.lang("mclib.gui.transforms.y"));
        this.tz = new GuiTrackpadElement(mc, (value) -> this.setT(this.tx.value, this.ty.value, value)).block();
        this.tz.tooltip(IKey.lang("mclib.gui.transforms.z"));

        this.sx = new GuiTrackpadElement(mc, (value) ->
        {
            boolean one = this.one.isToggled();

            this.setS(value, one ? value : this.sy.value, one ? value : this.sz.value);
        });
        this.sx.tooltip(IKey.lang("mclib.gui.transforms.x"));
        this.sy = new GuiTrackpadElement(mc, (value) -> this.setS(this.sx.value, value, this.sz.value));
        this.sy.tooltip(IKey.lang("mclib.gui.transforms.y"));
        this.sz = new GuiTrackpadElement(mc, (value) -> this.setS(this.sx.value, this.sy.value, value));
        this.sz.tooltip(IKey.lang("mclib.gui.transforms.z"));

        this.rx = new GuiTrackpadElement(mc, (value) -> this.setR(value, this.ry.value, this.rz.value)).degrees();
        this.rx.tooltip(IKey.lang("mclib.gui.transforms.x"));
        this.ry = new GuiTrackpadElement(mc, (value) -> this.setR(this.rx.value, value, this.rz.value)).degrees();
        this.ry.tooltip(IKey.lang("mclib.gui.transforms.y"));
        this.rz = new GuiTrackpadElement(mc, (value) -> this.setR(this.rx.value, this.ry.value, value)).degrees();
        this.rz.tooltip(IKey.lang("mclib.gui.transforms.z"));
        this.one = new GuiToggleElement(mc, IKey.EMPTY, false, (b) ->
        {
            boolean one = b.isToggled();

            this.updateScaleFields();

            if (!one)
            {
                this.sy.setValueAndNotify(this.sx.value);
                this.sz.setValueAndNotify(this.sx.value);
            }
        });

        this.one.flex().relative(this.sx).x(1F).y(-13).wh(11, 11).anchorX(1F);

        GuiElement first = new GuiElement(mc);
        GuiElement second = new GuiElement(mc);
        GuiElement third = new GuiElement(mc);

        first.flex().relative(this).w(1F).h(20).row(5).height(20);
        first.add(this.tx, sx, rx);

        second.flex().relative(this).y(0.5F, -10).w(1F).h(20).row(5).height(20);
        second.add(this.ty, sy, ry);

        third.flex().relative(this).y(1F, -20).w(1F).h(20).row(5).height(20);
        third.add(this.tz, sz, rz);

        this.add(first, second, third, this.one);
    }

    public void resetScale()
    {
        this.one.toggled(false);
        this.updateScaleFields();
    }

    public void updateScaleFields()
    {
        this.sy.setVisible(!this.one.isToggled());
        this.sz.setVisible(!this.one.isToggled());
    }

    public void fillT(double x, double y, double z)
    {
        this.tx.setValue(x);
        this.ty.setValue(y);
        this.tz.setValue(z);
    }

    public void fillS(double x, double y, double z)
    {
        this.resetScale();

        this.sx.setValue(x);
        this.sy.setValue(y);
        this.sz.setValue(z);
    }

    public void fillR(double x, double y, double z)
    {
        this.rx.setValue(x);
        this.ry.setValue(y);
        this.rz.setValue(z);
    }

    public void setT(double x, double y, double z)
    {}

    public void setS(double x, double y, double z)
    {}

    public void setR(double x, double y, double z)
    {}

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(context.mc);
        NBTTagList transforms = null;

        try
        {
            NBTTagCompound tag = JsonToNBT.getTagFromJson(GuiScreen.getClipboardString());
            NBTTagList list = tag.getTagList("Transforms", Constants.NBT.TAG_DOUBLE);

            if (list.tagCount() >= 9)
            {
                transforms = list;
            }
        }
        catch (Exception e)
        {}

        menu.action(Icons.COPY, IKey.lang("mclib.gui.transforms.context.copy"), () -> this.copyTransformations());

        if (transforms != null)
        {
            final NBTTagList innerList = transforms;

            menu.action(Icons.PASTE, IKey.lang("mclib.gui.transforms.context.paste"), () -> this.pasteAll(innerList));
            menu.action(Icons.ALL_DIRECTIONS, IKey.lang("mclib.gui.transforms.context.paste_translation"), () -> this.pasteTranslation(innerList));
            menu.action(Icons.MAXIMIZE, IKey.lang("mclib.gui.transforms.context.paste_scale"), () -> this.pasteScale(innerList));
            menu.action(Icons.REFRESH, IKey.lang("mclib.gui.transforms.context.paste_rotation"), () -> this.pasteRotation(innerList));
        }

        return menu;
    }

    private void copyTransformations()
    {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        list.appendTag(new NBTTagDouble(this.tx.value));
        list.appendTag(new NBTTagDouble(this.ty.value));
        list.appendTag(new NBTTagDouble(this.tz.value));
        list.appendTag(new NBTTagDouble(this.sx.value));
        list.appendTag(new NBTTagDouble(this.sy.value));
        list.appendTag(new NBTTagDouble(this.sz.value));
        list.appendTag(new NBTTagDouble(this.rx.value));
        list.appendTag(new NBTTagDouble(this.ry.value));
        list.appendTag(new NBTTagDouble(this.rz.value));
        tag.setTag("Transforms", list);

        GuiScreen.setClipboardString(tag.toString());
    }

    public void pasteAll(NBTTagList list)
    {
        this.pasteTranslation(list);
        this.pasteScale(list);
        this.pasteRotation(list);
    }

    public void pasteTranslation(NBTTagList list)
    {
        Vector3d translation = this.getVector(list, 0);

        this.tx.setValue(translation.x);
        this.ty.setValue(translation.y);
        this.tz.setValueAndNotify(translation.z);
    }

    public void pasteScale(NBTTagList list)
    {
        Vector3d scale = this.getVector(list, 3);

        this.sz.setValue(scale.z);
        this.sy.setValue(scale.y);
        this.sx.setValueAndNotify(scale.x);
    }

    public void pasteRotation(NBTTagList list)
    {
        Vector3d rotation = this.getVector(list, 6);

        this.rx.setValue(rotation.x);
        this.ry.setValue(rotation.y);
        this.rz.setValueAndNotify(rotation.z);
    }

    private Vector3d getVector(NBTTagList list, int offset)
    {
        Vector3d result = new Vector3d();

        result.x = list.getDoubleAt(offset);
        result.y = list.getDoubleAt(offset + 1);
        result.z = list.getDoubleAt(offset + 2);

        return result;
    }

    @Override
    public void draw(GuiContext context)
    {
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);

        super.draw(context);
    }
}