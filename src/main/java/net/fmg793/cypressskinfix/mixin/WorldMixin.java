package net.fmg793.cypressskinfix.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fmg793.cypressskinfix.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(World.class)
public class WorldMixin {
	@Shadow
	public List entities;
	@Unique
	private boolean hasSkin;

	@Inject(method = "addEntityAlways(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	public void addEntityAlwaysMixin(Entity entity, CallbackInfo ci) {
		if (this.entities.contains((PlayerEntity)entity) && hasSkin == false) {
			SkinManager.addSkin();
			hasSkin = true;
		}
	}

	@Inject(method = "addEntityAlways(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
	public void addEntityAlways2Mixin(Entity entity, CallbackInfo ci) {
		hasSkin = true;
		if (!this.entities.contains((PlayerEntity)entity)) {
			hasSkin = false;
		}
	}
}
