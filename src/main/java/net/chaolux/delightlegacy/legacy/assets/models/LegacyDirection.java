package net.chaolux.delightlegacy.legacy.assets.models;

public enum LegacyDirection {
    DOWN(0,0,-1,0,0.5f),UP(1,0,1,0,1.0f),NORTH(2,0,0,-1,0.8f),SOUTH(3,0,0,1,0.8f),WEST(4,-1,0,0,0.6f),EAST(5,1,0,0,0.6f);
    private final int direction;
    private final int offsetX;
    private final int offsetY;
    private final int offsetZ;
    private final float shade;
    LegacyDirection(int direction,int offsetX,int offsetY,int offsetZ,float shade) {
        this.direction=direction;
        this.offsetX=offsetX;
        this.offsetY=offsetY;
        this.offsetZ=offsetZ;
        this.shade=shade;
    }

    public int getDirection() {
        return direction;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    public float getShade() {
        return shade;
    }

    public LegacyDirection rotate(int rotateX,int rotateY) {
        int x=offsetX;
        int y=offsetY;
        int z=offsetZ;
        int normalizeX=normalize(rotateX);
        int normalizeY=normalize(rotateY);
        for(int index=0;index < normalizeX;index++) {
            int currentY=y;
            y=z;
            z=-currentY;
        }
        for (int index=0;index < normalizeY;index++) {
            int currentX=x;
            x=-z;
            z=currentX;
        }
        return from(x,y,z);
    }

    public static LegacyDirection name(String string) {
        if(string == null) return null;
        for(LegacyDirection legacyDirection : values()) {
            if(legacyDirection.name().equalsIgnoreCase(string)) return legacyDirection;
        }
        throw new IllegalArgumentException("Unknown model direction: " + string);
    }

    public static LegacyDirection from(int x,int y,int z) {
        for (LegacyDirection legacyDirection : values()) {
            if(legacyDirection.offsetX == x && legacyDirection.offsetY == y && legacyDirection.offsetZ == z) return legacyDirection;
        }
        return NORTH;
    }

    private static int normalize(int degrees) {
        int rotate=(degrees / 90) % 4;
        if(rotate < 0) rotate += 4;
        return rotate;
    }
}
