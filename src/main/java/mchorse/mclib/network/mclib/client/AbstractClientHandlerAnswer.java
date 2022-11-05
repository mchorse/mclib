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

public abstract class AbstractClientHandlerAnswer<T extends PacketAnswer> extends ClientMessageHandler<T>
{
    protected static final Consumers<Object> consumers = new Consumers<>();

    @Override
    public void run(EntityPlayerSP player, PacketAnswer message)
    {
        consumers.consume(message.getCallbackID(), message.getValue());
    }

    protected static int registerConsumer(Consumer<Object> callback)
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


    /**
     * Send the answer to the player. The answer's generic datatype needs to be equal
     * to the Consumer input datatype that has been registered on the client side.
     * @param receiver
     * @param answer
     * @param <T> the type of the registered Consumer input datatype.
     */
    public static <T extends Serializable> void sendAnswerTo(EntityPlayerMP receiver, PacketAnswer<T> answer)
    {
        Dispatcher.sendTo(answer, receiver);
    }
}
