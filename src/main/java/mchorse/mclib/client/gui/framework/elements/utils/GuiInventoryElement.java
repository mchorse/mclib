package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiInventoryElement extends GuiElement
{
    public static NonNullList<ItemStack> container;

    public GuiTrackpadElement count;
    public GuiIconElement toggle;
    public GuiTextElement search;

    public GuiSlotElement slot;
    protected ScrollArea inventory = new ScrollArea(20);
    protected Area hotbar = new Area();

    private ItemStack active = ItemStack.EMPTY;
    private boolean searching;

    public static void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        drawItemStack(stack, x, y, 200, altText);
    }

    /**
     * Draws an ItemStack.
     *
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    public static void drawItemStack(ItemStack stack, int x, int y, int z, String altText)
    {
        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        itemRender.zLevel = z;

        FontRenderer font = null;
        if (!stack.isEmpty()) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = Minecraft.getMinecraft().fontRenderer;

        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        itemRender.zLevel = 0.0F;
        GlStateManager.popMatrix();
    }

    /**
     * Draw item tooltip
     */
    public static void drawItemTooltip(ItemStack stack, EntityPlayerSP player, FontRenderer providedFont, int x, int y)
    {
        if (stack.isEmpty())
        {
            return;
        }

        List<String> list = stack.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        FontRenderer font = stack.getItem().getFontRenderer(stack);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }   

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, x, y, screen.width, screen.height, -1, font == null ? providedFont : font);
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    public GuiInventoryElement(Minecraft mc, GuiSlotElement slot)
    {
        super(mc);

        this.count = new GuiTrackpadElement(mc, (v) -> this.setCount(v.intValue()));
        this.count.limit(1).integer();
        this.toggle = new GuiIconElement(mc, Icons.SEARCH, this::toggleList);
        this.search = new GuiTextElement(mc, (t) -> this.updateList());
        this.search.setVisible(false);

        this.slot = slot;
        this.flex().wh(10 * 20, 7 * 20);

        this.count.flex().relative(this).x(10).y(10).w(1F, -40);
        this.search.flex().relative(this).x(10).y(10).w(1F, -40);
        this.toggle.flex().relative(this).x(1F, -30).y(10);

        this.add(this.count, this.toggle, this.search);

        this.inventory.scrollSpeed = 20;
    }

    private void setCount(int count)
    {
        ItemStack stack = this.slot.getStack().copy();

        stack.setCount(count);
        this.slot.acceptStack(stack, this.slot.lastSlot);
    }

    private void toggleList(GuiIconElement element)
    {
        this.searching = !this.searching;

        this.updateElements();
        this.updateList();
    }

    private void updateElements()
    {
        this.count.setVisible(!this.searching && !this.slot.getStack().isEmpty());
        this.search.setVisible(this.searching);
        this.inventory.h = this.searching ? 100 : 60;
    }

    private void updateList()
    {
        if (container == null)
        {
            container = NonNullList.create();
        }

        container.clear();
        container.addAll(this.mc.getSearchTree(SearchTreeManager.ITEMS).search(this.search.field.getText().toLowerCase()));

        this.inventory.scroll = 0;
        this.inventory.scrollSize = (int) (Math.ceil(container.size() / 9D) * this.inventory.scrollItemSize);
    }

    private void setStack(ItemStack stack, int slot)
    {
        this.slot.acceptStack(stack, slot);

        this.updateElements();
        this.fillStack(this.slot.getStack());
    }

    public void updateInventory()
    {
        this.inventory.scroll = 0;

        this.searching = false;
        this.fillStack(this.slot.getStack());
        this.updateElements();
    }

    private void fillStack(ItemStack stack)
    {
        this.count.setVisible(!stack.isEmpty());
        this.count.limit(1, stack.getMaxStackSize());
        this.count.setValue(stack.getCount());
    }

    @Override
    public void resize()
    {
        super.resize();

        int tile = 20;
        int row = 9 * tile;
        int fourth = this.area.h / 4;

        this.inventory.set(this.area.mx(row), this.area.ey() - (fourth + tile) / 2 - tile * 4, row, (this.searching ? 5 : 3) * tile);
        this.hotbar.set(this.area.mx(row), this.area.ey() - (fourth + tile) / 2, row, tile);
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (!this.area.isInside(context))
        {
            this.removeFromParent();

            return false;
        }

        if (this.searching && this.inventory.mouseClicked(context))
        {
            return true;
        }

        boolean inventory = this.inventory.isInside(context);
        boolean hotbar = this.hotbar.isInside(context);

        if ((inventory || hotbar) && context.mouseButton == 0)
        {
            Area area = inventory ? this.inventory : this.hotbar;

            int x = (context.mouseX - area.x - 2) / 20;
            int y = (context.mouseY - area.y - 2) / 20;

            if (inventory && !this.searching)
            {
                y += 1;
            }

            if (x >= 9 || y >= (this.inventory.h / 20) + 1 || x < 0 || y < 0 || !this.isVisible())
            {
                return true;
            }

            int index = x + y * 9;

            if (this.slot != null)
            {
                NonNullList<ItemStack> items = this.searching ? container : this.mc.player.inventory.mainInventory;

                if (this.searching)
                {
                    index += this.inventory.scroll / 20 * 9;
                }

                if (index < items.size())
                {
                    this.setStack(items.get(index), this.searching ? -1 : index);
                    this.removeFromParent();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        return super.mouseScrolled(context) || (this.searching && this.inventory.mouseScroll(context));
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        super.mouseReleased(context);

        this.inventory.mouseReleased(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.active = null;

        /* Background rendering */
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);

        int border = 0xffffffff;
        int fourth = this.area.y(0.75F);

        if (McLib.enableBorders.get())
        {
            Gui.drawRect(this.area.x + 1, this.area.y, this.area.ex() - 1, this.area.ey(), 0xff000000);
            Gui.drawRect(this.area.x, this.area.y + 1, this.area.ex(), this.area.ey() - 1, 0xff000000);
            Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, border);
            Gui.drawRect(this.area.x + 2, this.area.y + 2, this.area.ex() - 2, this.area.ey() - 2, 0xffc6c6c6);

            if (!this.searching)
            {
                Gui.drawRect(this.area.x + 1, fourth, this.area.ex() - 1, this.area.ey() - 1, 0xff222222);
            }
        }
        else
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), border);
            Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, 0xffc6c6c6);

            if (!this.searching)
            {
                Gui.drawRect(this.area.x, fourth, this.area.ex(), this.area.ey(), 0xff222222);
            }
        }

        GuiDraw.drawDropCircleShadow(this.toggle.area.mx(), this.toggle.area.my(), 10, 4, 8, 0x18000000, 0);

        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        if(this.searching)
        {
            int scroll = 0;

            if (container.size() > 45)
            {
                int rows = (int) Math.ceil(container.size() / 9F);
                float factor = this.inventory.scroll / (float) this.inventory.scrollSize;
                scroll = (int) (factor * rows);
                scroll *= 9;
            }

            int index = this.drawGrid(context, this.inventory, container, -1, scroll, scroll + this.inventory.h / 20 * 9);

            if (index != -1)
            {
                this.active = container.get(index);
            }
        }
        else
        {
            NonNullList<ItemStack> inventory = this.mc.player.inventory.mainInventory;

            int index = this.drawGrid(context, this.inventory, inventory, -1, 9, inventory.size());
            index = this.drawGrid(context, this.hotbar, inventory, index, 0, 9);

            if (index != -1)
            {
                this.active = inventory.get(index);
            }
        }

        if (this.active != null)
        {
            context.tooltip.set(context, this);
        }

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        GuiDraw.drawLockedArea(this, McLib.enableBorders.get() ? 1 : 0);

        if (this.searching)
        {
            this.inventory.drag(context);

            GuiDraw.scissor(this.inventory.x, this.inventory.y, this.inventory.w, this.inventory.h, context);
            this.inventory.drawScrollbar();
            GuiDraw.unscissor(context);
        }

        super.draw(context);
    }

    private int drawGrid(GuiContext context, Area area, NonNullList<ItemStack> inventory, int index, int i, int c)
    {
        for (int j = 0; j < c - i; j ++)
        {
            int k = i + j;

            if (k >= inventory.size())
            {
                return index;
            }

            ItemStack stack = inventory.get(k);

            int x = j % 9;
            int y = j / 9;

            x = area.x + 2 + 20 * x;
            y = area.y + 2 + 20 * y;

            int diffX = context.mouseX - x;
            int diffY = context.mouseY - y;

            boolean hover = diffX >= 0 && diffX < 18 && diffY >= 0 && diffY < 18;

            Gui.drawRect(x - 1, y - 1, x + 17, y + 17, area == this.hotbar ? 0xaa000000 : 0x44000000);

            drawItemStack(stack, x, y, null);

            if (hover)
            {
                Gui.drawRect(x - 2, y - 2, x + 18, y + 18, 0xcc000000 + McLib.primaryColor.get());
                index = k;
            }
        }

        return index;
    }

    @Override
    public void drawTooltip(GuiContext context, Area area)
    {
        super.drawTooltip(context, area);

        GuiInventoryElement.drawItemTooltip(this.active, this.mc.player, this.font, context.mouseX, context.mouseY);
    }
}