package net.chaolux.delightlegacy.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.chaolux.delightlegacy.legacy.common.item.LegacyItemProperties;
import net.chaolux.delightlegacy.legacy.common.item.LegacyToolAction;
import net.chaolux.delightlegacy.registry.ModMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class KnifeItem extends ItemSword implements LegacyToolAction {
    public static final String KNIFE_DIG="knife_dig";
    public static final String KNIFE_HARVEST="knife_harvest";
    private final Item.ToolMaterial toolMaterial;
    private final float attackDamage;
    public KnifeItem(Item.ToolMaterial toolMaterial, float attackDamage, float attackSpeed, LegacyItemProperties legacyItemProperties) {
        super(toolMaterial);
        this.toolMaterial=toolMaterial;
        this.attackDamage=attackDamage + toolMaterial.getDamageVsEntity();
        legacyItemProperties.apply(this);
        setMaxStackSize(1);
        setMaxDamage(toolMaterial.getMaxUses());
    }

    @Override
    public boolean isToolAction(ItemStack itemStack,String string) {
        return KNIFE_DIG.equals(string) || KNIFE_HARVEST.equals(string);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.none;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack,World world,EntityPlayer entityPlayer) {
        return itemStack;
    }

    @Override
    public boolean hitEntity(ItemStack itemStack,EntityLivingBase entityLivingBase,EntityLivingBase livingBase) {
        itemStack.damageItem(1,livingBase);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack,World world,Block block,int x,int y,int z,EntityLivingBase entityLivingBase) {
        if(block.getBlockHardness(world,x,y,z) != 0.0f) itemStack.damageItem(1,entityLivingBase);
        return true;
    }

    @Override
    public float func_150893_a(ItemStack itemStack,Block block) {
        if(block == Blocks.web || block == Blocks.cake || block == Blocks.hay_block || block.getMaterial() == Material.cloth) return toolMaterial.getEfficiencyOnProperMaterial();
        return 1.0f;
    }

    @Override
    public float getDigSpeed(ItemStack itemStack,Block block,int value) {
        return func_150893_a(itemStack,block);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap multimap= HashMultimap.create();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),new AttributeModifier(field_111210_e,"knife",(double) attackDamage,0));
        return multimap;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemStack,ItemStack stack) {
        if(stack == null) return false;
        if(toolMaterial == ModMaterials.FLINT) return stack.getItem() == Items.flint;
        if(toolMaterial == ModMaterials.NETHERITE) {
            for (ItemStack item : OreDictionary.getOres("ingotNetherite")) {
                if(OreDictionary.itemMatches(item,stack,false)) return true;
            }
            return false;
        }
        return super.getIsRepairable(itemStack,stack);
    }

    public float getAttackDamage() {
        return attackDamage;
    }
}
