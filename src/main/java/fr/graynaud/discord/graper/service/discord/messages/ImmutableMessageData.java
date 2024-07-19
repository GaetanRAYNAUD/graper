package fr.graynaud.discord.graper.service.discord.messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import discord4j.discordjson.Id;
import discord4j.discordjson.json.AttachmentData;
import discord4j.discordjson.json.ChannelMentionData;
import discord4j.discordjson.json.ComponentData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.MessageActivityData;
import discord4j.discordjson.json.MessageApplicationData;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.MessageInteractionData;
import discord4j.discordjson.json.MessageReferenceData;
import discord4j.discordjson.json.PartialMemberData;
import discord4j.discordjson.json.PartialStickerData;
import discord4j.discordjson.json.ReactionData;
import discord4j.discordjson.json.StickerData;
import discord4j.discordjson.json.UserData;
import discord4j.discordjson.json.UserWithMemberData;
import discord4j.discordjson.possible.Possible;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class ImmutableMessageData implements MessageData {
    private long id_value;
    private long channelId_value;
    private long guildId_value;
    private boolean guildId_absent;
    private UserData author;
    private PartialMemberData member_value;
    private boolean member_absent;
    private String content;
    private String timestamp;
    private String editedTimestamp;
    private boolean tts;
    private boolean mentionEveryone;
    private List<UserWithMemberData> mentions;
    private List<String> mentionRoles;
    private List<ChannelMentionData> mentionChannels_value;
    private boolean mentionChannels_absent;
    private List<AttachmentData> attachments;
    private List<EmbedData> embeds;
    private List<ReactionData> reactions_value;
    private boolean reactions_absent;
    private Object nonce_value;
    private boolean nonce_absent;
    private boolean pinned;
    private long webhookId_value;
    private boolean webhookId_absent;
    private int type;
    private MessageActivityData activity_value;
    private boolean activity_absent;
    private MessageApplicationData application_value;
    private boolean application_absent;
    private long applicationId_value;
    private boolean applicationId_absent;
    private MessageReferenceData messageReference_value;
    private boolean messageReference_absent;
    private Integer flags_value;
    private boolean flags_absent;
    private List<StickerData> stickers_value;
    private boolean stickers_absent;
    private List<PartialStickerData> stickerItems_value;
    private boolean stickerItems_absent;
    private MessageData referencedMessage_value;
    private boolean referencedMessage_absent;
    private MessageInteractionData interaction_value;
    private boolean interaction_absent;
    private List<ComponentData> components_value;
    private boolean components_absent;
    private static final byte STAGE_INITIALIZING = -1;
    private static final byte STAGE_UNINITIALIZED = 0;
    private static final byte STAGE_INITIALIZED = 1;

    private ImmutableMessageData(discord4j.discordjson.json.ImmutableMessageData original, Id id, Id channelId, Possible<Id> guildId, UserData author,
                                 Possible<PartialMemberData> member, String content, String timestamp, String editedTimestamp, boolean tts,
                                 boolean mentionEveryone, List<UserWithMemberData> mentions, List<String> mentionRoles,
                                 Possible<List<ChannelMentionData>> mentionChannels, List<AttachmentData> attachments, List<EmbedData> embeds,
                                 Possible<List<ReactionData>> reactions, Possible<Object> nonce, boolean pinned, Possible<Id> webhookId, int type,
                                 Possible<MessageActivityData> activity, Possible<MessageApplicationData> application, Possible<Id> applicationId,
                                 Possible<MessageReferenceData> messageReference, Possible<Integer> flags, Possible<List<StickerData>> stickers,
                                 Possible<List<PartialStickerData>> stickerItems, Possible<Optional<MessageData>> referencedMessage,
                                 Possible<MessageInteractionData> interaction, Possible<List<ComponentData>> components) {
        Id id$impl = id;
        Id channelId$impl = channelId;
        Possible<Id> guildId$impl = guildId;
        this.author = author;
        Possible<PartialMemberData> member$impl = member;
        this.content = content;
        this.timestamp = timestamp;
        this.editedTimestamp = editedTimestamp;
        this.tts = tts;
        this.mentionEveryone = mentionEveryone;
        this.mentions = mentions;
        this.mentionRoles = mentionRoles;
        Possible<List<ChannelMentionData>> mentionChannels$impl = mentionChannels;
        this.attachments = attachments;
        this.embeds = embeds;
        Possible<List<ReactionData>> reactions$impl = reactions;
        Possible<Object> nonce$impl = nonce;
        this.pinned = pinned;
        Possible<Id> webhookId$impl = webhookId;
        this.type = type;
        Possible<MessageActivityData> activity$impl = activity;
        Possible<MessageApplicationData> application$impl = application;
        Possible<Id> applicationId$impl = applicationId;
        Possible<MessageReferenceData> messageReference$impl = messageReference;
        Possible<Integer> flags$impl = flags;
        Possible<List<StickerData>> stickers$impl = stickers;
        Possible<List<PartialStickerData>> stickerItems$impl = stickerItems;
        Possible<Optional<MessageData>> referencedMessage$impl = referencedMessage;
        Possible<MessageInteractionData> interaction$impl = interaction;
        Possible<List<ComponentData>> components$impl = components;
        this.id_value = id$impl.asLong();
        this.channelId_value = channelId$impl.asLong();
        this.guildId_value = (Long) guildId$impl.toOptional().map(Id::asLong).orElse(0L);
        this.guildId_absent = guildId$impl.isAbsent();
        this.member_value = (PartialMemberData) member$impl.toOptional().orElse((PartialMemberData) null);
        this.member_absent = member$impl.isAbsent();
        this.mentionChannels_value = (List) mentionChannels$impl.toOptional().orElse((List) null);
        this.mentionChannels_absent = mentionChannels$impl.isAbsent();
        this.reactions_value = (List) reactions$impl.toOptional().orElse((List) null);
        this.reactions_absent = reactions$impl.isAbsent();
        this.nonce_value = nonce$impl.toOptional().orElse((Object) null);
        this.nonce_absent = nonce$impl.isAbsent();
        this.webhookId_value = (Long) webhookId$impl.toOptional().map(Id::asLong).orElse(0L);
        this.webhookId_absent = webhookId$impl.isAbsent();
        this.activity_value = (MessageActivityData) activity$impl.toOptional().orElse((MessageActivityData) null);
        this.activity_absent = activity$impl.isAbsent();
        this.application_value = (MessageApplicationData) application$impl.toOptional().orElse((MessageApplicationData) null);
        this.application_absent = application$impl.isAbsent();
        this.applicationId_value = (Long) applicationId$impl.toOptional().map(Id::asLong).orElse(0L);
        this.applicationId_absent = applicationId$impl.isAbsent();
        this.messageReference_value = (MessageReferenceData) messageReference$impl.toOptional().orElse((MessageReferenceData) null);
        this.messageReference_absent = messageReference$impl.isAbsent();
        this.flags_value = (Integer) flags$impl.toOptional().orElse((Integer) null);
        this.flags_absent = flags$impl.isAbsent();
        this.stickers_value = (List) stickers$impl.toOptional().orElse((List) null);
        this.stickers_absent = stickers$impl.isAbsent();
        this.stickerItems_value = (List) stickerItems$impl.toOptional().orElse((List) null);
        this.stickerItems_absent = stickerItems$impl.isAbsent();
        this.referencedMessage_value = (MessageData) Possible.flatOpt(referencedMessage$impl).orElse((MessageData) null);
        this.referencedMessage_absent = referencedMessage$impl.isAbsent();
        this.interaction_value = (MessageInteractionData) interaction$impl.toOptional().orElse((MessageInteractionData) null);
        this.interaction_absent = interaction$impl.isAbsent();
        this.components_value = (List) components$impl.toOptional().orElse((List) null);
        this.components_absent = components$impl.isAbsent();
    }

    @JsonProperty("id")
    public Id id() {
        return Id.of(this.id_value);
    }

    @JsonProperty("channel_id")
    public Id channelId() {
        return Id.of(this.channelId_value);
    }

    @JsonProperty("guild_id")
    public Possible<Id> guildId() {
        return this.guildId_absent ? Possible.absent() : Possible.of(Id.of(this.guildId_value));
    }

    @JsonProperty("author")
    public UserData author() {
        return this.author;
    }

    @JsonProperty("member")
    public Possible<PartialMemberData> member() {
        return this.member_absent ? Possible.absent() : Possible.of(this.member_value);
    }

    @JsonProperty("content")
    public String content() {
        return this.content;
    }

    @JsonProperty("timestamp")
    public String timestamp() {
        return this.timestamp;
    }

    @JsonProperty("edited_timestamp")
    public Optional<String> editedTimestamp() {
        return Optional.ofNullable(this.editedTimestamp);
    }

    @JsonProperty("tts")
    public boolean tts() {
        return this.tts;
    }

    @JsonProperty("mention_everyone")
    public boolean mentionEveryone() {
        return this.mentionEveryone;
    }

    @JsonProperty("mentions")
    public List<UserWithMemberData> mentions() {
        return this.mentions;
    }

    @JsonProperty("mention_roles")
    public List<String> mentionRoles() {
        return this.mentionRoles;
    }

    @JsonProperty("mention_channels")
    public Possible<List<ChannelMentionData>> mentionChannels() {
        return this.mentionChannels_absent ? Possible.absent() : Possible.of(this.mentionChannels_value);
    }

    @JsonProperty("attachments")
    public List<AttachmentData> attachments() {
        return this.attachments;
    }

    @JsonProperty("embeds")
    public List<EmbedData> embeds() {
        return this.embeds;
    }

    @JsonProperty("reactions")
    public Possible<List<ReactionData>> reactions() {
        return this.reactions_absent ? Possible.absent() : Possible.of(this.reactions_value);
    }

    @JsonProperty("nonce")
    public Possible<Object> nonce() {
        return this.nonce_absent ? Possible.absent() : Possible.of(this.nonce_value);
    }

    @JsonProperty("pinned")
    public boolean pinned() {
        return this.pinned;
    }

    @JsonProperty("webhook_id")
    public Possible<Id> webhookId() {
        return this.webhookId_absent ? Possible.absent() : Possible.of(Id.of(this.webhookId_value));
    }

    @JsonProperty("type")
    public int type() {
        return this.type;
    }

    @JsonProperty("activity")
    public Possible<MessageActivityData> activity() {
        return this.activity_absent ? Possible.absent() : Possible.of(this.activity_value);
    }

    @JsonProperty("application")
    public Possible<MessageApplicationData> application() {
        return this.application_absent ? Possible.absent() : Possible.of(this.application_value);
    }

    @JsonProperty("application_id")
    public Possible<Id> applicationId() {
        return this.applicationId_absent ? Possible.absent() : Possible.of(Id.of(this.applicationId_value));
    }

    @JsonProperty("message_reference")
    public Possible<MessageReferenceData> messageReference() {
        return this.messageReference_absent ? Possible.absent() : Possible.of(this.messageReference_value);
    }

    @JsonProperty("flags")
    public Possible<Integer> flags() {
        return this.flags_absent ? Possible.absent() : Possible.of(this.flags_value);
    }

    /**
     * @deprecated
     */
    @JsonProperty("stickers")
    @Deprecated
    public Possible<List<StickerData>> stickers() {
        return this.stickers_absent ? Possible.absent() : Possible.of(this.stickers_value);
    }

    @JsonProperty("sticker_items")
    public Possible<List<PartialStickerData>> stickerItems() {
        return this.stickerItems_absent ? Possible.absent() : Possible.of(this.stickerItems_value);
    }

    @JsonProperty("referenced_message")
    public Possible<Optional<MessageData>> referencedMessage() {
        return this.referencedMessage_absent ? Possible.absent() : Possible.of(Optional.ofNullable(this.referencedMessage_value));
    }

    @JsonProperty("interaction")
    public Possible<MessageInteractionData> interaction() {
        return this.interaction_absent ? Possible.absent() : Possible.of(this.interaction_value);
    }

    @JsonProperty("components")
    public Possible<List<ComponentData>> components() {
        return this.components_absent ? Possible.absent() : Possible.of(this.components_value);
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        } else {
            return another instanceof ImmutableMessageData && this.equalTo(0, (ImmutableMessageData) another);
        }
    }

    private boolean equalTo(int synthetic, ImmutableMessageData another) {
        return Objects.equals(this.id_value, another.id_value) && Objects.equals(this.channelId_value, another.channelId_value) &&
               this.guildId().equals(another.guildId()) && this.author.equals(another.author) && this.member().equals(another.member()) &&
               this.content.equals(another.content) && this.timestamp.equals(another.timestamp) &&
               Objects.equals(this.editedTimestamp, another.editedTimestamp) && this.tts == another.tts && this.mentionEveryone == another.mentionEveryone &&
               this.mentions.equals(another.mentions) && this.mentionRoles.equals(another.mentionRoles) &&
               Objects.equals(this.mentionChannels_value, another.mentionChannels_value) && this.attachments.equals(another.attachments) &&
               this.embeds.equals(another.embeds) && Objects.equals(this.reactions_value, another.reactions_value) && this.nonce().equals(another.nonce()) &&
               this.pinned == another.pinned && this.webhookId().equals(another.webhookId()) && this.type == another.type &&
               this.activity().equals(another.activity()) && this.application().equals(another.application()) &&
               this.applicationId().equals(another.applicationId()) && this.messageReference().equals(another.messageReference()) &&
               this.flags().equals(another.flags()) && Objects.equals(this.stickers_value, another.stickers_value) &&
               Objects.equals(this.stickerItems_value, another.stickerItems_value) && this.referencedMessage().equals(another.referencedMessage()) &&
               this.interaction().equals(another.interaction()) && Objects.equals(this.components_value, another.components_value);
    }

    public int hashCode() {
        int h = 5381;
        h += (h << 5) + Objects.hashCode(this.id_value);
        h += (h << 5) + Objects.hashCode(this.channelId_value);
        h += (h << 5) + this.guildId().hashCode();
        h += (h << 5) + this.author.hashCode();
        h += (h << 5) + this.member().hashCode();
        h += (h << 5) + this.content.hashCode();
        h += (h << 5) + this.timestamp.hashCode();
        h += (h << 5) + Objects.hashCode(this.editedTimestamp);
        h += (h << 5) + Boolean.hashCode(this.tts);
        h += (h << 5) + Boolean.hashCode(this.mentionEveryone);
        h += (h << 5) + this.mentions.hashCode();
        h += (h << 5) + this.mentionRoles.hashCode();
        h += (h << 5) + Objects.hashCode(this.mentionChannels_value);
        h += (h << 5) + this.attachments.hashCode();
        h += (h << 5) + this.embeds.hashCode();
        h += (h << 5) + Objects.hashCode(this.reactions_value);
        h += (h << 5) + this.nonce().hashCode();
        h += (h << 5) + Boolean.hashCode(this.pinned);
        h += (h << 5) + this.webhookId().hashCode();
        h += (h << 5) + this.type;
        h += (h << 5) + this.activity().hashCode();
        h += (h << 5) + this.application().hashCode();
        h += (h << 5) + this.applicationId().hashCode();
        h += (h << 5) + this.messageReference().hashCode();
        h += (h << 5) + this.flags().hashCode();
        h += (h << 5) + Objects.hashCode(this.stickers_value);
        h += (h << 5) + Objects.hashCode(this.stickerItems_value);
        h += (h << 5) + this.referencedMessage().hashCode();
        h += (h << 5) + this.interaction().hashCode();
        h += (h << 5) + Objects.hashCode(this.components_value);
        return h;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MessageData{");
        builder.append("id=").append(Objects.toString(this.id_value));
        builder.append(", ");
        builder.append("channelId=").append(Objects.toString(this.channelId_value));
        builder.append(", ");
        builder.append("guildId=").append(this.guildId().toString());
        builder.append(", ");
        builder.append("author=").append(this.author);
        builder.append(", ");
        builder.append("member=").append(this.member().toString());
        builder.append(", ");
        builder.append("content=").append(this.content);
        builder.append(", ");
        builder.append("timestamp=").append(this.timestamp);
        if (this.editedTimestamp != null) {
            builder.append(", ");
            builder.append("editedTimestamp=").append(this.editedTimestamp);
        }

        builder.append(", ");
        builder.append("tts=").append(this.tts);
        builder.append(", ");
        builder.append("mentionEveryone=").append(this.mentionEveryone);
        builder.append(", ");
        builder.append("mentions=").append(this.mentions);
        builder.append(", ");
        builder.append("mentionRoles=").append(this.mentionRoles);
        builder.append(", ");
        builder.append("mentionChannels=").append(Objects.toString(this.mentionChannels_value));
        builder.append(", ");
        builder.append("attachments=").append(this.attachments);
        builder.append(", ");
        builder.append("embeds=").append(this.embeds);
        builder.append(", ");
        builder.append("reactions=").append(Objects.toString(this.reactions_value));
        builder.append(", ");
        builder.append("nonce=").append(this.nonce().toString());
        builder.append(", ");
        builder.append("pinned=").append(this.pinned);
        builder.append(", ");
        builder.append("webhookId=").append(this.webhookId().toString());
        builder.append(", ");
        builder.append("type=").append(this.type);
        builder.append(", ");
        builder.append("activity=").append(this.activity().toString());
        builder.append(", ");
        builder.append("application=").append(this.application().toString());
        builder.append(", ");
        builder.append("applicationId=").append(this.applicationId().toString());
        builder.append(", ");
        builder.append("messageReference=").append(this.messageReference().toString());
        builder.append(", ");
        builder.append("flags=").append(this.flags().toString());
        builder.append(", ");
        builder.append("stickers=").append(Objects.toString(this.stickers_value));
        builder.append(", ");
        builder.append("stickerItems=").append(Objects.toString(this.stickerItems_value));
        builder.append(", ");
        builder.append("referencedMessage=").append(this.referencedMessage().toString());
        builder.append(", ");
        builder.append("interaction=").append(this.interaction().toString());
        builder.append(", ");
        builder.append("components=").append(Objects.toString(this.components_value));
        return builder.append("}").toString();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @JsonCreator(
            mode = Mode.DELEGATING
    )
    static ImmutableMessageData fromJson(Json json) {
        Builder builder = builder();
        if (json.id != null) {
            builder.id(json.id);
        }

        if (json.channelId != null) {
            builder.channelId(json.channelId);
        }

        if (json.guildId != null) {
            builder.guildId(json.guildId);
        }

        if (json.author != null) {
            builder.author(json.author);
        }

        if (json.member != null) {
            builder.member(json.member);
        }

        if (json.content != null) {
            builder.content(json.content);
        }

        if (json.timestamp != null) {
            builder.timestamp(json.timestamp);
        }

        if (json.editedTimestamp != null) {
            builder.editedTimestamp(json.editedTimestamp);
        }

        if (json.ttsIsSet) {
            builder.tts(json.tts);
        }

        if (json.mentionEveryoneIsSet) {
            builder.mentionEveryone(json.mentionEveryone);
        }

        if (json.mentions != null) {
            builder.addAllMentions(json.mentions);
        }

        if (json.mentionRoles != null) {
            builder.addAllMentionRoles(json.mentionRoles);
        }

        if (json.mentionChannels != null) {
            builder.mentionChannels(json.mentionChannels);
        }

        if (json.attachments != null) {
            builder.addAllAttachments(json.attachments);
        }

        if (json.embeds != null) {
            builder.addAllEmbeds(json.embeds);
        }

        if (json.reactions != null) {
            builder.reactions(json.reactions);
        }

        if (json.nonce != null) {
            builder.nonce(json.nonce);
        }

        if (json.pinnedIsSet) {
            builder.pinned(json.pinned);
        }

        if (json.webhookId != null) {
            builder.webhookId(json.webhookId);
        }

        if (json.typeIsSet) {
            builder.type(json.type);
        }

        if (json.activity != null) {
            builder.activity(json.activity);
        }

        if (json.application != null) {
            builder.application(json.application);
        }

        if (json.applicationId != null) {
            builder.applicationId(json.applicationId);
        }

        if (json.messageReference != null) {
            builder.messageReference(json.messageReference);
        }

        if (json.flags != null) {
            builder.flags(json.flags);
        }

        if (json.stickers != null) {
            builder.stickers(json.stickers);
        }

        if (json.stickerItems != null) {
            builder.stickerItems(json.stickerItems);
        }

        if (json.referencedMessage != null) {
            builder.referencedMessage(json.referencedMessage);
        }

        if (json.interaction != null) {
            builder.interaction(json.interaction);
        }

        if (json.components != null) {
            builder.components(json.components);
        }

        return builder.build();
    }

    public static ImmutableMessageData copyOf(MessageData instance) {
        return instance instanceof ImmutableMessageData ? (ImmutableMessageData) instance : builder().from(instance).build();
    }

    public boolean isGuildIdPresent() {
        return !this.guildId_absent;
    }

    public long guildIdOrElse(long defaultValue) {
        return !this.guildId_absent ? this.guildId_value : defaultValue;
    }

    public boolean isMemberPresent() {
        return !this.member_absent;
    }

    public PartialMemberData memberOrElse(PartialMemberData defaultValue) {
        return !this.member_absent ? this.member_value : defaultValue;
    }

    public boolean isMentionChannelsPresent() {
        return !this.mentionChannels_absent;
    }

    public List<ChannelMentionData> mentionChannelsOrElse(List<ChannelMentionData> defaultValue) {
        return !this.mentionChannels_absent ? this.mentionChannels_value : defaultValue;
    }

    public boolean isReactionsPresent() {
        return !this.reactions_absent;
    }

    public List<ReactionData> reactionsOrElse(List<ReactionData> defaultValue) {
        return !this.reactions_absent ? this.reactions_value : defaultValue;
    }

    public boolean isNoncePresent() {
        return !this.nonce_absent;
    }

    public Object nonceOrElse(Object defaultValue) {
        return !this.nonce_absent ? this.nonce_value : defaultValue;
    }

    public boolean isWebhookIdPresent() {
        return !this.webhookId_absent;
    }

    public long webhookIdOrElse(long defaultValue) {
        return !this.webhookId_absent ? this.webhookId_value : defaultValue;
    }

    public boolean isActivityPresent() {
        return !this.activity_absent;
    }

    public MessageActivityData activityOrElse(MessageActivityData defaultValue) {
        return !this.activity_absent ? this.activity_value : defaultValue;
    }

    public boolean isApplicationPresent() {
        return !this.application_absent;
    }

    public MessageApplicationData applicationOrElse(MessageApplicationData defaultValue) {
        return !this.application_absent ? this.application_value : defaultValue;
    }

    public boolean isApplicationIdPresent() {
        return !this.applicationId_absent;
    }

    public long applicationIdOrElse(long defaultValue) {
        return !this.applicationId_absent ? this.applicationId_value : defaultValue;
    }

    public boolean isMessageReferencePresent() {
        return !this.messageReference_absent;
    }

    public MessageReferenceData messageReferenceOrElse(MessageReferenceData defaultValue) {
        return !this.messageReference_absent ? this.messageReference_value : defaultValue;
    }

    public boolean isFlagsPresent() {
        return !this.flags_absent;
    }

    public Integer flagsOrElse(Integer defaultValue) {
        return !this.flags_absent ? this.flags_value : defaultValue;
    }

    public boolean isStickersPresent() {
        return !this.stickers_absent;
    }

    public List<StickerData> stickersOrElse(List<StickerData> defaultValue) {
        return !this.stickers_absent ? this.stickers_value : defaultValue;
    }

    public boolean isStickerItemsPresent() {
        return !this.stickerItems_absent;
    }

    public List<PartialStickerData> stickerItemsOrElse(List<PartialStickerData> defaultValue) {
        return !this.stickerItems_absent ? this.stickerItems_value : defaultValue;
    }

    public boolean isReferencedMessagePresent() {
        return !this.referencedMessage_absent;
    }

    public MessageData referencedMessageOrElse(MessageData defaultValue) {
        return !this.referencedMessage_absent ? this.referencedMessage_value : defaultValue;
    }

    public boolean isInteractionPresent() {
        return !this.interaction_absent;
    }

    public MessageInteractionData interactionOrElse(MessageInteractionData defaultValue) {
        return !this.interaction_absent ? this.interaction_value : defaultValue;
    }

    public boolean isComponentsPresent() {
        return !this.components_absent;
    }

    public List<ComponentData> componentsOrElse(List<ComponentData> defaultValue) {
        return !this.components_absent ? this.components_value : defaultValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
        ArrayList list;
        if (iterable instanceof Collection) {
            int size = ((Collection) iterable).size();
            if (size == 0) {
                return Collections.emptyList();
            }

            list = new ArrayList();
        } else {
            list = new ArrayList();
        }

        Iterator var6 = iterable.iterator();

        while (true) {
            Object element;
            do {
                if (!var6.hasNext()) {
                    return list;
                }

                element = var6.next();
            } while (skipNulls && element == null);

            if (checkNulls) {
                Objects.requireNonNull(element, "element");
            }

            list.add(element);
        }
    }

    private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
        switch (list.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(list.get(0));
            default:
                if (clone) {
                    return Collections.unmodifiableList(new ArrayList(list));
                } else {
                    if (list instanceof ArrayList) {
                        ((ArrayList) list).trimToSize();
                    }

                    return Collections.unmodifiableList(list);
                }
        }
    }

    public static final class Builder {
        private static final long INIT_BIT_AUTHOR = 1L;
        private static final long INIT_BIT_CONTENT = 2L;
        private static final long INIT_BIT_TIMESTAMP = 4L;
        private static final long INIT_BIT_TTS = 8L;
        private static final long INIT_BIT_MENTION_EVERYONE = 16L;
        private static final long INIT_BIT_PINNED = 32L;
        private static final long INIT_BIT_TYPE = 64L;
        private long initBits;
        private Id id_id;
        private Id channelId_id;
        private Possible<Id> guildId_possible;
        private Possible<PartialMemberData> member_possible;
        private List<ChannelMentionData> mentionChannels_list;
        private List<ReactionData> reactions_list;
        private Possible<Object> nonce_possible;
        private Possible<Id> webhookId_possible;
        private Possible<MessageActivityData> activity_possible;
        private Possible<MessageApplicationData> application_possible;
        private Possible<Id> applicationId_possible;
        private Possible<MessageReferenceData> messageReference_possible;
        private Possible<Integer> flags_possible;
        private List<StickerData> stickers_list;
        private List<PartialStickerData> stickerItems_list;
        private Possible<Optional<MessageData>> referencedMessage_possible;
        private Possible<MessageInteractionData> interaction_possible;
        private List<ComponentData> components_list;
        private UserData author;
        private String content;
        private String timestamp;
        private String editedTimestamp;
        private boolean tts;
        private boolean mentionEveryone;
        private List<UserWithMemberData> mentions;
        private List<String> mentionRoles;
        private List<AttachmentData> attachments;
        private List<EmbedData> embeds;
        private boolean pinned;
        private int type;

        private Builder() {
            this.initBits = 127L;
            this.id_id = null;
            this.channelId_id = null;
            this.guildId_possible = Possible.absent();
            this.member_possible = Possible.absent();
            this.mentionChannels_list = null;
            this.reactions_list = null;
            this.nonce_possible = Possible.absent();
            this.webhookId_possible = Possible.absent();
            this.activity_possible = Possible.absent();
            this.application_possible = Possible.absent();
            this.applicationId_possible = Possible.absent();
            this.messageReference_possible = Possible.absent();
            this.flags_possible = Possible.absent();
            this.stickers_list = null;
            this.stickerItems_list = null;
            this.referencedMessage_possible = Possible.absent();
            this.interaction_possible = Possible.absent();
            this.components_list = null;
            this.mentions = new ArrayList();
            this.mentionRoles = new ArrayList();
            this.attachments = new ArrayList();
            this.embeds = new ArrayList();
        }

        public final Builder from(MessageData instance) {
            Objects.requireNonNull(instance, "instance");
            this.id(instance.id());
            this.channelId(instance.channelId());
            this.guildId(instance.guildId());
            this.author(instance.author());
            this.member(instance.member());
            this.content(instance.content());
            this.timestamp(instance.timestamp());
            Optional<String> editedTimestampOptional = instance.editedTimestamp();
            if (editedTimestampOptional.isPresent()) {
                this.editedTimestamp(editedTimestampOptional);
            }

            this.tts(instance.tts());
            this.mentionEveryone(instance.mentionEveryone());
            this.addAllMentions(instance.mentions());
            this.addAllMentionRoles(instance.mentionRoles());
            this.mentionChannels(instance.mentionChannels());
            this.addAllAttachments(instance.attachments());
            this.addAllEmbeds(instance.embeds());
            this.reactions(instance.reactions());
            this.nonce(instance.nonce());
            this.pinned(instance.pinned());
            this.webhookId(instance.webhookId());
            this.type(instance.type());
            this.activity(instance.activity());
            this.application(instance.application());
            this.applicationId(instance.applicationId());
            this.messageReference(instance.messageReference());
            this.flags(instance.flags());
            this.stickers(instance.stickers());
            this.stickerItems(instance.stickerItems());
            this.referencedMessage(instance.referencedMessage());
            this.interaction(instance.interaction());
            this.components(instance.components());
            return this;
        }

        public Builder id(String value) {
            this.id_id = Id.of(value);
            return this;
        }

        public Builder id(long value) {
            this.id_id = Id.of(value);
            return this;
        }

        @JsonProperty("id")
        public Builder id(Id value) {
            this.id_id = value;
            return this;
        }

        public Builder channelId(String value) {
            this.channelId_id = Id.of(value);
            return this;
        }

        public Builder channelId(long value) {
            this.channelId_id = Id.of(value);
            return this;
        }

        @JsonProperty("channel_id")
        public Builder channelId(Id value) {
            this.channelId_id = value;
            return this;
        }

        public Builder guildId(String value) {
            this.guildId_possible = Possible.of(Id.of(value));
            return this;
        }

        public Builder guildId(long value) {
            this.guildId_possible = Possible.of(Id.of(value));
            return this;
        }

        public Builder guildId(Id value) {
            this.guildId_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("guild_id")
        public Builder guildId(Possible<Id> value) {
            this.guildId_possible = value;
            return this;
        }

        @JsonProperty("author")
        public final Builder author(UserData author) {
            this.author = (UserData) Objects.requireNonNull(author, "author");
            this.initBits &= -2L;
            return this;
        }

        @JsonProperty("member")
        public Builder member(Possible<PartialMemberData> value) {
            this.member_possible = value;
            return this;
        }

        public Builder member(PartialMemberData value) {
            this.member_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("content")
        public final Builder content(String content) {
            this.content = (String) Objects.requireNonNull(content, "content");
            this.initBits &= -3L;
            return this;
        }

        @JsonProperty("timestamp")
        public final Builder timestamp(String timestamp) {
            this.timestamp = (String) Objects.requireNonNull(timestamp, "timestamp");
            this.initBits &= -5L;
            return this;
        }

        public final Builder editedTimestamp(String editedTimestamp) {
            this.editedTimestamp = (String) Objects.requireNonNull(editedTimestamp, "editedTimestamp");
            return this;
        }

        @JsonProperty("edited_timestamp")
        public final Builder editedTimestamp(Optional<String> editedTimestamp) {
            this.editedTimestamp = (String) editedTimestamp.orElse((String) null);
            return this;
        }

        @JsonProperty("tts")
        public final Builder tts(boolean tts) {
            this.tts = tts;
            this.initBits &= -9L;
            return this;
        }

        @JsonProperty("mention_everyone")
        public final Builder mentionEveryone(boolean mentionEveryone) {
            this.mentionEveryone = mentionEveryone;
            this.initBits &= -17L;
            return this;
        }

        public final Builder addMention(UserWithMemberData element) {
            this.mentions.add((UserWithMemberData) Objects.requireNonNull(element, "mentions element"));
            return this;
        }

        public final Builder addMentions(UserWithMemberData... elements) {
            UserWithMemberData[] var2 = elements;
            int var3 = elements.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                UserWithMemberData element = var2[var4];
                this.mentions.add((UserWithMemberData) Objects.requireNonNull(element, "mentions element"));
            }

            return this;
        }

        @JsonProperty("mentions")
        public final Builder mentions(Iterable<? extends UserWithMemberData> elements) {
            this.mentions.clear();
            return this.addAllMentions(elements);
        }

        public final Builder addAllMentions(Iterable<? extends UserWithMemberData> elements) {
            Iterator var2 = elements.iterator();

            while (var2.hasNext()) {
                UserWithMemberData element = (UserWithMemberData) var2.next();
                this.mentions.add((UserWithMemberData) Objects.requireNonNull(element, "mentions element"));
            }

            return this;
        }

        public final Builder addMentionRole(String element) {
            this.mentionRoles.add((String) Objects.requireNonNull(element, "mentionRoles element"));
            return this;
        }

        public final Builder addMentionRoles(String... elements) {
            String[] var2 = elements;
            int var3 = elements.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String element = var2[var4];
                this.mentionRoles.add((String) Objects.requireNonNull(element, "mentionRoles element"));
            }

            return this;
        }

        @JsonProperty("mention_roles")
        public final Builder mentionRoles(Iterable<String> elements) {
            this.mentionRoles.clear();
            return this.addAllMentionRoles(elements);
        }

        public final Builder addAllMentionRoles(Iterable<String> elements) {
            Iterator var2 = elements.iterator();

            while (var2.hasNext()) {
                String element = (String) var2.next();
                this.mentionRoles.add((String) Objects.requireNonNull(element, "mentionRoles element"));
            }

            return this;
        }

        public Builder addMentionChannel(ChannelMentionData element) {
            this.mentionChannels_getOrCreate().add(element);
            return this;
        }

        public Builder addAllMentionChannels(List<ChannelMentionData> elements) {
            this.mentionChannels_getOrCreate().addAll(elements);
            return this;
        }

        @JsonProperty("mention_channels")
        public Builder mentionChannels(Possible<List<ChannelMentionData>> elements) {
            this.mentionChannels_list = null;
            elements.toOptional().ifPresent((e) -> {
                this.mentionChannels_getOrCreate().addAll(e);
            });
            return this;
        }

        public Builder mentionChannels(List<ChannelMentionData> elements) {
            this.mentionChannels_list = new ArrayList(elements);
            return this;
        }

        public Builder mentionChannels(Iterable<ChannelMentionData> elements) {
            this.mentionChannels_list = (List) StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
            return this;
        }

        public final Builder addAttachment(AttachmentData element) {
            this.attachments.add((AttachmentData) Objects.requireNonNull(element, "attachments element"));
            return this;
        }

        public final Builder addAttachments(AttachmentData... elements) {
            AttachmentData[] var2 = elements;
            int var3 = elements.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                AttachmentData element = var2[var4];
                this.attachments.add((AttachmentData) Objects.requireNonNull(element, "attachments element"));
            }

            return this;
        }

        @JsonProperty("attachments")
        public final Builder attachments(Iterable<? extends AttachmentData> elements) {
            this.attachments.clear();
            return this.addAllAttachments(elements);
        }

        public final Builder addAllAttachments(Iterable<? extends AttachmentData> elements) {
            Iterator var2 = elements.iterator();

            while (var2.hasNext()) {
                AttachmentData element = (AttachmentData) var2.next();
                this.attachments.add((AttachmentData) Objects.requireNonNull(element, "attachments element"));
            }

            return this;
        }

        public final Builder addEmbed(EmbedData element) {
            this.embeds.add((EmbedData) Objects.requireNonNull(element, "embeds element"));
            return this;
        }

        public final Builder addEmbeds(EmbedData... elements) {
            EmbedData[] var2 = elements;
            int var3 = elements.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                EmbedData element = var2[var4];
                this.embeds.add((EmbedData) Objects.requireNonNull(element, "embeds element"));
            }

            return this;
        }

        @JsonProperty("embeds")
        public final Builder embeds(Iterable<? extends EmbedData> elements) {
            this.embeds.clear();
            return this.addAllEmbeds(elements);
        }

        public final Builder addAllEmbeds(Iterable<? extends EmbedData> elements) {
            Iterator var2 = elements.iterator();

            while (var2.hasNext()) {
                EmbedData element = (EmbedData) var2.next();
                this.embeds.add((EmbedData) Objects.requireNonNull(element, "embeds element"));
            }

            return this;
        }

        public Builder addReaction(ReactionData element) {
            this.reactions_getOrCreate().add(element);
            return this;
        }

        public Builder addAllReactions(List<ReactionData> elements) {
            this.reactions_getOrCreate().addAll(elements);
            return this;
        }

        @JsonProperty("reactions")
        public Builder reactions(Possible<List<ReactionData>> elements) {
            this.reactions_list = null;
            elements.toOptional().ifPresent((e) -> {
                this.reactions_getOrCreate().addAll(e);
            });
            return this;
        }

        public Builder reactions(List<ReactionData> elements) {
            this.reactions_list = new ArrayList(elements);
            return this;
        }

        public Builder reactions(Iterable<ReactionData> elements) {
            this.reactions_list = (List) StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
            return this;
        }

        @JsonProperty("nonce")
        public Builder nonce(Possible<Object> value) {
            this.nonce_possible = value;
            return this;
        }

        public Builder nonce(Object value) {
            this.nonce_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("pinned")
        public final Builder pinned(boolean pinned) {
            this.pinned = pinned;
            this.initBits &= -33L;
            return this;
        }

        public Builder webhookId(String value) {
            this.webhookId_possible = Possible.of(Id.of(value));
            return this;
        }

        public Builder webhookId(long value) {
            this.webhookId_possible = Possible.of(Id.of(value));
            return this;
        }

        public Builder webhookId(Id value) {
            this.webhookId_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("webhook_id")
        public Builder webhookId(Possible<Id> value) {
            this.webhookId_possible = value;
            return this;
        }

        @JsonProperty("type")
        public final Builder type(int type) {
            this.type = type;
            this.initBits &= -65L;
            return this;
        }

        @JsonProperty("activity")
        public Builder activity(Possible<MessageActivityData> value) {
            this.activity_possible = value;
            return this;
        }

        public Builder activity(MessageActivityData value) {
            this.activity_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("application")
        public Builder application(Possible<MessageApplicationData> value) {
            this.application_possible = value;
            return this;
        }

        public Builder application(MessageApplicationData value) {
            this.application_possible = Possible.of(value);
            return this;
        }

        public Builder applicationId(String value) {
            this.applicationId_possible = Possible.of(Id.of(value));
            return this;
        }

        public Builder applicationId(long value) {
            this.applicationId_possible = Possible.of(Id.of(value));
            return this;
        }

        public Builder applicationId(Id value) {
            this.applicationId_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("application_id")
        public Builder applicationId(Possible<Id> value) {
            this.applicationId_possible = value;
            return this;
        }

        @JsonProperty("message_reference")
        public Builder messageReference(Possible<MessageReferenceData> value) {
            this.messageReference_possible = value;
            return this;
        }

        public Builder messageReference(MessageReferenceData value) {
            this.messageReference_possible = Possible.of(value);
            return this;
        }

        @JsonProperty("flags")
        public Builder flags(Possible<Integer> value) {
            this.flags_possible = value;
            return this;
        }

        public Builder flags(Integer value) {
            this.flags_possible = Possible.of(value);
            return this;
        }

        public Builder addSticker(StickerData element) {
            this.stickers_getOrCreate().add(element);
            return this;
        }

        public Builder addAllStickers(List<StickerData> elements) {
            this.stickers_getOrCreate().addAll(elements);
            return this;
        }

        @JsonProperty("stickers")
        public Builder stickers(Possible<List<StickerData>> elements) {
            this.stickers_list = null;
            elements.toOptional().ifPresent((e) -> {
                this.stickers_getOrCreate().addAll(e);
            });
            return this;
        }

        public Builder stickers(List<StickerData> elements) {
            this.stickers_list = new ArrayList(elements);
            return this;
        }

        public Builder stickers(Iterable<StickerData> elements) {
            this.stickers_list = (List) StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
            return this;
        }

        public Builder addStickerItem(PartialStickerData element) {
            this.stickerItems_getOrCreate().add(element);
            return this;
        }

        public Builder addAllStickerItems(List<PartialStickerData> elements) {
            this.stickerItems_getOrCreate().addAll(elements);
            return this;
        }

        @JsonProperty("sticker_items")
        public Builder stickerItems(Possible<List<PartialStickerData>> elements) {
            this.stickerItems_list = null;
            elements.toOptional().ifPresent((e) -> {
                this.stickerItems_getOrCreate().addAll(e);
            });
            return this;
        }

        public Builder stickerItems(List<PartialStickerData> elements) {
            this.stickerItems_list = new ArrayList(elements);
            return this;
        }

        public Builder stickerItems(Iterable<PartialStickerData> elements) {
            this.stickerItems_list = (List) StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
            return this;
        }

        @JsonProperty("referenced_message")
        public Builder referencedMessage(Possible<Optional<MessageData>> value) {
            this.referencedMessage_possible = value;
            return this;
        }

        /**
         * @deprecated
         */
        @Deprecated
        public Builder referencedMessage(@Nullable MessageData value) {
            this.referencedMessage_possible = Possible.of(Optional.ofNullable(value));
            return this;
        }

        public Builder referencedMessageOrNull(@Nullable MessageData value) {
            this.referencedMessage_possible = Possible.of(Optional.ofNullable(value));
            return this;
        }

        @JsonProperty("interaction")
        public Builder interaction(Possible<MessageInteractionData> value) {
            this.interaction_possible = value;
            return this;
        }

        public Builder interaction(MessageInteractionData value) {
            this.interaction_possible = Possible.of(value);
            return this;
        }

        public Builder addComponent(ComponentData element) {
            this.components_getOrCreate().add(element);
            return this;
        }

        public Builder addAllComponents(List<ComponentData> elements) {
            this.components_getOrCreate().addAll(elements);
            return this;
        }

        @JsonProperty("components")
        public Builder components(Possible<List<ComponentData>> elements) {
            this.components_list = null;
            elements.toOptional().ifPresent((e) -> {
                this.components_getOrCreate().addAll(e);
            });
            return this;
        }

        public Builder components(List<ComponentData> elements) {
            this.components_list = new ArrayList(elements);
            return this;
        }

        public Builder components(Iterable<ComponentData> elements) {
            this.components_list = (List) StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
            return this;
        }

        public ImmutableMessageData build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            } else {
                return new ImmutableMessageData(null, this.id_build(), this.channelId_build(), this.guildId_build(), this.author,
                                                this.member_build(), this.content, this.timestamp, this.editedTimestamp, this.tts, this.mentionEveryone,
                                                ImmutableMessageData.createUnmodifiableList(true, this.mentions),
                                                ImmutableMessageData.createUnmodifiableList(true, this.mentionRoles), this.mentionChannels_build(),
                                                ImmutableMessageData.createUnmodifiableList(true, this.attachments),
                                                ImmutableMessageData.createUnmodifiableList(true, this.embeds), this.reactions_build(), this.nonce_build(),
                                                this.pinned, this.webhookId_build(), this.type, this.activity_build(), this.application_build(),
                                                this.applicationId_build(), this.messageReference_build(), this.flags_build(), this.stickers_build(),
                                                this.stickerItems_build(), this.referencedMessage_build(), this.interaction_build(), this.components_build());
            }
        }

        private String formatRequiredAttributesMessage() {
            List<String> attributes = new ArrayList();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("author");
            }

            if ((this.initBits & 2L) != 0L) {
                attributes.add("content");
            }

            if ((this.initBits & 4L) != 0L) {
                attributes.add("timestamp");
            }

            if ((this.initBits & 8L) != 0L) {
                attributes.add("tts");
            }

            if ((this.initBits & 16L) != 0L) {
                attributes.add("mentionEveryone");
            }

            if ((this.initBits & 32L) != 0L) {
                attributes.add("pinned");
            }

            if ((this.initBits & 64L) != 0L) {
                attributes.add("type");
            }

            return "Cannot build MessageData, some of required attributes are not set " + attributes;
        }

        private Id id_build() {
            return this.id_id;
        }

        private Id channelId_build() {
            return this.channelId_id;
        }

        private Possible<Id> guildId_build() {
            return this.guildId_possible;
        }

        private Possible<PartialMemberData> member_build() {
            return this.member_possible;
        }

        private Possible<List<ChannelMentionData>> mentionChannels_build() {
            return this.mentionChannels_list == null ? Possible.absent() : Possible.of(this.mentionChannels_list);
        }

        private List<ChannelMentionData> mentionChannels_getOrCreate() {
            if (this.mentionChannels_list == null) {
                this.mentionChannels_list = new ArrayList();
            }

            return this.mentionChannels_list;
        }

        private Possible<List<ReactionData>> reactions_build() {
            return this.reactions_list == null ? Possible.absent() : Possible.of(this.reactions_list);
        }

        private List<ReactionData> reactions_getOrCreate() {
            if (this.reactions_list == null) {
                this.reactions_list = new ArrayList();
            }

            return this.reactions_list;
        }

        private Possible<Object> nonce_build() {
            return this.nonce_possible;
        }

        private Possible<Id> webhookId_build() {
            return this.webhookId_possible;
        }

        private Possible<MessageActivityData> activity_build() {
            return this.activity_possible;
        }

        private Possible<MessageApplicationData> application_build() {
            return this.application_possible;
        }

        private Possible<Id> applicationId_build() {
            return this.applicationId_possible;
        }

        private Possible<MessageReferenceData> messageReference_build() {
            return this.messageReference_possible;
        }

        private Possible<Integer> flags_build() {
            return this.flags_possible;
        }

        private Possible<List<StickerData>> stickers_build() {
            return this.stickers_list == null ? Possible.absent() : Possible.of(this.stickers_list);
        }

        private List<StickerData> stickers_getOrCreate() {
            if (this.stickers_list == null) {
                this.stickers_list = new ArrayList();
            }

            return this.stickers_list;
        }

        private Possible<List<PartialStickerData>> stickerItems_build() {
            return this.stickerItems_list == null ? Possible.absent() : Possible.of(this.stickerItems_list);
        }

        private List<PartialStickerData> stickerItems_getOrCreate() {
            if (this.stickerItems_list == null) {
                this.stickerItems_list = new ArrayList();
            }

            return this.stickerItems_list;
        }

        private Possible<Optional<MessageData>> referencedMessage_build() {
            return this.referencedMessage_possible;
        }

        private Possible<MessageInteractionData> interaction_build() {
            return this.interaction_possible;
        }

        private Possible<List<ComponentData>> components_build() {
            return this.components_list == null ? Possible.absent() : Possible.of(this.components_list);
        }

        private List<ComponentData> components_getOrCreate() {
            if (this.components_list == null) {
                this.components_list = new ArrayList();
            }

            return this.components_list;
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    @JsonDeserialize
    @JsonAutoDetect(
            fieldVisibility = Visibility.NONE
    )
    public static final class Json implements MessageData {
        Id id;
        Id channelId;
        Possible<Id> guildId = Possible.absent();
        UserData author;
        Possible<PartialMemberData> member = Possible.absent();
        String content;
        String timestamp;
        Optional<String> editedTimestamp = Optional.empty();
        boolean tts;
        boolean ttsIsSet;
        boolean mentionEveryone;
        boolean mentionEveryoneIsSet;
        List<UserWithMemberData> mentions = Collections.emptyList();
        List<String> mentionRoles = Collections.emptyList();
        Possible<List<ChannelMentionData>> mentionChannels = Possible.absent();
        List<AttachmentData> attachments = Collections.emptyList();
        List<EmbedData> embeds = Collections.emptyList();
        Possible<List<ReactionData>> reactions = Possible.absent();
        Possible<Object> nonce = Possible.absent();
        boolean pinned;
        boolean pinnedIsSet;
        Possible<Id> webhookId = Possible.absent();
        int type;
        boolean typeIsSet;
        Possible<MessageActivityData> activity = Possible.absent();
        Possible<MessageApplicationData> application = Possible.absent();
        Possible<Id> applicationId = Possible.absent();
        Possible<MessageReferenceData> messageReference = Possible.absent();
        Possible<Integer> flags = Possible.absent();
        Possible<List<StickerData>> stickers = Possible.absent();
        Possible<List<PartialStickerData>> stickerItems = Possible.absent();
        Possible<Optional<MessageData>> referencedMessage = Possible.absent();
        Possible<MessageInteractionData> interaction = Possible.absent();
        Possible<List<ComponentData>> components = Possible.absent();

        Json() {
        }

        @JsonProperty("id")
        public void setId(Id id) {
            this.id = id;
        }

        @JsonProperty("channel_id")
        public void setChannelId(Id channelId) {
            this.channelId = channelId;
        }

        @JsonProperty("guild_id")
        public void setGuildId(Possible<Id> guildId) {
            this.guildId = guildId;
        }

        @JsonProperty("author")
        public void setAuthor(UserData author) {
            this.author = author;
        }

        @JsonProperty("member")
        public void setMember(Possible<PartialMemberData> member) {
            this.member = member;
        }

        @JsonProperty("content")
        public void setContent(String content) {
            this.content = content;
        }

        @JsonProperty("timestamp")
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @JsonProperty("edited_timestamp")
        public void setEditedTimestamp(Optional<String> editedTimestamp) {
            this.editedTimestamp = editedTimestamp;
        }

        @JsonProperty("tts")
        public void setTts(boolean tts) {
            this.tts = tts;
            this.ttsIsSet = true;
        }

        @JsonProperty("mention_everyone")
        public void setMentionEveryone(boolean mentionEveryone) {
            this.mentionEveryone = mentionEveryone;
            this.mentionEveryoneIsSet = true;
        }

        @JsonProperty("mentions")
        public void setMentions(List<UserWithMemberData> mentions) {
            this.mentions = mentions;
        }

        @JsonProperty("mention_roles")
        public void setMentionRoles(List<String> mentionRoles) {
            this.mentionRoles = mentionRoles;
        }

        @JsonProperty("mention_channels")
        public void setMentionChannels(Possible<List<ChannelMentionData>> mentionChannels) {
            this.mentionChannels = mentionChannels;
        }

        @JsonProperty("attachments")
        public void setAttachments(List<AttachmentData> attachments) {
            this.attachments = attachments;
        }

        @JsonProperty("embeds")
        public void setEmbeds(List<EmbedData> embeds) {
            this.embeds = embeds;
        }

        @JsonProperty("reactions")
        public void setReactions(Possible<List<ReactionData>> reactions) {
            this.reactions = reactions;
        }

        @JsonProperty("nonce")
        public void setNonce(Possible<Object> nonce) {
            this.nonce = nonce;
        }

        @JsonProperty("pinned")
        public void setPinned(boolean pinned) {
            this.pinned = pinned;
            this.pinnedIsSet = true;
        }

        @JsonProperty("webhook_id")
        public void setWebhookId(Possible<Id> webhookId) {
            this.webhookId = webhookId;
        }

        @JsonProperty("type")
        public void setType(int type) {
            this.type = type;
            this.typeIsSet = true;
        }

        @JsonProperty("activity")
        public void setActivity(Possible<MessageActivityData> activity) {
            this.activity = activity;
        }

        @JsonProperty("application")
        public void setApplication(Possible<MessageApplicationData> application) {
            this.application = application;
        }

        @JsonProperty("application_id")
        public void setApplicationId(Possible<Id> applicationId) {
            this.applicationId = applicationId;
        }

        @JsonProperty("message_reference")
        public void setMessageReference(Possible<MessageReferenceData> messageReference) {
            this.messageReference = messageReference;
        }

        @JsonProperty("flags")
        public void setFlags(Possible<Integer> flags) {
            this.flags = flags;
        }

        @JsonProperty("stickers")
        public void setStickers(Possible<List<StickerData>> stickers) {
            this.stickers = stickers;
        }

        @JsonProperty("sticker_items")
        public void setStickerItems(Possible<List<PartialStickerData>> stickerItems) {
            this.stickerItems = stickerItems;
        }

        @JsonProperty("referenced_message")
        public void setReferencedMessage(Possible<Optional<MessageData>> referencedMessage) {
            this.referencedMessage = referencedMessage;
        }

        @JsonProperty("interaction")
        public void setInteraction(Possible<MessageInteractionData> interaction) {
            this.interaction = interaction;
        }

        @JsonProperty("components")
        public void setComponents(Possible<List<ComponentData>> components) {
            this.components = components;
        }

        public Id id() {
            throw new UnsupportedOperationException();
        }

        public Id channelId() {
            throw new UnsupportedOperationException();
        }

        public Possible<Id> guildId() {
            throw new UnsupportedOperationException();
        }

        public UserData author() {
            throw new UnsupportedOperationException();
        }

        public Possible<PartialMemberData> member() {
            throw new UnsupportedOperationException();
        }

        public String content() {
            throw new UnsupportedOperationException();
        }

        public String timestamp() {
            throw new UnsupportedOperationException();
        }

        public Optional<String> editedTimestamp() {
            throw new UnsupportedOperationException();
        }

        public boolean tts() {
            throw new UnsupportedOperationException();
        }

        public boolean mentionEveryone() {
            throw new UnsupportedOperationException();
        }

        public List<UserWithMemberData> mentions() {
            throw new UnsupportedOperationException();
        }

        public List<String> mentionRoles() {
            throw new UnsupportedOperationException();
        }

        public Possible<List<ChannelMentionData>> mentionChannels() {
            throw new UnsupportedOperationException();
        }

        public List<AttachmentData> attachments() {
            throw new UnsupportedOperationException();
        }

        public List<EmbedData> embeds() {
            throw new UnsupportedOperationException();
        }

        public Possible<List<ReactionData>> reactions() {
            throw new UnsupportedOperationException();
        }

        public Possible<Object> nonce() {
            throw new UnsupportedOperationException();
        }

        public boolean pinned() {
            throw new UnsupportedOperationException();
        }

        public Possible<Id> webhookId() {
            throw new UnsupportedOperationException();
        }

        public int type() {
            throw new UnsupportedOperationException();
        }

        public Possible<MessageActivityData> activity() {
            throw new UnsupportedOperationException();
        }

        public Possible<MessageApplicationData> application() {
            throw new UnsupportedOperationException();
        }

        public Possible<Id> applicationId() {
            throw new UnsupportedOperationException();
        }

        public Possible<MessageReferenceData> messageReference() {
            throw new UnsupportedOperationException();
        }

        public Possible<Integer> flags() {
            throw new UnsupportedOperationException();
        }

        public Possible<List<StickerData>> stickers() {
            throw new UnsupportedOperationException();
        }

        public Possible<List<PartialStickerData>> stickerItems() {
            throw new UnsupportedOperationException();
        }

        public Possible<Optional<MessageData>> referencedMessage() {
            throw new UnsupportedOperationException();
        }

        public Possible<MessageInteractionData> interaction() {
            throw new UnsupportedOperationException();
        }

        public Possible<List<ComponentData>> components() {
            throw new UnsupportedOperationException();
        }
    }
}
