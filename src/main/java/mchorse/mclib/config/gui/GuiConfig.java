package mchorse.mclib.config.gui;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.resizers.ColumnResizer;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfig extends GuiElement
{
	public GuiLabelListElement<String> mods;
	public GuiElement options;
	public ColumnResizer column;

	private Config config;

	public GuiConfig(Minecraft mc)
	{
		super(mc);

		this.mods = new GuiLabelListElement<String>(mc, (mod) -> this.selectConfig(mod.value));
		this.options = new GuiElement(mc);
		this.column = new ColumnResizer(this.options.area, 5, 10);

		this.mods.resizer().parent(this.area).set(10, 10, 100, 0).h(1, -20);
		this.options.resizer().parent(this.area).set(120, 0, 0, 0).w(1, -120).h(1, 0);

		for (Config config : McLib.proxy.configs.modules.values())
		{
			this.mods.add(config.getTitle(), config.id);
		}

		this.mods.sort();
		this.add(this.mods, this.options);
		this.selectConfig("mclib");
	}

	private void selectConfig(String mod)
	{
		this.mods.setCurrentValue(mod);
		this.options.clear();

		this.config = McLib.proxy.configs.modules.get(mod);

		boolean first = true;

		for (ConfigCategory category : this.config.categories.values())
		{
			GuiLabel label = new GuiLabel(this.mc, this.config.getCategoryTitle(category.id)).anchor(0, 1);
			label.resizer().set(0, 0, this.font.getStringWidth(label.label), 20);

			if (first)
			{
				label.anchor(0, 0).resizer().h(0, this.font.FONT_HEIGHT);
			}

			label.setResizer(this.column.child(label));
			this.options.add(label.tooltip(this.config.getCategoryTooltip(category.id), Direction.BOTTOM));

			for (IConfigValue value : category.values.values())
			{
				for (GuiElement element : value.getFields(this.mc, this.config, category))
				{
					this.options.add(element);
					element.setResizer(this.column.child(element));
				}
			}

			first = false;
		}

		this.resize();
	}

	@Override
	public void resize()
	{
		this.column.reset();

		super.resize();
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0x88000000);
		Gui.drawRect(this.area.x, this.area.y, this.area.x + this.mods.area.w + 20, this.area.getY(1), 0x88000000);

		super.draw(context);
	}
}