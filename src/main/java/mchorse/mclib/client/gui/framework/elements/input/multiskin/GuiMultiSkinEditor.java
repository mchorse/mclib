package mchorse.mclib.client.gui.framework.elements.input.multiskin;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvas;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.resources.FilteredResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiMultiSkinEditor extends GuiCanvas
{
	public GuiTexturePicker picker;
	public FilteredResourceLocation location;

	public GuiElement editor;
	public GuiTrackpadElement scale;
	public GuiToggleElement scaleToLargest;
	public GuiTrackpadElement shiftX;
	public GuiTrackpadElement shiftY;

	public GuiMultiSkinEditor(Minecraft mc, GuiTexturePicker picker)
	{
		super(mc);

		this.picker = picker;

		this.editor = new GuiElement(mc);
		this.editor.flex().relative(this).xy(1F, 1F).w(130).anchor(1F, 1F).column(5).stretch().vertical().padding(10);

		this.scale = new GuiTrackpadElement(mc, (value) -> this.location.scale = value.floatValue());
		this.scaleToLargest = new GuiToggleElement(mc, IKey.str("Scale to largest"), (toggle) -> this.location.scaleToLargest = toggle.isToggled());
		this.shiftX = new GuiTrackpadElement(mc, (value) -> this.location.shiftX = value.intValue());
		this.shiftX.integer();
		this.shiftY = new GuiTrackpadElement(mc, (value) -> this.location.shiftY = value.intValue());
		this.shiftY.integer();

		this.editor.add(Elements.label(IKey.str("Scale")).background(0x88000000), this.scale, this.scaleToLargest);
		this.editor.add(Elements.label(IKey.str("Shift")).background(0x88000000), this.shiftX, this.shiftY);
		this.add(this.editor);
	}

	public void setLocation(FilteredResourceLocation location)
	{
		this.location = location;

		this.scale.setValue(location.scale);
		this.scaleToLargest.toggled(location.scaleToLargest);
		this.shiftX.setValue(location.shiftX);
		this.shiftY.setValue(location.shiftY);
	}

	@Override
	protected void startDragging(GuiContext context)
	{
		super.startDragging(context);

		if (this.mouse == 0)
		{
			this.lastT = this.location.shiftX;
			this.lastV = this.location.shiftY;
		}
	}

	@Override
	protected void dragging(GuiContext context)
	{
		super.dragging(context);

		if (this.dragging && this.mouse == 0)
		{
			double dx = (context.mouseX - this.lastX) / this.scaleX.zoom;
			double dy = (context.mouseY - this.lastY) / this.scaleY.zoom;

			if (GuiScreen.isShiftKeyDown()) dx = 0;
			if (GuiScreen.isCtrlKeyDown()) dy = 0;

			this.location.shiftX = (int) (dx) + (int) this.lastT;
			this.location.shiftY = (int) (dy) + (int) this.lastV;

			this.shiftX.setValue(this.location.shiftX);
			this.shiftY.setValue(this.location.shiftY);
		}
	}

	@Override
	protected void drawCanvas(GuiContext context)
	{
		this.area.draw(0xff2f2f2f);

		int w = 0;
		int h = 0;

		for (FilteredResourceLocation child : this.picker.multiRL.children)
		{
			this.mc.renderEngine.bindTexture(child.path);
			w = Math.max(w, GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH));
			h = Math.max(h, GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT));
		}

		Area area = this.calculate(-w / 2, -h / 2, w / 2, h / 2);
		int x = area.x;
		int y = area.y;
		int fw = area.w;
		int fh = area.h;

		Gui.drawRect(x - 1, y - 1, x + fw + 1, y + fh + 1, 0xff181818);
		GlStateManager.color(1, 1, 1, 1);
		Icons.CHECKBOARD.renderArea(x, y, fw, fh);

		GuiDraw.scissor(x, y, fw, fh, context);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		for (FilteredResourceLocation child : this.picker.multiRL.children)
		{
			this.mc.renderEngine.bindTexture(child.path);

			int ww = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			int hh = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

			if (child.scaleToLargest)
			{
				ww = w;
				hh = h;
			}
			else if (child.scale != 1)
			{
				ww = (int) (ww * child.scale);
				hh = (int) (hh * child.scale);
			}

			if (ww > 0 && hh > 0)
			{
				area = this.calculate(-w / 2 + child.shiftX, -h / 2 + child.shiftY, -w / 2 + child.shiftX + ww, -h / 2 + child.shiftY + hh);

				if (child == this.picker.currentFRL)
				{
					Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x44ff0000);
					GlStateManager.color(1F, 1F, 1F);
					GlStateManager.enableBlend();
					GlStateManager.enableAlpha();
				}

				GuiDraw.drawBillboard(area.x, area.y, 0, 0, area.w, area.h, area.w, area.h);
			}
		}

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GuiDraw.unscissor(context);
	}

	private Area calculate(int a, int b, int c, int d)
	{
		int x1 = this.toX(a);
		int y1 = this.toY(b);
		int x2 = this.toX(c);
		int y2 = this.toY(d);

		int x = x1;
		int y = y1;
		int fw = x2 - x;
		int fh = y2 - y;

		Area.SHARED.set(x, y, fw, fh);

		return Area.SHARED;
	}
}