package net.hammerclock.mmnmcrewmanager.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.hammerclock.mmnmcrewmanager.CrewManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CrewDisplayWorldData extends WorldSavedData{
	public static final Logger LOGGER = LogManager.getLogger(CrewManager.PROJECT_ID);
    private static final String IDENTIFIER = CrewManager.PROJECT_ID + "-crew-display";
    
    private Map<String, Long> crewThreadMapping = new HashMap<>();

	public static Optional<CrewDisplayWorldData> get() {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			return Optional.of(ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage().computeIfAbsent(CrewDisplayWorldData::new, IDENTIFIER));
		}
		return Optional.empty();
	}

    public CrewDisplayWorldData(String identifier)
	{
		super(identifier);
	}

    public CrewDisplayWorldData() {
		this(IDENTIFIER);
	}

    public void load(CompoundNBT nbt) {
        CompoundNBT crewThreadMappingNBT = nbt.getCompound("crewThreadMappingNBT");

		this.crewThreadMapping.clear();
		crewThreadMappingNBT.getAllKeys().stream().forEach(x -> 
                this.crewThreadMapping.put(x, crewThreadMappingNBT.getLong(x))
            );
    }

    public CompoundNBT save(CompoundNBT nbt) {        
        CompoundNBT crewThreadMappingNBT = new CompoundNBT();
		if (this.crewThreadMapping.size() > 0) {
			this.crewThreadMapping.entrySet().stream().forEach(x -> crewThreadMappingNBT.putLong(x.getKey(), x.getValue()));
		}
		nbt.put("crewThreadMappingNBT", crewThreadMappingNBT);

        return nbt;
    }

    public void addCrewThread(String crewName, long threadId) {
        LOGGER.debug("Adding {} thread id to crew: {}", threadId, crewName );

        if (this.crewThreadMapping.containsKey(crewName)) {
            this.crewThreadMapping.remove(crewName);
        }

        this.crewThreadMapping.put(crewName, threadId);
        LOGGER.debug("Now have {} in crewThreadMapping", this.crewThreadMapping);
        this.setDirty();
    }

    public Optional<Long> getCrewThread(String crewName) {
        LOGGER.debug("Retrieving thread id for crew name {}", crewName);
        LOGGER.debug("Currently have {} in crewThreadMapping", this.crewThreadMapping);

        if (!this.crewThreadMapping.containsKey(crewName)) {
            LOGGER.debug("No crew thread found");
            return Optional.empty();
        }

        return Optional.of(this.crewThreadMapping.get(crewName));
    }

    public Collection<Long> getCrewThreads() {
        LOGGER.debug("Retrieving all thread ids for all crews. Current state is: {}", this.crewThreadMapping);

        return this.crewThreadMapping.values();
    }

    public void removeCrewThread(String crewName) {
        if (this.crewThreadMapping.containsKey(crewName)) {
            LOGGER.debug("Removing crew: {} from crewThreadMapping", crewName);
            this.crewThreadMapping.remove(crewName);
            LOGGER.debug("Now have {} in crewThreadMapping", this.crewThreadMapping);
        }
        this.setDirty();
    }
}
