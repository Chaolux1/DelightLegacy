package net.chaolux.delightlegacy.legacy.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class LegacyBlockProperties {
    private final Material material;
    private Float strength;
    private Block.SoundType soundType;
    private boolean randomTicks;
    private Integer lightLevel;
    private LegacyBlockProperties(Material material) {
        if(material == null) throw new IllegalArgumentException("Material cannot be null");
        this.material=material;
    }

    public static LegacyBlockProperties copy(Material material) {
        return new LegacyBlockProperties(material);
    }

    public LegacyBlockProperties strength(float strength) {
        this.strength=strength;
        return this;
    }

    public LegacyBlockProperties sound(Block.SoundType soundType) {
        this.soundType=soundType;
        return this;
    }

    public LegacyBlockProperties randomTicks() {
        this.randomTicks=true;
        return this;
    }

    public LegacyBlockProperties lightLevel(int lightLevel) {
        this.lightLevel=lightLevel;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public void from(Block block) {
        if(strength != null) block.setHardness(strength);
        if(soundType != null) block.setStepSound(soundType);
        if(randomTicks) block.setTickRandomly(true);
        if(lightLevel != null) block.setLightLevel(lightLevel / 15.0f);
    }
}
