package mchorse.mclib.utils;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

/**
 * Dummy entity
 *
 * This class is used in model editor as a player substitution for the model
 * methods.
 */
public class DummyEntity extends EntityLivingBase
{
    private final ItemStack[] held;
    private final List<ItemStack> emptyList;
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(World worldIn)
    {
        super(worldIn);

        this.right = new ItemStack(Items.DIAMOND_SWORD);
        this.left = new ItemStack(Items.GOLDEN_SWORD);
        this.emptyList = ImmutableList.of();

        this.held = new ItemStack[] {null, null};
    }

    public void setItems(ItemStack left, ItemStack right)
    {
        this.left = left;
        this.right = right;
    }

    public void toggleItems(boolean toggle)
    {
        if (toggle)
        {
            this.held[0] = this.right;
            this.held[1] = this.left;
        }
        else
        {
            this.held[0] = this.held[1] = null;
        }
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return this.emptyList;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        if (slotIn.equals(EntityEquipmentSlot.MAINHAND))
        {
            return this.held[0];
        }
        else if (slotIn.equals(EntityEquipmentSlot.OFFHAND))
        {
            return this.held[1];
        }

        return null;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {}

    @Override
    public EnumHandSide getPrimaryHand()
    {
        return EnumHandSide.RIGHT;
    }
}