package com.liglp.pvpoverhaul.gui;

import com.liglp.pvpoverhaul.PvpOverhaul;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * A square button that shows LiGLP's skin face on top of the normal button background.
 */
public class SkinButton extends Button {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(PvpOverhaul.MOD_ID, "textures/gui/skin_button.png");
	private static final int TEXTURE_SIZE = 128;
	private static final int PADDING = 2;

	public SkinButton(int x, int y, int size, OnPress onPress) {
		super(x, y, size, size, Component.literal("LiGLP's PvP Overhaul"), onPress, DEFAULT_NARRATION);
		this.setTooltip(Tooltip.create(Component.literal("LiGLP's PvP Overhaul")));
	}

	@Override
	protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderDefaultSprite(graphics);
		int size = this.getWidth() - PADDING * 2;
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX() + PADDING, this.getY() + PADDING,
				0.0F, 0.0F, size, size, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
	}
}
