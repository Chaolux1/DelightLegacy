package net.chaolux.delightlegacy.legacy.registry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LegacyBlockRegister<T extends Block> {
    private final String namespace;
    private final List<LegacyBlockRegistryObject<T>> list=new ArrayList<LegacyBlockRegistryObject<T>>();
    private boolean registered;
    private final CreativeTabs creativeTabs;
    public LegacyBlockRegister(String namespace, CreativeTabs creativeTabs) {
        this.namespace=namespace;
        this.creativeTabs=creativeTabs;
    }

    public LegacyBlockRegistryObject<T> register(String string, Supplier<? extends T> supplier) {
        return registryObject(string,supplier,true);
    }

    public LegacyBlockRegistryObject<T> registerNone(String string,Supplier<? extends T> supplier) {
        return registryObject(string,supplier,false);
    }

    private LegacyBlockRegistryObject<T> registryObject(String string,Supplier<? extends T> supplier,boolean value) {
        if(registered) throw new IllegalArgumentException("Block have already been registered");
        LegacyBlockRegistryObject<T> legacyBlockRegistryObject=new LegacyBlockRegistryObject<T>(namespace,string,supplier,value);
        list.add(legacyBlockRegistryObject);
        return legacyBlockRegistryObject;
    }

    public void setRegistered() {
        if(registered) return;
        for (LegacyBlockRegistryObject<? extends T> legacyBlockRegistryObject : list) {
            registerBlock(legacyBlockRegistryObject);
        }
        registered=true;
    }

    private void registerBlock(LegacyBlockRegistryObject<? extends T> legacyBlockRegistryObject) {
        legacyBlockRegistryObject.create();
        Block block=legacyBlockRegistryObject.getValue();
        block.setBlockName(namespace + "." + legacyBlockRegistryObject.getString());
        block.setBlockTextureName("minecraft:stone");
        if(legacyBlockRegistryObject.isTab()) {
            if(creativeTabs != null) block.setCreativeTab(creativeTabs);
            GameRegistry.registerBlock(block,legacyBlockRegistryObject.getItemblock(),legacyBlockRegistryObject.getString());
        } else {
            GameRegistry.registerBlock(block,null,legacyBlockRegistryObject.getString());
        }
    }

    public List<LegacyBlockRegistryObject<? extends T>> getObject() {
        return Collections.unmodifiableList(list);
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isRegistered() {
        return registered;
    }
}
