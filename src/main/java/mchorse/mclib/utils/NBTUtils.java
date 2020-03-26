package mchorse.mclib.utils;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

import javax.vecmath.Vector3f;

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

    public static void readFloatList(NBTTagList list, Vector3f vector)
    {
        if (list.tagCount() != 3)
        {
            return;
        }

        vector.x = list.getFloatAt(0);
        vector.y = list.getFloatAt(1);
        vector.z = list.getFloatAt(2);
    }

    public static NBTTagList writeFloatList(NBTTagList list, Vector3f vector)
    {
        list.appendTag(new NBTTagFloat(vector.x));
        list.appendTag(new NBTTagFloat(vector.y));
        list.appendTag(new NBTTagFloat(vector.z));

        return list;
    }
}