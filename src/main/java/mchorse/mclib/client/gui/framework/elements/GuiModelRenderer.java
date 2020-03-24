package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import mchorse.mclib.utils.DummyEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;

/**
 * Model renderer GUI element
 * 
 * This base class can be used for full screen model viewer. 
 */
public abstract class GuiModelRenderer extends GuiElement
{
    public DummyEntity dummy;
    protected IBlockState block = Blocks.GRASS.getDefaultState();

    protected float scale;
    protected int timer;

    protected boolean dragging;
    protected boolean position;
    protected float yaw;
    protected float pitch;
    protected float x;
    protected float y;

    protected float lastX;
    protected float lastY;

    public GuiModelRenderer(Minecraft mc)
    {
        super(mc);

        this.dummy = new DummyEntity(mc.theWorld);
    }

    public void reset()
    {
        this.yaw = 0;
        this.pitch = 0;
        this.x = 0;
        this.y = 0;
        this.scale = 0;
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        this.dragging = true;
        this.lastX = context.mouseX;
        this.lastY = context.mouseY;

        this.position = GuiScreen.isShiftKeyDown() || context.mouseButton == 2;

        return false;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (super.mouseScrolled(context))
        {
            return true;
        }

        this.scale += Math.copySign(0.25F, context.mouseWheel);
        this.scale = MathHelper.clamp_float(this.scale, -1.5F, 30);

        return false;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.dragging)
        {
            if (this.position)
            {
                this.x -= (this.lastX - mouseX) / 60;
                this.y += (this.lastY - mouseY) / 60;
            }
            else
            {
                this.yaw -= this.lastX - mouseX;
                this.pitch += this.lastY - mouseY;
            }
        }

        this.dragging = false;
        this.position = false;

        super.mouseReleased(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.update();
        this.drawModel(context.mouseX, context.mouseY, context.partialTicks);
        super.draw(context);
    }

    /**
     * Update logic
     */
    protected void update()
    {
        this.timer = this.mc.thePlayer != null ? this.mc.thePlayer.ticksExisted : this.timer + 1;
        this.dummy.ticksExisted = this.timer;
    }

    /**
     * Draw currently edited model
     */
    private void drawModel(int mouseX, int mouseY, float partialTicks)
    {
        /* Changing projection mode to perspective. In order for this to 
         * work, depth buffer must also be cleared. Thanks to Gegy for 
         * pointing this out (depth buffer)! */
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        Project.gluPerspective(70, (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, 1000);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        float newYaw = this.yaw;
        float newPitch = this.pitch;

        float x = this.x;
        float y = this.y;

        if (this.dragging)
        {
            if (this.position)
            {
                x -= (this.lastX - mouseX) / 60;
                y += (this.lastY - mouseY) / 60;
            }
            else
            {
                newYaw -= this.lastX - mouseX;
                newPitch += this.lastY - mouseY;
            }
        }

        /* Enable rendering states */
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        /* Setup transformations */
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.translate(0 + x, -1 + y, -2 - this.scale);
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F + newYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(newPitch, 1.0F, 0.0F, 0.0F);

        /* Drawing begins */
        this.drawGround();
        this.drawModel(newYaw, newPitch, mouseX, mouseY, partialTicks);

        /* Disable rendering states */
        GlStateManager.popMatrix();

        GlStateManager.enableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        /* Return back to orthographic projection */
        GuiScreen screen = this.mc.currentScreen;

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, screen.width, screen.height, 0.0D, 1000.0D, 3000000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    /**
     * Draw your model here 
     */
    protected abstract void drawModel(float headYaw, float headPitch, int mouseX, int mouseY, float partial);

    /**
     * Render block of grass under the model (which signify where 
     * located the ground below the model) 
     */
    protected void drawGround()
    {
        BlockRendererDispatcher renderer = this.mc.getBlockRendererDispatcher();

        this.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -0.5F, 0);
        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, 0.5F);
        renderer.renderBlockBrightness(this.block, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}