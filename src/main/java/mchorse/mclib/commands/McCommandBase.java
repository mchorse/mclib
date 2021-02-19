package mchorse.mclib.commands;

import com.google.common.collect.ImmutableList;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * McHorse's base command class
 *
 * This command class is responsible for catching {@link CommandException}s and
 * output them as my error styled messages sent via {@link L10n}. This class
 * is also frees the check for required arguments (which is very often
 * redundant if for args.length).
 */
public abstract class McCommandBase extends CommandBase
{
    public static final List<String> BOOLEANS = ImmutableList.of("true", "false", "1", "0");

    public static String processSyntax(String str)
    {
        return str.replaceAll("\\{([\\w\\d_]+)\\}", "§$1");
    }

    public abstract L10n getL10n();

    public abstract String getSyntax();

    public String getProcessedSyntax()
    {
        return processSyntax(this.getSyntax());
    }

    public ITextComponent getUsageMessage(ICommandSender sender)
    {
        ITextComponent message = new TextComponentTranslation(this.getProcessedSyntax());

        message.getStyle().setColor(TextFormatting.WHITE);

        return message
            .appendSibling(new TextComponentString("\n\n"))
            .appendSibling(new TextComponentTranslation(this.getUsage(sender)));
    }

    /**
     * Get the count of arguments which are required
     */
    public int getRequiredArgs()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && args[0].equals("-h"))
        {
            throw new WrongUsageException("mclib.commands.wrapper", this.getUsageMessage(sender));
        }

        if (args.length < this.getRequiredArgs())
        {
            throw new WrongUsageException("mclib.commands.wrapper", this.getUsageMessage(sender));
        }

        try
        {
            this.executeCommand(server, sender, args);
        }
        catch (CommandException e)
        {
            if (e.getMessage().startsWith("commands."))
            {
                throw e;
            }

            throw new CommandException("mclib.commands.wrapper", this.getL10n().error(e.getMessage(), e.getErrorObjects()));
        }
    }

    /**
     * Execute the command's task
     */
    public abstract void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
}