package net.chaolux.delightlegacy.common.block;

import net.chaolux.delightlegacy.common.block.entity.StoveBlockEntity;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.chaolux.delightlegacy.legacy.common.block.LegacyBlockProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class StoveBlock extends AbstractStoveBlock {
    public static final LegacyDataStateMapper STATE_MAPPER=variant();
    public StoveBlock(LegacyBlockProperties legacyBlockProperties) {
        super(legacyBlockProperties);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int value) {
        return new StoveBlockEntity();
    }

    private static LegacyDataStateMapper variant() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        String[] strings={"north","east","south","west"};
        for (int index=0;index < 8;index++) {
            builder.put(index,"facing",strings[index & FACING]);
            builder.put(index,"lit",String.valueOf((index & LIT) != 0));
        }
        return builder.build();
    }
}
