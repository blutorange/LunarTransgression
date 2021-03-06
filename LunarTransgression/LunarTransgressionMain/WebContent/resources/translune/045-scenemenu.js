(function(Lunar, window, undefined) {
	/**
	 * The main menu.
	 */
	Lunar.Scene.Menu = class extends Lunar.Scene.Base {
		constructor(game) {
			super(game);
			this._invitations = [];
			this._loaded = false;
			this._tabState = undefined;
			this._tabScene = undefined;
			this._tabDetails = undefined;
			this._loadScene = undefined;
			this._player = undefined;
			this._inviteConfirm = undefined;
		}
		
		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				 fetch: Lunar.FetchType.availableBgAndBgm
			});
			const requestLoadableInvitation = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				 fetch: Lunar.FetchType.openInvitations
			});
			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, requestLoadableInvitation, delegateLoadable);
			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);
			
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			requestLoadableInvitation.promise.then(response => this._invitations = response.data.data);
			requestLoadable.promise.then(response => _this._initLoader(delegateLoadable, response));
						
			return this._loadScene;
		}
		
		destroy() {
			this.game.loaderFor("menu").reset();
			this._player = undefined;
			this._inviteConfirm = undefined;
			this._battlePrep = undefined;
			this._loadScene = undefined;
			this._tabScene = undefined;
			this._tabDetails = undefined;
			this._invitations = [];
			super.destroy();
		}
		
		onAdd() {
			super.onAdd();
			this._onClickChar();
			this.game.net.registerMessageHandler(Lunar.Message.invited, {
				handle: this.method('_onMessageInvited'),
				error: () => null
			});
			this.game.net.registerMessageHandler(Lunar.Message.inviteRetracted, {
				handle: this.method('_onMessageInviteRetracted'),
				error: () => null
			});
			this._updateInvitations();
		}
		
		onRemove() {
			this.game.net.removeMessageHandlers(Lunar.Message.invited);
			this.game.net.removeMessageHandlers(Lunar.Message.inviteRetracted);
			this.game.net.removeMessageHandlers(Lunar.Message.battlePreparationCancelled);
			this.game.net.removeMessageHandlers(Lunar.Message.battlePrepared);
			this.game.removeScene(this._battlePrep);
			this.game.removeScene(this._loadScene);
			this.game.removeScene(this._tabScene);
			this.game.removeScene(this._tabDetails);
			this.game.removeScene(this._inviteConfirm);
			super.onRemove();
		}
		
		update(delta) {
			if (this._invitations.length !== 0) {
				this.hierarchy.side.$battle.rotation = Math.sin(Lunar.Interpolation.slowInSlowOut(Lunar.Interpolation.backAndForth(2*this.time%1))*0.3+0.1);
			}
			super.update(delta);
		}
		
		set player(player) {
			this._player = player;
		}
		
		get player() {
			return this._player;
		}
			
		/**
		 * @private
		 */
		_initLoader(delegateLoadable, response) {		
			const background = Lunar.Object.randomEntry(response.data.data.bgMenu).value;
			const music = Lunar.Object.randomEntry(response.data.data.bgmMenu).value;
			
			this.game.switchBgm(music.map(file => `resource/${file}`));
			
			const loader = this.game.loaderFor("menu");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable; 
			loader.reset();
			loader.add("bg", `resource/${background}`);			
			loader.load();
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			const geoRoot = Lunar.Geometry.layoutHbox({
				box: {
					w: this.game.w,
					h: this.game.h
				},
				dimension: [7, 93]
			});
			const geoCenter = Lunar.Geometry.layoutVbox({
				box: geoRoot[1],
				dimension: [15, 70, 15]
			});
			const geoTop = Lunar.Geometry.layoutHbox({
				box: geoCenter[0],
				relative: true
			});
			const geoMid = Lunar.Geometry.layoutHbox({
				box: geoCenter[1],
				dimension: [3, 2],
				relative: true
			});
			
			h.$background.width = this.game.w;
			h.$background.height = this.game.h;

			this.geo(h.$side, geoRoot[0]);
			this.geo(h.$top, geoCenter[0]);
			this.geo(h.$mid, geoCenter[1]);
			this.geo(h.$bottom, geoCenter[2]);
			this.geo(h.top.$tabs, geoTop[0]);

			const geoLeft = Lunar.Geometry.layoutVbox({
				box: h.$side.bodyDimension,
				dimension: [2,1,1],
				relative: true
			});
			
			this.geo(h.side.$battle, geoLeft[0], {proportional: true, anchor: 0.5});
			this.geo(h.side.$settings, geoLeft[1], {proportional: true, anchor: 0.5});
			this.geo(h.side.$exit, geoLeft[2], {proportional: true, anchor: 0.5});
			
			this.geo(h.mid.$left, geoMid[0]);
			this.geo(h.mid.$right, geoMid[1]);
			
			const geoAction = Lunar.Geometry.layoutHbox({
				box: geoCenter[2],
				dimension: 2,
				padding: {
					top: 0.1,
					bottom: 0.1,
					left: 0.1,
					right: 0.1,
					x: 0.05
				},
				relative: true
			});
			
			const geoTab = Lunar.Geometry.layoutHbox({
				box: h.top.$tabs.bodyDimension,
				dimension: 4,
				padding: {
					top: 0.05,
					bottom: 0.05,
					x: 20
				},
				relative: true
			});
			
			this.geo(h.top.tabs.$char, geoTab[0]);
			this.geo(h.top.tabs.$item, geoTab[1]);
			this.geo(h.top.tabs.$invite, geoTab[2]);
			this.geo(h.top.tabs.$collection, geoTab[3]);
			
			this.layoutButtonText(h.top.tabs.char.$text, true);
			this.layoutButtonText(h.top.tabs.item.$text, true);
			this.layoutButtonText(h.top.tabs.invite.$text, true);
			this.layoutButtonText(h.top.tabs.collection.$text, true);

			this.geo(h.bottom.$action1, geoAction[0]);
			this.geo(h.bottom.$action2, geoAction[1]);
			
			this.layoutButtonText(h.bottom.action1.$text, true);
			this.layoutButtonText(h.bottom.action2.$text, true);
		}
		
		get actionButton1() {
			return this.hierarchy.bottom.$action1;
		}
		
		get actionButton2() {
			return this.hierarchy.bottom.$action2;
		}
		
		/**
		 * @private
		 */
		_initScene() {
			this._loadScene = undefined;
			this._loaded = true;
			this.game.pushScene(this);
			const _this = this;			
						
			// Top
			const topContainer = new PIXI.ClipContainer();
			
			// Top
			const topBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			topBar.alpha = 0.5;
			
			// Left side bar
			const sideContainer = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			sideContainer.alpha = 0.5;
			
			// Mid
			const midContainer = new PIXI.ClipContainer();
			
			// Mid-Left
			const leftPanel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			leftPanel.alpha = 0.65;
			
			// Mid-Right
			const rightPanel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			rightPanel.alpha = 0.8;
			
			// Bottom
			const bottomBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			bottomBar.alpha = 0.5;
			
			// Action buttons
			const buttonAction1 = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const buttonAction2 = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			
			const textAction1 = this.createButtonText(buttonAction1, "");
			const textAction2 = this.createButtonText(buttonAction2, "");
			
			buttonAction1.visible = false;
			buttonAction2.visible = false;
			
			// Tab buttons		
			const buttonChar = new PIXI.NinePatch(this.game.baseLoader.resources.button);
			const buttonItem = new PIXI.NinePatch(this.game.baseLoader.resources.button);
			const buttonInvite = new PIXI.NinePatch(this.game.baseLoader.resources.button);
			const buttonCollection = new PIXI.NinePatch(this.game.baseLoader.resources.button);

			const textButtonChar = this.createButtonText(buttonChar, "Pokémon");
			const textButtonItem = this.createButtonText(buttonItem, "Item");
			const textButtonInvite = this.createButtonText(buttonInvite, "Players");
			const textButtonCollection = this.createButtonText(buttonCollection, "Collection");
			
			buttonItem.on('pointertap', this._onClickItem, this);
			buttonInvite.on('pointertap', this._onClickInvite, this);
			buttonChar.on('pointertap', this._onClickChar, this);
			buttonCollection.on('pointertap', this._onClickCollection, this);
			
			// Side buttons
			const buttonBattle = this._createSideButton('battle.png', this._onClickBattle);
			const buttonExit = this._createSideButton('close.png', this._onClickExit);
			const buttonSettings = this._createSideButton('large.png', this._onClickSettings);
			
			buttonBattle.tint = 0x111111;
			
			//Background
			const bg = new PIXI.Sprite(this.game.loaderFor("menu").resources.bg.texture);
			
			this.view.addChild(bg);
			this.view.addChild(topContainer);
			this.view.addChild(sideContainer);
			this.view.addChild(midContainer);
			this.view.addChild(bottomBar);

			topContainer.addChild(topBar);

			bottomBar.addChild(buttonAction1);
			bottomBar.addChild(buttonAction2);
			
			midContainer.addChild(leftPanel);
			midContainer.addChild(rightPanel);
			
			topBar.body.addChild(buttonChar);
			topBar.body.addChild(buttonItem);
			topBar.body.addChild(buttonInvite);
			topBar.body.addChild(buttonCollection);
					
			buttonChar.body.addChild(textButtonChar);
			buttonItem.body.addChild(textButtonItem);
			buttonInvite.body.addChild(textButtonInvite);
			buttonCollection.body.addChild(textButtonCollection);
			
			buttonAction1.body.addChild(textAction1);
			buttonAction2.body.addChild(textAction2);

			sideContainer.body.addChild(buttonBattle);
			sideContainer.body.addChild(buttonSettings);
			sideContainer.body.addChild(buttonExit);
						
			this.hierarchy = {
				top: {
					tabs: {
						$char: buttonChar,
						$item: buttonItem,
						$invite: buttonInvite,
						$collection: buttonCollection,
						char: {
							$text: textButtonChar
						},
						item: {
							$text: textButtonItem
						},
						invite: {
							$text: textButtonInvite
						},
						collection: {
							$text: textButtonCollection
						}
					},
					$tabs: topBar,
				},
				side: {
					$battle: buttonBattle,
					$settings: buttonSettings,
					$exit: buttonExit,
				},
				mid: {
					$left: leftPanel,
					$right: rightPanel,					
				},
				bottom: {
					action1: {
						$text: textAction1
					},
					action2: {
						$text: textAction2
					},
					$action1: buttonAction1,
					$action2: buttonAction2
				},
				$side: sideContainer,
				$top: topContainer,
				$mid: midContainer,
				$bottom: bottomBar,
				$background: bg
			};
		}
		
		_createSideButton(icon, handler) {
			const button = new PIXI.Sprite(this.game.baseLoader.resources.packed.spritesheet.textures[icon]);
			button.interactive = true;
			button.buttonMode = true;
			button.on('pointerover', () => {
				button.width = button.width*9/8;
				button.height = button.height*9/8;
				button.rotation = 0.2;
			});
			button.on('pointerout', () => {
				button.width = button.width*8/9;
				button.height = button.height*8/9;
				button.rotation = 0.0;
			});
			button.on('pointertap', handler, this);
			return button;
		}
		
		_onClickSettings() {
			const _this = this;
			this.game.sfx('resources/translune/static/confirm');
			this.game.toogleFullscreen();
		}
		
		_onClickBattle() {
			const invitation = this._invitations.shift();
			if (!invitation) {
				this.game.sfx('resources/translune/static/unable', {volume: 0.5});
				return;
			}
			this._updateInvitations();
			this.game.sfx('resources/translune/static/confirm');
			this._inviteConfirm = new Lunar.Scene.InviteConfirm(this, invitation);
			this.game.pushScene(this._inviteConfirm);
			this.tabModal(true);
		}
		
		closeInviteConfirm() {
			this.tabModal(false);
			this.game.removeScene(this._inviteConfirm);
			this._inviteConfirm = undefined;
		}
		
		onOpponentAccepted(nickname) {
			const _this = this;
			this.game.switchBgm(null, 0.01,2000);
			this.game.net.registerMessageHandler(Lunar.Message.battlePreparationCancelled, {
				handle: this.method('_onBattlePrepCancelled'),
				error: error => {
					console.error('received error on battle prep cancelled', error);
					_this._onBattlePrepCancelled();
				}
			});
			this.tabModal(true);
			this._battlePrep = new Lunar.Scene.BattlePreparation(this);
			this.game.pushScene(this._battlePrep);
		}
		
		cancelBattlePrep() {
			this.tabModal(false);
			this.game.removeScene(this._loadScene);
			this.game.removeScene(this._battlePrep);
			this.game.switchBgm(null, null, 2000);
			this.game.net.dispatchMessage(Lunar.Message.cancelBattlePreparation, {
				
			}).then(() => {
				
			}).catch(error => { 
				console.log("failed to cancel battle preparations")
			}).then(() => {
				this.game.net.removeMessageHandlers(Lunar.Message.battlePrepared);
				this.game.net.removeMessageHandlers(Lunar.Message.battlePreparationCancelled);
			});
			this._battlePrep = undefined;
		}
		
		_cancelBattle() {
			this.tabModal(false);
			this.game.switchBgm(null, null, 2000);
			this.game.net.removeMessageHandlers(Lunar.Message.battlePrepared);
			this.game.net.removeMessageHandlers(Lunar.Message.battlePreparationCancelled);
			this.game.removeScene(this._battlePrep);
			this.game.removeScene(this._loadScene);
			this.showConfirmDialog("Could not start battle, please try again later.");
			this._battlePrep = undefined;
			this._loadScene = undefined;
		}
		
		battle(characterStates, items) {
			this.tabModal(true);
			this._loadScene = new Lunar.Scene.Load(this.game);
			this.game.pushScene(this._loadScene);
			this.game.net.registerMessageHandler(Lunar.Message.battlePrepared, {
				handle: this.method('_onBattlePrepared'),
				errror: error => {
					console.error('received error on battle prepared', error);
					this._cancelBattle();
				}
			});
			this.game.net.dispatchMessage(Lunar.Message.prepareBattle, {
				characterStates: characterStates.map(cs => cs.id),
				items: items.map(item => item.name)
			}).then(response => {
			}).catch(error => {
				console.error("could not prepare battle", error);
				this._cancelBattle();
			}).then(() => {
			});
		}
		
		_onBattlePrepared(response) {
			const battleData = response.data;
			this.game.net.removeMessageHandlers(Lunar.Message.battlePrepared);
			this.game.net.removeMessageHandlers(Lunar.Message.battlePreparationCancelled);
			this.game.removeScene(this._battlePrep);
			this.game.removeScene(this._loadScene);
			this.game.removeScene(this);
			this.game.removeAllScenes();
			this.game.pushScene(new Lunar.Scene.Battle(this.game, battleData));
		}
		
		/**
		 * @private
		 */
		_onBattlePrepCancelled() {
			this.cancelBattlePrep();
			this.showConfirmDialog("The other player does not want to battle anymore.");
		}
		
		/**
		 * @private
		 */
		_onClickExit() {
			const _this = this;
			this.game.sfx('resources/translune/static/cancel');
			const id = this.game.window.setTimeout(() => _this.game.exit(), 10000);
			const start = new Date().getTime() + 10000;
			this.tabModal(true);
			this.game.pushScene(new Lunar.Scene.Dialog(this.game, { 
				message: "Exiting in 10 seconds...",
				update: (delta, dialog) =>
					dialog.hierarchy.top.$message.text = `Exiting in ${Math.round((start - new Date().getTime())/1000.0)} seconds...`,
				choices: [
					{
						text: "Keep playing",
						callback: dialog => {
							_this.game.window.clearTimeout(id);
							_this.game.sfx('resources/translune/static/ping');
							dialog.close();
							this.tabModal(false);
						}
					}
				]
			}));
		}
		
		tabModal(value) {
			if (this._tabScene)
				this._tabScene.modal = value;
			if (this._tabDetails)
				this._tabDetails.modal = value;
		}
		
		/**
		 * @private
		 */
		_onClickChar() {
			this._switchTab('char', () => new Lunar.Scene.MenuChar(this.game, this));
		}
		
		/**
		 * @private
		 */
		_onSelectChar(characterState) {
			this.game.sfx(`/resource/${characterState.character.cry}`);
			this._switchTabDetails(() => new Lunar.Scene.MenuCharDetails(this.game, this, characterState));
		}
		
		_onRemoveChar(characterState) {
			this._switchTab('char', () => new Lunar.Scene.MenuChar(this.game, this), true);
		}
		
		/**
		 * @private
		 */
		_onSelectInvite(characterState) {
			this._switchTabDetails(() => new Lunar.Scene.MenuInviteDetails(this.game, this, characterState))
		}
		
		/**
		 * @private
		 */
		_onClickItem() {
			this._switchTab('item', () => {
				console.info("items not yet implemented")
				return null;
			});
		}
		
		/**
		 * @private
		 */
		_onClickCollection() {
			this._switchTab('collection', () => {
				console.info("collection not yet implemented")
				return null;
			});
		}
		
		/**
		 * @private
		 */
		_onClickInvite() {
			this._switchTab('invite', () => new Lunar.Scene.MenuInvite(this.game, this));
		}
		
		_updateInvitations() {
			if (this._invitations.length > 0) {
				this.hierarchy.side.$battle.tint = 0xFFFF00;
			}
			else {
				this.hierarchy.side.$battle.rotation = 0;
				this.hierarchy.side.$battle.tint = 0x111111;
			}
		}
		
		/**
		 * @private
		 */
		_onMessageInvited(response) {
			const from = response.data.nickname;
			if (!from)
				return;
			this._invitations.push(from);
			this.game.sfx('resources/translune/static/bell', {volume: 0.5});
			this._updateInvitations();
		}

		/**
		 * @private
		 */
		_onMessageInviteRetracted(response) {
			const from = response.data.nickname;
			if (!from)
				return;
			Lunar.Array.removeElement(this._invitations, from);
			this._updateInvitations();
		}
			
		/**
		 * @private
		 */
		_switchTab(tabName, sceneFactory, force = false) {
			if (!force && this._tabState === tabName) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/buttonclick');
			this._clearActionButton(this.actionButton1);
			this._clearActionButton(this.actionButton2);
			if (this._tabScene)
				this.game.removeScene(this._tabScene);
			if (this._tabDetails)
				this.game.removeScene(this._tabDetails);
			this._tabDetails = undefined;
			this._tabScene = sceneFactory.call(this);
			if (this._tabScene)
				this.game.pushScene(this._tabScene, this.hierarchy.mid.$left.body);
			this._tabState = tabName;
		}

		/**
		 * @private
		 */
		_switchTabDetails(sceneFactory) {
			if (this._tabDetails)
				this.game.removeScene(this._tabDetails);
			this._clearActionButton(this.actionButton1);
			this._clearActionButton(this.actionButton2);			
			this._tabDetails = sceneFactory.call(this);
			this.game.pushScene(this._tabDetails, this.hierarchy.mid.$right.body);
		}
		
		/**
		 * @private
		 */
		_clearActionButton(button) {
			button.visible = false;
			button.removeAllListeners('pointertap');
			button.body.children[0].text = '';
		}		
	};
})(window.Lunar, window);