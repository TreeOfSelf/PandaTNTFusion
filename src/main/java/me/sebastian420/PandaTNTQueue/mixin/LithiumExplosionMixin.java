package me.sebastian420.PandaTNTQueue.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.sebastian420.PandaTNTQueue.PandaTNTQueue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Explosion.class, priority = 1500) // priority is higher than the target mixin
public abstract class LithiumExplosionMixin {
    @Shadow @Final private World world;

    @TargetHandler(
            mixin = "me.jellysquid.mods.lithium.mixin.world.explosions.block_raycast.ExplosionMixin",
            name = "collectBlocks(Lit/unimi/dsi/fastutil/objects/ObjectArrayList;Ljava/util/Collection;)Z"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;add(Ljava/lang/Object;)Z"
            )
    )
    private boolean addTNTQueue(ObjectArrayList<BlockPos> instance, Object e) {
        BlockPos pos = (BlockPos)e;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() != Blocks.TNT) {
            return instance.add(pos);
        } else {
            PandaTNTQueue.addQueue((ServerWorld) world, pos, (Explosion)(Object)this);
            return false;
        }
    }
}