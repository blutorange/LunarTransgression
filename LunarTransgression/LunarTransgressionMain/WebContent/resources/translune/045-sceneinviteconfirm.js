(function(Lunar, window, undefined) {
	const LIST_X = 7;
	const LIST_Y = 4;
	Lunar.Scene.InviteConfirm = class extends Lunar.Scene.Base {
		constructor(menu, nickname) {
			super(menu.game);
			this._dialog = undefined;
			this._loadScene = undefined;
			this._nickname = nickname;
			this._menu = menu;
		}
		
		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				fetch: Lunar.FetchType.playerDetail,
				details: this._nickname
			});
			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, delegateLoadable);

			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);

			requestLoadable.promise
				.then(response => _this._initLoader(delegateLoadable, response))
				.catch(error =>  {
					console.log("failed to load opponent data", error);
					_this.showConfirmDialog("Could not load opponent data, please try again later.");
					_this.game.removeScene(this._loadScene);
					_this.game.removeScene(this);
				});
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		destroy() {
			this._menu = undefined;
			this._nickname = undefined;
			this._loadScene = undefined;
			this._playerDetails = undefined;
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
					left: 0.25,
					right: 0.25,
					top: 0.1,
					bottom: 0.1
				},
				relative: true
			});
			
			this.geo(h.$frame, geoRoot[0]);
			
			const geoBase = Lunar.Geometry.layoutVbox({
				box: h.$frame.bodyDimension,
				dimension: [10,10,60,20],
				padding: {
					y: 0.05,
					top: 0.04,
					left: 0.08,
					right: 0.08,
					bottom: 0.04
				},
				relative: true
			});
			
			const geoIcons = Lunar.Geometry.layoutGrid({
				box: geoBase[2],
				dimension: {
					n: LIST_X,
					m: LIST_Y
				},
				relative: true
			});
			
			const geoButtons = Lunar.Geometry.layoutHbox({
				box: geoBase[3],
				dimension: 2,
				padding: {
					x: 0.05
				},
				relative: true
			});
			
			h.$overlay.clear();
			h.$overlay.beginFill(0x222222, 0.85);
			h.$overlay.drawRect(0, 0, this.game.w, this.game.h);
			h.$overlay.endFill();

			this.geo(h.frame.$label, geoBase[0], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.$nickname, geoBase[1], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.$icons, geoBase[2]);
			this.geo(h.frame.$buttons, geoBase[3]);
			
			this.geo(h.frame.buttons.$accept, geoButtons[0]);
			this.geo(h.frame.buttons.$reject, geoButtons[1]);
			
			this.layoutButtonText(h.frame.buttons.accept.$text, true);
			this.layoutButtonText(h.frame.buttons.reject.$text, true);
			
			geoIcons.flatten().forEach((geo, index) => {
				const characterState = this._playerDetails.characterStates[index];
				if (characterState) {
					this.geo(h.frame.icons[index].$icon, geo, {anchor: 0.5, proportional: true});
					this.geo(h.frame.icons[index].$level, geo, {anchor: 0.85, keepSize: true});
				}
			});
			
			super.layout();
		}
		
		get menu() {
			return this._menu;
		}
		
		_initLoader(delegateLoadable, response) {
			this._playerDetails = response.data.data;
			const icons = this._playerDetails.characterStates.map(characterState => characterState.imgIcon).join(',');		
			const loader = this.game.loaderFor("menu-invite");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable;
			loader.reset();
			loader.add("icons", `spritesheet/spritesheet.json?resources=${icons}`);
			loader.load();
		}
		
		_initScene() {
			this.game.pushScene(this);
			this._loadScene = undefined;
			this._loaded = true;
			
			const overlay = new PIXI.Graphics();
			const frame = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const loader = this.game.loaderFor('menu-invite');
			const label = new PIXI.Text("Received a battle invitation!", Lunar.FontStyle.inviteReceiveTitle);
			const nickname = new PIXI.Text(this._nickname, Lunar.FontStyle.inviteReceiveNickname);
			const formatted = this._playerDetails.description  ? `»${this._playerDetails.description}«` : '(no description)'
			const description = new PIXI.Text(formatted, Lunar.FontStyle.playerDesc);

			const buttonAccept = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const buttonReject = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			
			overlay.interactive = true;
			
			buttonAccept.on('pointertap', this._onClickAccept, this);
			buttonReject.on('pointertap', this._onClickReject, this);
			
			const textAccept = this.createButtonText(buttonAccept, "Accept");
			const textReject = this.createButtonText(buttonReject, "Reject");
			
			const containerIcons = new PIXI.ClipContainer();
			const containerButtons = new PIXI.ClipContainer();
			
			const icons = this._playerDetails.characterStates.map(characterState => {
				const texture = loader.resources.icons.spritesheet.textures[characterState.imgIcon];
				const icon = new PIXI.Sprite(texture);
				const level = new PIXI.Text(characterState.level, Lunar.FontStyle.charIconLevel);
				containerIcons.addChild(icon);
				containerIcons.addChild(level);
				return {
					$icon: icon,
					$level: level
				};
			});
			
			this.view.addChild(overlay);
			this.view.addChild(frame);
			
			frame.body.addChild(label);
			frame.body.addChild(nickname);
			frame.body.addChild(containerIcons);
			frame.body.addChild(containerButtons);
			
			containerButtons.addChild(buttonAccept);
			containerButtons.addChild(buttonReject);
			
			buttonAccept.body.addChild(textAccept);
			buttonReject.body.addChild(textReject);
			
			this.hierarchy = {
				frame: {
					icons: icons,
					buttons: {
						accept: {
							$text: textAccept,
						},
						reject: {
							$text: textReject
						},
						$accept: buttonAccept,
						$reject: buttonReject
					},
					$label: label,
					$nickname: nickname,
					$icons: containerIcons,
					$buttons: containerButtons
				},
				$overlay: overlay,
				$frame: frame
			};
		}
		
		_onClickAccept() {
			const _this = this;
			this.game.sfx('resources/translune/static/confirm');
			this.game.net.dispatchMessage(Lunar.Message.inviteAccept, {
				nickname: this._nickname
			}).then(response => {
				_this.menu.closeInviteConfirm();
				_this.menu.onOpponentAccepted(this._nickname);
			}).catch(error => {
				console.error("could not accept invitation", error);
				_this.showConfirmDialog("Invitation could not be accepted, please try again.");
			});
		}
		
		_onClickReject() {
			const _this = this;
			this.game.sfx('resources/translune/static/confirm');
			this.game.net.dispatchMessage(Lunar.Message.inviteReject, {
				nickname: this._nickname
			}).catch(error => {
				console.error("failed to reject invitation", error);
			});
			this._menu.closeInviteConfirm();
		}
	}
})(window.Lunar, window);