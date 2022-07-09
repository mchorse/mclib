package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import mchorse.mclib.utils.ICopy;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

public interface IWritableLocation<T> extends ICopy<T>
{
    public void fromNbt(NBTBase nbt) throws Exception;

    public void fromJson(JsonElement element) throws Exception;

    public NBTBase writeNbt();

    public JsonElement writeJson();
}