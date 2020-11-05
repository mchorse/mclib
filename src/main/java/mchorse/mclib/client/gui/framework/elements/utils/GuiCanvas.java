package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Scale;
import net.minecraft.client.Minecraft;

public class GuiCanvas extends GuiElement
{
	public Scale scaleX = new Scale(false);
	public Scale scaleY = new Scale(false);

	public boolean dragging;
	public int mouse;

	protected int lastX;
	protected int lastY;
	protected double lastT;
	protected double lastV;

	public GuiCanvas(Minecraft mc)
	{
		super(mc);
	}

	public int toX(double x)
	{
		return (int) Math.round(this.scaleX.to(x) + this.area.mx());
	}

	public double fromX(int mouseX)
	{
		return this.scaleX.from(mouseX - this.area.mx());
	}

	public int toY(double y)
	{
		return (int) Math.round(this.scaleY.to(y) + this.area.my());
	}

	public double fromY(int mouseY)
	{
		return this.scaleY.from(mouseY - this.area.my());
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.area.isInside(context))
		{
			this.dragging = true;
			this.mouse = context.mouseButton;

			this.lastX = context.mouseX;
			this.lastY = context.mouseY;

			this.startDragging(context);

			return true;
		}

		return false;
	}

	protected void startDragging(GuiContext context)
	{
		this.lastT = this.scaleX.shift;
		this.lastV = this.scaleY.shift;
	}

	@Override
	public boolean mouseScrolled(GuiContext context)
	{
		if (super.mouseScrolled(context))
		{
			return true;
		}

		if (this.area.isInside(context.mouseX, context.mouseY) && !this.dragging)
		{
			int scroll = context.mouseWheel;

			if (!Minecraft.isRunningOnMac)
			{
				scroll = -scroll;
			}

			this.zoom(scroll);

			return true;
		}

		return false;
	}

	protected void zoom(int scroll)
	{
		this.scaleX.zoom(Math.copySign(this.getZoomFactor(this.scaleX.zoom), scroll), 0.01F, 50F);
		this.scaleY.zoom(Math.copySign(this.getZoomFactor(this.scaleY.zoom), scroll), 0.01F, 50F);
	}

	protected double getZoomFactor(double zoom)
	{
		double factor = 0;

		if (zoom < 0.2F) factor = 0.005F;
		else if (zoom < 1.0F) factor = 0.025F;
		else if (zoom < 2.0F) factor = 0.1F;
		else if (zoom < 15.0F) factor = 0.5F;
		else if (zoom <= 50.0F) factor = 1F;

		return factor;
	}

	@Override
	public void mouseReleased(GuiContext context)
	{
		super.mouseReleased(context);

		this.dragging = false;
	}

	@Override
	public void draw(GuiContext context)
	{
		this.dragging(context);

		GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
		this.drawCanvas(context);
		GuiDraw.unscissor(context);

		super.draw(context);
	}

	protected void dragging(GuiContext context)
	{
		if (this.dragging && this.mouse == 2)
		{
			this.scaleX.shift = -(context.mouseX - this.lastX) / this.scaleX.zoom + this.lastT;
			this.scaleY.shift = -(context.mouseY - this.lastY) / this.scaleY.zoom + this.lastV;
		}
	}

	protected void drawCanvas(GuiContext context)
	{}
}