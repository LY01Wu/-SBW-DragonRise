package com.modernwarfare.dragonrise.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.modernwarfare.dragonrise.Mod;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraftforge.fml.loading.FMLPaths;

import static com.modernwarfare.dragonrise.Mod.LOGGER;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;


public class ConfigHandler<T extends IConfig> {

	private final Gson gson;
	private final Class<T> configType;
	private final Path configPath;

	@Getter
	private T config;
	private int configHash;

	public static <T extends IConfig> ConfigHandler<T> of(Class<T> configType, String configExtension) {
		return new ConfigHandler<>(configType, FMLPaths.CONFIGDIR.get().resolve((Mod.MODID + configExtension)));
	}

	@SneakyThrows
	public ConfigHandler(Class<T> configType, Path configPath) {
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.configType = configType;
		this.configPath = configPath;

		this.configHash = 0;
		try{
			this.config = configType.getDeclaredConstructor().newInstance();
		}catch (NoSuchMethodException e){
			System.err.println("配置类缺少无参构造函数: " + e.getMessage());
			throw new RuntimeException("配置初始化失败", e);
		}catch (InstantiationException|IllegalAccessException| InvocationTargetException e){
			System.err.println("创建配置实例失败: " + e.getMessage());
			throw new RuntimeException("配置初始化失败", e);
		}
	}

	public void save() {
		if (configHash == config.hashCode()) {
			return;
		}

		config.onUpdate();

		if (!Files.exists(configPath)) {
			try {
				Files.createDirectories(configPath.getParent());
				Files.createFile(configPath);
			} catch (IOException e) {
				LOGGER.error("Creating Config failed: " + e);
				return;
			}
		}

		try {
			var writer = Files.newBufferedWriter(configPath);
			gson.toJson(config, writer);
			writer.close();
		} catch (Exception e) {
			LOGGER.error("Saving Config failed: " + e);
			return;
		}

		configHash = config.hashCode();
		LOGGER.info("Saved " + config);
	}

	@SneakyThrows
	public void load() {
		if (!Files.exists(configPath)) {
			save();
			return;
		}

		try {
			var reader = Files.newBufferedReader(configPath);
			config = gson.fromJson(reader, configType);
			reader.close();
		} catch (Exception e) {
			config = null;
		}

		if (config == null) {
			try{
			config = configType.getDeclaredConstructor().newInstance();
		}catch (NoSuchMethodException e){
			System.err.println("配置类缺少无参构造函数: " + e.getMessage());
			// 可以选择抛出运行时异常或使用默认配置
			throw new RuntimeException("配置初始化失败", e);
		}catch (InstantiationException|IllegalAccessException| InvocationTargetException e){
			System.err.println("创建配置实例失败: " + e.getMessage());
			throw new RuntimeException("配置初始化失败", e);
		}
			LOGGER.error("Config is broken -> reset to defaults");

			save();
			return;
		}

		config.validate();
		configHash = config.hashCode();
		LOGGER.info("Loaded " + config);
	}
}
