(function(Lunar, window, undefined) {
	Lunar.Scene.MenuChar = class extends Lunar.Scene.Base {
		constructor(game, menu) {
			super(game);
			this._loaded = false;
			this._loadScene = undefined;
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
		
		destroy() {
			this._loadScene = undefined;
			this._menu = undefined;
			this.game.loaderFor("menu-tab").reset();
			super.destroy();
		}
		
		onRemove() {
			super.onRemove();
			if (this._loadScene)
				this.game.removeScene(this._loadScene);
		}
		
		layout() {
			const h = this.hierarchy;
			const panel = this.view.parent.parent;
			const _this = this;
			const geoGrid = Lunar.Geometry.layoutGrid({
				box: {
					x:0,
					y:0,
					w: panel.bodyWidth,
					h: panel.bodyHeight
				},
				padding: {
					left: 10,
					right: 10,
					top: 10,
					bottom: 10,
					x: 4,
					y: 4
				},
				dimension: {
					n: 7,
					m: 4
				}
			});
			geoGrid.flatten().forEach((geo, index) => {
				const characterState = _this.menu.player.characterStates[index];
				if (characterState) {
					const sprite = h.grid[index].$icon;
					this.geo(sprite, geo, {anchor: 0.5, proportional: true});
				}
			});
		}
		
		/**
		 * @private
		 */
		_initLoader(delegateLoadable, response) {
			this.menu.player = response.data.data;
			const icons = this.menu.player.characterStates.map(characterState => characterState.character.imgIcon).join(',');		
			const loader = this.game.loaderFor("menu-tab");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable;
			loader.reset();
			loader.add("icons", `spritesheet/spritesheet.json?resources=${icons}`);
			loader.load();
		}
		
		get menu() {
			return this._menu;
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
			
			const grid = this.menu.player.characterStates.map(characterState => {
				const texture = loader.resources.icons.spritesheet.textures[characterState.character.imgIcon];
				const sprite = new PIXI.Sprite(texture);
				sprite.interactive = true;
				sprite.buttonMode = true;
				sprite.on('pointerover', () => {
					sprite.width = sprite.width * 6/5,
					sprite.height = sprite.height * 6/5
				});
				sprite.on('pointerout', () => {
					sprite.width = sprite.width * 5/6,
					sprite.height = sprite.height * 5/6
				});				
				sprite.on('pointerdown', () => _this._menu.onSelectChar(characterState));
				_this.view.addChild(sprite);
				return {
					$icon: sprite
				};
			});
			
			this.hierarchy = {
				grid: grid	
			};
		}
	};
})(window.Lunar, window);