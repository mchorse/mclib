package mchorse.mclib.utils.resources;

import mchorse.mclib.utils.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Stack;

public class MultiskinThread implements Runnable
{
	private static MultiskinThread instance;

	public Stack<MultiResourceLocation> locations = new Stack<MultiResourceLocation>();

	public static synchronized void add(MultiResourceLocation location)
	{
		if (instance != null && instance.locations.isEmpty())
		{
			instance = null;
		}

		if (instance == null)
		{
			instance = new MultiskinThread();
			instance.addLocation(location);
			new Thread(instance).start();
		}
		else
		{
			instance.addLocation(location);
		}
	}

	public void addLocation(MultiResourceLocation location)
	{
		this.locations.add(location);
	}

	@Override
	public void run()
	{
		while (!this.locations.isEmpty())
		{
			try
			{
				Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(Minecraft.getMinecraft().renderEngine);
				MultiResourceLocation location = this.locations.pop();
				ITextureObject texture = map.get(location);

				if (texture != null)
				{
					Minecraft.getMinecraft().addScheduledTask(() ->
					{
						TextureUtil.uploadTextureImageAllocate(texture.getGlTextureId(), TextureProcessor.process(location), false, false);
					});
				}
				else
				{
					this.locations.add(location);
				}

				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		instance = null;
	}
}