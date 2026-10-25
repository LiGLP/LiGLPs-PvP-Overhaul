package com.liglp.pvpoverhaul;

import com.liglp.pvpoverhaul.config.PvpConfig;
import com.liglp.pvpoverhaul.gui.PauseMenuButton;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class PvpOverhaul implements ClientModInitializer {
	public static final String MOD_ID = "liglpspvp";
	public static final Logger LOGGER = LoggerFactory.getLogger("LiGLP's PvP Overhaul");

	@Override
	public void onInitializeClient() {
		PvpConfig.load();
		PauseMenuButton.register();

		// Used by CI: apply every mixin once, then quit
		if (Boolean.getBoolean("liglpspvp.audit")) {
			ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
				MixinEnvironment.getCurrentEnvironment().audit();
				LOGGER.info("Mixin audit finished");
				client.stop();
			});
		}
	}
}
