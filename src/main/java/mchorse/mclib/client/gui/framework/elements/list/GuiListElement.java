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
import mchorse.mclib.utils.MathUtils;
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
    public ScrollArea scroll;

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

    private String filter = "";
    private List<Pair<T>> filtered = new ArrayList<Pair<T>>();

    private int dragging = -1;
    private long dragTime;

    public GuiListElement(Minecraft mc, Consumer<List<T>> callback)
    {
        super(mc);

        this.callback = callback;
        this.area = this.scroll = new ScrollArea(20);
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

    public GuiListElement<T> horizontal()
    {
        this.scroll.direction = ScrollArea.ScrollDirection.HORIZONTAL;

        return this;
    }

    public boolean isHorizontal()
    {
        return this.scroll.direction == ScrollArea.ScrollDirection.HORIZONTAL;
    }

    /* Filtering elements */

    public void filter(String filter)
    {
        filter = filter.toLowerCase();

        if (this.filter.equals(filter))
        {
            return;
        }

        this.filter = filter;
        this.filtered.clear();

        if (filter.isEmpty())
        {
            this.update();

            return;
        }

        for (int i = 0; i < this.list.size(); i ++)
        {
            T element = this.list.get(i);

            if (this.elementToString(element).toLowerCase().contains(filter))
            {
                this.filtered.add(new Pair<T>(element, i));
            }
        }

        this.update();
    }

    public boolean isFiltering()
    {
        return !this.filter.isEmpty();
    }

    /* Index and current value(s) methods */

    public boolean isDeselected()
    {
        return this.current.isEmpty();
    }

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

    public T getCurrentFirst()
    {
        if (!this.current.isEmpty())
        {
            return this.list.get(this.current.get(0));
        }

        return null;
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

    public void setCurrentDirect(T element)
    {
        this.current.clear();

        for (int i = 0; i < this.list.size(); i ++)
        {
            if (this.list.get(i) == element)
            {
                this.current.add(i);

                return;
            }
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
        this.filter("");

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
        int index = this.current.size() == 1 ? this.current.get(0) : -1;

        if (this.exists(index))
        {
            this.list.set(index, element);
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
        this.update();
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

            for (T element : current)
            {
                this.current.add(this.list.indexOf(element));
            }
        }
    }

    /**
     * Sort elements
     */
    protected boolean sortElements()
    {
        return false;
    }

    /* Miscellaneous methods */

    public void update()
    {
        this.scroll.setSize(this.isFiltering() ? this.filtered.size() : this.list.size());
        this.scroll.clamp();
    }

    public boolean exists(int index)
    {
        return this.exists(this.list, index);
    }

    public boolean exists(List list, int index)
    {
        return index >= 0 && index < list.size();
    }

    public boolean isDragging()
    {
        return this.exists(this.dragging) && System.currentTimeMillis() - this.dragTime > 100;
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.scroll.mouseClicked(context))
        {
            return true;
        }

        if (this.scroll.isInside(context))
        {
            int index = this.scroll.getIndex(context.mouseX, context.mouseY);
            boolean filtering = this.isFiltering();

            if (filtering)
            {
                index = this.exists(this.filtered, index) ? this.filtered.get(index).index : -1;
            }

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

                if (!filtering && this.sorting && this.current.size() == 1)
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
        return super.mouseScrolled(context) || this.scroll.mouseScroll(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        if (this.sorting && !this.isFiltering())
        {
            if (this.isDragging())
            {
                int index = this.scroll.getIndex(context.mouseX, context.mouseY);

                if (index == -2)
                {
                    index = this.getList().size() - 1;
                }

                if (index != this.dragging && this.exists(index))
                {
                    T value = this.list.remove(this.dragging);

                    this.list.add(index, value);
                    this.setIndex(index);
                }
            }

            this.dragging = -1;
        }

        this.scroll.mouseReleased(context);
        super.mouseReleased(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.scroll.drag(context);

        if (this.background)
        {
            this.area.draw(this.color);
        }

        GuiDraw.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, context);
        this.drawList(context);
        GuiDraw.unscissor(context);

        this.scroll.drawScrollbar();

        GuiDraw.drawLockedArea(this);

        super.draw(context);

        if (this.exists(this.dragging) && this.isDragging())
        {
            this.drawListElement(this.list.get(this.dragging), this.dragging, context.mouseX + 6, context.mouseY - this.scroll.scrollItemSize / 2, true, true);
        }
    }

    public void drawList(GuiContext context)
    {
        int i = 0;

        if (this.isFiltering())
        {
            for (Pair<T> element : this.filtered)
            {
                i = this.drawElement(context, element.value, i, element.index);

                if (i == -1)
                {
                    break;
                }
            }
        }
        else
        {
            for (T element : this.list)
            {
                i = this.drawElement(context, element, i, i);

                if (i == -1)
                {
                    break;
                }
            }
        }
    }

    public int drawElement(GuiContext context, T element, int i, int index)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;
        int s = this.scroll.scrollItemSize;

        int xSide = this.isHorizontal() ? this.scroll.scrollItemSize : this.scroll.w;
        int ySide = this.isHorizontal() ? this.scroll.h : this.scroll.scrollItemSize;

        int x = this.scroll.x;
        int y = this.scroll.y + i * s - this.scroll.scroll;

        int axis = y;
        int low = this.scroll.y;
        int high = this.scroll.ey();

        if (this.isHorizontal())
        {
            x = this.scroll.x + i * s - this.scroll.scroll;
            y = this.scroll.y;

            axis = x;
            low = this.scroll.x;
            high = this.scroll.ex();
        }

        if (axis + s < low || (!this.isFiltering() && this.isDragging() && this.dragging == i))
        {
            return i + 1;
        }

        if (axis >= high)
        {
            return -1;
        }

        boolean hover = mouseX >= x && mouseY >= y && mouseX < x + xSide && mouseY < y + ySide;
        boolean selected = this.current.indexOf(index) != -1;

        this.drawListElement(element, index, x, y, hover, selected);

        return i + 1;
    }

    /**
     * Draw individual element (with selection
     */
    public void drawListElement(T element, int i, int x, int y, boolean hover, boolean selected)
    {
        if (selected)
        {
            if (this.isHorizontal())
            {
                Gui.drawRect(x, y, x + this.scroll.scrollItemSize, y + this.scroll.h, 0x88000000 + McLib.primaryColor.get());
            }
            else
            {
                Gui.drawRect(x, y, x + this.scroll.w, y + this.scroll.scrollItemSize, 0x88000000 + McLib.primaryColor.get());
            }
        }

        this.drawElementPart(element, i, x, y, hover, selected);
    }

    /**
     * Draw only the main part (without selection or any hover elements)
     */
    protected void drawElementPart(T element, int i, int x, int y, boolean hover, boolean selected)
    {
        this.font.drawStringWithShadow(this.elementToString(element), x + 4, y + this.scroll.scrollItemSize / 2 - this.font.FONT_HEIGHT / 2, hover ? 16777120 : 0xffffff);
    }

    /**
     * Convert element to string
     */
    protected String elementToString(T element)
    {
        return element.toString();
    }

    public static class Pair<T>
    {
        public T value;
        public int index;

        public Pair(T value, int index)
        {
            this.value = value;
            this.index = index;
        }
    }
}