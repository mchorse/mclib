package mchorse.mclib.client.gui.framework.elements.input;

import java.util.function.Consumer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.MatrixUtils.Transformation;
import mchorse.mclib.utils.MatrixUtils.RotationOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

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
    public GuiTrackpadElement localtx;
    public GuiTrackpadElement localty;
    public GuiTrackpadElement localtz;
    public GuiTrackpadElement sx;
    public GuiTrackpadElement sy;
    public GuiTrackpadElement sz;
    public GuiTrackpadElement rx;
    public GuiTrackpadElement ry;
    public GuiTrackpadElement rz;
    public GuiToggleElement one;
    
    public GuiTrackpadElement drx;
    public GuiTrackpadElement dry;
    public GuiTrackpadElement drz;
    public GuiToggleElement origin;
    public GuiCirculateElement orientation;

    /**
     * Used to update the fields when the static orientation changes.
     */
    protected TransformOrientation orientationCache;
    protected GuiElement first;
    protected GuiElement second;
    protected GuiElement third;

    public GuiTransformations(Minecraft mc)
    {
        super(mc);

        this.tx = new GuiTrackpadElement(mc, (value) -> this.internalSetT(value, this.ty.value, this.tz.value)).block();
        this.tx.tooltip(IKey.lang("mclib.gui.transforms.x"));
        this.ty = new GuiTrackpadElement(mc, (value) -> this.internalSetT(this.tx.value, value, this.tz.value)).block();
        this.ty.tooltip(IKey.lang("mclib.gui.transforms.y"));
        this.tz = new GuiTrackpadElement(mc, (value) -> this.internalSetT(this.tx.value, this.ty.value, value)).block();
        this.tz.tooltip(IKey.lang("mclib.gui.transforms.z"));

        this.sx = new GuiTrackpadElement(mc, (value) ->
        {
            boolean one = this.one.isToggled();

            this.internalSetS(value, one ? value : this.sy.value, one ? value : this.sz.value);
        });
        this.sx.tooltip(IKey.lang("mclib.gui.transforms.x"));
        this.sy = new GuiTrackpadElement(mc, (value) -> this.internalSetS(this.sx.value, value, this.sz.value));
        this.sy.tooltip(IKey.lang("mclib.gui.transforms.y"));
        this.sz = new GuiTrackpadElement(mc, (value) -> this.internalSetS(this.sx.value, this.sy.value, value));
        this.sz.tooltip(IKey.lang("mclib.gui.transforms.z"));

        this.rx = new GuiTrackpadElement(mc, (value) -> this.internalSetR(value, this.ry.value, this.rz.value)).degrees();
        this.rx.tooltip(IKey.lang("mclib.gui.transforms.x"));
        this.ry = new GuiTrackpadElement(mc, (value) -> this.internalSetR(this.rx.value, value, this.rz.value)).degrees();
        this.ry.tooltip(IKey.lang("mclib.gui.transforms.y"));
        this.rz = new GuiTrackpadElement(mc, (value) -> this.internalSetR(this.rx.value, this.ry.value, value)).degrees();
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
        
        this.drx = new GuiRelativeTrackpadElement(mc, (value) -> this.deltaRotate(value, 0, 0), IKey.str("Rx")).degrees();
        this.dry = new GuiRelativeTrackpadElement(mc, (value) -> this.deltaRotate(0, value, 0), IKey.str("Ry")).degrees();
        this.drz = new GuiRelativeTrackpadElement(mc, (value) -> this.deltaRotate(0, 0, value), IKey.str("Rz")).degrees();
        this.origin = new GuiToggleElement(mc, IKey.EMPTY, false, null);
        this.origin.flex().relative(this.drx).x(1F).y(-13).wh(11, 11).anchorX(1F);
        this.origin.tooltip(IKey.lang("mclib.gui.transforms.delta.origin"));

        this.localtx = new GuiRelativeTrackpadElement(mc, (value) -> this.localTranslate(value, 0, 0), IKey.str("X")).block();
        this.localty = new GuiRelativeTrackpadElement(mc, (value) -> this.localTranslate(0, value, 0), IKey.str("Y")).block();
        this.localtz = new GuiRelativeTrackpadElement(mc, (value) -> this.localTranslate(0, 0, value), IKey.str("Z")).block();
        this.orientation = new GuiStaticTransformOrientation(mc, (value) ->
        {
            this.updateFields();
        });
        this.orientation.addLabel(IKey.lang("mclib.gui.transforms.orientation.global"));
        this.orientation.addLabel(IKey.lang("mclib.gui.transforms.orientation.local"));
        this.orientation.tooltip(IKey.lang("mclib.gui.transforms.orientation.tooltip"));
        this.orientation.flex().relative(this.tx).set(0, -44, 60, 20);
        
        this.first = new GuiElement(mc);
        this.second = new GuiElement(mc);
        this.third = new GuiElement(mc);

        this.first.flex().relative(this).w(1.25F).h(20).row(5).height(20);
        this.first.add(this.tx, sx, rx, drx);

        this.second.flex().relative(this).y(0.5F, -10).w(1.25F).h(20).row(5).height(20);
        this.second.add(this.ty, sy, ry, dry);

        this.third.flex().relative(this).y(1F, -20).w(1.25F).h(20).row(5).height(20);
        this.third.add(this.tz, sz, rz, drz);

        this.add(this.first, this.second, this.third, this.orientation, this.one, this.origin);
    }

    protected void updateFields()
    {
        this.tx.removeFromParent();
        this.ty.removeFromParent();
        this.tz.removeFromParent();
        this.localtx.removeFromParent();
        this.localty.removeFromParent();
        this.localtz.removeFromParent();

        if (GuiStaticTransformOrientation.getOrientation() == TransformOrientation.GLOBAL)
        {
            this.first.addBefore(this.sx, this.tx);
            this.second.addBefore(this.sy, this.ty);
            this.third.addBefore(this.sz, this.tz);
        }
        else if (GuiStaticTransformOrientation.getOrientation() == TransformOrientation.LOCAL)
        {
            this.first.addBefore(this.sx, this.localtx);
            this.second.addBefore(this.sy, this.localty);
            this.third.addBefore(this.sz, this.localtz);
        }

        this.parent.resize();
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

    public void fillSetT(double x, double y, double z)
    {
        this.fillT(x, y, z);
        this.setT(x, y, z);
    }

    public void fillSetS(double x, double y, double z)
    {
        this.fillS(x, y, z);
        this.setS(x, y, z);
    }

    public void fillSetR(double x, double y, double z)
    {
        this.fillR(x, y, z);
        this.setR(x, y, z);
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
    
    private void internalSetT(double x, double y, double z)
    {
        try
        {
            this.setT(x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void internalSetS(double x, double y, double z)
    {
        try
        {
            this.setS(x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void internalSetR(double x, double y, double z)
    {
        try
        {
            this.setR(x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            NBTTagCompound tag = JsonToNBT.getTagFromJson("{Transforms:"+ GuiScreen.getClipboardString()+"}");
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

        menu.action(Icons.CLOSE, IKey.lang("mclib.gui.transforms.context.reset"), this::reset);

        return menu;
    }

    private void copyTransformations()
    {
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

        GuiScreen.setClipboardString(list.toString());
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

    protected void reset()
    {
        this.fillSetT(0, 0, 0);
        this.fillSetS(1, 1, 1);
        this.fillSetR(0, 0, 0);
    }
    
    protected void prepareRotation(Matrix4f mat)
    {
        Matrix4f rot = new Matrix4f();
        rot.rotZ((float) Math.toRadians(this.rz.value));
        mat.mul(rot);
        rot.rotY((float) Math.toRadians(this.ry.value));
        mat.mul(rot);
        rot.rotX((float) Math.toRadians(this.rx.value));
        mat.mul(rot);
    }
    
    protected void postRotation(Transformation transform)
    {
        Vector3f result = transform.getRotation(RotationOrder.XYZ, new Vector3f((float) this.rx.value, (float) this.ry.value, (float) this.rz.value));
        this.rx.setValueAndNotify(result.x);
        this.ry.setValueAndNotify(result.y);
        this.rz.setValueAndNotify(result.z);
    }

    protected void localTranslate(double x, double y, double z)
    { }

    protected void deltaRotate(double x, double y, double z)
    {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        if (this.origin.isToggled())
        {
            mat.m03 = (float) this.tx.value;
            mat.m13 = (float) this.ty.value;
            mat.m23 = (float) this.tz.value;
        }
        Matrix4f rot = new Matrix4f();
        rot.rotZ((float) Math.toRadians(z));
        mat.mul(rot, mat);
        rot.rotY((float) Math.toRadians(y));
        mat.mul(rot, mat);
        rot.rotX((float) Math.toRadians(x));
        mat.mul(rot, mat);
        prepareRotation(mat);
        Transformation transform = MatrixUtils.extractTransformations(null, mat);
        if (this.origin.isToggled())
        {
            Vector3f result = transform.getTranslation3f();
            this.tx.setValueAndNotify(result.x);
            this.ty.setValueAndNotify(result.y);
            this.tz.setValueAndNotify(result.z);
        }
        postRotation(transform);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);

        if (this.orientationCache != GuiStaticTransformOrientation.getOrientation())
        {
            this.updateFields();
        }

        this.orientationCache = GuiStaticTransformOrientation.getOrientation();

        if (McLib.renderTranslateTextColors.get())
        {
            this.tx.setTextColor(new Color(1,0.5F,0.5F));
            this.ty.setTextColor(new Color(0.5F,1,0.5F));
            this.tz.setTextColor(new Color(0.5F,0.5F,1));

            this.localtx.setTextColor(new Color(1,0.5F,0.5F));
            this.localty.setTextColor(new Color(0.5F,1,0.5F));
            this.localtz.setTextColor(new Color(0.5F,0.5F,1));
        }
        else
        {
            Color color = new Color(14737632);

            this.tx.setTextColor(color);
            this.ty.setTextColor(color);
            this.tz.setTextColor(color);
            this.localtx.setTextColor(color);
            this.localty.setTextColor(color);
            this.localtz.setTextColor(color);
        }

        super.draw(context);
    }
    
    public static class GuiRelativeTrackpadElement extends GuiTrackpadElement
    {
        public IKey label;
        public double lastValue;
        
        public GuiRelativeTrackpadElement(Minecraft mc, Consumer<Double> callback, IKey label)
        {
            super(mc, callback);
            this.label = label;
            this.setText(label.get());
            this.setTextColor(0xE0E0E0);
        }

        @Override
        public void mouseReleased(GuiContext context)
        {
            super.mouseReleased(context);
            this.lastValue = 0;
            this.setValue(0);
            this.setText(this.label.get());
        }

        @Override
        public void setValueAndNotify(double value)
        {
            this.setValue(value);

            if (this.callback != null)
            {
                this.callback.accept(this.value - this.lastValue);
                this.lastValue = this.value;
            }
        }
    }

    /**
     * A GuiCirculateElement that has a static value. This class overrides the methods that access the value attribute
     */
    public static class GuiStaticTransformOrientation extends GuiCirculateElement
    {
        protected static int value = 0;

        public GuiStaticTransformOrientation(Minecraft mc, Consumer<GuiCirculateElement> callback)
        {
            super(mc, callback);
        }

        public static TransformOrientation getOrientation()
        {
            return TransformOrientation.values()[value];
        }

        @Override
        public int getValue()
        {
            return value;
        }

        @Override
        public String getLabel()
        {
            return this.labels.get(value).get();
        }

        @Override
        public void setValue(int newValue, int direction)
        {
            value = newValue;

            if (this.disabled.contains(newValue))
            {
                this.setValue(newValue + direction, direction);

                return;
            }

            if (value > this.labels.size() - 1)
            {
                value = 0;
            }

            if (value < 0)
            {
                value = this.labels.size() - 1;
            }
        }

        @Override
        protected void click(int mouseButton)
        {
            int direction = mouseButton == 0 ? 1 : -1;

            this.setValue(value + direction, direction);

            if (this.callback != null)
            {
                this.callback.accept(this.get());
            }
        }
    }

    public enum TransformOrientation
    {
        GLOBAL,
        LOCAL
    }
}