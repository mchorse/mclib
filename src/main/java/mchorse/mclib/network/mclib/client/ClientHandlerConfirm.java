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
    private static TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP entityPlayerSP, PacketConfirm packetConfirm)
    {
        if (consumers.containsKey(packetConfirm.behaviourId))
        {
            consumers.remove(packetConfirm.behaviourId).accept(true);
        }
    }

    public static void addConsumer(int id, Consumer<Boolean> item)
    {
        consumers.put(id, item);
    }

    public static Entry getLastConsumerEntry()
    {
        return consumers.lastEntry();
    }
}
