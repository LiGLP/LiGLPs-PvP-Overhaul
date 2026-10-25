package com.liglp.pvpoverhaul.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.List;

/**
 * Adds the skin button to the pause (ESC) menu, left of the "Options..." button.
 */
public final class PauseMenuButton {
	private static final int SIZE = 20;
	private static final int GAP = 4;

	private PauseMenuButton() {
	}

	public static void register() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof PauseScreen)) {
				return;
			}
			List<AbstractWidget> buttons = Screens.getButtons(screen);
			AbstractWidget options = null;
			for (AbstractWidget widget : buttons) {
				if (widget.getMessage().getContents() instanceof TranslatableContents translatable
						&& translatable.getKey().equals("menu.options")) {
					options = widget;
					break;
				}
			}
			// The pause menu without buttons (F3+Esc) has no options button
			if (options == null) {
				return;
			}
			int x = options.getX() - SIZE - GAP;
			int y = options.getY() + (options.getHeight() - SIZE) / 2;
			buttons.add(new SkinButton(x, y, SIZE, b -> client.setScreen(new PvpSettingsScreen(screen))));
		});
	}
}
