package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketDropItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class GuiSlotElement extends GuiClickElement<ItemStack>
{
    public static final ResourceLocation SHIELD = new ResourceLocation("minecraft:textures/items/empty_armor_slot_shield.png");
    public static final ResourceLocation BOOTS = new ResourceLocation("minecraft:textures/items/empty_armor_slot_boots.png");
    public static final ResourceLocation LEGGINGS = new ResourceLocation("minecraft:textures/items/empty_armor_slot_leggings.png");
    public static final ResourceLocation CHESTPLATE = new ResourceLocation("minecraft:textures/items/empty_armor_slot_chestplate.png");
    public static final ResourceLocation HELMET = new ResourceLocation("minecraft:textures/items/empty_armor_slot_helmet.png");

    public GuiInventoryElement inventory;
    public final int slot;
    private ItemStack stack = ItemStack.EMPTY;

    public boolean drawDisabled = true;

    public GuiSlotElement(Minecraft mc, int slot, Consumer<ItemStack> callback)
    {
        super(mc, callback);

        this.slot = slot;
        this.inventory = new GuiInventoryElement(mc, this);

        this.flex().wh(24, 24);
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack.copy();

        if (this.inventory.hasParent())
        {
            this.inventory.updateInventory();
        }
    }

    public void acceptStack(ItemStack stack)
    {
        this.stack = stack.copy();

        if (this.callback != null)
        {
            this.callback.accept(stack);
        }
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        if (this.contextMenu == null)
        {
            return this.createDefaultSlotContextMenu();
        }

        return super.createContextMenu(context);
    }

    public GuiSimpleContextMenu createDefaultSlotContextMenu()
    {
        GuiSimpleContextMenu menu = new GuiSimpleContextMenu(this.mc).action(Icons.COPY, IKey.lang("mclib.gui.item_slot.context.copy"), this::copyNBT);

        try
        {
            ItemStack stack = new ItemStack(JsonToNBT.getTagFromJson(GuiScreen.getClipboardString()));

            if (!stack.isEmpty())
            {
                menu.action(Icons.PASTE, IKey.lang("mclib.gui.item_slot.context.paste"), () -> this.pasteItem(stack));
            }
        }
        catch (Exception e)
        {}

        return menu
            .action(Icons.DOWNLOAD, IKey.lang("mclib.gui.item_slot.context.drop"), this::dropItem)
            .action(Icons.CLOSE, IKey.lang("mclib.gui.item_slot.context.clear"), this::clearItem);
    }

    private void copyNBT()
    {
        if (!this.stack.isEmpty())
        {
            GuiScreen.setClipboardString(this.stack.serializeNBT().toString());
        }
    }

    private void pasteItem(ItemStack stack)
    {
        this.acceptStack(stack);
    }

    private void dropItem()
    {
        if (!this.stack.isEmpty())
        {
            Dispatcher.sendToServer(new PacketDropItem(this.stack));
        }
    }

    private void clearItem()
    {
        this.acceptStack(ItemStack.EMPTY);
    }

    @Override
    protected void click(int mouseButton)
    {
        this.inventory.removeFromParent();

        GuiContext context = GuiBase.getCurrent();

        this.inventory.flex().relative(context.screen.root).xy(0.5F, 0.5F).anchor(0.5F, 0.5F);
        this.inventory.resize();
        this.inventory.updateInventory();

        context.screen.root.add(this.inventory);
    }

    @Override
    protected ItemStack get()
    {
        return this.stack;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        int border = this.inventory.hasParent() ? 0xff000000 + McLib.primaryColor.get() : 0xffffffff;

        if (McLib.enableBorders.get())
        {
            Gui.drawRect(this.area.x + 1, this.area.y, this.area.ex() - 1, this.area.ey(), 0xff000000);
            Gui.drawRect(this.area.x, this.area.y + 1, this.area.ex(), this.area.ey() - 1, 0xff000000);
            Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, border);
            Gui.drawRect(this.area.x + 2, this.area.y + 2, this.area.ex() - 2, this.area.ey() - 2, 0xffc6c6c6);
        }
        else
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), border);
            Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, 0xffc6c6c6);
        }

        int x = this.area.mx() - 8;
        int y = this.area.my() - 8;

        if (this.stack.isEmpty() && this.slot != 0)
        {
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1, 1);

            if (this.slot == 1)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(SHIELD);
            }
            else if (this.slot == 2)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(BOOTS);
            }
            else if (this.slot == 3)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(LEGGINGS);
            }
            else if (this.slot == 4)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(CHESTPLATE);
            }
            else if (this.slot == 5)
            {
                Minecraft.getMinecraft().renderEngine.bindTexture(HELMET);
            }

            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
        }
        else
        {
            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.enableDepth();

            GuiInventoryElement.drawItemStack(this.stack, x, y, null);

            if (this.area.isInside(context))
            {
                context.tooltip.set(context, this);
            }

            GlStateManager.disableDepth();
            RenderHelper.disableStandardItemLighting();
        }

        if (this.drawDisabled)
        {
            GuiDraw.drawLockedArea(this, McLib.enableBorders.get() ? 1 : 0);
        }
    }

    @Override
    public void drawTooltip(GuiContext context, Area area)
    {
        super.drawTooltip(context, area);

        GuiInventoryElement.drawItemTooltip(this.stack, this.mc.player, this.font, context.mouseX, context.mouseY);

        GlStateManager.disableLighting();
    }
}