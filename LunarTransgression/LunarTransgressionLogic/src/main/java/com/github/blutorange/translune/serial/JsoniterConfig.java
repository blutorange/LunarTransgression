package com.github.blutorange.translune.serial;

import org.eclipse.jdt.annotation.NonNull;

import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.EStatusValue;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.handler.EHandlerFetchData;
import com.github.blutorange.translune.handler.EHandlerUpdateData;
import com.github.blutorange.translune.logic.CharacterStatsDelta;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EBattleCommandType;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.EExperienceGroup;
import com.github.blutorange.translune.logic.ENature;
import com.github.blutorange.translune.logic.EOrderDirection;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.EStatusCondition;
import com.github.blutorange.translune.logic.IBattleStatus;
import com.github.blutorange.translune.logic.IComputedBattleStatus;
import com.github.blutorange.translune.logic.Orderable;
import com.github.blutorange.translune.logic.Pageable;
import com.github.blutorange.translune.logic.PageableResult;
import com.github.blutorange.translune.message.MessageAuthorize;
import com.github.blutorange.translune.message.MessageAuthorizeResponse;
import com.github.blutorange.translune.message.MessageBattleCancelled;
import com.github.blutorange.translune.message.MessageBattleEnded;
import com.github.blutorange.translune.message.MessageBattlePreparationCancelled;
import com.github.blutorange.translune.message.MessageBattlePrepared;
import com.github.blutorange.translune.message.MessageBattleStepped;
import com.github.blutorange.translune.message.MessageCancelBattlePreparation;
import com.github.blutorange.translune.message.MessageCancelBattlePreparationResponse;
import com.github.blutorange.translune.message.MessageFetchData;
import com.github.blutorange.translune.message.MessageFetchDataResponse;
import com.github.blutorange.translune.message.MessageInvite;
import com.github.blutorange.translune.message.MessageInviteAccept;
import com.github.blutorange.translune.message.MessageInviteAcceptResponse;
import com.github.blutorange.translune.message.MessageInviteAccepted;
import com.github.blutorange.translune.message.MessageInviteReject;
import com.github.blutorange.translune.message.MessageInviteRejectResponse;
import com.github.blutorange.translune.message.MessageInviteRejected;
import com.github.blutorange.translune.message.MessageInviteResponse;
import com.github.blutorange.translune.message.MessageInviteRetract;
import com.github.blutorange.translune.message.MessageInviteRetractResponse;
import com.github.blutorange.translune.message.MessageInviteRetracted;
import com.github.blutorange.translune.message.MessageInvited;
import com.github.blutorange.translune.message.MessageLoot;
import com.github.blutorange.translune.message.MessageLootResponse;
import com.github.blutorange.translune.message.MessagePrepareBattle;
import com.github.blutorange.translune.message.MessagePrepareBattleResponse;
import com.github.blutorange.translune.message.MessageReleaseCharacter;
import com.github.blutorange.translune.message.MessageReleaseCharacterResponse;
import com.github.blutorange.translune.message.MessageStepBattle;
import com.github.blutorange.translune.message.MessageStepBattleResponse;
import com.github.blutorange.translune.message.MessageUnknown;
import com.github.blutorange.translune.message.MessageUpdateData;
import com.github.blutorange.translune.message.MessageUpdateDataResponse;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import com.jsoniter.static_codegen.StaticCodegenConfig;

public class JsoniterConfig implements StaticCodegenConfig {

	public static void doSetup() {
		final Config.Builder builder = new Config.Builder().decodingMode(DecodingMode.STATIC_MODE)
				.encodingMode(EncodingMode.STATIC_MODE).escapeUnicode(true).omitDefaultValue(false);

		JsoniterSpi.setCurrentConfig(new JsoniterConfigSanitized(builder));

		registerEnumMapKey(EStatusValue.class);
		registerEnum(EActionTarget.class);
		registerEnum(EBattleCommandType.class);
		registerEnum(EElement.class);
		registerEnum(EExperienceGroup.class);
		registerEnum(EHandlerFetchData.class);
		registerEnum(EHandlerUpdateData.class);
		registerEnum(ELunarMessageType.class);
		registerEnum(ENature.class);
		registerEnum(ESkillEffect.class);
		registerEnum(EStatusValue.class);
		registerEnum(EStatusCondition.class);
		registerEnum(EOrderDirection.class);

		JsoniterSpi.registerTypeEncoder(JsoniterEmptyMap.class, new EmptyMapEncoder());
		JsoniterSpi.registerTypeDecoder(JsoniterEmptyMap.class, new EmptyMapDecoder());
		JsoniterSpi.registerTypeEncoder(IComputedBattleStatus.class, IComputedBattleStatus::encodeJson);
		JsoniterSpi.registerTypeEncoder(IBattleStatus.class, IBattleStatus::encodeJson);

		JsoniterSpi.registerPropertyEncoder(LunarMessage.class, "status", new LunarStatusEncoder());
		JsoniterSpi.registerPropertyDecoder(LunarMessage.class, "status", new LunarStatusDecoder());
		JsoniterSpi.registerPropertyEncoder(Character.class, "skills", new SkillEncoder());
		JsoniterSpi.registerPropertyEncoder(CharacterViewBattle.class, "skills", new SkillEncoder());
	}

	private static void registerMapKey(final Class<@NonNull Skill> clazz) {
		JsoniterSpi.registerMapKeyEncoder(clazz, EMapKeyEncoder.GENERIC_WITHOUT_CLASS);
		JsoniterSpi.registerMapKeyDecoder(clazz, new GenericMapKeyDecoder<>(clazz));
	}

	private static void registerEnumMapKey(final Class<@NonNull EStatusValue> clazz) {
		JsoniterSpi.registerMapKeyEncoder(clazz, EMapKeyEncoder.ENUM_WITHOUT_CLASS);
		JsoniterSpi.registerMapKeyDecoder(clazz, new JsoniterEnumMapKeyDecoder<>(clazz));
	}

	private static <E extends Enum<E>> void registerEnum(final Class<@NonNull E> clazz) {
		JsoniterSpi.registerTypeEncoder(clazz, EJsoniterEncoder.ENUM_WITHOUT_CLASS);
		JsoniterSpi.registerTypeDecoder(clazz, new JsoniterEnumDecoder<>(clazz));
	}

	@Override
	public void setup() {
		doSetup();
	}

	@Override
	public TypeLiteral<?>[] whatToCodegen() {
		return new TypeLiteral<?>[] {
				TypeLiteral.create(Player.class),
				TypeLiteral.create(Item.class),
				TypeLiteral.create(CharacterState.class),
				TypeLiteral.create(Character.class),
				TypeLiteral.create(Skill.class),

				TypeLiteral.create(PlayerViewInvite.class),
				TypeLiteral.create(PlayerViewBattle.class),
				TypeLiteral.create(CharacterViewInvite.class),
				TypeLiteral.create(CharacterViewBattle.class),
				TypeLiteral.create(CharacterStateViewLoot.class),
				TypeLiteral.create(ItemViewLoot.class),

				TypeLiteral.create(LunarMessage.class),
				TypeLiteral.create(BattleAction.class),
				TypeLiteral.create(BattleCommand.class),
				TypeLiteral.create(BattleResult.class),
				TypeLiteral.create(AvailableBgAndBgm.class),
				TypeLiteral.create(Filterable.class),
				TypeLiteral.create(Orderable.class),
				TypeLiteral.create(Pageable.class),
				TypeLiteral.create(PageableResult.class),
				TypeLiteral.create(CharacterStatsDelta.class),
				TypeLiteral.create(LootableStuff.class),

				TypeLiteral.create(MessageReleaseCharacter.class),
				TypeLiteral.create(MessageReleaseCharacterResponse.class),

				TypeLiteral.create(MessageFetchData.class),
				TypeLiteral.create(MessageFetchDataResponse.class),

				TypeLiteral.create(MessageUpdateData.class),
				TypeLiteral.create(MessageUpdateDataResponse.class),

				TypeLiteral.create(MessageAuthorize.class),
				TypeLiteral.create(MessageAuthorizeResponse.class),

				TypeLiteral.create(MessageInvite.class),
				TypeLiteral.create(MessageInviteResponse.class),

				TypeLiteral.create(MessageInviteAccept.class),
				TypeLiteral.create(MessageInviteAcceptResponse.class),
				TypeLiteral.create(MessageInviteAccepted.class),

				TypeLiteral.create(MessageInviteReject.class),
				TypeLiteral.create(MessageInviteRejectResponse.class),

				TypeLiteral.create(MessageInviteRetract.class),
				TypeLiteral.create(MessageInviteRetractResponse.class),

				TypeLiteral.create(MessageInvited.class),
				TypeLiteral.create(MessageInviteRetracted.class),
				TypeLiteral.create(MessageInviteRejected.class),

				TypeLiteral.create(MessageBattlePreparationCancelled.class),
				TypeLiteral.create(MessageCancelBattlePreparation.class),
				TypeLiteral.create(MessageCancelBattlePreparationResponse.class),

				TypeLiteral.create(MessagePrepareBattle.class),
				TypeLiteral.create(MessagePrepareBattleResponse.class),

				TypeLiteral.create(MessageStepBattle.class),
				TypeLiteral.create(MessageStepBattleResponse.class),

				TypeLiteral.create(MessageLoot.class),
				TypeLiteral.create(MessageLootResponse.class),

				TypeLiteral.create(MessageBattlePrepared.class),
				TypeLiteral.create(MessageBattleStepped.class),
				TypeLiteral.create(MessageBattleCancelled.class),
				TypeLiteral.create(MessageBattleEnded.class),

				TypeLiteral.create(MessageUnknown.class) };
	}

	private static class JsoniterConfigSanitized extends Config {
		public JsoniterConfigSanitized(final Builder builder) {
			super("jsoniter_codegen.cfg.", builder);
		}
	}
}