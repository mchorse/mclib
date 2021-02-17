package mchorse.mclib.config.gui;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.mclib.GuiAbstractDashboard;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.ScrollDirection;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.ConfigCategory;
import mchorse.mclib.config.values.IConfigValue;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketRequestConfigs;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiConfigPanel extends GuiDashboardPanel<GuiAbstractDashboard>
{
    public GuiIconElement request;
    public GuiIconElement reload;
    public GuiLabelListElement<String> mods;
    public GuiScrollElement options;

    private Config config;
    private IKey title = IKey.lang("mclib.gui.config.title");

    private Map<String, Config> serverConfigs;

    public GuiConfigPanel(Minecraft mc, GuiAbstractDashboard dashboard)
    {
        super(mc, dashboard);

        this.request = new GuiIconElement(mc, Icons.DOWNLOAD, (button) -> this.request());
        this.request.tooltip(IKey.lang("mclib.gui.config.request_tooltip"), Direction.BOTTOM);
        this.reload = new GuiIconElement(mc, Icons.REFRESH, (button) -> this.reload());
        this.reload.tooltip(IKey.lang("mclib.gui.config.reload_tooltip"), Direction.BOTTOM);
        this.mods = new GuiLabelListElement<String>(mc, (mod) -> this.selectConfig(mod.get(0).value));
        this.options = new GuiScrollElement(mc, ScrollDirection.HORIZONTAL);
        this.options.scroll.scrollSpeed = 51;

        this.reload.flex().relative(this).set(120 - 14, 12, 16, 16);
        this.request.flex().relative(this.reload.resizer()).set(-20, 0, 16, 16);
        this.mods.flex().relative(this).set(10, 35, 110, 0).h(1, -45);
        this.options.flex().relative(this).set(130, 0, 0, 0).w(1, -120).h(1F);
        this.options.flex().column(5).scroll().width(240).height(20).padding(15);

        this.fillClientMods();

        this.add(this.reload, this.request, this.mods, this.options);
        this.selectConfig("mclib");
        this.markContainer();
    }

    private void fillClientMods()
    {
        for (Config config : McLib.proxy.configs.modules.values())
        {
            if (!config.isServerSide())
            {
                this.mods.add(IKey.lang(config.getTitleKey()), config.id);
            }
        }

        this.mods.sort();
    }

    @Override
    public void open()
    {
        this.request.setVisible(!Minecraft.getMinecraft().isIntegratedServerRunning() && OpHelper.isPlayerOp());
    }

    @Override
    public void close()
    {
        if (this.serverConfigs != null)
        {
            this.request();
        }
    }

    public void storeServerConfig(Config config)
    {
        this.serverConfigs.put(config.id, config);
        this.mods.add(IKey.lang(config.getTitleKey()), config.id);
        this.mods.sort();

        if (config.id.equals("mclib"))
        {
            this.selectConfig("mclib");
        }
    }

    private void request()
    {
        this.config = null;
        this.mods.clear();

        if (this.serverConfigs == null)
        {
            this.serverConfigs = new HashMap<String, Config>();
            this.mods.setCurrent((Label<String>) null);

            Dispatcher.sendToServer(new PacketRequestConfigs());
        }
        else
        {
            this.serverConfigs = null;

            this.fillClientMods();
            this.selectConfig("mclib");
        }

        this.reload.setEnabled(this.serverConfigs == null);
        this.request.both(this.serverConfigs == null ? Icons.DOWNLOAD : Icons.UPLOAD);
    }

    private void reload()
    {
        if (this.serverConfigs == null)
        {
            McLib.proxy.configs.reload();
            this.refresh();
        }
    }

    private void selectConfig(String mod)
    {
        this.mods.setCurrentValue(mod);
        this.config = this.serverConfigs == null ? McLib.proxy.configs.modules.get(mod) : this.serverConfigs.get(mod);
        this.refresh();
    }

    public void refresh()
    {
        if (this.config == null)
        {
            return;
        }

        this.options.removeAll();

        boolean first = true;
        boolean checkForClient = this.serverConfigs != null;

        for (ConfigCategory category : this.config.categories.values())
        {
            if (!category.isVisible() || (checkForClient && category.isClientSide()))
            {
                continue;
            }

            GuiLabel label = Elements.label(IKey.lang(category.getTitleKey()), 40).anchor(0, 1).background(0x88000000);

            label.flex().w(this.font.getStringWidth(label.label.get()));

            if (first)
            {
                label.anchor(0, 0).flex().h(0, this.font.FONT_HEIGHT);
            }

            this.options.add(label.tooltip(IKey.lang(category.getTooltipKey()), Direction.BOTTOM));

            for (IConfigValue value : category.values.values())
            {
                if (!value.isVisible() || (checkForClient && value.isClientSide()))
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
        this.mods.area.draw(0xdd000000, -10, -35, -10, -10);
        this.font.drawStringWithShadow(this.title.get(), this.area.x + 10, this.area.y + 20 - this.font.FONT_HEIGHT / 2, 0xffffff);

        super.draw(context);
    }
}