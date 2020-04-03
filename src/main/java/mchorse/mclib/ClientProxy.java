package mchorse.mclib;

import mchorse.mclib.client.KeyboardHandler;
import mchorse.mclib.client.MouseRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);

		MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
		MinecraftForge.EVENT_BUS.register(new MouseRenderer());
	}
}