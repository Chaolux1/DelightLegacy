package net.chaolux.delightlegacy.legacy.data.tags;

import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class LegacyTags {
    public static final class Items {
        public static boolean isItem(String string, ItemStack itemStack) {
            return LegacyTagManager.getInstance().isItem(ResourceLocations.parse(string),itemStack);
        }

        public static List<ItemStack> getItemStack(String string) {
            return LegacyTagManager.getInstance().getItem(ResourceLocations.parse(string));
        }
    }

    public static final class Blocks {
        public static boolean isBlock(String string, Block block,int value) {
            return LegacyTagManager.getInstance().isBlock(ResourceLocations.parse(string),block,value);
        }
    }

    public static final class Entities {
        public static boolean isEntity(String string, Entity entity) {
            return LegacyTagManager.getInstance().isEntity(ResourceLocations.parse(string),entity);
        }
    }

}
