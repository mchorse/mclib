package mchorse.mclib.client.gui.utils;

import mchorse.mclib.McLib;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextField
{
    private String text = "";
    private Consumer<String> callback;

    private int cursor;
    private int selection = -1;
    private int left;
    private int right;

    private Predicate<String> validator;
    private int length = 40;
    private boolean focused;
    private boolean enabled = true;
    private boolean visible = true;

    private boolean background = true;
    private int color = 0xffffff;

    private boolean holding;
    private int lastX;

    public Area area = new Area();
    public FontRenderer font;

    private int lastW;

    public TextField(FontRenderer font, Consumer<String> callback)
    {
        this.callback = callback;
        this.font = font;
    }

    /* Text */

    public String getSelectedText()
    {
        if (this.isSelected())
        {
            int min = Math.min(this.cursor, this.selection);
            int max = Math.max(this.cursor, this.selection);

            return this.text.substring(min, max);
        }

        return "";
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        if (text.length() > this.length)
        {
            text = text.substring(0, this.length);
        }

        this.text = text;

        this.moveCursorToStart();
        this.clearSelection();
        this.updateBounds(false);
    }

    public void acceptText()
    {
        if (this.callback != null)
        {
            this.callback.accept(this.text);
        }
    }

    public void insert(String text)
    {
        this.deleteSelection();

        text = text.replaceAll("\n", "");

        int i = this.text.length() + text.length();

        if (i >= this.length)
        {
            text = text.substring(0, this.length - this.text.length());
        }

        if (text.isEmpty())
        {
            return;
        }

        String newText = this.text;

        if (this.cursor == 0)
        {
            newText = text + newText;
        }
        else if (this.cursor >= newText.length())
        {
            newText += text;
        }
        else
        {
            newText = newText.substring(0, this.cursor) + text + newText.substring(this.cursor);
        }

        this.text = newText;
        this.moveCursorTo(this.cursor + text.length());

        this.updateBounds(false);
    }

    public void deleteCharacter()
    {
        if (this.cursor > 0)
        {
            this.text = this.text.substring(0, this.cursor - 1) + this.text.substring(this.cursor);
            this.moveCursorBy(-1);
        }
    }

    public void setValidator(Predicate<String> validator)
    {
        this.validator = validator;
    }

    public void setLength(int length)
    {
        if (this.text.length() > length)
        {
            this.text = this.text.substring(0, length);
            this.updateBounds(false);
        }

        this.length = length;
    }

    /* Cursor, selection and offsets */

    public void moveCursorTo(int cursor)
    {
        this.cursor = cursor;

        this.updateBounds(false);
    }

    public void moveCursorToStart()
    {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd()
    {
        this.moveCursorTo(this.text.length());
    }

    private void moveCursorBy(int i)
    {
        this.cursor += (int) Math.copySign(1, i);
        this.cursor = MathUtils.clamp(this.cursor, 0, this.text.length());

        this.updateBounds(false);
    }

    public boolean isSelected()
    {
        return this.selection != this.cursor && this.selection >= 0;
    }

    public void setSelection(int selection)
    {
        this.selection = selection;

        this.updateBounds(true);
    }

    public void clearSelection()
    {
        this.selection = -1;
    }

    public void deleteSelection()
    {
        if (this.cursor == this.selection)
        {
            this.clearSelection();
        }

        if (!this.isSelected())
        {
            return;
        }

        int min = Math.min(this.cursor, this.selection);
        int max = Math.max(this.cursor, this.selection);

        this.text = this.text.substring(0, min) + this.text.substring(max);

        this.clearSelection();
        this.updateBounds(false);
        this.clamp();
    }

    private void updateBounds(boolean selection)
    {
        int cursor = selection ? this.selection : this.cursor;
        int length = this.text.length();
        int offset = this.background ? 10 : 0;
        int max = this.area.w - offset;

        if (this.font.getStringWidth(this.text) < max)
        {
            this.left = 0;
            this.right = length;

            return;
        }

        if (cursor < this.left)
        {
            int bound = this.getBound(max, cursor, 1);

            if (bound == cursor)
            {
                bound = this.getBound(max, length - 1, -1);

                this.left = bound;
                this.right = length;
            }
            else
            {
                this.left = cursor;
                this.right = MathUtils.clamp(bound + 1, 0, length);
            }
        }
        else if (cursor >= this.right)
        {
            int bound = this.getBound(max, MathUtils.clamp(cursor, 0, length - 1), -1);

            if (bound == cursor)
            {
                bound = this.getBound(max, 0, 1);

                this.left = 0;
                this.right = bound;
            }
            else
            {
                this.left = bound;
                this.right = cursor;
            }
        }

        this.left = MathUtils.clamp(this.left, 0, length);
        this.right = MathUtils.clamp(this.right, 0, length);
    }

    private int getBound(int max, int start, int direction)
    {
        int w = 0;

        for (int i = start; i >= 0 && i < this.text.length(); i += direction)
        {
            int sw = this.font.getCharWidth(this.text.charAt(i));

            if (w < max && w + sw >= max)
            {
                return i;
            }

            w += sw;
        }

        return start;
    }

    private void clamp()
    {
        this.cursor = MathUtils.clamp(this.cursor, 0, this.text.length());
        this.selection = MathUtils.clamp(this.selection, -1, this.text.length());
    }

    private String getWrappedText()
    {
        int length = this.text.length();

        return this.text.substring(
            MathUtils.clamp(this.left, 0, length),
            MathUtils.clamp(this.right, 0, length)
        );
    }

    /* Visual */

    public boolean hasBackground()
    {
        return this.background;
    }

    public void setBackground(boolean background)
    {
        this.background = background;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public boolean isVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /* Input handling */

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isFocused()
    {
        return this.focused;
    }

    public void setFocused(boolean focused)
    {
        this.focused = focused;
    }

    public void mouseClicked(int x, int y, int button)
    {
        if (button == 0 && this.area.isInside(x, y))
        {
            this.clearSelection();

            if (GuiScreen.isShiftKeyDown())
            {
                this.selection = this.cursor;
            }

            this.focused = true;
            this.lastX = x;
            this.holding = true;
            this.moveCursorTo(this.getIndexAt(x));
        }
        else
        {
            this.focused = false;
        }
    }

    public void mouseReleased(int x, int y, int button)
    {
        if (button == 0)
        {
            this.holding = false;
        }
    }

    private int getIndexAt(int x)
    {
        x -= this.area.x;

        if (this.background)
        {
            x -= 4;
        }

        if (x >= 0)
        {
            String wrappedText = this.getWrappedText();
            int w = this.font.getStringWidth(wrappedText);

            if (x >= w)
            {
                return this.right;
            }
            else
            {
                w = 0;

                for (int i = 0, c = wrappedText.length(); i < c; i++)
                {
                    int string = this.font.getCharWidth(wrappedText.charAt(i));

                    if (x >= w && x < w + string)
                    {
                        return this.left + i;
                    }

                    w += string;
                }
            }
        }

        return this.left;
    }

    public boolean keyTyped(char typedChar, int keyCode)
    {
        if (!this.focused || !this.enabled || !this.visible)
        {
            return false;
        }

        boolean selecting = this.isSelected();
        boolean ctrl = GuiScreen.isCtrlKeyDown();
        boolean shift = GuiScreen.isShiftKeyDown();

        if (ctrl && (keyCode == Keyboard.KEY_C || keyCode == Keyboard.KEY_X))
        {
            if (selecting)
            {
                GuiScreen.setClipboardString(this.getSelectedText());

                if (keyCode == Keyboard.KEY_X)
                {
                    this.deleteSelection();
                }

                return true;
            }
        }
        else if (ctrl && keyCode == Keyboard.KEY_V)
        {
            String clipboard = GuiScreen.getClipboardString();

            if (!clipboard.isEmpty())
            {
                this.insert(clipboard);
                this.acceptText();
            }

            return true;
        }
        else if (ctrl && keyCode == Keyboard.KEY_A)
        {
            this.selection = 0;
            this.cursor = this.text.length();
            this.updateBounds(false);

            return true;
        }
        else if (keyCode == Keyboard.KEY_HOME)
        {
            this.handleShift(shift);
            this.moveCursorToStart();
        }
        else if (keyCode == Keyboard.KEY_END)
        {
            this.handleShift(shift);
            this.moveCursorToEnd();
        }
        else if (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT)
        {
            this.handleShift(shift);
            this.moveCursorBy(keyCode == Keyboard.KEY_LEFT ? -1 : 1);
        }
        else if (keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE)
        {
            if (this.isSelected())
            {
                this.deleteSelection();
                this.acceptText();

                return true;
            }
            else if (keyCode == Keyboard.KEY_DELETE && this.cursor < this.text.length())
            {
                this.moveCursorBy(1);
                this.deleteCharacter();
                this.acceptText();

                return true;
            }
            else if (keyCode == Keyboard.KEY_BACK)
            {
                this.deleteCharacter();
                this.acceptText();

                return true;
            }
        }
        else if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
        {
            if (this.validator != null && !this.validator.test(String.valueOf(typedChar)))
            {
                return false;
            }

            this.insert(String.valueOf(typedChar));
            this.acceptText();

            return true;
        }

        return false;
    }

    private void handleShift(boolean shift)
    {
        if (shift)
        {
            if (this.selection == -1)
            {
                this.selection = cursor;
            }
        }
        else
        {
            this.clearSelection();
        }
    }

    /* Rendering */

    public void draw(int mouseX, int mouseY)
    {
        if (!this.visible)
        {
            return;
        }

        if (this.lastW != this.area.w)
        {
            this.lastW = this.area.w;
            this.updateBounds(false);
        }

        if (this.area.isInside(mouseX, mouseY) && this.holding && Math.abs(mouseX - this.lastX) > 2)
        {
            this.moveCursorTo(this.getIndexAt(mouseX));
        }

        int x = this.area.x;
        int y = this.area.y;

        if (this.background)
        {
            Gui.drawRect(this.area.x - 1, this.area.y - 1, this.area.ex() + 1, this.area.ey() + 1, 0xffaaaaaa);
            this.area.draw(0xff000000);

            x = this.area.x + 4;
            y = this.area.my() - this.font.FONT_HEIGHT / 2;
        }

        String text = this.getWrappedText();
        int length = text.length();

        if (this.isSelected())
        {
            int min = MathUtils.clamp(Math.min(this.cursor, this.selection) - this.left, 0, length);
            int max = MathUtils.clamp(Math.max(this.cursor, this.selection) - this.left, 0, length);

            int offset = this.font.getStringWidth(text.substring(0, min));
            int sx = x + offset;
            int sw = this.font.getStringWidth(text.substring(min, max));

            Gui.drawRect(sx - 2, y - 2, sx + sw + 2, y + this.font.FONT_HEIGHT + 2, 0x88000000 + McLib.primaryColor.get());
        }

        this.font.drawStringWithShadow(text, x, y, this.color);

        if (this.focused)
        {
            int relativeIndex = this.cursor - this.left;

            if (relativeIndex >= 0 && relativeIndex <= length)
            {
                x += this.font.getStringWidth(text.substring(0, relativeIndex));

                Gui.drawRect(x, y - 2, x + 1, y + this.font.FONT_HEIGHT, 0xffffffff);
            }
        }
    }
}