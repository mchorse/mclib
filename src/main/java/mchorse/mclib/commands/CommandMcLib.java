package mchorse.mclib.commands;

import mchorse.mclib.McLib;
import mchorse.mclib.commands.config.SubCommandConfig;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.command.ICommandSender;

public class CommandMcLib extends SubCommandBase
{
    public CommandMcLib()
    {
        this.add(new SubCommandConfig());
    }

    @Override
    public String getName()
    {
        return "mclib";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "mclib.commands.mclib.help";
    }

    @Override
    public String getSyntax()
    {
        return "";
    }

    @Override
    public L10n getL10n()
    {
        return McLib.l10n;
    }
}
