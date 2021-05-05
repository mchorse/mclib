package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.DummyEntity;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Model renderer GUI element
 * 
 * This base class can be used for full screen model viewer. 
 */
public abstract class GuiModelRenderer extends GuiElement
{
    private static boolean rendering;
    private static Vector3d vec = new Vector3d();
    private static Matrix3d mat = new Matrix3d();

    protected EntityLivingBase entity;
    protected IBlockState block = Blocks.GRASS.getDefaultState();

    protected int timer;
    protected boolean dragging;
    protected boolean position;
    protected Vector3f temp = new Vector3f();

    public float fov = 70.0F;
    public float scale;
    public float yaw;
    public float pitch;
    public Vector3f pos = new Vector3f();

    protected float lastX;
    protected float lastY;

    /* Picking */
    protected boolean tryPicking;
    protected Consumer<String> callback;

    private long tick;

    public static boolean isRendering()
    {
        return rendering;
    }

    public GuiModelRenderer(Minecraft mc)
    {
        super(mc);

        this.entity = new DummyEntity(mc.world);
        this.entity.rotationYaw = this.entity.prevRotationYaw = 0.0F;
        this.entity.rotationPitch = this.entity.prevRotationPitch = 0.0F;
        this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0.0F;
        this.entity.renderYawOffset = this.entity.prevRenderYawOffset = 0.0F;
        this.entity.onGround = true;
        this.reset();
    }

    public GuiModelRenderer picker(Consumer<String> callback)
    {
        this.callback = callback;

        return this;
    }

    public void setRotation(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setPosition(float x, float y, float z)
    {
        this.pos.set(x, y, z);
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public EntityLivingBase getEntity()
    {
        return this.entity;
    }

    public void reset()
    {
        this.yaw = 0;
        this.pitch = 0;
        this.scale = 2;
        this.pos = new Vector3f(0, 1, 0);
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context) && (context.mouseButton == 0 || context.mouseButton == 2))
        {
            this.dragging = true;
            this.position = GuiScreen.isShiftKeyDown() || context.mouseButton == 2;
            this.lastX = context.mouseX;
            this.lastY = context.mouseY;

            if (GuiScreen.isCtrlKeyDown())
            {
                this.tryPicking = true;
                this.dragging = false;
            }
        }

        return this.area.isInside(context);
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (super.mouseScrolled(context))
        {
            return true;
        }

        if (this.area.isInside(context))
        {
            this.scale += Math.copySign(this.getZoomFactor(), context.mouseWheel);
            this.scale = MathUtils.clamp(this.scale, 0, 100);
        }

        return this.area.isInside(context);
    }

    protected float getZoomFactor()
    {
        if (this.scale < 1) return 0.05F;
        if (this.scale > 30) return 5F;
        if (this.scale > 10) return 1F;
        if (this.scale > 3) return 0.5F;

        return 0.1F;
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.dragging = false;
        this.tryPicking = false;

        super.mouseReleased(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        this.updateLogic(context);

        rendering = true;

        GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
        this.drawModel(context);
        GuiDraw.unscissor(context);

        rendering = false;

        super.draw(context);
    }

    private void updateLogic(GuiContext context)
    {
        long i = context.tick - this.tick;

        if (i > 10)
        {
            i = 10;
        }

        while (i > 0)
        {
            this.update();
            i --;
        }

        this.tick = context.tick;
    }

    /**
     * Update logic
     */
    protected void update()
    {
        this.timer = this.mc.player != null ? this.mc.player.ticksExisted : this.timer + 1;
        this.entity.ticksExisted = this.timer;
    }

    /**
     * Draw currently edited model
     */
    private void drawModel(GuiContext context)
    {
        this.setupViewport(context);
        this.setupPosition(context);

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
        GlStateManager.rotate(this.pitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(this.yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-this.temp.x, -this.temp.y, -this.temp.z);

        /* Drawing begins */
        this.drawGround();
        this.drawUserModel(context);

        GlStateManager.popMatrix();

        /* Disable rendering states */
        GlStateManager.enableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        /* Return back to orthographic projection */
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, context.screen.width, context.screen.height, 0.0D, 1000.0D, 3000000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    protected void setupPosition(GuiContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.dragging)
        {
            if (this.position)
            {
                float x = this.pos.x;
                float y = this.pos.y;
                float z = this.pos.z;

                double xx = -(this.lastX - mouseX) / 60F;
                double yy = -(this.lastY - mouseY) / 60F;
                float factor = this.getZoomFactor();

                xx *= factor / 0.1F;
                yy *= factor / 0.1F;

                if (xx != 0 || yy != 0)
                {
                    vec.set(xx, yy, 0);
                    this.rotateVector(vec);

                    x += vec.x;
                    y += vec.y;
                    z += vec.z;

                    this.pos.set(x, y, z);
                }
            }
            else
            {
                this.yaw -= this.lastX - mouseX;
                this.pitch -= this.lastY - mouseY;
            }

            this.lastX = mouseX;
            this.lastY = mouseY;
        }

        this.temp = new Vector3f(this.pos);

        vec.set(0, 0, -this.scale);
        this.rotateVector(vec);

        this.temp.x += vec.x;
        this.temp.y += vec.y;
        this.temp.z += vec.z;
    }

    private void rotateVector(Vector3d vec)
    {
        mat.rotX(this.pitch / 180 * (float) Math.PI);
        mat.transform(vec);
        mat.rotY((180 - this.yaw) / 180 * (float) Math.PI);
        mat.transform(vec);
    }

    protected void setupViewport(GuiContext context)
    {
        /* Changing projection mode to perspective. In order for this to
         * work, depth buffer must also be cleared. Thanks to Gegy for
         * pointing this out (depth buffer)! */
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);

        float rx = (float) Math.ceil(mc.displayWidth / (double) context.screen.width);
        float ry = (float) Math.ceil(mc.displayHeight / (double) context.screen.height);

        int vx = (int) (this.area.x * rx);
        int vy = (int) (this.mc.displayHeight - (this.area.y + this.area.h) * ry);
        int vw = (int) (this.area.w * rx);
        int vh = (int) (this.area.h * ry);

        GlStateManager.viewport(vx, vy, vw, vh);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        Project.gluPerspective(this.fov, (float) vw / (float) vh, 0.05F, 1000);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    /**
     * Draw your model here 
     */
    protected abstract void drawUserModel(GuiContext context);

    /**
     * IMPORTANT: this method should be called manually by the subclass right
     * after rendering the model
     */
    protected void tryPicking(GuiContext context)
    {
        if (!this.tryPicking)
        {
            return;
        }

        float rx = (float) Math.ceil(mc.displayWidth / (double) context.screen.width);
        float ry = (float) Math.ceil(mc.displayHeight / (double) context.screen.height);

        int x = (int) (context.mouseX * rx);
        int y = (int) (this.mc.displayHeight - (context.mouseY) * ry);

        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        GL11.glColorMask(false, false, false, false);
        this.drawForStencil(context);
        GL11.glColorMask(true, true, true, true);

        ByteBuffer buffer = ByteBuffer.allocateDirect(1);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_STENCIL_INDEX, GL11.GL_UNSIGNED_BYTE, buffer);

        buffer.rewind();

        if (this.callback != null)
        {
            int value = buffer.get();

            if (value > 0)
            {
                this.callback.accept(this.getStencilValue(value));
            }
        }

        this.tryPicking = false;
    }

    /**
     * Here you should draw your own things into stencil
     */
    protected void drawForStencil(GuiContext context)
    {}

    protected String getStencilValue(int value)
    {
        return null;
    }

    /**
     * Render block of grass under the model (which signify where 
     * located the ground below the model) 
     */
    protected void drawGround()
    {
        if (McLib.enableGridRendering.get())
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            GL11.glLineWidth(3);
            GlStateManager.disableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            for (int x = 0; x <= 10; x ++)
            {
                if (x == 0)
                {
                    buffer.pos(x - 5, 0, -5).color(0F, 0F, 1F, 0.75F).endVertex();
                    buffer.pos(x - 5, 0, 5).color(0F, 0F, 1F, 0.75F).endVertex();
                }
                else
                {
                    buffer.pos(x - 5, 0, -5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                    buffer.pos(x - 5, 0, 5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                }
            }

            for (int x = 0; x <= 10; x ++)
            {
                if (x == 10)
                {
                    buffer.pos(-5, 0, x - 5).color(1F, 0F, 0F, 0.75F).endVertex();
                    buffer.pos(5, 0, x - 5).color(1F, 0F, 0F, 0.75F).endVertex();
                }
                else
                {
                    buffer.pos(-5, 0, x - 5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                    buffer.pos(5, 0, x - 5).color(0.25F, 0.25F, 0.25F, 0.75F).endVertex();
                }
            }

            tessellator.draw();

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
        else
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
}