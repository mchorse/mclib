package mchorse.mclib.client.gui.framework.elements.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.ScrollArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

/**
 * Abstract GUI list element
 * 
 * This element allows managing scrolling vertical lists much easier
 */
public abstract class GuiListElement<T> extends GuiElement
{
    /**
     * List of elements 
     */
    protected List<T> list = new ArrayList<T>();

    /**
     * List for copying
     */
    private List<T> copy = new ArrayList<T>();

    /**
     * Scrolling area
     */
    public ScrollArea scroll = new ScrollArea(20);

    /**
     * Callback which gets invoked when user selects an element
     */
    public Consumer<List<T>> callback;

    /**
     * Selected elements
     */
    public List<Integer> current = new ArrayList<Integer>();

    /**
     * Whether this list supports multi selection
     */
    public boolean multi;

    /**
     * Whether this list supports reordering
     */
    public boolean sorting;

    public boolean background = false;
    public int color = 0x88000000;

    private int dragging = -1;
    private long dragTime;

    public GuiListElement(Minecraft mc, Consumer<List<T>> callback)
    {
        super(mc);

        this.callback = callback;
    }

    /* List element settings */

    public GuiListElement<T> background()
    {
        this.background = true;

        return this;
    }

    public GuiListElement<T> background(int color)
    {
        return this.background(true, color);
    }

    public GuiListElement<T> background(boolean background, int color)
    {
        this.background = background;
        this.color = color;

        return this;
    }

    public GuiListElement<T> multi()
    {
        this.multi = true;

        return this;
    }

    public GuiListElement<T> sorting()
    {
        this.sorting = true;

        return this;
    }

    /* Index and current value(s) methods */

    public List<T> getCurrent()
    {
        this.copy.clear();

        for (Integer integer : this.current)
        {
            if (this.exists(integer))
            {
                this.copy.add(this.list.get(integer));
            }
        }

        return this.copy;
    }

    public int getIndex()
    {
        return this.current.isEmpty() ? -1 : this.current.get(0);
    }

    public void setIndex(int index)
    {
        this.current.clear();
        this.addIndex(index);
    }

    public void addIndex(int index)
    {
        if (this.exists(index) && this.current.indexOf(index) == -1)
        {
            this.current.add(index);
        }
    }

    public void toggleIndex(int index)
    {
        if (this.exists(index))
        {
            int i = this.current.indexOf(index);

            if (i == -1)
            {
                this.current.add(index);
            }
            else
            {
                this.current.remove(i);
            }
        }
    }

    public void setCurrent(T element)
    {
        this.current.clear();

        int index = this.list.indexOf(element);

        if (this.exists(index))
        {
            this.current.add(index);
        }
    }

    public void setCurrent(List<T> elements)
    {
        if (!this.multi && !elements.isEmpty())
        {
            this.setCurrent(elements.get(0));

            return;
        }

        this.current.clear();

        for (T element : elements)
        {
            int index = this.list.indexOf(element);

            if (this.exists(index))
            {
                this.current.add(index);
            }
        }
    }

    public void setCurrentScroll(T element)
    {
        this.setCurrent(element);

        if (!this.current.isEmpty())
        {
            this.scroll.scrollTo(this.current.get(0) * this.scroll.scrollItemSize);
        }
    }

    public List<T> getList()
    {
        return this.list;
    }

    /* Content management */

    public void clear()
    {
        this.current.clear();
        this.list.clear();
        this.update();
    }

    public void add(T element)
    {
        this.list.add(element);
        this.update();
    }

    public void add(Collection<T> elements)
    {
        this.list.addAll(elements);
        this.update();
    }

    public void replace(T element)
    {
        int size = this.list.size();

        if (this.current.size() == 1 && this.exists(this.current.get(0)))
        {
            this.list.set(this.current.get(0), element);
        }
    }

    public void setList(List<T> list)
    {
        if (list == null)
        {
            return;
        }

        this.list = list;
        this.update();
    }

    public void remove(T element)
    {
        this.list.remove(element);
    }

    /**
     * Sort elements in this array, the subsclasses should implement
     * the other sorting method in order for it to work
     */
    public final void sort()
    {
        List<T> current = this.getCurrent();

        if (this.sortElements())
        {
            this.current.clear();

            for (T string : current)
            {
                this.current.add(this.list.indexOf(string));
            }
        }
    }

    /* Miscellaneous methods */

    /**
     * Sort elements
     */
    protected boolean sortElements()
    {
        return false;
    }

    public void update()
    {
        this.scroll.setSize(this.list.size());
        this.scroll.clamp();
    }

    public boolean exists(int index)
    {
        return index >= 0 && index < this.list.size();
    }

    public boolean isDragging()
    {
        return this.exists(this.dragging) && System.currentTimeMillis() - this.dragTime > 100;
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.scroll.mouseClicked(mouseX, mouseY))
        {
            return true;
        }

        if (this.scroll.isInside(mouseX, mouseY))
        {
            int index = this.scroll.getIndex(mouseX, mouseY);

            if (this.exists(index))
            {
                if (this.multi && GuiScreen.isShiftKeyDown())
                {
                    this.toggleIndex(index);
                }
                else
                {
                    this.setIndex(index);
                }

                if (this.sorting && this.current.size() == 1)
                {
                    this.dragging = index;
                    this.dragTime = System.currentTimeMillis();
                }

                if (this.callback != null)
                {
                    this.callback.accept(this.getCurrent());

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return this.scroll.mouseScroll(context.mouseX, context.mouseY, context.mouseWheel);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        if (this.sorting)
        {
            if (this.isDragging())
            {
                int index = this.scroll.getIndex(context.mouseX, context.mouseY);

                if (index != this.dragging && this.exists(index))
                {
                    T value = this.list.remove(this.dragging);

                    this.list.add(index, value);
                    this.setIndex(index);
                }
            }

            this.dragging = -1;
        }

        this.scroll.mouseReleased(context.mouseX, context.mouseY);
    }

    @Override
    public void draw(GuiContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        this.scroll.drag(mouseX, mouseY);

        if (this.background)
        {
            this.area.draw(this.color);
        }

        int i = 0;
        int h = this.scroll.scrollItemSize;
        boolean dragging = isDragging();

        GuiDraw.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, context);

        for (T element : this.list)
        {
            int x = this.scroll.x;
            int y = this.scroll.y + i * h - this.scroll.scroll;

            if (y + h < this.scroll.y || (this.dragging == i && dragging))
            {
                i++;
                continue;
            }

            if (y >= this.scroll.ey())
            {
                break;
            }

            boolean hover = mouseX >= x && mouseY >= y && mouseX < x + this.scroll.w && mouseY < y + this.scroll.scrollItemSize;
            boolean selected = this.current.indexOf(i) != -1;

            this.drawListElement(element, i, x, y, hover, selected);

            i++;
        }

        GuiDraw.unscissor(context);

        this.scroll.drawScrollbar();

        GuiDraw.drawLockedArea(this);

        super.draw(context);

        if (this.exists(this.dragging) && dragging)
        {
            this.drawListElement(this.list.get(this.dragging), this.dragging, context.mouseX + 6, context.mouseY - this.scroll.scrollItemSize / 2, true, true);
        }
    }

    /**
     * Draw individual element (with selection
     */
    public void drawListElement(T element, int i, int x, int y, boolean hover, boolean selected)
    {
        if (selected)
        {
            Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x88000000 + McLib.primaryColor.get());
        }

        this.drawElementPart(element, i, x, y, hover, selected);
    }

    /**
     * Draw only the main part (without selection or any hover elements)
     */
    protected void drawElementPart(T element, int i, int x, int y, boolean hover, boolean selected)
    {
        this.font.drawStringWithShadow(this.elementToString(element, i, x, y, hover, selected), x + 4, y + this.scroll.scrollItemSize / 2 - this.font.FONT_HEIGHT / 2, hover ? 16777120 : 0xffffff);
    }

    /**
     * Convert element to string
     */
    protected String elementToString(T element, int i, int x, int y, boolean hover, boolean selected)
    {
        return element.toString();
    }
}