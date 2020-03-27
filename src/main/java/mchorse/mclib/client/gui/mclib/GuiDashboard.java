package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.resizers.RowResizer;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

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

		this.panels.flex().parent(this.viewport).w(1, 0).h(1, 0);
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
		private GuiSlotElement current;
		private GuiInventoryElement inve;

		public GuiTest(Minecraft mc)
		{
			super(mc);

			GuiButtonElement button = new GuiButtonElement(mc, "Context", (b) ->
			{
				GuiConfirmModal modal = new GuiConfirmModal(mc, "Hello dude, I heard you like jokes?\n\nMy favorite joke is about some kind of nonesense. It's very cool, right?", (bool) -> {});

				modal.flex().parent(this.area).set(10, 30, 200, 300);
				modal.resize();

				this.add(modal);
			});

			button.setEnabled(false);
			button.keys().register("To pay respect", Keyboard.KEY_F, () ->
			{
				System.out.println("F");

				return false;
			}, Keyboard.KEY_LCONTROL);

			button.flex().parent(this.area).set(10, 10, 100, 20);
			button.context(() -> new GuiSimpleContextMenu(mc)
				.action(Icons.ADD, "Add", () -> System.out.println("Add"))
				.action(Icons.REMOVE, "Remove", () -> System.out.println("Remove, hehe..."))
				.action(Icons.NONE, "Space...", () -> System.out.println("Boo hoo"))
				.action(Icons.LOCKED, "SECRET", () -> System.out.println("Secret!")));

			GuiToggleElement toggle = new GuiToggleElement(mc, "Hello!", (b) -> {});

			toggle.setEnabled(false);
			toggle.flex().relative(button.resizer()).y(1, 5).w(1, 0).h(20);

			GuiIconElement icon = new GuiIconElement(mc, Icons.POSE, (ic) -> {});

			icon.flex().relative(toggle.resizer()).x(1, -20).y(1, 5).w(20).h(20);
			icon.setEnabled(false);

			GuiTextElement input = new GuiTextElement(mc, (v) -> {});

			input.flex().relative(toggle.resizer()).y(1, 5).w(1, -25).h(20);
			input.setEnabled(false);
			input.setText("Test!");

			GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, (v) -> {});

			trackpad.flex().relative(input.resizer()).y(1, 5).w(1, 25).h(20);
			trackpad.setEnabled(false);

			GuiColorElement color = new GuiColorElement(mc, (v) -> {});

			color.picker.editAlpha().setColor(0x880088ff);
			color.flex().relative(trackpad.resizer()).y(1, 5).w(1, 0).h(20);
			color.setEnabled(true);

			this.add(button, toggle, icon, input, trackpad, color);

			GuiElement slots = new GuiElement(mc);

			slots.flex().parent(this.area).wh(145, 40).anchor(0.5F, 0).x(0.5F, 0);

			GuiSlotElement slot1 = new GuiSlotElement(mc, 0, this::setSlot);
			GuiSlotElement slot2 = new GuiSlotElement(mc, 1, this::setSlot);
			GuiSlotElement slot3 = new GuiSlotElement(mc, 2, this::setSlot);
			GuiSlotElement slot4 = new GuiSlotElement(mc, 3, this::setSlot);

			slot1.setEnabled(false);
			slot1.flex().wh(0, 30);
			slot2.flex().wh(0, 30);
			slot3.flex().wh(0, 30);
			slot4.flex().wh(0, 30);

			slots.add(slot1, slot2, slot3, slot4);
			slots.resizer(new RowResizer(slots, 5).padding(5));

			this.inve = new GuiInventoryElement(mc, (item) ->
			{
				if (this.current != null)
				{
					this.current.stack = item;
				}

				this.current.selected = false;
				this.current = null;
				this.inve.setVisible(false);
			});

			this.inve.keys().registerInside("To clear inventory", Keyboard.KEY_0, () ->
			{
				System.out.println("Clear!");

				return true;
			});

			this.inve.flex().relative(slots.resizer()).y(1, 0).x(0.5F, 0).wh(10 * 20, 5 * 20).anchor(0.5F, 0);
			this.inve.setVisible(false);

			this.add(slots, this.inve);
		}

		private void setSlot(GuiSlotElement element)
		{
			if (this.current != null)
			{
				this.current.selected = false;
			}

			this.current = element;
			this.current.selected = true;
			this.inve.setVisible(true);
		}

		@Override
		public void draw(GuiContext context)
		{
			super.draw(context);
		}
	}
}