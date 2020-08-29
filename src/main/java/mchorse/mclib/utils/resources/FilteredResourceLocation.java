package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

public class FilteredResourceLocation extends TextureLocation implements IWritableLocation
{
	public boolean scaleToLargest;

	public static FilteredResourceLocation from(NBTBase base)
	{
		if (base instanceof NBTTagCompound)
		{
			try
			{
				FilteredResourceLocation location = new FilteredResourceLocation("");

				location.fromNbt(base);

				return location;
			}
			catch (Exception e)
			{}
		}

		return null;
	}

	public static FilteredResourceLocation from(JsonElement element)
	{
		if (element.isJsonObject())
		{
			try
			{
				FilteredResourceLocation location = new FilteredResourceLocation("");

				location.fromJson(element);

				return location;
			}
			catch (Exception e)
			{}
		}

		return null;
	}

	public FilteredResourceLocation(String domain, String path)
	{
		super(domain, path);
	}

	public FilteredResourceLocation(String string)
	{
		super(string);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() + (this.scaleToLargest ? 1 : 0) * 31;
	}

	public boolean isDefault()
	{
		return !this.scaleToLargest;
	}

	@Override
	public void fromNbt(NBTBase nbt) throws Exception
	{
		NBTTagCompound tag = (NBTTagCompound) nbt;

		this.set(tag.getString("Path"));
		this.scaleToLargest = tag.getBoolean("ScaleToLargest");
	}

	@Override
	public void fromJson(JsonElement element) throws Exception
	{
		JsonObject object = element.getAsJsonObject();

		this.set(object.get("path").getAsString());
		this.scaleToLargest = object.get("scaleToLargest").getAsBoolean();
	}

	@Override
	public NBTBase writeNbt()
	{
		if (this.isDefault())
		{
			return new NBTTagString(this.toString());
		}

		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("Path", this.toString());
		tag.setBoolean("ScaleToLargest", this.scaleToLargest);

		return tag;
	}

	@Override
	public JsonElement writeJson()
	{
		if (this.isDefault())
		{
			return new JsonPrimitive(this.toString());
		}

		JsonObject object = new JsonObject();

		object.addProperty("path", this.toString());
		object.addProperty("scaleToLargest", this.scaleToLargest);

		return object;
	}

	@Override
	public ResourceLocation clone()
	{
		FilteredResourceLocation location = new FilteredResourceLocation(this.toString());

		location.scaleToLargest = this.scaleToLargest;

		return location;
	}
}