package me.sebastian420.PandaTNTQueue;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PandaTNTQueue implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("panda-tnt-queue");
	public static int tntCount = 0;
	public static int maxTntAdjusted = PandaTNTConfig.MaxTNTPrimed;

	private static final List<TNTQueueData> TNTQueue = new ArrayList<>();
	private static final List<TNTExplosionQueueData> TNTExplosionQueue = new ArrayList<>();

	@Override
	public void onInitialize() {
		PandaTNTConfig.loadOrGenerateConfig();
		ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
		ServerEntityEvents.ENTITY_LOAD.register(this::onEntityLoad);
		ServerEntityEvents.ENTITY_UNLOAD.register(this::onEntityUnload);
		LOGGER.info("PandaTNTQueue Started!");
	}

	private void onEntityUnload(Entity entity, ServerWorld serverWorld) {
		if (entity.getType() == EntityType.TNT) tntCount--;
	}

	private void onEntityLoad(Entity entity, ServerWorld serverWorld) {
		if (entity.getType() == EntityType.TNT) tntCount++;;
	}

	public static void addQueue(ServerWorld world, BlockPos pos, Explosion explosion) {
		if (!queueContainsPos(pos))
			TNTQueue.add(new TNTQueueData(world, pos, explosion));
	}

	public static void addExplosionQueue(ServerWorld world, BlockPos pos, Explosion explosion) {
		//if (!queueExplosionContainsPos(pos))
			TNTExplosionQueue.add(new TNTExplosionQueueData(world, pos, explosion));
	}

	private static boolean queueContainsPos(BlockPos pos) {
		return TNTQueue.stream().anyMatch(data -> data.pos.equals(pos));
	}

	private static boolean queueExplosionContainsPos(BlockPos pos) {
		return TNTExplosionQueue.stream().anyMatch(data -> data.pos.equals(pos));
	}

	private void onServerTick(MinecraftServer server) {
		int tntProcessed = 0;

		/*maxTntAdjusted = (int) (PandaTNTConfig.MaxTNTPrimed * 30 / server.getAverageTickTime());

		if (maxTntAdjusted > PandaTNTConfig.MaxTNTPrimed) maxTntAdjusted = PandaTNTConfig.MaxTNTPrimed;

		System.out.println("Count: "+PandaTNTQueue.tntCount);
		System.out.println("Adjusted: "+PandaTNTQueue.maxTntAdjusted);
		/*

		while (!TNTExplosionQueue.isEmpty() && tntCount < maxTntAdjusted && tntProcessed < maxTntAdjusted) {
			TNTExplosionQueueData tntData = TNTExplosionQueue.removeFirst();
			TntEntity tntEntity = new TntEntity(tntData.world, (double)tntData.pos.getX() + 0.5, (double)tntData.pos.getY(), (double)tntData.pos.getZ() + 0.5, tntData.explosion.getCausingEntity());
			int i = tntEntity.getFuse();
			tntEntity.setFuse((short)(tntData.world.random.nextInt(i / 4) + i / 8));
			tntData.world.spawnEntity(tntEntity);
			tntProcessed++;

		}

		while (!TNTQueue.isEmpty() && tntCount < maxTntAdjusted && tntProcessed < maxTntAdjusted) {
			TNTQueueData tntData = TNTQueue.removeFirst();
			if (tntData.world.getBlockState(tntData.pos).getBlock() == Blocks.TNT) {
				tntData.world.getBlockState(tntData.pos).onExploded(tntData.world, tntData.pos, tntData.explosion, (stack, pos) -> {});
			}
			tntProcessed++;
		}*/

	}

	private record TNTQueueData(ServerWorld world, BlockPos pos, Explosion explosion) {}
	private record TNTExplosionQueueData(ServerWorld world, BlockPos pos, Explosion explosion) {}
}