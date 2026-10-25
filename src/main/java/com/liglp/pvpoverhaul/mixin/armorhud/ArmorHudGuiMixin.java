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
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * Draws the armor HUD next to the hotbar and pushes the status effect icons out of its way.
 */
@Mixin(Gui.class)
public abstract class ArmorHudGuiMixin {
	@Shadow
	@Final
	private RandomSource random;

	@Shadow
	@Final
	private static Identifier HOTBAR_SPRITE;

	@Shadow
	@Final
	private static Identifier HOTBAR_OFFHAND_LEFT_SPRITE;

	@Shadow
	protected abstract void renderSlot(GuiGraphics graphics, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed);

	@Shadow
	public abstract Font getFont();

	@Inject(method = "renderItemHotbar", at = @At("TAIL"))
	private void liglpspvp$renderArmorHud(GuiGraphics graphics, DeltaTracker tickCounter, CallbackInfo ci) {
		Profiler.get().push("liglpspvp_armor_hud");
		liglpspvp$drawArmorHud(graphics, tickCounter);
		Profiler.get().pop();
	}

	@Unique
	private void liglpspvp$drawArmorHud(GuiGraphics graphics, DeltaTracker tickCounter) {
		ArmorHudConfig config = ArmorHud.config();
		if (!config.enabled) return;

		Player player = ArmorHud.getCameraPlayer();
		if (player == null) return;

		final Optional<Rect2i> rect = ArmorHud.getWidgetRect(graphics, player);
		// return if there is nothing to draw
		if (rect.isEmpty()) return;

		// fetch armor items
		List<ItemStack> armorItems = ArmorHud.getArmorItems(player);
		if (config.reversed) {
			armorItems = armorItems.reversed();
		}

		final int textureWidth = ArmorHud.SIZE + ((armorItems.size() - 1) * ArmorHud.STEP);

		// here I draw the slots
		graphics.pose().pushMatrix();
		graphics.pose().translate(rect.get().getX(), rect.get().getY());

		if (config.orientation == ArmorHudConfig.Orientation.VERTICAL) {
			graphics.pose().rotate(Mth.HALF_PI).translate(0, -ArmorHud.SIZE);
		}

		int color = 0xFFFFFFFF;

		switch (config.style) {
			case HOTBAR -> {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 0, 0, 0, 0, textureWidth - 3, ArmorHud.SIZE, color);
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 182 - 3, 0, textureWidth - 3, 0, 3, ArmorHud.SIZE, color);
			}
			case ROUNDED_CORNERS -> {
				if (armorItems.size() > 1) {
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, 3, ArmorHud.SIZE, color);
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, 22, 3, 0, 3, 0, textureWidth - 6, ArmorHud.SIZE, color);
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, ArmorHud.SIZE - 3, 1, textureWidth - 3, 0, 3, ArmorHud.SIZE, color);
				} else {
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, ArmorHud.SIZE, ArmorHud.SIZE, color);
				}
			}
			case ROUNDED -> {
				if (armorItems.size() > 1) {
					int borderWidth = (ArmorHud.SIZE - ArmorHud.STEP) / 2;
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, ArmorHud.SIZE - borderWidth, ArmorHud.SIZE, color);
					// nothing happens if slots <= 2
					for (int i = 1; i < armorItems.size() - 1; i++) {
						graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, borderWidth, 1, borderWidth + i * ArmorHud.STEP, 0, ArmorHud.STEP, ArmorHud.SIZE, color);
					}
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 1, 1, textureWidth - ArmorHud.STEP - borderWidth, 0, ArmorHud.SIZE - borderWidth, ArmorHud.SIZE, color);
				} else {
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_LEFT_SPRITE, 29, 24, 0, 1, 0, 0, ArmorHud.SIZE, ArmorHud.SIZE, color);
				}
			}
			// case NONE -> // nothing to draw ^_^
		}
		graphics.pose().popMatrix();

		for (int i = 0; i < armorItems.size(); i++) {
			ItemStack stack = armorItems.get(i);
			int x = rect.get().getX();
			int y = rect.get().getY();

			switch (config.orientation) {
				case HORIZONTAL -> x += (ArmorHud.STEP * i);
				case VERTICAL -> y += (ArmorHud.STEP * i);
			}

			// here I blend in slot icons if so tells the current config
			if (config.iconsShown && config.widgetShown.shouldDrawEmptySlots() && stack.isEmpty()) {
				int slotIndex = config.reversed ? 3 - i : i;
				Identifier identifier = InventoryMenuAccessor.getTEXTURE_EMPTY_SLOTS().get(ArmorHud.SLOT_IDS[slotIndex]);
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, x + 3, y + 3, 16, 16);
			}

			// here I draw the armour items
			this.renderSlot(graphics, x + 3, y + 3, tickCounter, player, stack, i + 1);

			// when anchoring to the hotbar, we want the warning to be on the other side to avoid clipping with the hotbar
			ArmorHudConfig.Side extrasSide = config.anchor == ArmorHudConfig.Anchor.HOTBAR ? config.side : config.side.getOpposite();

			if (config.anchor.isTop() && config.orientation == ArmorHudConfig.Orientation.HORIZONTAL) {
				y += ArmorHud.SIZE;
			} else if (extrasSide == ArmorHudConfig.Side.RIGHT && config.orientation == ArmorHudConfig.Orientation.VERTICAL) {
				x += ArmorHud.SIZE;
			}

			if (config.durabilityDisplay != ArmorHudConfig.DurabilityDisplay.BAR && !stack.isEmpty() && stack.isDamageableItem()) {
				String dura = switch (config.durabilityDisplay) {
					case NUMERIC -> String.valueOf(stack.getMaxDamage() - stack.getDamageValue());
					case PERCENTAGE -> {
						double percentage = 1 - (double) stack.getDamageValue() / stack.getMaxDamage();
						yield (int) Math.floor(percentage * 100) + "%";
					}
					case BAR -> throw new IllegalStateException("unreachable");
				};
				int textHeight = this.getFont().lineHeight;

				if (config.orientation == ArmorHudConfig.Orientation.HORIZONTAL) {
					if (!config.anchor.isTop()) y -= textHeight;
					graphics.drawCenteredString(this.getFont(), dura, x + (ArmorHud.SIZE / 2), y, ARGB.opaque(stack.getBarColor()));
					if (config.anchor.isTop()) y += textHeight;
				} else {
					int textWidth = this.getFont().width(dura) + 2;
					int textY = (ArmorHud.SIZE - textHeight) / 2;

					if (extrasSide == ArmorHudConfig.Side.LEFT) x -= textWidth;
					graphics.drawString(this.getFont(), dura, x + 1, y + textY, ARGB.opaque(stack.getBarColor()));
					if (extrasSide == ArmorHudConfig.Side.RIGHT) x += textWidth;
				}
			}

			// here I draw warning icons if necessary
			if (config.warningShown && ArmorHud.shouldShowWarning(stack)) {
				if (config.warningBobIntensity != 0) {
					int intensity = config.warningBobIntensity;
					y += (int) (this.random.nextInt(intensity) - Math.ceil(intensity / 2F));
				}

				if (config.orientation == ArmorHudConfig.Orientation.HORIZONTAL) {
					if (!config.anchor.isTop()) y -= ArmorHud.WARNING_SIZE + 2;

					int warnX = (ArmorHud.SIZE - ArmorHud.WARNING_SIZE) / 2;
					graphics.blit(RenderPipelines.GUI_TEXTURED, ArmorHud.WARNING_TEXTURE, x + warnX, y + 1, 0, 0, ArmorHud.WARNING_SIZE, ArmorHud.WARNING_SIZE, ArmorHud.WARNING_SIZE, ArmorHud.WARNING_SIZE);
				} else {
					if (extrasSide == ArmorHudConfig.Side.LEFT) x -= ArmorHud.WARNING_SIZE + 2;

					int warnY = (ArmorHud.SIZE - ArmorHud.WARNING_SIZE) / 2;
					graphics.blit(RenderPipelines.GUI_TEXTURED, ArmorHud.WARNING_TEXTURE, x + 1, y + warnY, 0, 0, ArmorHud.WARNING_SIZE, ArmorHud.WARNING_SIZE, ArmorHud.WARNING_SIZE, ArmorHud.WARNING_SIZE);
				}

			}
		}
	}

	@Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
	private void liglpspvp$calculateStatusEffectIconsOffset(GuiGraphics graphics, DeltaTracker tickCounter, CallbackInfo ci, @Share("shift") LocalIntRef shiftRef) {
		ArmorHudConfig config = ArmorHud.config();
		if (!config.enabled || !config.pushStatusEffectIcons || config.anchor != ArmorHudConfig.Anchor.TOP
				|| config.side != ArmorHudConfig.Side.RIGHT) return;

		Player player = ArmorHud.getCameraPlayer();
		if (player == null) return;

		Optional<Rect2i> rect = ArmorHud.getEffectiveWidgetRect(graphics, player);
		if (rect.isEmpty()) return;

		shiftRef.set(rect.get().getY() + rect.get().getHeight());
	}

	@ModifyVariable(method = "renderEffects", at = @At(value = "STORE"), ordinal = 3)
	private int liglpspvp$statusEffectIconsOffset(int y, @Share("shift") LocalIntRef shiftRef) {
		return y + shiftRef.get();
	}
}
