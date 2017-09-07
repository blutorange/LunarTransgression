package com.github.blutorange.translune.ic;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.jdt.annotation.NonNull;
import org.quartz.Scheduler;

import com.github.blutorange.translune.LunarServletContextListener;
import com.github.blutorange.translune.db.IEntityManagerFactory;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.LunarDatabaseManager;
import com.github.blutorange.translune.gui.AbstractBean;
import com.github.blutorange.translune.gui.AdminBean;
import com.github.blutorange.translune.gui.ManageBean;
import com.github.blutorange.translune.gui.PlayerBean;
import com.github.blutorange.translune.gui.SessionBean;
import com.github.blutorange.translune.gui.StatusBean;
import com.github.blutorange.translune.handler.EHandlerFetchData;
import com.github.blutorange.translune.handler.EHandlerUpdateData;
import com.github.blutorange.translune.handler.HandlerAuthorize;
import com.github.blutorange.translune.handler.HandlerCancelBattlePreparation;
import com.github.blutorange.translune.handler.HandlerFetchData;
import com.github.blutorange.translune.handler.HandlerInvite;
import com.github.blutorange.translune.handler.HandlerInviteAccept;
import com.github.blutorange.translune.handler.HandlerInviteReject;
import com.github.blutorange.translune.handler.HandlerInviteRetract;
import com.github.blutorange.translune.handler.HandlerLoot;
import com.github.blutorange.translune.handler.HandlerPrepareBattle;
import com.github.blutorange.translune.handler.HandlerStepBattle;
import com.github.blutorange.translune.handler.HandlerUpdateData;
import com.github.blutorange.translune.i18n.ILocalizationBundle;
import com.github.blutorange.translune.i18n.LocalizationBundle;
import com.github.blutorange.translune.job.CleanInitId;
import com.github.blutorange.translune.job.SaveDb;
import com.github.blutorange.translune.logic.ABattleCommandHandler;
import com.github.blutorange.translune.logic.AExecutor;
import com.github.blutorange.translune.logic.BattleProcessing;
import com.github.blutorange.translune.logic.BattleRunner;
import com.github.blutorange.translune.logic.BattleStore;
import com.github.blutorange.translune.logic.IBattleProcessing;
import com.github.blutorange.translune.logic.IBattleRunner;
import com.github.blutorange.translune.logic.IInitIdStore;
import com.github.blutorange.translune.logic.IInvitationStore;
import com.github.blutorange.translune.logic.IRandomSupplier;
import com.github.blutorange.translune.logic.ISessionStore;
import com.github.blutorange.translune.logic.InitIdStore;
import com.github.blutorange.translune.logic.InvitationStore;
import com.github.blutorange.translune.logic.SessionStore;
import com.github.blutorange.translune.media.IImageProcessing;
import com.github.blutorange.translune.media.ImageProcessing;
import com.github.blutorange.translune.serial.IImportProcessing;
import com.github.blutorange.translune.serial.IJsoniter.IJsoniterSupplier;
import com.github.blutorange.translune.serial.ImportProcessing;
import com.github.blutorange.translune.servlet.BaseResourceServlet;
import com.github.blutorange.translune.servlet.BaseSpritesheetServlet;
import com.github.blutorange.translune.socket.ELunarMessageType;
import com.github.blutorange.translune.socket.ILunarMessageHandler;
import com.github.blutorange.translune.socket.ISocketProcessing;
import com.github.blutorange.translune.socket.LunarDecoder;
import com.github.blutorange.translune.socket.LunarEncoder;
import com.github.blutorange.translune.socket.LunarEndpoint;
import com.github.blutorange.translune.socket.SocketProcessing;

import dagger.Component;

@Singleton
@Component(modules={LoggingModule.class, LogicModule.class, SocketModule.class, StorageModule.class, DatabaseModule.class})
public interface LunarComponent {
	void inject(LunarEndpoint injectable);
	void inject(LunarDecoder injectable);
	void inject(LunarEncoder sessionBean);

	void inject(SessionBean sessionBean);
	void inject(AdminBean sessionBean);
	void inject(PlayerBean abstractBean);
	void inject(ManageBean abstractBean);
	void inject(StatusBean abstractBean);
	void inject(AbstractBean abstractBean);
	void inject(BaseResourceServlet baseResourceServlet);
	void inject(BaseSpritesheetServlet baseSpritesheetServlet);
	void inject(LunarServletContextListener lunarServletContextListener);

	void inject(EHandlerFetchData eFetchDataType);
	void inject(EHandlerUpdateData eUpdateDataType);

	void inject(LunarDatabaseManager lunarDatabaseManager);
	void inject(BattleProcessing battle);
	void inject(BattleStore battleStore);
	void inject(SaveDb jobSaveDb);
	void inject(CleanInitId cleanInitId);
	void inject(AExecutor executor);
	void inject(ABattleCommandHandler battleCommandHandler);

	@NonNull @LunarMessageTyped(ELunarMessageType.AUTHORIZE) ILunarMessageHandler ihandlerAuthorize();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE) ILunarMessageHandler ihandlerInvite();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_ACCEPT) ILunarMessageHandler ihandlerInviteAccept();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_RETRACT) ILunarMessageHandler ihandlerInviteRetract();
	@NonNull @LunarMessageTyped(ELunarMessageType.INVITE_REJECT) ILunarMessageHandler ihandlerInviteReject();
	@NonNull @LunarMessageTyped(ELunarMessageType.PREPARE_BATTLE) ILunarMessageHandler ihandlerPrepareBattle();
	@NonNull @LunarMessageTyped(ELunarMessageType.STEP_BATTLE) ILunarMessageHandler ihandlerStepBattle();
	@NonNull @LunarMessageTyped(ELunarMessageType.LOOT) ILunarMessageHandler ihandlerLoot();
	@NonNull @LunarMessageTyped(ELunarMessageType.FETCH_DATA) ILunarMessageHandler ihandlerFetchData();
	@NonNull @LunarMessageTyped(ELunarMessageType.UPDATE_DATA) ILunarMessageHandler ihandlerUpdateData();
	@NonNull @LunarMessageTyped(ELunarMessageType.CANCEL_BATTLE_PREPARATION) ILunarMessageHandler handlerCancelBattlePreparation();
	@NonNull ISocketProcessing iSocketProcessing();

	ILunarDatabaseManager iLunarDatabaseManager();
	IEntityManagerFactory entityManagerFactory();
	IImportProcessing iImportProcessing();
	IImageProcessing imageProcessing();

	IBattleRunner battleRunner();
	IBattleProcessing battleProcessing();

	ILocalizationBundle iLocalizationBundle();
	MimetypesFileTypeMap mimetypesFileTypeMap();
	@Named("default") Scheduler defaultScheduler();

	@NonNull @Named("secure") IRandomSupplier randomSecure();
	@NonNull @Named("basic") IRandomSupplier randomBasic();

	BattleStore battleStore();
	IInitIdStore initIdStore();
	ISessionStore sessionStore();
	IInvitationStore invitationStore();

	InitIdStore _initIdStore();
	SocketProcessing _socketProcessing();
	ImportProcessing _importProcessing();
	LocalizationBundle _localizationBundle();
	ImageProcessing _imageProcessing();
	BattleRunner _battleRunner();

	HandlerAuthorize _handlerAuthorize();
	HandlerInvite _handlerInvite();
	HandlerInviteAccept _handlerInviteAccept();
	HandlerInviteRetract _handlerInviteRetract();
	HandlerInviteReject _handlerInviteReject();
	HandlerPrepareBattle _handlerPrepareBattle();
	HandlerStepBattle _handlerStepBattle();
	HandlerLoot _handlerLoot();
	HandlerFetchData _handlerFetchData();
	HandlerUpdateData _handlerUpdateData();
	HandlerCancelBattlePreparation _handlerCancelBattlePreparation();

	SessionStore _sessionStore();
	InvitationStore _invitationStore();
	IJsoniterSupplier jsoniter();
}