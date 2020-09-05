package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.function.Consumer;

public class GuiInventoryElement extends GuiElement
{
	public Consumer<ItemStack> callback;
	public GuiSlotElement linked;

	protected Area inventory = new Area();
	protected Area hotbar = new Area();

	private ItemStack active = null;

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
		if (stack == null) font = stack.getItem().getFontRenderer(stack);
		if (font == null) font = Minecraft.getMinecraft().fontRendererObj;

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
		if (stack == null)
		{
			return;
		}

		List<String> list = stack.getTooltip(player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		FontRenderer font = stack.getItem().getFontRenderer(stack);

		for (int i = 0; i < list.size(); ++i)
		{
			if (i == 0)
			{
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			}
			else
			{
				list.set(i, EnumChatFormatting.GRAY + list.get(i));
			}
		}   

		GuiScreen screen = Minecraft.getMinecraft().currentScreen;

		net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, x, y, screen.width, screen.height, -1, font == null ? providedFont : font);
	}

	public GuiInventoryElement(Minecraft mc, Consumer<ItemStack> callback)
	{
		super(mc);

		this.callback = callback;
		this.flex().wh(10 * 20, 5 * 20);
	}

	public void link(GuiSlotElement slot)
	{
		this.linked = slot;
		this.setVisible(true);
	}

	public void unlink()
	{
		this.linked = null;
		this.setVisible(false);
	}

	@Override
	public void resize()
	{
		super.resize();

		int tile = 20;
		int row = 9 * tile;
		int fourth = this.area.h / 4;

		this.inventory.set(this.area.mx(row), this.area.y + (this.area.h - fourth) / 2 - 30, row, 3 * tile);
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
			this.setVisible(false);

			return false;
		}

		boolean inventory = this.inventory.isInside(context);
		boolean hotbar = this.hotbar.isInside(context);

		if ((inventory || hotbar) && context.mouseButton == 0)
		{
			Area area = inventory ? this.inventory : this.hotbar;

			int x = (context.mouseX - area.x - 2) / 20;
			int y = (context.mouseY - area.y - 2) / 20;

			if (inventory)
			{
				y ++;
			}

			if (x >= 9 || y >= 4 || x < 0 || y < 0 || !this.visible)
			{
				return true;
			}

			int index = x + y * 9;

			if (this.callback != null)
			{
				this.callback.accept(Minecraft.getMinecraft().thePlayer.inventory.mainInventory[index]);

				return true;
			}
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		this.active = null;

		/* Background rendering */
		int border = 0xffffffff;
		int fourth = this.area.y(0.75F);

		if (McLib.enableBorders.get())
		{
			Gui.drawRect(this.area.x + 1, this.area.y, this.area.ex() - 1, this.area.ey(), 0xff000000);
			Gui.drawRect(this.area.x, this.area.y + 1, this.area.ex(), this.area.ey() - 1, 0xff000000);
			Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, border);
			Gui.drawRect(this.area.x + 2, this.area.y + 2, this.area.ex() - 2, fourth, 0xffc6c6c6);
			Gui.drawRect(this.area.x + 1, fourth, this.area.ex() - 1, this.area.ey() - 1, 0xff222222);
		}
		else
		{
			Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), border);
			Gui.drawRect(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, fourth, 0xffc6c6c6);
			Gui.drawRect(this.area.x, fourth, this.area.ex(), this.area.ey(), 0xff222222);
		}

		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		ItemStack[] inventory = player.inventory.mainInventory;

		int index = this.drawGrid(context, this.inventory, inventory, -1, 9, inventory.length);
		index = this.drawGrid(context, this.hotbar, inventory, index, 0, 9);

		if (index != -1)
		{
			context.tooltip.set(context, this);
			this.active = inventory[index];
		}

		GlStateManager.disableDepth();
		RenderHelper.disableStandardItemLighting();

		GuiDraw.drawLockedArea(this, McLib.enableBorders.get() ? 1 : 0);

		super.draw(context);
	}

	private int drawGrid(GuiContext context, Area area, ItemStack[] inventory, int index, int i, int c)
	{
		for (int j = 0; j < c - i; j ++)
		{
			int k = i + j;
			ItemStack stack = inventory[k];

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

		GuiInventoryElement.drawItemTooltip(this.active, this.mc.thePlayer, this.font, context.mouseX, context.mouseY);
	}
}