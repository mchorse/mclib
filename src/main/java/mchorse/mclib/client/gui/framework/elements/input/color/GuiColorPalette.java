package mchorse.mclib.client.gui.framework.elements.input.color;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiUtils;

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

	@Override
	public boolean mouseClicked(GuiContext context)
	{
		if (super.mouseClicked(context))
		{
			return true;
		}

		if (this.area.isInside(context) && context.mouseButton == 0)
		{
			int index = this.area.getIndex(context.mouseX, context.mouseY, this.cellSize);
			int size = this.colors.size();

			index = size - 1 - index;

			if (index >= 0 && index < size && this.callback != null)
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

			this.mc.renderEngine.bindTexture(Icons.ICONS);

			if (this.area.h > this.cellSize)
			{
				GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 240, this.area.w, this.area.h - this.cellSize, 16, 16, 0, 0);
			}

			GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 240, count % elements * this.cellSize, this.area.h, 16, 16, 0, 0);

			for (int i = count - 1, j = 0; i >= 0; i--, j++)
			{
				Color c = this.colors.get(i);
				int x = this.area.x + j % elements * this.cellSize;
				int y = this.area.y + j / elements * this.cellSize;

				GuiColorPickerElement.drawAlphaPreviewQuad(x, y, x + this.cellSize, y + this.cellSize, c);
			}
		}

		super.draw(context);
	}
}
