package me.sebastian420.PandaTNTQueue;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaTNTQueue implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("panda-tnt-queue");

	@Override
	public void onInitialize() {
		PandaTNTConfig.loadOrGenerateConfig();
		LOGGER.info("PandaTNTQueue Started!");
	}
	
}