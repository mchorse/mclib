package mchorse.mclib.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import mchorse.mclib.utils.files.GlobalTree;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReflectionUtils
{
    /**
     * Minecraft texture manager's field to the texture map (a map of 
     * {@link ITextureObject} which is used to cache references to 
     * OpenGL textures). 
     */
    public static Field TEXTURE_MAP;

    /**
     * Get texture map from texture manager using reflection API
     */
    @SuppressWarnings("unchecked")
    public static Map<ResourceLocation, ITextureObject> getTextures(TextureManager manager)
    {
        if (TEXTURE_MAP == null)
        {
            setupTextureMapField(manager);
        }

        try
        {
            return (Map<ResourceLocation, ITextureObject>) TEXTURE_MAP.get(manager);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Setup texture map field which is looked up using the reflection API
     */
    @SuppressWarnings("rawtypes")
    public static void setupTextureMapField(TextureManager manager)
    {
        /* Finding the field which has holds the texture cache */
        for (Field field : manager.getClass().getDeclaredFields())
        {
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }

            field.setAccessible(true);

            try
            {
                Object value = field.get(manager);

                if (value instanceof Map && ((Map) value).keySet().iterator().next() instanceof ResourceLocation)
                {
                    TEXTURE_MAP = field;

                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean registerResourcePack(IResourcePack pack)
    {
        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(FMLClientHandler.instance());
            packs.add(pack);
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();

            if (manager instanceof SimpleReloadableResourceManager)
            {
                ((SimpleReloadableResourceManager) manager).reloadResourcePack(pack);
            }

            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}