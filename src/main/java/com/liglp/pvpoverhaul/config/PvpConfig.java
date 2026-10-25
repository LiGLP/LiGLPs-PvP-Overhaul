package com.liglp.pvpoverhaul.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liglp.pvpoverhaul.PvpOverhaul;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * All toggles of the mod. Saved as JSON in config/liglps-pvp-overhaul.json.
 */
public final class PvpConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("liglps-pvp-overhaul.json");

	private static PvpConfig instance = new PvpConfig();

	// General
	public boolean clearWater = false;
	public boolean clearLava = false;
	public boolean clearPowderSnow = false;
	public boolean fullbright = false;
	public boolean noFireOverlay = false;

	// Crystal category
	public boolean smallTotemAndFood = false;
	public boolean noEnderPearlSounds = false;
	public boolean noCrystalExplosionVfx = false;
	public boolean lowerShield = false;

	public static PvpConfig get() {
		return instance;
	}

	public boolean isCrystalCategoryFullyEnabled() {
		return smallTotemAndFood && noEnderPearlSounds && noCrystalExplosionVfx && lowerShield;
	}

	public void setCrystalCategory(boolean enabled) {
		smallTotemAndFood = enabled;
		noEnderPearlSounds = enabled;
		noCrystalExplosionVfx = enabled;
		lowerShield = enabled;
	}

	public static void load() {
		if (!Files.exists(FILE)) {
			save();
			return;
		}
		try (Reader reader = Files.newBufferedReader(FILE)) {
			PvpConfig loaded = GSON.fromJson(reader, PvpConfig.class);
			if (loaded != null) {
				instance = loaded;
			}
		} catch (Exception e) {
			PvpOverhaul.LOGGER.error("Failed to read config, using defaults", e);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			try (Writer writer = Files.newBufferedWriter(FILE)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException e) {
			PvpOverhaul.LOGGER.error("Failed to save config", e);
		}
	}
}
