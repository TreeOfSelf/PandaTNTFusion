package me.sebastian420.PandaTNTQueue.mixin;

import me.sebastian420.PandaTNTQueue.PandaTNTQueue;
import me.sebastian420.PandaTNTQueue.TNTEntityAccess;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TntBlock.class)
public class TNTBlockQueue {

    @Unique
    public TntEntity getNearestTNTEntity(ServerWorld world, Vec3d position, double radius) {
        Box searchBox = new Box(position.subtract(radius, radius, radius), position.add(radius, radius, radius));
        List<TntEntity> tntEntities = world.getEntitiesByType(EntityType.TNT, searchBox, entity -> true);

        TntEntity nearestTNT = null;
        double closestDistance = Double.MAX_VALUE;

        for (TntEntity tntEntity : tntEntities) {
            double distance = tntEntity.squaredDistanceTo(position);
            if (distance < closestDistance) {
                closestDistance = distance;
                nearestTNT = tntEntity;
            }
        }

        return nearestTNT;
    }

    @Inject(method = "onDestroyedByExplosion", at = @At(value = "HEAD"), cancellable = true)
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion, CallbackInfo ci) {
        if(PandaTNTQueue.tntCount >= PandaTNTQueue.maxTntAdjusted) {
            System.out.println("Adjusted: "+PandaTNTQueue.tntCount);
            System.out.println("Adjusted: "+PandaTNTQueue.maxTntAdjusted);

            TntEntity nearestEntity = getNearestTNTEntity((ServerWorld) world, pos.toCenterPos(), 20);
            if (nearestEntity != null) {
                TNTEntityAccess accessor = (TNTEntityAccess) nearestEntity;
                accessor.pandaTNTQueue$addPower();
                ci.cancel();
                //PandaTNTQueue.addExplosionQueue((ServerWorld) world, pos, explosion);
            }
        }
    }
}