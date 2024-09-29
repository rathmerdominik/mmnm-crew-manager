package net.hammerclock.mmnmcrewmanager.events;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.storage.linking.LinkManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import net.hammerclock.mmnmcrewmanager.CrewManager;
import net.hammerclock.mmnmcrewmanager.config.CommonConfig;
import net.hammerclock.mmnmcrewmanager.world.CrewDisplayWorldData;

import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import xyz.pixelatedw.mineminenomi.api.crew.Crew;
import xyz.pixelatedw.mineminenomi.api.crew.JollyRoger;
import xyz.pixelatedw.mineminenomi.api.crew.Crew.Member;
import xyz.pixelatedw.mineminenomi.api.events.CrewEvent;
import xyz.pixelatedw.mineminenomi.api.events.JollyRogerEvent;
import xyz.pixelatedw.mineminenomi.data.world.ExtendedWorldData;

public class CrewDisplayEvent {
	public static final Logger LOGGER = LogManager.getLogger(CrewManager.PROJECT_ID);
	public static final CommonConfig CONFIG = CommonConfig.INSTANCE;
	public static final String FANCY_DECORATION = "**════════════**";

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onServerStartedEvent(FMLServerStartedEvent event) {
		Guild guild = DiscordIntegration.INSTANCE.getChannel().getGuild();
		ForumChannel channel = guild.getForumChannelById(CONFIG.getCrewForumChannelId());
		CrewDisplayWorldData displayWorldData = CrewDisplayWorldData.get().orElseThrow(IllegalArgumentException::new);
		
		// Cleanup for Thread channels that have no crew to their name
		if (CONFIG.getCrewForumChannelId() != 0L) {
			for (ThreadChannel threadChannel : channel.getThreadChannels()) {
				if (!displayWorldData.getCrewThread(threadChannel.getName()).isPresent()) {
					threadChannel.delete().queue();
				}
			}
		}

		if (ExtendedWorldData.get() != null) {
			for (Crew crew : ExtendedWorldData.get().getCrews()) {
				if (crew.getMembers().isEmpty()) {
					ExtendedWorldData.get().removeCrew(crew);
					this.createOrUpdateCrew(crew, null, true, null, null);
					continue;
				}
				createOrUpdateCrew(crew, null, null, null, null);
			}
		} else {
			LOGGER.error("ExtendedWorldData is null!");
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onJollyRogerUpdate(JollyRogerEvent.Update event) {
		this.createOrUpdateCrew(event.getCrew(), event.getJollyRoger(), null, null, null);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onCrewCreated(CrewEvent.Create event) {
		if(event.isCanceled()) {
			return;
		}
		this.createOrUpdateCrew(event.getCrew(), null, null, null, null);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onCrewJoined(CrewEvent.Join event) {
		if(event.isCanceled()) {
			return;
		}
		if (event.getCrew().hasMember(event.getPlayer().getUUID())) {
			return;
		}
		this.createOrUpdateCrew(event.getCrew(), null, null, event.getPlayer(), null);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onCrewLeave(CrewEvent.Leave event) {
		if(event.isCanceled()) {
			return;
		}
		if (event.getCrew().getMembers().size() - 1 <= 0) {
			this.createOrUpdateCrew(event.getCrew(), null, true, null, null);
		} else {
			this.createOrUpdateCrew(event.getCrew(), null, null, null, event.getPlayer());
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onCrewKick(CrewEvent.Kick event) {
		if(event.isCanceled()) {
			return;
		}
		this.createOrUpdateCrew(event.getCrew(), null, null, null, event.getPlayer());
	}

	/**
	 * Create a new crew entry in the Discord forum channel or update an existing one
	 * 
	 * @param crew				The crew to update/create
	 * @param jollyRoger		The jolly roger of the crew
	 * @param deleteChannel		Delete channel if true is given
	 * @param playerToJoin		The player that intends to join the crew
	 * @param playerToLeave		The player that intends to leave the crew
	 */
	public void createOrUpdateCrew(Crew crew, @Nullable JollyRoger jollyRoger, @Nullable Boolean deleteChannel, @Nullable PlayerEntity playerToJoin, @Nullable PlayerEntity playerToLeave) {
		Guild guild = DiscordIntegration.INSTANCE.getChannel().getGuild();
		Long existingCrewThread = 0L;
		List<Member> members = crew.getMembers();
		Member crewCaptain = crew.getCaptain();
		Optional<CrewDisplayWorldData> crewWorldData = CrewDisplayWorldData.get();
		if(!crewWorldData.isPresent()) return;

		if (playerToJoin != null) {
			crew.addMember(playerToJoin);
		}

		if (playerToLeave != null) {
			crew.removeMember(playerToLeave.getUUID());
		}

		if (CONFIG.getCrewForumChannelId() != 0L) {
			ForumChannel channel = guild.getForumChannelById(CONFIG.getCrewForumChannelId());
			Optional<Long> crewThreadId = crewWorldData.get().getCrewThread(crew.getName());
			if (crewThreadId.isPresent()) {
				existingCrewThread = crewThreadId.get();
				if (deleteChannel != null || members.isEmpty()) {
					guild.getThreadChannelById(existingCrewThread).delete().queue();
					this.reAddOrDeletePlayer(crew, playerToJoin, playerToLeave);
					crewWorldData.get().removeCrewThread(crew.getName());
					return;
				}
			}

			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setTitle(crew.getName());
			embedBuilder.addField("Discord Name", "", true);
			embedBuilder.addBlankField(true);
			embedBuilder.addField("Minecraft Name", "", true);

			if (CONFIG.showCaptain() && crewCaptain != null) {
				embedBuilder.addField(FANCY_DECORATION, "", true);
				embedBuilder.addField("**Captain**", "", true);
				embedBuilder.addField(FANCY_DECORATION, "", true);

				embedBuilder.addField("",getDiscordLinkOrNotLinked(crewCaptain), true);
				embedBuilder.addBlankField(true);
				embedBuilder.addField("",crewCaptain.getUsername(), true);
			}

			if (CONFIG.syncCrewMembers()) {
				embedBuilder = this.addCrewMembersToEmbed(embedBuilder, members, crewCaptain);
			}

			if (CONFIG.showCreationDate()) {
				embedBuilder.setFooter("Creation Date");				
				embedBuilder.setTimestamp(Instant.ofEpochSecond(crew.getCreationDate()));
			}

			MessageCreateBuilder messageBuilder = new MessageCreateBuilder();

			if (CONFIG.syncCrewBanner() && crew.getJollyRoger().getAsBufferedImage().isPresent()) {
				if (jollyRoger == null) {
					jollyRoger = crew.getJollyRoger();
				}

				messageBuilder = this.addJollyRogerToMessageBuilder(messageBuilder, jollyRoger);
				embedBuilder.setThumbnail("attachment://jollyRoger.png");
			}
			MessageCreateBuilder finalMessageBuilder = messageBuilder.setEmbeds(embedBuilder.build());

			if (existingCrewThread != 0L && guild.getThreadChannelById(existingCrewThread) != null) {
				ThreadChannel threadChannel = guild.getThreadChannelById(existingCrewThread);
				Message startMessage = threadChannel.retrieveStartMessage().complete();

				startMessage.editMessage(MessageEditData.fromCreateData(finalMessageBuilder.build())).queue();

				this.reAddOrDeletePlayer(crew, playerToJoin, playerToLeave);
				return;
			}
			channel.createForumPost(crew.getName(), finalMessageBuilder.build()).queue(result -> crewWorldData.get().addCrewThread(crew.getName(), result.getThreadChannel().getIdLong()));
			this.reAddOrDeletePlayer(crew, playerToJoin, playerToLeave);
		}
	}

	/**
	 * Because the crew events are called before the actions are actually happening i had to add/remove players from the crew first to work with it.
	 * This function restores the original state
	 * 
	 * @param crew				The crew from which to restore the original state from
	 * @param playerToJoin		The player to re-add to the crew
	 * @param playerToLeave		The player to remove from the crew
	 */
	private void reAddOrDeletePlayer(Crew crew, @Nullable PlayerEntity playerToJoin, @Nullable PlayerEntity playerToLeave) {
		if (playerToJoin != null) {
			crew.removeMember(playerToJoin.getUUID());
		}

		if (playerToLeave != null) {
			crew.addMember(playerToLeave);
		}
	}

	/**
	 * Adds crew members to the embed with Discord Name and Minecraft name.
	 * 
	 * @param embedBuilder
	 * @param members
	 * @param captain
	 * @return
	 */
	private EmbedBuilder addCrewMembersToEmbed(EmbedBuilder embedBuilder, List<Member> members, Member captain) {
		List<String> discordUsers = new ArrayList<>();
		List<String> minecraftNames = new ArrayList<>();

		embedBuilder.addField(FANCY_DECORATION, "", true);
		embedBuilder.addField("**Members**", "", true);
		embedBuilder.addField(FANCY_DECORATION, "", true);

		for (Member crewMember : members) {
			if (CONFIG.showCaptain() && crewMember.getUsername().equals(captain.getUsername())) {
				continue;
			}
			discordUsers.add(getDiscordLinkOrNotLinked(crewMember));
			minecraftNames.add(crewMember.getUsername());
		}

		embedBuilder.addField("",String.join("\n", discordUsers), true);
		embedBuilder.addBlankField(true);
		embedBuilder.addField("", String.join("\n", minecraftNames), true);

		return embedBuilder;
	}

	/**
	 * Uploads the Jolly Roger of the crew as an attachment of the message builder
	 * 
	 * @param messageBuilder
	 * @param jollyRoger
	 * @return
	 */
	private MessageCreateBuilder addJollyRogerToMessageBuilder(MessageCreateBuilder messageBuilder, JollyRoger jollyRoger) {
		BufferedImage jollyRogerImage = jollyRoger.getAsBufferedImage().orElseThrow(NullPointerException::new);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(jollyRogerImage, "png", os);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		InputStream is = new ByteArrayInputStream(os.toByteArray());

		return messageBuilder.setFiles(FileUpload.fromData(is, "jollyRoger.png"));
	}

	/**
	 * Either returns "NO DISCORD LINK" or the mention of the found Discord User
	 * 
	 * @param member
	 * @return
	 */
	private String getDiscordLinkOrNotLinked(Member member) {
		return LinkManager.isPlayerLinked(member.getUUID())
				? DiscordIntegration.INSTANCE.getMemberById(
						LinkManager.getLink(null, member.getUUID()).discordID)
						.getAsMention()
				: "NO DISCORD LINK";
	}
}