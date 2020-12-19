package mchorse.mclib.network.mclib.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.network.mclib.common.PacketDropItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class ServerHandlerDropItem extends ServerMessageHandler<PacketDropItem>
{
	@Override
	public void run(EntityPlayerMP player, PacketDropItem message)
	{
		if (player.isCreative())
		{
			ItemStack stack = message.stack;

			player.inventory.addItemStackToInventory(stack);
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 1F);
			player.inventoryContainer.detectAndSendChanges();
		}
	}
}