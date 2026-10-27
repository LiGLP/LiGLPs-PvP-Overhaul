package com.liglp.pvpoverhaul.gui;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Small factories for the buttons used in the settings lists.
 */
public final class ConfigWidgets {
	public static final int WIDTH = 150;
	public static final int HEIGHT = 20;

	private ConfigWidgets() {
	}

	public static CycleButton<Boolean> toggle(String name, boolean value, Consumer<Boolean> setter) {
		return CycleButton.onOffBuilder(value)
				.create(0, 0, WIDTH, HEIGHT, Component.literal(name), (button, v) -> setter.accept(v));
	}

	public static <T> CycleButton<T> cycle(String name, T[] values, T value, Function<T, String> label, Consumer<T> setter) {
		return CycleButton.<T>builder(v -> Component.literal(label.apply(v)), value)
				.withValues(values)
				.create(0, 0, WIDTH, HEIGHT, Component.literal(name), (button, v) -> setter.accept(v));
	}
}
