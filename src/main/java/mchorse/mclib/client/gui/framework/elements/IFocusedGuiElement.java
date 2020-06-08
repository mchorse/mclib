package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;

public interface IFocusedGuiElement
{
	public boolean isFocused();

	public void focus(GuiContext context);

	public void unfocus(GuiContext context);

	public void selectAll(GuiContext context);

	public void unselect(GuiContext context);
}