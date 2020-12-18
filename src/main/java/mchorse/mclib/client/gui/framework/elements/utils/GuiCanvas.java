package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.utils.Scale;
import mchorse.mclib.client.gui.utils.ScrollDirection;
import net.minecraft.client.Minecraft;

public class GuiCanvas extends GuiElement
{
	public Scale scaleX;
	public Scale scaleY;

	public boolean dragging;
	public int mouse;

	protected int lastX;
	protected int lastY;
	protected double lastT;
	protected double lastV;

	public GuiCanvas(Minecraft mc)
	{
		super(mc);

		this.scaleX = new Scale(this.area, false);
		this.scaleX.anchor(0.5F);
		this.scaleY = new Scale(this.area, ScrollDirection.VERTICAL, false);
		this.scaleY.anchor(0.5F);
	}

	public int toX(double x)
	{
		return (int) Math.round(this.scaleX.to(x));
	}

	public double fromX(int mouseX)
	{
		return this.scaleX.from(mouseX);
	}

	public int toY(double y)
	{
		return (int) Math.round(this.scaleY.to(y));
	}

	public double fromY(int mouseY)
	{
		return this.scaleY.from(mouseY);
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
		this.lastT = this.scaleX.getShift();
		this.lastV = this.scaleY.getShift();
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

			if (!Minecraft.IS_RUNNING_ON_MAC)
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
		this.scaleX.zoom(Math.copySign(this.scaleX.getZoomFactor(), scroll), 0.001, 1000);
		this.scaleY.zoom(Math.copySign(this.scaleY.getZoomFactor(), scroll), 0.001, 1000);
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
			this.scaleX.setShift(-(context.mouseX - this.lastX) / this.scaleX.getZoom() + this.lastT);
			this.scaleY.setShift(-(context.mouseY - this.lastY) / this.scaleY.getZoom() + this.lastV);
		}
	}

	protected void drawCanvas(GuiContext context)
	{}
}