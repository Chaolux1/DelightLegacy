package net.chaolux.delightlegacy.legacy.assets.models;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.legacy.assets.LegacyPackType;
import net.chaolux.delightlegacy.legacy.assets.LegacyResources;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.chaolux.delightlegacy.legacy.assets.ResourcePaths;
import net.chaolux.delightlegacy.legacy.assets.blockstates.LegacyBlockStateDefinition;
import net.chaolux.delightlegacy.legacy.assets.textures.LegacyTextureAtlas;
import net.chaolux.delightlegacy.legacy.assets.textures.LegacyTextureAtlasSprite;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.IOException;
import java.util.*;

public class LegacyModelManager implements IResourceManagerReloadListener {
    private static final LegacyModelManager INSTANCE=new LegacyModelManager();
    private final LegacyModelJsonLoader legacyModelJsonLoader=new LegacyModelJsonLoader();
    private final LegacyModelBaker legacyModelBaker=new LegacyModelBaker();
    private final LegacyTextureAtlas legacyTextureAtlas=new LegacyTextureAtlas();
    private final Map<ResourceLocation,LegacyModelDefinition> map=new LinkedHashMap<ResourceLocation,LegacyModelDefinition>();
    private final Map<ResourceLocation,LegacyBakedModel> bakedModelMap=new LinkedHashMap<ResourceLocation,LegacyBakedModel>();
    private final Map<ResourceLocation, LegacyBlockStateDefinition> blockStateDefinitionMap=new LinkedHashMap<ResourceLocation,LegacyBlockStateDefinition>();
    private boolean registered;
    public static LegacyModelManager getInstance() {
        return INSTANCE;
    }

    public void register(IResourceManager iResourceManager) {
        if(registered) return;
        MinecraftForge.EVENT_BUS.register(this);
        if(iResourceManager instanceof IReloadableResourceManager) ((IReloadableResourceManager) iResourceManager).registerReloadListener(this);
        registered=true;
    }

    @Override
    public synchronized void onResourceManagerReload(IResourceManager iResourceManager) {
        clear();
    }

    @SubscribeEvent
    public synchronized void onTextureStitch(TextureStitchEvent.Pre pre) {
        if(pre.map != Minecraft.getMinecraft().getTextureMapBlocks()) return;
        clear();
        Set<ResourceLocation> resourceLocationSet=collectTexture();
        legacyTextureAtlas.build(pre.map,resourceLocationSet);
        DelightLegacy.LOGGER.info("Prepare {} JSON model texture",resourceLocationSet.size());
    }

    public synchronized LegacyBakedModel getBakedModel(ResourceLocation resourceLocation) {
        LegacyBakedModel legacyBakedModel=bakedModelMap.get(resourceLocation);
        if(legacyBakedModel != null) return legacyBakedModel;
        LegacyModelDefinition legacyModelDefinition=resolve(resourceLocation,new LinkedHashSet<ResourceLocation>());
        legacyBakedModel=legacyModelBaker.bake(legacyModelDefinition);
        bakedModelMap.put(resourceLocation,legacyBakedModel);
        return legacyBakedModel;
    }

    public synchronized LegacyBlockStateDefinition getBlockState(ResourceLocation resourceLocation) {
        LegacyBlockStateDefinition legacyBlockStateDefinition=blockStateDefinitionMap.get(resourceLocation);
        if(legacyBlockStateDefinition != null) return legacyBlockStateDefinition;
        try {
            legacyBlockStateDefinition=legacyModelJsonLoader.legacyBlockStateDefinition(resourceLocation);
        } catch (Exception exception) {
            DelightLegacy.LOGGER.error("Could not load blockstate {}",ResourceLocations.getString(resourceLocation),exception);
            legacyBlockStateDefinition=new LegacyBlockStateDefinition(Collections.singletonList(new LegacyBlockStateDefinition.Variant(LegacyBlockStateDefinition.Condition.TRUE,Collections.singletonList(new LegacyBlockStateDefinition.Model(ResourceLocations.locations("minecraft","block/missingno"),0,0,false,1)))),Collections.<LegacyBlockStateDefinition.Part>emptyList());
        }
        blockStateDefinitionMap.put(resourceLocation,legacyBlockStateDefinition);
        return legacyBlockStateDefinition;
    }

    public List<LegacyBlockStateDefinition.Model> getModel(IBlockAccess iBlockAccess, int x, int y, int z, Block block) {
        LegacyModelRegistry.BlockRegistration blockRegistration=LegacyModelRegistry.getBlock(block);
        if(blockRegistration == null) return Collections.emptyList();
        int data=iBlockAccess.getBlockMetadata(x,y,z);
        Map<String,String> stringStringMap=blockRegistration.getMapper().getBlockState(iBlockAccess,x,y,z,block,data);
        long seed=currentSeed(x,y,z);
        return getBlockState(blockRegistration.getBlock()).modelList(stringStringMap,seed);
    }

    public List<LegacyBlockStateDefinition.Model> getInventoryModel(Block block,int data) {
        LegacyModelRegistry.BlockRegistration blockRegistration=LegacyModelRegistry.getBlock(block);
        if(blockRegistration == null) return Collections.emptyList();
        Map<String,String> stringStringMap=blockRegistration.getMapper().getItemState(block,data);
        return getBlockState(blockRegistration.getBlock()).modelList(stringStringMap,0);
    }

    public LegacyBakedModel getItemModel(ItemStack itemStack) {
        if(itemStack == null) return null;
        ResourceLocation resourceLocation=LegacyModelRegistry.getItemModel(itemStack.getItem());
        if(resourceLocation == null) return null;
        LegacyBakedModel legacyBakedModel=getBakedModel(resourceLocation);
        ResourceLocation location=find(itemStack,legacyBakedModel);
        return location == null ? legacyBakedModel : getBakedModel(location);
    }

    public LegacyTextureAtlas getTexture() {
        return legacyTextureAtlas;
    }

    public ResourceLocation find(ItemStack itemStack,LegacyBakedModel legacyBakedModel) {
        ResourceLocation resourceLocation=null;
        for(LegacyModelDefinition.Override override : legacyBakedModel.getOverride()) {
            boolean matches=true;
            for (Map.Entry<ResourceLocation,Float> entry : override.getPredicate().entrySet()) {
                float value=LegacyModelRegistry.getItemValue(itemStack,entry.getKey());
                if(value < entry.getValue()) {
                    matches=false;
                    break;
                }
            }
            if(matches) resourceLocation=override.getOverrides();
        }
        return resourceLocation;
    }

    private Set<ResourceLocation> collectTexture() {
        Set<ResourceLocation> resourceLocationSet=new LinkedHashSet<ResourceLocation>();
        Set<ResourceLocation> resourceLocations=new HashSet<ResourceLocation>();
        for (ResourceLocation resourceLocation : LegacyModelRegistry.getItems().values()) {
            collectModel(resourceLocation,resourceLocationSet,resourceLocations);
        }
        for (LegacyModelRegistry.BlockRegistration blockRegistration : LegacyModelRegistry.getBlocks().values()) {
            LegacyBlockStateDefinition legacyBlockStateDefinition=getBlockState(blockRegistration.getBlock());
            for(ResourceLocation resourceLocation : legacyBlockStateDefinition.resourceLocationSet()) {
                collectModel(resourceLocation,resourceLocationSet,resourceLocations);
            }
        }
        return resourceLocationSet;
    }

    private void collectModel(ResourceLocation resourceLocation,Set<ResourceLocation> resourceLocationSet,Set<ResourceLocation> resourceLocations) {
        if(!resourceLocations.add(resourceLocation)) return;
        LegacyBakedModel legacyBakedModel=getBakedModel(resourceLocation);
        resourceLocationSet.addAll(legacyBakedModel.getLocation());
        for (LegacyModelDefinition.Override override : legacyBakedModel.getOverride()) {
            collectModel(override.getOverrides(),resourceLocationSet,resourceLocations);
        }
    }

    private LegacyModelDefinition resolve(ResourceLocation resourceLocation,Set<ResourceLocation> resourceLocationSet) {
        LegacyModelDefinition legacyModelDefinition=map.get(resourceLocation);
        if(legacyModelDefinition != null) return legacyModelDefinition;
        if(!resourceLocationSet.add(resourceLocation)) throw new IllegalStateException("Model parent chain at " + ResourceLocations.getString(resourceLocation));
        LegacyModelDefinition modelDefinition=load(resourceLocation);
        LegacyModelDefinition definition;
        if(modelDefinition.getLocation() == null) {
            definition=modelDefinition;
        } else {
            LegacyModelDefinition legacy=resolve(modelDefinition.getLocation(),resourceLocationSet);
            definition=merge(legacy,modelDefinition);
        }
        resourceLocationSet.remove(resourceLocation);
        map.put(resourceLocation,definition);
        return definition;
    }

    private LegacyModelDefinition load(ResourceLocation resourceLocation) {
        LegacyModelDefinition legacyModelDefinition=LegacyBuildModels.get(resourceLocation);
        if(legacyModelDefinition != null) return legacyModelDefinition;
        ResourceLocation location= ResourcePaths.model(resourceLocation);
        if(LegacyResources.is(LegacyPackType.ASSETS,location)) {
            try {
                return legacyModelJsonLoader.legacyModelDefinition(resourceLocation);
            } catch (IOException exception) {
                throw new RuntimeException("Can not load model" + ResourceLocations.getString(resourceLocation),exception);
            }
        }
        LegacyModelDefinition modelDefinition=LegacyBuildModels.legacyModelDefinition(resourceLocation);
        if(modelDefinition != null) return modelDefinition;
        DelightLegacy.LOGGER.warn("Missing model {}",ResourceLocations.getString(resourceLocation));
        Map<String,String> stringStringMap=new LinkedHashMap<String,String>();
        stringStringMap.put("all","minecraft:block/stone");
        LegacyModelDefinition definition=LegacyBuildModels.get(ResourceLocations.locations("minecraft","block/cube_all"));
        return new LegacyModelDefinition(resourceLocation,null,definition.getOcclusion(),definition.getGUI(),stringStringMap,definition.getElement(),true,definition.getTransform(),definition.getOverride(),false,false,null);
    }

    private LegacyModelDefinition merge(LegacyModelDefinition legacyModelDefinition,LegacyModelDefinition modelDefinition) {
        Map<String,String> stringStringMap=new LinkedHashMap<String,String>(legacyModelDefinition.getTexture());
        stringStringMap.putAll(modelDefinition.getTexture());
        Map<String,LegacyModelDefinition.Transform> transformMap=new LinkedHashMap<String,LegacyModelDefinition.Transform>(legacyModelDefinition.getTransform());
        transformMap.putAll(modelDefinition.getTransform());
        List<LegacyModelDefinition.Element> elementList=modelDefinition.isDefine() ? modelDefinition.getElement() : legacyModelDefinition.getElement();
        List<LegacyModelDefinition.Override> overrideList=modelDefinition.getOverride().isEmpty() ? legacyModelDefinition.getOverride() : modelDefinition.getOverride();
        return new LegacyModelDefinition(modelDefinition.getId(),null,modelDefinition.getOcclusion() != null ? modelDefinition.getOcclusion() : legacyModelDefinition.getOcclusion(),modelDefinition.getGUI() != null ? modelDefinition.getGUI() : legacyModelDefinition.getGUI(),stringStringMap,elementList,true,transformMap,overrideList,modelDefinition.isGenerated() || legacyModelDefinition.isGenerated(),modelDefinition.isHandheld() || legacyModelDefinition.isHandheld(),modelDefinition.getString() != null ? modelDefinition.getString() : legacyModelDefinition.getString());
    }

    private synchronized void clear() {
        map.clear();
        bakedModelMap.clear();
        blockStateDefinitionMap.clear();
    }

    private static long currentSeed(int x,int y,int z) {
        long seed=(long) (x * 3129871) ^ (long) z * 116129781L ^ (long) y;
        seed=seed * seed * 42317861L + seed * 11L;
        return seed >> 16;
    }

    public IIcon getParticle(Block block,int data) {
        for(LegacyBlockStateDefinition.Model model : getInventoryModel(block,data)) {
            ResourceLocation resourceLocation=getBakedModel(model.getModel()).getParticle();
            if(resourceLocation != null) return legacyTextureAtlas.getIcon(resourceLocation);
        }
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");
    }
}
