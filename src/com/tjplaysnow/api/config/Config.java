package com.tjplaysnow.api.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class Config {
	
	private File file;
	private FileConfiguration fileConfig;
	
	/** Creates a new config at the path, with the fileName, with a configCreate method caller, and uses the Plugin */
	public Config(String path, String fileName, Runnable callback, Plugin plugin) {
		if (!fileName.contains(".yml")) {
			fileName = fileName + ".yml";
		}
		file = new File(path, fileName);
		fileConfig = YamlConfiguration.loadConfiguration(file);
		
		if (!file.exists()) {
			fileConfig.options().copyDefaults(true);
			callback.run();
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
	
	/** Set a location in the config */
	public void setLocation(String path, Location location) {
		fileConfig.set(path + ".World", location.getWorld().getName());
		fileConfig.set(path + ".X", location.getX());
		fileConfig.set(path + ".Y", location.getY());
		fileConfig.set(path + ".Z", location.getZ());
		fileConfig.set(path + ".Pitch", location.getPitch());
		fileConfig.set(path + ".Yaw", location.getYaw());
		saveConfig();
	}
	
	/** Get a location in the config */
	public Location getLocation(String path) {
		if (fileConfig.getString(path + ".World") == null) {
			return null;
		}
		Location location = new Location(Bukkit.getWorld(fileConfig.getString(path + ".World")), fileConfig.getDouble(path + ".X"), fileConfig.getDouble(path + ".Y"), fileConfig.getDouble(path + ".Z"), (float) fileConfig.getDouble(path + ".Yaw"), (float) fileConfig.getDouble(path + ".Pitch"));
		return location;
	}
	
	@SuppressWarnings("deprecation")
	public void setItemStack(String path, ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return;
		}
		fileConfig.set(path + ".Byte", item.getData().getData());
		fileConfig.set(path + ".Material", item.getType().toString());
		fileConfig.set(path + ".Ammount", item.getAmount());
		fileConfig.set(path + ".Damage", item.getDurability());
		if (item.getItemMeta().getDisplayName() == null) {
			fileConfig.set(path + ".Name", item.getType().toString());
		} else {
			fileConfig.set(path + ".Name", item.getItemMeta().getDisplayName().replace("§", "&"));
		}
		List<String> lore = new ArrayList<String>();
		if (item.getItemMeta().getLore() != null) {
			for (String l : item.getItemMeta().getLore()) {
				lore.add(l.replace("§", "&"));
			}
		}
		fileConfig.set(path + ".Lore", lore);
		for (Enchantment e : item.getItemMeta().getEnchants().keySet()) {
			fileConfig.set(path + ".Enchants." + e.getName().toString() + ".Level", item.getEnchantmentLevel(e));
			saveConfig();
		}
		if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
			for (Enchantment e : meta.getStoredEnchants().keySet()) {
				fileConfig.set(path + ".Enchants." + e.getName().toString() + ".Level", meta.getStoredEnchantLevel(e));
				saveConfig();
			}
		}
		if (item.getItemMeta() instanceof SpawnEggMeta) {
			SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
			fileConfig.set(path + ".SpawnEggMeta.Type", meta.getSpawnedType().toString());
			saveConfig();
		}
		if (item.getItemMeta() instanceof PotionMeta) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			fileConfig.set(path + ".PotionMeta.Type", meta.getBasePotionData().getType().toString());
			fileConfig.set(path + ".PotionMeta.isExtended", meta.getBasePotionData().isExtended());
			fileConfig.set(path + ".PotionMeta.isUpgraded", meta.getBasePotionData().isUpgraded());
			saveConfig();
		}
		saveConfig();
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItemStack(String path) {
		ItemStack item = new ItemStack(Material.valueOf(fileConfig.getString(path + ".Material", "STONE")), fileConfig.getInt(path + ".Ammount", 1), (short) fileConfig.getInt(path + ".Damage", 0), (byte) fileConfig.getInt(path + ".Byte", 0));
		if (fileConfig.getString(path + ".Material") == null) {
			return null;
		}
		List<String> lore = new ArrayList<String>();
		for (String l : fileConfig.getStringList(path + ".Lore")) {
			lore.add(l.replace("&", "§"));
		}
		if (item.getType().equals(Material.POTION) || item.getType().equals(Material.LINGERING_POTION) || item.getType().equals(Material.SPLASH_POTION) || item.getType().equals(Material.TIPPED_ARROW)) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			meta.setBasePotionData(new PotionData(PotionType.valueOf(fileConfig.getString(path + ".PotionMeta.Type")), fileConfig.getBoolean(path + ".PotionMeta.IsExtended"), fileConfig.getBoolean(path + ".PotionMeta.IsUpgraded")));
			item.setItemMeta(meta);
		}
		if (item.getItemMeta() instanceof SpawnEggMeta) {
			SpawnEggMeta meta = (SpawnEggMeta) item.getItemMeta();
			meta.setSpawnedType(EntityType.valueOf(fileConfig.getString(path + ".SpawnEggMeta.Type")));
			item.setItemMeta(meta);
		}
		if (fileConfig.getConfigurationSection(path + ".Enchants") != null) {
			for (String l : fileConfig.getConfigurationSection(path + ".Enchants").getKeys(false)) {
				if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
					EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
					meta.addStoredEnchant(Enchantment.getByName(l), fileConfig.getInt(path + ".Enchants." + l + ".Level"), false);
					item.setItemMeta(meta);
				} else if (item.getItemMeta() instanceof ItemMeta) {
					ItemMeta meta = item.getItemMeta();
					meta.addEnchant(Enchantment.getByName(l), fileConfig.getInt(path + ".Enchants." + l + ".Level"), true);
					item.setItemMeta(meta);
				}
			}
		}
		if (fileConfig.getString(path + ".Name").replace("&", "§").equals(item.getType().toString())) {
			ItemManager.setLore(item, lore);
		} else {
			ItemManager.setNameAndLore(item, fileConfig.getString(path + ".Name").replace("&", "§"), lore);
		}
		return item;
	}
}

class ItemManager {
	
	public static ItemStack getSkull(SkullType type) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) type.ordinal());
		return item;
	}
	
	public static ItemStack getSkull(String playerName) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta im = (SkullMeta) item.getItemMeta();
		im.setOwner(playerName);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setNameAndLoreandSkull(String name, String playerName, String... lore) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta im = (SkullMeta) item.getItemMeta();
		im.setDisplayName(name);
		List<String> il = new ArrayList<String>();
		for (String l : lore) {
			il.add(l);
		}
		im.setLore(il);
		im.setOwner(playerName);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setNameAndLore(ItemStack item, String name, String... lore) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		List<String> il = new ArrayList<String>();
		for (String l : lore) {
			il.add(l);
		}
		im.setLore(il);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setNameAndLore(ItemStack item, String name, List<String> il) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(il);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setNameAndLoreandEnchants(ItemStack item, String name, List<String> il, List<String> enchants) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(il);
		for (String enchant : enchants) {
			String[] part = enchant.split(":");
			im.addEnchant(Enchantment.getByName(part[0]), Integer.getInteger(part[1]), true);
		}
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setLore(ItemStack item, String... lore) {
		ItemMeta im = item.getItemMeta();
		List<String> il = new ArrayList<String>();
		for (String l : lore) {
			il.add(l);
		}
		im.setLore(il);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta im = item.getItemMeta();
		List<String> il = new ArrayList<String>();
		for (String l : lore) {
			il.add(l);
		}
		im.setLore(il);
		item.setItemMeta(im);
		return item;
	}
	
//	public static ItemStack setUnbreakable(ItemStack item) {
//		ItemMeta meta = item.getItemMeta();
//		meta.setUnbreakable(true);
//		item.setItemMeta(meta);
//		return item;
//	}
	
	public static ItemStack stripLore(ItemStack item) {
		if (item.getType().equals(Material.SKULL) || item.getType().equals(Material.SKULL_ITEM)) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		} else {
			ItemMeta meta = item.getItemMeta();
			List<String> lore = new ArrayList<String>();
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}
	}
	
	public static boolean isEqual(ItemStack item1, ItemStack item2) {
		if (stripLore(item1).equals(stripLore(item2))) {
			return true;
		} else {
			return false;
		}
	}

	public static ItemStack setUnbreakable(ItemStack itemStack) {
		ItemMeta meta = itemStack.getItemMeta();
		meta.setUnbreakable(true);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
}