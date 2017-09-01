(function(Lunar, window, undefined) {
	Lunar.Scene.MenuChar = class extends Lunar.Scene.Base {
		constructor(game, menu) {
			super(game);
			this._loaded = false;
			this._loadScene = undefined;
			this._player = undefined;
			this._hierarchy = {};
			this._menu = menu;
		}

		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			
			const _this = this;			
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				fetch: Lunar.FetchType.userPlayer
			});
			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, delegateLoadable);

			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);

			requestLoadable.promise
				.then(response => _this._initLoader(delegateLoadable, response))
				.catch(response =>  {
					_this.showConfirmDialog("Could not load your player data, please try again later.");
					_this.game.removeScene(this._loadScene);
					_this.game.removeScene(this);
				});
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		onRemove() {
			if (this._loadScene)
				this.game.removeScene(this._loadScene);
			this._loadScene = undefined;
			this._listPanel = undefined;
			this._detailPanel = undefined;
			this.game.loaderFor("menu-tab").reset();
		}
		
		/**
		 * @private
		 */
		_initLoader(delegateLoadable, response) {
			this._player = response.data.data;
			const icons = this._player.characterStates.map(characterState => characterState.character.imgIcon).join(',');		
			const loader = this.game.loaderFor("menu-tab");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable;
			loader.reset();
			loader.add("icons", `spritesheet/spritesheet.json?resources=${icons}`);
			loader.load();
		}
		
		/**
		 * @private
		 */
		_initScene() {		
			const loader = this.game.loaderFor('menu-tab');
			const panel = this._loadScene.view.parent.parent; 
			const _this = this;
			
			this._loadScene = undefined;
			this._loaded = true;
			this.game.pushScene(this, panel.body);
			
			Lunar.Geometry.layoutGrid({
				boxWidth: panel.bodyWidth,
				boxHeight: panel.bodyHeight,
				paddingLeft: 0.02,
				paddingRight: 0.02,
				paddingTop: 0.02,
				paddingBottom: 0.02,
				paddingX: 0.01,
				paddingY: 0.01,
				tileX: 7,
				tileY: 4
			}, (x, y, i, j, n, width, height) => {
				const characterState = this._player.characterStates[n];
				if (characterState) {
					const texture = loader.resources.icons.spritesheet.textures[characterState.character.imgIcon];
					const sprite = new PIXI.Sprite(texture);
					sprite.interactive = true;
					sprite.buttonMode = true;
					sprite.on('pointerover', () => Lunar.Geometry.proportionalScale(sprite, 1.2*width, 1.2*height));
					sprite.on('pointerout', () => Lunar.Geometry.proportionalScale(sprite, width, height));
					sprite.on('pointerdown', () => {
						/*
					}
						const l = _this.game.loaderFor('test');
						l.reset();
						l.add("test",`/resource/${characterState.character.imgFront}`).load((loader, resources) => {
							const s = new PIXI.extras.AnimatedSprite(Object.values(resources.test.spritesheet.textures));
							_this.view.addChild(s);
							s.position.set(_this.game.x(0.38),_this.game.y(0.68));
							s.anchor.set(0.5,0.5);
							s.scale.set(-3,3);
							s.play();
						});*/
						_this.game.sfx(`/resource/${characterState.character.cry}`);
					});
					sprite.position.set(x,y);
					sprite.anchor.set(0.5,0.5);
					Lunar.Geometry.proportionalScale(sprite, width, height);
					_this.view.addChild(sprite);
				}
			});
		}
	};
	
	
	/**
	 * The main menu.
	 */
	Lunar.Scene.Menu = class extends Lunar.Scene.Base {
		constructor(game) {
			super(game);
			this.loaded = false;
			this._tabScene = undefined;
			this._loadScene = undefined;
			this._hierarchy = {};
		}
		
		sceneToAdd() {
			if (this.loaded)
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
		
		onRemove() {
			if (this._loadScene)
				this.game.removeScene(this._loadScene);
			if (this._tabScene)
				this.game.removeScene(this._tabScene);
			this._loadScene = undefined;
			this._tabScene = undefined;
			this._hierarchy = undefined;
			this.game.loaderFor("menu").reset();
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
			loader.add("packed", 'resources/translune/static/menu/packed.json');			
			loader.load();
		}
		
		get hierarchy() {
			return this._hierarchy;
		}
		
		/**
		 * @private
		 */
		_initScene() {
			this._loadScene = undefined;
			this.loaded = true;
			this.game.pushScene(this);
			
			const topButtonPadding = 10;
			const topButtonHeight  = 0.9;
			const widthTopBar      = 0.9;
			const widthExitBar     = 1-widthTopBar;
			const heightTopBar     = 0.15;
			const heightBottomBar  = 0.15;
			const widthLeftPanel   = 0.6;
			const widthRightPanel  = 1-widthLeftPanel;
			const heightPanel      = 0.7;
					
			// Top
			const topBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthTopBar), this.game.dy(heightTopBar));
			topBar.position.set(this.game.x(-1), this.game.y(1));
			topBar.alpha = 0.5;
					
			const exitBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthExitBar), this.game.dy(heightTopBar));
			exitBar.position.set(this.game.x(-1+2*widthTopBar), this.game.y(1));
			exitBar.alpha = 0.5;
	
			// Left
			const leftPanel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthLeftPanel), this.game.dy(heightPanel));
			leftPanel.position.set(this.game.x(-1), this.game.y(1-2*heightTopBar));
			leftPanel.alpha = 0.65;
			
			// Right
			const rightPanel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthRightPanel), this.game.dy(heightPanel));
			rightPanel.position.set(this.game.x(-1+2*widthLeftPanel), this.game.y(1-2*heightTopBar));
			rightPanel.alpha = 0.8;
			
			// Bottom
			const bottomBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.w, this.game.dy(heightBottomBar));
			bottomBar.position.set(this.game.x(-1), this.game.y(1-2*(heightTopBar+heightPanel)));
			bottomBar.alpha = 0.5;
			
			// Tab buttons & Text
			const buttonChar = new PIXI.NinePatch(this.game.baseLoader.resources.button, (topBar.bodyWidth-topButtonPadding*5)/4, topBar.bodyHeight*topButtonHeight);
			const textButtonChar = new PIXI.Text("Pokémon", Lunar.FontStyle.button);
			buttonChar.position.set(topButtonPadding, topBar.bodyHeight*(1-topButtonHeight)/2);
			this.buttonCommons(buttonChar, textButtonChar);
			buttonChar.on('pointerdown', this.onClickChar.bind(this));
									
			const buttonItem = new PIXI.NinePatch(this.game.baseLoader.resources.button, buttonChar.width, buttonChar.height);
			const textButtonItem = new PIXI.Text("Item", Lunar.FontStyle.button);
			buttonItem.position.set(buttonChar.x+buttonChar.width+topButtonPadding, buttonChar.y);
			this.buttonCommons(buttonItem, textButtonItem);
			buttonItem.on('pointerdown', this.onClickItem.bind(this));
			
			const buttonInvite = new PIXI.NinePatch(this.game.baseLoader.resources.button, buttonChar.width, buttonChar.height);
			const textButtonInvite = new PIXI.Text("Invite", Lunar.FontStyle.button);
			buttonInvite.position.set(buttonItem.x+buttonItem.width + topButtonPadding, buttonChar.y);
			this.buttonCommons(buttonInvite, textButtonInvite);
			buttonInvite.on('pointerdown', this.onClickInvite.bind(this));
			
			const buttonReserved = new PIXI.NinePatch(this.game.baseLoader.resources.button, buttonChar.width, buttonChar.height);
			const textButtonReserved = new PIXI.Text("Collection", Lunar.FontStyle.button);
			buttonReserved.position.set(buttonInvite.x+buttonInvite.width + topButtonPadding, buttonChar.y);
			textButtonReserved.anchor.set(0.5,0.5);
			textButtonReserved.position.set(buttonReserved.bodyWidth/2,buttonReserved.bodyHeight/2);
			
			// Close button
			const buttonExit = new PIXI.Sprite(this.game.loaderFor("menu").resources.packed.spritesheet.textures['close.png']);
			buttonExit.width = exitBar.bodyWidth*0.9;
			buttonExit.height = exitBar.bodyHeight*0.9;
			buttonExit.interactive = true;
			buttonExit.buttonMode = true;
			buttonExit.on('pointerover', () => {
				buttonExit.width = exitBar.bodyWidth*1.0;
				buttonExit.height = exitBar.bodyHeight*1.0;
				buttonExit.rotation = 0.2;
			});
			buttonExit.on('pointerout', () => {
				buttonExit.width = exitBar.bodyWidth*0.9;
				buttonExit.height = exitBar.bodyHeight*0.9;
				buttonExit.rotation = 0.0;
			});
			buttonExit.anchor.set(0.5,0.5);
			buttonExit.position.set(exitBar.bodyWidth/2,exitBar.bodyHeight/2);
			buttonExit.on('pointerdown', this.onClickExit.bind(this));
			
			//Background
			const bg = new PIXI.Sprite(this.game.loaderFor("menu").resources.bg.texture);
			bg.width = this.game.w;
			bg.height = this.game.h;
			
			this.view.addChild(bg);
			this.view.addChild(topBar);
			this.view.addChild(exitBar);
			this.view.addChild(leftPanel);
			this.view.addChild(rightPanel);
			this.view.addChild(bottomBar);
			
			topBar.body.addChild(buttonChar);
			topBar.body.addChild(buttonItem);
			topBar.body.addChild(buttonInvite);
			topBar.body.addChild(buttonReserved);
					
			buttonChar.body.addChild(textButtonChar);
			buttonItem.body.addChild(textButtonItem);
			buttonInvite.body.addChild(textButtonInvite);
			buttonReserved.body.addChild(textButtonReserved);

			exitBar.body.addChild(buttonExit);
			
			this._hierarchy = {
				top: {
					tabs: {
						$char: buttonChar,
						$item: buttonItem,
						$invite: buttonInvite,
						char: {
							$text: textButtonChar
						},
						item: {
							$text: textButtonItem
						},
						invite: {
							$text: textButtonInvite
						}
					},
					exit: {
						$button: buttonExit
					},
					$tabs: topBar,
					$exit: exitBar,
				},
				$left: leftPanel,
				$right: rightPanel,
				$bottom: bottomBar,
				$background: bg
			};
		}
		
		/**
		 * @private
		 */
		onClickExit() {
			const _this = this;
			this.game.sfx('resources/translune/static/confirm');
			const id = this.game.window.setTimeout(() => _this.game.exit(), 10000);
			const start = new Date().getTime() + 10000;
			this.game.pushScene(new Lunar.Scene.Dialog(this.game, { 
				message: "Exiting in 10 seconds...",
				update: (delta, textMessage, textChoices) =>
					textMessage.text = `Exiting in ${Math.round((start - new Date().getTime())/1000.0)} seconds...`,
				choices: [
					{
						text: "Keep playing",
						callback: close => {
							_this.game.window.clearTimeout(id);
							_this.game.sfx('resources/translune/static/ping');
							close();
						}
					},
	//				{
	//					text: "Exit now",
	//					callback: close => {
	//						_this.game.window.clearTimeout(id);
	//						_this.game.sfx('static/ping');
	//						_this.game.exit();
	//					}
	//				}
				]
			}));
		}
		
		/**
		 * @private
		 */
		onClickChar() {
			this.game.sfx('resources/translune/static/buttonclick');
			if (this._tabScene)
				this.game.removeScene(this._tabScene);
			this._tabScene = new Lunar.Scene.MenuChar(this.game, this);
			this.game.pushScene(this._tabScene, this.hierarchy.$left.body);
		}
		
		
		/**
		 * @private
		 */
		onClickItem() {
			this.game.sfx('resources/translune/static/buttonclick');
		}
		
		/**
		 * @private
		 */
		onClickInvite() {
			this.game.sfx('resources/translune/static/buttonclick');
		}
	
		/**
		 * @private
		 */
		buttonCommons(button, text) {
			button.interactive = true;
			button.buttonMode = true;
			button.on('pointerover', () => text.style = Lunar.FontStyle.buttonActive);
			button.on('pointerout', () => text.style = Lunar.FontStyle.button);
			text.anchor.set(0.5,0.5);
			text.position.set(button.bodyWidth/2,button.bodyHeight/2);
		}
		
		update(delta) {
			super.update(delta);
		}
	};
})(window.Lunar, window);