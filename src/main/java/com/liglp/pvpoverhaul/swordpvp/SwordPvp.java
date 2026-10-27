package com.liglp.pvpoverhaul.swordpvp;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

/**
 * Sword PvP helper: note block sounds when you drop to low health and when you can W-tap and hit again.
 */
public final class SwordPvp {
	/** If no hurt animation shows up on the target (missed, blocked, lag), give up waiting for it after this many ticks. */
	private static final int MAX_HURT_WAIT_TICKS = 20;
	/** Stop tracking a target that is farther away than this (blocks), you cannot hit it anyway. */
	private static final double MAX_TARGET_DISTANCE = 8.0;

	/** Health from the last tick, -1 when there was no player. */
	private static float lastHealth = -1.0F;

	/** The entity you hit last, null when there is nothing to wait for. */
	private static @Nullable LivingEntity wTapTarget;
	/** Whether the target's hurt animation (damage immunity) was seen since the hit. */
	private static boolean sawTargetHurt;
	private static int ticksSinceHit;

	private SwordPvp() {
	}

	public static SwordPvpConfig config() {
		return PvpConfig.get().swordPvp;
	}

	public static void register() {
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
			// Also fires on the integrated server, only react to our own client player
			if (player instanceof LocalPlayer && config().wTapAlert && entity instanceof LivingEntity living) {
				wTapTarget = living;
				sawTargetHurt = false;
				ticksSinceHit = 0;
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
			wTapTarget = null;
			return;
		}

		// Low health: play once when the health drops to or below the limit
		float health = player.getHealth();
		float limit = (float) (config.lowHealthHearts * 2.0);
		if (config.lowHealthAlert && lastHealth >= 0.0F && health > 0.0F && health <= limit && lastHealth > limit) {
			play(config.lowHealthSound, config.lowHealthNote);
		}
		lastHealth = health;

		tickWTap(player, config);
	}

	/**
	 * A W-tap is only worth it when both are true again after your hit:
	 * your attack is fully charged (full damage and sprint knockback) and the target
	 * lost its damage immunity (its red hurt animation is over), so the next hit really lands.
	 */
	private static void tickWTap(LocalPlayer player, SwordPvpConfig config) {
		LivingEntity target = wTapTarget;
		if (target == null) {
			return;
		}
		if (!config.wTapAlert || target.isRemoved() || !target.isAlive() || !player.isAlive()
				|| player.distanceTo(target) > MAX_TARGET_DISTANCE) {
			wTapTarget = null;
			return;
		}

		ticksSinceHit++;
		if (target.hurtTime > 0) {
			sawTargetHurt = true;
		}

		boolean attackReady = player.getAttackStrengthScale(0.0F) >= 1.0F;
		// The hurt animation starts when the server confirms the hit, wait for it to start and end
		boolean targetVulnerable = sawTargetHurt ? target.hurtTime <= 0 : ticksSinceHit >= MAX_HURT_WAIT_TICKS;

		if (attackReady && targetVulnerable) {
			wTapTarget = null;
			play(config.wTapSound, config.wTapNote);
		}
	}

	public static void play(NoteSound sound, int note) {
		// Same pitch as a note block: note 12 is the normal pitch, 0 and 24 are one octave lower/higher
		float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.sound.value(), pitch, (float) config().volume));
	}
}
