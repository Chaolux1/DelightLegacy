package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyCropBlock;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;

public class OnionBlock extends LegacyCropBlock {
    private static final float[] STAGE= {2.0f / 16.0f,3.0f / 16.0f,4.0f / 16.0f,5.0f / 16.0f,6.0f / 16.0f,7.0f / 16.0f,8.0f / 16.0f,9.0f / 16.0f};
    public OnionBlock() {
        super(7);
    }

    @Override
    protected Item getSeed() {
        return ModItems.ONION.getValue();
    }

    @Override
    protected Item getCrop() {
        return ModItems.ONION.getValue();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world,int x,int y,int z,int data,int fortune) {
        ArrayList<ItemStack> itemStacks=new ArrayList<ItemStack>();
        int value=1;
        if(isMaxAge(data)) {
            for (int index=0;index < 3 + Math.max(0,fortune);index++) {
                if(world.rand.nextInt(7) < 4) value++;
            }
        }
        itemStacks.add(new ItemStack(ModItems.ONION.getValue(),value,0));
        return itemStacks;
    }

    @Override
    protected float getStage(int stage) {
        return STAGE[Math.max(0,Math.min(stage,STAGE.length - 1))];
    }
}
