package net.chaolux.delightlegacy.legacy.registry;

import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyBlockStateMapper;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyDataStateMapper;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class LegacyBlockRegistryObject<T extends Block> {
    private String namespace;
    private final String string;
    private final Supplier<? extends T> supplier;
    private boolean tab;
    private ResourceLocation resourceLocation;
    private LegacyBlockStateMapper legacyBlockStateMapper;
    private T value;
    LegacyBlockRegistryObject(String namespace,String string,Supplier<? extends T> supplier,boolean tab) {
        this.namespace=namespace;
        this.string=string;
        this.supplier=supplier;
        this.tab=tab;
        this.resourceLocation= ResourceLocations.locations(namespace,string);
        this.legacyBlockStateMapper= LegacyDataStateMapper.builder().build();
    }

    public LegacyBlockRegistryObject<T> legacyBlockRegistryObject(String string) {
        if(string == null || string.isEmpty()) throw new IllegalArgumentException("Model cannot be empty");
        this.resourceLocation=ResourceLocations.parse(string);
        return this;
    }

    public LegacyBlockRegistryObject<T> legacyBlockRegistryObject(ResourceLocation resourceLocation) {
        if(resourceLocation == null) throw new IllegalArgumentException("Model cannot be null");
        this.resourceLocation=resourceLocation;
        return this;
    }

    public LegacyBlockRegistryObject<T> blockStateMapper(LegacyBlockStateMapper legacyBlockStateMapper) {
        if(legacyBlockStateMapper == null) throw new IllegalArgumentException("State cannot be null");
        this.legacyBlockStateMapper=legacyBlockStateMapper;
        return this;
    }

    public LegacyBlockRegistryObject<T> tLegacyBlockRegistryObject(boolean value) {
        this.tab=value;
        return this;
    }

    void create() {
        if(value != null) return;
        value=supplier.get();
        if(value == null) throw new IllegalArgumentException("Block supplier return empty for" + namespace + ":" + string);
    }

    public T getValue() {
        if(value == null) throw new IllegalArgumentException("Block " + namespace + ":" + string +" has not been register");
        return value;
    }

    public T get() {
        return getValue();
    }

    public String getString() {
        return string;
    }

    public String getNamespace() {
        return namespace;
    }

    public ResourceLocation getLocation() {
        return ResourceLocations.locations(namespace,string);
    }

    public ResourceLocation getModel() {
        return resourceLocation;
    }

    public LegacyBlockStateMapper getLegacyBlockStateMapper() {
        return legacyBlockStateMapper;
    }

    public boolean isTab() {
        return tab;
    }

    public boolean isCreate() {
        return value != null;
    }
}
