package fr.graynaud.discord.graper.model;

import discord4j.discordjson.Id;
import discord4j.discordjson.json.ChannelMentionData;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.discordjson.json.UserWithMemberData;
import discord4j.discordjson.possible.Possible;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "messages", createIndex = false)
public class EsMessage {

    @org.springframework.data.annotation.Id
    @Field("id")
    private long id;

    @Field(name = "mention_everyone")
    private boolean mentionEveryone;

    @Field(name = "mention_roles_ids")
    private List<Long> mentionRolesIds;

    @Field(name = "nb_mentions_roles")
    private int nbMentionsRoles;

    @Field(name = "content")
    private String content;

    @Field(name = "content_length")
    private int contentLength;

    @Field(name = "tts")
    private boolean tts;

    @Field(name = "@timestamp")
    private long timestamp;

    @Field(name = "mentions_ids")
    private List<Long> mentionsIds;

    @Field(name = "nb_mentions")
    private int nbMentions;

    @Field(name = "message_reference_id")
    private Long messageReferenceId;

    @Field(name = "guild_id")
    private Long guildId;

    @Field(name = "mention_channels_ids")
    private List<Long> mentionChannelsIds;

    @Field(name = "nb_mentions_channels")
    private int nbMentionsChannels;

    @Field(name = "author_id")
    private long authorId;

    @Field(name = "channel_id")
    private long channelId;

    @Field(name = "reactions")
    private List<EsMessageReaction> reactions;

    @Field(name = "nb_reactions")
    private int nbReactions;

    @Field(name = "day")
    private int day;

    @Field(name = "hour_of_day")
    private int hourOfDay;

    public EsMessage() {
    }

    public EsMessage(MessageData message, long guildId) {
        Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(message.timestamp()));
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Europe/Paris"));
        this.id = message.id().asLong();
        this.mentionEveryone = message.mentionEveryone();
        this.mentionRolesIds = message.mentionRoles().stream().map(Long::parseLong).toList();
        this.nbMentionsRoles = this.mentionRolesIds.size();
        this.content = message.content();
        this.contentLength = this.content.length();
        this.tts = message.tts();
        this.timestamp = instant.toEpochMilli();
        this.mentionsIds = message.mentions().stream().map(UserWithMemberData::id).map(Id::asLong).toList();
        this.nbMentions = this.mentionsIds.size();
        this.messageReferenceId = message.messageReference()
                                         .toOptional()
                                         .map(MessageReferenceData::messageId)
                                         .flatMap(Possible::toOptional)
                                         .map(Id::asLong)
                                         .orElse(null);
        this.guildId = guildId;
        this.mentionChannelsIds = message.mentionChannels().toOptional().map(l -> l.stream().map(ChannelMentionData::id).map(Id::asLong).toList()).orElse(null);
        this.nbMentionsChannels = this.mentionChannelsIds == null ? 0 : this.mentionChannelsIds.size();
        this.authorId = message.author().id().asLong();
        this.channelId = message.channelId().asLong();
        this.day = zonedDateTime.getDayOfWeek().getValue();
        this.hourOfDay = zonedDateTime.getHour();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMentionEveryone() {
        return mentionEveryone;
    }

    public void setMentionEveryone(boolean mentionEveryone) {
        this.mentionEveryone = mentionEveryone;
    }

    public List<Long> getMentionRolesIds() {
        return mentionRolesIds;
    }

    public void setMentionRolesIds(List<Long> mentionRolesIds) {
        this.mentionRolesIds = mentionRolesIds;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isTts() {
        return tts;
    }

    public void setTts(boolean tts) {
        this.tts = tts;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Long> getMentionsIds() {
        return mentionsIds;
    }

    public void setMentionsIds(List<Long> mentionsIds) {
        this.mentionsIds = mentionsIds;
    }

    public Long getMessageReferenceId() {
        return messageReferenceId;
    }

    public void setMessageReferenceId(Long messageReferenceId) {
        this.messageReferenceId = messageReferenceId;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public List<Long> getMentionChannelsIds() {
        return mentionChannelsIds;
    }

    public void setMentionChannelsIds(List<Long> mentionChannelsIds) {
        this.mentionChannelsIds = mentionChannelsIds;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public List<EsMessageReaction> getReactions() {
        return reactions;
    }

    public boolean addReaction(EsMessageReaction reaction) {
        if (this.reactions == null) {
            this.reactions = new ArrayList<>();
        }

        return this.reactions.add(reaction);
    }

    public void setReactions(List<EsMessageReaction> reactions) {
        this.reactions = reactions;
    }

    public int getNbMentionsRoles() {
        return nbMentionsRoles;
    }

    public void setNbMentionsRoles(int nbMentionsRoles) {
        this.nbMentionsRoles = nbMentionsRoles;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public int getNbMentions() {
        return nbMentions;
    }

    public void setNbMentions(int nbMentions) {
        this.nbMentions = nbMentions;
    }

    public int getNbMentionsChannels() {
        return nbMentionsChannels;
    }

    public void setNbMentionsChannels(int nbMentionsChannels) {
        this.nbMentionsChannels = nbMentionsChannels;
    }

    public int getNbReactions() {
        return nbReactions;
    }

    public void setNbReactions(int nbReactions) {
        this.nbReactions = nbReactions;
    }


}
