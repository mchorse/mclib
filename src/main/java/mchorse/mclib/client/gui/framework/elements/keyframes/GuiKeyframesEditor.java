package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiKeyframesEditor<T extends GuiKeyframeElement> extends GuiElement
{
    public GuiElement frameButtons;
    public GuiTrackpadElement tick;
    public GuiTrackpadElement value;
    public GuiButtonElement interp;
    public GuiListElement<KeyframeInterpolation> interpolations;
    public GuiCirculateElement easing;

    public T graph;

    private int clicks;
    private long clickTimer;

    public GuiKeyframesEditor(Minecraft mc)
    {
        super(mc);

        this.frameButtons = new GuiElement(mc);
        this.frameButtons.setVisible(false);
        this.tick = new GuiTrackpadElement(mc, this::setTick);
        this.tick.limit(Integer.MIN_VALUE, Integer.MAX_VALUE, true).tooltip(IKey.lang("aperture.gui.panels.tick"));
        this.value = new GuiTrackpadElement(mc, this::setValue);
        this.value.tooltip(IKey.lang("aperture.gui.panels.value"));
        this.interp = new GuiButtonElement(mc, IKey.lang(""), (b) -> this.interpolations.toggleVisible());
        this.interpolations = new GuiKeyframeInterpolationsList(mc, (interp) -> this.pickInterpolation(interp.get(0)));

        this.easing = new GuiCirculateElement(mc, (b) -> this.changeEasing());

        for (KeyframeEasing easing : KeyframeEasing.values())
        {
            this.easing.addLabel(IKey.lang(easing.getKey()));
        }

        this.graph = this.createElement(mc);

        /* Position the elements */
        this.tick.flex().relative(this).set(0, 10, 80, 20).x(1, -90);
        this.value.flex().relative(this).set(0, 35, 80, 20).x(1, -90);

        this.interp.flex().relative(this.tick).set(-90, 0, 80, 20);
        this.easing.flex().relative(this.value).set(-90, 0, 80, 20);
        this.interpolations.flex().relative(this).set(0, 30, 80, 20).x(1, -180).h(1, -60).maxH(16 * 7);
        this.graph.flex().relative(this).set(0, 0, 0, 0).w(1, 0).h(1, 0);

        /* Add all elements */
        this.add(this.graph, this.frameButtons);
        this.frameButtons.add(this.tick, this.value, this.interp, this.easing, this.interpolations);
    }

    protected void toggleInterpolation()
    {
        Keyframe keyframe = this.graph.getCurrent();

        if (keyframe == null)
        {
            return;
        }

        KeyframeInterpolation interp = keyframe.interp;
        int factor = GuiScreen.isShiftKeyDown() ? -1 : 1;
        int index = MathUtils.cycler(interp.ordinal() + factor, 0, KeyframeInterpolation.values().length - 1);

        interp = KeyframeInterpolation.values()[index];
        this.graph.getCurrent().setInterpolation(interp);
        this.interp.label.set(interp.getKey());
        this.interpolations.setCurrent(interp);
        GuiUtils.playClick();
    }

    protected void toggleEasing()
    {
        this.easing.clickItself(GuiBase.getCurrent(), GuiScreen.isShiftKeyDown() ? 1 : 0);
    }

    protected abstract T createElement(Minecraft mc);

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.area.isInside(mouseX, mouseY))
        {
            /* On double click add or remove a keyframe */
            if (context.mouseButton == 0)
            {
                long time = System.currentTimeMillis();

                if (time - this.clickTimer < 175)
                {
                    this.clicks++;

                    if (this.clicks >= 1)
                    {
                        this.clicks = 0;
                        this.doubleClick(mouseX, mouseY);
                    }
                }
                else
                {
                    this.clicks = 0;
                }

                this.clickTimer = time;
            }
        }

        return this.area.isInside(mouseX, mouseY);
    }

    protected void doubleClick(int mouseX, int mouseY)
    {
        this.graph.doubleClick(mouseX, mouseY);
        this.fillData(this.graph.getCurrent());
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return super.mouseScrolled(context) || this.area.isInside(context.mouseX, context.mouseY);
    }

    public void setTick(double value)
    {
        this.graph.which.setX(this.graph.getCurrent(), value);
        this.graph.setSliding();
    }

    public void setValue(double value)
    {
        this.graph.which.setY(this.graph.getCurrent(), value);
    }

    public void pickInterpolation(KeyframeInterpolation interp)
    {
        if (this.graph.getCurrent() == null)
        {
            return;
        }

        this.graph.getCurrent().setInterpolation(interp);
        this.interp.label.set(interp.getKey());
        this.interpolations.setVisible(false);
    }

    public void changeEasing()
    {
        if (this.graph.getCurrent() == null)
        {
            return;
        }

        this.graph.getCurrent().setEasing(KeyframeEasing.values()[this.easing.getValue()]);
    }

    public void fillData(Keyframe frame)
    {
        boolean show = frame != null && this.graph.which != Selection.NOT_SELECTED;

        this.frameButtons.setVisible(show);

        if (!show)
        {
            return;
        }

        this.tick.integer = this.graph.which == Selection.KEYFRAME;
        this.tick.setValue(this.graph.which.getX(frame));
        this.value.setValue(this.graph.which.getY(frame));
        this.interp.label.set(frame.interp.getKey());
        this.interpolations.setCurrent(frame.interp);
        this.easing.setValue(frame.easing.ordinal());
    }
}