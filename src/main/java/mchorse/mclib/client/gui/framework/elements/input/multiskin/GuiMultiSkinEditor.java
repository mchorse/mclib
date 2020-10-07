package mchorse.mclib.client.gui.framework.elements.input.multiskin;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvas;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.resources.FilteredResourceLocation;
import mchorse.mclib.utils.shaders.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class GuiMultiSkinEditor extends GuiCanvas
{
	public static Shader shader;
	public static int uTexture;
	public static int uTextureBackground;
	public static int uSize;
	public static int uFilters;
	public static int uColor;

	public GuiTexturePicker picker;
	public FilteredResourceLocation location;

	public GuiElement editor;
	public GuiColorElement color;
	public GuiTrackpadElement scale;
	public GuiToggleElement scaleToLargest;
	public GuiTrackpadElement shiftX;
	public GuiTrackpadElement shiftY;

	public GuiTrackpadElement pixelate;
	public GuiToggleElement erase;

	private int w;
	private int h;

	public GuiMultiSkinEditor(Minecraft mc, GuiTexturePicker picker)
	{
		super(mc);

		this.picker = picker;

		this.editor = new GuiElement(mc);
		this.editor.flex().relative(this).xy(1F, 1F).w(130).anchor(1F, 1F).column(5).stretch().vertical().padding(10);

		this.color = new GuiColorElement(mc, (value) -> this.location.color = value);
		this.color.picker.editAlpha();
		this.color.direction(Direction.TOP).tooltip(IKey.lang("mclib.gui.multiskin.color"));
		this.scale = new GuiTrackpadElement(mc, (value) -> this.location.scale = value.floatValue());
		this.scale.limit(0).metric();
		this.scaleToLargest = new GuiToggleElement(mc, IKey.lang("mclib.gui.multiskin.scale_to_largest"), (toggle) -> this.location.scaleToLargest = toggle.isToggled());
		this.shiftX = new GuiTrackpadElement(mc, (value) -> this.location.shiftX = value.intValue());
		this.shiftX.integer();
		this.shiftY = new GuiTrackpadElement(mc, (value) -> this.location.shiftY = value.intValue());
		this.shiftY.integer();

		this.pixelate = new GuiTrackpadElement(mc, (value) -> this.location.pixelate = value.intValue());
		this.pixelate.integer().limit(1);
		this.erase = new GuiToggleElement(mc, IKey.lang("mclib.gui.multiskin.erase"), (toggle) -> this.location.erase = toggle.isToggled());
		this.erase.tooltip(IKey.lang("mclib.gui.multiskin.erase_tooltip"), Direction.TOP);

		this.editor.add(this.color);
		this.editor.add(Elements.label(IKey.lang("mclib.gui.multiskin.scale")).background(0x88000000), this.scale, this.scaleToLargest);
		this.editor.add(Elements.label(IKey.lang("mclib.gui.multiskin.shift")).background(0x88000000), this.shiftX, this.shiftY);
		this.editor.add(Elements.label(IKey.lang("mclib.gui.multiskin.pixelate")).background(0x88000000), this.pixelate, this.erase);
		this.add(this.editor);

		if (shader == null)
		{
			try
			{
				String vert = IOUtils.toString(this.getClass().getResourceAsStream("/assets/mclib/shaders/preview.vert"));
				String frag = IOUtils.toString(this.getClass().getResourceAsStream("/assets/mclib/shaders/preview.frag"));

				shader = new Shader();
				shader.compile(vert, frag, true);

				uTexture = GL20.glGetUniformLocation(shader.programId, "texture");
				uTextureBackground = GL20.glGetUniformLocation(shader.programId, "texture_background");
				uSize = GL20.glGetUniformLocation(shader.programId, "size");
				uFilters = GL20.glGetUniformLocation(shader.programId, "filters");
				uColor = GL20.glGetUniformLocation(shader.programId, "color");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void resetView()
	{
		this.w = 0;
		this.h = 0;

		for (FilteredResourceLocation child : this.picker.multiRL.children)
		{
			this.mc.renderEngine.bindTexture(child.path);
			this.w = Math.max(this.w, GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH));
			this.h = Math.max(this.h, GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT));
		}

		this.scaleX.set(0, 2);
		this.scaleY.set(0, 2);
		this.scaleX.view(-this.w / 2, this.w / 2, this.area.w, 20);
		this.scaleY.view(-this.h / 2, this.h / 2, this.area.h, 20);

		double min = Math.min(this.scaleX.zoom, this.scaleY.zoom);

		this.scaleX.zoom = min;
		this.scaleY.zoom = min;
	}

	public void setLocation(FilteredResourceLocation location)
	{
		this.location = location;

		this.color.picker.setColor(location.color);
		this.scale.setValue(location.scale);
		this.scaleToLargest.toggled(location.scaleToLargest);
		this.shiftX.setValue(location.shiftX);
		this.shiftY.setValue(location.shiftY);

		this.pixelate.setValue(location.pixelate);
		this.erase.toggled(location.erase);
	}

	@Override
	protected void startDragging(GuiContext context)
	{
		super.startDragging(context);

		if (this.mouse == 0)
		{
			this.lastT = this.location.shiftX;
			this.lastV = this.location.shiftY;
		}
	}

	@Override
	protected void dragging(GuiContext context)
	{
		super.dragging(context);

		if (this.dragging && this.mouse == 0)
		{
			double dx = (context.mouseX - this.lastX) / this.scaleX.zoom;
			double dy = (context.mouseY - this.lastY) / this.scaleY.zoom;

			if (GuiScreen.isShiftKeyDown()) dx = 0;
			if (GuiScreen.isCtrlKeyDown()) dy = 0;

			this.location.shiftX = (int) (dx) + (int) this.lastT;
			this.location.shiftY = (int) (dy) + (int) this.lastV;

			this.shiftX.setValue(this.location.shiftX);
			this.shiftY.setValue(this.location.shiftY);
		}
	}

	@Override
	protected void drawCanvas(GuiContext context)
	{
		this.area.draw(0xff2f2f2f);

		Area area = this.calculate(-this.w / 2, -this.h / 2, this.w / 2, this.h / 2);

		Gui.drawRect(area.x - 1, area.y - 1, area.ex() + 1, area.ey() + 1, 0xff181818);
		GlStateManager.color(1, 1, 1, 1);

		if (this.picker.multiRL == null)
		{
			return;
		}

		GuiDraw.scissor(area.x, area.y, area.w, area.h, context);

		int ox = (this.area.x - area.x) % 16;
		int oy = (this.area.y - area.y) % 16;

		Area processed = new Area();
		processed.copy(this.area);
		processed.offsetX(ox < 0 ? 16 + ox : ox);
		processed.offsetY(oy < 0 ? 16 + oy : oy);
		processed.clamp(area);
		Icons.CHECKBOARD.renderArea(area.x, area.y, area.w, area.h);

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		for (FilteredResourceLocation child : this.picker.multiRL.children)
		{
			this.mc.renderEngine.bindTexture(child.path);

			int ow = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			int oh = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
			int ww = ow;
			int hh = oh;

			if (child.scaleToLargest)
			{
				ww = this.w;
				hh = this.h;
			}
			else if (child.scale != 1)
			{
				ww = (int) (ww * child.scale);
				hh = (int) (hh * child.scale);
			}

			if (ww > 0 && hh > 0)
			{
				area = this.calculate(-this.w / 2 + child.shiftX, -this.h / 2 + child.shiftY, -this.w / 2 + child.shiftX + ww, -this.h / 2 + child.shiftY + hh);

				if (child == this.picker.currentFRL)
				{
					Gui.drawRect(area.x, area.y, area.ex(), area.ey(), 0x44ff0000);
					GlStateManager.enableBlend();
					GlStateManager.enableAlpha();
				}

				ColorUtils.bindColor(child.color);

				if (child.pixelate > 1 || child.erase)
				{
					shader.bind();
					GL20.glUniform1i(uTexture, 0);
					GL20.glUniform1i(uTextureBackground, 5);
					GL20.glUniform2f(uSize, ow, oh);
					GL20.glUniform4f(uFilters, (float) child.pixelate, child.erase ? 1F : 0F, 0, 0);
					GL20.glUniform4f(uColor, ColorUtils.COLOR.r, ColorUtils.COLOR.g, ColorUtils.COLOR.b, ColorUtils.COLOR.a);
				}

				GlStateManager.setActiveTexture(GL13.GL_TEXTURE5);
				this.mc.renderEngine.bindTexture(Icons.ICONS);
				GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

				GuiDraw.drawBillboard(area.x, area.y, 0, 0, area.w, area.h, area.w, area.h);

				if (child.pixelate > 1 || child.erase)
				{
					shader.unbind();
				}
			}
		}

		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GuiDraw.unscissor(context);
	}

	private Area calculate(int a, int b, int c, int d)
	{
		int x1 = this.toX(a);
		int y1 = this.toY(b);
		int x2 = this.toX(c);
		int y2 = this.toY(d);

		int x = x1;
		int y = y1;
		int fw = x2 - x;
		int fh = y2 - y;

		Area.SHARED.set(x, y, fw, fh);

		return Area.SHARED;
	}
}