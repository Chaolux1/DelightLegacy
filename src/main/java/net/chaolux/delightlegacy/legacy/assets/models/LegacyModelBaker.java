package net.chaolux.delightlegacy.legacy.assets.models;

import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.util.ResourceLocation;

import java.util.*;

final class LegacyModelBaker {
    public LegacyBakedModel bake(LegacyModelDefinition legacyModelDefinition) {
        if(legacyModelDefinition.getString() != null && !legacyModelDefinition.getString().isEmpty()) throw new IllegalArgumentException("Custom model '" + legacyModelDefinition.getString() + "'");
        List<LegacyBakedModel.Quad> quadList=new ArrayList<LegacyBakedModel.Quad>();
        Set<ResourceLocation> resourceLocationSet=new LinkedHashSet<ResourceLocation>();
        for(LegacyModelDefinition.Element element : legacyModelDefinition.getElement()) {
            bakeElement(legacyModelDefinition,element,quadList,resourceLocationSet);
        }
        List<ResourceLocation> resourceLocationList=new ArrayList<ResourceLocation>();
        for (int index=0;index < 5;index++) {
            String string="layer" + index;
            if(!legacyModelDefinition.getTexture().containsKey(string)) continue;
            ResourceLocation resourceLocation=resolve(legacyModelDefinition,"#" + string);
            if(resourceLocation != null) {
                resourceLocationList.add(resourceLocation);
                resourceLocationSet.add(resourceLocation);
            }
        }
        ResourceLocation resourceLocation=resolve(legacyModelDefinition,"#particle");
        if(resourceLocation == null && !quadList.isEmpty()) resourceLocation=quadList.get(0).getLocation();
        if(resourceLocation == null && !resourceLocationList.isEmpty()) resourceLocation=resourceLocationList.get(0);
        if(resourceLocation != null) resourceLocationSet.add(resourceLocation);
        return new LegacyBakedModel(quadList,legacyModelDefinition.getTransform(),legacyModelDefinition.getOverride(),legacyModelDefinition.isGenerated(),legacyModelDefinition.isHandheld(),legacyModelDefinition.getOcclusion() == null || legacyModelDefinition.getOcclusion(),legacyModelDefinition.getGUI(),resourceLocationList,resourceLocationSet,resourceLocation);
    }

    private void bakeElement(LegacyModelDefinition legacyModelDefinition,LegacyModelDefinition.Element element,List<LegacyBakedModel.Quad> quadList,Set<ResourceLocation> resourceLocationSet) {
        float[] from=element.getFrom();
        float[] to=element.getTo();
        for(Map.Entry<LegacyDirection,LegacyModelDefinition.Face> entry : element.getFace().entrySet()) {
            LegacyDirection legacyDirection=entry.getKey();
            LegacyModelDefinition.Face face=entry.getValue();
            ResourceLocation resourceLocation=resolve(legacyModelDefinition,face.getString());
            if(resourceLocation == null) continue;
            resourceLocationSet.add(resourceLocation);
            float[] uv=face.getUV();
            if(uv == null) uv=defaultUV(legacyDirection,from,to);
            LegacyBakedModel.Vertex[] vertices=buildVertices(legacyDirection,from,to,uv,face.getRotate());
            if(element.getRotation() != null) {
                for(int index=0;index < vertices.length;index++) {
                    vertices[index]=rotate(vertices[index],element.getRotation());
                }
            }
            quadList.add(new LegacyBakedModel.Quad(vertices,resourceLocation,legacyDirection,face.getLegacyDirection(),face.getTint(),element.isShade()));
        }
    }

    private ResourceLocation resolve(LegacyModelDefinition legacyModelDefinition,String string) {
        if(string == null || string.isEmpty()) return null;
        String resolve=string;
        Set<String> stringSet=new HashSet<String>();
        while (resolve.startsWith("#")) {
            String strings=resolve.substring(1);
            if(!stringSet.add(strings)) throw new IllegalArgumentException("Texture reference '#" + strings + "' in " + ResourceLocations.getString(legacyModelDefinition.getId()));
            resolve=legacyModelDefinition.getTexture().get(strings);
            if(resolve == null) return null;
        }
        return ResourceLocations.parse(resolve);
    }

    private LegacyBakedModel.Vertex[] buildVertices(LegacyDirection legacyDirection,float[] from,float[] to,float[] uv,int rotate) {
        float minX=from[0] / 16.0f;
        float maxX=to[0] / 16.0f;
        float minY=from[1] / 16.0f;
        float maxY=to[1] / 16.0f;
        float minZ=from[2] / 16.0f;
        float maxZ=to[2] / 16.0f;
        float[][] floats;
        switch (legacyDirection) {
            case DOWN: floats=new float[][] {{minX,minY,maxZ},{minX,minY,minZ},{maxX,minY,minZ},{maxX,minY,maxZ}};
            break;
            case UP: floats=new float[][] {{minX,maxY,minZ},{minX,maxY,maxZ},{maxX,maxY,maxZ},{maxX,maxY,minZ}};
                break;
            case NORTH: floats=new float[][] {{maxX,maxY,minZ},{maxX,minY,minZ},{minX,minY,minZ},{minX,maxY,minZ}};
                break;
            case SOUTH: floats=new float[][] {{minX,maxY,maxZ},{minX,minY,maxZ},{maxX,minY,maxZ},{maxX,maxY,maxZ}};
                break;
            case WEST: floats=new float[][] {{minX,maxY,minZ},{minX,minY,minZ},{minX,minY,maxZ},{minX,maxY,maxZ}};
                break;
            case EAST: floats=new float[][] {{maxX,maxY,maxZ},{maxX,minY,maxZ},{maxX,minY,minZ},{maxX,maxY,minZ}};
                break;
            default: throw new IllegalArgumentException();
        }
        float[][] angle=new float[][] {{uv[0],uv[1]},{uv[0],uv[3]},{uv[2],uv[3]},{uv[2],uv[1]}};
        int value=((rotate / 90) % 4 + 4) % 4;
        LegacyBakedModel.Vertex[] vertices=new LegacyBakedModel.Vertex[4];
        for(int index=0;index < 4;index++) {
            int uvIndex=(index - value + 4) % 4;
            vertices[index]=new LegacyBakedModel.Vertex(floats[index][0],floats[index][1],floats[index][2],angle[uvIndex][0],angle[uvIndex][1]);
        }
        return vertices;
    }

    private float[] defaultUV(LegacyDirection legacyDirection,float[] from,float[] to) {
        switch (legacyDirection) {
            case DOWN: return new float[] {from[0],16.0f - to[2],to[0],16.0f - from[2]};
            case UP: return new float[] {from[0],from[2],to[0],to[2]};
            case NORTH: return new float[] {16.0f - to[0],16.0f - to[1],16.0f - from[0],16.0f - from[1]};
            case SOUTH: return new float[] {from[0],16.0f - to[1],to[0],16.0f - from[1]};
            case WEST: return new float[] {from[2],16.0f - to[1],to[2],16.0f - from[1]};
            case EAST: return new float[] {16.0f - to[2],16.0f - to[1],16.0f - from[2],16.0f - from[1]};
            default: return new float[] {0.0f,0.0f,16.0f,16.0f};
        }
    }

    private LegacyBakedModel.Vertex rotate(LegacyBakedModel.Vertex vertex,LegacyModelDefinition.Rotation rotation) {
        float[] floats=rotation.getRotate();
        float ox=floats[0] / 16.0f;
        float oy=floats[1] / 16.0f;
        float oz=floats[2] / 16.0f;
        float x=vertex.getX() - ox;
        float y=vertex.getY() - oy;
        float z=vertex.getZ() - oz;
        double radians=Math.toRadians(rotation.getAngle());
        float sin = (float) Math.sin(radians);
        float cos=(float) Math.cos(radians);
        float rx=x;
        float ry=y;
        float rz=z;
        switch (rotation.getAxis()) {
            case x: ry=y * cos - z * sin;
            rz=y * sin + z * cos;
            break;
            case y: rx=x * cos + z * sin;
                rz=-x * sin + z * cos;
                break;
            case z: rx=x * cos - y * sin;
                ry=x * sin + y * cos;
                break;
        }
        if(rotation.isScale() && rotation.getAngle() != 0.0f) {
            float scale=1.0f / Math.abs(cos);
            switch (rotation.getAxis()) {
                case x: ry *= scale;
                rz *= scale;
                break;
                case y: rx *= scale;
                    rz *= scale;
                    break;
                case z: rx *= scale;
                    ry *= scale;
                    break;
            }
        }
        return new LegacyBakedModel.Vertex(rx + ox,ry + oy,rz + oz, vertex.getU(),vertex.getV());
    }
}
