package mchorse.mclib.network.mclib.common;

import java.util.NoSuchElementException;

public interface IAnswerRequest
{
    /**
     * Call this before calling {@link #getAnswer(Object[])}
     * @return true if this packet requires an answer to be sent back
     */
    boolean requiresAnswer();

    /**
     * Get an answer packet with the provided values and callbackID
     * @param value
     * @return
     * @throws NoSuchElementException if {@link #requiresAnswer()} returns false.
     */
    PacketAnswer getAnswer(Object[] value) throws NoSuchElementException;
}
