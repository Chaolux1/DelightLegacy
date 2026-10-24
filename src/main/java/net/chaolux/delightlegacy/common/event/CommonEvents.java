package net.chaolux.delightlegacy.common.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.chaolux.delightlegacy.common.utility.ItemUtils;
import net.chaolux.delightlegacy.registry.ModBlocks;
import net.chaolux.delightlegacy.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;

public class CommonEvents {
    @SubscribeEvent
    public void onUseHoe(UseHoeEvent useHoeEvent) {
        if(useHoeEvent.world.getBlock(useHoeEvent.x,useHoeEvent.y,useHoeEvent.z) != ModBlocks.RICH_SOIL.get()) return;
        if(!useHoeEvent.world.isAirBlock(useHoeEvent.x,useHoeEvent.y + 1,useHoeEvent.z)) return;
        if(!useHoeEvent.world.isRemote) useHoeEvent.world.setBlock(useHoeEvent.x,useHoeEvent.y,useHoeEvent.z,ModBlocks.RICH_SOIL_FARMLAND.get(),0,3);
        useHoeEvent.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent harvestDropsEvent) {
        if(harvestDropsEvent.world.isRemote || harvestDropsEvent.harvester == null || harvestDropsEvent.isSilkTouching) return;
        if(!ItemUtils.isHarvestStraw(harvestDropsEvent.harvester.getCurrentEquippedItem())) return;
        Block block=harvestDropsEvent.block;
        boolean wheat=block == Blocks.wheat && harvestDropsEvent.blockMetadata >= 7;
        boolean rice=block == ModBlocks.RICE_CROP_PANICLES.get() && harvestDropsEvent.blockMetadata >= 3;
        boolean grass=block == Blocks.tallgrass && harvestDropsEvent.blockMetadata != 0;
        boolean tallGrass=block == Blocks.double_plant && (harvestDropsEvent.blockMetadata & 7) == 2;
        if(!wheat && !rice && !grass && !tallGrass) return;
        if(!wheat && !rice && harvestDropsEvent.world.rand.nextFloat() >= 0.2f) return;
        harvestDropsEvent.drops.add(new ItemStack(ModItems.STRAW.getValue()));
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent livingDropsEvent) {
        if(livingDropsEvent.entityLiving.worldObj.isRemote || livingDropsEvent.source == null) return;
        if(!(livingDropsEvent.source.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer entityPlayer=(EntityPlayer)livingDropsEvent.source.getEntity();
        if(!ItemUtils.isKnife(entityPlayer.getCurrentEquippedItem())) return;
        Item item=null;
        if(livingDropsEvent.entityLiving instanceof EntityChicken) item= Items.feather;
        else if(livingDropsEvent.entityLiving instanceof EntityCow) item=Items.leather;
        else if(livingDropsEvent.entityLiving instanceof EntityHorse && ((EntityHorse)livingDropsEvent.entityLiving).getHorseType() <= 2) item=Items.leather;
        else if(livingDropsEvent.entityLiving instanceof EntitySpider) item=Items.string;
        if(item != null) setDrop(livingDropsEvent,new ItemStack(item));
        if(livingDropsEvent.entityLiving instanceof EntityPig && !((EntityPig)livingDropsEvent.entityLiving).isChild()) {
            float fortune=Math.min(1.0f,0.5f + 0.1f * EnchantmentHelper.getLootingModifier(entityPlayer));
            if(livingDropsEvent.entityLiving.worldObj.rand.nextFloat() < fortune) setDrop(livingDropsEvent,new ItemStack(livingDropsEvent.entityLiving.isBurning() ? ModItems.SMOKED_HAM.getValue() : ModItems.HAM.getValue()));
        }
    }

    private static void setDrop(LivingDropsEvent livingDropsEvent,ItemStack itemStack) {
        livingDropsEvent.drops.add(new EntityItem(livingDropsEvent.entityLiving.worldObj,livingDropsEvent.entityLiving.posX,livingDropsEvent.entityLiving.posY,livingDropsEvent.entityLiving.posZ,itemStack));
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
    }
}
