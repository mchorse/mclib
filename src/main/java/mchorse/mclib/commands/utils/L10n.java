package mchorse.mclib.commands.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * Localization utils
 *
 * This class provides shortcuts for sending messages to players. Pretty tired
 * of typing a lot of characters with provided API.
 *
 * API should be clear, short and concise.
 */
public class L10n
{
    public static String ERROR_MARKER = "§4(§cX§4)§r ";
    public static String SUCCESS_MARKER = "§2(§aV§2)§r ";
    public static String INFO_MARKER = "§9(§bi§9)§r ";

    private final String id;

    public L10n(String id)
    {
        this.id = id;
    }

    /**
     * Send a translated message to player
     */
    public void send(ICommandSender sender, String key, Object... objects)
    {
        sender.sendMessage(new TextComponentTranslation(key, objects));
    }

    /**
     * Send a translated message to player
     */
    public void sendColored(ICommandSender sender, TextFormatting color, String key, Object... objects)
    {
        ITextComponent text = new TextComponentTranslation(key, objects);
        text.getStyle().setColor(color);

        sender.sendMessage(text);
    }

    /**
     * Send error message to the sender
     */
    public void error(ICommandSender sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, ERROR_MARKER, this.id + ".error." + key, objects);
    }

    /**
     * Get error message
     */
    public ITextComponent error(String key, Object... objects)
    {
        return this.messageWithMarker(ERROR_MARKER, this.id + ".error." + key, objects);
    }

    /**
     * Send success message to the sender
     */
    public void success(ICommandSender sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, SUCCESS_MARKER, this.id + ".success." + key, objects);
    }

    /**
     * Get success message
     */
    public ITextComponent success(String key, Object... objects)
    {
        return this.messageWithMarker(SUCCESS_MARKER, this.id + ".success." + key, objects);
    }

    /**
     * Send informing message to the sender
     */
    public void info(ICommandSender sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, INFO_MARKER, this.id + ".info." + key, objects);
    }

    /**
     * Get informing message
     */
    public ITextComponent info(String key, Object... objects)
    {
        return  this.messageWithMarker(INFO_MARKER, this.id + ".info." + key, objects);
    }

    /**
     * Send a message with given marker
     */
    public void sendWithMarker(ICommandSender sender, String marker, String key, Object... objects)
    {
        sender.sendMessage(this.messageWithMarker(marker, key, objects));
    }

    public ITextComponent messageWithMarker(String marker, String key, Object... objects)
    {
        ITextComponent message = new TextComponentString(marker);
        ITextComponent string = new TextComponentTranslation(key, objects);

        string.getStyle().setColor(TextFormatting.GRAY);

        message.appendSibling(string);

        return message;
    }
}