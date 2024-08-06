package me.sebastian420.PandaTNTQueue.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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

import java.util.*;

@Mixin(Explosion.class)
public abstract class ExplosionQueue {
	@Shadow @Final private World world;

	@Redirect(method = "collectBlocksAndDamageEntities",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"))
	private boolean addTNTQueue(Set<BlockPos> instance, Object e, @Local BlockState blockState) {
		BlockPos pos = (BlockPos)e;
		if (blockState.getBlock() != Blocks.TNT) {
			return instance.add(pos);
		} else {
			PandaTNTQueue.addQueue((ServerWorld) world, pos, (Explosion)(Object)this);
			return false;
		}
	}

}