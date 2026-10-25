package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
	// Clear Powder Snow: no frozen outline on the screen
	@Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
	private void liglpspvp$hidePowderSnowOverlay(GuiGraphics graphics, Identifier texture, float alpha, CallbackInfo ci) {
		if (PvpConfig.get().clearPowderSnow && texture.getPath().equals("textures/misc/powder_snow_outline.png")) {
			ci.cancel();
		}
	}
}
