package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.keyframes.Keyframe;
import mchorse.mclib.utils.keyframes.KeyframeChannel;
import mchorse.mclib.utils.keyframes.KeyframeEasing;
import mchorse.mclib.utils.keyframes.KeyframeInterpolation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dope sheet editor
 *
 * This GUI element is responsible for editing mutliple keyframe channels
 * at the same time (however without editing the Y values of these individual
 * channels)
 */
public class GuiDopeSheet extends GuiKeyframeElement
{
    public List<GuiSheet> sheets = new ArrayList<GuiSheet>();
    public GuiSheet current;

    public GuiDopeSheet(Minecraft mc, Consumer<Keyframe> callback)
    {
        super(mc, callback);
    }

    /* Implementation of setters */

    @Override
    public void setTick(double tick)
    {
        for (GuiSheet sheet : this.sheets)
        {
            sheet.setTick(tick, this.which);
        }
    }

    @Override
    public void setValue(double value)
    {
        for (GuiSheet sheet : this.sheets)
        {
            sheet.setValue(value, this.which);
        }
    }

    @Override
    public void setInterpolation(KeyframeInterpolation interp)
    {
        for (GuiSheet sheet : this.sheets)
        {
            sheet.setInterpolation(interp);
        }
    }

    @Override
    public void setEasing(KeyframeEasing easing)
    {
        for (GuiSheet sheet : this.sheets)
        {
            sheet.setEasing(easing);
        }
    }

    /* Graphing code */

    @Override
    public void resetView()
    {
        int c = 0;

        this.scaleX.set(0, 2);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        /* Find minimum and maximum */
        for (GuiSheet sheet : this.sheets)
        {
            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                min = Integer.min((int) frame.tick, min);
                max = Integer.max((int) frame.tick, max);
            }

            c = Math.max(c, sheet.channel.getKeyframes().size());
        }

        if (c <= 1)
        {
            if (c == 0)
            {
                min = 0;
            }

            max = this.duration;
        }

        if (Math.abs(max - min) > 0.01F)
        {
            this.scaleX.view(min, max, this.area.w, 30);
        }

        this.recalcMultipliers();
    }

    @Override
    public Keyframe getCurrent()
    {
        if (this.current != null)
        {
            return this.current.getKeyframe();
        }

        return null;
    }

	@Override
	public void addCurrent(int mouseX, int mouseY)
	{
		int count = this.sheets.size();
		int h = (this.area.h - 15) / count;
		int i = (mouseY - (this.area.ey() - h * count)) / h;

		if (i < 0 || i >= count)
		{
			return;
		}

		this.current = this.sheets.get(i);

		KeyframeEasing easing = KeyframeEasing.IN;
		KeyframeInterpolation interp = KeyframeInterpolation.LINEAR;
		Keyframe frame = this.getCurrent();
		long tick = (long) this.fromGraphX(mouseX);
		long oldTick = tick;

		if (frame != null)
		{
			easing = frame.easing;
			interp = frame.interp;
			oldTick = frame.tick;
		}

		this.current.selected = this.current.channel.insert(tick, this.current.channel.interpolate(tick));
		frame = this.getCurrent();

		if (oldTick != tick)
		{
			frame.setEasing(easing);
			frame.setInterpolation(interp);
		}

		this.addedDoubleClick(frame, tick, mouseX, mouseY);
	}

	protected void addedDoubleClick(Keyframe frame, long tick, int mouseX, int mouseY)
	{}

	@Override
	public void removeCurrent()
	{
		Keyframe frame = this.getCurrent();

		if (frame == null)
		{
			return;
		}

		this.current.channel.remove(this.current.selected);
		this.current.selected -= 1;
		this.which = Selection.NOT_SELECTED;
	}

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        /* Select current point with a mouse click */
        if (this.area.isInside(mouseX, mouseY))
        {
            if (context.mouseButton == 0)
            {
                /* Duplicate the keyframe */
                if (GuiScreen.isAltKeyDown() && this.current != null && this.which == Selection.KEYFRAME)
                {
                    Keyframe frame = this.getCurrent();

                    if (frame != null)
                    {
                        long offset = (long) this.fromGraphX(mouseX);
                        Keyframe created = this.current.channel.get(this.current.channel.insert(offset, frame.value));

                        this.current.selected = this.current.channel.getKeyframes().indexOf(created);
                        created.copy(frame);
                        created.tick = offset;
                    }

                    return false;
                }

                this.which = Selection.NOT_SELECTED;
                this.current = null;

                int count = this.sheets.size();
                int h = (this.area.h - 15) / count;
                int y = this.area.ey() - h * count;
                boolean reset = true;

                for (GuiSheet sheet : this.sheets)
                {
                    Keyframe prev = null;
                    int i = 0;
                    sheet.selected = -1;

                    for (Keyframe frame : sheet.channel.getKeyframes())
                    {
                        boolean left = sheet.handles && prev != null && prev.interp == KeyframeInterpolation.BEZIER && this.isInside(this.toGraphX(frame.tick - frame.lx), y + h / 2, mouseX, mouseY);
                        boolean right = sheet.handles && frame.interp == KeyframeInterpolation.BEZIER && this.isInside(this.toGraphX(frame.tick + frame.rx), y + h / 2, mouseX, mouseY);
                        boolean point = this.isInside(this.toGraphX(frame.tick), y + h / 2, mouseX, mouseY);

                        if (left || right || point)
                        {
                            this.which = left ? Selection.LEFT_HANDLE : (right ? Selection.RIGHT_HANDLE : Selection.KEYFRAME);
                            this.current = sheet;
                            this.setKeyframe(frame);

                            this.lastT = frame.tick;

                            this.lastX = mouseX;
                            this.dragging = true;
                            sheet.selected = i;
                            reset = false;

                            break;
                        }

                        prev = frame;
                        i++;
                    }

                    y += h;
                }

                if (this.parent != null && reset)
                {
                    this.dragging = true;
                }
            }
            else if (context.mouseButton == 2)
            {
                this.scrolling = true;
                this.lastX = mouseX;
                this.lastT = this.scaleX.shift;
            }
        }

        return false;
    }

    private boolean isInside(double x, double y, int mouseX, int mouseY)
    {
        double d = Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2);

        return Math.sqrt(d) < 4;
    }

	@Override
	protected void postSlideSort(GuiContext context)
	{
		/* Resort after dragging the tick thing */
		this.current.sort();
		this.sliding = false;
	}

	@Override
    public void draw(GuiContext context)
    {
        int mouseX = context.mouseX;

        this.handleMouse(context, mouseX);

        /* Draw shit */
        this.area.draw(0x88000000);

        if (this.duration > 0)
        {
            int leftBorder = this.toGraphX(0);
            int rightBorder = this.toGraphX(this.duration);

            if (leftBorder > this.area.x) Gui.drawRect(this.area.x, this.area.y, leftBorder, this.area.y + this.area.h, 0x88000000);
            if (rightBorder < this.area.ex()) Gui.drawRect(rightBorder, this.area.y, this.area.ex() , this.area.y + this.area.h, 0x88000000);
        }

        /* Draw scaling grid */
        int hx = this.duration / this.scaleX.mult;

        for (int j = 0; j <= hx; j++)
        {
            int x = this.toGraphX(j * this.scaleX.mult);

            Gui.drawRect(this.area.x + x, this.area.y, this.area.x + x + 1, this.area.ey(), 0x44ffffff);
            this.font.drawString(String.valueOf(j * this.scaleX.mult), this.area.x + x + 4, this.area.y + 4, 0xffffff);
        }

        /* Draw current point at the timeline */
        this.drawCursor(context);

        /* Draw dope sheet */
        int count = this.sheets.size();
        int h = (this.area.h - 15) / count;
        int y = this.area.ey() - h * count;

        for (GuiSheet sheet : this.sheets)
        {
            float r = (sheet.color >> 16 & 255) / 255.0F;
            float g = (sheet.color >> 8 & 255) / 255.0F;
            float b = (sheet.color & 255) / 255.0F;

            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(Minecraft.getMinecraft().gameSettings.guiScale * 1.5F);

            BufferBuilder vb = Tessellator.getInstance().getBuffer();

            vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            vb.pos(this.area.x, y + h / 2, 0).color(r, g, b, 0.65F).endVertex();
            vb.pos(this.area.ex(), y + h / 2, 0).color(r, g, b, 0.65F).endVertex();

            Tessellator.getInstance().draw();

            /* Draw points */
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            Keyframe prev = null;
            int i = 0;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                this.drawRect(vb, this.toGraphX(frame.tick), y + h / 2, 3, this.current == sheet && i == sheet.selected ? 0xffffff : sheet.color);

                if (frame.interp == KeyframeInterpolation.BEZIER && sheet.handles)
                {
                    this.drawRect(vb, this.toGraphX(frame.tick + frame.rx), y + h / 2, 2, this.current == sheet && i == sheet.selected ? 0xffffff : sheet.color);
                }

                if (prev != null && prev.interp == KeyframeInterpolation.BEZIER && sheet.handles)
                {
                    this.drawRect(vb, this.toGraphX(frame.tick - frame.lx), y + h / 2, 2, this.current == sheet && i == sheet.selected ? 0xffffff : sheet.color);
                }

                prev = frame;
                i++;
            }

            prev = null;
            i = 0;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                this.drawRect(vb, this.toGraphX(frame.tick), y + h / 2, 2, this.current == sheet && i == sheet.selected && this.which == Selection.KEYFRAME ? 0x0080ff : 0);

                if (frame.interp == KeyframeInterpolation.BEZIER && sheet.handles)
                {
                    this.drawRect(vb, this.toGraphX(frame.tick + frame.rx), y + h / 2, 1, this.current == sheet && i == sheet.selected && this.which == Selection.RIGHT_HANDLE ? 0x0080ff : 0);
                }

                if (prev != null && prev.interp == KeyframeInterpolation.BEZIER && sheet.handles)
                {
                    this.drawRect(vb, this.toGraphX(frame.tick - frame.lx), y + h / 2, 1, this.current == sheet && i == sheet.selected && this.which == Selection.LEFT_HANDLE ? 0x0080ff : 0);
                }

                prev = frame;
                i++;
            }

            Tessellator.getInstance().draw();

            int lw = this.font.getStringWidth(sheet.title.get()) + 10;
            GuiDraw.drawHorizontalGradientRect(this.area.ex() - lw - 10, y, this.area.ex(), y + h, sheet.color, 0xaa000000 + sheet.color, 0);
            this.font.drawStringWithShadow(sheet.title.get(), this.area.ex() - lw + 5, y + (h - this.font.FONT_HEIGHT) / 2 + 1, 0xffffff);

            y += h;
        }

        super.draw(context);
    }

	private void handleMouse(GuiContext context, int mouseX)
	{
		if (this.dragging && !this.moving && (Math.abs(this.lastX - mouseX) > 3))
		{
			this.moving = true;
			this.sliding = true;
		}

		if (this.scrolling)
		{
			this.scaleX.shift = -(mouseX - this.lastX) / this.scaleX.zoom + this.lastT;
		}
		/* Move the current keyframe */
		else if (this.moving)
		{
			Keyframe frame = this.getCurrent();
			double x = this.fromGraphX(mouseX);

			if (this.which == Selection.KEYFRAME)
			{
				frame.setTick((long) x);
			}
			else if (this.which == Selection.LEFT_HANDLE)
			{
				frame.lx = (int) -(x - frame.tick);

				if (!GuiScreen.isAltKeyDown())
				{
					frame.rx = frame.lx;
				}
			}
			else if (this.which == Selection.RIGHT_HANDLE)
			{
				frame.rx = (int) x - frame.tick;

				if (!GuiScreen.isAltKeyDown())
				{
					frame.lx = frame.rx;
				}
			}
			else if (this.which == Selection.NOT_SELECTED && this.parent != null)
			{
				this.moveNoKeyframe(context, frame, x, 0);
			}

			this.setKeyframe(this.getCurrent());
		}
	}

	public static class GuiSheet
    {
        public IKey title;
        public int color;
        public KeyframeChannel channel;
        public int selected = -1;
        public boolean handles = true;

        public GuiSheet(IKey title, int color, KeyframeChannel channel, boolean handles)
        {
            this(title, color, channel);

            this.handles = handles;
        }

        public GuiSheet(IKey title, int color, KeyframeChannel channel)
        {
            this.title = title;
            this.color = color;
            this.channel = channel;
        }

        public void sort()
        {
            Keyframe frame = this.getKeyframe();

            this.channel.sort();
            this.selected = this.channel.getKeyframes().indexOf(frame);
        }

        public void setTick(double tick, Selection which)
        {
	        Keyframe keyframe = this.getKeyframe();

	        if (keyframe != null)
	        {
		        which.setX(keyframe, tick);
	        }
        }

        public void setValue(double value, Selection which)
        {
	        Keyframe keyframe = this.getKeyframe();

	        if (keyframe != null)
	        {
		        which.setY(keyframe, value);
	        }
        }

        public void setInterpolation(KeyframeInterpolation interp)
        {
	        Keyframe keyframe = this.getKeyframe();

	        if (keyframe != null)
	        {
		        keyframe.setInterpolation(interp);
	        }
        }

        public void setEasing(KeyframeEasing easing)
        {
			Keyframe keyframe = this.getKeyframe();

			if (keyframe != null)
			{
				keyframe.setEasing(easing);
			}
        }

        public Keyframe getKeyframe()
        {
            return this.channel.get(this.selected);
        }
    }
}