package mchorse.mclib.client.gui.framework.elements.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class GuiSearchListElement<T> extends GuiElement
{
    public List<T> elements = new ArrayList<T>();
    public GuiTextElement search;
    public GuiListElement<T> list;
    public String label;
    public boolean background;

    public GuiSearchListElement(Minecraft mc, Consumer<List<T>> callback)
    {
        super(mc);

        this.search = new GuiTextElement(mc, 100, (str) -> this.filter(str, false));
        this.search.resizer().parent(this.area).set(0, 0, 0, 20).w(1, 0);

        this.list = this.createList(mc, callback);
        this.list.resizer().parent(this.area).set(0, 20, 0, 0).w(1, 0).h(1, -20);

        this.add(this.search, this.list);
    }

    protected abstract GuiListElement<T> createList(Minecraft mc, Consumer<List<T>> callback);

    public void filter(String str, boolean fill)
    {
        if (fill) this.search.setText(str);

        this.list.clear();

        if (str == null || str.isEmpty())
        {
            this.list.add(this.elements);
        }
        else
        {
            for (T element : this.elements)
            {
                if (element.toString().toLowerCase().startsWith(str.toLowerCase()))
                {
                    this.list.add(element);
                }
            }
        }

        this.list.sort();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        return super.mouseClicked(context) || this.isVisible() && this.area.isInside(context.mouseX, context.mouseY);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.background)
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000);
        }

        super.draw(context);

        if (!this.search.field.isFocused() && this.search.field.getText().isEmpty())
        {
            this.font.drawStringWithShadow(this.label, this.search.area.x + 4, this.search.area.y + 6, 0x888888);
        }
    }
}