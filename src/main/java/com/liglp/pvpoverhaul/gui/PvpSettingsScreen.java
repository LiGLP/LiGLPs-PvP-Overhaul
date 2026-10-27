package com.liglp.pvpoverhaul.gui;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Settings menu opened from the skin button in the pause menu.
 */
public class PvpSettingsScreen extends Screen {
	private static final int BUTTON_WIDTH = 180;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ROW = 24;
	private static final int COLUMN_GAP = 10;

	private final Screen parent;
	private final List<Runnable> refreshers = new ArrayList<>();
	private int columnTop;

	public PvpSettingsScreen(Screen parent) {
		super(Component.literal("LiGLP's PvP Overhaul"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		refreshers.clear();
		PvpConfig config = PvpConfig.get();

		int left = this.width / 2 - BUTTON_WIDTH - COLUMN_GAP / 2;
		int right = this.width / 2 + COLUMN_GAP / 2;
		columnTop = Math.max(40, this.height / 2 - 3 * ROW - 10);

		// General column
		int y = columnTop;
		toggle(left, y, "Clear Water", "Removes underwater fog and the water overlay.",
				() -> config.clearWater, v -> config.clearWater = v);
		toggle(left, y += ROW, "Clear Lava", "Removes the thick lava fog.",
				() -> config.clearLava, v -> config.clearLava = v);
		toggle(left, y += ROW, "Clear Powder Snow", "Removes powder snow fog and the frozen overlay.",
				() -> config.clearPowderSnow, v -> config.clearPowderSnow = v);
		toggle(left, y += ROW, "Fullbright", "Everything is fully lit.",
				() -> config.fullbright, v -> config.fullbright = v);
		toggle(left, y += ROW, "Fire View", "Hides the fire on your screen while you are burning.",
				() -> config.noFireOverlay, v -> config.noFireOverlay = v);

		// Crystal column: the category button toggles everything below it
		y = columnTop;
		Button category = Button.builder(Component.empty(), b -> {
			config.setCrystalCategory(!config.isCrystalCategoryFullyEnabled());
			PvpConfig.save();
			refreshAll();
		}).bounds(right, y, BUTTON_WIDTH, BUTTON_HEIGHT)
				.tooltip(Tooltip.create(Component.literal("Turn the whole Crystal category on or off.")))
				.build();
		refreshers.add(() -> category.setMessage(categoryLabel(config)));
		this.addRenderableWidget(category);

		toggle(right, y += ROW, "Small Totem & Totem Pop", "Smaller held totems, smaller totem pop animation and smaller food while eating.",
				() -> config.smallTotemAndFood, v -> config.smallTotemAndFood = v);
		toggle(right, y += ROW, "No Ender Pearl SFX", "Mutes ender pearl throw and teleport sounds.",
				() -> config.noEnderPearlSounds, v -> config.noEnderPearlSounds = v);
		toggle(right, y += ROW, "No Crystal Explosion VFX", "Hides explosion particles.",
				() -> config.noCrystalExplosionVfx, v -> config.noCrystalExplosionVfx = v);
		toggle(right, y += ROW, "Lower Shield", "Renders your shield lower in first person.",
				() -> config.lowerShield, v -> config.lowerShield = v);

		this.addRenderableWidget(Button.builder(Component.literal("Armor HUD..."), b -> this.minecraft.setScreen(new ArmorHudSettingsScreen(this)))
				.bounds(left, columnTop + 5 * ROW + 4, BUTTON_WIDTH, BUTTON_HEIGHT)
				.tooltip(Tooltip.create(Component.literal("Shows your armor next to the hotbar and warns you before it breaks.")))
				.build());

		this.addRenderableWidget(Button.builder(Component.literal("Sword PvP..."), b -> this.minecraft.setScreen(new SwordPvpSettingsScreen(this)))
				.bounds(right, columnTop + 5 * ROW + 4, BUTTON_WIDTH, BUTTON_HEIGHT)
				.tooltip(Tooltip.create(Component.literal("Note block sounds for low health and for when you can W-tap and hit again.")))
				.build());

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose())
				.bounds(this.width / 2 - 100, columnTop + 6 * ROW + 10, 200, BUTTON_HEIGHT)
				.build());

		refreshAll();
	}

	private void toggle(int x, int y, String name, String description, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		Button button = Button.builder(Component.empty(), b -> {
			setter.accept(!getter.get());
			PvpConfig.save();
			refreshAll();
		}).bounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT)
				.tooltip(Tooltip.create(Component.literal(description)))
				.build();
		refreshers.add(() -> button.setMessage(label(name, getter.get())));
		this.addRenderableWidget(button);
	}

	private void refreshAll() {
		refreshers.forEach(Runnable::run);
	}

	private static Component label(String name, boolean on) {
		return Component.literal(name + ": ").append(on
				? Component.literal("ON").withStyle(ChatFormatting.GREEN)
				: Component.literal("OFF").withStyle(ChatFormatting.RED));
	}

	private static Component categoryLabel(PvpConfig config) {
		int enabled = (config.smallTotemAndFood ? 1 : 0) + (config.noEnderPearlSounds ? 1 : 0)
				+ (config.noCrystalExplosionVfx ? 1 : 0) + (config.lowerShield ? 1 : 0);
		Component state = switch (enabled) {
			case 4 -> Component.literal("ON").withStyle(ChatFormatting.GREEN);
			case 0 -> Component.literal("OFF").withStyle(ChatFormatting.RED);
			default -> Component.literal(enabled + "/4").withStyle(ChatFormatting.YELLOW);
		};
		return Component.literal("Crystal Category: ").withStyle(ChatFormatting.BOLD).append(state);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredString(this.font, this.title, this.width / 2, columnTop - 28, 0xFFFFFFFF);
		int left = this.width / 2 - BUTTON_WIDTH - COLUMN_GAP / 2;
		int right = this.width / 2 + COLUMN_GAP / 2;
		graphics.drawCenteredString(this.font, Component.literal("General"), left + BUTTON_WIDTH / 2, columnTop - 12, 0xFFAAAAAA);
		graphics.drawCenteredString(this.font, Component.literal("Crystal"), right + BUTTON_WIDTH / 2, columnTop - 12, 0xFFAAAAAA);
	}

	@Override
	public void onClose() {
		PvpConfig.save();
		this.minecraft.setScreen(this.parent);
	}
}
