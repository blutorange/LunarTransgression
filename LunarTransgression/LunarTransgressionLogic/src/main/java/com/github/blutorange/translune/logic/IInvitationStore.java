package com.github.blutorange.translune.logic;

import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.message.MessageInvite;

public interface IInvitationStore {
	void add(String nicknameFrom, MessageInvite invitation);
	@Nullable
	MessageInvite remove(String from, String to);
	void removeAllFrom(String from);
	void removeAllTo(String to);
	void removeAllWith(String nickname);
	@Nullable
	MessageInvite retrieve(String from, String to);
}