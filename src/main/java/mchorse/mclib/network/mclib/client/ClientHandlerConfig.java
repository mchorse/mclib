package mchorse.mclib.network.mclib.client;

import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.common.PacketConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerConfig extends ClientMessageHandler<PacketConfig>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketConfig message)
    {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiDashboard)
        {
            GuiConfigPanel panel = ((GuiDashboard) screen).config;

            panel.storeServerConfig(message.config);
        }
    }
}
