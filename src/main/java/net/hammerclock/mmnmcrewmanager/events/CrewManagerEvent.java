package net.hammerclock.mmnmcrewmanager.events;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.hammerclock.mmnmcrewmanager.CrewManager;
import net.hammerclock.mmnmcrewmanager.config.CommonConfig;
import net.hammerclock.mmnmcrewmanager.world.CrewDisplayWorldData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import xyz.pixelatedw.mineminenomi.api.crew.Crew;
import xyz.pixelatedw.mineminenomi.api.events.CrewEvent;
import xyz.pixelatedw.mineminenomi.data.world.ExtendedWorldData;

public class CrewManagerEvent {
	public static final Logger LOGGER = LogManager.getLogger(CrewManager.PROJECT_ID);

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerStartedEvent(FMLServerStartedEvent event) {
        LOGGER.info("Crew Manager Started!");
        if(CommonConfig.INSTANCE.dissolveExistingCrews() && CommonConfig.INSTANCE.getCrewSizeLimit() != -1) {
            ExtendedWorldData worldData = ExtendedWorldData.get();

            for(Crew crew : worldData.getCrews()) {
                if(crew.getMembers().size() > CommonConfig.INSTANCE.getCrewSizeLimit()) {
                    LOGGER.debug("Found existing crew {} that goes over the limit. Removing...", crew.getName());
                    worldData.removeCrew(crew);
                    LOGGER.debug("Deleted crew from mmnm world data. Also trying to delete from our world data.");
                    CrewDisplayWorldData.get().orElseThrow(IllegalArgumentException::new).removeCrewThread(crew.getName());
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onCrewJoinedEvent(CrewEvent.Join event) {
        if(event.getPlayer() instanceof ServerPlayerEntity &&  
            CommonConfig.INSTANCE.getCrewSizeLimit() != -1 && 
            event.getCrew().getMembers().size() + 1 > CommonConfig.INSTANCE.getCrewSizeLimit()
        ) {
            LOGGER.debug("Player tried to join crew with member count {} with maximum allowed members {}. Denying request!", event.getCrew().getMembers().size(), CommonConfig.INSTANCE.getCrewSizeLimit());
            event.getPlayer().sendMessage(new StringTextComponent("You cannot join this crew as it is already full!"), event.getPlayer().getUUID());
            event.setCanceled(true);
        }      
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onCrewCreatedEvent(CrewEvent.Create event) {
        if(event.getPlayer() instanceof ServerPlayerEntity && CommonConfig.INSTANCE.getCrewSizeLimit() == 0) {
            event.getPlayer().sendMessage(new StringTextComponent("Crews are disabled on this server!"), event.getPlayer().getUUID());
            event.setCanceled(true);
        }
    }
}
