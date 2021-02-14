package mchorse.mclib.client.gui.framework.elements.list;

import mchorse.mclib.client.gui.utils.Label;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

public class GuiLabelSearchListElement <T> extends GuiSearchListElement<Label<T>>
{
    public GuiLabelSearchListElement(Minecraft mc, Consumer<List<Label<T>>> callback)
    {
        super(mc, callback);
    }

    @Override
    protected GuiListElement<Label<T>> createList(Minecraft mc, Consumer<List<Label<T>>> callback)
    {
        return new GuiLabelListElement<T>(mc, callback);
    }
}