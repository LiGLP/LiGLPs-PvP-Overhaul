package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	// No Ender Pearl SFX: mute the throw and the teleport sound
	@Inject(method = "play", at = @At("HEAD"), cancellable = true)
	private void liglpspvp$muteEnderPearl(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
		if (!PvpConfig.get().noEnderPearlSounds) {
			return;
		}
		Identifier id = sound.getIdentifier();
		if (id.equals(SoundEvents.ENDER_PEARL_THROW.location()) || id.equals(SoundEvents.PLAYER_TELEPORT.location())) {
			cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
		}
	}
}
