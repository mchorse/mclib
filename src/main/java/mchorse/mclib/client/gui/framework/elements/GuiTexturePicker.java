package mchorse.mclib.client.gui.framework.elements;

import java.util.function.Consumer;

import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.list.GuiFolderEntryList;
import mchorse.mclib.client.gui.framework.elements.list.GuiResourceLocationList;
import mchorse.mclib.client.gui.widgets.buttons.GuiTextureButton;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.GlobalTree;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * Texture picker GUI
 * 
 * This bad boy allows picking a texture from the file browser, and also 
 * it allows creating multi-skins. See {@link MultiResourceLocation} for 
 * more information.
 */
public class GuiTexturePicker extends GuiElement
{
    public GuiTextElement text;
    public GuiButtonElement<GuiButton> close;
    public GuiButtonElement<GuiButton> folder;
    public GuiFolderEntryList picker;

    public GuiButtonElement<GuiButton> multi;
    public GuiButtonElement<GuiTextureButton> add;
    public GuiButtonElement<GuiTextureButton> remove;
    public GuiResourceLocationList multiList;

    public Consumer<ResourceLocation> callback;

    public MultiResourceLocation multiRL;
    public ResourceLocation current;
    public FileTree tree = GlobalTree.TREE;

    private long lastChecked;
    private long lastTyped;
    private String typed = "";

    public GuiTexturePicker(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc);

        this.text = new GuiTextElement(mc, 1000, (str) -> this.selectCurrent(str.isEmpty() ? null : RLUtils.create(str)));
        this.close = GuiButtonElement.button(mc, "X", (b) -> this.setVisible(false));
        this.folder = GuiButtonElement.button(mc, I18n.format("mclib.gui.open_folder"), (b) -> this.openFolder());
        this.picker = new GuiFolderEntryList(mc, (entry) ->
        {
            ResourceLocation rl = entry.resource;

            this.selectCurrent(rl);
            this.text.setText(rl == null ? "" : rl.toString());
        })  {
            @Override
            public void setFolder(FolderEntry folder)
            {
                super.setFolder(folder);

                GuiTexturePicker.this.updateFolderButton();
            }
        };

        this.multi = GuiButtonElement.button(mc, I18n.format("mclib.gui.multi_skin"), (b) -> this.toggleMultiSkin());
        this.multiList = new GuiResourceLocationList(mc, (rl) -> this.displayCurrent(rl));
        this.add = GuiButtonElement.icon(mc, GuiBase.ICONS, 32, 32, 32, 48, (b) -> this.addMultiSkin());
        this.remove = GuiButtonElement.icon(mc, GuiBase.ICONS, 64, 32, 64, 48, (b) -> this.removeMultiSkin());

        this.text.resizer().set(115, 5, 0, 20).parent(this.area).w(1, -145);
        this.close.resizer().set(0, 5, 20, 20).parent(this.area).x(1, -25);
        this.folder.resizer().set(0, 0, 80, 20).parent(this.area).x(1, -90).y(1, - 30);
        this.picker.resizer().set(115, 30, 0, 0).parent(this.area).w(1, -120).h(1, -30);

        this.multi.resizer().parent(this.area).set(5, 5, 100, 20);
        this.add.resizer().parent(this.area).set(67, 7, 16, 16);
        this.remove.resizer().relative(this.add.resizer()).set(20, 0, 16, 16);
        this.multiList.resizer().set(5, 35, 100, 0).parent(this.area).h(1, -40);

        this.add(this.picker, this.multi, this.multiList, this.text, this.close, this.folder, this.add, this.remove);
        this.callback = callback;
    }

    public void refresh()
    {
        this.picker.update();
        this.updateFolderButton();
    }

    public void openFolder()
    {
        if (this.picker.parent != null && this.picker.parent.file != null)
        {
            mchorse.mclib.client.gui.utils.GuiUtils.openWebLink(this.picker.parent.file.toURI());
        }
    }

    public void updateFolderButton()
    {
        this.folder.setEnabled(this.picker.parent != null && this.picker.parent.file != null);
    }

    public void fill(ResourceLocation skin)
    {
        this.setMultiSkin(skin, false);
    }

    /**
     * Add a {@link ResourceLocation} to the multiRL 
     */
    private void addMultiSkin()
    {
        ResourceLocation rl = this.current;

        if (rl == null && !this.text.field.getText().isEmpty())
        {
            rl = RLUtils.create(this.text.field.getText());
        }

        this.multiList.add(rl);
        this.multiList.current = this.multiList.getList().size() - 1;
        this.displayCurrent(this.multiList.getCurrent());
    }

    /**
     * Remove currently selected {@link ResourceLocation} from multiRL 
     */
    private void removeMultiSkin()
    {
        if (this.multiList.current >= 0 && this.multiList.getList().size() > 1)
        {
            this.multiList.getList().remove(this.multiList.current);
            this.multiList.update();
            this.multiList.current--;

            if (this.multiList.current >= 0)
            {
                this.displayCurrent(this.multiList.getCurrent());
            }
        }
    }

    /**
     * Display current resource location (it's just for visual, not 
     * logic)
     */
    protected void displayCurrent(ResourceLocation rl)
    {
        this.current = rl;
        this.picker.rl = rl;
        this.text.setText(rl == null ? "" : rl.toString());
        this.text.field.setCursorPositionZero();

        if (this.tree != null)
        {
            FolderEntry folder = this.tree.getByPath(rl == null ? "" : rl.getResourceDomain() + "/" + rl.getResourcePath());

            if (folder != this.tree.root || this.picker.getList().isEmpty())
            {
                this.picker.setList(folder.getEntries());
                this.picker.parent = folder;
                this.picker.setCurrent(rl);
                this.picker.update();

                this.updateFolderButton();
            }
        }
    }

    /**
     * Select current resource location
     */
    protected void selectCurrent(ResourceLocation rl)
    {
        try
        {
            /* If this code causes an exception, that means that there 
             * is some issue with the texture, so we should rather not 
             * use it */
            this.mc.renderEngine.bindTexture(rl);
        }
        catch (Exception e)
        {
            return;
        }

        this.current = rl;
        this.picker.rl = rl;

        if (this.multiRL != null)
        {
            if (this.multiList.current != -1 && rl != null)
            {
                this.multiList.getList().set(this.multiList.current, rl);
            }

            rl = this.multiRL;
        }

        if (this.callback != null)
        {
            this.callback.accept(rl);
        }
    }

    protected void toggleMultiSkin()
    {
        if (this.multiRL != null)
        {
            this.setMultiSkin(this.multiRL.children.get(0), true);
        }
        else if (this.current != null)
        {
            this.setMultiSkin(new MultiResourceLocation(this.current.toString()), true);
        }
        else
        {
            ResourceLocation rl = this.picker.getCurrentResource();

            if (rl != null)
            {
                this.setMultiSkin(rl, true);
            }
        }
    }

    protected void setMultiSkin(ResourceLocation skin, boolean notify)
    {
        boolean show = skin instanceof MultiResourceLocation;

        if (show)
        {
            this.multiRL = (MultiResourceLocation) skin;
            this.displayCurrent(this.multiRL.children.get(0));

            this.multiList.current = this.multiRL.children.isEmpty() ? -1 : 0;
            this.multiList.setList(this.multiRL.children);

            this.picker.resizer().set(115, 30, 0, 0).parent(this.area).w(1, -120).h(1, -30);
            this.multi.resizer().set(5, 5, 60, 20).parent(this.area);
        }
        else
        {
            this.multiRL = null;

            this.picker.resizer().set(5, 30, 0, 0).parent(this.area).w(1, -10).h(1, -30);
            this.multi.resizer().set(5, 5, 100, 20).parent(this.area);

            this.displayCurrent(skin);
        }

        if (notify)
        {
            if (show) if (this.callback != null) this.callback.accept(skin);
            else this.selectCurrent(skin);
        }

        this.multiList.setVisible(show);
        this.add.setVisible(show);
        this.remove.setVisible(show);

        this.picker.resize();
        this.multi.resize();
        this.updateFolderButton();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        /* Necessary measure to avoid triggering buttons when you press 
         * on a text field, for example */
        return super.mouseClicked(mouseX, mouseY, mouseButton) || (this.isVisible() && this.area.isInside(mouseX, mouseY));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        if (this.hasActiveTextfields())
        {
            super.keyTyped(typedChar, keyCode);
        }
        else if (keyCode == Keyboard.KEY_RETURN)
        {
            AbstractEntry entry = this.picker.getCurrent();

            if (entry instanceof FolderEntry)
            {
                this.picker.setFolder((FolderEntry) entry);
            }
            else if (entry instanceof FileEntry)
            {
                this.selectCurrent(((FileEntry) entry).resource);
            }

            this.typed = "";
        }
        else if (keyCode == Keyboard.KEY_UP)
        {
            this.moveCurrent(-1, GuiScreen.isShiftKeyDown());
        }
        else if (keyCode == Keyboard.KEY_DOWN)
        {
            this.moveCurrent(1, GuiScreen.isShiftKeyDown());
        }
        else if (!this.pickByTyping(typedChar))
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void moveCurrent(int factor, boolean top)
    {
        int index = this.picker.current + factor;
        int length = this.picker.getList().size();

        if (index < 0) index = length - 1;
        else if (index >= length) index = 0;

        if (top) index = factor > 0 ? length - 1 : 0;

        this.picker.current = index;
        this.picker.scroll.scrollIntoView(index * this.picker.scroll.scrollItemSize);
        this.typed = "";
    }

    protected boolean pickByTyping(char typedChar)
    {
        if (!ChatAllowedCharacters.isAllowedCharacter(typedChar))
        {
            return false;
        }

        long diff = System.currentTimeMillis() - this.lastTyped;

        if (diff > 1000)
        {
            this.typed = "";
        }

        this.typed += Character.toString(typedChar);
        this.lastTyped = System.currentTimeMillis();

        for (AbstractEntry entry : this.picker.getList())
        {
            if (entry.title.startsWith(this.typed))
            {
                this.picker.setCurrentScroll(entry);

                return true;
            }
        }

        return false;
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        /* Refresh the list */
        long time = System.currentTimeMillis();

        if (this.lastChecked + 1000 < time)
        {
            this.lastChecked = time;

            FolderEntry folder = this.picker.parent;

            if (folder != null && folder.isTop())
            {
                folder = folder.top;
            }

            if (folder != null && folder.hasChanged())
            {
                this.picker.setDirectFolder(folder);
            }
        }

        /* Draw the GUI */
        drawGradientRect(this.area.x, this.area.y, this.area.getX(1), this.area.getY(1), 0x88000000, 0xff000000);

        if (this.multiList.isVisible())
        {
            this.mc.renderEngine.bindTexture(GuiBase.ICONS);
            GuiUtils.drawContinuousTexturedBox(this.area.x, this.area.y, 0, 32, 110, this.area.h, 32, 32, 0, 0);
            drawRect(this.area.x, this.area.y, this.area.x + 110, this.area.y + 30, 0x44000000);
            drawGradientRect(this.area.x, this.area.getY(1) - 20, this.area.x + 110, this.area.getY(1), 0x00, 0x44000000);
        }

        if (this.picker.getList().isEmpty())
        {
            this.drawCenteredString(this.font, I18n.format("mclib.gui.no_data"), this.area.getX(0.5F), this.area.getY(0.5F), 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (System.currentTimeMillis() - this.lastTyped < 1000)
        {
            int w = this.font.getStringWidth(this.typed);
            int x = this.text.area.x;
            int y = this.text.area.getY(1);

            Gui.drawRect(x, y, x + w + 4, y + 4 + this.font.FONT_HEIGHT, 0x880088ff);
            this.font.drawStringWithShadow(this.typed, x + 2, y + 2, 0xffffff);
        }

        ResourceLocation loc = this.current;

        /* Draw preview */
        if (loc != null)
        {
            this.mc.renderEngine.bindTexture(loc);

            int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
            int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

            int x = this.area.getX(1);
            int y = this.area.getY(1);
            int fw = w;
            int fh = h;

            if (fw > 128 || fh > 128)
            {
                fw = fh = 128;

                if (w > h)
                {
                    fh = (int) ((h / (float) w) * fw);
                }
                else if (h > w)
                {
                    fw = (int) ((w / (float) h) * fh);
                }
            }

            x -= fw + 10;
            y -= fh + 40;

            this.mc.renderEngine.bindTexture(GuiBase.ICONS);
            GuiUtils.drawContinuousTexturedBox(x, y, 0, 96, fw, fh, 32, 32, 0, 0);
            this.mc.renderEngine.bindTexture(loc);

            GlStateManager.enableAlpha();
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(x, y + fh, 0.0D).tex(0, 1).endVertex();
            vertexbuffer.pos(x + fw, y + fh, 0.0D).tex(1, 1).endVertex();
            vertexbuffer.pos(x + fw, y, 0.0D).tex(1, 0).endVertex();
            vertexbuffer.pos(x, y, 0.0D).tex(0, 0).endVertex();
            tessellator.draw();
        }
    }
}