package com.liglp.pvpoverhaul.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

/**
 * Slider for a number between min and max, snapped to step.
 */
public class ValueSlider extends AbstractSliderButton {
	private final String name;
	private final double min;
	private final double max;
	private final double step;
	private final DoubleFunction<Component> valueToText;
	private final DoubleConsumer setter;

	public ValueSlider(String name, double initial, double min, double max, double step,
			DoubleFunction<Component> valueToText, DoubleConsumer setter) {
		super(0, 0, ConfigWidgets.WIDTH, ConfigWidgets.HEIGHT, Component.empty(), (Mth.clamp(initial, min, max) - min) / (max - min));
		this.name = name;
		this.min = min;
		this.max = max;
		this.step = step;
		this.valueToText = valueToText;
		this.setter = setter;
		this.updateMessage();
	}

	private double currentValue() {
		double raw = min + this.value * (max - min);
		return Mth.clamp(Math.round(raw / step) * step, min, max);
	}

	@Override
	protected void updateMessage() {
		this.setMessage(Component.literal(name + ": ").append(valueToText.apply(currentValue())));
	}

	@Override
	protected void applyValue() {
		setter.accept(currentValue());
	}
}
