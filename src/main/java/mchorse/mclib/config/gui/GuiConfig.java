package mchorse.mclib.config.gui;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.ScrollArea;
import mchorse.mclib.client.gui.utils.resizers.layout.ColumnResizer;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfig extends GuiElement
{
	public GuiIconElement reload;
	public GuiLabelListElement<String> mods;
	public GuiScrollElement options;

	private Config config;

	public GuiConfig(Minecraft mc)
	{
		super(mc);

		this.reload = new GuiIconElement(mc, Icons.REFRESH, (button) -> this.reload());
		this.reload.tooltip(I18n.format("mclib.gui.config.reload_tooltip"), Direction.BOTTOM);
		this.mods = new GuiLabelListElement<String>(mc, (mod) -> this.selectConfig(mod.get(0).value));
		this.options = new GuiScrollElement(mc, ScrollArea.ScrollDirection.HORIZONTAL);

		this.reload.flex().relative(this).set(110 - 14, 12, 16, 16);
		this.mods.flex().relative(this).set(10, 35, 100, 0).h(1, -45);
		this.options.flex().relative(this).set(120, 0, 0, 0).w(1, -120).h(1F);
		this.options.flex().column(5).scroll().width(240).height(20).padding(15);

		for (Config config : McLib.proxy.configs.modules.values())
		{
			this.mods.add(config.getTitle(), config.id);
		}

		this.mods.sort();
		this.add(this.reload, this.mods, this.options);
		this.selectConfig("mclib");
		this.markContainer();
	}

	private void reload()
	{
		McLib.proxy.configs.reload();
		this.refresh();
	}

	private void selectConfig(String mod)
	{
		this.mods.setCurrentValue(mod);
		this.config = McLib.proxy.configs.modules.get(mod);
		this.refresh();
	}

	public void refresh()
	{
		this.options.clear();

		boolean first = true;

		for (ConfigCategory category : this.config.categories.values())
		{
			if (!category.isVisible())
			{
				continue;
			}

			GuiLabel label = new GuiLabel(this.mc, category.getTitle()).anchor(0, 1).background(0x88000000);
			label.flex().set(0, 0, this.font.getStringWidth(label.label), 40);

			if (first)
			{
				label.anchor(0, 0).flex().h(0, this.font.FONT_HEIGHT);
			}

			this.options.add(label.tooltip(category.getTooltip(), Direction.BOTTOM));

			for (IConfigValue value : category.values.values())
			{
				if (!value.isVisible())
				{
					continue;
				}

				for (GuiElement element : value.getFields(this.mc, this))
				{
					this.options.add(element);
				}
			}

			first = false;
		}

		this.resize();
	}

	@Override
	public void draw(GuiContext context)
	{
		this.area.draw(0xaa000000);
		this.mods.area.draw(0xdd000000, -10, -35, -10, -10);
		this.font.drawStringWithShadow(I18n.format("mclib.gui.config.title"), this.area.x + 10, this.area.y + 20 - this.font.FONT_HEIGHT / 2, 0xffffff);

		super.draw(context);
	}
}