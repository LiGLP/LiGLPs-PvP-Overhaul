package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
	@Unique
	private static final float SMALL_TOTEM_POP_SCALE = 0.5F;

	// Fire View: no fire on the screen while burning
	@Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
	private static void liglpspvp$hideFire(PoseStack poseStack, MultiBufferSource bufferSource, TextureAtlasSprite sprite, CallbackInfo ci) {
		if (PvpConfig.get().noFireOverlay) {
			ci.cancel();
		}
	}

	// Clear Water: no blue underwater texture on the screen
	@Inject(method = "renderWater", at = @At("HEAD"), cancellable = true)
	private static void liglpspvp$hideWater(Minecraft minecraft, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
		if (PvpConfig.get().clearWater) {
			ci.cancel();
		}
	}

	// Small Totem Pop: shrink the item activation animation
	@Inject(method = "renderItemActivationAnimation", at = @At(value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER))
	private void liglpspvp$smallTotemPop(PoseStack poseStack, float partialTick, SubmitNodeCollector collector, CallbackInfo ci) {
		if (PvpConfig.get().smallTotemAndFood) {
			poseStack.scale(SMALL_TOTEM_POP_SCALE, SMALL_TOTEM_POP_SCALE, SMALL_TOTEM_POP_SCALE);
		}
	}
}
