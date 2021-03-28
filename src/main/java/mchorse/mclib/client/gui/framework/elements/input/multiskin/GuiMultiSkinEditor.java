package mchorse.mclib.client.gui.framework.elements.input.multiskin;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvasEditor;
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

import java.nio.charset.Charset;

public class GuiMultiSkinEditor extends GuiCanvasEditor
{
    public static Shader shader;
    public static int uTexture;
    public static int uTextureBackground;
    public static int uSize;
    public static int uFilters;
    public static int uColor;

    public GuiTexturePicker picker;
    public FilteredResourceLocation location;

    public GuiToggleElement autoSize;
    public GuiTrackpadElement sizeW;
    public GuiTrackpadElement sizeH;

    public GuiColorElement color;
    public GuiTrackpadElement scale;
    public GuiToggleElement scaleToLargest;
    public GuiTrackpadElement shiftX;
    public GuiTrackpadElement shiftY;

    public GuiTrackpadElement pixelate;
    public GuiToggleElement erase;

    public GuiMultiSkinEditor(Minecraft mc, GuiTexturePicker picker)
    {
        super(mc);

        this.picker = picker;

        this.autoSize = new GuiToggleElement(mc, IKey.lang("mclib.gui.multiskin.auto_size"), (toggle) ->
        {
            this.location.autoSize = toggle.isToggled();
            this.resizeCanvas();
        });
        this.autoSize.tooltip(IKey.lang("mclib.gui.multiskin.auto_size_tooltip"));
        this.sizeW = new GuiTrackpadElement(mc, (value) ->
        {
            this.location.sizeW = value.intValue();
            this.resizeCanvas();
        });
        this.sizeW.integer().limit(0).tooltip(IKey.lang("mclib.gui.multiskin.size_w"));
        this.sizeH = new GuiTrackpadElement(mc, (value) ->
        {
            this.location.sizeH = value.intValue();
            this.resizeCanvas();
        });
        this.sizeH.integer().limit(0).tooltip(IKey.lang("mclib.gui.multiskin.size_h"));

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
        this.editor.add(Elements.label(IKey.lang("mclib.gui.multiskin.custom_size")).background(0x88000000), this.autoSize, this.sizeW, this.sizeH);

        if (shader == null)
        {
            try
            {
                String vert = IOUtils.toString(this.getClass().getResourceAsStream("/assets/mclib/shaders/preview.vert"), Charset.defaultCharset());
                String frag = IOUtils.toString(this.getClass().getResourceAsStream("/assets/mclib/shaders/preview.frag"), Charset.defaultCharset());

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
        int w = 0;
        int h = 0;

        for (FilteredResourceLocation child : this.picker.multiRL.children)
        {
            this.mc.renderEngine.bindTexture(child.path);
            w = Math.max(w, child.getWidth(GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)));
            h = Math.max(h, child.getHeight(GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)));
        }

        this.setSize(w, h);
        this.color.picker.removeFromParent();
    }

    private void resizeCanvas()
    {
        int w = 0;
        int h = 0;

        for (FilteredResourceLocation child : this.picker.multiRL.children)
        {
            this.mc.renderEngine.bindTexture(child.path);
            w = Math.max(w, child.getWidth(GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH)));
            h = Math.max(h, child.getHeight(GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT)));
        }

        if (w != this.getWidth() || h != this.getHeight())
        {
            this.setSize(w, h);
        }
    }

    public void close()
    {
        this.color.picker.removeFromParent();
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

        this.autoSize.toggled(location.autoSize);
        this.sizeW.setValue(location.sizeW);
        this.sizeH.setValue(location.sizeH);
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
            double dx = (context.mouseX - this.lastX) / this.scaleX.getZoom();
            double dy = (context.mouseY - this.lastY) / this.scaleY.getZoom();

            if (GuiScreen.isShiftKeyDown()) dx = 0;
            if (GuiScreen.isCtrlKeyDown()) dy = 0;

            this.location.shiftX = (int) (dx) + (int) this.lastT;
            this.location.shiftY = (int) (dy) + (int) this.lastV;

            this.shiftX.setValue(this.location.shiftX);
            this.shiftY.setValue(this.location.shiftY);
        }
    }

    @Override
    protected boolean shouldDrawCanvas(GuiContext context)
    {
        return this.picker.multiRL != null;
    }

    @Override
    protected void drawCanvasFrame(GuiContext context)
    {
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
                Area area = this.calculate(-this.w / 2 + child.shiftX, -this.h / 2 + child.shiftY, -this.w / 2 + child.shiftX + ww, -this.h / 2 + child.shiftY + hh);

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
    }
}