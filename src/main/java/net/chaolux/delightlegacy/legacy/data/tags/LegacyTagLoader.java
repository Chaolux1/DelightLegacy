package net.chaolux.delightlegacy.legacy.data.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.chaolux.delightlegacy.DelightLegacy;
import net.chaolux.delightlegacy.legacy.assets.ClassPathResourceProvider;
import net.chaolux.delightlegacy.legacy.assets.LegacyPackType;
import net.chaolux.delightlegacy.legacy.assets.ResourceLocations;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LegacyTagLoader {
    static List<EntryWithSource> list() {
        List<EntryWithSource> entryWithSourceList=new ArrayList<EntryWithSource>();
            try {
                for (File file : new ClassPathResourceProvider(DelightLegacy.class).getPack(LegacyPackType.DATA)) {
                    try {
                        if(file.isDirectory()) {
                            scan(file,entryWithSourceList);
                        } else {
                            scanJar(file,entryWithSourceList);
                        }
                    } catch (Exception exception) {
                        DelightLegacy.LOGGER.error("Cannot read tag {}",file,exception);
                    }
                }
            } catch (IOException ioException) {
                DelightLegacy.LOGGER.error("Cannot enumerate tag",ioException);
            }
        return entryWithSourceList;
    }

    private static void scan(File file,List<EntryWithSource> entryWithSourceList) throws IOException {
        File data=new File(file,"data");
        if(!data.isDirectory()) return;
        List<File> fileList=new ArrayList<File>();
        scanData(data,fileList);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
        for (File files : fileList) {
            String string=file.toURI().relativize(files.toURI()).getPath();
            try(InputStream inputStream=new FileInputStream(files)) {
                read(string,inputStream,entryWithSourceList);
            }
        }
    }

    private static void scanData(File file,List<File> fileList) {
        File[] files=file.listFiles();
        if(files == null) return;
        for(File data : files) {
            if(data.isDirectory()) {
                scanData(data,fileList);
            } else if (data.isFile() && data.getName().endsWith(".json") && data.getPath().contains("tags")) {
                fileList.add(data);
            }
        }
    }

    private static void scanJar(File file,List<EntryWithSource> entryWithSourceList) throws IOException {
        try(JarFile jarFile=new JarFile(file)) {
            List<String> stringList = new ArrayList<String>();
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = jarEntryEnumeration.nextElement();
                if (!jarEntry.isDirectory() && jarEntry.getName().startsWith("data/") && jarEntry.getName().contains("/tags/") && jarEntry.getName().endsWith(".json")) {
                    stringList.add(jarEntry.getName());
                }
            }
            Collections.sort(stringList);
            for (String string : stringList) {
                try (InputStream inputStream = jarFile.getInputStream(jarFile.getJarEntry(string))) {
                    read(string, inputStream, entryWithSourceList);
                }
            }
        }
    }

    private static void read(String string,InputStream inputStream,List<EntryWithSource> entryWithSourceList) {
        try {
            String[] strings=string.replace('\\','/').split("/");
            if(strings.length < 5 || !"data".equals(strings[0]) || !"tags".equals(strings[2])) return;
            Tag tag=Tag.from(strings[3]);
            if(tag == null || !string.endsWith(".json")) return;
            StringBuilder stringBuilder=new StringBuilder();
            for (int index=4;index < strings.length;index++) {
                if(index > 4)stringBuilder.append('/');
                stringBuilder.append(strings[index]);
            }
            stringBuilder.setLength(stringBuilder.length() - 5);
            ResourceLocation resourceLocation= ResourceLocations.parse(strings[1] + ":" + stringBuilder);
            JsonElement jsonElement=new JsonParser().parse(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
            if(jsonElement == null || !jsonElement.isJsonObject()) throw new IllegalArgumentException("Expect JSON");
            entryWithSourceList.add(new EntryWithSource(tag,resourceLocation,jsonElement.getAsJsonObject(),string));
        } catch (Exception exception) {
            DelightLegacy.LOGGER.warn("Skip invalid tag {}: {}",string,exception.getMessage());
        }
    }

    static final class EntryWithSource {
        final Tag tag;
        final ResourceLocation resourceLocation;
        final JsonObject jsonObject;
        final String string;
        EntryWithSource(Tag tag,ResourceLocation resourceLocation,JsonObject jsonObject,String string) {
            this.tag=tag;
            this.resourceLocation=resourceLocation;
            this.jsonObject=jsonObject;
            this.string=string;
        }
    }
}
