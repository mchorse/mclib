package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.config.gui.GuiConfig;
import mchorse.mclib.events.RegisterDashboardPanels;
import mchorse.mclib.utils.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

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

		this.panels.flex().relative(this.viewport).w(1, 0).h(1, 0);
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

			GuiModelRenderer renderer = new GuiModelRenderer(mc)
			{
				@Override
				protected void drawUserModel(GuiContext context)
				{
					EntityPlayer ent = this.mc.player;
					boolean render = ent.getAlwaysRenderNameTag();

					RenderHelper.enableStandardItemLighting();

					GlStateManager.enableRescaleNormal();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

					float f = ent.renderYawOffset;
					float f1 = ent.rotationYaw;
					float f2 = ent.rotationPitch;
					float f3 = ent.prevRotationYawHead;
					float f4 = ent.rotationYawHead;

					ent.renderYawOffset = 0;
					ent.rotationYaw = 0;
					ent.rotationPitch = 0;
					ent.rotationYawHead = ent.rotationYaw;
					ent.prevRotationYawHead = ent.rotationYaw;
					ent.setAlwaysRenderNameTag(false);

					GlStateManager.translate(0.0F, 0.0F, 0.0F);

					RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
					rendermanager.setPlayerViewY(180.0F);
					rendermanager.setRenderShadow(false);
					rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
					rendermanager.setRenderShadow(true);

					ent.renderYawOffset = f;
					ent.rotationYaw = f1;
					ent.rotationPitch = f2;
					ent.prevRotationYawHead = f3;
					ent.rotationYawHead = f4;

					ent.setAlwaysRenderNameTag(render);

					RenderHelper.disableStandardItemLighting();
				}
			};

			renderer.flex().relative(this.area).wh(0.5F, 0.5F);
			this.add(renderer);
		}

		@Override
		public void draw(GuiContext context)
		{
			this.area.draw(0x88000000);

			super.draw(context);
		}
	}
}