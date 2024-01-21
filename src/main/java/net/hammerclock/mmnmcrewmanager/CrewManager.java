package net.hammerclock.mmnmcrewmanager;

import net.hammerclock.mmnmcrewmanager.config.CommonConfig;
import net.hammerclock.mmnmcrewmanager.events.CrewDisplayEvent;
import net.hammerclock.mmnmcrewmanager.events.CrewManagerEvent;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CrewManager.PROJECT_ID)
public class CrewManager {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String PROJECT_ID = "mmnmcrewmanager";
	public static final String CONFIG_NAME = "mmnm-crewmanager-common.toml";

	public CrewManager() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
				() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		ModLoadingContext context = ModLoadingContext.get();

		context.registerConfig(Type.COMMON, CommonConfig.CONFIG, CONFIG_NAME);

		MinecraftForge.EVENT_BUS.addListener(CrewManager::onServerStarting);
		MinecraftForge.EVENT_BUS.register(new CrewManagerEvent());
	}

	private static void onServerStarting(FMLServerStartingEvent event) {
		CheckResult result = VersionChecker.getResult(ModList.get().getModContainerById(PROJECT_ID).orElseThrow(IllegalArgumentException::new).getModInfo());
		if(result.status == Status.OUTDATED) {
			LOGGER.warn("Your mod is outdated.\nThe latest version is {}. Please get the latest version here: {}", result.target, result.url);
		}
		if(ModList.get().isLoaded("dcintegration")) {
			MinecraftForge.EVENT_BUS.register(new CrewDisplayEvent());
		}
	}
}
