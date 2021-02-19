package mchorse.mclib.commands.config;

import mchorse.mclib.McLib;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.config.ConfigManager;
import mchorse.mclib.config.values.IConfigValue;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SubCommandConfigSet extends SubCommandConfigBase
{
    @Override
    public String getName()
    {
        return "set";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "mclib.commands.mclib.config.set";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}mclib {8}config set{r} {7}<mod.category.option> <value...>{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        IConfigValue value = SubCommandConfig.get(args[0]);

        if (!value.isClientSide())
        {
            String command = String.join(" ", SubCommandBase.dropFirstArguments(args, 1));

            if (value.parseFromCommand(command))
            {
                if (value.isSyncable())
                {
                    String mod = args[0].substring(0, args[0].indexOf("."));

                    ConfigManager.synchronizeConfig(McLib.proxy.configs.modules.get(mod), server);
                }

                this.getL10n().info(sender, "config.set", args[0], value.toString());
            }
            else
            {
                throw new CommandException("config.invalid_format", args[0], args[1]);
            }
        }
        else
        {
            this.getL10n().info(sender, "config.client_side", args[0]);
        }
    }
}
