(function(Lunar, window, undefined) {
	const LIST_CHAR_X = 5;
	const LIST_CHAR_Y = 6;
	const LIST_ITEM_X = 5;
	const LIST_ITEM_Y = 6;
	const MAX_SELECT_CHARACTER = 1;
	const MAX_SELECT_ITEM = 1;
	const ALPHA_DISABLED = 0.25;
	
	// TODO Check for max chars / max items (28/28)
	Lunar.Scene.BattleLoot = class extends Lunar.Scene.Base {
		
		constructor(battle) {
			super(battle.game);
			this._batle = battle;
			this._lootableStuff = undefined;
			this._loadScene = undefined;
			this._selectedChars = {};
			this._selectedItems = {};
		}
		
		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				fetch: Lunar.FetchType.lootableStuff
			});
			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, delegateLoadable);

			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);

			requestLoadable.promise
				.then(response => _this._initLoader(delegateLoadable, response))
				.catch(error =>  {
					console.log("failed to load loot data", error);
					_this.game.removeScene(this._loadScene);
					_this.emit('loot-error', error);
				});
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		destroy() {
			this._battle = undefined;
			this._lootableStuff = undefined;
			this._loadScene = undefined;
			this._selectedChars = undefined;
			this._selectedItems = undefined;
			this.game.loaderFor('battle-loot').reset();
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
			
			this.geo(h.frame.buttons.$loot, geoButtons[0]);
			this.geo(h.frame.buttons.$cancel, geoButtons[1]);
			
			this.layoutButtonText(h.frame.buttons.loot.$text, true);
			this.layoutButtonText(h.frame.buttons.cancel.$text, true);
			
			geoIcons.flatten().forEach((geo, index) => {
				const characterState = this._lootableStuff.characterStates[index];
				if (characterState) {
					this.geo(h.frame.mid.chars.icons[index].$icon, geo, {anchor: 0.5, proportional: true});
					this.geo(h.frame.mid.chars.icons[index].$level, geo, {anchor: 0.80, keepSize: true});
				}
			});
			
			geoItems.flatten().forEach((geo, index) => {
				const item = this._lootableStuff.items[index];
				if (item) {
					this.geo(h.frame.mid.items.icons[index].$icon, geo, {anchor: 0.5, proportional: true});
				}
			});
			
			super.layout();
		}
		
		get menu() {
			return this._battle;
		}
		
		_initLoader(delegateLoadable, response) {
			this._lootableStuff = response.data.data;
			const icons = this._lootableStuff.characterStates.map(characterState => characterState.imgIcon).join(',');
			const items = this._lootableStuff.items.map(item => item.imgIcon).join(',');
			const loader = this.game.loaderFor("battle-loot");
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
			
			const loader = this.game.loaderFor('battle-loot');		
			const overlay = new PIXI.Graphics();
			const frame = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const label = new PIXI.Text("Select up to 1 character and 1 item to loot.", Lunar.FontStyle.inviteReceiveTitle);

			overlay.interactive = true;
			
			const buttonLoot = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const buttonCancel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const textLoot = this.createButtonText(buttonLoot, "Loot!");
			const textCancel = this.createButtonText(buttonCancel, "Be nice!");
			buttonLoot.alpaha = ALPHA_DISABLED;
			buttonLoot.on('pointertap', this._onClickLoot, this);
			buttonCancel.on('pointertap', this._onClickCancel, this);
			
			const containerMid = new PIXI.ClipContainer();
			const containerIcons = new PIXI.ClipContainer();
			const containerItems = new PIXI.ClipContainer();
			const containerButtons = new PIXI.ClipContainer();
			
			const icons = this._lootableStuff.characterStates.map(characterState => {
				const texture = loader.resources.icons.spritesheet.textures[characterState.imgIcon];
				const icon = new PIXI.Sprite(texture);
				const level = new PIXI.Text(characterState.level, Lunar.FontStyle.charIconLevel);
				containerIcons.addChild(icon);
				containerIcons.addChild(level);
				icon.interactive = true;
				icon.buttonMode = true;
				icon.alpha = ALPHA_DISABLED;
				icon.on('pointertap', () => this._onClickChar(characterState, icon), this);
				return {
					$icon: icon,
					$level: level
				};
			});
			
			const iconsItem = this._lootableStuff.items.map(item => {
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

			this.view.addChild(overlay);
			this.view.addChild(frame);
			
			frame.body.addChild(label);
			frame.body.addChild(containerMid);
			frame.body.addChild(containerButtons);
			
			containerMid.addChild(containerIcons);
			containerMid.addChild(containerItems);

			containerButtons.addChild(buttonLoot);
			containerButtons.addChild(buttonCancel);
			buttonLoot.body.addChild(textLoot);
			buttonCancel.body.addChild(textCancel);
							
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
						loot: {
							$text: textLoot,
						},
						cancel: {
							$text: textCancel,
						},
						$loot: buttonLoot,
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
		
		_onClickLoot() {
			if (Lunar.Object.length(this._selectedChars) > MAX_SELECT_CHARACTER || Lunar.Object.length(this._selectedItems) > MAX_SELECT_ITEM) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/confirm');
			this.emit('loot-perform', Object.values(this._selectedChars), Object.values(this._selectedItems));
		}
		
		_onClickCancel() {
			this.game.sfx('resources/translune/static/cancel');
			this.emit('loot-cancel');
		}
		
		_onClickChar(characterState, icon) {
			// Deselect
			if (this._selectedChars[characterState.id]) {
				this.game.sfx('resources/translune/static/cancel');
				delete this._selectedChars[characterState.id];
				icon.alpha = ALPHA_DISABLED;
				this.hierarchy.frame.buttons.$loot.alpha = ALPHA_DISABLED;
				return;
			}
			// Cannot select more
			if (Lunar.Object.length(this._selectedChars) >= MAX_SELECT_CHARACTER) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}			
			this.game.sfx('resources/translune/static/ping');
			this._selectedChars[characterState.id] = characterState;
			icon.alpha = 1;
			this.hierarchy.frame.buttons.$loot.alpha = Lunar.Object.length(this._selectedChars) > 0 || Lunar.Object.length(this._selectedItems) > 0 ? 1 : ALPHA_DISABLED; 
		}
			
		_onClickItem(item) {
			// Deselect
			if (this._selectedItem[item.name]) {
				this.game.sfx('resources/translune/static/cancel');
				delete this._selectedItem[item.name];
				icon.alpha = ALPHA_DISABLED;
				this.hierarchy.frame.buttons.$loot.alpha = ALPHA_DISABLED;
				return;
			}
			// Cannot select more
			if (Lunar.Object.length(this._selectedItems) >= MAX_SELECT_ITEM) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}			
			this.game.sfx('resources/translune/static/ping');
			this._selectedItems[item.name] = item;
			icon.alpha = 1;
			this.hierarchy.frame.buttons.$loot.alpha = Lunar.Object.length(this._selectedChars) > 0 || Lunar.Object.length(this._selectedItems) > 0 ? 1 : ALPHA_DISABLED;
		}
	}
})(window.Lunar, window);