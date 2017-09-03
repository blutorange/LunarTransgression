package com.github.blutorange.translune.ic;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.LunarServletContextListener;
import com.github.blutorange.translune.db.LunarDatabaseManager;
import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.ManageBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.gui.StatusBean;
import com.github.blutorange.translune.handler.HandlerAuthorize;
import com.github.blutorange.translune.handler.HandlerFetchData;
import com.github.blutorange.translune.handler.HandlerInvite;
import com.github.blutorange.translune.handler.HandlerInviteAccept;
import com.github.blutorange.translune.handler.HandlerInviteReject;
import com.github.blutorange.translune.handler.HandlerInviteRetract;
import com.github.blutorange.translune.handler.HandlerLoot;
import com.github.blutorange.translune.handler.HandlerPrepareBattle;
import com.github.blutorange.translune.handler.HandlerStepBattle;
import com.github.blutorange.translune.handler.HandlerUpdateData;
import com.github.blutorange.translune.job.CleanInitId;
import com.github.blutorange.translune.logic.BattleRunner;
import com.github.blutorange.translune.logic.BattleStore;
import com.github.blutorange.translune.media.ImageProcessing;
import com.github.blutorange.translune.serial.ImportProcessing;
import com.github.blutorange.translune.servlet.BaseResourceServlet;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;
import com.github.blutorange.translune.socket.LunarMessage;
import com.github.blutorange.translune.socket.SocketProcessing;
import com.github.blutorange.translune.util.CustomProperties;

import dagger.Module;
import dagger.Provides;

@Module
public class LoggingModule {
	@Provides @Singleton @Classed(LunarMessage.class) static Logger provideLogger1() {
		return provideLogger(LunarMessage.class);
	}

	@Provides @Singleton @Classed(LunarDatabaseManager.class) static Logger provideLogger3() {
		return provideLogger(LunarDatabaseManager.class);
	}

	@Provides @Singleton @Classed(LunarEncoder.class) static Logger provideLogger4() {
		return provideLogger(LunarEncoder.class);
	}

	@Provides @Singleton @Classed(LunarDecoder.class) static Logger provideLogger5() {
		return provideLogger(LunarDecoder.class);
	}

	@Provides @Singleton @Classed(LunarEndpoint.class) static Logger provideLogger6() {
		return provideLogger(LunarEndpoint.class);
	}

	@Provides @Singleton @Classed(CustomProperties.class) static Logger provideLogger7() {
		return provideLogger(CustomProperties.class);
	}

	@Provides @Singleton @Classed(DatabaseModule.class) static Logger provideLogger9() {
		return provideLogger(DatabaseModule.class);
	}

	@Provides @Singleton @Classed(AbstractBean.class) static Logger provideLogger10() {
		return provideLogger(AbstractBean.class);
	}

	@Provides @Singleton @Classed(PlayerBean.class) static Logger provideLogger11() {
		return provideLogger(PlayerBean.class);
	}

	@Provides @Singleton @Classed(HandlerAuthorize.class) static Logger provideLogger12() {
		return provideLogger(HandlerAuthorize.class);
	}

	@Provides @Singleton @Classed(HandlerInvite.class) static Logger provideLogger13() {
		return provideLogger(HandlerInvite.class);
	}

	@Provides @Singleton @Classed(SocketProcessing.class) static Logger provideLogger14() {
		return provideLogger(SocketProcessing.class);
	}

	@Provides @Singleton @Classed(BattleStore.class) static Logger provideLogger15() {
		return provideLogger(BattleStore.class);
	}

	@Provides @Singleton @Classed(HandlerInviteAccept.class) static Logger provideLogger16() {
		return provideLogger(HandlerInviteAccept.class);
	}

	@Provides @Singleton @Classed(HandlerInviteRetract.class) static Logger provideLogger17() {
		return provideLogger(HandlerInviteRetract.class);
	}

	@Provides @Singleton @Classed(HandlerInviteReject.class) static Logger provideLogger18() {
		return provideLogger(HandlerInviteReject.class);
	}

	@Provides @Singleton @Classed(BattleRunner.class) static Logger provideLogger19() {
		return provideLogger(BattleRunner.class);
	}

	@Provides @Singleton @Classed(HandlerPrepareBattle.class) static Logger provideLogger20() {
		return provideLogger(HandlerPrepareBattle.class);
	}

	@Provides @Singleton @Classed(HandlerStepBattle.class) static Logger provideLogger21() {
		return provideLogger(HandlerStepBattle.class);
	}

	@Provides @Singleton @Classed(HandlerLoot.class) static Logger provideLogger22() {
		return provideLogger(HandlerLoot.class);
	}

	@Provides @Singleton @Classed(LogicModule.class) static Logger provideLogger23() {
		return provideLogger(LogicModule.class);
	}

	@Provides @Singleton @Classed(ManageBean.class) static Logger provideLogger24() {
		return provideLogger(ManageBean.class);
	}

	@Provides @Singleton @Classed(ImportProcessing.class) static Logger provideLogger25() {
		return provideLogger(ImportProcessing.class);
	}

	@Provides @Singleton @Classed(StatusBean.class) static Logger provideLogger26() {
		return provideLogger(StatusBean.class);
	}

	@Provides @Singleton @Classed(BaseResourceServlet.class) static Logger provideLogger27() {
		return provideLogger(BaseResourceServlet.class);
	}

	@Provides @Singleton @Classed(LunarServletContextListener.class) static Logger provideLogger28() {
		return provideLogger(LunarServletContextListener.class);
	}

	@Provides @Singleton @Classed(ImageProcessing.class) static Logger provideLogger29() {
		return provideLogger(ImageProcessing.class);
	}

	@Provides @Singleton @Classed(HandlerFetchData.class) static Logger provideLogger30() {
		return provideLogger(HandlerFetchData.class);
	}

	@Provides @Singleton @Classed(CleanInitId.class) static Logger provideLogger31() {
		return provideLogger(CleanInitId.class);
	}

	@Provides @Singleton @Classed(HandlerUpdateData.class) static Logger provideLogger32() {
		return provideLogger(HandlerUpdateData.class);
	}

	private static Logger provideLogger(final Class<?> clazz) {
		return LoggerFactory.getLogger(clazz);
	}
}