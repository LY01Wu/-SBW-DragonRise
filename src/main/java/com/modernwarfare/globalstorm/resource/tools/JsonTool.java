package com.modernwarfare.globalstorm.resource.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.modernwarfare.globalstorm.GlobalStorm;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTool {

    /** 如果是从资源包或数据包中读取json文件，可以用ResourceManager。
     * 这里是从游戏根目录下的某个文件夹中读取json文件。
     */

    //Path一般为FMLPaths.GAMEDIR.get().resolve("modern_warfare")
    public static JsonElement getJsonByPath(@NotNull Path path, String fileName, @Nullable String fileType) {

        Logger logger = GlobalStorm.LOGGER;
        File file;

        if( fileType == null ) {
            file = new File(path + File.separator + fileName);
        }else {
            file = new File(path + File.separator + fileType, fileName);
        }

        if (!Files.exists(path)) {
            // Create an empty file
            try {
                Files.createDirectories(path);
                logger.log(Level.INFO, "Created directory: " + path);
            } catch (IOException e) {
                logger.log(Level.ERROR, "Error creating directory: " + path + "!");
                return null;
            }
            return null;
        }

        logger.log(Level.INFO, "Reading from " + fileName);
        InputStream inputstream = null;
        try {
            inputstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.log(Level.ERROR, "Error reading " + fileName + "!");
            return null;
        }

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error reading " + fileName + "!");
            return null;
        }

        JsonParser parser = new JsonParser();

        return parser.parse(br);
    }

    public static Map<String,JsonElement> getAllJsonByPath(@NotNull Path path, @Nullable String fileType){

        Logger logger = GlobalStorm.LOGGER;
        Map<String,JsonElement> output = new HashMap<>();

        if (!Files.exists(path)) {
            // Create an empty file
            try {
                Files.createDirectories(path);
                logger.log(Level.INFO, "Created directory: " + path);
            } catch (IOException e) {
                logger.log(Level.ERROR, "Error creating directory: " + path + "!");
                return null;
            }
            return null;
        }

        try {
            var stream = Files.newDirectoryStream(path,"*.json");
            for(Path file:stream){
                String filename = file.getFileName().toString();
                output.put(filename,getJsonByPath(path,filename,fileType));
                logger.log(Level.INFO, "Found json file: " + filename);
            }
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
