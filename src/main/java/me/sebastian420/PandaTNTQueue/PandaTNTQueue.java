package me.sebastian420.PandaTNTQueue;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PandaTNTQueue implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("panda-tnt-queue");
	public static int tntCount = 0;
	private static final List<TNTQueueData> TNTQueue = new ArrayList<>();

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

	private static boolean queueContainsPos(BlockPos pos) {
		return TNTQueue.stream().anyMatch(data -> data.pos.equals(pos));
	}

	private void onServerTick(MinecraftServer server) {
		int tntProcessed = 0;
		/*AtomicInteger primedTNT = new AtomicInteger();

		server.getWorlds().forEach(world -> {
			primedTNT.addAndGet(world.getEntitiesByType(EntityType.TNT, entity -> true).size());
		});*/

		while (!TNTQueue.isEmpty() && tntCount < PandaTNTConfig.MaxTNTPrimed && tntProcessed < PandaTNTConfig.MaxTNTPrimed) {
			TNTQueueData tntData = TNTQueue.removeFirst();
			if (tntData.world.getBlockState(tntData.pos).getBlock() == Blocks.TNT) {
				tntData.world.getBlockState(tntData.pos).onExploded(tntData.world, tntData.pos, tntData.explosion, (stack, pos) -> {});
			}
			tntProcessed++;
		}
	}

	private record TNTQueueData(ServerWorld world, BlockPos pos, Explosion explosion) {
	}
}