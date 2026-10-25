/*
 * Based on uku's Armor HUD (https://github.com/uku3lig/armor-hud),
 * Copyright (c) 2021 BerdinskiyBear, forked by uku. Licensed under the MIT License,
 * see THIRD_PARTY_LICENSES.md. Modified for LiGLP's PvP Overhaul.
 */
package com.liglp.pvpoverhaul.armorhud;

import net.minecraft.world.entity.HumanoidArm;

/**
 * Settings of the armor HUD widget. Stored inside the main mod config.
 */
public class ArmorHudConfig {
	public boolean enabled = false;
	public Anchor anchor = Anchor.HOTBAR;
	public Side side = Side.LEFT;
	public int offsetX = 0;
	public int offsetY = 0;
	public Style style = Style.HOTBAR;
	public Orientation orientation = Orientation.HORIZONTAL;
	public WidgetShown widgetShown = WidgetShown.NOT_EMPTY;
	public OffhandSlotBehavior offhandSlotBehavior = OffhandSlotBehavior.ADHERE;
	public DurabilityDisplay durabilityDisplay = DurabilityDisplay.BAR;
	public boolean pushBossbars = true;
	public boolean pushStatusEffectIcons = true;
	public boolean pushSubtitles = true;
	public boolean reversed = false;
	public boolean iconsShown = true;
	public boolean warningShown = true;
	public boolean playBreakSound = true;
	public int minDurabilityValue = 20;
	public double minDurabilityPercentage = 0.1;
	public int warningBobIntensity = 3;

	/**
	 * Replaces missing or unknown values (e.g. from a hand-edited config file) with the defaults.
	 */
	public void sanitize() {
		ArmorHudConfig defaults = new ArmorHudConfig();
		if (anchor == null) anchor = defaults.anchor;
		if (side == null) side = defaults.side;
		if (style == null) style = defaults.style;
		if (orientation == null) orientation = defaults.orientation;
		if (widgetShown == null) widgetShown = defaults.widgetShown;
		if (offhandSlotBehavior == null) offhandSlotBehavior = defaults.offhandSlotBehavior;
		if (durabilityDisplay == null) durabilityDisplay = defaults.durabilityDisplay;
		warningBobIntensity = Math.max(0, warningBobIntensity);
		minDurabilityValue = Math.max(0, minDurabilityValue);
		minDurabilityPercentage = Math.clamp(minDurabilityPercentage, 0.0, 1.0);
	}

	public enum Anchor {
		TOP_CENTER("Top Center"),
		TOP("Top"),
		BOTTOM("Bottom"),
		HOTBAR("Hotbar");

		public final String label;

		Anchor(String label) {
			this.label = label;
		}

		public boolean isTop() {
			return this == TOP || this == TOP_CENTER;
		}
	}

	public enum Side {
		LEFT("Left"),
		RIGHT("Right");

		public final String label;

		Side(String label) {
			this.label = label;
		}

		public Side getOpposite() {
			return this == LEFT ? RIGHT : LEFT;
		}

		public HumanoidArm asArm() {
			return this == LEFT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
		}
	}

	public enum OffhandSlotBehavior {
		ALWAYS_IGNORE("Always Ignore"),
		ADHERE("Adhere"),
		ALWAYS_LEAVE_SPACE("Always Leave Space");

		public final String label;

		OffhandSlotBehavior(String label) {
			this.label = label;
		}
	}

	public enum WidgetShown {
		ALWAYS("Always"),
		IF_ANY_PRESENT("If Any Present"),
		NOT_EMPTY("Not Empty"),
		DAMAGED_PIECES("Damaged Pieces");

		public final String label;

		WidgetShown(String label) {
			this.label = label;
		}

		public boolean shouldDrawEmptySlots() {
			return this == ALWAYS || this == IF_ANY_PRESENT;
		}
	}

	public enum Style {
		HOTBAR("Hotbar"),
		ROUNDED_CORNERS("Rounded Corners"),
		ROUNDED("Rounded"),
		NONE("None");

		public final String label;

		Style(String label) {
			this.label = label;
		}
	}

	public enum Orientation {
		HORIZONTAL("Horizontal"),
		VERTICAL("Vertical");

		public final String label;

		Orientation(String label) {
			this.label = label;
		}
	}

	public enum DurabilityDisplay {
		BAR("Bar"),
		NUMERIC("Numeric"),
		PERCENTAGE("Percentage");

		public final String label;

		DurabilityDisplay(String label) {
			this.label = label;
		}
	}
}
