package com.liglp.pvpoverhaul.gui;

import com.liglp.pvpoverhaul.config.PvpConfig;
import com.liglp.pvpoverhaul.swordpvp.NoteSound;
import com.liglp.pvpoverhaul.swordpvp.SwordPvp;
import com.liglp.pvpoverhaul.swordpvp.SwordPvpConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Settings of the sword PvP sound alerts.
 */
public class SwordPvpSettingsScreen extends Screen {
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private OptionList list;

	public SwordPvpSettingsScreen(Screen parent) {
		super(Component.literal("Sword PvP"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		SwordPvpConfig config = PvpConfig.get().swordPvp;

		this.layout.addTitleHeader(this.title, this.font);
		this.list = this.layout.addToContents(new OptionList(this.minecraft, this.width, this.layout));

		// Low health alert
		AbstractWidget lowHealth = ConfigWidgets.toggle("Low Health Alert", config.lowHealthAlert, v -> config.lowHealthAlert = v);
		lowHealth.setTooltip(Tooltip.create(Component.literal("Plays a note block sound when your health drops to the set number of hearts.")));
		this.list.addRow(List.of(
				lowHealth,
				new ValueSlider("Hearts", config.lowHealthHearts, 0.5, 10, 0.5,
						v -> Component.literal(formatHearts(v) + " ❤"), v -> config.lowHealthHearts = v)));
		this.list.addRow(List.of(
				ConfigWidgets.cycle("Sound", NoteSound.values(), config.lowHealthSound, s -> s.label, v -> config.lowHealthSound = v),
				new ValueSlider("Note", config.lowHealthNote, 0, SwordPvpConfig.MAX_NOTE, 1,
						v -> Component.literal(String.valueOf((int) v)), v -> config.lowHealthNote = (int) v)));
		this.list.addRow(List.of(testButton("Test Low Health Sound", () -> SwordPvp.play(config.lowHealthSound, config.lowHealthNote))));

		// W-tap alert
		AbstractWidget wTap = ConfigWidgets.toggle("W-Tap Alert", config.wTapAlert, v -> config.wTapAlert = v);
		wTap.setTooltip(Tooltip.create(Component.literal("After you hit something, plays a note block sound as soon as you can really W-tap and hit again: your attack is fully charged and the target can take damage and knockback again.")));
		this.list.addRow(List.of(wTap));
		this.list.addRow(List.of(
				ConfigWidgets.cycle("Sound", NoteSound.values(), config.wTapSound, s -> s.label, v -> config.wTapSound = v),
				new ValueSlider("Note", config.wTapNote, 0, SwordPvpConfig.MAX_NOTE, 1,
						v -> Component.literal(String.valueOf((int) v)), v -> config.wTapNote = (int) v)));
		this.list.addRow(List.of(testButton("Test W-Tap Sound", () -> SwordPvp.play(config.wTapSound, config.wTapNote))));

		// Shared
		this.list.addRow(List.of(new ValueSlider("Volume", config.volume, 0, 1, 0.05,
				v -> Component.literal(Math.round(v * 100) + "%"), v -> config.volume = v)));

		this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).width(200).build());
		this.layout.visitWidgets(this::addRenderableWidget);
		this.repositionElements();
	}

	private static Button testButton(String name, Runnable action) {
		return Button.builder(Component.literal(name), b -> action.run())
				.size(ConfigWidgets.WIDTH, ConfigWidgets.HEIGHT)
				.build();
	}

	private static String formatHearts(double hearts) {
		return hearts == Math.floor(hearts) ? String.valueOf((int) hearts) : String.valueOf(hearts);
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
