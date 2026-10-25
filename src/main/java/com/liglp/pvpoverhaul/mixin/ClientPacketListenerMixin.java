package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * No Crystal Explosion VFX: skip the explosion particles, but keep the sound and knockback.
 */
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@WrapWithCondition(method = "handleExplosion", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private boolean liglpspvp$hideExplosionParticle(ClientLevel level, ParticleOptions particle, double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed) {
		return !PvpConfig.get().noCrystalExplosionVfx;
	}

	@WrapWithCondition(method = "handleExplosion", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;trackExplosionEffects(Lnet/minecraft/world/phys/Vec3;FILnet/minecraft/util/random/WeightedList;)V"))
	private boolean liglpspvp$hideExplosionBlockParticles(ClientLevel level, Vec3 center, float radius, int blockCount,
			WeightedList<ExplosionParticleInfo> blockParticles) {
		return !PvpConfig.get().noCrystalExplosionVfx;
	}
}
