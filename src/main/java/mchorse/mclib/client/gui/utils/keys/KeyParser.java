package mchorse.mclib.client.gui.utils.keys;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;

public class KeyParser
{
    public static IKey keyFromBytes(ByteBuf buffer)
    {
        byte type = buffer.readByte();

        if (type == 0)
        {
            return IKey.str(ByteBufUtils.readUTF8String(buffer));
        }
        else if (type == 1)
        {
            String key = ByteBufUtils.readUTF8String(buffer);
            List<String> args = new ArrayList<String>();

            for (int i = 0, c = buffer.readInt(); i < c; i++)
            {
                args.add(ByteBufUtils.readUTF8String(buffer));
            }

            return args.isEmpty() ? IKey.lang(key) : IKey.format(key, args.toArray(new String[args.size()]));
        }
        else if (type == 2)
        {
            List<IKey> keys = new ArrayList<>();

            for (int i = 0, c = buffer.readInt(); i < c; i++)
            {
                IKey key = keyFromBytes(buffer);

                if (key != null)
                {
                    keys.add(key);
                }
            }

            return IKey.comp(keys.toArray(new IKey[keys.size()]));
        }

        return null;
    }

    public static void keyToBytes(ByteBuf buffer, IKey key)
    {
        if (key instanceof StringKey)
        {
            buffer.writeByte(0);
            ByteBufUtils.writeUTF8String(buffer, ((StringKey) key).string);
        }
        else if (key instanceof LangKey)
        {
            LangKey lang = (LangKey) key;

            buffer.writeByte(1);
            ByteBufUtils.writeUTF8String(buffer, lang.key);
            buffer.writeInt(lang.args.length);

            for (String arg : lang.args)
            {
                ByteBufUtils.writeUTF8String(buffer, arg);
            }
        }
        else if (key instanceof CompoundKey)
        {
            CompoundKey compound = (CompoundKey) key;

            buffer.writeByte(2);
            buffer.writeInt(compound.keys.length);

            for (IKey childKey : compound.keys)
            {
                keyToBytes(buffer, childKey);
            }
        }
        else
        {
            /* Just some random value */
            buffer.writeByte(100);
        }
    }
}
