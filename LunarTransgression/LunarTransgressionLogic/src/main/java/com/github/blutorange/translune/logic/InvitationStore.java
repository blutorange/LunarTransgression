package com.github.blutorange.translune.logic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	public Set<String> removeAllFrom(@NonNull final String from) {
		synchronized (table) {
			final Map<String,MessageInvite> row = table.row(from);
			final Set<String> setFrom = new HashSet<>(row.keySet());
			row.clear();
			return setFrom;
		}
	}

	@Override
	public Set<String> removeAllTo(@NonNull final String to) {
		synchronized (table) {
			final Map<String,MessageInvite> column = table.column(to);
			final Set<String> setFrom = new HashSet<>(column.keySet());
			column.clear();
			return setFrom;
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