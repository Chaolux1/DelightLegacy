package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.legacy.common.block.LegacyCropBlock;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.item.Item;

public class CabbageBlock extends LegacyCropBlock {
    private static final float[] STAGE= {2.0f / 16.0f,3.0f / 16.0f,5.0f / 16.0f,7.0f / 16.0f,8.0f / 16.0f,9.0f / 16.0f,9.0f / 16.0f,10.0f / 16.0f};
    public CabbageBlock() {
        super(7);
    }

    @Override
    protected Item getSeed() {
        return ModItems.CABBAGE_SEEDS.getValue();
    }

    @Override
    protected Item getCrop() {
        return ModItems.CABBAGE.getValue();
    }

    @Override
    protected float getStage(int stage) {
        return STAGE[Math.max(0,Math.min(stage,STAGE.length - 1))];
    }
}
