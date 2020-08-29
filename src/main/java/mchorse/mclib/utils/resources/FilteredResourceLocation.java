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
	public float scale = 1F;
	public boolean scaleToLargest;
	public int shiftX;
	public int shiftY;

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
		int hashCode = (super.hashCode() + (this.scaleToLargest ? 1 : 0)) * 31;

		if (this.scale != 1) hashCode = (hashCode + (int) (this.scale * 1000)) * 31;
		if (this.shiftX != 0) hashCode = (hashCode + this.shiftX) * 31;
		if (this.shiftY != 0) hashCode = (hashCode + this.shiftY) * 31;

		return hashCode;
	}

	public boolean isDefault()
	{
		return !this.scaleToLargest && this.scale == 1F && this.shiftX == 0 && this.shiftY == 0;
	}

	@Override
	public void fromNbt(NBTBase nbt) throws Exception
	{
		NBTTagCompound tag = (NBTTagCompound) nbt;

		this.set(tag.getString("Path"));

		if (tag.hasKey("Scale"))
		{
			this.scale = tag.getInteger("Scale");
		}

		if (tag.hasKey("ScaleToLargest"))
		{
			this.scaleToLargest = tag.getBoolean("ScaleToLargest");
		}

		if (tag.hasKey("ShiftX"))
		{
			this.shiftX = tag.getInteger("ShiftX");
		}

		if (tag.hasKey("ShiftY"))
		{
			this.shiftY = tag.getInteger("ShiftY");
		}
	}

	@Override
	public void fromJson(JsonElement element) throws Exception
	{
		JsonObject object = element.getAsJsonObject();

		this.set(object.get("path").getAsString());

		if (object.has("scale"))
		{
			this.scale = object.get("scale").getAsFloat();
		}

		if (object.has("scaleToLargest"))
		{
			this.scaleToLargest = object.get("scaleToLargest").getAsBoolean();
		}

		if (object.has("shiftX"))
		{
			this.shiftX = object.get("shiftX").getAsInt();
		}

		if (object.has("shiftY"))
		{
			this.shiftY = object.get("shiftY").getAsInt();
		}
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

		if (this.scale != 1) tag.setFloat("Scale", this.scale);
		if (this.scaleToLargest) tag.setBoolean("ScaleToLargest", this.scaleToLargest);
		if (this.shiftX != 0) tag.setInteger("ShiftX", this.shiftX);
		if (this.shiftY != 0) tag.setInteger("ShiftY", this.shiftY);

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

		if (this.scale != 1) object.addProperty("scale", this.scale);
		if (this.scaleToLargest) object.addProperty("scaleToLargest", this.scaleToLargest);
		if (this.shiftX != 0) object.addProperty("shiftX", this.shiftX);
		if (this.shiftY != 0) object.addProperty("shiftY", this.shiftY);

		return object;
	}

	@Override
	public ResourceLocation clone()
	{
		FilteredResourceLocation location = new FilteredResourceLocation(this.toString());

		location.scale = this.scale;
		location.scaleToLargest = this.scaleToLargest;
		location.shiftX = this.shiftX;
		location.shiftY = this.shiftY;

		return location;
	}
}