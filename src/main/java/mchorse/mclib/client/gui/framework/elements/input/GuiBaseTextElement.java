package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IFocusedGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public abstract class GuiBaseTextElement extends GuiElement implements IFocusedGuiElement
{
	public GuiTextField field;

	public GuiBaseTextElement(Minecraft mc)
	{
		super(mc);

		this.field = new GuiTextField(0, this.font, 0, 0, 0, 0);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		this.field.setEnabled(enabled);
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		this.field.setVisible(visible);
	}

	@Override
	public boolean isFocused()
	{
		return this.field.isFocused();
	}

	@Override
	public void focus(GuiContext context)
	{
		this.field.setFocused(true);
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void unfocus(GuiContext context)
	{
		this.field.setFocused(false);
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void selectAll(GuiContext context)
	{
		this.field.setCursorPosition(0);
		this.field.setSelectionPos(this.field.getText().length());
	}

	@Override
	public void unselect(GuiContext context)
	{
		this.field.setSelectionPos(this.field.getCursorPosition());
	}
}