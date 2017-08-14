package com.github.blutorange.translune.ic;

import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.AdminBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.gui.SessionBean;
import com.github.blutorange.translune.handler.HandlerAuthorize;
import com.github.blutorange.translune.handler.HandlerInvite;
import com.github.blutorange.translune.handler.HandlerInviteAccept;
import com.github.blutorange.translune.handler.HandlerInviteReject;
import com.github.blutorange.translune.handler.HandlerInviteRetract;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;

public class InjectionUtil {
	public static void inject(final SessionBean sessionBean) {
		DaggerBeanComponent.create().inject(sessionBean);
	}

	public static void inject(final AdminBean adminBean) {
		DaggerBeanComponent.create().inject(adminBean);
	}

	public static void inject(final LunarDecoder lunarDecoder) {
		DaggerSocketComponent.create().inject(lunarDecoder);
	}

	public static void inject(final LunarEndpoint lunarEndpoint) {
		DaggerSocketComponent.create().inject(lunarEndpoint);
	}

	public static void inject(final LunarEncoder lunarEncoder) {
		DaggerSocketComponent.create().inject(lunarEncoder);
	}

	public static void inject(final AbstractBean abstractBean) {
		DaggerBeanComponent.create().inject(abstractBean);
	}

	public static void inject(final PlayerBean playerBean) {
		DaggerBeanComponent.create().inject(playerBean);
	}

	public static void inject(final HandlerAuthorize handler) {
		DaggerSocketComponent.create().inject(handler);
	}

	public static void inject(final HandlerInvite handler) {
		DaggerSocketComponent.create().inject(handler);
	}

	public static void inject(final HandlerInviteAccept handler) {
		DaggerSocketComponent.create().inject(handler);
	}

	public static void inject(final HandlerInviteRetract handler) {
		DaggerSocketComponent.create().inject(handler);
	}

	public static void inject(final HandlerInviteReject handler) {
		DaggerSocketComponent.create().inject(handler);
	}
}
