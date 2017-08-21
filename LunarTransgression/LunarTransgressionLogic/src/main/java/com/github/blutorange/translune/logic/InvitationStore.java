package com.github.blutorange.translune.logic;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.message.MessageInvite;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@Singleton
public class InvitationStore implements IInvitationStore {
	Table<String, String, MessageInvite> table;

	@Inject
	public InvitationStore() {
		table = HashBasedTable.create();
	}

	@Override
	public void add(final String nicknameFrom, final MessageInvite invitation) {
		synchronized (table) {
			table.put(nicknameFrom, invitation.getNickname(), invitation);
		}
	}

	@Nullable
	@Override
	public MessageInvite remove(final String from, final String to) {
		synchronized (table) {
			return table.remove(from, to);
		}
	}

	@Override
	public void removeAllFrom(@NonNull final String from) {
		synchronized (table) {
			table.row(from).clear();
		}
	}

	@Override
	public void removeAllTo(@NonNull final String to) {
		synchronized (table) {
			table.column(to).clear();
		}
	}

	@Override
	public void removeAllWith(@NonNull final String nickname) {
		synchronized (table) {
			table.row(nickname).clear();
			table.column(nickname).clear();
		}
	}

	@Nullable
	@Override
	public MessageInvite retrieve(final String from, @NonNull final String to) {
		synchronized (table) {
			return table.get(from, to);
		}
	}
}