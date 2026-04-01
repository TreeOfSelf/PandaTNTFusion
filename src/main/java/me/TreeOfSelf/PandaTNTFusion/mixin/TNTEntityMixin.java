package me.TreeOfSelf.PandaTNTFusion.mixin;

import me.TreeOfSelf.PandaTNTFusion.TNTEntityAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class TNTEntityMixin extends Entity implements TNTEntityAccess {

	@Shadow
	private boolean usedPortal;

	@Shadow
	@Final
	private static ExplosionDamageCalculator USED_PORTAL_DAMAGE_CALCULATOR;

	public TNTEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Unique
	float setPower = 4.0F;

	@Override
	public void pandaTNTFusion$addPower() {
		if (setPower < 16.0F) {
			setPower += 0.1F;
		}
	}

	@Inject(method = "explode", at = @At("HEAD"), cancellable = true)
	private void explode(CallbackInfo ci) {
		PrimedTnt tntEntity = (PrimedTnt) (Object) this;
		this.level()
			.explode(
				tntEntity,
				Explosion.getDefaultDamageSource(this.level(), tntEntity),
				this.usedPortal ? USED_PORTAL_DAMAGE_CALCULATOR : null,
				this.getX(),
				this.getY(0.0625),
				this.getZ(),
				this.setPower,
				false,
				Level.ExplosionInteraction.TNT
			);
		ci.cancel();
	}
}
