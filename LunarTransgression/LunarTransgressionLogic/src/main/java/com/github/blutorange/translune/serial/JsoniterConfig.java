package com.github.blutorange.translune.serial;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.EBattleCommandType;
import com.github.blutorange.translune.logic.IComputedBattleStatus;
import com.github.blutorange.translune.message.MessageAuthorize;
import com.github.blutorange.translune.message.MessageAuthorizeResponse;
import com.github.blutorange.translune.message.MessageBattleCancelled;
import com.github.blutorange.translune.message.MessageBattlePrepared;
import com.github.blutorange.translune.message.MessageBattleStepped;
import com.github.blutorange.translune.message.MessageInvite;
import com.github.blutorange.translune.message.MessageInviteAccept;
import com.github.blutorange.translune.message.MessageInviteAcceptResponse;
import com.github.blutorange.translune.message.MessageInviteAccepted;
import com.github.blutorange.translune.message.MessageInviteReject;
import com.github.blutorange.translune.message.MessageInviteRejectResponse;
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
import com.github.blutorange.translune.socket.BattleAction;
import com.github.blutorange.translune.socket.BattleCommand;
import com.github.blutorange.translune.socket.BattleResult;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.LunarMessage;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import com.jsoniter.static_codegen.StaticCodegenConfig;

public class JsoniterConfig implements StaticCodegenConfig {
	@Override
	public void setup() {
		JsoniterSpi.registerTypeEncoder(ELunarMessageType.class, EJsoniterEncoder.ENUM_WITHOUT_CLASS);
        JsoniterSpi.registerTypeDecoder(ELunarMessageType.class, new JsoniterEnumDecoder<>(ELunarMessageType.class));

		JsoniterSpi.registerTypeEncoder(EBattleCommandType.class, EJsoniterEncoder.ENUM_WITHOUT_CLASS);
        JsoniterSpi.registerTypeDecoder(EBattleCommandType.class, new JsoniterEnumDecoder<>(EBattleCommandType.class));

        JsoniterSpi.registerTypeEncoder(IComputedBattleStatus.class, IComputedBattleStatus::encodeJson);

         JsoniterSpi.registerPropertyEncoder(LunarMessage.class, "status", new Encoder() {
			@Override
			public void encode(@Nullable final Object obj, @NonNull final JsonStream stream) throws IOException {
				final ELunarStatusCode status = obj == null ? ELunarStatusCode.OK : (ELunarStatusCode)obj;
				stream.writeVal(status.numerical);
			}
		});
        JsoniterSpi.registerPropertyDecoder(LunarMessage.class, "status", new Decoder() {
			@Override
			@Nullable
			public ELunarStatusCode decode(final JsonIterator iter) throws IOException {
				if (iter.whatIsNext() == ValueType.NULL) {
					iter.readNull();
					try {
						return ELunarStatusCode.OK;
					}
					catch (final IllegalArgumentException e) {
						return ELunarStatusCode.OK;
					}
				}
				return ELunarStatusCode.valueOf(iter.readInt());
			}
		});
	}

	@Override
	public TypeLiteral<?>[] whatToCodegen() {
		return new TypeLiteral<?>[]{
			TypeLiteral.create(LunarMessage.class),

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

			TypeLiteral.create(MessageInviteRetracted.class),
			TypeLiteral.create(MessageInvited.class),

			TypeLiteral.create(MessagePrepareBattle.class),
			TypeLiteral.create(MessagePrepareBattleResponse.class),

			TypeLiteral.create(MessageStepBattle.class),
			TypeLiteral.create(MessageStepBattleResponse.class),

			TypeLiteral.create(MessageBattlePrepared.class),
			TypeLiteral.create(MessageBattleStepped.class),
			TypeLiteral.create(MessageBattleCancelled.class),

			TypeLiteral.create(BattleAction.class),
			TypeLiteral.create(BattleCommand.class),
			TypeLiteral.create(BattleResult.class),

			TypeLiteral.create(MessageUnknown.class)
		};
	}
}