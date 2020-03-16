package mchorse.mclib.client.gui.framework.elements;

public interface IFocusedGuiElement
{
	public boolean isFocused();

	public void focus(GuiContext context);

	public void unfocus(GuiContext context);
}