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

import java.util.TreeMap;
import java.util.function.Consumer;

public class ClientHandlerConfirm extends ClientMessageHandler<PacketConfirm>
{
    public static TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP entityPlayerSP, PacketConfirm packetConfirm)
    {
        consumers.get(packetConfirm.behaviourId).accept(true);
        consumers.remove(packetConfirm.behaviourId);
    }
}
