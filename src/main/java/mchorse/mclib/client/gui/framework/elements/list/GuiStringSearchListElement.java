package mchorse.mclib.client.gui.framework.elements.list;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;

public class GuiStringSearchListElement extends GuiSearchListElement<String>
{
    public GuiStringSearchListElement(Minecraft mc, Consumer<List<String>> callback)
    {
        super(mc, callback);
    }

    @Override
    protected GuiListElement<String> createList(Minecraft mc, Consumer<List<String>> callback)
    {
        return new GuiStringListElement(mc, callback);
    }
}