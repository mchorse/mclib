package mchorse.mclib;

import mchorse.mclib.client.KeyboardHandler;
import mchorse.mclib.client.InputRenderer;
import mchorse.mclib.client.gui.utils.keys.LangKey;
import mchorse.mclib.utils.ReflectionUtils;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new InputRenderer());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        Minecraft mc = Minecraft.getMinecraft();

        /* OMG, thank you very much Forge! */
        if (!mc.getFramebuffer().isStencilEnabled())
        {
            mc.getFramebuffer().enableStencil();
        }

        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener((manager) ->
        {
            LangKey.lastTime = System.currentTimeMillis();

            if (McLib.multiskinClear.get())
            {
                mc.addScheduledTask(this::clearMultiTextures);
            }
        });
    }

    private void clearMultiTextures()
    {
        Minecraft mc = Minecraft.getMinecraft();
        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(mc.renderEngine);
        List<ResourceLocation> toClear = new ArrayList<ResourceLocation>();

        for (ResourceLocation location : map.keySet())
        {
            if (location instanceof MultiResourceLocation)
            {
                toClear.add(location);
            }
        }

        for (ResourceLocation location : toClear)
        {
            mc.renderEngine.deleteTexture(location);
        }
    }
}