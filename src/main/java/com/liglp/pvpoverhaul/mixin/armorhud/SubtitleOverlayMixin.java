/*
 * Based on uku's Armor HUD (https://github.com/uku3lig/armor-hud),
 * Copyright (c) 2021 BerdinskiyBear, forked by uku. Licensed under the MIT License,
 * see THIRD_PARTY_LICENSES.md. Modified for LiGLP's PvP Overhaul.
 */
package com.liglp.pvpoverhaul.mixin.armorhud;

import com.liglp.pvpoverhaul.armorhud.ArmorHud;
import com.liglp.pvpoverhaul.armorhud.ArmorHudConfig;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Pushes the subtitles up when the armor HUD is in the bottom right corner.
 */
@Mixin(SubtitleOverlay.class)
public class SubtitleOverlayMixin {
	// doing the calculation here allows to calculate only once, since there is one translate call for each subtitle
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I", ordinal = 3))
	private void liglpspvp$calculateOffset(GuiGraphics graphics, CallbackInfo ci, @Share("offset") LocalIntRef offsetRef) {
		ArmorHudConfig config = ArmorHud.config();
		if (!config.enabled || !config.pushSubtitles || config.anchor != ArmorHudConfig.Anchor.BOTTOM
				|| config.side != ArmorHudConfig.Side.RIGHT) return;

		Player player = ArmorHud.getCameraPlayer();
		if (player == null) return;
		Optional<Rect2i> rect = ArmorHud.getEffectiveWidgetRect(graphics, player);
		if (rect.isEmpty()) return;

		// The subtitles widget is approx 25 pixels above the bottom of the screen
		offsetRef.set(Math.max(rect.get().getHeight() - 25, 0));
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;translate(FF)Lorg/joml/Matrix3x2f;", shift = At.Shift.AFTER, remap = false))
	private void liglpspvp$offset(GuiGraphics graphics, CallbackInfo ci, @Share("offset") LocalIntRef offsetRef) {
		graphics.pose().translate(0.0F, -offsetRef.get());
	}
}
