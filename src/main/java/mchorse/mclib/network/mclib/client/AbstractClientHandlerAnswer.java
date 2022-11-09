package mchorse.mclib.network.mclib.client;

import mchorse.mclib.McLib;
import mchorse.mclib.network.AbstractDispatcher;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.mclib.network.mclib.Dispatcher;
import mchorse.mclib.network.mclib.common.IAnswerRequest;
import mchorse.mclib.network.mclib.common.PacketAnswer;
import mchorse.mclib.utils.Consumers;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public abstract class AbstractClientHandlerAnswer<T extends PacketAnswer> extends ClientMessageHandler<T>
{
    protected static final Consumers<Object> CONSUMERS = new Consumers<>();
    protected static final Map<Integer, Long> TIME = new HashMap<>();
    /**
     * For logging / debugging purposes
     */
    protected static final Map<Integer, IAnswerRequest<?>> REQUESTS = new HashMap<>();

    @Override
    public void run(EntityPlayerSP player, PacketAnswer message)
    {
        CONSUMERS.consume(message.getCallbackID(), message.getValue());
        TIME.remove(message.getCallbackID());
        REQUESTS.remove(message.getCallbackID());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START) return;

        long now = System.currentTimeMillis();

        for (Map.Entry<Integer, Long> entry : TIME.entrySet())
        {
            //5 minute timeout
            if (entry.getValue() + 5 * 60000 < now)
            {
                IAnswerRequest<?> request = REQUESTS.get(entry.getKey());

                McLib.LOGGER.info("Timeout for the answer request " + request.getClass().getSimpleName() + ". The consumer has been removed.");

                CONSUMERS.remove(entry.getKey());
                TIME.remove(entry.getKey());
                REQUESTS.remove(entry.getKey());
            }
        }
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
        int id = CONSUMERS.register((obj) ->
        {
            T param;

            try
            {
                param = (T) obj;
            }
            catch (ClassCastException e)
            {
                McLib.LOGGER.error("Type of the answer's value is incompatible with the consumer generic type!");
                e.printStackTrace();

                return;
            }

            callback.accept(param);
        });

        TIME.put(id, System.currentTimeMillis());
        REQUESTS.put(id, request);
        request.setCallbackID(id);

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
