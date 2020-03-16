package mchorse.mclib.client.gui.framework;

import java.io.IOException;

import mchorse.mclib.client.gui.framework.elements.GuiContext;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for GUI screens using this framework
 */
@SideOnly(Side.CLIENT)
public class GuiBase extends GuiScreen
{
    /**
     * Icons texture used across all dashboard panels 
     */
    public static final ResourceLocation ICONS = new ResourceLocation(McLib.MOD_ID, "textures/gui/icons.png");

    public GuiElements<IGuiElement> elements = new GuiElements<IGuiElement>();
    public GuiContext context = new GuiContext(this);
    public Area area = new Area();

    @Override
    public void initGui()
    {
        this.area.set(0, 0, this.width, this.height);
        this.elements.resize();
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (!this.elements.handleMouseInput(x, y))
        {
            super.handleMouseInput();
        }

        int scroll = -Mouse.getEventDWheel();

        if (scroll == 0)
        {
            return;
        }

        this.mouseScrolled(x, y, scroll);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.context.setMouse(mouseX, mouseY, mouseButton);

        if (this.elements.isEnabled())
        {
            this.elements.mouseClicked(this.context);
        }
    }

    protected void mouseScrolled(int x, int y, int scroll)
    {
        this.context.setMouseWheel(x, y, scroll);

        if (this.elements.isEnabled())
        {
            this.elements.mouseScrolled(this.context);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.context.setMouse(mouseX, mouseY, state);

        if (this.elements.isEnabled())
        {
            this.elements.mouseReleased(this.context);
        }
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        if (!this.elements.handleKeyboardInput())
        {
            super.handleKeyboardInput();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.elements.isEnabled() && this.elements.keyTyped(this.context))
        {
            return;
        }

        this.keyPressed(typedChar, keyCode);

        if (keyCode == 1)
        {
            this.closeScreen();
        }
    }

    /**
     * This method is getting called when there are no active text 
     * fields in the GUI (this can be used for handling shortcuts)
     */
    public void keyPressed(char typedChar, int keyCode)
    {}

    /**
     * This method is called when this screen is about to get closed
     */
    protected void closeScreen()
    {
        this.mc.displayGuiScreen((GuiScreen) null);

        if (this.mc.currentScreen == null)
        {
            this.mc.setIngameFocus();
        }

        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.context.setMouse(mouseX, mouseY);
        this.context.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        if (this.elements.isVisible())
        {
            this.context.tooltip.set(null, null);
            this.elements.draw(this.context);
            this.context.tooltip.draw(this.fontRendererObj, this.width, this.height);
        }
    }
}