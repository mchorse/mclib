package mchorse.mclib.client.gui.framework.elements.buttons;

import mchorse.mclib.McLib;
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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class GuiSlotElement extends GuiClickElement<GuiSlotElement>
{
    public static final ResourceLocation SHIELD = new ResourceLocation("minecraft:textures/items/empty_armor_slot_shield.png");
    public static final ResourceLocation BOOTS = new ResourceLocation("minecraft:textures/items/empty_armor_slot_boots.png");
    public static final ResourceLocation LEGGINGS = new ResourceLocation("minecraft:textures/items/empty_armor_slot_leggings.png");
    public static final ResourceLocation CHESTPLATE = new ResourceLocation("minecraft:textures/items/empty_armor_slot_chestplate.png");
    public static final ResourceLocation HELMET = new ResourceLocation("minecraft:textures/items/empty_armor_slot_helmet.png");

    public int slot;
    public ItemStack stack = ItemStack.EMPTY;
    public GuiInventoryElement inventory;

    public GuiSlotElement(Minecraft mc, int slot, GuiInventoryElement inventory)
    {
        this(mc, slot, inventory, inventory::link);
    }

    public GuiSlotElement(Minecraft mc, int slot, GuiInventoryElement inventory, Consumer<GuiSlotElement> callback)
    {
        this(mc, slot, callback);

        this.inventory = inventory;
    }

    public GuiSlotElement(Minecraft mc, int slot, Consumer<GuiSlotElement> callback)
    {
        super(mc, callback);

        this.slot = slot;
        this.flex().wh(24, 24);
    }

    public GuiSlotElement inventory(GuiInventoryElement inventory)
    {
        this.inventory = inventory;

        return this;
    }

    @Override
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        if (this.contextMenu == null)
        {
            return new GuiSimpleContextMenu(this.mc)
                .action(Icons.DOWNLOAD, IKey.lang("mclib.gui.item_slot.context.drop"), () -> {
                    if (!stack.isEmpty())
                    {
                        Dispatcher.sendToServer(new PacketDropItem(this.stack));
                    }
                })
                .action(Icons.CLOSE, IKey.lang("mclib.gui.item_slot.context.clear"), () -> {
                    this.stack = ItemStack.EMPTY;

                    if (this.inventory != null && this.inventory.callback != null)
                    {
                        this.inventory.callback.accept(this.stack);
                    }
                });
        }

        return super.createContextMenu(context);
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        int border = this.inventory != null && this.inventory.isVisible() && this.inventory.linked == this ? 0xff000000 + McLib.primaryColor.get() : 0xffffffff;

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

        GuiDraw.drawLockedArea(this, McLib.enableBorders.get() ? 1 : 0);
    }

    @Override
    public void drawTooltip(GuiContext context, Area area)
    {
        super.drawTooltip(context, area);

        GuiInventoryElement.drawItemTooltip(this.stack, this.mc.player, this.font, context.mouseX, context.mouseY);
    }
}