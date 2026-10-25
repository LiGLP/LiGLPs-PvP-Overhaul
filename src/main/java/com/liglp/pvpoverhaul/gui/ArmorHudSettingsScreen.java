package com.liglp.pvpoverhaul.gui;

import com.liglp.pvpoverhaul.armorhud.ArmorHudConfig;
import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * All settings of the Armor HUD, opened from the "Armor HUD" button in the main settings menu.
 */
public class ArmorHudSettingsScreen extends Screen {
	private static final int WIDGET_WIDTH = 150;
	private static final int WIDGET_HEIGHT = 20;

	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private OptionList list;

	public ArmorHudSettingsScreen(Screen parent) {
		super(Component.literal("Armor HUD"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		ArmorHudConfig config = PvpConfig.get().armorHud;

		this.layout.addTitleHeader(this.title, this.font);
		this.list = this.layout.addToContents(new OptionList(this.minecraft, this.width, this.layout));

		List<AbstractWidget> widgets = new ArrayList<>();
		widgets.add(toggle("Enabled", config.enabled, v -> config.enabled = v));
		widgets.add(cycle("Anchor", ArmorHudConfig.Anchor.values(), config.anchor, a -> a.label, v -> config.anchor = v));
		widgets.add(cycle("Side", ArmorHudConfig.Side.values(), config.side, s -> s.label, v -> config.side = v));
		widgets.add(cycle("Style", ArmorHudConfig.Style.values(), config.style, s -> s.label, v -> config.style = v));
		widgets.add(cycle("Orientation", ArmorHudConfig.Orientation.values(), config.orientation, o -> o.label, v -> config.orientation = v));
		widgets.add(cycle("Show Widget", ArmorHudConfig.WidgetShown.values(), config.widgetShown, w -> w.label, v -> config.widgetShown = v));
		widgets.add(cycle("Offhand Slot", ArmorHudConfig.OffhandSlotBehavior.values(), config.offhandSlotBehavior, o -> o.label, v -> config.offhandSlotBehavior = v));
		widgets.add(cycle("Durability", ArmorHudConfig.DurabilityDisplay.values(), config.durabilityDisplay, d -> d.label, v -> config.durabilityDisplay = v));
		widgets.add(toggle("Push Bossbars", config.pushBossbars, v -> config.pushBossbars = v));
		widgets.add(toggle("Push Effect Icons", config.pushStatusEffectIcons, v -> config.pushStatusEffectIcons = v));
		widgets.add(toggle("Push Subtitles", config.pushSubtitles, v -> config.pushSubtitles = v));
		widgets.add(toggle("Reversed", config.reversed, v -> config.reversed = v));
		widgets.add(toggle("Show Icons", config.iconsShown, v -> config.iconsShown = v));
		widgets.add(toggle("Breaking Sound", config.playBreakSound, v -> config.playBreakSound = v));
		widgets.add(toggle("Show Warning", config.warningShown, v -> config.warningShown = v));
		widgets.add(new ValueSlider("Warning Bob", config.warningBobIntensity, 0, 10, 1,
				v -> Component.literal(String.valueOf((int) v)), v -> config.warningBobIntensity = (int) v));
		widgets.add(new ValueSlider("Min Durability", config.minDurabilityValue, 0, 200, 1,
				v -> Component.literal(String.valueOf((int) v)), v -> config.minDurabilityValue = (int) v));
		widgets.add(new ValueSlider("Min Durability", config.minDurabilityPercentage, 0, 1, 0.01,
				v -> Component.literal(Math.round(v * 100) + "%"), v -> config.minDurabilityPercentage = v));
		widgets.add(new ValueSlider("X Offset", config.offsetX, -200, 200, 1,
				v -> Component.literal(String.valueOf((int) v)), v -> config.offsetX = (int) v));
		widgets.add(new ValueSlider("Y Offset", config.offsetY, -200, 200, 1,
				v -> Component.literal(String.valueOf((int) v)), v -> config.offsetY = (int) v));

		for (int i = 0; i < widgets.size(); i += 2) {
			this.list.addRow(widgets.subList(i, Math.min(i + 2, widgets.size())));
		}

		this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).width(200).build());
		this.layout.visitWidgets(this::addRenderableWidget);
		this.repositionElements();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
		if (this.list != null) {
			this.list.updateSize(this.width, this.layout);
		}
	}

	@Override
	public void onClose() {
		PvpConfig.save();
		this.minecraft.setScreen(this.parent);
	}

	private static CycleButton<Boolean> toggle(String name, boolean value, Consumer<Boolean> setter) {
		return CycleButton.onOffBuilder(value)
				.create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Component.literal(name), (button, v) -> setter.accept(v));
	}

	private static <T> CycleButton<T> cycle(String name, T[] values, T value, Function<T, String> label, Consumer<T> setter) {
		return CycleButton.<T>builder(v -> Component.literal(label.apply(v)), value)
				.withValues(values)
				.create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Component.literal(name), (button, v) -> setter.accept(v));
	}

	/**
	 * Slider for a number between min and max, snapped to step.
	 */
	private static class ValueSlider extends AbstractSliderButton {
		private final String name;
		private final double min;
		private final double max;
		private final double step;
		private final DoubleFunction<Component> valueToText;
		private final DoubleConsumer setter;

		ValueSlider(String name, double initial, double min, double max, double step,
				DoubleFunction<Component> valueToText, DoubleConsumer setter) {
			super(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Component.empty(), (Mth.clamp(initial, min, max) - min) / (max - min));
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

	/**
	 * Scrollable list with two widgets per row.
	 */
	private static class OptionList extends ContainerObjectSelectionList<OptionList.Row> {
		OptionList(Minecraft minecraft, int width, HeaderAndFooterLayout layout) {
			super(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), 25);
		}

		void addRow(List<AbstractWidget> widgets) {
			this.addEntry(new Row(List.copyOf(widgets)));
		}

		@Override
		public int getRowWidth() {
			return 310;
		}

		class Row extends ContainerObjectSelectionList.Entry<Row> {
			private final List<AbstractWidget> widgets;

			Row(List<AbstractWidget> widgets) {
				this.widgets = widgets;
			}

			@Override
			public List<? extends NarratableEntry> narratables() {
				return widgets;
			}

			@Override
			public List<? extends GuiEventListener> children() {
				return widgets;
			}

			@Override
			public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float delta) {
				int x = OptionList.this.getWidth() / 2 - 155;
				for (AbstractWidget widget : widgets) {
					widget.setPosition(x, this.getContentY());
					widget.render(graphics, mouseX, mouseY, delta);
					x += 160;
				}
			}
		}
	}
}
