package me.TreeOfSelf.PandaTNTFusion.mixin;

import me.TreeOfSelf.PandaTNTFusion.PandaTNTConfig;
import me.TreeOfSelf.PandaTNTFusion.PandaTNTFusion;
import me.TreeOfSelf.PandaTNTFusion.TNTEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TntBlock.class)
public class TNTBlockMixin {

	@Unique
	public PrimedTnt getNearestTNTEntity(ServerLevel world, Vec3 position, double radius) {
		AABB searchBox = new AABB(position.subtract(radius, radius, radius), position.add(radius, radius, radius));
		List<PrimedTnt> tntEntities = world.getEntities(EntityType.TNT, searchBox, entity -> entity.getBlockState().is(Blocks.TNT));

		PrimedTnt nearestTNT = null;
		double closestDistance = Double.MAX_VALUE;

		for (PrimedTnt tntEntity : tntEntities) {
			double distance = tntEntity.distanceToSqr(position);
			if (distance < closestDistance) {
				closestDistance = distance;
				nearestTNT = tntEntity;
			}
		}

		return nearestTNT;
	}

	@Inject(method = "wasExploded", at = @At("HEAD"), cancellable = true)
	public void wasExploded(ServerLevel world, BlockPos pos, Explosion explosion, CallbackInfo ci) {
		if (PandaTNTFusion.tntCount >= PandaTNTConfig.MaxTNTPrimed) {
			PrimedTnt nearestEntity = getNearestTNTEntity(world, pos.getCenter(), 5);
			if (nearestEntity != null) {
				TNTEntityAccess accessor = (TNTEntityAccess) nearestEntity;
				accessor.pandaTNTFusion$addPower();
			}
			ci.cancel();
		}
	}
}
