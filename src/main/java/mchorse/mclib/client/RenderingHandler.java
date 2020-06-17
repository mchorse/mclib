package mchorse.mclib.client;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.Keys;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Mouse renderer
 * 
 * This class is responsible for rendering a mouse pointer on the screen 
 */
@SideOnly(Side.CLIENT)
public class RenderingHandler
{
    public static final ResourceLocation MOUSE_POINTER = new ResourceLocation("mclib", "textures/gui/mouse.png");
    public static boolean disabledForFrame = false;

    private List<PressedKey> pressedKeys = new ArrayList<PressedKey>();
    private float lastQX = 1;
    private float lastQY = 0;
    private float currentQX = 0;
    private float currentQY = 1;

    public static void disable()
    {
        disabledForFrame = true;
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

        if (McLib.enableMouseRendering.get())
        {
            this.renderMouse(x, y);
        }

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
        Minecraft.getMinecraft().renderEngine.bindTexture(MOUSE_POINTER);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);

        boolean left = Mouse.isButtonDown(0);
        boolean right = Mouse.isButtonDown(1);
        boolean middle = Mouse.isButtonDown(2);

        if (left || right || middle)
        {
            x += 16;
            y += 2;

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

            if (middle)
            {
                Gui.drawRect(x + 5, y + 1, x + 7, y + 5, 0xff444444);
                Gui.drawRect(x + 5, y + 4, x + 7, y + 5, 0xff333333);
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

        if (lqx == this.currentQX && lqy == this.currentQY)
        {
            this.currentQX = this.lastQX;
            this.currentQY = this.lastQY;
        }

        float qx = this.currentQX;
        float qy = this.currentQY;

        int fy = qy > 0.5F ? 1 : -1;
        int mx = 10 + (int) (qx * (screen.width - 30));
        int my = 10 + (int) (qy * (screen.height - 40));

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        Iterator<PressedKey> it = this.pressedKeys.iterator();

        while (it.hasNext())
        {
            PressedKey key = it.next();

            if (key.expired())
            {
                it.remove();
            }
            else
            {
                int x = mx + (qx < 0.5F ? key.x : -(key.x + key.width));
                int y = my + (int) (Interpolation.EXP_INOUT.interpolate(0, 1, key.getFactor()) * 50 * fy) + (key.i % 2 == 0 ? -1 : 0);

                GuiDraw.drawDropShadow(x, y, x + 10 + key.width, y + 20, 4, 0x44000000, 0);
                Gui.drawRect(x, y, x + 10 + key.width, y + 20, 0xff000000 + McLib.primaryColor.get());
                font.drawStringWithShadow(key.name, x + 5, y + 6, 0xffffff);
            }
        }

        this.lastQX = lqx;
        this.lastQY = lqy;
    }

    @SubscribeEvent
    public void onKeyPressedInGUI(GuiScreenEvent.KeyboardInputEvent.Post event)
    {
        if (Keyboard.getEventKeyState() && GuiBase.getCurrent().activeElement == null)
        {
            int key = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            if (key >= 256 || key < 0)
            {
                return;
            }

            PressedKey last = null;

            for (PressedKey pressed : this.pressedKeys)
            {
                if (pressed.key == key)
                {
                    return;
                }

                last = pressed;
            }

            int x = last == null ? 0 : last.x + last.width + 5;
            PressedKey newKey = new PressedKey(key, x);

            if (newKey.x + newKey.width + 10 > event.getGui().width - 20)
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
        public int i = 0;

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
    }
}