package mchorse.mclib.network.mclib.client;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.common.PacketAnswer;
import mchorse.mclib.utils.Consumers;

import java.util.function.Consumer;

public abstract class ClientHandlerAnswer<T extends PacketAnswer> extends ClientMessageHandler<T>
{
    private static final Consumers<Object[]> consumers = new Consumers();

    protected static void consume(int id, Object[] value)
    {
        consumers.consume(id, value);
    }

    public static int registerConsumer(Consumer<Object[]> callback)
    {
        return consumers.register(callback);
    }
}
