package mchorse.mclib.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;

import java.net.URI;
import java.net.URL;

/**
 * GUI utilities
 */
public class GuiUtils
{
    public static void scissor(int x, int y, int w, int h, GuiContext context)
    {
        scissor(x, y, w, h, context.screen.width, context.screen.height);
    }

    /**
     * Scissor (clip) the screen 
     */
    public static void scissor(int x, int y, int w, int h, int sw, int sh)
    {
        Minecraft mc = Minecraft.getMinecraft();

        /* F*$! those ints */
        float rx = (float) Math.ceil((double) mc.displayWidth / (double) sw);
        float ry = (float) Math.ceil((double) mc.displayHeight / (double) sh);

        /* Clipping area around scroll area */
        int xx = (int) (x * rx);
        int yy = (int) (mc.displayHeight - (y + h) * ry);
        int ww = (int) (w * rx);
        int hh = (int) (h * ry);

        GL11.glScissor(xx, yy, ww, hh);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void unscissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void drawModel(ModelBase model, EntityPlayer player, int x, int y, float scale)
    {
        drawModel(model, player, x, y, scale, 1.0F);
    }

    /**
     * Draw a {@link ModelBase} without using the {@link RenderManager} (which 
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    public static void drawModel(ModelBase model, EntityPlayer player, int x, int y, float scale, float alpha)
    {
        float factor = 0.0625F;

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        GlStateManager.enableAlpha();

        model.setLivingAnimations(player, 0, 0, 0);
        model.setRotationAngles(0, 0, player.ticksExisted, 0, 0, factor, player);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        model.render(player, 0, 0, 0, 0, 0, factor);

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     * @param alpha 
     */
    public static void drawEntityOnScreen(int posX, int posY, float scale, EntityLivingBase ent, float alpha)
    {
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 100.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        boolean render = ent.getAlwaysRenderNameTag();

        if (ent instanceof EntityDragon)
        {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }

        RenderHelper.enableStandardItemLighting();

        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        ent.renderYawOffset = 0;
        ent.rotationYaw = 0;
        ent.rotationPitch = 0;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        ent.setAlwaysRenderNameTag(false);

        GlStateManager.translate(0.0F, 0.0F, 0.0F);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        ent.setAlwaysRenderNameTag(render);

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();

        GlStateManager.disableBlend();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableDepth();
    }

    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, int mouseX, int mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 100.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;

        GlStateManager.translate(0.0F, 0.0F, 0.0F);

        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Draws a rectangle with a horizontal gradient between with specified
     * colors, the code is borrowed form drawGradient()
     */
    public static void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, float zLevel)
    {
        float a1 = (startColor >> 24 & 255) / 255.0F;
        float r1 = (startColor >> 16 & 255) / 255.0F;
        float g1 = (startColor >> 8 & 255) / 255.0F;
        float b1 = (startColor & 255) / 255.0F;
        float a2 = (endColor >> 24 & 255) / 255.0F;
        float r2 = (endColor >> 16 & 255) / 255.0F;
        float g2 = (endColor >> 8 & 255) / 255.0F;
        float b2 = (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos(right, top, zLevel).color(r2, g2, b2, a2).endVertex();
        vertexbuffer.pos(left, top, zLevel).color(r1, g1, b1, a1).endVertex();
        vertexbuffer.pos(left, bottom, zLevel).color(r1, g1, b1, a1).endVertex();
        vertexbuffer.pos(right, bottom, zLevel).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH)
    {
        drawBillboard(x, y, u, v, w, h, textureW, textureH, 0);
    }

    public static void drawBillboard(int x, int y, int u, int v, int w, int h, int textureW, int textureH, float z)
    {
        float tw = 1F / textureW;
        float th = 1F / textureH;
        
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x, y + h, z).tex(u * tw, (v + h) * th).endVertex();
        vertexbuffer.pos(x + w, y + h, z).tex((u + w) * tw, (v + h) * th).endVertex();
        vertexbuffer.pos(x + w, y, z).tex((u + w) * tw, v * th).endVertex();
        vertexbuffer.pos(x, y, z).tex(u * tw, v * th).endVertex();

        tessellator.draw();
    }

    /**
     * Open web link
     */
    public static void openWebLink(String address)
    {
        try
        {
            openWebLink(new URI(address));
        }
        catch (Exception e)
        {}
    }

    /**
     * Open a URL
     */
    public static void openWebLink(URI uri)
    {
        try
        {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null);

            clazz.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (Throwable t)
        {}
    }
}