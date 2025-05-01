package me.TreeOfSelf.PandaTNTFusion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

public class PandaTNTFusion implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("panda-tnt-fusion");
	public static int tntCount = 0;
	private int tickCounter = 0;

	@Unique
	public void checkAndFuseTNT(ServerWorld world) {
		if(PandaTNTFusion.tntCount >= PandaTNTConfig.MaxTNTPrimed) {
			List<TntEntity> allTNT = (List<TntEntity>) world.getEntitiesByType(EntityType.TNT,
					entity -> entity.getBlockState().getBlock() == Blocks.TNT);

			Set<TntEntity> processed = new HashSet<>();
			List<TntEntity> toRemove = new ArrayList<>();

			for (TntEntity tnt : allTNT) {
				if (processed.contains(tnt)) continue;

				Box searchBox = new Box(
						tnt.getX() - 4, tnt.getY() - 4, tnt.getZ() - 4,
						tnt.getX() + 4, tnt.getY() + 4, tnt.getZ() + 4
				);

				List<TntEntity> nearbyTNT = world.getEntitiesByType(
						EntityType.TNT,
						searchBox,
						entity -> entity != tnt && !processed.contains(entity) &&
								entity.getBlockState().getBlock() == Blocks.TNT
				);

				if (!nearbyTNT.isEmpty()) {
					TntEntity keepTNT = tnt;
					for (TntEntity nearby : nearbyTNT) {
						if (nearby.getId() < keepTNT.getId()) {
							keepTNT = nearby;
						}
					}

					if (keepTNT == tnt) {
						toRemove.addAll(nearbyTNT);
						TNTEntityAccess accessor = (TNTEntityAccess) tnt;
						accessor.pandaTNTFusion$addPower();
					} else {
						toRemove.add(tnt);
						TNTEntityAccess accessor = (TNTEntityAccess) keepTNT;
						accessor.pandaTNTFusion$addPower();
					}

					processed.add(tnt);
					processed.addAll(nearbyTNT);
				}
			}

			for (TntEntity tnt : toRemove) {
				tnt.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}
	@Override
	public void onInitialize() {
		PandaTNTConfig.loadOrGenerateConfig();
		ServerEntityEvents.ENTITY_LOAD.register(this::onEntityLoad);
		ServerEntityEvents.ENTITY_UNLOAD.register(this::onEntityUnload);
		ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
		LOGGER.info("PandaTNTFusion Started!");
	}

	private void onServerTick(MinecraftServer minecraftServer) {
		tickCounter++;
		if (tickCounter >= 10) {
			checkAndFuseTNT(Objects.requireNonNull(minecraftServer.getWorld(ServerWorld.OVERWORLD)));
			checkAndFuseTNT(Objects.requireNonNull(minecraftServer.getWorld(ServerWorld.END)));
			checkAndFuseTNT(Objects.requireNonNull(minecraftServer.getWorld(ServerWorld.NETHER)));
			tickCounter = 0;
		}
	}

	private void onEntityUnload(Entity entity, ServerWorld serverWorld) {
		if (entity.getType() == EntityType.TNT) tntCount--;
	}

	private void onEntityLoad(Entity entity, ServerWorld serverWorld) {
		if (entity.getType() == EntityType.TNT) tntCount++;
	}

}
