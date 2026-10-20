package me.TreeOfSelf.PandaTNTFusion.mixin;

import me.TreeOfSelf.PandaTNTFusion.PandaTNTConfig;
import me.TreeOfSelf.PandaTNTFusion.PandaTNTFusion;
import me.TreeOfSelf.PandaTNTFusion.TNTEntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
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
		List<PrimedTnt> tntEntities = world.getEntities(EntityTypes.TNT, searchBox, entity -> entity.getBlockState().is(Blocks.TNT));

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

	// Explosions break their blocks one after another, so one cached answer covers all the TNT one blast hits
	@Unique
	private static Explosion lastExplosion;
	@Unique
	private static boolean lastExplosionBig;

	@Unique
	private static boolean isBigBlast(ServerLevel world, Explosion explosion) {
		if (explosion == lastExplosion) return lastExplosionBig;
		boolean big;
		if (explosion.getDirectSourceEntity() instanceof TNTEntityAccess tnt && tnt.pandaTNTFusion$getPower() > 4.0F) {
			big = true;
		} else {
			double r = PandaTNTConfig.BigBlastRadius;
			AABB box = new AABB(explosion.center().subtract(r, r, r), explosion.center().add(r, r, r));
			big = world.getEntities(EntityTypes.TNT, box, entity -> true).size() >= PandaTNTConfig.BigBlastTNT;
		}
		lastExplosion = explosion;
		lastExplosionBig = big;
		return big;
	}

	@Inject(method = "wasExploded", at = @At("HEAD"), cancellable = true)
	public void wasExploded(ServerLevel world, BlockPos pos, Explosion explosion, CallbackInfo ci) {
		if (PandaTNTFusion.tntCount < PandaTNTConfig.MaxTNTPrimed) return;
		// Over the limit, small blasts still chain normally; only big ones (and everything past the hard limit) get cut down
		if (PandaTNTFusion.tntCount < PandaTNTConfig.HardMaxTNTPrimed && !isBigBlast(world, explosion)) return;
		PrimedTnt nearestEntity = getNearestTNTEntity(world, Vec3.atCenterOf(pos), 5);
		if (nearestEntity != null) {
			((TNTEntityAccess) nearestEntity).pandaTNTFusion$addPower();
		}
		ci.cancel();
	}
}
