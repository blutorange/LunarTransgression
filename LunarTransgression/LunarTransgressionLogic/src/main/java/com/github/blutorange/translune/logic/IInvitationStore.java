package com.github.blutorange.translune.logic;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.message.MessageInvite;

public interface IInvitationStore {
	void add(String nicknameFrom, MessageInvite invitation);
	@Nullable
	MessageInvite remove(String from, String to);
	Set<@NonNull String> removeAllFrom(String from);
	Set<@NonNull String> removeAllTo(String to);
	void removeAllWith(String nickname);
	@Nullable
	MessageInvite retrieve(String from, String to);
	Set<String> getAllTo(String to);
}