package net.chaolux.delightlegacy.registry;

import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import net.chaolux.delightlegacy.common.FoodValues;
import net.chaolux.delightlegacy.common.item.*;
import net.chaolux.delightlegacy.legacy.common.item.*;
import net.chaolux.delightlegacy.legacy.registry.LegacyItemRegister;
import net.chaolux.delightlegacy.legacy.registry.LegacyRegistryObject;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

public class ModItems {
        public static final LegacyItemRegister ITEMS;
        public static LinkedHashSet<LegacyRegistryObject<Item>> CREATIVE_TAB_ITEMS;
//        public static final LegacyRegistryObject<Item> STOVE;
//        public static final LegacyRegistryObject<Item> COOKING_POT;
//        public static final LegacyRegistryObject<Item> SKILLET;
//        public static final LegacyRegistryObject<Item> CUTTING_BOARD;
//        public static final LegacyRegistryObject<Item> WOODEN_BASKET;
//        public static final LegacyRegistryObject<Item> BAMBOO_BASKET;
//        public static final LegacyRegistryObject<Item> BASKET;
//        public static final LegacyRegistryObject<Item> CARROT_CRATE;
//        public static final LegacyRegistryObject<Item> POTATO_CRATE;
//        public static final LegacyRegistryObject<Item> BEETROOT_CRATE;
//        public static final LegacyRegistryObject<Item> CABBAGE_CRATE;
//        public static final LegacyRegistryObject<Item> TOMATO_CRATE;
//        public static final LegacyRegistryObject<Item> ONION_CRATE;
//        public static final LegacyRegistryObject<Item> RICE_BALE;
//        public static final LegacyRegistryObject<Item> RICE_BAG;
//        public static final LegacyRegistryObject<Item> STRAW_BALE;
//        public static final LegacyRegistryObject<Item> SAFETY_NET;
//        public static final LegacyRegistryObject<Item> OAK_CABINET;
//        public static final LegacyRegistryObject<Item> SPRUCE_CABINET;
//        public static final LegacyRegistryObject<Item> BIRCH_CABINET;
//        public static final LegacyRegistryObject<Item> JUNGLE_CABINET;
//        public static final LegacyRegistryObject<Item> ACACIA_CABINET;
//        public static final LegacyRegistryObject<Item> DARK_OAK_CABINET;
//        public static final LegacyRegistryObject<Item> MANGROVE_CABINET;
//        public static final LegacyRegistryObject<Item> CHERRY_CABINET;
//        public static final LegacyRegistryObject<Item> BAMBOO_CABINET;
//        public static final LegacyRegistryObject<Item> CRIMSON_CABINET;
//        public static final LegacyRegistryObject<Item> WARPED_CABINET;
//        public static final LegacyRegistryObject<Item> TATAMI;
//        public static final LegacyRegistryObject<Item> FULL_TATAMI_MAT;
//        public static final LegacyRegistryObject<Item> HALF_TATAMI_MAT;
//        public static final LegacyRegistryObject<Item> CANVAS_RUG;
//        public static final LegacyRegistryObject<Item> ROPE_FENCE;
//        public static final LegacyRegistryObject<Item> ROPE_FENCE_GATE;
//        public static final LegacyRegistryObject<Item> ORGANIC_COMPOST;
//        public static final LegacyRegistryObject<Item> RICH_SOIL;
//        public static final LegacyRegistryObject<Item> RICH_SOIL_FARMLAND;
//        public static final LegacyRegistryObject<Item> ROPE;
//        public static final LegacyRegistryObject<Item> CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> WHITE_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> WHITE_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> LIGHT_GRAY_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> LIGHT_GRAY_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> GRAY_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> GRAY_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> BLACK_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> BLACK_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> BROWN_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> BROWN_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> RED_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> RED_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> ORANGE_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> ORANGE_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> YELLOW_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> YELLOW_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> LIME_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> LIME_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> GREEN_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> GREEN_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> CYAN_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> CYAN_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> LIGHT_BLUE_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> LIGHT_BLUE_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> BLUE_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> BLUE_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> PURPLE_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> PURPLE_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> MAGENTA_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> MAGENTA_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> PINK_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> PINK_HANGING_CANVAS_SIGN;
//        public static final LegacyRegistryObject<Item> FLINT_KNIFE;
//        public static final LegacyRegistryObject<Item> IRON_KNIFE;
//        public static final LegacyRegistryObject<Item> DIAMOND_KNIFE;
//        public static final LegacyRegistryObject<Item> NETHERITE_KNIFE;
//        public static final LegacyRegistryObject<Item> GOLDEN_KNIFE;
        public static final LegacyRegistryObject<Item> STRAW;
        public static final LegacyRegistryObject<Item> CANVAS;
        public static final LegacyRegistryObject<Item> TREE_BARK;
//        public static final LegacyRegistryObject<Item> SANDY_SHRUB;
//        public static final LegacyRegistryObject<Item> WILD_CABBAGES;
//        public static final LegacyRegistryObject<Item> WILD_ONIONS;
//        public static final LegacyRegistryObject<Item> WILD_TOMATOES;
//        public static final LegacyRegistryObject<Item> WILD_CARROTS;
//        public static final LegacyRegistryObject<Item> WILD_POTATOES;
//        public static final LegacyRegistryObject<Item> WILD_BEETROOTS;
//        public static final LegacyRegistryObject<Item> WILD_RICE;
//        public static final LegacyRegistryObject<Item> BROWN_MUSHROOM_COLONY;
//        public static final LegacyRegistryObject<Item> RED_MUSHROOM_COLONY;
        public static final LegacyRegistryObject<Item> CABBAGE;
        public static final LegacyRegistryObject<Item> TOMATO;
        public static final LegacyRegistryObject<Item> ONION;
        public static final LegacyRegistryObject<Item> RICE_PANICLE;
        public static final LegacyRegistryObject<Item> RICE;
        public static final LegacyRegistryObject<Item> CABBAGE_SEEDS;
        public static final LegacyRegistryObject<Item> TOMATO_SEEDS;
//        public static final LegacyRegistryObject<Item> ROTTEN_TOMATO;
        public static final LegacyRegistryObject<Item> FRIED_EGG;
        public static final LegacyRegistryObject<Item> MILK_BOTTLE;
        public static final LegacyRegistryObject<Item> HOT_COCOA;
        public static final LegacyRegistryObject<Item> APPLE_CIDER;
        public static final LegacyRegistryObject<Item> MELON_JUICE;
        public static final LegacyRegistryObject<Item> TOMATO_SAUCE;
        public static final LegacyRegistryObject<Item> WHEAT_DOUGH;
        public static final LegacyRegistryObject<Item> RAW_PASTA;
        public static final LegacyRegistryObject<Item> PUMPKIN_SLICE;
        public static final LegacyRegistryObject<Item> CABBAGE_LEAF;
        public static final LegacyRegistryObject<Item> MINCED_BEEF;
        public static final LegacyRegistryObject<Item> BEEF_PATTY;
        public static final LegacyRegistryObject<Item> CHICKEN_CUTS;
        public static final LegacyRegistryObject<Item> COOKED_CHICKEN_CUTS;
        public static final LegacyRegistryObject<Item> BACON;
        public static final LegacyRegistryObject<Item> COOKED_BACON;
        public static final LegacyRegistryObject<Item> COD_SLICE;
        public static final LegacyRegistryObject<Item> COOKED_COD_SLICE;
        public static final LegacyRegistryObject<Item> SALMON_SLICE;
        public static final LegacyRegistryObject<Item> COOKED_SALMON_SLICE;
        public static final LegacyRegistryObject<Item> MUTTON_CHOPS;
        public static final LegacyRegistryObject<Item> COOKED_MUTTON_CHOPS;
        public static final LegacyRegistryObject<Item> HAM;
        public static final LegacyRegistryObject<Item> SMOKED_HAM;
        public static final LegacyRegistryObject<Item> PIE_CRUST;
//        public static final LegacyRegistryObject<Item> APPLE_PIE;
//        public static final LegacyRegistryObject<Item> SWEET_BERRY_CHEESECAKE;
//        public static final LegacyRegistryObject<Item> CHOCOLATE_PIE;
        public static final LegacyRegistryObject<Item> CAKE_SLICE;
        public static final LegacyRegistryObject<Item> APPLE_PIE_SLICE;
        public static final LegacyRegistryObject<Item> SWEET_BERRY_CHEESECAKE_SLICE;
        public static final LegacyRegistryObject<Item> CHOCOLATE_PIE_SLICE;
        public static final LegacyRegistryObject<Item> PUMPKIN_PIE_SLICE;
        public static final LegacyRegistryObject<Item> SWEET_BERRY_COOKIE;
        public static final LegacyRegistryObject<Item> HONEY_COOKIE;
        public static final LegacyRegistryObject<Item> MELON_POPSICLE;
        public static final LegacyRegistryObject<Item> GLOW_BERRY_CUSTARD;
        public static final LegacyRegistryObject<Item> FRUIT_SALAD;
        public static final LegacyRegistryObject<Item> MIXED_SALAD;
        public static final LegacyRegistryObject<Item> NETHER_SALAD;
        public static final LegacyRegistryObject<Item> BARBECUE_STICK;
        public static final LegacyRegistryObject<Item> EGG_SANDWICH;
        public static final LegacyRegistryObject<Item> CHICKEN_SANDWICH;
        public static final LegacyRegistryObject<Item> HAMBURGER;
        public static final LegacyRegistryObject<Item> BACON_SANDWICH;
        public static final LegacyRegistryObject<Item> MUTTON_WRAP;
        public static final LegacyRegistryObject<Item> DUMPLINGS;
        public static final LegacyRegistryObject<Item> STUFFED_POTATO;
        public static final LegacyRegistryObject<Item> CABBAGE_ROLLS;
        public static final LegacyRegistryObject<Item> SALMON_ROLL;
        public static final LegacyRegistryObject<Item> COD_ROLL;
        public static final LegacyRegistryObject<Item> KELP_ROLL;
        public static final LegacyRegistryObject<Item> KELP_ROLL_SLICE;
        public static final LegacyRegistryObject<Item> COOKED_RICE;
        public static final LegacyRegistryObject<Item> BONE_BROTH;
        public static final LegacyRegistryObject<Item> BEEF_STEW;
        public static final LegacyRegistryObject<Item> CHICKEN_SOUP;
        public static final LegacyRegistryObject<Item> VEGETABLE_SOUP;
        public static final LegacyRegistryObject<Item> FISH_STEW;
        public static final LegacyRegistryObject<Item> FRIED_RICE;
        public static final LegacyRegistryObject<Item> PUMPKIN_SOUP;
        public static final LegacyRegistryObject<Item> BAKED_COD_STEW;
        public static final LegacyRegistryObject<Item> NOODLE_SOUP;
        public static final LegacyRegistryObject<Item> ONION_SOUP;
        public static final LegacyRegistryObject<Item> BACON_AND_EGGS;
        public static final LegacyRegistryObject<Item> PASTA_WITH_MEATBALLS;
        public static final LegacyRegistryObject<Item> PASTA_WITH_MUTTON_CHOP;
        public static final LegacyRegistryObject<Item> MUSHROOM_RICE;
        public static final LegacyRegistryObject<Item> ROASTED_MUTTON_CHOPS;
        public static final LegacyRegistryObject<Item> VEGETABLE_NOODLES;
        public static final LegacyRegistryObject<Item> STEAK_AND_POTATOES;
        public static final LegacyRegistryObject<Item> RATATOUILLE;
        public static final LegacyRegistryObject<Item> SQUID_INK_PASTA;
        public static final LegacyRegistryObject<Item> GRILLED_SALMON;
//        public static final LegacyRegistryObject<Item> ROAST_CHICKEN_BLOCK;
        public static final LegacyRegistryObject<Item> ROAST_CHICKEN;
//        public static final LegacyRegistryObject<Item> STUFFED_PUMPKIN_BLOCK;
        public static final LegacyRegistryObject<Item> STUFFED_PUMPKIN;
//        public static final LegacyRegistryObject<Item> HONEY_GLAZED_HAM_BLOCK;
        public static final LegacyRegistryObject<Item> HONEY_GLAZED_HAM;
//        public static final LegacyRegistryObject<Item> SHEPHERDS_PIE_BLOCK;
        public static final LegacyRegistryObject<Item> SHEPHERDS_PIE;
//        public static final LegacyRegistryObject<Item> GLEAMING_SALAD_BLOCK;
        public static final LegacyRegistryObject<Item> GLEAMING_SALAD;
//        public static final LegacyRegistryObject<Item> RICE_ROLL_MEDLEY_BLOCK;
        public static final LegacyRegistryObject<Item> DOG_FOOD;
        public static final LegacyRegistryObject<Item> HORSE_FEED;
//        public static final LegacyRegistryObject<Item> DEBUG_PUMPKIN_PIE;

        public ModItems() {
        }

        public static <T extends Item> LegacyRegistryObject<T> registerWithTab(String name,Supplier<? extends T> supplier) {
            return ITEMS.register(name,supplier);
        }

        public static <T extends Item> LegacyRegistryObject<T> registerHidden(String name,Supplier<? extends T> supplier) {
            return ITEMS.registryObject(name,supplier);
        }

        public static LegacyItemProperties basicItem() {
            return new LegacyItemProperties();
        }

        public static LegacyItemProperties foodItem(LegacyFoodProperties food) {
            return new LegacyItemProperties().food(food);
        }

        public static LegacyItemProperties bowlFoodItem(LegacyFoodProperties food) {
            return new LegacyItemProperties().food(food).craftRemainder(Items.bowl).stacksTo(16);
        }

        public static LegacyItemProperties drinkItem() {
            return new LegacyItemProperties().craftRemainder(Items.glass_bottle).stacksTo(16);
        }

        static {
            ITEMS = new LegacyItemRegister("delightlegacy",ModCreativeTabs.DELIGHT_LEGACY);
            CREATIVE_TAB_ITEMS = Sets.newLinkedHashSet();
//            STOVE = registerWithTab("stove", () -> new BlockItem((Block) ModBlocks.STOVE.get(), basicItem()));
//            COOKING_POT = registerWithTab("cooking_pot", () -> new CookingPotItem((Block) ModBlocks.COOKING_POT.get(), basicItem().stacksTo(1)));
//            SKILLET = registerWithTab("skillet", () -> new SkilletItem((Block) ModBlocks.SKILLET.get(), basicItem().stacksTo(1)));
//            CUTTING_BOARD = registerWithTab("cutting_board", () -> new FuelBlockItem((Block) ModBlocks.CUTTING_BOARD.get(), basicItem(), 200));
//            WOODEN_BASKET = registerWithTab("wooden_basket", () -> new FuelBlockItem((Block) ModBlocks.WOODEN_BASKET.get(), basicItem(), 300));
//            BAMBOO_BASKET = registerWithTab("bamboo_basket", () -> new FuelBlockItem((Block) ModBlocks.BAMBOO_BASKET.get(), basicItem(), 300));
//            BASKET = BAMBOO_BASKET;
//            CARROT_CRATE = registerWithTab("carrot_crate", () -> new BlockItem((Block) ModBlocks.CARROT_CRATE.get(), basicItem()));
//            POTATO_CRATE = registerWithTab("potato_crate", () -> new BlockItem((Block) ModBlocks.POTATO_CRATE.get(), basicItem()));
//            BEETROOT_CRATE = registerWithTab("beetroot_crate", () -> new BlockItem((Block) ModBlocks.BEETROOT_CRATE.get(), basicItem()));
//            CABBAGE_CRATE = registerWithTab("cabbage_crate", () -> new BlockItem((Block) ModBlocks.CABBAGE_CRATE.get(), basicItem()));
//            TOMATO_CRATE = registerWithTab("tomato_crate", () -> new BlockItem((Block) ModBlocks.TOMATO_CRATE.get(), basicItem()));
//            ONION_CRATE = registerWithTab("onion_crate", () -> new BlockItem((Block) ModBlocks.ONION_CRATE.get(), basicItem()));
//            RICE_BALE = registerWithTab("rice_bale", () -> new BlockItem((Block) ModBlocks.RICE_BALE.get(), basicItem()));
//            RICE_BAG = registerWithTab("rice_bag", () -> new BlockItem((Block) ModBlocks.RICE_BAG.get(), basicItem()));
//            STRAW_BALE = registerWithTab("straw_bale", () -> new FuelBlockItem((Block) ModBlocks.STRAW_BALE.get(), basicItem(), 1000));
//            SAFETY_NET = registerWithTab("safety_net", () -> new FuelBlockItem((Block) ModBlocks.SAFETY_NET.get(), basicItem(), 200));
//            OAK_CABINET = registerWithTab("oak_cabinet", () -> new FuelBlockItem((Block) ModBlocks.OAK_CABINET.get(), basicItem(), 300));
//            SPRUCE_CABINET = registerWithTab("spruce_cabinet", () -> new FuelBlockItem((Block) ModBlocks.SPRUCE_CABINET.get(), basicItem(), 300));
//            BIRCH_CABINET = registerWithTab("birch_cabinet", () -> new FuelBlockItem((Block) ModBlocks.BIRCH_CABINET.get(), basicItem(), 300));
//            JUNGLE_CABINET = registerWithTab("jungle_cabinet", () -> new FuelBlockItem((Block) ModBlocks.JUNGLE_CABINET.get(), basicItem(), 300));
//            ACACIA_CABINET = registerWithTab("acacia_cabinet", () -> new FuelBlockItem((Block) ModBlocks.ACACIA_CABINET.get(), basicItem(), 300));
//            DARK_OAK_CABINET = registerWithTab("dark_oak_cabinet", () -> new FuelBlockItem((Block) ModBlocks.DARK_OAK_CABINET.get(), basicItem(), 300));
//            MANGROVE_CABINET = registerWithTab("mangrove_cabinet", () -> new FuelBlockItem((Block) ModBlocks.MANGROVE_CABINET.get(), basicItem(), 300));
//            CHERRY_CABINET = registerWithTab("cherry_cabinet", () -> new FuelBlockItem((Block) ModBlocks.CHERRY_CABINET.get(), basicItem(), 300));
//            BAMBOO_CABINET = registerWithTab("bamboo_cabinet", () -> new FuelBlockItem((Block) ModBlocks.BAMBOO_CABINET.get(), basicItem(), 300));
//            CRIMSON_CABINET = registerWithTab("crimson_cabinet", () -> new BlockItem((Block) ModBlocks.CRIMSON_CABINET.get(), basicItem()));
//            WARPED_CABINET = registerWithTab("warped_cabinet", () -> new BlockItem((Block) ModBlocks.WARPED_CABINET.get(), basicItem()));
//            TATAMI = registerWithTab("tatami", () -> new FuelBlockItem((Block) ModBlocks.TATAMI.get(), basicItem(), 400));
//            FULL_TATAMI_MAT = registerWithTab("full_tatami_mat", () -> new FuelBlockItem((Block) ModBlocks.FULL_TATAMI_MAT.get(), basicItem(), 200));
//            HALF_TATAMI_MAT = registerWithTab("half_tatami_mat", () -> new FuelBlockItem((Block) ModBlocks.HALF_TATAMI_MAT.get(), basicItem()));
//            CANVAS_RUG = registerWithTab("canvas_rug", () -> new FuelBlockItem((Block) ModBlocks.CANVAS_RUG.get(), basicItem(), 200));
//            ROPE_FENCE = registerWithTab("rope_fence", () -> new FuelBlockItem((Block) ModBlocks.ROPE_FENCE.get(), basicItem(), 200));
//            ROPE_FENCE_GATE = registerWithTab("rope_fence_gate", () -> new FuelBlockItem((Block) ModBlocks.ROPE_FENCE_GATE.get(), basicItem(), 200));
//            ORGANIC_COMPOST = registerWithTab("organic_compost", () -> new BlockItem((Block) ModBlocks.ORGANIC_COMPOST.get(), basicItem()));
//            RICH_SOIL = registerWithTab("rich_soil", () -> new BlockItem((Block) ModBlocks.RICH_SOIL.get(), basicItem()));
//            RICH_SOIL_FARMLAND = registerWithTab("rich_soil_farmland", () -> new BlockItem((Block) ModBlocks.RICH_SOIL_FARMLAND.get(), basicItem()));
//            ROPE = registerWithTab("rope", () -> new RopeItem((Block) ModBlocks.ROPE.get(), basicItem()));
//            CANVAS_SIGN = registerWithTab("canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.CANVAS_SIGN.get(), (Block) ModBlocks.CANVAS_WALL_SIGN.get()));
//            HANGING_CANVAS_SIGN = registerWithTab("hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            WHITE_CANVAS_SIGN = registerWithTab("white_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.WHITE_CANVAS_SIGN.get(), (Block) ModBlocks.WHITE_CANVAS_WALL_SIGN.get()));
//            WHITE_HANGING_CANVAS_SIGN = registerWithTab("white_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.WHITE_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.WHITE_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            LIGHT_GRAY_CANVAS_SIGN = registerWithTab("light_gray_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.LIGHT_GRAY_CANVAS_SIGN.get(), (Block) ModBlocks.LIGHT_GRAY_CANVAS_WALL_SIGN.get()));
//            LIGHT_GRAY_HANGING_CANVAS_SIGN = registerWithTab("light_gray_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.LIGHT_GRAY_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.LIGHT_GRAY_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            GRAY_CANVAS_SIGN = registerWithTab("gray_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.GRAY_CANVAS_SIGN.get(), (Block) ModBlocks.GRAY_CANVAS_WALL_SIGN.get()));
//            GRAY_HANGING_CANVAS_SIGN = registerWithTab("gray_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.GRAY_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.GRAY_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            BLACK_CANVAS_SIGN = registerWithTab("black_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.BLACK_CANVAS_SIGN.get(), (Block) ModBlocks.BLACK_CANVAS_WALL_SIGN.get()));
//            BLACK_HANGING_CANVAS_SIGN = registerWithTab("black_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.BLACK_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.BLACK_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            BROWN_CANVAS_SIGN = registerWithTab("brown_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.BROWN_CANVAS_SIGN.get(), (Block) ModBlocks.BROWN_CANVAS_WALL_SIGN.get()));
//            BROWN_HANGING_CANVAS_SIGN = registerWithTab("brown_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.BROWN_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.BROWN_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            RED_CANVAS_SIGN = registerWithTab("red_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.RED_CANVAS_SIGN.get(), (Block) ModBlocks.RED_CANVAS_WALL_SIGN.get()));
//            RED_HANGING_CANVAS_SIGN = registerWithTab("red_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.RED_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.RED_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            ORANGE_CANVAS_SIGN = registerWithTab("orange_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.ORANGE_CANVAS_SIGN.get(), (Block) ModBlocks.ORANGE_CANVAS_WALL_SIGN.get()));
//            ORANGE_HANGING_CANVAS_SIGN = registerWithTab("orange_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.ORANGE_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.ORANGE_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            YELLOW_CANVAS_SIGN = registerWithTab("yellow_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.YELLOW_CANVAS_SIGN.get(), (Block) ModBlocks.YELLOW_CANVAS_WALL_SIGN.get()));
//            YELLOW_HANGING_CANVAS_SIGN = registerWithTab("yellow_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.YELLOW_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.YELLOW_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            LIME_CANVAS_SIGN = registerWithTab("lime_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.LIME_CANVAS_SIGN.get(), (Block) ModBlocks.LIME_CANVAS_WALL_SIGN.get()));
//            LIME_HANGING_CANVAS_SIGN = registerWithTab("lime_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.LIME_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.LIME_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            GREEN_CANVAS_SIGN = registerWithTab("green_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.GREEN_CANVAS_SIGN.get(), (Block) ModBlocks.GREEN_CANVAS_WALL_SIGN.get()));
//            GREEN_HANGING_CANVAS_SIGN = registerWithTab("green_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.GREEN_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.GREEN_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            CYAN_CANVAS_SIGN = registerWithTab("cyan_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.CYAN_CANVAS_SIGN.get(), (Block) ModBlocks.CYAN_CANVAS_WALL_SIGN.get()));
//            CYAN_HANGING_CANVAS_SIGN = registerWithTab("cyan_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.CYAN_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.CYAN_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            LIGHT_BLUE_CANVAS_SIGN = registerWithTab("light_blue_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.LIGHT_BLUE_CANVAS_SIGN.get(), (Block) ModBlocks.LIGHT_BLUE_CANVAS_WALL_SIGN.get()));
//            LIGHT_BLUE_HANGING_CANVAS_SIGN = registerWithTab("light_blue_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.LIGHT_BLUE_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.LIGHT_BLUE_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            BLUE_CANVAS_SIGN = registerWithTab("blue_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.BLUE_CANVAS_SIGN.get(), (Block) ModBlocks.BLUE_CANVAS_WALL_SIGN.get()));
//            BLUE_HANGING_CANVAS_SIGN = registerWithTab("blue_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.BLUE_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.BLUE_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            PURPLE_CANVAS_SIGN = registerWithTab("purple_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.PURPLE_CANVAS_SIGN.get(), (Block) ModBlocks.PURPLE_CANVAS_WALL_SIGN.get()));
//            PURPLE_HANGING_CANVAS_SIGN = registerWithTab("purple_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.PURPLE_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.PURPLE_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            MAGENTA_CANVAS_SIGN = registerWithTab("magenta_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.MAGENTA_CANVAS_SIGN.get(), (Block) ModBlocks.MAGENTA_CANVAS_WALL_SIGN.get()));
//            MAGENTA_HANGING_CANVAS_SIGN = registerWithTab("magenta_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.MAGENTA_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.MAGENTA_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            PINK_CANVAS_SIGN = registerWithTab("pink_canvas_sign", () -> new SignItem(basicItem(), (Block) ModBlocks.PINK_CANVAS_SIGN.get(), (Block) ModBlocks.PINK_CANVAS_WALL_SIGN.get()));
//            PINK_HANGING_CANVAS_SIGN = registerWithTab("pink_hanging_canvas_sign", () -> new HangingSignItem((Block) ModBlocks.PINK_HANGING_CANVAS_SIGN.get(), (Block) ModBlocks.PINK_HANGING_CANVAS_WALL_SIGN.get(), basicItem()));
//            FLINT_KNIFE = registerWithTab("flint_knife", () -> new KnifeItem(ModMaterials.FLINT, 0.5F, -2.0F, basicItem()));
//            IRON_KNIFE = registerWithTab("iron_knife", () -> new KnifeItem(Tiers.IRON, 0.5F, -2.0F, basicItem()));
//            DIAMOND_KNIFE = registerWithTab("diamond_knife", () -> new KnifeItem(Tiers.DIAMOND, 0.5F, -2.0F, basicItem()));
//            NETHERITE_KNIFE = registerWithTab("netherite_knife", () -> new KnifeItem(Tiers.NETHERITE, 0.5F, -2.0F, basicItem().fireResistant()));
//            GOLDEN_KNIFE = registerWithTab("golden_knife", () -> new KnifeItem(Tiers.GOLD, 0.5F, -2.0F, basicItem()));
            STRAW = registerWithTab("straw", () -> new FuelItem(basicItem()));
            CANVAS = registerWithTab("canvas", () -> new FuelItem(basicItem(), 400));
            TREE_BARK = registerWithTab("tree_bark", () -> new FuelItem(basicItem(), 200));
//            SANDY_SHRUB = registerWithTab("sandy_shrub", () -> new BlockItem((Block) ModBlocks.SANDY_SHRUB.get(), basicItem()));
//            WILD_CABBAGES = registerWithTab("wild_cabbages", () -> new BlockItem((Block) ModBlocks.WILD_CABBAGES.get(), basicItem()));
//            WILD_ONIONS = registerWithTab("wild_onions", () -> new BlockItem((Block) ModBlocks.WILD_ONIONS.get(), basicItem()));
//            WILD_TOMATOES = registerWithTab("wild_tomatoes", () -> new BlockItem((Block) ModBlocks.WILD_TOMATOES.get(), basicItem()));
//            WILD_CARROTS = registerWithTab("wild_carrots", () -> new BlockItem((Block) ModBlocks.WILD_CARROTS.get(), basicItem()));
//            WILD_POTATOES = registerWithTab("wild_potatoes", () -> new BlockItem((Block) ModBlocks.WILD_POTATOES.get(), basicItem()));
//            WILD_BEETROOTS = registerWithTab("wild_beetroots", () -> new BlockItem((Block) ModBlocks.WILD_BEETROOTS.get(), basicItem()));
//            WILD_RICE = registerWithTab("wild_rice", () -> new DoubleHighBlockItem((Block) ModBlocks.WILD_RICE.get(), basicItem()));
//            BROWN_MUSHROOM_COLONY = registerWithTab("brown_mushroom_colony", () -> new MushroomColonyItem((Block) ModBlocks.BROWN_MUSHROOM_COLONY.get(), basicItem()));
//            RED_MUSHROOM_COLONY = registerWithTab("red_mushroom_colony", () -> new MushroomColonyItem((Block) ModBlocks.RED_MUSHROOM_COLONY.get(), basicItem()));
            CABBAGE = registerWithTab("cabbage", () -> new LegacyItem(foodItem(FoodValues.CABBAGE)));
            TOMATO = registerWithTab("tomato", () -> new LegacyItem(foodItem(FoodValues.TOMATO)));
            ONION = registerWithTab("onion", () -> new LegacySeedFoodItem(ModBlocks.ONION_CROP.getValue(), Blocks.farmland, foodItem(FoodValues.ONION)));
            RICE_PANICLE = registerWithTab("rice_panicle", () -> new LegacyItem(basicItem()));
            RICE = registerWithTab("rice", () -> new RiceItem(ModBlocks.RICE_CROP.getValue(), basicItem()));
            CABBAGE_SEEDS = registerWithTab("cabbage_seeds", () -> new LegacySeedItem(ModBlocks.CABBAGE_CROP.getValue(), Blocks.farmland, basicItem()));
            TOMATO_SEEDS = registerWithTab("tomato_seeds", () -> new LegacySeedItem(ModBlocks.BUDDING_TOMATO_CROP.getValue(), Blocks.farmland, basicItem()));
//                public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
//                    super.registerBlocks(blockToItemMap, item);
//                    if (ModBlocks.TOMATO_CROP.isPresent()) {
//                        blockToItemMap.put((Block) ModBlocks.TOMATO_CROP.get(), item);
//                    }
//
//                    if (ModBlocks.TOMATO_CROP_ON_ROPE.isPresent()) {
//                        blockToItemMap.put((Block) ModBlocks.TOMATO_CROP_ON_ROPE.get(), item);
//                    }
//
//                }
//
//                public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
//                    super.removeFromBlockToItemMap(blockToItemMap, itemIn);
//                    if (ModBlocks.TOMATO_CROP.isPresent()) {
//                        blockToItemMap.remove(ModBlocks.TOMATO_CROP.get());
//                    }
//
//                    if (ModBlocks.TOMATO_CROP_ON_ROPE.isPresent()) {
//                        blockToItemMap.remove(ModBlocks.TOMATO_CROP_ON_ROPE.get());
//                    }
//
//                }
//            });
//            ROTTEN_TOMATO = registerWithTab("rotten_tomato", () -> new RottenTomatoItem((new Item.Properties()).stacksTo(16)));
            FRIED_EGG = registerWithTab("fried_egg", () -> new LegacyItem(foodItem(FoodValues.FRIED_EGG)));
            MILK_BOTTLE = registerWithTab("milk_bottle", () -> new MilkBottleItem(drinkItem()));
            HOT_COCOA = registerWithTab("hot_cocoa", () -> new HotCocoaItem(drinkItem()));
            APPLE_CIDER = registerWithTab("apple_cider", () -> new DrinkableItem(drinkItem().food(FoodValues.APPLE_CIDER), true, false));
            MELON_JUICE = registerWithTab("melon_juice", () -> new MelonJuiceItem(drinkItem()));
            TOMATO_SAUCE = registerWithTab("tomato_sauce", () -> new ConsumableItem(foodItem(FoodValues.TOMATO_SAUCE).craftRemainder(Items.bowl)));
            WHEAT_DOUGH = registerWithTab("wheat_dough", () -> new LegacyItem(foodItem(FoodValues.WHEAT_DOUGH)));
            RAW_PASTA = registerWithTab("raw_pasta", () -> new LegacyItem(foodItem(FoodValues.RAW_PASTA)));
            PUMPKIN_SLICE = registerWithTab("pumpkin_slice", () -> new LegacyItem(foodItem(FoodValues.PUMPKIN_SLICE)));
            CABBAGE_LEAF = registerWithTab("cabbage_leaf", () -> new LegacyItem(foodItem(FoodValues.CABBAGE_LEAF)));
            MINCED_BEEF = registerWithTab("minced_beef", () -> new LegacyItem(foodItem(FoodValues.MINCED_BEEF)));
            BEEF_PATTY = registerWithTab("beef_patty", () -> new LegacyItem(foodItem(FoodValues.BEEF_PATTY)));
            CHICKEN_CUTS = registerWithTab("chicken_cuts", () -> new LegacyItem(foodItem(FoodValues.CHICKEN_CUTS)));
            COOKED_CHICKEN_CUTS = registerWithTab("cooked_chicken_cuts", () -> new LegacyItem(foodItem(FoodValues.COOKED_CHICKEN_CUTS)));
            BACON = registerWithTab("bacon", () -> new LegacyItem(foodItem(FoodValues.BACON)));
            COOKED_BACON = registerWithTab("cooked_bacon", () -> new LegacyItem(foodItem(FoodValues.COOKED_BACON)));
            COD_SLICE = registerWithTab("cod_slice", () -> new LegacyItem(foodItem(FoodValues.COD_SLICE)));
            COOKED_COD_SLICE = registerWithTab("cooked_cod_slice", () -> new LegacyItem(foodItem(FoodValues.COOKED_COD_SLICE)));
            SALMON_SLICE = registerWithTab("salmon_slice", () -> new LegacyItem(foodItem(FoodValues.SALMON_SLICE)));
            COOKED_SALMON_SLICE = registerWithTab("cooked_salmon_slice", () -> new LegacyItem(foodItem(FoodValues.COOKED_SALMON_SLICE)));
            MUTTON_CHOPS = registerWithTab("mutton_chops", () -> new LegacyItem(foodItem(FoodValues.MUTTON_CHOP)));
            COOKED_MUTTON_CHOPS = registerWithTab("cooked_mutton_chops", () -> new LegacyItem(foodItem(FoodValues.COOKED_MUTTON_CHOP)));
            HAM = registerWithTab("ham", () -> new LegacyItem(foodItem(FoodValues.HAM)));
            SMOKED_HAM = registerWithTab("smoked_ham", () -> new LegacyItem(foodItem(FoodValues.SMOKED_HAM)));
            PIE_CRUST = registerWithTab("pie_crust", () -> new LegacyItem(foodItem(FoodValues.PIE_CRUST)));
//            APPLE_PIE = registerWithTab("apple_pie", () -> new PlaceableItem((Block) ModBlocks.APPLE_PIE.get(), basicItem()));
//            SWEET_BERRY_CHEESECAKE = registerWithTab("sweet_berry_cheesecake", () -> new PlaceableItem((Block) ModBlocks.SWEET_BERRY_CHEESECAKE.get(), basicItem()));
//            CHOCOLATE_PIE = registerWithTab("chocolate_pie", () -> new PlaceableItem((Block) ModBlocks.CHOCOLATE_PIE.get(), basicItem()));
            CAKE_SLICE = registerWithTab("cake_slice", () -> new ConsumableItem(foodItem(FoodValues.CAKE_SLICE)));
            APPLE_PIE_SLICE = registerWithTab("apple_pie_slice", () -> new ConsumableItem(foodItem(FoodValues.PIE_SLICE)));
            SWEET_BERRY_CHEESECAKE_SLICE = registerWithTab("sweet_berry_cheesecake_slice", () -> new ConsumableItem(foodItem(FoodValues.PIE_SLICE)));
            CHOCOLATE_PIE_SLICE = registerWithTab("chocolate_pie_slice", () -> new ConsumableItem(foodItem(FoodValues.PIE_SLICE)));
            PUMPKIN_PIE_SLICE = registerWithTab("pumpkin_pie_slice", () -> new ConsumableItem(foodItem(FoodValues.PIE_SLICE)));
            SWEET_BERRY_COOKIE = registerWithTab("sweet_berry_cookie", () -> new LegacyItem(foodItem(FoodValues.COOKIES)));
            HONEY_COOKIE = registerWithTab("honey_cookie", () -> new LegacyItem(foodItem(FoodValues.COOKIES)));
            MELON_POPSICLE = registerWithTab("melon_popsicle", () -> new PopsicleItem(foodItem(FoodValues.POPSICLE)));
            GLOW_BERRY_CUSTARD = registerWithTab("glow_berry_custard", () -> new ConsumableItem(foodItem(FoodValues.GLOW_BERRY_CUSTARD).craftRemainder(Items.glass_bottle).stacksTo(16)));
            FRUIT_SALAD = registerWithTab("fruit_salad", () -> new ConsumableItem(bowlFoodItem(FoodValues.FRUIT_SALAD)));
            MIXED_SALAD = registerWithTab("mixed_salad", () -> new ConsumableItem(bowlFoodItem(FoodValues.MIXED_SALAD)));
            NETHER_SALAD = registerWithTab("nether_salad", () -> new ConsumableItem(bowlFoodItem(FoodValues.NETHER_SALAD), false));
            BARBECUE_STICK = registerWithTab("barbecue_stick", () -> new LegacyItem(foodItem(FoodValues.BARBECUE_STICK)));
            EGG_SANDWICH = registerWithTab("egg_sandwich", () -> new LegacyItem(foodItem(FoodValues.EGG_SANDWICH)));
            CHICKEN_SANDWICH = registerWithTab("chicken_sandwich", () -> new LegacyItem(foodItem(FoodValues.CHICKEN_SANDWICH)));
            HAMBURGER = registerWithTab("hamburger", () -> new LegacyItem(foodItem(FoodValues.HAMBURGER)));
            BACON_SANDWICH = registerWithTab("bacon_sandwich", () -> new LegacyItem(foodItem(FoodValues.BACON_SANDWICH)));
            MUTTON_WRAP = registerWithTab("mutton_wrap", () -> new LegacyItem(foodItem(FoodValues.MUTTON_WRAP)));
            DUMPLINGS = registerWithTab("dumplings", () -> new LegacyItem(foodItem(FoodValues.DUMPLINGS)));
            STUFFED_POTATO = registerWithTab("stuffed_potato", () -> new LegacyItem(foodItem(FoodValues.STUFFED_POTATO)));
            CABBAGE_ROLLS = registerWithTab("cabbage_rolls", () -> new LegacyItem(foodItem(FoodValues.CABBAGE_ROLLS)));
            SALMON_ROLL = registerWithTab("salmon_roll", () -> new LegacyItem(foodItem(FoodValues.SALMON_ROLL)));
            COD_ROLL = registerWithTab("cod_roll", () -> new LegacyItem(foodItem(FoodValues.COD_ROLL)));
            KELP_ROLL = registerWithTab("kelp_roll", () -> new KelpRollItem(foodItem(FoodValues.KELP_ROLL)));
            KELP_ROLL_SLICE = registerWithTab("kelp_roll_slice", () -> new LegacyItem(foodItem(FoodValues.KELP_ROLL_SLICE)));
            COOKED_RICE = registerWithTab("cooked_rice", () -> new ConsumableItem(bowlFoodItem(FoodValues.COOKED_RICE)));
            BONE_BROTH = registerWithTab("bone_broth", () -> new DrinkableItem(bowlFoodItem(FoodValues.BONE_BROTH)));
            BEEF_STEW = registerWithTab("beef_stew", () -> new ConsumableItem(bowlFoodItem(FoodValues.BEEF_STEW)));
            CHICKEN_SOUP = registerWithTab("chicken_soup", () -> new ConsumableItem(bowlFoodItem(FoodValues.CHICKEN_SOUP)));
            VEGETABLE_SOUP = registerWithTab("vegetable_soup", () -> new ConsumableItem(bowlFoodItem(FoodValues.VEGETABLE_SOUP)));
            FISH_STEW = registerWithTab("fish_stew", () -> new ConsumableItem(bowlFoodItem(FoodValues.FISH_STEW)));
            FRIED_RICE = registerWithTab("fried_rice", () -> new ConsumableItem(bowlFoodItem(FoodValues.FRIED_RICE)));
            PUMPKIN_SOUP = registerWithTab("pumpkin_soup", () -> new ConsumableItem(bowlFoodItem(FoodValues.PUMPKIN_SOUP)));
            BAKED_COD_STEW = registerWithTab("baked_cod_stew", () -> new ConsumableItem(bowlFoodItem(FoodValues.BAKED_COD_STEW)));
            NOODLE_SOUP = registerWithTab("noodle_soup", () -> new ConsumableItem(bowlFoodItem(FoodValues.NOODLE_SOUP)));
            ONION_SOUP = registerWithTab("onion_soup", () -> new ConsumableItem(bowlFoodItem(FoodValues.ONION_SOUP)));
            BACON_AND_EGGS = registerWithTab("bacon_and_eggs", () -> new ConsumableItem(bowlFoodItem(FoodValues.BACON_AND_EGGS)));
            PASTA_WITH_MEATBALLS = registerWithTab("pasta_with_meatballs", () -> new ConsumableItem(bowlFoodItem(FoodValues.PASTA_WITH_MEATBALLS)));
            PASTA_WITH_MUTTON_CHOP = registerWithTab("pasta_with_mutton_chop", () -> new ConsumableItem(bowlFoodItem(FoodValues.PASTA_WITH_MUTTON_CHOP)));
            MUSHROOM_RICE = registerWithTab("mushroom_rice", () -> new ConsumableItem(bowlFoodItem(FoodValues.MUSHROOM_RICE)));
            ROASTED_MUTTON_CHOPS = registerWithTab("roasted_mutton_chops", () -> new ConsumableItem(bowlFoodItem(FoodValues.ROASTED_MUTTON_CHOPS)));
            VEGETABLE_NOODLES = registerWithTab("vegetable_noodles", () -> new ConsumableItem(bowlFoodItem(FoodValues.VEGETABLE_NOODLES)));
            STEAK_AND_POTATOES = registerWithTab("steak_and_potatoes", () -> new ConsumableItem(bowlFoodItem(FoodValues.STEAK_AND_POTATOES)));
            RATATOUILLE = registerWithTab("ratatouille", () -> new ConsumableItem(bowlFoodItem(FoodValues.RATATOUILLE)));
            SQUID_INK_PASTA = registerWithTab("squid_ink_pasta", () -> new ConsumableItem(bowlFoodItem(FoodValues.SQUID_INK_PASTA)));
            GRILLED_SALMON = registerWithTab("grilled_salmon", () -> new ConsumableItem(bowlFoodItem(FoodValues.GRILLED_SALMON)));
//            ROAST_CHICKEN_BLOCK = registerWithTab("roast_chicken_block", () -> new PlaceableItem((Block) ModBlocks.ROAST_CHICKEN_BLOCK.get(), basicItem().stacksTo(1)));
            ROAST_CHICKEN = registerWithTab("roast_chicken", () -> new ConsumableItem(bowlFoodItem(FoodValues.ROAST_CHICKEN)));
//            STUFFED_PUMPKIN_BLOCK = registerWithTab("stuffed_pumpkin_block", () -> new PlaceableItem((Block) ModBlocks.STUFFED_PUMPKIN_BLOCK.get(), basicItem().stacksTo(1)));
            STUFFED_PUMPKIN = registerWithTab("stuffed_pumpkin", () -> new ConsumableItem(bowlFoodItem(FoodValues.STUFFED_PUMPKIN)));
//            HONEY_GLAZED_HAM_BLOCK = registerWithTab("honey_glazed_ham_block", () -> new PlaceableItem((Block) ModBlocks.HONEY_GLAZED_HAM_BLOCK.get(), basicItem().stacksTo(1)));
            HONEY_GLAZED_HAM = registerWithTab("honey_glazed_ham", () -> new ConsumableItem(bowlFoodItem(FoodValues.HONEY_GLAZED_HAM)));
//            SHEPHERDS_PIE_BLOCK = registerWithTab("shepherds_pie_block", () -> new PlaceableItem((Block) ModBlocks.SHEPHERDS_PIE_BLOCK.get(), basicItem().stacksTo(1)));
            SHEPHERDS_PIE = registerWithTab("shepherds_pie", () -> new ConsumableItem(bowlFoodItem(FoodValues.SHEPHERDS_PIE)));
//            GLEAMING_SALAD_BLOCK = registerWithTab("gleaming_salad_block", () -> new PlaceableItem((Block) ModBlocks.GLEAMING_SALAD_BLOCK.get(), basicItem().stacksTo(1)));
            GLEAMING_SALAD = registerWithTab("gleaming_salad", () -> new ConsumableItem(bowlFoodItem(FoodValues.GLEAMING_SALAD)));
//            RICE_ROLL_MEDLEY_BLOCK = registerWithTab("rice_roll_medley_block", () -> new PlaceableItem((Block) ModBlocks.RICE_ROLL_MEDLEY_BLOCK.get(), basicItem().stacksTo(1)));
            DOG_FOOD = registerWithTab("dog_food", () -> new DogFoodItem(bowlFoodItem(FoodValues.DOG_FOOD)));
            HORSE_FEED = registerWithTab("horse_feed", () -> new HorseFeedItem(basicItem().stacksTo(16)));
//            DEBUG_PUMPKIN_PIE = registerHidden("debug_pumpkin_pie", () -> new BlockItem((Block) ModBlocks.PUMPKIN_PIE.get(), basicItem()) {
//                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
//                    tooltip.add(TextUtils.DEBUG_ITEM);
//                }
//            });
        }

        public static void register() {
            ITEMS.setRegistered();
            GameRegistry.registerFuelHandler(new LegacyFuelHandler());
            MinecraftForge.EVENT_BUS.register(new LegacyAnimalFeedHandler());
        }
}
