package mchorse.mclib.client.gui.framework.elements.input.color;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

/**
 * Color palette GUI element
 *
 * This element allows to provide a way to select a color from a grid like
 * list
 */
public class GuiColorPalette extends GuiElement
{
	public List<Color> colors;
	public Consumer<Color> callback;
	public int cellSize = 10;

	public GuiColorPalette(Minecraft mc, Consumer<Color> callback)
	{
		super(mc);

		this.callback = callback;
	}

	public GuiColorPalette colors(List<Color> colors)
	{
		this.colors = colors;

		return this;
	}

	public GuiColorPalette cellSize(int cellSize)
	{
		this.cellSize = cellSize;

		return this;
	}

	public int getHeight(int width)
	{
		return MathUtils.gridRows(this.colors.size(), this.cellSize, width) * this.cellSize;
	}

	public boolean hasColor(int index)
	{
		return index >= 0 && index < this.colors.size();
	}

	public int getIndex(GuiContext context)
	{
		return this.colors.size() - 1 - this.area.getIndex(context.mouseX, context.mouseY, this.cellSize);
	}

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.area.isInside(context) && context.mouseButton == 0)
		{
			int index = this.getIndex(context);

			if (this.hasColor(index) && this.callback != null)
			{
				this.callback.accept(this.colors.get(index));
			}

			return true;
		}

		return false;
	}

	@Override
	public void draw(GuiContext context)
	{
		/* Draw recent colors panel */
		int count = this.colors.size();

		if (count > 0)
		{
			int elements = this.area.w / this.cellSize;

			if (this.area.h > this.cellSize)
			{
				Icons.CHECKBOARD.renderArea(this.area.x, this.area.y, this.area.w, this.area.h - this.cellSize);
			}

			Icons.CHECKBOARD.renderArea(this.area.x, this.area.ey() - this.cellSize, count % elements * this.cellSize, this.cellSize);

			for (int i = count - 1, j = 0; i >= 0; i--, j++)
			{
				Color c = this.colors.get(i);
				int x = this.area.x + j % elements * this.cellSize;
				int y = this.area.y + j / elements * this.cellSize;

				GuiColorPicker.drawAlphaPreviewQuad(x, y, x + this.cellSize, y + this.cellSize, c);
			}
		}

		super.draw(context);
	}
}
