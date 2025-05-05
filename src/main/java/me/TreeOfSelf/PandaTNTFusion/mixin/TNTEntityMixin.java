package me.TreeOfSelf.PandaTNTFusion.mixin;

import me.TreeOfSelf.PandaTNTFusion.TNTEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntEntity.class)
public abstract class TNTEntityMixin extends Entity implements TNTEntityAccess {

    @Shadow private boolean teleported;

    @Shadow @Final private static ExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR;

    public TNTEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    float setPower = 4.0F;

    @Override
    public void pandaTNTFusion$addPower() {
        if (setPower < 16.0F) {
            setPower += 0.1F;
        }
    }


    @Inject(method = "explode", at = @At(value = "HEAD"), cancellable = true)
    private void explode(CallbackInfo ci) {
        TntEntity tntEntity = (TntEntity) (Object) this;
        this.getWorld().createExplosion(tntEntity, Explosion.createDamageSource(this.getWorld(), tntEntity), this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null, this.getX(), this.getBodyY(0.0625), this.getZ(), this.setPower, false, World.ExplosionSourceType.TNT);
        ci.cancel();
    }
}