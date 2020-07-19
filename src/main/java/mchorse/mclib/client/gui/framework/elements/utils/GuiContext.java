package mchorse.mclib.client.gui.framework.elements.utils;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IFocusedGuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public class GuiContext
{
	public Minecraft mc;
	public FontRenderer font;

	/* GUI elements */
	public final GuiBase screen;
	public final GuiTooltip tooltip;
	public final GuiKeybinds keybinds;
	public IFocusedGuiElement activeElement;
	public GuiContextMenu contextMenu;

	/* Mouse states */
	public int mouseX;
	public int mouseY;
	public int mouseButton;
	public int mouseWheel;

	/* Keyboard states */
	public char typedChar;
	public int keyCode;

	/* Render states */
	public float partialTicks;
	public int shiftX;
	public int shiftY;
	public long tick;

	public GuiContext(GuiBase screen)
	{
		this.screen = screen;
		this.tooltip = new GuiTooltip();
		this.keybinds = new GuiKeybinds(Minecraft.getMinecraft());
		this.keybinds.setVisible(false);
	}

	/**
	 * Get absolute X coordinate of the mouse without the
	 * scrolling areas applied
	 */
	public int mouseX()
	{
		return this.globalX(this.mouseX);
	}

	/**
	 * Get absolute Y coordinate of the mouse without the
	 * scrolling areas applied
	 */
	public int mouseY()
	{
		return this.globalY(this.mouseY);
	}

	/**
	 * Get global X (relative to screen)
	 */
	public int globalX(int x)
	{
		return x - this.shiftX;
	}

	/**
	 * Get global Y (relative to screen)
	 */
	public int globalY(int y)
	{
		return y - this.shiftY;
	}

	/**
	 * Get local X (relative to current scrolling area)
	 */
	public int localX(int x)
	{
		return x + this.shiftX;
	}

	/**
	 * Get local Y (relative to current scrolling area)
	 */
	public int localY(int y)
	{
		return y + this.shiftY;
	}

	public void setMouse(int mouseX, int mouseY)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.shiftX = 0;
		this.shiftY = 0;
	}

	public void setMouse(int mouseX, int mouseY, int mouseButton)
	{
		this.setMouse(mouseX, mouseY);
		this.mouseButton = mouseButton;
	}

	public void setMouseWheel(int mouseX, int mouseY, int mouseWheel)
	{
		this.setMouse(mouseX, mouseY);
		this.mouseWheel = mouseWheel;
	}

	public void setKey(char typedChar, int keyCode)
	{
		this.typedChar = typedChar;
		this.keyCode = keyCode;
	}

	public void reset()
	{
		this.tooltip.set(null, null);

		if (this.activeElement instanceof GuiElement && !((GuiElement) this.activeElement).canBeSeen())
		{
			this.unfocus();
		}
	}

	/* Tooltip */

	public void drawTooltip()
	{
		this.tooltip.drawTooltip(this);
	}

	/* Element focusing */

	public boolean isFocused()
	{
		return this.activeElement != null;
	}

	public void focus(IFocusedGuiElement element)
	{
		this.focus(element, false);
	}

	public void focus(IFocusedGuiElement element, boolean select)
	{
		if (this.activeElement == element)
		{
			return;
		}

		if (this.activeElement != null)
		{
			this.activeElement.unfocus(this);

			if (select)
			{
				this.activeElement.unselect(this);
			}
		}

		this.activeElement = element;

		if (this.activeElement != null)
		{
			this.activeElement.focus(this);

			if (select)
			{
				this.activeElement.selectAll(this);
			}
		}
	}

	public void unfocus()
	{
		this.focus(null);
	}

	public boolean focus(GuiElement parent, int index, int factor)
	{
		return this.focus(parent, index, factor, false);
	}

	/**
	 * Focus next focusable GUI element
	 */
	public boolean focus(GuiElement parent, int index, int factor, boolean stop)
	{
		List<IGuiElement> children = parent.getChildren();

		factor = factor >= 0 ? 1 : -1;
		index += factor;

		for (; index >= 0 && index < children.size(); index += factor)
		{
			IGuiElement child = children.get(index);

			if (!child.isEnabled())
			{
				continue;
			}

			if (child instanceof IFocusedGuiElement)
			{
				this.focus((IFocusedGuiElement) child, true);

				return true;
			}
			else if (child instanceof GuiElement)
			{
				int start = factor > 0 ? -1 : ((GuiElement) child).getChildren().size();

				if (this.focus((GuiElement) child, start, factor, true))
				{
					return true;
				}
			}
		}

		GuiElement grandparent = parent.getParent();
		boolean isRoot = grandparent == this.screen.root;

		if (grandparent != null && !stop && (isRoot || grandparent.canBeSeen()))
		{
			/* Forgive me for this heresy, but I have no idea what other name I could give
			 * to this variable */
			List<IGuiElement> childs = grandparent.getChildren();

			if (this.focus(grandparent, childs.indexOf(parent), factor))
			{
				return true;
			}

			if (isRoot)
			{
				if (this.focus(grandparent, factor > 0 ? -1 : childs.size() - 1, factor))
				{
					return true;
				}
			}
		}

		return false;
	}

	/* Context menu */

	public boolean hasContextMenu()
	{
		if (this.contextMenu == null)
		{
			return false;
		}

		if (!this.contextMenu.hasParent())
		{
			this.contextMenu = null;
		}

		return this.contextMenu != null;
	}

	public void setContextMenu(GuiContextMenu menu)
	{
		if (this.hasContextMenu() || menu == null)
		{
			return;
		}

		menu.setMouse(this);
		menu.resize();

		this.contextMenu = menu;
		this.screen.root.add(menu);
	}

	public void replaceContextMenu(GuiContextMenu menu)
	{
		if (menu == null)
		{
			return;
		}

		if (this.contextMenu != null)
		{
			this.contextMenu.removeFromParent();
		}

		menu.setMouse(this);
		menu.resize();

		this.contextMenu = menu;
		this.screen.root.add(menu);
	}
}