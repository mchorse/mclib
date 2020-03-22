package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiContext;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiMessageModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.resizers.GridResizer;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class GuiDashboard extends GuiBase
{
	public GuiPanelBase<GuiElement> panels;

	public GuiDashboard(Minecraft mc)
	{
		GuiConfig config = new GuiConfig(mc);

		this.panels = new GuiPanelBase<GuiElement>(mc, Direction.LEFT)
		{
			@Override
			protected void drawBackground(GuiContext context, int x, int y, int w, int h)
			{
				Gui.drawRect(x, y, x + w, y + h, 0xff111111);
			}
		};

		this.panels.resizer().parent(this.viewport).w(1, 0).h(1, 0);
		this.panels.registerPanel(config, I18n.format("mclib.gui.config.tooltip"), Icons.GEAR);
		this.panels.registerPanel(new GuiTest(mc), "Test", Icons.POSE);
		McLib.EVENT_BUS.post(new RegisterDashboardPanels(this));
		this.panels.setPanel(config);

		this.root.add(this.panels);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public static class GuiTest extends GuiElement
	{
		public GuiTest(Minecraft mc)
		{
			super(mc);

			GuiButtonElement button = new GuiButtonElement(mc, "Context", (b) ->
			{
				GuiConfirmModal modal = new GuiConfirmModal(mc, "Hello dude, I heard you like jokes?\n\nMy favorite joke is about some kind of nonesense. It's very cool, right?", (bool) -> {});
				
				modal.resizer().parent(this.area).set(10, 30, 200, 300);
				modal.resize();

				this.add(modal);
			});

			button.resizer().parent(this.area).set(10, 10, 100, 20);
			button.context(() -> new GuiSimpleContextMenu(mc)
				.action(Icons.ADD, "Add", () -> System.out.println("Add"))
				.action(Icons.REMOVE, "Remove", () -> System.out.println("Remove, hehe..."))
				.action(Icons.NONE, "Space...", () -> System.out.println("Boo hoo"))
				.action(Icons.LOCKED, "SECRET", () -> System.out.println("Secret!")));

			this.add(button);
		}

		@Override
		public void draw(GuiContext context)
		{
			super.draw(context);
		}
	}
}