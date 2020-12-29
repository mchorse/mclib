package mchorse.mclib.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Cancelable
@SideOnly(Side.CLIENT)
public class PreRenderOverlayEvent extends Event
{
	public final Minecraft mc;
	public final ScaledResolution resolution;

	public PreRenderOverlayEvent(Minecraft mc, ScaledResolution resolution)
	{
		this.mc = mc;
		this.resolution = resolution;
	}
}