package net.chaolux.delightlegacy.common.block.entity;

import com.mojang.authlib.UserType;
import net.chaolux.delightlegacy.common.block.AbstractStoveBlock;
import net.chaolux.delightlegacy.common.crafting.CookingPotRecipe;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.data.recipe.LegacyRecipeManager;
import net.chaolux.delightlegacy.legacy.data.tags.LegacyTagManager;
import net.chaolux.delightlegacy.registry.ModRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CookingPotBlockEntity extends SyncedBlockEntity implements ISidedInventory,HeatableBlockEntity {
    public static final int MEAL_DISPLAY_SLOT = 6;
    public static final int CONTAINER_SLOT = 7;
    public static final int OUTPUT_SLOT = 8;
    public static final int INVENTORY_SIZE = 9;
    private final int[] INPUT_INVENTORY ={0,1,2,3,4,5};
    private final int[] OUTPUT_INVENTORY ={CONTAINER_SLOT,OUTPUT_SLOT};
    private final ItemStack[] inventory=new ItemStack[INVENTORY_SIZE];
    private int cookTime;
    private int cookTimeTotal;
    private ItemStack mealContainerStack;
    private String customName;
    private boolean creative;
    private final Map<ResourceLocation,Integer> usedRecipeTracker=new LinkedHashMap<ResourceLocation,Integer>();
    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe=true;

    @Override
    public void updateEntity() {
        if(worldObj == null || worldObj.isRemote) return;
        boolean value=false;
        if(isHeated() && hasInput()) {
            CookingPotRecipe cookingPotRecipe=getMatchingRecipe();
            if(cookingPotRecipe != null && canCook(cookingPotRecipe)) {
                value=processCooking(cookingPotRecipe);
            } else {
                if(cookTime > 0) {
                    cookTime=Math.max(0,cookTime - 2);
                    markDirty();
                }
            }
        } else {
            if(cookTime > 0) {
                cookTime=Math.max(0,cookTime - 2);
                markDirty();
            }
        }
        ItemStack itemStack=getMeal();
        if(itemStack != null && itemStack.stackSize > 0) {
            if(doesMealHaveContainer(itemStack)) {
                if(useStoredContainersOnMeal()) value=true;
            } else if(moveMealToOutput()) {
                value=true;
            }
        }
        if(value) inventoryChanged();
    }

    public boolean isHeated() {
        return worldObj != null && isHeated(worldObj,xCoord,yCoord,zCoord);
    }

    @Override
    public boolean requiresDirectHeat() {
        return false;
    }

    public ItemStack getMealContainerStack() {
        return mealContainerStack == null ? null : mealContainerStack.copy();
    }

    public void setMealContainerStack(ItemStack itemStack) {
        mealContainerStack=itemStack == null || itemStack.stackSize <= 0 ? null : itemStack.copy();
        if(mealContainerStack != null) mealContainerStack.stackSize=1;
        inventoryChanged();
    }

    public ItemStack getContainer() {
        if(mealContainerStack != null) return mealContainerStack.copy();
        ItemStack itemStack=getMeal();
        if(itemStack == null || itemStack.stackSize <= 0) return null;
        Item item=itemStack.getItem();
        if(item != null && item.hasContainerItem()) {
            Item items=item.getContainerItem();
            if(items != null) return new ItemStack(items);
        }
        return null;
    }

    public ItemStack useHeldItemOnMeal(ItemStack itemStack,boolean value) {
        ItemStack stack=getMeal();
        if(stack == null || stack.stackSize <= 0) return null;
        if(!isContainerValid(itemStack)) return null;
        ItemStack item=stack.copy();
        item.stackSize=1;
        if(!value) itemStack.stackSize--;
        stack.stackSize--;
        if(stack.stackSize <= 0) {
            inventory[MEAL_DISPLAY_SLOT]=null;
            mealContainerStack=null;
        }
        inventoryChanged();
        return item;
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getCookTimeTotal() {
        return cookTimeTotal;
    }

    public void setCookCrafting(int value,int data) {
        cookTime=Math.max(0,value);
        cookTimeTotal=Math.max(0,data);
        inventoryChanged();
    }

    public boolean isCreative() {
        return creative;
    }

    public void setCreative(boolean value) {
        this.creative=value;
    }

    public boolean isContainer(ItemStack itemStack) {
        if(itemStack == null || itemStack.stackSize <= 0) return false;
        if(getContainer() != null) return isContainerValid(itemStack);
        return itemStack.getItem() == Items.bowl || itemStack.getItem() == Items.glass_bottle || itemStack.getItem() == Items.bucket || LegacyTagManager.getInstance().isItem(ResourceLocations.resourceLocations("serving_containers"),itemStack);
    }

    public boolean isContainerValid(ItemStack itemStack) {
        if(itemStack == null || itemStack.stackSize <= 0) return false;
        ItemStack stack=getContainer();
        return stack != null && itemStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(itemStack,stack);
    }

    public void awardUsedRecipes(EntityPlayer entityPlayer,int value) {
        if(entityPlayer == null || entityPlayer.worldObj.isRemote || value <= 0 || usedRecipeTracker.isEmpty()) return;
        float data=0;
        LegacyRecipeManager legacyRecipeManager=LegacyRecipeManager.getInstance();
        for(Map.Entry<ResourceLocation,Integer> resourceLocationIntegerEntry : usedRecipeTracker.entrySet()) {
            Optional<CookingPotRecipe> cookingPotRecipeOptional=legacyRecipeManager.getRecipe(ModRecipeTypes.COOKING,resourceLocationIntegerEntry.getKey());
            if(cookingPotRecipeOptional.isPresent()) data += cookingPotRecipeOptional.get().getExperience() * resourceLocationIntegerEntry.getValue();
        }
        int metadata=MathHelper.floor_float(data);
        if(worldObj.rand.nextFloat() < data - metadata) metadata++;
        while (metadata > 0) {
            int index= EntityXPOrb.getXPSplit(metadata);
            metadata -= index;
            worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj,entityPlayer.posX,entityPlayer.posY + 0.5,entityPlayer.posZ,index));
        }
        usedRecipeTracker.clear();
        inventoryChanged();
    }

    public ItemStack getMeal() {
        return inventory[MEAL_DISPLAY_SLOT];
    }

    private boolean doesMealHaveContainer(ItemStack itemStack) {
        return itemStack != null && itemStack.stackSize > 0 && getContainer() != null;
    }

    private boolean moveMealToOutput() {
        return moveMealToOutput(Integer.MAX_VALUE) > 0;
    }

    private int moveMealToOutput(int values) {
        ItemStack itemStack=getMeal();
        ItemStack stack=inventory[OUTPUT_SLOT];
        if(itemStack == null || itemStack.stackSize <= 0 || values <= 0) return 0;
        int value=Math.min(getInventoryStackLimit(),itemStack.getMaxStackSize());
        if(stack != null) {
            if(!stack.isItemEqual(itemStack)) return 0;
            if(!ItemStack.areItemStackTagsEqual(stack,itemStack)) return 0;
            value=Math.min(value,stack.getMaxStackSize());
        }
        int data=stack == null ? 0 : stack.stackSize;
        int metadata=value - data;
        if(metadata <= 0) return 0;
        int index=Math.min(itemStack.stackSize,Math.min(values,metadata));
        if(index <= 0) return 0;
        if(stack == null) {
            ItemStack item=itemStack.copy();
            item.stackSize=index;
            inventory[OUTPUT_SLOT]=item;
        } else {
            stack.stackSize += index;
        }
        itemStack.stackSize -= index;
        if(itemStack.stackSize <= 0) {
            inventory[MEAL_DISPLAY_SLOT]=null;
            mealContainerStack=null;
        }
        return index;
    }

    private boolean useStoredContainersOnMeal() {
        ItemStack itemStack=inventory[CONTAINER_SLOT];
        if(!isContainerValid(itemStack)) return false;
        int value=moveMealToOutput(itemStack.stackSize);
        if(value <= 0) return false;
        itemStack.stackSize -= value;
        if(itemStack.stackSize <= 0) inventory[CONTAINER_SLOT]=null;
        return true;
    }


    public int getComparatorLevel() {
        float value=0;
        int data=0;
        for(int index=0;index < INVENTORY_SIZE;index++) {
            ItemStack itemStack=inventory[index];
            if(itemStack == null) continue;
            int metadata=index == MEAL_DISPLAY_SLOT ? 64 : Math.min(getInventoryStackLimit(),itemStack.getMaxStackSize());
            value += (float)itemStack.stackSize / Math.max(1,metadata);
            data++;
        }
        return MathHelper.floor_float(value / INVENTORY_SIZE * 14.0f) + (data > 0 ? 1 : 0);
    }

    public void dropItem() {
        if(worldObj == null || worldObj.isRemote) return;
        for(int index=0;index < INVENTORY_SIZE;index++) {
            if(index == MEAL_DISPLAY_SLOT) continue;
            ItemStack itemStack=inventory[index];
            if(itemStack == null) continue;
            EntityItem entityItem=new EntityItem(worldObj,xCoord + 0.5,yCoord + 0.5,zCoord + 0.5,itemStack.copy());
            entityItem.delayBeforeCanPickup=10;
            worldObj.spawnEntityInWorld(entityItem);
            inventory[index]=null;
        }
        inventoryChanged();
    }

    public NBTTagCompound writeMeal(NBTTagCompound nbtTagCompound) {
        ItemStack itemStack=getMeal();
        if(itemStack == null) return nbtTagCompound;
        nbtTagCompound.setTag("Meal",itemStack.writeToNBT(new NBTTagCompound()));
        if(mealContainerStack != null) nbtTagCompound.setTag("Container",mealContainerStack.writeToNBT(new NBTTagCompound()));
        if(customName != null) nbtTagCompound.setString("CustomName",customName);
        return nbtTagCompound;
    }

    public void readMeal(NBTTagCompound nbtTagCompound) {
        if(nbtTagCompound == null || !nbtTagCompound.hasKey("Meal",10)) return;
        ItemStack itemStack=ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("Meal"));
        if(itemStack == null || itemStack.stackSize <= 0) return;
        inventory[MEAL_DISPLAY_SLOT]=itemStack;
        mealContainerStack=nbtTagCompound.hasKey("Container",10) ? ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("Container")) : null;
        if(nbtTagCompound.hasKey("CustomName",8)) customName=nbtTagCompound.getString("CustomName");
        inventoryChanged();
    }

    private boolean hasInput() {
        for(int index=0;index < MEAL_DISPLAY_SLOT;index++) {
            if(inventory[index] != null && inventory[index].stackSize > 0) return true;
        }
        return false;
    }

    private CookingPotRecipe getMatchingRecipe() {
        LegacyRecipeManager legacyRecipeManager=LegacyRecipeManager.getInstance();
        if(lastRecipeID != null) {
            Optional<CookingPotRecipe> cookingPotRecipeOptional=legacyRecipeManager.getRecipe(ModRecipeTypes.COOKING,lastRecipeID);
            if(cookingPotRecipeOptional.isPresent() && cookingPotRecipeOptional.get().matches(inventory)) return cookingPotRecipeOptional.get();
        }
        if(!checkNewRecipe) return null;
        Optional<CookingPotRecipe> cookingPotRecipeOptional=legacyRecipeManager.getRecipeT(ModRecipeTypes.COOKING,inventory);
        if(!cookingPotRecipeOptional.isPresent()) {
            checkNewRecipe=false;
            return null;
        }
        CookingPotRecipe cookingPotRecipe=cookingPotRecipeOptional.get();
        if(lastRecipeID == null || !lastRecipeID.equals(cookingPotRecipe.getId())) {
            cookTime=0;
            lastRecipeID=cookingPotRecipe.getId();
            markDirty();
        }
        checkNewRecipe=false;
        return cookingPotRecipe;
    }

    private boolean canCook(CookingPotRecipe cookingPotRecipe) {
        if(!hasInput() || !cookingPotRecipe.matches(inventory)) return false;
        ItemStack itemStack=cookingPotRecipe.getResultItem();
        if(itemStack == null || itemStack.stackSize <= 0 || itemStack.stackSize > 64) return false;
        ItemStack stack=getMeal();
        if(stack == null) return true;
        if(!stack.isItemEqual(itemStack) || !ItemStack.areItemStackTagsEqual(stack,itemStack)) return false;
        if(stack.stackSize + itemStack.stackSize > 64) return false;
        ItemStack item=cookingPotRecipe.getOutputContainer();
        return isStack(getContainer(),item);
    }

    private static boolean isStack(ItemStack itemStack,ItemStack stack) {
        if(itemStack == null || itemStack.stackSize <= 0) return stack == null || stack.stackSize <= 0;
        return stack != null && stack.stackSize > 0 && itemStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(itemStack,stack);
    }

    private boolean processCooking(CookingPotRecipe cookingPotRecipe) {
        cookTimeTotal=cookingPotRecipe.getCookTime();
        cookTime++;
        if(cookTime < cookTimeTotal) {
            markDirty();
            return false;
        }
        ItemStack itemStack=cookingPotRecipe.getResultItem();
        if(!canCook(cookingPotRecipe)) {
            cookTime=0;
            markDirty();
            return false;
        }
        cookTime=0;
        mealContainerStack=cookingPotRecipe.getOutputContainer();
        if(inventory[MEAL_DISPLAY_SLOT] == null) {
            inventory[MEAL_DISPLAY_SLOT]=itemStack;
        } else {
            inventory[MEAL_DISPLAY_SLOT].stackSize += itemStack.stackSize;
        }
        setRecipeUsed(cookingPotRecipe);
        for(int index=0;index < MEAL_DISPLAY_SLOT;index++) {
            ItemStack stack=inventory[index];
            if(stack == null || stack.stackSize <= 0) continue;
            ItemStack item=getRemainderStack(stack);
            if(item != null) ejectIngredientRemainder(item);
            if(--stack.stackSize <= 0) inventory[index]=null;
        }
        checkNewRecipe=true;
        return true;
    }

    private ItemStack getRemainderStack(ItemStack itemStack) {
        Item item=itemStack.getItem();
        if(item == null) return null;
        if(item.hasContainerItem() && item.getContainerItem() != null) return new ItemStack(item.getContainerItem());
        if(item == Items.mushroom_stew) return new ItemStack(Items.bowl);
        if(item == Items.potionitem || item == Items.experience_bottle) return new ItemStack(Items.glass_bottle);
        if(item == Items.milk_bucket || item == Items.water_bucket || item == Items.lava_bucket) return new ItemStack(Items.bucket);
        return null;
    }

    protected void ejectIngredientRemainder(ItemStack itemStack) {
        if(itemStack == null || itemStack.stackSize <= 0 || worldObj == null || worldObj.isRemote) return;
        int value=worldObj.getBlockMetadata(xCoord,yCoord,zCoord) & 3;
        int[] x={-1,0,1,0};
        int[] z={0,-1,0,1};
        EntityItem entityItem=new EntityItem(worldObj,xCoord + 0.5 + x[value] * 0.25,yCoord + 0.7,zCoord + 0.5 + z[value] * 0.25,itemStack.copy());
        entityItem.motionX=x[value] * 0.08;
        entityItem.motionY=0.25;
        entityItem.motionZ=z[value] * 0.08;
        entityItem.delayBeforeCanPickup=10;
        worldObj.spawnEntityInWorld(entityItem);
    }

    public void setRecipeUsed(CookingPotRecipe cookingPotRecipe) {
        if(cookingPotRecipe == null) return;
        ResourceLocation resourceLocation=cookingPotRecipe.getId();
        Integer integer=usedRecipeTracker.get(resourceLocation);
        usedRecipeTracker.put(resourceLocation,integer == null ? 1 : integer + 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        for (int index=0;index < INVENTORY_SIZE;index++) {
            inventory[index]=null;
        }
        NBTTagList nbtTagList=nbtTagCompound.getTagList("Inventory",10);
        for (int value=0;value < nbtTagList.tagCount();value++) {
            NBTTagCompound tagCompound=nbtTagList.getCompoundTagAt(value);
            int data=tagCompound.getByte("Slot") & 255;
            if(data >= INVENTORY_SIZE) continue;
            ItemStack itemStack=ItemStack.loadItemStackFromNBT(tagCompound);
            if(itemStack != null && itemStack.stackSize > 0) inventory[data]=itemStack;
        }
        mealContainerStack=nbtTagCompound.hasKey("Container",10) ? ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("Container")) : null;
        cookTime=Math.max(0,nbtTagCompound.getInteger("CookTime"));
        cookTimeTotal=Math.max(0,nbtTagCompound.getInteger("CookTimeTotal"));
        customName=nbtTagCompound.hasKey("CustomName",8) ? nbtTagCompound.getString("CustomName") : null;
        usedRecipeTracker.clear();
        NBTTagCompound tagCompound=nbtTagCompound.getCompoundTag("UsedRecipes");
        for(Object object : tagCompound.func_150296_c()) {
            String string=(String)object;
            ResourceLocation resourceLocation=ResourceLocations.tryParse(string);
            if(resourceLocation != null && tagCompound.getInteger(string) > 0) usedRecipeTracker.put(resourceLocation,tagCompound.getInteger(string));
        }
        lastRecipeID=null;
        checkNewRecipe=true;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        NBTTagList nbtTagList=new NBTTagList();
        for(int index=0;index < INVENTORY_SIZE;index++) {
            ItemStack itemStack=inventory[index];
            if(itemStack == null) continue;
            NBTTagCompound tagCompound=itemStack.writeToNBT(new NBTTagCompound());
            tagCompound.setByte("Slot",(byte)index);
            nbtTagList.appendTag(tagCompound);
        }
        nbtTagCompound.setTag("Inventory",nbtTagList);
        if(mealContainerStack != null) nbtTagCompound.setTag("Container",mealContainerStack.writeToNBT(new NBTTagCompound()));
        nbtTagCompound.setInteger("CookTime",cookTime);
        nbtTagCompound.setInteger("CookTimeTotal",cookTimeTotal);
        if(customName != null) nbtTagCompound.setString("CustomName",customName);
        NBTTagCompound tagCompound=new NBTTagCompound();
        for(Map.Entry<ResourceLocation,Integer> resourceLocationIntegerEntry : usedRecipeTracker.entrySet()) tagCompound.setInteger(ResourceLocations.getString(resourceLocationIntegerEntry.getKey()),resourceLocationIntegerEntry.getValue());
        nbtTagCompound.setTag("UsedRecipes",tagCompound);
    }

    @Override
    public int getSizeInventory() {
        return INVENTORY_SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int value) {
        return value >= 0 && value < INVENTORY_SIZE ? inventory[value] : null;
    }

    @Override
    public ItemStack decrStackSize(int value,int data) {
        if(value < 0 || value >= INVENTORY_SIZE || data <= 0 || inventory[value] == null) return null;
        ItemStack itemStack;
        if(inventory[value].stackSize <= data) {
            itemStack=inventory[value];
            inventory[value]=null;
        } else {
            itemStack=inventory[value].splitStack(data);
            if(inventory[value].stackSize <= 0 ) inventory[value]=null;
        }
        if(value == MEAL_DISPLAY_SLOT && inventory[value] == null) mealContainerStack=null;
        if(value >= 0 && value < MEAL_DISPLAY_SLOT) checkNewRecipe=true;
        inventoryChanged();
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int value) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int value,ItemStack itemStack) {
        if(value < 0 || value >= INVENTORY_SIZE) return;
        inventory[value]=itemStack;
        if(itemStack != null) itemStack.stackSize=Math.min(itemStack.stackSize,value == MEAL_DISPLAY_SLOT ? 64 : Math.min(getInventoryStackLimit(),itemStack.getMaxStackSize()));
        if(value == MEAL_DISPLAY_SLOT && itemStack == null) mealContainerStack=null;
        if(value >= 0 && value < MEAL_DISPLAY_SLOT) checkNewRecipe=true;
        inventoryChanged();
    }

    public void setCustomName(String string) {
        customName=string == null || string.isEmpty() ? null : string;
        inventoryChanged();
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : "container.delightlegacy.cooking_pot";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return worldObj != null && worldObj.getTileEntity(xCoord,yCoord,zCoord) == this && entityPlayer.getDistanceSq(xCoord + 0.5,yCoord + 0.5,zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int value,ItemStack itemStack) {
        if(itemStack == null || itemStack.stackSize <= 0) return false;
        if(value >= 0 && value <= 5) return true;
        if(value == CONTAINER_SLOT) return isContainer(itemStack);
        return false;
    }


    @Override
    public int[] getAccessibleSlotsFromSide(int value) {
        if(value == 1) return INPUT_INVENTORY.clone();
        return OUTPUT_INVENTORY.clone();
    }

    @Override
    public boolean canInsertItem(int value,ItemStack itemStack,int data) {
        return (data == 1 ? value >= 0 && value <= 5 : value == CONTAINER_SLOT) && isItemValidForSlot(value,itemStack);
    }

    @Override
    public boolean canExtractItem(int value,ItemStack itemStack,int data) {
        return (data != 1 && value == OUTPUT_SLOT) || (data == 1 && value >= 0 && value <= 5);
    }
}
