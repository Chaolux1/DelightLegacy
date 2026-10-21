package net.chaolux.delightlegacy.legacy.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.chaolux.delightlegacy.legacy.assets.models.LegacyModelManager;
import net.minecraft.block.BlockFarmland;
import net.minecraft.util.IIcon;

public class LegacyFarmlandBlock extends BlockFarmland {
    protected LegacyFarmlandBlock(LegacyBlockProperties legacyBlockProperties) {
        super();
        legacyBlockProperties.from(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int data) {
        return LegacyModelManager.getInstance().getParticle(this,data);
    }
}
