package mchorse.mclib.commands.config;

import mchorse.mclib.McLib;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.L10n;
import mchorse.mclib.config.Config;
import mchorse.mclib.config.values.Value;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class SubCommandConfigBase extends McCommandBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public L10n getL10n()
    {
        return McLib.l10n;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            List<String> ids = new ArrayList<>();

            for (Config config : McLib.proxy.configs.modules.values())
            {
                for (Value category : config.values.values())
                {
                    for (Value value : category.getSubValues())
                    {
                        if (value.isClientSide())
                        {
                            continue;
                        }

                        ids.add(config.id + "." + category.id + "." + value.id);
                    }
                }
            }

            return getListOfStringsMatchingLastWord(args, ids);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
