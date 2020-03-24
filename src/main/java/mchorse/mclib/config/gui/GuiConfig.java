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
import mchorse.mclib.client.gui.utils.resizers.ColumnResizer;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.utils.Direction;
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
	public ColumnResizer column;

	private Config config;
	private boolean save;
	private long saveTime;

	public GuiConfig(Minecraft mc)
	{
		super(mc);

		this.reload = new GuiIconElement(mc, Icons.REFRESH, (button) -> this.reload());
		this.reload.tooltip(I18n.format("mclib.gui.config.reload_tooltip"), Direction.BOTTOM);
		this.mods = new GuiLabelListElement<String>(mc, (mod) -> this.selectConfig(mod.get(0).value));
		this.options = new GuiScrollElement(mc, ScrollArea.ScrollDirection.HORIZONTAL);

		this.reload.resizer().parent(this.area).set(110 - 14, 12, 16, 16);
		this.mods.resizer().parent(this.area).set(10, 35, 100, 0).h(1, -45);
		this.options.resizer().parent(this.area).set(120, 0, 0, 0).w(1, -120).h(1, 0);

		this.column = new ColumnResizer(this.options, 5);
		this.column.dontCollect().padding(15);
		this.options.setResizer(this.column);

		for (Config config : McLib.proxy.configs.modules.values())
		{
			this.mods.add(config.getTitle(), config.id);
		}

		this.mods.sort();
		this.add(this.reload, this.mods, this.options);
		this.selectConfig("mclib");
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
				if (!value.isVisible())
				{
					continue;
				}

				for (GuiElement element : value.getFields(this.mc, this.config, category, this::save))
				{
					this.options.add(element);
					element.setResizer(this.column.child(element));
				}
			}

			first = false;
		}

		this.resize();
	}

	public void save(IConfigValue value)
	{
		this.save = true;
		this.saveTime = System.currentTimeMillis() + 500;
	}

	@Override
	public void resize()
	{
		super.resize();

		this.options.scroll.scrollSize = this.column.getSize();
		this.options.scroll.clamp();
	}

	@Override
	public void draw(GuiContext context)
	{
		if (this.save && System.currentTimeMillis() > this.saveTime)
		{
			this.save = false;
			this.config.save();
		}

		this.area.draw(0xaa000000);
		Gui.drawRect(this.area.x, this.area.y, this.area.x + this.mods.area.w + 20, this.area.getY(1), 0xdd000000);
		this.font.drawStringWithShadow(I18n.format("mclib.gui.config.title"), this.area.x + 10, this.area.y + 20 - this.font.FONT_HEIGHT / 2, 0xffffff);

		super.draw(context);
	}
}