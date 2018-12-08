package mchorse.mclib.client.gui.framework;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base class for GUI screens using this framework
 */
@SideOnly(Side.CLIENT)
public class GuiBase extends GuiScreen
{
    public GuiElements<IGuiElement> elements = new GuiElements<IGuiElement>();
    public GuiTooltip tooltip = new GuiTooltip();
    public Area area = new Area();

    @Override
    public void initGui()
    {
        this.area.set(0, 0, this.width, this.height);
        this.elements.resize(this.width, this.height);
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
        if (this.elements.isEnabled())
        {
            this.elements.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected void mouseScrolled(int x, int y, int scroll)
    {
        if (this.elements.isEnabled())
        {
            this.elements.mouseScrolled(x, y, scroll);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.elements.isEnabled())
        {
            this.elements.mouseReleased(mouseX, mouseY, state);
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
        if (this.elements.isEnabled())
        {
            this.elements.keyTyped(typedChar, keyCode);
        }

        if (!this.elements.hasActiveTextfields())
        {
            this.keyPressed(typedChar, keyCode);
        }

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
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.elements.isVisible())
        {
            this.tooltip.set(null, null);
            this.elements.draw(this.tooltip, mouseX, mouseY, partialTicks);
            this.tooltip.draw(this.fontRenderer, this.width, this.height);
        }
    }
}