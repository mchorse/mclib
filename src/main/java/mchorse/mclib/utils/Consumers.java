package mchorse.mclib.utils;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Consumers<C>
{
    private final TreeMap<Integer, Consumer<C>> callbacks = new TreeMap<>();

    public void remove(int id)
    {
        this.callbacks.remove(id);
    }

    /**
     * Executes the Consumer at the given id, if present, and remove after execution.
     * @param id
     * @param value
     */
    public void consume(int id, C value)
    {
        this.consume(id, value, true);
    }

    /**
     * Executes the Consumer at the given id, if present and remove after execution.
     * @param id
     * @param value
     * @param remove whether to remove the consumer after execution
     */
    public void consume(int id, C value, boolean remove)
    {
        Consumer<C> callback = this.callbacks.get(id);

        if (callback != null)
        {
            callback.accept(value);

            if (remove)
            {
                callbacks.remove(id);
            }
        }
    }

    public int register(Consumer<C> callback)
    {
        if (!this.callbacks.containsValue(callback))
        {
            Map.Entry<Integer, Consumer<C>> last = this.callbacks.lastEntry();

            int id = (last != null) ? last.getKey() + 1 : 0;

            this.callbacks.put(id, callback);

            return id;
        }
        else
        {
            for (Map.Entry<Integer, Consumer<C>> entry : callbacks.entrySet())
            {
                if (entry.getValue() == callback)
                {
                    return entry.getKey();
                }
            }
        }

        /* this statement should never be reached... */
        return -1;
    }
}
