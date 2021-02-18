package mchorse.mclib.commands;

import mchorse.mclib.McLib;
import mchorse.mclib.commands.config.SubCommandConfig;
import mchorse.mclib.commands.utils.L10n;

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
    protected String getHelp()
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
