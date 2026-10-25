package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LightTexture.class)
public class LightTextureMixin {
	// Fullbright: the 4th lightmap uniform is the night vision factor, force it to full strength
	@ModifyArg(method = "updateLightTexture", at = @At(value = "INVOKE",
			target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 3))
	private float liglpspvp$fullbright(float nightVision) {
		return PvpConfig.get().fullbright ? 1.0F : nightVision;
	}
}
