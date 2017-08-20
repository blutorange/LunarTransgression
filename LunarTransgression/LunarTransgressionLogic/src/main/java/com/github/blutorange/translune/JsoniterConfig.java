package com.github.blutorange.translune;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.logic.BattleAction;
import com.github.blutorange.translune.logic.BattleCommand;
import com.github.blutorange.translune.logic.IBattleAction;
import com.github.blutorange.translune.logic.IBattleCommand;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ELunarStatusCode;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.socket.message.MessageAuthorize;
import com.github.blutorange.translune.socket.message.MessageAuthorizeResponse;
import com.github.blutorange.translune.socket.message.MessageBattleCancelled;
import com.github.blutorange.translune.socket.message.MessageBattlePrepared;
import com.github.blutorange.translune.socket.message.MessageBattleStepped;
import com.github.blutorange.translune.socket.message.MessageInvite;
import com.github.blutorange.translune.socket.message.MessageInviteAccept;
import com.github.blutorange.translune.socket.message.MessageInviteAcceptResponse;
import com.github.blutorange.translune.socket.message.MessageInviteAccepted;
import com.github.blutorange.translune.socket.message.MessageInviteReject;
import com.github.blutorange.translune.socket.message.MessageInviteRejectResponse;
import com.github.blutorange.translune.socket.message.MessageInviteResponse;
import com.github.blutorange.translune.socket.message.MessageInviteRetract;
import com.github.blutorange.translune.socket.message.MessageInviteRetractResponse;
import com.github.blutorange.translune.socket.message.MessageInviteRetracted;
import com.github.blutorange.translune.socket.message.MessageInvited;
import com.github.blutorange.translune.socket.message.MessagePrepareBattle;
import com.github.blutorange.translune.socket.message.MessagePrepareBattleResponse;
import com.github.blutorange.translune.socket.message.MessageStepBattle;
import com.github.blutorange.translune.socket.message.MessageStepBattleResponse;
import com.github.blutorange.translune.socket.message.MessageUnknown;
import com.github.blutorange.translune.util.EJsoniterEncoder;
import com.github.blutorange.translune.util.JsoniterDelegateDecoder;
import com.github.blutorange.translune.util.JsoniterEnumDecoder;
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
		JsoniterSpi.registerPropertyEncoder(LunarMessage.class, "type", EJsoniterEncoder.ENUM_WITHOUT_CLASS);
        JsoniterSpi.registerPropertyDecoder(LunarMessage.class, "type", new JsoniterEnumDecoder<>(ELunarMessageType.class));

        JsoniterSpi.registerTypeEncoder(IBattleAction.class, EJsoniterEncoder.THROUGHPASS);
        JsoniterSpi.registerTypeDecoder(IBattleAction.class, new JsoniterDelegateDecoder(BattleAction[].class));
        JsoniterSpi.registerTypeDecoder(IBattleCommand.class, new JsoniterDelegateDecoder(BattleCommand[].class));

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

			TypeLiteral.create(MessageUnknown.class)
		};
	}
}