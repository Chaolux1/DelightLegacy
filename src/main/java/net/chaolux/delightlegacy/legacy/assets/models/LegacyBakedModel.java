/*
Copyright (c) Forge Development LLC and contributors
SPDX-License-Identifier: LGPL-2.1-only
Modified by Chaolux, 2026
 */
package net.chaolux.delightlegacy.legacy.assets.models;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Vertex;

import java.util.*;

public class LegacyBakedModel {
    private final List<Quad> quadList;
    private final Map<String,LegacyModelDefinition.Transform> transformMap;
    private final List<LegacyModelDefinition.Override> overrideList;
    private final boolean generated;
    private final boolean handheld;
    private final boolean occlusion;
    private final String GUI;
    private final List<ResourceLocation> resourceLocationList;
    private final Set<ResourceLocation> resourceLocationSet;
    private final ResourceLocation resourceLocation;
    public LegacyBakedModel(List<Quad> quadList,Map<String,LegacyModelDefinition.Transform> transformMap,List<LegacyModelDefinition.Override> overrideList,boolean generated,boolean handheld,boolean occlusion,String GUI,List<ResourceLocation> resourceLocationList,Set<ResourceLocation> resourceLocationSet,ResourceLocation resourceLocation) {
        this.quadList= Collections.unmodifiableList(new ArrayList<Quad>(quadList));
        this.transformMap=Collections.unmodifiableMap(new LinkedHashMap<String,LegacyModelDefinition.Transform>(transformMap));
        this.overrideList=Collections.unmodifiableList(new ArrayList<LegacyModelDefinition.Override>(overrideList));
        this.generated=generated;
        this.handheld=handheld;
        this.occlusion=occlusion;
        this.GUI=GUI;
        this.resourceLocationList=Collections.unmodifiableList(new ArrayList<ResourceLocation>(resourceLocationList));
        this.resourceLocationSet=Collections.unmodifiableSet(new LinkedHashSet<ResourceLocation>(resourceLocationSet));
        this.resourceLocation=resourceLocation;
    }

    public List<Quad> getQuad() {
        return quadList;
    }

    public LegacyModelDefinition.Transform getTransform(String string) {
        LegacyModelDefinition.Transform transform=transformMap.get(string);
        return transform == null ? LegacyModelDefinition.Transform.TRANSFORM : transform;
    }

    public List<LegacyModelDefinition.Override> getOverride() {
        return overrideList;
    }

    public boolean isGenerated() {
        return generated;
    }

    public boolean isHandheld() {
        return handheld;
    }

    public boolean isOcclusion() {
        return occlusion;
    }

    public String getGUI() {
        return GUI;
    }

    public List<ResourceLocation> getLayer() {
        return resourceLocationList;
    }

    public Set<ResourceLocation> getLocation() {
        return resourceLocationSet;
    }

    public static final class Quad {
        private final Vertex[] vertices;
        private final ResourceLocation resourceLocation;
        private final LegacyDirection legacyDirection;
        private final LegacyDirection direction;
        private final int tint;
        private final boolean shade;
        public Quad(Vertex[] vertices,ResourceLocation resourceLocation,LegacyDirection legacyDirection,LegacyDirection direction,int tint,boolean shade) {
            this.vertices=new Vertex[] {vertices[0],vertices[1],vertices[2],vertices[3]};
            this.resourceLocation=resourceLocation;
            this.legacyDirection=legacyDirection;
            this.direction=direction;
            this.tint=tint;
            this.shade=shade;
        }

        public Vertex[] getVertices() {
            return new Vertex[] {vertices[0],vertices[1],vertices[2],vertices[3]};
        }

        public ResourceLocation getLocation() {
            return resourceLocation;
        }

        public LegacyDirection getLegacyDirection() {
            return legacyDirection;
        }

        public LegacyDirection getDirection() {
            return direction;
        }

        public int getTint() {
            return tint;
        }

        public boolean isShade() {
            return shade;
        }
    }

    public static final class Vertex {
        private final float x;
        private final float y;
        private final float z;
        private final float u;
        private final float v;
        public Vertex(float x,float y,float z,float u,float v) {
            this.x=x;
            this.y=y;
            this.z=z;
            this.u=u;
            this.v=v;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

        public float getU() {
            return u;
        }

        public float getV() {
            return v;
        }
    }

    public ResourceLocation getParticle() {
        return resourceLocation;
    }
}
