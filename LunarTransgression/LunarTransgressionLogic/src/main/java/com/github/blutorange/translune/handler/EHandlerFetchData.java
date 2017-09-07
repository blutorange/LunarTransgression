package com.github.blutorange.translune.handler;

import java.io.IOException;

import javax.inject.Inject;
import javax.websocket.Session;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Player;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.logic.Pageable;
import com.github.blutorange.translune.serial.IImportProcessing;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.github.blutorange.translune.serial.PlayerViewInvite;
import com.github.blutorange.translune.socket.ISocketProcessing;

public enum EHandlerFetchData {
	NONE {
		@Nullable
		@Override
		public Object fetch(final Session session, final String details) {
			return null;
		}
	},
	USER_PLAYER {
		@Nullable
		@Override
		public Object fetch(final Session session, final String details) {
			return databaseManager.find(Player.class, socketProcessing.getNickname(session));
		}
	},
	AVAILABLE_BG_AND_BGM {
		@Nullable
		@Override
		public Object fetch(final Session session, final String details) throws IOException {
			return importProcessing.availableBgAndBgm();
		}
	},
	PLAYER_DETAIL {
		@Override
		@Nullable
		public Object fetch(final Session session, final String details) throws Exception {
			final Player player = databaseManager.find(Player.class, details);
			if (player == null)
				return null;
			return new PlayerViewInvite(player);
		}
	},
	ACTIVE_PLAYER {
		@Override
		@Nullable
		public Object fetch(final Session session, final String details) throws Exception {
			final Pageable pageable = jsoniter.get().deserialize(details, Pageable.class);
			if (pageable == null)
				return null;
			return ComponentFactory.getLunarComponent().sessionStore().findNicknames(pageable);
//			// not what we want start ...
//			final List<String> list = databaseManager.withEm(false, em -> {
//				final CriteriaBuilder cb = em.getCriteriaBuilder();
//				final CriteriaQuery<String> cq = cb.createQuery(String.class);
//				final Root<Player> root = cq.from(Player.class);
//				final TypedQuery<String> tq = em.createQuery(cq
//						.orderBy(pageable.toOrders(root, cb))
//						.select(cb.construct(String.class, root.get(Player_.nickname)))
//				);
//				tq.setFirstResult(pageable.getOffset());
//				tq.setMaxResults(pageable.getCount());
//				return tq.getResultList();
//			});
//			// end
//			if (list == null)
//				return null;
//			return new PageableResult(0, 0, list.toArray(new Player[0]));
		}
	},

	OPEN_INVITATIONS(){
		@Nullable
		@Override
		public Object fetch(final Session session, final String details) throws Exception {
			return invitationStore.removeAllTo(socketProcessing.getNickname(session))
					.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
		}
	};

	@Inject
	ISessionStore sessionStore;

	@Inject
	IInvitationStore invitationStore;

	@Inject
	IImportProcessing importProcessing;

	@Inject
	IJsoniterSupplier jsoniter;

	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	ISocketProcessing socketProcessing;

	private EHandlerFetchData() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Nullable
	public abstract Object fetch(final Session session, final String details) throws Exception;
}