package mchorse.mclib;

import java.util.Map;

import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import mchorse.mclib.utils.files.GlobalTree;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * McLib mod
 * 
 * All it does is provides common code for McHorse's mods.
 */
@Mod.EventBusSubscriber
@Mod(modid = McLib.MOD_ID, name = "McLib", version = McLib.VERSION, updateJSON = "https://raw.githubusercontent.com/mchorse/mclib/master/version.json")
public class McLib
{
    public static final String MOD_ID = "mclib";
    public static final String VERSION = "%VERSION%";

    @NetworkCheckHandler
    public boolean checkModDependencies(Map<String, String> map, Side side)
    {
        return true;
    }

    public static void main(String[] args)
    {
        MathBuilder builder = new MathBuilder();
        builder.register(new Variable("hacks", 11));

        try {
            IValue value = builder.parse("!sqrt(4) - trunc(-1.75)");

            int i = 10 > 5 ? 16 < 5 ? 15 : 6 : 50 == 5 ? 5 : 7;

            System.out.println(value.get());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}