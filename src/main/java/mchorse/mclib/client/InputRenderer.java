package mchorse.mclib.client;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.events.RenderOverlayEvent;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.Keys;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Mouse renderer
 * 
 * This class is responsible for rendering a mouse pointer on the screen 
 */
@SideOnly(Side.CLIENT)
public class InputRenderer
{
    public static boolean disabledForFrame = false;

    private List<PressedKey> pressedKeys = new ArrayList<PressedKey>();
    private float lastQX = 1;
    private float lastQY = 0;
    private float currentQX = 0;
    private float currentQY = 1;
    private long lastDWheelTime;
    private int lastDWheelScroll;

    public static void disable()
    {
        disabledForFrame = true;
    }

    /**
     * Called by ASM
     */
    public static void preRenderOverlay()
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);

        setupOrthoProjection(resolution);

        McLib.EVENT_BUS.post(new RenderOverlayEvent.Pre(mc, resolution));
    }

    /**
     * Called by ASM
     */
    public static void postRenderOverlay()
    {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);

        setupOrthoProjection(resolution);

        McLib.EVENT_BUS.post(new RenderOverlayEvent.Post(mc, resolution));
    }

    private static void setupOrthoProjection(ScaledResolution resolution)
    {
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0, 1000D, 3000D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0, 0, -2000F);
    }

    @SubscribeEvent
    public void onDrawEvent(DrawScreenEvent.Post event)
    {
        if (disabledForFrame)
        {
            disabledForFrame = false;

            return;
        }

        int x = event.getMouseX();
        int y = event.getMouseY();

        this.renderMouse(x, y);

        if (McLib.enableKeystrokeRendering.get())
        {
            this.renderKeys(event.getGui(), x, y);
        }
    }

    /**
     * Draw mouse cursor
     */
    private void renderMouse(int x, int y)
    {
        GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1000);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();

        if (McLib.enableCursorRendering.get())
        {
            Icons.CURSOR.render(x, y);
        }

        if (McLib.enableMouseButtonRendering.get())
        {
            boolean left = Mouse.isButtonDown(0);
            boolean right = Mouse.isButtonDown(1);
            boolean middle = Mouse.isButtonDown(2);

            int scroll = Mouse.getDWheel();
            long current = System.currentTimeMillis();
            boolean isScrolling = scroll != 0 || current - this.lastDWheelTime < 500;

            if (scroll != 0)
            {
                this.lastDWheelTime = current;
                this.lastDWheelScroll = scroll;
            }

            if (scroll == 0 && isScrolling)
            {
                scroll = this.lastDWheelScroll;
            }

            x += 16;
            y += 2;

            if (left || right || middle || isScrolling)
            {
                /* Outline */
                Gui.drawRect(x - 1, y, x + 13, y + 16, 0xff000000);
                Gui.drawRect(x, y - 1, x + 12, y + 17, 0xff000000);
                /* Background */
                Gui.drawRect(x, y + 1, x + 12, y + 15, 0xffffffff);
                Gui.drawRect(x + 1, y, x + 11, y + 1, 0xffffffff);
                Gui.drawRect(x + 1, y + 15, x + 11, y + 16, 0xffffffff);
                /* Over outline */
                Gui.drawRect(x, y + 7, x + 12, y + 8, 0xffeeeeee);

                if (left)
                {
                    Gui.drawRect(x + 1, y, x + 6, y + 7, 0xffcccccc);
                    Gui.drawRect(x, y + 1, x + 1, y + 7, 0xffaaaaaa);
                }

                if (right)
                {
                    Gui.drawRect(x + 6, y, x + 11, y + 7, 0xffaaaaaa);
                    Gui.drawRect(x + 11, y + 1, x + 12, y + 7, 0xff888888);
                }

                if (middle || isScrolling)
                {
                    int offset = 0;

                    if (isScrolling)
                    {
                        offset = scroll < 0 ? 1 : -1;
                    }

                    Gui.drawRect(x + 4, y, x + 8, y + 6, 0x20000000);
                    Gui.drawRect(x + 5, y + 1 + offset, x + 7, y + 5 + offset, 0xff444444);
                    Gui.drawRect(x + 5, y + 4 + offset, x + 7, y + 5 + offset, 0xff333333);
                }
            }

            if (isScrolling)
            {
                x += 16;

                int color = McLib.primaryColor.get();

                GuiDraw.drawDropShadow(x, y, x + 4, y + 16, 2, 0x88000000 + color, color);
                Gui.drawRect(x, y, x + 4, y + 16, 0xff111111);
                Gui.drawRect(x + 1, y, x + 3, y + 15, 0xff2a2a2a);

                int offset = (int) ((current % 1000 / 50) % 4);

                if (scroll >= 0)
                {
                    offset = 3 - offset;
                }

                for (int i = 0; i < 4; i++)
                {
                    Gui.drawRect(x, y + offset, x + 4, y + offset + 1, 0x88555555);

                    y += 4;
                }
            }
        }

        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    /**
     * Render pressed key strokes
     */
    private void renderKeys(GuiScreen screen, int mouseX, int mouseY)
    {
        float lqx = Math.round(mouseX / (float) screen.width);
        float lqy = Math.round(mouseY / (float) screen.height);
        int mode = McLib.keystrokeMode.get();

        if (lqx == this.currentQX && lqy == this.currentQY)
        {
            this.currentQX = this.lastQX;
            this.currentQY = this.lastQY;
        }

        if (mode == 1)
        {
            this.currentQX = 0;
            this.currentQY = 1;
        }
        else if (mode == 2)
        {
            this.currentQX = 1;
            this.currentQY = 1;
        }
        else if (mode == 3)
        {
            this.currentQX = 1;
            this.currentQY = 0;
        }
        else if (mode == 4)
        {
            this.currentQX = 0;
            this.currentQY = 0;
        }

        float qx = this.currentQX;
        float qy = this.currentQY;

        int fy = qy > 0.5F ? 1 : -1;
        int offset = McLib.keystrokeOffset.get();
        int mx = offset + (int) (qx * (screen.width - offset * 2));
        int my = offset + (int) (qy * (screen.height - 20 - offset * 2));

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        Iterator<PressedKey> it = this.pressedKeys.iterator();

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        while (it.hasNext())
        {
            PressedKey key = it.next();

            if (key.expired())
            {
                it.remove();
            }
            else
            {
                int x = mx + (qx < 0.5F ? key.x : -(key.x + key.width + 10));
                int y = my + (int) (Interpolation.EXP_INOUT.interpolate(0, 1, key.getFactor()) * 50 * fy) + (key.i % 2 == 0 ? -1 : 0);

                GuiDraw.drawDropShadow(x, y, x + 10 + key.width, y + 20, 4, 0x44000000, 0);
                Gui.drawRect(x, y, x + 10 + key.width, y + 20, 0xff000000 + McLib.primaryColor.get());
                font.drawStringWithShadow(key.getLabel(), x + 5, y + 6, 0xffffff);
            }
        }

        this.lastQX = lqx;
        this.lastQY = lqy;
    }

    @SubscribeEvent
    public void onKeyPressedInGUI(GuiScreenEvent.KeyboardInputEvent.Post event)
    {
        boolean inputFocused = GuiBase.getCurrent() == null || GuiBase.getCurrent().activeElement == null;

        if (Keyboard.getEventKeyState() && inputFocused)
        {
            int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            if (key >= 256 || key < 0)
            {
                return;
            }

            PressedKey last = null;
            int offset = -1000;

            for (PressedKey pressed : this.pressedKeys)
            {
                if (pressed.key == key)
                {
                    offset = pressed.increment();
                }
                else if (offset != -1000)
                {
                    pressed.x += offset;
                }

                last = pressed;
            }

            if (offset != -1000)
            {
                return;
            }

            offset = McLib.keystrokeOffset.get();
            int x = last == null ? 0 : last.x + last.width + 5;
            PressedKey newKey = new PressedKey(key, x);

            if (newKey.x + newKey.width + offset > event.getGui().width - offset * 2)
            {
                newKey.x = 0;
            }

            this.pressedKeys.add(newKey);
        }
    }

    /**
     * Release the matrix at the end of frame to avoid messing
     * up matrix capture even more
     */
    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event)
    {
        MatrixUtils.releaseMatrix();
    }

    /**
     * Information about pressed key strokes
     */
    @SideOnly(Side.CLIENT)
    public static class PressedKey
    {
        public static int INDEX = 0;

        public int key;
        public long time;
        public int x;

        public String name;
        public int width;
        public int i;
        public int times = 1;

        public PressedKey(int key, int x)
        {
            this.key = key;
            this.time = System.currentTimeMillis();
            this.x = x;

            this.name = Keys.getKeyName(key);
            this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.name);
            this.i = INDEX ++;
        }

        public float getFactor()
        {
            return (System.currentTimeMillis() - this.time - 500) / 1000F;
        }

        public boolean expired()
        {
            if (Keyboard.isKeyDown(this.key))
            {
                this.time = System.currentTimeMillis();
            }

            return System.currentTimeMillis() - this.time > 1500;
        }

        public String getLabel()
        {
            if (this.times > 1)
            {
                return this.name + " (" + this.times + ")";
            }

            return this.name;
        }

        public int increment()
        {
            int lastWidth = this.width;

            this.times ++;
            this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(this.getLabel());

            return this.width - lastWidth;
        }
    }
}