package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.tooltips.InterpolationTooltip;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.InterpolationRenderer;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.IInterpolation;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.MathUtils;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private IAxisConverter converter;

    public GuiKeyframesEditor(Minecraft mc)
    {
        super(mc);

        InterpolationTooltip tooltip = new InterpolationTooltip(0F, 0F, () ->
        {
            Keyframe keyframe = this.graph.getCurrent();

            if (keyframe == null)
            {
                return null;
            }

            return keyframe.interp.from(keyframe.easing);
        }, null);

        this.frameButtons = new GuiElement(mc);
        this.frameButtons.flex().relative(this).x(1F, -10).y(10).w(170).h(50).anchorX(1F);
        this.frameButtons.setVisible(false);
        this.tick = new GuiTrackpadElement(mc, this::setTick);
        this.tick.limit(Integer.MIN_VALUE, Integer.MAX_VALUE, true).tooltip(IKey.lang("mclib.gui.keyframes.tick"));
        this.value = new GuiTrackpadElement(mc, this::setValue);
        this.value.tooltip(IKey.lang("mclib.gui.keyframes.value"));
        this.interp = new GuiButtonElement(mc, IKey.lang(""), (b) -> this.interpolations.toggleVisible());
        this.interp.tooltip(tooltip);
        this.interpolations = new GuiKeyframeInterpolationsList(mc, (interp) -> this.pickInterpolation(interp.get(0)));
        this.interpolations.tooltip(tooltip).setVisible(false);

        this.easing = new GuiCirculateElement(mc, (b) -> this.changeEasing());
        this.easing.tooltip(tooltip);

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
        this.interpolations.flex().relative(this).set(0, 30, 80, 20).x(1, -180).h(1, -30).maxH(16 * 7);
        this.graph.flex().relative(this).set(0, 0, 0, 0).w(1, 0).h(1, 0);

        /* Add all elements */
        this.add(this.graph, this.frameButtons);
        this.frameButtons.add(this.tick, this.value, this.interp, this.easing, this.interpolations);

        this.keys().register(IKey.lang("mclib.gui.keyframes.context.maximize"), Keyboard.KEY_HOME, this::resetView).inside();
        this.keys().register(IKey.lang("mclib.gui.keyframes.context.select_all"), Keyboard.KEY_A, this::selectAll).held(Keyboard.KEY_LCONTROL).inside();
    }

    protected abstract T createElement(Minecraft mc);

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

        this.pickInterpolation(KeyframeInterpolation.values()[index]);
        this.interpolations.setCurrent(interp);
        GuiUtils.playClick();
    }

    protected void toggleEasing()
    {
        this.easing.clickItself(GuiBase.getCurrent(), GuiScreen.isShiftKeyDown() ? 1 : 0);
    }

    public void setConverter(IAxisConverter converter)
    {
        this.converter = converter;
        this.graph.setConverter(converter);

        if (converter != null)
        {
            converter.updateField(this.tick);
        }

        this.fillData(this.graph.getCurrent());
    }

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

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(this.mc);

        menu.action(Icons.MAXIMIZE, IKey.lang("mclib.gui.keyframes.context.maximize"), this::resetView);
        menu.action(Icons.FULLSCREEN, IKey.lang("mclib.gui.keyframes.context.select_all"), this::selectAll);

        if (this.graph.which != Selection.NOT_SELECTED)
        {
            menu.action(Icons.REMOVE, IKey.lang("mclib.gui.keyframes.context.remove"), this::removeSelectedKeyframes);
            menu.action(Icons.COPY, IKey.lang("mclib.gui.keyframes.context.copy"), this::copyKeyframes);
        }

        Map<String, List<Keyframe>> pasted = this.parseKeyframes();

        if (pasted != null)
        {
            final Map<String, List<Keyframe>> keyframes = pasted;
            double offset = this.graph.scaleX.from(context.mouseX);
            int mouseY = context.mouseY;

            menu.action(Icons.PASTE, IKey.lang("mclib.gui.keyframes.context.paste"), () -> this.pasteKeyframes(keyframes, (long) offset, mouseY));
        }

        if (this.graph.which != Selection.NOT_SELECTED && this.graph.isMultipleSelected())
        {
            menu.action(Icons.LEFT_HANDLE, IKey.lang("mclib.gui.keyframes.context.to_left"), () -> this.graph.which = Selection.LEFT_HANDLE);
            menu.action(Icons.MAIN_HANDLE, IKey.lang("mclib.gui.keyframes.context.to_main"), () -> this.graph.which = Selection.KEYFRAME);
            menu.action(Icons.RIGHT_HANDLE, IKey.lang("mclib.gui.keyframes.context.to_right"), () -> this.graph.which = Selection.RIGHT_HANDLE);
        }

        return menu;
    }

    /**
     * Parse keyframes from clipboard
     */
    private Map<String, List<Keyframe>> parseKeyframes()
    {
        try
        {
            NBTTagCompound tag = JsonToNBT.getTagFromJson(GuiScreen.getClipboardString());
            Map<String, List<Keyframe>> temp = new HashMap<String, List<Keyframe>>();

            for (String key : tag.getKeySet())
            {
                NBTTagList list = tag.getTagList(key, Constants.NBT.TAG_COMPOUND);

                for (int i = 0, c = list.tagCount(); i < c; i++)
                {
                    List<Keyframe> keyframes = temp.get(key);

                    if (keyframes == null)
                    {
                        keyframes = new ArrayList<Keyframe>();

                        temp.put(key, keyframes);
                    }

                    Keyframe keyframe = new Keyframe();

                    keyframe.fromNBT(list.getCompoundTagAt(i));
                    keyframes.add(keyframe);
                }
            }

            if (!temp.isEmpty())
            {
                return temp;
            }
        }
        catch (Exception e)
        {}

        return null;
    }

    /**
     * Copy keyframes to clipboard
     */
    private void copyKeyframes()
    {
        NBTTagCompound keyframes = new NBTTagCompound();

        for (GuiSheet sheet : this.graph.getSheets())
        {
            int c = sheet.getSelectedCount();

            if (c > 0)
            {
                NBTTagList list = new NBTTagList();

                for (int i = 0; i < c; i++)
                {
                    Keyframe keyframe = sheet.channel.get(sheet.selected.get(i));

                    list.appendTag(keyframe.toNBT());
                }

                if (list.tagCount() > 0)
                {
                    keyframes.setTag(sheet.id, list);
                }
            }
        }

        GuiScreen.setClipboardString(keyframes.toString());
    }

    /**
     * Paste copied keyframes to clipboard
     */
    protected void pasteKeyframes(Map<String, List<Keyframe>> keyframes, long offset, int mouseY)
    {
        List<GuiSheet> sheets = this.graph.getSheets();

        this.graph.clearSelection();

        if (keyframes.size() == 1)
        {
            GuiSheet current = this.graph.getSheet(mouseY);

            if (current == null)
            {
                current =  sheets.get(0);
            }

            this.pasteKeyframesTo(current, keyframes.get(keyframes.keySet().iterator().next()), offset);

            return;
        }

        for (Map.Entry<String, List<Keyframe>> entry : keyframes.entrySet())
        {
            for (GuiSheet sheet : sheets)
            {
                if (!sheet.id.equals(entry.getKey()))
                {
                    continue;
                }

                this.pasteKeyframesTo(sheet, entry.getValue(), offset);
            }
        }
    }

    private void pasteKeyframesTo(GuiSheet sheet, List<Keyframe> keyframes, long offset)
    {
        long firstX = keyframes.get(0).tick;
        List<Keyframe> toSelect = new ArrayList<Keyframe>();

        if (GuiScreen.isCtrlKeyDown())
        {
            offset = firstX;
        }

        for (Keyframe keyframe : keyframes)
        {
            keyframe.tick = keyframe.tick - firstX + offset;

            int index = sheet.channel.insert(keyframe.tick, keyframe.value);
            Keyframe inserted = sheet.channel.get(index);

            inserted.copy(keyframe);
            toSelect.add(inserted);
        }

        for (Keyframe select : toSelect)
        {
            sheet.selected.add(sheet.channel.getKeyframes().indexOf(select));
        }

        this.graph.which = Selection.KEYFRAME;
        this.graph.setKeyframe(this.graph.getCurrent());
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

    public void resetView()
    {
        this.graph.resetView();
    }

    public void selectAll()
    {
        this.graph.selectAll();
    }

    public void removeSelectedKeyframes()
    {
        this.graph.removeSelectedKeyframes();
    }

    public void setTick(double tick)
    {
        this.graph.setTick(this.converter == null ? tick : this.converter.from(tick), false);
    }

    public void setValue(double value)
    {
        this.graph.setValue(value, false);
    }

    public void pickInterpolation(KeyframeInterpolation interp)
    {
        this.graph.setInterpolation(interp);
        this.interp.label.set(interp.getKey());
        this.interpolations.setVisible(false);
    }

    public void changeEasing()
    {
        this.graph.setEasing(KeyframeEasing.values()[this.easing.getValue()]);
    }

    public void fillData(Keyframe frame)
    {
        boolean show = frame != null && this.graph.which != Selection.NOT_SELECTED;

        this.frameButtons.setVisible(show);

        if (!show)
        {
            return;
        }

        double tick = this.graph.which.getX(frame);
        boolean forceInteger = this.graph.which == Selection.KEYFRAME;

        this.tick.integer = this.converter == null ? forceInteger : this.converter.forceInteger(frame, this.graph.which, forceInteger);
        this.tick.setValue(this.converter == null ? tick : this.converter.to(tick));
        this.value.setValue(this.graph.which.getY(frame));
        this.interp.label.set(frame.interp.getKey());
        this.interpolations.setCurrent(frame.interp);
        this.easing.setValue(frame.easing.ordinal());
    }
}