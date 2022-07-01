package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import mchorse.mclib.utils.ICloneable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

public interface IWritableLocation extends ICloneable<ResourceLocation>
{
    public void fromNbt(NBTBase nbt) throws Exception;

    public void fromJson(JsonElement element) throws Exception;

    public NBTBase writeNbt();

    public JsonElement writeJson();
}