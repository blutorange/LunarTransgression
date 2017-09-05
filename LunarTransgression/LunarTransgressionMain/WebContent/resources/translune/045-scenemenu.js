(function(Lunar, window, undefined) {
	/**
	 * The main menu.
	 */
	Lunar.Scene.Menu = class extends Lunar.Scene.Base {
		constructor(game) {
			super(game);
			this._loaded = false;
			this._tabState = undefined;
			this._tabScene = undefined;
			this._tabDetails = undefined;
			this._loadScene = undefined;
			this._player = undefined;
		}
		
		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				 fetch: Lunar.FetchType.availableBgAndBgm
			});
			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, delegateLoadable);
			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);
			
			requestLoadable.promise.then(response => _this._initLoader(delegateLoadable, response));
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		destroy() {
			this.game.loaderFor("menu").reset();
			this._player = undefined;
			super.destroy();
		}
		
		onAdd() {
			super.onAdd();
			this._onClickChar();
		}
		
		onRemove() {
			super.onRemove();
			if (this._loadScene)
				this.game.removeScene(this._loadScene);
			if (this._tabScene)
				this.game.removeScene(this._tabScene);
			if (this._tabDetails)
				this.game.removeScene(this._tabDetails);
			this._loadScene = undefined;
			this._tabScene = undefined;
		}
		
		update(delta) {
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
//			loader.add("packed", 'resources/translune/static/menu/packed.json');			
			loader.load();
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			const geoRoot = Lunar.Geometry.layoutVbox({
				box: {
					w: this.game.w,
					h: this.game.h
				},
				dimension: [15, 70, 15]
			});
			const geoTop = Lunar.Geometry.layoutHbox({
				box: geoRoot[0],
				dimension: [9,1],
				relative: true
			});
			const geoMid = Lunar.Geometry.layoutHbox({
				box: geoRoot[1],
				dimension: [3, 2],
				relative: true
			});
			
			h.$background.width = this.game.w;
			h.$background.height = this.game.h;
			this.geo(h.$top, geoRoot[0]);
			this.geo(h.$mid, geoRoot[1]);
			this.geo(h.$bottom, geoRoot[2]);
			this.geo(h.top.$tabs, geoTop[0]);
			this.geo(h.top.$exit, geoTop[1]);
			
			const geoExit = Lunar.Geometry.layoutVbox({
				box: h.top.$exit.bodyDimension,
				padding: {
					top: 0.1,
					bottom: 0.1,
					left: 0.1,
					right: 0.1
				},
				relative: true
			});
			
			this.geo(h.top.exit.$icon, geoExit[0], {anchor: 0.5});
			this.geo(h.mid.$left, geoMid[0]);
			this.geo(h.mid.$right, geoMid[1]);
			
			const geoAction = Lunar.Geometry.layoutHbox({
				box: geoRoot[2],
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
			
			// Top-Left
			const topBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			topBar.alpha = 0.5;
			
			// Top-Right
			const exitBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			exitBar.alpha = 0.5;
			
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
			const textButtonInvite = this.createButtonText(buttonInvite, "Invite");
			const textButtonCollection = this.createButtonText(buttonCollection, "Collection");
			
			buttonItem.on('pointertap', this._onClickItem, this);
			buttonInvite.on('pointertap', this._onClickInvite, this);
			buttonChar.on('pointertap', this._onClickChar, this);
			buttonCollection.on('pointertap', this._onClickCollection, this);
			
			// Close button
			const buttonExit = new PIXI.Sprite(this.game.baseLoader.resources.packed.spritesheet.textures['close.png']);
			buttonExit.interactive = true;
			buttonExit.buttonMode = true;
			buttonExit.on('pointerover', () => {
				buttonExit.width = buttonExit.width*9/8;
				buttonExit.height = buttonExit.height*9/8;
				buttonExit.rotation = 0.2;
			});
			buttonExit.on('pointerout', () => {
				buttonExit.width = buttonExit.width*8/9;
				buttonExit.height = buttonExit.height*8/9;
				buttonExit.rotation = 0.0;
			});
			buttonExit.on('pointertap', this._onClickExit, this);
			
			//Background
			const bg = new PIXI.Sprite(this.game.loaderFor("menu").resources.bg.texture);
			
			this.view.addChild(bg);
			this.view.addChild(topContainer);
			this.view.addChild(exitBar);
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

			exitBar.body.addChild(buttonExit);
						
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
					exit: {
						$icon: buttonExit
					},
					$tabs: topBar,
					$exit: exitBar,
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
				$top: topContainer,
				$mid: midContainer,
				$bottom: bottomBar,
				$background: bg
			};
		}
		
		/**
		 * @private
		 */
		_onClickExit() {
			const _this = this;
			this.game.sfx('resources/translune/static/cancel');
			const id = this.game.window.setTimeout(() => _this.game.exit(), 10000);
			const start = new Date().getTime() + 10000;
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
						}
					}
				]
			}));
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
		
		/**
		 * @private
		 */
		_switchTab(tabName, sceneFactory) {
			if (this._tabState === tabName) {
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