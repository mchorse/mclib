package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiElement
{
    /**
     * Should be called when position has to be recalculated
     */
    public void resize();

    /**
     * Whether this element is enabled (and can accept any input) 
     */
    public boolean isEnabled();

    /**
     * Whether this element is visible
     */
    public boolean isVisible();

    /**
     * Mouse was clicked
     */
    public boolean mouseClicked(GuiContext context);

    /**
     * Mouse wheel was scrolled
     */
    public boolean mouseScrolled(GuiContext context);

    /**
     * Mouse was released
     */
    public void mouseReleased(GuiContext context);

    /**
     * Key was typed
     */
    public boolean keyTyped(GuiContext context);

    /**
     * Determines whether this element can be drawn on the screen
     */
    public boolean canBeDrawn(Area viewport);

    /**
     * Draw its components on the screen
     */
    public void draw(GuiContext context);
}