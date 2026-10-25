package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.client.renderer.fog.environment.PowderedSnowFogEnvironment;
import net.minecraft.client.renderer.fog.environment.WaterFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Clear Water / Lava / Powder Snow: push the fluid fog out past the render distance.
 */
@Mixin({WaterFogEnvironment.class, LavaFogEnvironment.class, PowderedSnowFogEnvironment.class})
public class FogEnvironmentMixin {
	@Inject(method = "setupFog", at = @At("TAIL"))
	private void liglpspvp$clearFog(FogData fogData, Camera camera, ClientLevel level, float renderDistance, DeltaTracker deltaTracker, CallbackInfo ci) {
		PvpConfig config = PvpConfig.get();
		Object self = this;
		boolean clear = self instanceof WaterFogEnvironment ? config.clearWater
				: self instanceof LavaFogEnvironment ? config.clearLava
				: config.clearPowderSnow;
		if (clear) {
			fogData.environmentalStart = renderDistance;
			fogData.environmentalEnd = renderDistance * 2.0F;
			fogData.skyEnd = renderDistance;
			fogData.cloudEnd = renderDistance;
		}
	}
}
