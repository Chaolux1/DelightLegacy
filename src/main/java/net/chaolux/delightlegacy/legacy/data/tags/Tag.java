package net.chaolux.delightlegacy.legacy.data.tags;

public enum Tag {
    ITEMS("items"),BLOCKS("blocks"),ENTITIES("entity_types");
    private final String string;
    Tag(String string) {
        this.string=string;
    }

    public static Tag from(String string) {
        for(Tag tag : values()) {
            if(tag.string.equals(string)) return tag;
        }
        return null;
    }

    public String getString() {
        return string;
    }
}
