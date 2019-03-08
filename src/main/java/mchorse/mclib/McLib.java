package mchorse.mclib;

import mchorse.mclib.utils.files.GlobalTree;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * McLib mod
 * 
 * All it does is provides common code for McHorse's mods.
 */
@Mod.EventBusSubscriber
@Mod(modid = McLib.MOD_ID, name = "McLib", version = McLib.VERSION)
public class McLib
{
    public static final String MOD_ID = "mclib";
    public static final String VERSION = "%VERSION%";

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onGuiOpen(GuiOpenEvent event)
    {
        GlobalTree.TREE.needsRebuild();
        GlobalTree.TREE.rebuild();
    }
}