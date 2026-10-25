/*
 * Based on uku's Armor HUD (https://github.com/uku3lig/armor-hud),
 * Copyright (c) 2021 BerdinskiyBear, forked by uku. Licensed under the MIT License,
 * see THIRD_PARTY_LICENSES.md. Modified for LiGLP's PvP Overhaul.
 */
package com.liglp.pvpoverhaul.armorhud;

import com.liglp.pvpoverhaul.PvpOverhaul;
import com.liglp.pvpoverhaul.config.PvpConfig;
import com.liglp.pvpoverhaul.mixin.armorhud.InventoryMenuAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Armor HUD: a small vanilla-style widget that shows the armor you are wearing
 * and warns you when a piece is about to break.
 */
public final class ArmorHud {
	public static final int STEP = 20;
	public static final int SIZE = 22;
	public static final int HOTBAR_OFFSET = 98;
	public static final int OFFHAND_OFFSET = 29;
	public static final int ATTACK_INDICATOR_OFFSET = 23;
	public static final int WARNING_SIZE = 8;

	public static final Identifier WARNING_TEXTURE = Identifier.fromNamespaceAndPath(PvpOverhaul.MOD_ID, "textures/gui/armor_hud_warning.png");
	public static final SoundEvent ARMOR_BREAKING_SOUND = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(PvpOverhaul.MOD_ID, "armor_breaking"));

	public static final EquipmentSlot[] SLOT_IDS = InventoryMenuAccessor.getSLOT_IDS();

	private static final List<ItemStack> lastStacks = new ArrayList<>(Collections.nCopies(SLOT_IDS.length, ItemStack.EMPTY));

	private ArmorHud() {
	}

	public static ArmorHudConfig config() {
		return PvpConfig.get().armorHud;
	}

	public static void register() {
		KeyMapping toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.liglpspvp.toggle_armor_hud",
				InputConstants.UNKNOWN.getValue(), KeyMapping.Category.register(Identifier.fromNamespaceAndPath(PvpOverhaul.MOD_ID, "main"))));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				ArmorHudConfig config = config();
				config.enabled = !config.enabled;
				PvpConfig.save();
				client.gui.setOverlayMessage(Component.literal("Armor HUD: " + (config.enabled ? "ON" : "OFF")), false);
			}

			Player player = getCameraPlayer();
			if (player != null && config().enabled && config().playBreakSound && shouldPlayBreakSound(player)) {
				player.makeSound(ARMOR_BREAKING_SOUND);
			}
		});
	}

	public static @Nullable Player getCameraPlayer() {
		return Minecraft.getInstance().getCameraEntity() instanceof Player player ? player : null;
	}

	/**
	 * Returns the bounding box of the widget itself, <strong>excluding</strong> "external" information like warning icon
	 */
	public static Optional<Rect2i> getWidgetRect(GuiGraphics graphics, Player player) {
		ArmorHudConfig config = config();
		List<ItemStack> armorItems = getArmorItems(player);

		if (armorItems.isEmpty()) {
			return Optional.empty();
		}

		// hotbar offset is relative to the bar, so when we are on the left it needs to be flipped
		// and on the right side, we need to flip the offset, except when anchored to the hotbar
		final int sideMultiplier, sideOffsetMultiplier;
		if ((config.anchor == ArmorHudConfig.Anchor.HOTBAR && config.side == ArmorHudConfig.Side.LEFT)
				|| (config.anchor != ArmorHudConfig.Anchor.HOTBAR && config.side == ArmorHudConfig.Side.RIGHT)) {
			sideMultiplier = -1;
			sideOffsetMultiplier = -1;
		} else {
			sideMultiplier = 1;
			sideOffsetMultiplier = 0;
		}

		final int addedHotbarOffset = switch (config.offhandSlotBehavior) {
			case ALWAYS_IGNORE -> 0;
			case ALWAYS_LEAVE_SPACE -> player.getMainArm() == config.side.asArm() ? ATTACK_INDICATOR_OFFSET : OFFHAND_OFFSET;
			case ADHERE -> {
				if (player.getMainArm() == config.side.asArm()) {
					if (Minecraft.getInstance().options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR
							&& player.getAttackStrengthScale(0) < 1) {
						yield ATTACK_INDICATOR_OFFSET;
					}
				} else if (!player.getOffhandItem().isEmpty()) {
					yield OFFHAND_OFFSET;
				}

				yield 0;
			}
		};

		final int textureWidth = SIZE + ((armorItems.size() - 1) * STEP);
		final int widgetWidth = config.orientation == ArmorHudConfig.Orientation.VERTICAL ? SIZE : textureWidth;
		final int widgetHeight = config.orientation == ArmorHudConfig.Orientation.VERTICAL ? textureWidth : SIZE;

		final int armorWidgetX = config.offsetX * sideMultiplier + switch (config.anchor) {
			case TOP_CENTER -> (graphics.guiWidth() - widgetWidth) / 2;
			case TOP, BOTTOM -> (widgetWidth - graphics.guiWidth()) * sideOffsetMultiplier;
			case HOTBAR -> graphics.guiWidth() / 2 + ((HOTBAR_OFFSET + addedHotbarOffset) * sideMultiplier) + (widgetWidth * sideOffsetMultiplier);
		};

		final int armorWidgetY = switch (config.anchor) {
			case BOTTOM, HOTBAR -> graphics.guiHeight() - widgetHeight - config.offsetY;
			case TOP, TOP_CENTER -> config.offsetY;
		};

		return Optional.of(new Rect2i(armorWidgetX, armorWidgetY, widgetWidth, widgetHeight));
	}

	/**
	 * Returns the effective bounding box, <strong>including</strong> "external" information like warning icon
	 */
	public static Optional<Rect2i> getEffectiveWidgetRect(GuiGraphics graphics, Player player) {
		ArmorHudConfig config = config();

		return getWidgetRect(graphics, player).map(rect -> {
			if (config.orientation == ArmorHudConfig.Orientation.HORIZONTAL) {
				int additionalHeight = 0;

				if (config.warningShown) {
					additionalHeight += WARNING_SIZE + 2 + (config.warningBobIntensity / 2);
				}

				if (config.durabilityDisplay != ArmorHudConfig.DurabilityDisplay.BAR) {
					additionalHeight += Minecraft.getInstance().font.lineHeight;
				}

				rect.setHeight(rect.getHeight() + additionalHeight);
				if (!config.anchor.isTop()) {
					rect.setY(rect.getY() - additionalHeight);
				}
			}

			return rect;
		});
	}

	public static List<ItemStack> getArmorItems(Player player) {
		Stream<ItemStack> items = Arrays.stream(SLOT_IDS).map(player::getItemBySlot);
		items = switch (config().widgetShown) {
			case ALWAYS -> items;
			case IF_ANY_PRESENT -> {
				List<ItemStack> itemList = items.toList();
				yield itemList.stream().allMatch(ItemStack::isEmpty) ? Stream.of() : itemList.stream();
			}
			case NOT_EMPTY -> items.filter(s -> !s.isEmpty());
			case DAMAGED_PIECES -> items.filter(ArmorHud::shouldShowWarning);
		};

		return items.toList();
	}

	public static boolean shouldPlayBreakSound(Player player) {
		boolean play = false;
		for (int i = 0; i < SLOT_IDS.length; i++) {
			ItemStack current = player.getItemBySlot(SLOT_IDS[i]);
			ItemStack last = lastStacks.set(i, current);
			if (last.getDamageValue() != current.getDamageValue() && shouldShowWarning(current)) {
				play = true;
			}
		}

		return play;
	}

	public static boolean shouldShowWarning(ItemStack stack) {
		if (stack.isEmpty() || !stack.isDamageableItem()) return false;

		final int damage = stack.getDamageValue();
		final int maxDamage = stack.getMaxDamage();
		double percentage = 1.0 - ((double) damage / maxDamage);

		return percentage <= config().minDurabilityPercentage
				|| maxDamage - damage <= config().minDurabilityValue;
	}
}
