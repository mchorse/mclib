package mchorse.mclib.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(World worldIn)
    {
        super(worldIn);

        this.right = new ItemStack(Items.diamond_sword);
        this.left = new ItemStack(Items.golden_sword);
        this.held = new ItemStack[] {null, null, null, null, null, null};
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
        }
        else
        {
            this.held[0] = null;
        }
    }

    @Override
    public ItemStack getHeldItem()
    {
        return this.held[0];
    }

    @Override
    public ItemStack getEquipmentInSlot(int slotIn)
    {
        return this.held[slotIn];
    }

    @Override
    public ItemStack getCurrentArmor(int slotIn)
    {
        return this.held[1 + slotIn];
    }

    @Override
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack)
    {
        this.held[slotIn] = stack;
    }

    @Override
    public ItemStack[] getInventory()
    {
        return this.held;
    }
}