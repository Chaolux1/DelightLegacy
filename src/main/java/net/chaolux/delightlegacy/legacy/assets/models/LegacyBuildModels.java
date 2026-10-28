/*
Copyright (c) Forge Development LLC and contributors
SPDX-License-Identifier: LGPL-2.1-only
Modified by Chaolux, 2026
 */
package net.chaolux.delightlegacy.legacy.assets.models;

import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.util.ResourceLocation;

import java.util.*;

final class LegacyBuildModels {
    public static LegacyModelDefinition get(ResourceLocation resourceLocation) {
        String namespace=ResourceLocations.namespace(resourceLocation);
        String string=ResourceLocations.path(resourceLocation);
        if(!"minecraft".equals(namespace)) return null;
        if("block/block".equals(string)) return empty(resourceLocation);
        if("block/cube".equals(string)) return cube(resourceLocation,"#down","#up","#north","#south","#west","#east");
        if("block/cube_all".equals(string)) return cubeAll(resourceLocation,"#all");
        if("block/cube_column".equals(string)) return cube(resourceLocation,"#end","#end","#side","#side","#side","#side");
        if("block/cube_column_horizontal".equals(string)) return cube(resourceLocation,"#side","#side","#end","#end","#side","#side");
        if("block/cube_bottom_top".equals(string)) return cube(resourceLocation,"#bottom","#top","#side","#side","#side","#side");
        if("block/orientable".equals(string)) return cube(resourceLocation,"#top","#top","#front","#side","#side","#side");
        if("block/orientable_with_bottom".equals(string)) return cube(resourceLocation,"#bottom","#top","#front","#side","#side","#side");
        if("block/orientable_vertical".equals(string)) return cube(resourceLocation,"#front","#front","#side","#side","#side","#side");
        if("block/cross".equals(string) || "block/tinted_cross".equals(string)) return cross(resourceLocation,"#cross");
        if("block/crop".equals(string)) return cross(resourceLocation,"#crop");
        if("item/generated".equals(string)) return generated(resourceLocation,false);
        if("item/handheld".equals(string) || "item/handheld_rod".equals(string)) return generated(resourceLocation,true);
        return null;
    }

   public static LegacyModelDefinition legacyModelDefinition(ResourceLocation resourceLocation) {
        String namespace=ResourceLocations.namespace(resourceLocation);
        String string=ResourceLocations.path(resourceLocation);
        if(!"minecraft".equals(namespace)) return null;
        if(string.startsWith("block/")) {
            Map<String,String> texture=new LinkedHashMap<String,String>();
            texture.put("all",ResourceLocations.getString(resourceLocation));
            LegacyModelDefinition legacyModelDefinition=cubeAll(resourceLocation,"#all");
            return copy(legacyModelDefinition,texture);
        }
        if(string.startsWith("item/")) {
            Map<String,String> texture=new LinkedHashMap<String,String>();
            texture.put("layer0",ResourceLocations.getString(resourceLocation));
            LegacyModelDefinition legacyModelDefinition=generated(resourceLocation,false);
            return copy(legacyModelDefinition,texture);
        }
        return null;
   }

   private static LegacyModelDefinition empty(ResourceLocation resourceLocation) {
        Map<String,LegacyModelDefinition.Transform> stringTransformMap=display(false);
        stringTransformMap.put("ground",blockTransform().get("ground"));
       return new LegacyModelDefinition(resourceLocation,null,Boolean.TRUE,null,new LinkedHashMap<String,String>(),new ArrayList<LegacyModelDefinition.Element>(),true,display(false),new ArrayList<LegacyModelDefinition.Override>(),false,false,null);
   }

   private static LegacyModelDefinition cube(ResourceLocation resourceLocation,String down,String up,String north,String south,String west,String east) {
        Map<LegacyDirection,LegacyModelDefinition.Face> faceMap=new EnumMap<LegacyDirection,LegacyModelDefinition.Face>(LegacyDirection.class);
        faceMap.put(LegacyDirection.DOWN,face(down,LegacyDirection.DOWN));
        faceMap.put(LegacyDirection.UP,face(up,LegacyDirection.UP));
        faceMap.put(LegacyDirection.NORTH,face(north,LegacyDirection.NORTH));
        faceMap.put(LegacyDirection.SOUTH,face(south,LegacyDirection.SOUTH));
        faceMap.put(LegacyDirection.WEST,face(west,LegacyDirection.WEST));
        faceMap.put(LegacyDirection.EAST,face(east,LegacyDirection.EAST));
        List<LegacyModelDefinition.Element> elementList=new ArrayList<LegacyModelDefinition.Element>();
        elementList.add(new LegacyModelDefinition.Element(new float[] {0.0f,0.0f,0.0f},new float[] {16.0f,16.0f,16.0f},null,true,faceMap));
        return new LegacyModelDefinition(resourceLocation,null,Boolean.TRUE,null,new LinkedHashMap<String,String>(),elementList,true,blockTransform(),new ArrayList<LegacyModelDefinition.Override>(),false,false,null);
   }

   private static LegacyModelDefinition cubeAll(ResourceLocation resourceLocation,String string) {
        return cube(resourceLocation,string,string,string,string,string,string);
   }

   private static LegacyModelDefinition generated(ResourceLocation resourceLocation,boolean generated) {
        return new LegacyModelDefinition(resourceLocation,null,Boolean.TRUE,"front",new LinkedHashMap<String,String>(),new ArrayList<LegacyModelDefinition.Element>(),true,display(generated),new ArrayList<LegacyModelDefinition.Override>(),true,generated,null);
   }

   private static LegacyModelDefinition cross(ResourceLocation resourceLocation,String string) {
        List<LegacyModelDefinition.Element> elementList=new ArrayList<LegacyModelDefinition.Element>();
        elementList.add(crossElement(string));
        elementList.add(crossElements(string));
        return new LegacyModelDefinition(resourceLocation,null,Boolean.FALSE,null,new LinkedHashMap<String,String>(),elementList,true,new LinkedHashMap<String,LegacyModelDefinition.Transform>(),new ArrayList<LegacyModelDefinition.Override>(),false,false,null);
   }

   private static LegacyModelDefinition.Element crossElement(String string) {
        Map<LegacyDirection,LegacyModelDefinition.Face> faceMap=new EnumMap<LegacyDirection,LegacyModelDefinition.Face>(LegacyDirection.class);
        faceMap.put(LegacyDirection.NORTH,new LegacyModelDefinition.Face(new float[] {0.0f,0.0f,16.0f,16.0f},string,null,0,-1));
        faceMap.put(LegacyDirection.SOUTH,new LegacyModelDefinition.Face(new float[] {0.0f,0.0f,16.0f,16.0f},string,null,0,-1));
        return new LegacyModelDefinition.Element(new float[] {0.8f,0.0f,8.0f},new float[] {15.2f,16.0f,8.0f},new LegacyModelDefinition.Rotation(new float[] {8.0f,8.0f,8.0f},LegacyModelDefinition.Axis.y,45.0f,true),false,faceMap);
   }

    private static LegacyModelDefinition.Element crossElements(String string) {
        Map<LegacyDirection,LegacyModelDefinition.Face> faceMap=new EnumMap<LegacyDirection,LegacyModelDefinition.Face>(LegacyDirection.class);
        faceMap.put(LegacyDirection.WEST,new LegacyModelDefinition.Face(new float[] {0.0f,0.0f,16.0f,16.0f},string,null,0,-1));
        faceMap.put(LegacyDirection.EAST,new LegacyModelDefinition.Face(new float[] {0.0f,0.0f,16.0f,16.0f},string,null,0,-1));
        return new LegacyModelDefinition.Element(new float[] {8.0f,0.0f,0.8f},new float[] {8.0f,16.0f,15.2f},new LegacyModelDefinition.Rotation(new float[] {8.0f,8.0f,8.0f},LegacyModelDefinition.Axis.y,45.0f,true),false,faceMap);
    }

    private static LegacyModelDefinition.Face face(String string,LegacyDirection legacyDirection) {
        return new LegacyModelDefinition.Face(null,string,legacyDirection,0,-1);
    }

    static LegacyModelDefinition.Transform getItemTransform(String string,boolean handheld) {
        LegacyModelDefinition.Transform transform=display(handheld).get(string);
        return transform == null ? LegacyModelDefinition.Transform.TRANSFORM : transform;
    }

    private static Map<String,LegacyModelDefinition.Transform> display(boolean handheld) {
        Map<String,LegacyModelDefinition.Transform> transformMap=new LinkedHashMap<String,LegacyModelDefinition.Transform>();
        if(handheld) {
            transformMap.put("thirdperson_righthand",transform(new float[] {0.0f,-90.0f,55.0f},new float[] {0.0f,4.0f,0.5f},new float[] {0.85f,0.85f,0.85f}));
            transformMap.put("firstperson_righthand",transform(new float[] {0.0f,-90.0f,25.0f},new float[] {1.13f,3.2f,1.13f},new float[] {0.68f,0.68f,0.68f}));
        } else {
            transformMap.put("thirdperson_righthand",transform(new float[] {0.0f,0.0f,0.0f},new float[] {0.0f,3.0f,1.0f},new float[] {0.55f,0.55f,0.55f}));
            transformMap.put("firstperson_righthand",transform(new float[] {0.0f,-90.0f,25.0f},new float[] {1.13f,3.2f,1.13f},new float[] {0.68f,0.68f,0.68f}));
        }
        transformMap.put("ground",transform(new float[] {0.0f,0.0f,0.0f},new float[] {0.0f,2.0f,0.0f},new float[] {0.5f,0.5f,0.5f}));
        transformMap.put("head",transform(new float[] {0.0f,180.0f,0.0f},new float[] {0.0f,13.0f,7.0f},new float[] {1.0f,1.0f,1.0f}));
        transformMap.put("gui",LegacyModelDefinition.Transform.TRANSFORM);
        transformMap.put("fixed",transform(new float[] {0.0f,180.0f,0.0f},new float[] {0.0f,0.0f,0.0f},new float[] {1.0f,1.0f,1.0f}));
        return transformMap;
    }

    private static LegacyModelDefinition.Transform transform(float[] rotate,float[] transform,float[] scale) {
        return new LegacyModelDefinition.Transform(rotate,transform,scale);
    }

    private static LegacyModelDefinition copy(LegacyModelDefinition legacyModelDefinition,Map<String,String> texture) {
        return new LegacyModelDefinition(legacyModelDefinition.getId(),legacyModelDefinition.getLocation(),legacyModelDefinition.getOcclusion(),legacyModelDefinition.getGUI(),texture,legacyModelDefinition.getElement(),legacyModelDefinition.isDefine(),legacyModelDefinition.getTransform(),legacyModelDefinition.getOverride(),legacyModelDefinition.isGenerated(),legacyModelDefinition.isHandheld(),legacyModelDefinition.getString());
    }

    private static Map<String,LegacyModelDefinition.Transform> blockTransform() {
        Map<String,LegacyModelDefinition.Transform> stringTransformMap=new LinkedHashMap<String,LegacyModelDefinition.Transform>();
        stringTransformMap.put("ground",transform(new float[] {0.0f,0.0f,0.0f},new float[] {0.0f,3.0f,0.0f},new float[] {0.25f,0.25f,0.25f}));
        stringTransformMap.put("fixed",transform(new float[] {0.0f,0.0f,0.0f},new float[] {0.0f,0.0f,0.0f},new float[] {0.5f,0.5f,0.5f}));
        return stringTransformMap;
    }
}
