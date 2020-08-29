package mchorse.mclib.client.gui.framework.elements.input;

import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.input.multiskin.GuiMultiSkinEditor;
import mchorse.mclib.client.gui.framework.elements.list.GuiFolderEntryListElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiResourceLocationListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Timer;
import mchorse.mclib.utils.files.FileTree;
import mchorse.mclib.utils.files.GlobalTree;
import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FileEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;
import mchorse.mclib.utils.resources.FilteredResourceLocation;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.function.Consumer;

/**
 * Texture picker GUI
 * 
 * This bad boy allows picking a texture from the file browser, and also 
 * it allows creating multi-skins. See {@link MultiResourceLocation} for 
 * more information.
 */
public class GuiTexturePicker extends GuiElement
{
    public GuiElement right;
    public GuiTextElement text;
    public GuiButtonElement close;
    public GuiButtonElement folder;
    public GuiFolderEntryListElement picker;

    public GuiButtonElement multi;
    public GuiResourceLocationListElement multiList;
    public GuiMultiSkinEditor editor;

    public GuiElement buttons;
    public GuiIconElement add;
    public GuiIconElement remove;
    public GuiIconElement edit;

    public Consumer<ResourceLocation> callback;

    public MultiResourceLocation multiRL;
    public ResourceLocation current;
    public FileTree tree = GlobalTree.TREE;

    private Timer lastTyped = new Timer(1000);
    private Timer lastChecked = new Timer(1000);
    private String typed = "";

    public GuiTexturePicker(Minecraft mc, Consumer<ResourceLocation> callback)
    {
        super(mc);

        this.right = new GuiElement(mc);
        this.text = new GuiTextElement(mc, 1000, (str) -> this.selectCurrent(str.isEmpty() ? null : RLUtils.create(str)));
        this.close = new GuiButtonElement(mc, IKey.str("X"), (b) -> this.close());
        this.folder = new GuiButtonElement(mc, IKey.lang("mclib.gui.open_folder"), (b) -> this.openFolder());
        this.picker = new GuiFolderEntryListElement(mc, (entry) ->
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
        this.picker.cancelScrollEdge();

        this.multi = new GuiButtonElement(mc, IKey.lang("mclib.gui.multi_skin"), (b) -> this.toggleMultiSkin());
        this.multiList = new GuiResourceLocationListElement(mc, (list) -> this.displayCurrent(list.get(0)));
        this.editor = new GuiMultiSkinEditor(mc, this);
        this.editor.setVisible(false);

        this.buttons = new GuiElement(mc);
        this.add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addMultiSkin());
        this.remove = new GuiIconElement(mc, Icons.REMOVE, (b) -> this.removeMultiSkin());
        this.edit = new GuiIconElement(mc, Icons.EDIT, (b) -> this.toggleEditor());

        this.right.flex().relative(this).wh(1F, 1F);
        this.text.flex().relative(this.multi).x(1F, 20).wTo(this.close.flex(), -5).h(20);
        this.close.flex().relative(this).set(0, 10, 20, 20).x(1, -30);
        this.folder.flex().relative(this.right).set(0, 0, 80, 20).x(1, -10).y(1, -10).anchor(1F, 1F);
        this.picker.flex().relative(this.right).set(10, 30, 0, 0).w(1, -10).h(1, -30);

        this.multi.flex().relative(this).set(10, 10, 100, 20);
        this.multiList.flex().set(10, 35, 100, 0).relative(this).hTo(this.buttons.flex());
        this.editor.flex().set(120, 0, 0, 0).w(1F, -120).h(1F);

        this.buttons.flex().relative(this).y(1F, -20).wTo(this.right.area).h(20);
        this.add.flex().relative(this.buttons).set(0, 0, 20, 20);
        this.remove.flex().relative(this.add).set(20, 0, 20, 20);
        this.edit.flex().relative(this.buttons).wh(20, 20).x(1F, -20);

        this.right.add(this.text, this.picker, this.folder);
        this.buttons.add(this.add, this.remove, this.edit);
        this.add(this.multi, this.multiList, this.close, this.right, this.editor, this.buttons);

        this.callback = callback;

        this.fill(null);
        this.markContainer();
    }

    public void close()
    {
        this.removeFromParent();

        if (this.callback != null)
        {
            this.callback.accept(this.multiRL != null ? this.multiRL : this.current);
        }
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
        this.multiList.setIndex(this.multiList.getList().size() - 1);
        this.displayCurrent(this.multiList.getCurrent().get(0));
    }

    /**
     * Remove currently selected {@link ResourceLocation} from multiRL 
     */
    private void removeMultiSkin()
    {
        int index = this.multiList.getIndex();

        if (index >= 0 && this.multiList.getList().size() > 1)
        {
            this.multiList.getList().remove(index);
            this.multiList.update();
            this.multiList.setIndex(index - 1);

            if (this.multiList.getIndex() >= 0)
            {
                this.displayCurrent(this.multiList.getCurrent().get(0));
            }
        }
    }

    private void toggleEditor()
    {
        this.editor.toggleVisible();
        this.right.setVisible(!this.editor.isVisible());
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
            if (rl != null)
            {
                this.mc.renderEngine.bindTexture(rl);
            }
        }
        catch (Exception e)
        {
            return;
        }

        if (this.current instanceof FilteredResourceLocation)
        {
            FilteredResourceLocation location = (FilteredResourceLocation) this.current;

            location.set(rl.toString());
            this.picker.rl = location;
        }
        else
        {
            this.current = rl;
            this.picker.rl = rl;

            if (this.multiRL != null)
            {
                int index = this.multiList.getIndex();

                if (index != -1 && rl != null)
                {
                    this.multiList.getList().set(index, rl);
                }

                rl = this.multiRL;
            }
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

            this.multiList.setIndex(this.multiRL.children.isEmpty() ? -1 : 0);;
            this.multiList.setList(this.multiRL.children);

            if (this.current != null)
            {
                this.multiList.setIndex(0);
            }

            this.right.flex().x(120).w(1F, -120);
        }
        else
        {
            this.multiRL = null;

            this.right.flex().x(0).w(1F);
            this.displayCurrent(skin);
        }

        if (notify)
        {
            if (show) if (this.callback != null) this.callback.accept(skin);
            else this.selectCurrent(skin);
        }

        this.multiList.setVisible(show);
        this.buttons.setVisible(show);

        this.resize();
        this.updateFolderButton();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        /* Necessary measure to avoid triggering buttons when you press 
         * on a text field, for example */
        return super.mouseClicked(context) || (this.isVisible() && this.area.isInside(context));
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        int keyCode = context.keyCode;

        if (keyCode == Keyboard.KEY_RETURN)
        {
            List<AbstractEntry> selected = this.picker.getCurrent();
            AbstractEntry entry = selected.isEmpty() ? null : selected.get(0);

            if (entry instanceof FolderEntry)
            {
                this.picker.setFolder((FolderEntry) entry);
            }
            else if (entry instanceof FileEntry)
            {
                this.selectCurrent(((FileEntry) entry).resource);
            }

            this.typed = "";

            return true;
        }
        else if (keyCode == Keyboard.KEY_UP)
        {
            return this.moveCurrent(-1, GuiScreen.isShiftKeyDown());
        }
        else if (keyCode == Keyboard.KEY_DOWN)
        {
            return this.moveCurrent(1, GuiScreen.isShiftKeyDown());
        }
        else if (keyCode == Keyboard.KEY_ESCAPE)
        {
            this.close();

            return true;
        }

        return this.pickByTyping(context.typedChar);
    }

    protected boolean moveCurrent(int factor, boolean top)
    {
        int index = this.picker.getIndex() + factor;
        int length = this.picker.getList().size();

        if (index < 0) index = length - 1;
        else if (index >= length) index = 0;

        if (top) index = factor > 0 ? length - 1 : 0;

        this.picker.setIndex(index);
        this.picker.scroll.scrollIntoView(index * this.picker.scroll.scrollItemSize);
        this.typed = "";

        return true;
    }

    protected boolean pickByTyping(char typedChar)
    {
        if (!ChatAllowedCharacters.isAllowedCharacter(typedChar))
        {
            return false;
        }

        if (this.lastTyped.checkReset())
        {
            this.typed = "";
        }

        this.typed += Character.toString(typedChar);
        this.lastTyped.mark();

        for (AbstractEntry entry : this.picker.getList())
        {
            if (entry.title.startsWith(this.typed))
            {
                this.picker.setCurrentScroll(entry);
            }
        }

        return true;
    }

    @Override
    public void draw(GuiContext context)
    {
        /* Refresh the list */
        if (this.lastChecked.checkRepeat())
        {
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

        /* Draw the background */
        drawGradientRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0x88000000, 0xff000000);

        if (this.multiList.isVisible())
        {
            drawRect(this.area.x, this.area.y, this.area.x + 120, this.area.ey(), 0xff181818);
            drawRect(this.area.x, this.area.y, this.area.x + 120, this.area.y + 30, 0x44000000);
            drawGradientRect(this.area.x, this.area.ey() - 20, this.buttons.area.ex(), this.area.ey(), 0x00, 0x88000000);
        }

        if (this.editor.isVisible())
        {
            this.edit.area.draw(0x88000000 + McLib.primaryColor.get());
        }

        super.draw(context);

        /* Draw the overlays */
        if (this.right.isVisible())
        {
            if (this.picker.getList().isEmpty())
            {
                this.drawCenteredString(this.font, I18n.format("mclib.gui.no_data"), this.picker.area.mx(), this.picker.area.my() - 8, 0xffffff);
            }

            if (!this.lastTyped.check() && this.lastTyped.enabled)
            {
                int w = this.font.getStringWidth(this.typed);
                int x = this.text.area.x;
                int y = this.text.area.ey();

                Gui.drawRect(x, y, x + w + 4, y + 4 + this.font.FONT_HEIGHT, 0x88000000 + McLib.primaryColor.get());
                this.font.drawStringWithShadow(this.typed, x + 2, y + 2, 0xffffff);
            }

            ResourceLocation loc = this.current;

            /* Draw preview */
            if (loc != null)
            {
                GlStateManager.color(1, 1, 1, 1);
                this.mc.renderEngine.bindTexture(loc);

                int w = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                int h = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

                int x = this.area.ex();
                int y = this.area.ey();
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
                y -= fh + 35;

                Icons.CHECKBOARD.renderArea(x, y, fw, fh);

                GlStateManager.enableAlpha();
                this.mc.renderEngine.bindTexture(loc);
                GuiDraw.drawBillboard(x, y, 0, 0, fw, fh, fw, fh);
            }
        }
    }
}