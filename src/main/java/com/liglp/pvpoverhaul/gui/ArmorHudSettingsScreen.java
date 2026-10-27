package com.liglp.pvpoverhaul.gui;

import com.liglp.pvpoverhaul.armorhud.ArmorHudConfig;
import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * All settings of the Armor HUD, opened from the "Armor HUD" button in the main settings menu.
 */
public class ArmorHudSettingsScreen extends Screen {
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
		widgets.add(ConfigWidgets.toggle("Enabled", config.enabled, v -> config.enabled = v));
		widgets.add(ConfigWidgets.cycle("Anchor", ArmorHudConfig.Anchor.values(), config.anchor, a -> a.label, v -> config.anchor = v));
		widgets.add(ConfigWidgets.cycle("Side", ArmorHudConfig.Side.values(), config.side, s -> s.label, v -> config.side = v));
		widgets.add(ConfigWidgets.cycle("Style", ArmorHudConfig.Style.values(), config.style, s -> s.label, v -> config.style = v));
		widgets.add(ConfigWidgets.cycle("Orientation", ArmorHudConfig.Orientation.values(), config.orientation, o -> o.label, v -> config.orientation = v));
		widgets.add(ConfigWidgets.cycle("Show Widget", ArmorHudConfig.WidgetShown.values(), config.widgetShown, w -> w.label, v -> config.widgetShown = v));
		widgets.add(ConfigWidgets.cycle("Offhand Slot", ArmorHudConfig.OffhandSlotBehavior.values(), config.offhandSlotBehavior, o -> o.label, v -> config.offhandSlotBehavior = v));
		widgets.add(ConfigWidgets.cycle("Durability", ArmorHudConfig.DurabilityDisplay.values(), config.durabilityDisplay, d -> d.label, v -> config.durabilityDisplay = v));
		widgets.add(ConfigWidgets.toggle("Push Bossbars", config.pushBossbars, v -> config.pushBossbars = v));
		widgets.add(ConfigWidgets.toggle("Push Effect Icons", config.pushStatusEffectIcons, v -> config.pushStatusEffectIcons = v));
		widgets.add(ConfigWidgets.toggle("Push Subtitles", config.pushSubtitles, v -> config.pushSubtitles = v));
		widgets.add(ConfigWidgets.toggle("Reversed", config.reversed, v -> config.reversed = v));
		widgets.add(ConfigWidgets.toggle("Show Icons", config.iconsShown, v -> config.iconsShown = v));
		widgets.add(ConfigWidgets.toggle("Breaking Sound", config.playBreakSound, v -> config.playBreakSound = v));
		widgets.add(ConfigWidgets.toggle("Show Warning", config.warningShown, v -> config.warningShown = v));
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

		this.list.addPairs(widgets);

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
}
