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
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Pushes the boss bars down when the armor HUD is at the top center.
 */
@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void liglpspvp$calculateOffset(GuiGraphics graphics, CallbackInfo ci, @Share("offset") LocalIntRef offsetRef) {
		ArmorHudConfig config = ArmorHud.config();
		if (!config.enabled || !config.pushBossbars || config.anchor != ArmorHudConfig.Anchor.TOP_CENTER) return;

		Player player = ArmorHud.getCameraPlayer();
		if (player == null) return;
		Optional<Rect2i> rect = ArmorHud.getEffectiveWidgetRect(graphics, player);
		if (rect.isEmpty()) return;

		offsetRef.set(rect.get().getY() + rect.get().getHeight());
	}

	@ModifyVariable(method = "render", at = @At("STORE"), ordinal = 1)
	private int liglpspvp$pushBossBars(int y, @Share("offset") LocalIntRef offsetRef) {
		return y + offsetRef.get();
	}
}
