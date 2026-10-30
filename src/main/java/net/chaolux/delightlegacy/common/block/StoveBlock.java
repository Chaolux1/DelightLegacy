package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.common.block.entity.StoveBlockEntity;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StoveBlock extends AbstractStoveBlock {
    public StoveBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new StoveBlockEntity();
    }
}
