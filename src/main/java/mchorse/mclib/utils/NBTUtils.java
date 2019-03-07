package mchorse.mclib.utils;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

/**
 * NBT utils 
 */
public class NBTUtils
{
    public static void readFloatList(NBTTagList list, float[] array)
    {
        int count = Math.min(array.length, list.tagCount());

        for (int i = 0; i < count; i++)
        {
            array[i] = list.getFloatAt(i);
        }
    }

    public static NBTTagList writeFloatList(NBTTagList list, float[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            list.appendTag(new NBTTagFloat(array[i]));
        }

        return list;
    }
}