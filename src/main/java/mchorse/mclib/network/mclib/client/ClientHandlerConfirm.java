package mchorse.mclib.network.mclib.client;

import mchorse.mclib.client.gui.framework.elements.GuiConfirmationScreen;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.PacketConfirm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ClientHandlerConfirm extends ClientMessageHandler<PacketConfirm>
{

    /**
     * Renders the GUI based on the enum value of packet. Every GUI confirmation screen dispatches the packet back to the server
     * @param packet
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP entityPlayerSP, PacketConfirm packet)
    {
        switch(packet.gui)
        {
            case MCSCREEN:
                Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmationScreen(IKey.lang(packet.messageKey), (value) ->
                {
                    this.dispatchPacket(packet, value);
                }));
        }
    }

    private void dispatchPacket(PacketConfirm packet, boolean value)
    {
        packet.confirm = value;

        Dispatcher.sendToServer(packet);
    }

    public enum GUI {
        MCSCREEN;
    }
}
