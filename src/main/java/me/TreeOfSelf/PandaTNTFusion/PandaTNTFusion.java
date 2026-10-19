package me.TreeOfSelf.PandaTNTFusion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaTNTFusion implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("panda-tnt-fusion");
	public static int tntCount = 0;

	@Override
	public void onInitialize() {
		PandaTNTConfig.loadOrGenerateConfig();
		ServerEntityEvents.ENTITY_LOAD.register(this::onEntityLoad);
		ServerEntityEvents.ENTITY_UNLOAD.register(this::onEntityUnload);
		ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
		LOGGER.info("PandaTNTFusion Started!");
	}

	// Recount every 5 s so a missed load/unload event can't leave the limit stuck (no TNT ever exploding again)
	private void onServerTick(MinecraftServer server) {
		if (server.getTickCount() % 100 != 0) return;
		int count = 0;
		for (ServerLevel level : server.getAllLevels()) {
			count += level.getEntities(EntityTypes.TNT, entity -> true).size();
		}
		tntCount = count;
	}

	private void onEntityUnload(Entity entity, ServerLevel serverLevel) {
		if (entity.getType() == EntityTypes.TNT) tntCount--;
	}

	private void onEntityLoad(Entity entity, ServerLevel serverLevel) {
		if (entity.getType() == EntityTypes.TNT) tntCount++;
	}
}
