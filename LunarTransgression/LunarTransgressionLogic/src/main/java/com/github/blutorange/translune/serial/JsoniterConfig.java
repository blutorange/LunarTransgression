package com.github.blutorange.translune.serial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.Character;
import com.github.blutorange.translune.db.CharacterState;
import com.github.blutorange.translune.db.EStatusValue;
import com.github.blutorange.translune.db.Item;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.db.Skill;
import com.github.blutorange.translune.logic.EActionTarget;
import com.github.blutorange.translune.logic.EBattleCommandType;
import com.github.blutorange.translune.logic.EElement;
import com.github.blutorange.translune.logic.EExperienceGroup;
import com.github.blutorange.translune.logic.ENature;
import com.github.blutorange.translune.logic.EOrderDirection;
import com.github.blutorange.translune.logic.ESkillEffect;
import com.github.blutorange.translune.logic.EStatusCondition;
import com.github.blutorange.translune.logic.IComputedBattleStatus;
import com.github.blutorange.translune.logic.Orderable;
import com.github.blutorange.translune.logic.Pageable;
import com.github.blutorange.translune.logic.PageableResult;
import com.github.blutorange.translune.message.EFetchDataType;
import com.github.blutorange.translune.message.EUpdateDataType;
import com.github.blutorange.translune.message.MessageAuthorize;
import com.github.blutorange.translune.message.MessageAuthorizeResponse;
import com.github.blutorange.translune.message.MessageBattleCancelled;
import com.github.blutorange.translune.message.MessageBattleEnded;
import com.github.blutorange.translune.message.MessageBattlePrepared;
import com.github.blutorange.translune.message.MessageBattleStepped;
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
import com.github.blutorange.translune.message.MessagePrepareBattle;
import com.github.blutorange.translune.message.MessagePrepareBattleResponse;
import com.github.blutorange.translune.message.MessageStepBattle;
import com.github.blutorange.translune.message.MessageStepBattleResponse;
import com.github.blutorange.translune.message.MessageUnknown;
import com.github.blutorange.translune.message.MessageUpdateData;
import com.github.blutorange.translune.message.MessageUpdateDataResponse;
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.Encoder;
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
		registerEnum(EFetchDataType.class);
		registerEnum(EUpdateDataType.class);
		registerEnum(ELunarMessageType.class);
		registerEnum(ENature.class);
		registerEnum(ESkillEffect.class);
		registerEnum(EStatusValue.class);
		registerEnum(EStatusCondition.class);
		registerEnum(EOrderDirection.class);

		JsoniterSpi.registerTypeEncoder(JsoniterEmptyMap.class, new Encoder() {
			@Override
			public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
				if (obj == null)
					stream.writeNull();
				else
					stream.writeEmptyObject();
			}
		});
		JsoniterSpi.registerTypeDecoder(JsoniterEmptyMap.class, new Decoder() {
			@Override
			public @Nullable Object decode(@NonNull final JsonIterator iter) throws IOException {
				if (iter.whatIsNext() == ValueType.NULL) {
					iter.readNull();
					return null;
				}
				iter.readObject();
				iter.readObject();
				return JsoniterCollections.EMPTY_MAP;
			}
		});

		JsoniterSpi.registerTypeEncoder(IComputedBattleStatus.class, IComputedBattleStatus::encodeJson);
		JsoniterSpi.registerPropertyEncoder(LunarMessage.class, "status", new Encoder() {
			@Override
			public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
				final ELunarStatusCode status = obj == null ? ELunarStatusCode.OK : (ELunarStatusCode) obj;
				stream.writeVal(status.numerical);
			}
		});
		JsoniterSpi.registerPropertyDecoder(LunarMessage.class, "status", new Decoder() {
			@Override
			@Nullable
			public ELunarStatusCode decode(final JsonIterator iter) throws IOException {
				if (iter.whatIsNext() == ValueType.NULL) {
					iter.readNull();
					return ELunarStatusCode.OK;
				}
				try {
					return ELunarStatusCode.valueOf(iter.readInt());
				}
				catch (final IllegalArgumentException e) {
					return ELunarStatusCode.OK;
				}
			}
		});

		JsoniterSpi.registerPropertyEncoder(Character.class, "skills", new Encoder(){
			@Override
			public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
				if (obj == null)
					stream.writeEmptyObject();
				else {
					final Map<Integer, List<Skill>> remap = new HashMap<>();
					((Map<Skill, Integer>)obj).forEach((skill,level) -> remap.computeIfAbsent(level, x -> new ArrayList<>()).add(skill));
					stream.writeObjectStart();
					for (final Iterator<Entry<Integer,List<Skill>>> it = remap.entrySet().iterator(); it.hasNext();) {
						final Entry<Integer,List<Skill>> entry = it.next();
						stream.writeObjectField(entry.getKey().toString());
						stream.writeArrayStart();
						for (final Iterator<Skill> listIterator = entry.getValue().iterator(); listIterator.hasNext();) {
							stream.writeVal(listIterator.next());
							if (listIterator.hasNext())
								stream.writeMore();
						}
						stream.writeArrayEnd();
						if (it.hasNext())
							stream.writeMore();
					}
					stream.writeObjectEnd();
				}
			}
		});
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
				TypeLiteral.create(CharacterViewInvite.class),

				TypeLiteral.create(LunarMessage.class),
				TypeLiteral.create(BattleAction.class),
				TypeLiteral.create(BattleCommand.class),
				TypeLiteral.create(BattleResult.class),
				TypeLiteral.create(AvailableBgAndBgm.class),
				TypeLiteral.create(Filterable.class),
				TypeLiteral.create(Orderable.class),
				TypeLiteral.create(Pageable.class),
				TypeLiteral.create(PageableResult.class),

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

				TypeLiteral.create(MessagePrepareBattle.class),
				TypeLiteral.create(MessagePrepareBattleResponse.class),

				TypeLiteral.create(MessageStepBattle.class),
				TypeLiteral.create(MessageStepBattleResponse.class),

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