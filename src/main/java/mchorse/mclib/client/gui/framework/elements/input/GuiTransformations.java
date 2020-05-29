package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

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

            this.sy.setVisible(!one);
            this.sz.setVisible(!one);

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

    public void fillT(double x, double y, double z)
    {
        this.tx.setValue(x);
        this.ty.setValue(y);
        this.tz.setValue(z);
    }

    public void fillS(double x, double y, double z)
    {
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
    public void draw(GuiContext context)
    {
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.translate"), this.tx.area.x, this.tx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.scale"), this.sx.area.x, this.sx.area.y - 12, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("mclib.gui.transforms.rotate"), this.rx.area.x, this.rx.area.y - 12, 0xffffff);

        super.draw(context);
    }
}