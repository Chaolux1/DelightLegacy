package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RiceRollMedleyBlock extends FeastBlock {
    private static final List<Supplier<Item>> ROLL_SERVINGS= Arrays.<Supplier<Item>>asList(() -> ModItems.COD_ROLL.getValue(),() -> ModItems.COD_ROLL.getValue(),() -> ModItems.SALMON_ROLL.getValue(),() -> ModItems.SALMON_ROLL.getValue(),() -> ModItems.SALMON_ROLL.getValue(),() -> ModItems.KELP_ROLL_SLICE.getValue(),() -> ModItems.KELP_ROLL_SLICE.getValue(),() -> ModItems.KELP_ROLL_SLICE.getValue());
    public RiceRollMedleyBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties,() -> ModItems.SALMON_ROLL.getValue(),true,false);
    }

    @Override
    public int getMaxServings() {
        return 8;
    }

    @Override
    public ItemStack getServingItem(int value) {
        if(value < 1 || value > ROLL_SERVINGS.size()) return null;
        return new ItemStack(ROLL_SERVINGS.get(value - 1).get());
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess,int x,int y,int z) {
        int value=getValue(iBlockAccess,x,y,z);
        if((value & 1) == 0) {
            setBlockBounds(2.0f / 16.0f,0.0f,1.0f / 16.0f,14.0f / 16.0f,3.0f / 16.0f,15.0f / 16.0f);
        } else {
            setBlockBounds(1.0f / 16.0f,0.0f,2.0f / 16.0f,15.0f / 16.0f,3.0f / 16.0f,14.0f / 16.0f);
        }
    }
}
