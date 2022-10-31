package mchorse.mclib.network.mclib.client;

import mchorse.mclib.network.AbstractDispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.IAnswerRequest;
import mchorse.mclib.network.mclib.common.PacketAnswer;
import mchorse.mclib.utils.Consumers;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.Serializable;
import java.util.function.Consumer;

public class ClientHandlerAnswer extends ClientMessageHandler<PacketAnswer>
{
    private static final Consumers<Object> consumers = new Consumers<>();

    @Override
    public void run(EntityPlayerSP player, PacketAnswer message)
    {
        consumers.consume(message.getCallbackID(), message.getValue());
    }

    private static int registerConsumer(Consumer<Object> callback)
    {
        return consumers.register(callback);
    }

    /**
     * This will register the consumer and set the resulting callbackID to the provided AnswerRequest.
     * The AnswerRequest will then be sent to the server.
     * @param request
     * @param callback
     */
    @SideOnly(Side.CLIENT)
    public static <T extends Serializable> void requestServerAnswer(AbstractDispatcher dispatcher, IAnswerRequest<T> request, Consumer<T> callback)
    {
        request.setCallbackID(registerConsumer((obj) ->
        {
            callback.accept((T) obj);
        }));

        dispatcher.sendToServer(request);
    }

    public static void sendAnswerTo(EntityPlayerMP receiver, PacketAnswer answer)
    {
        Dispatcher.sendTo(answer, receiver);
    }
}
