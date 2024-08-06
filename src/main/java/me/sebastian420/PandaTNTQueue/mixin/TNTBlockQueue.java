package me.sebastian420.PandaTNTQueue.mixin;

import me.sebastian420.PandaTNTQueue.PandaTNTConfig;
import me.sebastian420.PandaTNTQueue.PandaTNTQueue;
import net.minecraft.block.TntBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntBlock.class)
public class TNTBlockQueue {
    @Inject(method = "onDestroyedByExplosion", at = @At(value = "HEAD"), cancellable = true)
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion, CallbackInfo ci) {
        if(PandaTNTQueue.tntCount >= PandaTNTQueue.maxTntAdjusted) {
            //PandaTNTQueue.addExplosionQueue((ServerWorld) world, pos, explosion);
            ci.cancel();
        }
    }
}