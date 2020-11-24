package mchorse.mclib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Render brightness
 * 
 * This class is a workaround class which allows using lightmap methods 
 * without having to resort to straight copy-pasting the code.
 */
@SideOnly(Side.CLIENT)
public class RenderLightmap extends RenderLivingBase<EntityLivingBase>
{
    /**
     * Private instance 
     */
    private static RenderLightmap instance;

    public static RenderLightmap getInstance()
    {
        if (instance == null)
        {
            instance = new RenderLightmap(Minecraft.getMinecraft().getRenderManager(), null, 0);
        }

        return instance;
    }

    public static boolean canRenderNamePlate(EntityLivingBase entity)
    {
        return getInstance().canRenderName(entity);
    }

    public static boolean set(EntityLivingBase entity, float partialTicks)
    {
        return getInstance().setBrightness(entity, partialTicks, true);
    }

    public static void unset()
    {
        getInstance().unsetBrightness();
    }

    public RenderLightmap(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        return 0;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLivingBase entity)
    {
        return null;
    }
}