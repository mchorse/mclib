package mchorse.mclib.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

/**
 * Dummy entity
 *
 * This class is used in model editor as a player substitution for the model
 * methods.
 */
public class DummyEntity extends EntityLivingBase
{
    private final ItemStack[] held;
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(World worldIn)
    {
        super(worldIn);

        this.right = new ItemStack(Items.DIAMOND_SWORD);
        this.left = new ItemStack(Items.GOLDEN_SWORD);
        this.held = new ItemStack[6];
    }

    public void setItems(ItemStack left, ItemStack right)
    {
        this.left = left;
        this.right = right;
    }

    public void toggleItems(boolean toggle)
    {
        int main = EntityEquipmentSlot.MAINHAND.getSlotIndex();
        int off = EntityEquipmentSlot.OFFHAND.getSlotIndex();

        if (toggle)
        {
            this.held[main] = this.right;
            this.held[off] = this.left;
        }
        else
        {
            this.held[main] = this.held[off] = null;
        }
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return (Iterable<ItemStack>) Arrays.asList(this.held).iterator();
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        return this.held[slotIn.getSlotIndex()];
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {
        this.held[slotIn.getSlotIndex()] = stack;
    }

    @Override
    public EnumHandSide getPrimaryHand()
    {
        return EnumHandSide.RIGHT;
    }
}