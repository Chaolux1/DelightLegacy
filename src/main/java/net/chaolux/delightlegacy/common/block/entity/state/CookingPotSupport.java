package net.chaolux.delightlegacy.common.block.entity.state;

public enum CookingPotSupport {
    NONE(0,"none"),TRAY(4,"tray"),HANDLE(8,"handle");
    private final int value;
    private final String supportName;
    CookingPotSupport(int value,String supportName) {
        this.value=value;
        this.supportName=supportName;
    }

    public int getValue() {
        return value;
    }

    public String getSupportName() {
        return supportName;
    }

    public static CookingPotSupport from(int data) {
        int metadata=data & 12;
        if(metadata == TRAY.value) return TRAY;
        if(metadata == HANDLE.value) return HANDLE;
        return NONE;
    }
}
