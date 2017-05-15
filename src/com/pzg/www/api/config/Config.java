package com.pzg.www.api.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginAwareness;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class Config {
	
	private File file;
	private FileConfiguration fileConfig;
	
	/** Creates a new config at the path, with the fileName, with a configCreate method caller, and uses the Plugin */
	public Config(String path, String fileName, ConfigCreate configCreate, Plugin plugin) {
		if (!fileName.contains(".yml")) {
			fileName = fileName + ".yml";
		}
		file = new File(path, fileName);
		fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			fileConfig.options().copyDefaults(true);
			configCreate.configCreate();
			try {
				fileConfig.save(file);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/** Creates a new config at the path, with the fileName, and uses the Plugin */
	public Config(String path, String fileName, Plugin plugin) {
		if (!fileName.contains(".yml")) {
			fileName = fileName + ".yml";
		}
		file = new File(path, fileName);
		fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			fileConfig.options().copyDefaults(true);
			try {
				fileConfig.save(file);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/** Creates a new config at the path, with the fileName, the default file name found in the jar, with a configCreate method caller, and uses the Plugin */
	@SuppressWarnings("deprecation")
	public Config(String path, String fileName, String deffault, ConfigCreate configCreate, Plugin plugin) {
		if (!fileName.contains(".yml")) {
			fileName = fileName + ".yml";
		}
		file = new File(path, fileName);
		fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			InputStream deffaultConfigStream = plugin.getResource(deffault);
			if (deffaultConfigStream == null) return;
			YamlConfiguration defConfig;
			if ((plugin.getDescription().getAwareness().contains(
					PluginAwareness.Flags.UTF8))) {
				defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(deffaultConfigStream, Charsets.UTF_8));
			} else {
				defConfig = new YamlConfiguration();
				byte[] contents;
				try {
					contents = ByteStreams.toByteArray(deffaultConfigStream);
				} catch (IOException e) {
					plugin.getLogger().log(Level.SEVERE, "Unexpected failure reading " + deffault, e);
					return;
				}
				String text = new String(contents, Charset.defaultCharset());
				if (!(text.equals(new String(contents, Charsets.UTF_8)))) {
					plugin.getLogger().warning("Default system encoding may have misread " + deffault + " from plugin jar");
				}
				try {
					defConfig.loadFromString(text);
				} catch (InvalidConfigurationException e) {
					plugin.getLogger().log(Level.SEVERE, "Cannot load configuration from jar", e);
				}
			}
			fileConfig.setDefaults(defConfig);
			try {
				fileConfig.save(file);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
			configCreate.configCreate();
		}
	}
	
	/** Creates a new config at the path, with the fileName, the default file name found in the jar, and uses the Plugin */
	@SuppressWarnings("deprecation")
	public Config(String path, String fileName, String deffault, Plugin plugin) {
		if (!fileName.contains(".yml")) {
			fileName = fileName + ".yml";
		}
		file = new File(path, fileName);
		fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			InputStream deffaultConfigStream = plugin.getResource(deffault);
			if (deffaultConfigStream == null) return;
			YamlConfiguration defConfig;
			if ((plugin.getDescription().getAwareness().contains(
					PluginAwareness.Flags.UTF8))) {
				defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(deffaultConfigStream, Charsets.UTF_8));
			} else {
				defConfig = new YamlConfiguration();
				byte[] contents;
				try {
					contents = ByteStreams.toByteArray(deffaultConfigStream);
				} catch (IOException e) {
					plugin.getLogger().log(Level.SEVERE, "Unexpected failure reading " + deffault, e);
					return;
				}
				String text = new String(contents, Charset.defaultCharset());
				if (!(text.equals(new String(contents, Charsets.UTF_8)))) {
					plugin.getLogger().warning("Default system encoding may have misread " + deffault + " from plugin jar");
				}
				try {
					defConfig.loadFromString(text);
				} catch (InvalidConfigurationException e) {
					plugin.getLogger().log(Level.SEVERE, "Cannot load configuration from jar", e);
				}
			}
			fileConfig.setDefaults(defConfig);
			try {
				fileConfig.save(file);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	/** Get the Configuration section */
	public FileConfiguration getConfig() {
		return fileConfig;
	}
	
	/** Save the config */
	public void saveConfig() {
		try {
			fileConfig.save(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}