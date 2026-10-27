package com.liglp.pvpoverhaul.swordpvp;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.InteractionResult;

/**
 * Sword PvP helper: note block sounds when you drop to low health and when you can W-tap and hit again.
 */
public final class SwordPvp {
	/** Health from the last tick, -1 when there was no player. */
	private static float lastHealth = -1.0F;
	/** Set after you hit an entity, cleared once the attack is fully charged again. */
	private static boolean waitingForAttackReady = false;

	private SwordPvp() {
	}

	public static SwordPvpConfig config() {
		return PvpConfig.get().swordPvp;
	}

	public static void register() {
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			// Also fires on the integrated server, only react to our own client player
			if (player instanceof LocalPlayer && config().wTapAlert) {
				waitingForAttackReady = true;
			}
			return InteractionResult.PASS;
		});

		ClientTickEvents.END_CLIENT_TICK.register(SwordPvp::tick);
	}

	private static void tick(Minecraft client) {
		LocalPlayer player = client.player;
		SwordPvpConfig config = config();
		if (player == null) {
			lastHealth = -1.0F;
			waitingForAttackReady = false;
			return;
		}

		// Low health: play once when the health drops to or below the limit
		float health = player.getHealth();
		float limit = (float) (config.lowHealthHearts * 2.0);
		if (config.lowHealthAlert && lastHealth >= 0.0F && health > 0.0F && health <= limit && lastHealth > limit) {
			play(config.lowHealthSound, config.lowHealthNote);
		}
		lastHealth = health;

		// W-tap: play once the attack cooldown is full again after a hit
		if (waitingForAttackReady && player.getAttackStrengthScale(0.0F) >= 1.0F) {
			waitingForAttackReady = false;
			if (config.wTapAlert) {
				play(config.wTapSound, config.wTapNote);
			}
		}
	}

	public static void play(NoteSound sound, int note) {
		// Same pitch as a note block: note 12 is the normal pitch, 0 and 24 are one octave lower/higher
		float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.sound, pitch, (float) config().volume));
	}
}
