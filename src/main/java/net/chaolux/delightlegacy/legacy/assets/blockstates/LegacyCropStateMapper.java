package net.chaolux.delightlegacy.legacy.assets.blockstates;

public class LegacyCropStateMapper {
    public static LegacyDataStateMapper dataStateMapper(int value) {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        for (int index=0;index < 16;index++) {
            builder.put(index,"age",String.valueOf(clamp(index,0,value)));
        }
        return builder.build();
    }

    public static LegacyDataStateMapper tomato() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        for (int index=0;index < 16;index++) {
            builder.put(index,"age",String.valueOf(index & 3));
            builder.put(index,"ropelogged",String.valueOf((index & 4) != 0));
        }
        return builder.build();
    }

    public static LegacyDataStateMapper rice() {
        LegacyDataStateMapper.Builder builder=LegacyDataStateMapper.builder();
        for (int index=0;index < 16;index++) {
            builder.put(index,"age",String.valueOf(index & 3));
            builder.put(index,"supporting",String.valueOf((index & 4) != 0));
        }
        return builder.build();
    }

    private static int clamp(int value,int min,int max) {
        return value < min ? min : Math.min(value,max);
    }
}
