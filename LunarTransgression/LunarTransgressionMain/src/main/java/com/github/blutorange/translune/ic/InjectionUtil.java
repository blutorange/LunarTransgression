package com.github.blutorange.translune.ic;

import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.AdminBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.gui.SessionBean;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;
import com.github.blutorange.translune.socket.TestEndpoint;

public class InjectionUtil {
	public static void inject(final SessionBean sessionBean) {
		DaggerDatabaseComponent.create().inject(sessionBean);
	}

	public static void inject(final AdminBean adminBean) {
		DaggerDatabaseComponent.create().inject(adminBean);
	}

	public static void inject(final LunarDecoder lunarDecoder) {
		DaggerLoggingComponent.create().inject(lunarDecoder);
	}

	public static void inject(final LunarEndpoint lunarEndpoint) {
		DaggerLoggingComponent.create().inject(lunarEndpoint);
	}

	public static void inject(final LunarEncoder lunarEncoder) {
		DaggerLoggingComponent.create().inject(lunarEncoder);
	}

	public static void inject(final TestEndpoint testEndpoint) {
		DaggerLoggingComponent.create().inject(testEndpoint);
	}

	public static void inject(final AbstractBean abstractBean) {
		DaggerLoggingComponent.create().inject(abstractBean);
	}

	public static void inject(final PlayerBean playerBean) {
		DaggerLoggingComponent.create().inject(playerBean);
	}
}
