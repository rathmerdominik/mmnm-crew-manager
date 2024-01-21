package net.hammerclock.mmnmcrewmanager.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.hammerclock.mmnmcrewmanager.CrewManager;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	public static final Path CONFIG_PATH = Paths.get("config", CrewManager.CONFIG_NAME);
	public static final CommonConfig INSTANCE;
	public static final ForgeConfigSpec CONFIG;

	// Display settings
	private ForgeConfigSpec.LongValue crewForumChannelId;
	private ForgeConfigSpec.BooleanValue syncCrewMembers;
	private ForgeConfigSpec.BooleanValue syncCrewBanner;
	private ForgeConfigSpec.BooleanValue showCaptain;
	private ForgeConfigSpec.BooleanValue showCreationDate;

	// Manager settings
	private ForgeConfigSpec.IntValue crewSizeLimit;
	private ForgeConfigSpec.BooleanValue dissolveExistingCrews;

	static {
		Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

		CONFIG = pair.getRight();
		INSTANCE = pair.getLeft();

		CommentedFileConfig file = CommentedFileConfig
									.builder(CONFIG_PATH)
									.sync()
									.autoreload()
									.writingMode(WritingMode.REPLACE)
									.build();

		file.load();
		file.save();

		CONFIG.setConfig(file);
	}

	public CommonConfig(ForgeConfigSpec.Builder builder) {
		builder.push("Crew Display");
		this.crewForumChannelId = builder
				.comment("Requires the Discord Integration Mod to be fully set up!")
				.comment("Has to be a Forum channel! Therefore your server MUST be a community server if you want to use this feature!")
				.defineInRange("Crew Forum Channel Id", 0L, 0L, Long.MAX_VALUE);
		this.syncCrewBanner = builder.define("Sync Crew Banner", true);
		this.syncCrewMembers = builder.define("Sync Crew Members", true);
		this.showCaptain = builder.define("Show Captain of the crew", true);
		this.showCreationDate = builder.define("Show Crew Creation Date", true);
		builder.pop();

		builder.push("Crew Manager");
		this.crewSizeLimit = builder
				.comment("Leave it as -1 if you do not want any crew size limits. Crew size includes the captain! So 4 Members and 1 Captain would need a crew size of 5 to exist.\n Setting this to 0 will disable crews on your server.")
				.defineInRange("Limit crew size", -1, -1, Integer.MAX_VALUE);
		this.dissolveExistingCrews = builder
				.comment("If set to true existing crews that are over the crew limit will be dissolved and removed")
				.define("Dissolve existing crews", false);
		builder.pop();
	}

	// Crew Display getters

	public long getCrewForumChannelId() {
		return this.crewForumChannelId.get();
	}

	public boolean syncCrewMembers() {
		return this.syncCrewMembers.get();
	}

	public boolean syncCrewBanner() {
		return this.syncCrewBanner.get();
	}

	public boolean showCaptain() {
		return this.showCaptain.get();
	}

	public boolean showCreationDate() {
		return this.showCreationDate.get();
	}

	// Crew Manager getters

	public int getCrewSizeLimit() {
		return this.crewSizeLimit.get();
	}

	public boolean dissolveExistingCrews() {
		return this.dissolveExistingCrews.get();
	}
}