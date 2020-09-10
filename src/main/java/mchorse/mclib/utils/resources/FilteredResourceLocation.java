package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class FilteredResourceLocation implements IWritableLocation
{
	public static final int DEFAULT_COLOR = 0xffffffff;

	public ResourceLocation path;

	public int color = DEFAULT_COLOR;
	public float scale = 1F;
	public boolean scaleToLargest;
	public int shiftX;
	public int shiftY;

	/* Filters */
	public int pixelate = 1;
	public boolean erase;

	public static FilteredResourceLocation from(NBTBase base)
	{
		try
		{
			FilteredResourceLocation location = new FilteredResourceLocation();

			location.fromNbt(base);

			return location;
		}
		catch (Exception e)
		{}

		return null;
	}

	public static FilteredResourceLocation from(JsonElement element)
	{
		try
		{
			FilteredResourceLocation location = new FilteredResourceLocation();

			location.fromJson(element);

			return location;
		}
		catch (Exception e)
		{}

		return null;
	}

	public FilteredResourceLocation()
	{}

	public FilteredResourceLocation(ResourceLocation path)
	{
		this.path = path;
	}

	@Override
	public String toString()
	{
		return this.path == null ? "" : this.path.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (super.equals(obj))
		{
			return true;
		}

		if (obj instanceof FilteredResourceLocation)
		{
			FilteredResourceLocation frl = (FilteredResourceLocation) obj;

			return Objects.equals(this.path, frl.path)
				&& this.scaleToLargest == frl.scaleToLargest
				&& this.color == frl.color
				&& this.scale == frl.scale
				&& this.shiftX == frl.shiftX
				&& this.shiftY == frl.shiftY
				&& this.pixelate == frl.pixelate
				&& this.erase == frl.erase;
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		int hashCode = this.path.hashCode();

		hashCode = 31 * hashCode + (this.scaleToLargest ? 1 : 0);
		hashCode = 31 * hashCode + this.color;
		hashCode = 31 * hashCode + (int) (this.scale * 1000);
		hashCode = 31 * hashCode + this.shiftX;
		hashCode = 31 * hashCode + this.shiftY;
		hashCode = 31 * hashCode + this.pixelate;
		hashCode = 31 * hashCode + (this.erase ? 1 : 0);

		return hashCode;
	}

	public boolean isDefault()
	{
		return this.color == DEFAULT_COLOR && !this.scaleToLargest && this.scale == 1F && this.shiftX == 0 && this.shiftY == 0 && this.pixelate <= 1 && !this.erase;
	}

	@Override
	public void fromNbt(NBTBase nbt) throws Exception
	{
		if (nbt instanceof NBTTagString)
		{
			this.path = RLUtils.create(nbt);

			return;
		}

		NBTTagCompound tag = (NBTTagCompound) nbt;

		this.path = RLUtils.create(tag.getString("Path"));

		if (tag.hasKey("Color"))
		{
			this.color = tag.getInteger("Color");
		}

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

		if (tag.hasKey("Pixelate"))
		{
			this.pixelate = tag.getInteger("Pixelate");
		}

		if (tag.hasKey("Erase"))
		{
			this.erase = tag.getBoolean("Erase");
		}
	}

	@Override
	public void fromJson(JsonElement element) throws Exception
	{
		if (element.isJsonPrimitive())
		{
			this.path = RLUtils.create(element);

			return;
		}

		JsonObject object = element.getAsJsonObject();

		this.path = RLUtils.create(object.get("path").getAsString());

		if (object.has("color"))
		{
			this.color = object.get("color").getAsInt();
		}

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

		if (object.has("pixelate"))
		{
			this.pixelate = object.get("pixelate").getAsInt();
		}

		if (object.has("erase"))
		{
			this.erase = object.get("erase").getAsBoolean();
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

		if (this.color != DEFAULT_COLOR) tag.setInteger("Color", this.color);
		if (this.scale != 1) tag.setFloat("Scale", this.scale);
		if (this.scaleToLargest) tag.setBoolean("ScaleToLargest", this.scaleToLargest);
		if (this.shiftX != 0) tag.setInteger("ShiftX", this.shiftX);
		if (this.shiftY != 0) tag.setInteger("ShiftY", this.shiftY);
		if (this.pixelate > 1) tag.setInteger("Pixelate", this.pixelate);
		if (this.erase) tag.setBoolean("Erase", this.erase);

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

		if (this.color != DEFAULT_COLOR) object.addProperty("color", this.color);
		if (this.scale != 1) object.addProperty("scale", this.scale);
		if (this.scaleToLargest) object.addProperty("scaleToLargest", this.scaleToLargest);
		if (this.shiftX != 0) object.addProperty("shiftX", this.shiftX);
		if (this.shiftY != 0) object.addProperty("shiftY", this.shiftY);
		if (this.pixelate > 1) object.addProperty("pixelate", this.pixelate);
		if (this.erase) object.addProperty("erase", this.erase);

		return object;
	}

	@Override
	public ResourceLocation clone()
	{
		return RLUtils.clone(this.path);
	}

	public FilteredResourceLocation copy()
	{
		return FilteredResourceLocation.from(this.writeNbt());
	}
}