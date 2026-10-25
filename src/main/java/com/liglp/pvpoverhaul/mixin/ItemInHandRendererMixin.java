package com.liglp.pvpoverhaul.mixin;

import com.liglp.pvpoverhaul.config.PvpConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Unique
	private static final float SMALL_ITEM_SCALE = 0.6F;
	@Unique
	private static final float SHIELD_LOWER_OFFSET = -0.25F;

	@Unique
	private boolean liglpspvp$pushed;

	@Inject(method = "renderItem", at = @At("HEAD"))
	private void liglpspvp$transformItem(LivingEntity entity, ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
			SubmitNodeCollector collector, int light, CallbackInfo ci) {
		liglpspvp$pushed = false;
		if (stack.isEmpty() || !context.firstPerson()) {
			return;
		}
		PvpConfig config = PvpConfig.get();
		if (config.lowerShield && stack.getItem() instanceof ShieldItem) {
			poseStack.pushPose();
			liglpspvp$pushed = true;
			poseStack.translate(0.0F, SHIELD_LOWER_OFFSET, 0.0F);
		} else if (config.smallTotemAndFood && (stack.is(Items.TOTEM_OF_UNDYING) || stack.has(DataComponents.FOOD))) {
			poseStack.pushPose();
			liglpspvp$pushed = true;
			poseStack.scale(SMALL_ITEM_SCALE, SMALL_ITEM_SCALE, SMALL_ITEM_SCALE);
		}
	}

	@Inject(method = "renderItem", at = @At("TAIL"))
	private void liglpspvp$restore(LivingEntity entity, ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
			SubmitNodeCollector collector, int light, CallbackInfo ci) {
		if (liglpspvp$pushed) {
			poseStack.popPose();
			liglpspvp$pushed = false;
		}
	}
}
