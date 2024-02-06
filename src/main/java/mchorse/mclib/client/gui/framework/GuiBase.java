package mchorse.mclib.client.gui.framework;

import mchorse.mclib.client.gui.framework.elements.IViewport;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.IViewportStack;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

/**
 * Base class for GUI screens using this framework
 */
@SideOnly(Side.CLIENT)
public class GuiBase extends GuiScreen
{
    private static GuiContext current;

    public GuiElement root;
    public GuiContext context = new GuiContext(this);
    public Area viewport = new Area();

    public static GuiContext getCurrent()
    {
        return current;
    }

    public GuiBase()
    {
        current = this.context;

        this.context.mc = Minecraft.getMinecraft();
        this.context.font = this.context.mc.fontRenderer;

        this.root = new GuiRootElement(this.context.mc);
        this.root.markContainer().flex().relative(this.viewport).wh(1F, 1F);
        this.root.keys().register(IKey.lang("mclib.gui.keys.list"), Keyboard.KEY_F9, () -> this.context.keybinds.toggleVisible());

        this.context.keybinds.flex().relative(this.viewport).wh(0.5F, 1F);

        Keyboard.enableRepeatEvents(false);
    }

    /**
     *
     * @param clazz the class to search for in the children of this screen.root
     * @param <T>
     * @return null if GuiBase.screen or GuiBase.screen.root is null or if the children List is empty.
     */
    public static <T> List<T> getCurrentChildren(Class<T> clazz)
    {
        if (GuiBase.getCurrent() != null && GuiBase.getCurrent().screen != null && GuiBase.getCurrent().screen.root != null)
        {
            List<T> childList = GuiBase.getCurrent().screen.root.getChildren(clazz);

            return (childList.isEmpty()) ? null : childList;
        }

        return null;
    }

    @Override
    public void updateScreen()
    {
        this.context.tick += 1;
    }

    @Override
    public void initGui()
    {
        current = this.context;

        if (!this.context.keybinds.hasParent())
        {
            this.root.add(this.context.keybinds);
        }

        this.viewport.set(0, 0, this.width, this.height);
        this.viewportSet();

        this.context.pushViewport(this.viewport);
        this.root.resize();
        this.context.popViewport();
    }

    protected void viewportSet()
    {}

    @Override
    public void onGuiClosed()
    {
        current = null;
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        super.handleMouseInput();

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

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            this.root.mouseClicked(this.context);
            this.context.popViewport();
        }
    }

    protected void mouseScrolled(int x, int y, int scroll)
    {
        this.context.setMouseWheel(x, y, scroll);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            this.root.mouseScrolled(this.context);
            this.context.popViewport();
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.context.setMouse(mouseX, mouseY, state);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            this.root.mouseReleased(this.context);
            this.context.popViewport();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.context.setKey(typedChar, keyCode);

        if (this.root.isEnabled() && this.root.keyTyped(this.context))
        {
            return;
        }

        this.context.pushViewport(this.viewport);
        this.keyPressed(typedChar, keyCode);
        this.context.popViewport();


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
        this.mc.displayGuiScreen(null);

        if (this.mc.currentScreen == null)
        {
            this.mc.setIngameFocus();
        }

        Keyboard.enableRepeatEvents(false);
    }

    public void closeThisScreen()
    {
        this.closeScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.context.setMouse(mouseX, mouseY);
        this.context.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        if (this.root.isVisible())
        {
            this.context.reset();
            this.context.pushViewport(this.viewport);

            this.root.draw(this.context);

            this.context.popViewport();
            this.context.drawTooltip();
            this.context.postRenderCallbacks.forEach((element) ->
            {
                element.accept(this.context);
            });
        }
    }

    public static class GuiRootElement extends GuiElement implements IViewport
    {
        public GuiRootElement(Minecraft mc)
        {
            super(mc);
        }

        @Override
        public void apply(IViewportStack stack)
        {
            stack.pushViewport(this.area);
        }

        @Override
        public void unapply(IViewportStack stack)
        {
            stack.popViewport();
        }
    }
}