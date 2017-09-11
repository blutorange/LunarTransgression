(function(Lunar, window, undefined) {
	const LIST_CHAR_X = 5;
	const LIST_CHAR_Y = 6;
	const LIST_ITEM_X = 5;
	const LIST_ITEM_Y = 6;
	const ALPHA_DISABLED = 0.25;
	
	Lunar.Scene.BattlePreparation = class extends Lunar.Scene.Base {
		
		constructor(menu) {
			super(menu.game);
			this._menu = menu;
			this._playerDetails = undefined;
			this._loadScene = undefined;
			this._selectedChars = {};
			this._selectedItems = {};
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
				.catch(error =>  {
					console.log("failed to load player data", error);
					_this.game.removeScene(this._loadScene);
					_this.menu.cancelBattlePrep();
					_this.showConfirmDialog("Could not load your character and item data.");
				});
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		destroy() {
			this._menu = undefined;
			this._playerDetails = undefined;
			this._loadScene = undefined;
			this._selectedChars = undefined;
			this._selectedItems = undefined;
			this.game.loaderFor('menu-invite').reset();
			super.destroy();
		}
		
		onRemove() {
			this.game.removeScene(this._loadScene);
			super.onRemove();
		}
		
		layout() {
			const h = this.hierarchy;
			
			const geoRoot = Lunar.Geometry.layoutVbox({
				box: {
					w: this.game.w,
					h: this.game.h
				},
				padding: {
					left: 0.10,
					right: 0.10,
					top: 0.05,
					bottom: 0.05
				},
				relative: true
			});
			
			this.geo(h.$frame, geoRoot[0]);
			
			const geoBase = Lunar.Geometry.layoutVbox({
				box: h.$frame.bodyDimension,
				dimension: [15,70,15],
				padding: {
					left: 0.05,
					right: 0.05,
					top: 0.02,
					bottom: 0.02
				},
				relative: true
			});
			
			const geoMid = Lunar.Geometry.layoutHbox({
				box: geoBase[1],
				dimension: 2,
				relative: true
			});
			
			const geoIcons = Lunar.Geometry.layoutGrid({
				box: geoMid[0],
				dimension: {
					n: LIST_CHAR_X,
					m: LIST_CHAR_Y
				},
				relative: true
			});
			
			const geoItems = Lunar.Geometry.layoutGrid({
				box: geoMid[1],
				dimension: {
					n: LIST_ITEM_X,
					m: LIST_ITEM_Y
				},
				relative: true
			});
			
			const geoButtons = Lunar.Geometry.layoutHbox({
				box: geoBase[2],
				dimension: 2,
				padding: {
					x: 0.07
				},
				relative: true
			});
			
			h.$overlay.clear();
			h.$overlay.beginFill(0x222222, 0.85);
			h.$overlay.drawRect(0, 0, this.game.w, this.game.h);
			h.$overlay.endFill();

			this.geo(h.frame.$label, geoBase[0], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.$mid, geoBase[1])
			this.geo(h.frame.$buttons, geoBase[2]);

			this.geo(h.frame.mid.$chars, geoMid[0]);
			this.geo(h.frame.mid.$items, geoMid[1]);
			
			this.geo(h.frame.buttons.$battle, geoButtons[0]);
			this.geo(h.frame.buttons.$cancel, geoButtons[1]);
			
			this.layoutButtonText(h.frame.buttons.battle.$text, true);
			this.layoutButtonText(h.frame.buttons.cancel.$text, true);
			
			geoIcons.flatten().forEach((geo, index) => {
				const characterState = this._playerDetails.characterStates[index];
				if (characterState) {
					this.geo(h.frame.mid.chars.icons[index].$icon, geo, {anchor: 0.5, proportional: true});
					this.geo(h.frame.mid.chars.icons[index].$level, geo, {anchor: 0.80, keepSize: true});
				}
			});
			
			geoItems.flatten().forEach((geo, index) => {
				const item = this._playerDetails.items[index];
				if (item) {
					this.geo(h.frame.mid.items.icons[index].$icon, geo, {anchor: 0.5, proportional: true});
				}
			});
			
			super.layout();
		}
		
		get menu() {
			return this._menu;
		}
		
		_initLoader(delegateLoadable, response) {
			this._playerDetails = response.data.data;
			const icons = this._playerDetails.characterStates.map(characterState => characterState.character.imgIcon).join(',');
			const items = this._playerDetails.items.map(item => item.imgIcon).join(',');
			const loader = this.game.loaderFor("menu-invite");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			loader.reset();
			loader.add("icons", `spritesheet/spritesheet.json?resources=${icons}`);
			if (items.length > 0)
				loader.add("items", `spritesheet/spritesheet.json?resources=${items}`);
			loader.load();
			delegateLoadable.loadable = loaderLoadable;
		}
		
		_initScene() {
			this.game.pushScene(this);
			this._loadScene = undefined;
			this._loaded = true;
			
			const loader = this.game.loaderFor('menu-invite');		
			const overlay = new PIXI.Graphics();
			const frame = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const label = new PIXI.Text("Select 4 characters and up to 3 items", Lunar.FontStyle.inviteReceiveTitle);

			overlay.interactive = true;
			
			const buttonBattle = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const buttonCancel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const textBattle = this.createButtonText(buttonBattle, "Battle!");
			const textCancel = this.createButtonText(buttonCancel, "Retreat!");
			buttonBattle.on('pointertap', this._onClickBattle, this);
			buttonCancel.on('pointertap', this._onClickCancel, this);
			
			const containerMid = new PIXI.ClipContainer();
			const containerIcons = new PIXI.ClipContainer();
			const containerItems = new PIXI.ClipContainer();
			const containerButtons = new PIXI.ClipContainer();
			
			const icons = this._playerDetails.characterStates.map(characterState => {
				const texture = loader.resources.icons.spritesheet.textures[characterState.character.imgIcon];
				const icon = new PIXI.Sprite(texture);
				const level = new PIXI.Text(characterState.level, Lunar.FontStyle.charIconLevel);
				containerIcons.addChild(icon);
				containerIcons.addChild(level);
				icon.interactive = true;
				icon.buttonMode = true;
				if (this._playerDetails.characterStates.length > 4)
					icon.alpha = ALPHA_DISABLED;
				icon.on('pointertap', () => this._onClickChar(characterState, icon), this);
				return {
					$icon: icon,
					$level: level
				};
			});
			
			const iconsItem = this._playerDetails.items.map(item => {
				const texture = loader.resources.items.spritesheet.textures[item.imgIcon];
				const icon = new PIXI.Sprite(texture);
				containerItems.addChild(icon);
				icon.interactive = true;
				icon.buttonMode = true;
				icon.alpha = ALPHA_DISABLED;
				icon.on('pointertap', () => this._onClickItem(item, icon), this);
				return {
					$icon: icon,
				};
			}); 

			if (this._playerDetails.characterStates.length > 4)
				buttonBattle.alpha = Lunar.Object.length(this._selectedChars) === 4 ? 1 : ALPHA_DISABLED; 
			
			this.view.addChild(overlay);
			this.view.addChild(frame);
			
			frame.body.addChild(label);
			frame.body.addChild(containerMid);
			frame.body.addChild(containerButtons);
			
			containerMid.addChild(containerIcons);
			containerMid.addChild(containerItems);

			containerButtons.addChild(buttonBattle);
			containerButtons.addChild(buttonCancel);
			buttonBattle.body.addChild(textBattle);
			buttonCancel.body.addChild(textCancel);
			
			if (this._playerDetails.characterStates.length <= 4) {
				this._playerDetails.characterStates.forEach(characterState => {
					this._selectedChars[characterState.id] = characterState;
				});
			}
					
			this.hierarchy = {
				frame: {
					mid: {
						chars: {
							icons: icons
						},
						items: {
							icons: iconsItem
						},
						$chars: containerIcons,
						$items: containerItems,
					},
					buttons: {
						battle: {
							$text: textBattle,
						},
						cancel: {
							$text: textCancel,
						},
						$battle: buttonBattle,
						$cancel: buttonCancel,
					},
					$label: label,
					$mid: containerMid,
					$buttons: containerButtons
				},
				$overlay: overlay,
				$frame: frame
			};
		}
		
		_onClickBattle() {
			if (Lunar.Object.length(this._selectedChars) !== 4 || Lunar.Object.length(this._selectedItems) > 3) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/confirm');
			this.menu.battle(Object.values(this._selectedChars), Object.values(this._selectedItems));
		}
		
		_onClickCancel() {
			this.game.sfx('resources/translune/static/cancel');
			this.menu.cancelBattlePrep();
		}
		
		_onClickChar(characterState, icon) {
			// Deselect
			if (this._selectedChars[characterState.id]) {
				this.game.sfx('resources/translune/static/cancel');
				delete this._selectedChars[characterState.id];
				icon.alpha = ALPHA_DISABLED;
				this.hierarchy.frame.buttons.$battle.alpha = ALPHA_DISABLED;
				return;
			}
			// Cannot select more
			if (Lunar.Object.length(this._selectedChars) >= 4) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}			
			this.game.sfx('resources/translune/static/ping');
			this._selectedChars[characterState.id] = characterState;
			icon.alpha = 1;
			this.hierarchy.frame.buttons.$battle.alpha = Lunar.Object.length(this._selectedChars) === 4 ? 1 : ALPHA_DISABLED; 
		}
			
		_onClickItem(item) {
			// Deselect
			if (this._selectedItem[item.name]) {
				this.game.sfx('resources/translune/static/cancel');
				delete this._selectedItem[item.name];
				icon.alpha = ALPHA_DISABLED;
				this.hierarchy.frame.buttons.$battle.alpha = ALPHA_DISABLED;
				return;
			}
			// Cannot select more
			if (Lunar.Object.length(this._selectedItems) >= 3) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}			
			this.game.sfx('resources/translune/static/ping');
			this._selectedItems[item.name] = item;
			icon.alpha = 1;
		}
	}
})(window.Lunar, window);