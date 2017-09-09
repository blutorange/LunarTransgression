/**
 * 
 */
(function(Lunar, window, undefined) {
	const LIST_X = 7;
	const LIST_Y = 4;
	
	Lunar.Scene.MenuInviteDetails= class extends Lunar.Scene.Base {
		constructor(game, menu, nickname) {
			super(game);
			this._nickname = nickname;
			this._menu = menu;
			this._loaded = false;
			this._loadScene = undefined;
			this._playerDetails = undefined;
			this._dialog = undefined;
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
			this._playerDetails = undefined;
			this._loadScene = undefined;
			this._menu = undefined;
			this._nickname = undefined; 
			this._dialog = undefined;
			this.game.loaderFor('menu-detail').reset();
			super.destroy();
		}
		
		onRemove() {
			this._removeListeners();
			this.game.removeScene(this._loadScene);
			this.game.removeScene(this._dialog);
			super.onRemove();
		}
		
		layout() {
			const h = this.hierarchy;
			const panel = this.view.parent.parent;
			
			const geoBase = Lunar.Geometry.layoutVbox({
				box: panel.bodyDimension,
				dimension: [20,15,65],
				relative: true
			});
			
			const geoTop = Lunar.Geometry.layoutHbox({
				box: geoBase[0],
				dimension: [1,2],
				relative: true
			});
			
			const geoAvatar = Lunar.Geometry.layoutHbox({
				box: geoTop[0],
				padding: {
					left: 0.04,
					right: 0.04,
					top: 0.04,
					bottom: 0.04
				},
				relative: true
			});
			
			const geoDescription = Lunar.Geometry.layoutHbox({
				box: geoBase[1],
				padding: {
					top: 0.3,
					left: 0.05,
					right: 0.05
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
			
			this.geo(h.$top, geoBase[0]);
			this.geo(h.$description, geoBase[1]);
			this.geo(h.$icons, geoBase[2]);
			
			this.geo(h.top.$avatar, geoTop[0]);
			this.geo(h.top.$nickname, geoTop[1], {keepSize: true, anchor: [0,0.5]});
			
			this.geo(h.top.avatar.$image, geoAvatar[0], {proportional: true, anchor: 0.5});
			this.geo(h.description.$text, geoDescription[0], {keepSize: true, anchor: 0.5});
			
			geoIcons.flatten().forEach((geo, index) => {
				const characterState = this._playerDetails.characterStates[index];
				if (characterState) {
					this.geo(h.icons[index].$icon, geo, {anchor: 0.5, proportional: true});
					this.geo(h.icons[index].$level, geo, {anchor: 0.85, keepSize: true});
				}
			});
			
			super.layout();
		}
		
		get menu() {
			return this._menu;
		}

		/**
		 * @private
		 */
		_initLoader(delegateLoadable, response) {
			this._playerDetails = response.data.data;
			const icons = this._playerDetails.characterStates.map(characterState => characterState.imgIcon).join(',');		
			const loader = this.game.loaderFor("menu-detail");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable;
			loader.reset();
			loader.add("icons", `spritesheet/spritesheet.json?resources=${icons}`);
			loader.add("avatar", `resource/${this._playerDetails.imgAvatar}`);
			loader.load();
		}
		
		/**
		 * @private
		 */
		_initScene() {
			this.game.pushScene(this, this._loadScene.view.parent.parent.body);
			this._loadScene = undefined;
			this._loaded = true;
			
			const loader = this.game.loaderFor('menu-detail');
			const avatar = new PIXI.Sprite(loader.resources.avatar.texture);
			const nickname = new PIXI.Text(this._nickname, Lunar.FontStyle.playerTitle);
			const formatted = this._playerDetails.description  ? `»${this._playerDetails.description}«` : '(no description)'
			const description = new PIXI.Text(formatted, Lunar.FontStyle.playerDesc);
			
			const containerIcons = new PIXI.ClipContainer();
			const containerTop = new PIXI.ClipContainer();
			const containerDescription = new PIXI.ClipContainer();
			const containerAvatar = new PIXI.ClipContainer();
			
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
			
			// If we are us, we can change our description.
			if (this._playerDetails.nickname === this.game.net.nickname) {
				description.interactive = true;
				description.buttonMode = true;
				description.on('pointertap', this._onClickDescription, this);
			}
			else {
				// Otherwise we are you and and can challenge us.
				this.menu.actionButton1.visible = true;
				this.menu.actionButton1.body.children[0].text = 'Invite';
				this.menu.actionButton1.on('pointertap', this._onClickInvite, this);
			}
			
			this.view.addChild(containerTop);
			this.view.addChild(containerDescription);
			this.view.addChild(containerIcons);
			containerTop.addChild(containerAvatar);
			containerTop.addChild(nickname);
			containerAvatar.addChild(avatar);
			containerDescription.addChild(description);
			
			this.hierarchy = {
				top: {
					avatar: {
						$image: avatar
					},
					$nickname: nickname,
					$avatar: containerAvatar
				},
				description: {
					$text: description
				},
				icons: icons,
				$top: containerTop,
				$description: containerDescription,
				$icons: containerIcons
			};	
		}
		
		_registerListeners() {
			this.game.net.registerMessageHandler(Lunar.Message.inviteAccepted, {
				handle: this.method('_onMessageAccepted'),
				error: () => null
			});
			this.game.net.registerMessageHandler(Lunar.Message.inviteRejected, {
				handle: this.method('_onMessageRejected'),
				error: () => null
			});
		}
		
		_removeListeners() {
			this.game.net.removeMessageHandlers(Lunar.Message.inviteAccepted);
			this.game.net.removeMessageHandlers(Lunar.Message.inviteRetracted);
		}
		
		_onMessageAccepted() {
			this._removeListeners();
			this.game.removeScene(this._dialog);
			this._dialog = undefined;
			this.menu.onOpponentAccepted(this._nickname);
		}
		
		_onMessageRejected() {
			this._removeListeners();
			this.game.removeScene(this._dialog);
			this._dialog = undefined;
		}
		
		_onClickInvite() {
			const _this = this;
			if (this._dialog)
				return;
			this.game.sfx('resources/translune/static/confirm');
			this.menu.tabModal(true);
			this._dialog = new Lunar.Scene.Dialog(this.game, {
				message: `Now calling ${this._playerDetails.nickname}... prepare for battle!`,
				choices: [
					{
						text: 'Cancel',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/cancel');
							this.game.net.dispatchMessage(Lunar.Message.inviteRetract, {
								nickname: _this._playerDetails.nickname
							}).then(response => {
							}).catch(error => {
								console.error("failed to retract invitation", error);
							}).then(() => {
								_this.menu.tabModal(false);
								_this._removeListeners();
								_this.game.removeScene(this._dialog);
								_this._dialog = undefined;
							});
						},
					}
				]
			});
			this.game.pushScene(this._dialog);
			this._registerListeners();
			this.game.net.dispatchMessage(Lunar.Message.invite, {
				nickname: _this._playerDetails.nickname
			}).then(response => {
				// Server accepted the invitation, now we need to
				// wait for the opponent to answer.
			}).catch(error => {
				console.error("failed to invite player", error);
				_this.menu.tabModal(false);
				_this.game.removeScene(this._dialog);
				_this._dialog = undefined;
				_this.removeListeners();
				_this.showConfirmDialog("Player seems to be offline, please try again later.");
			});			
		}
		
		_onClickDescription() {
			const _this = this;
			if (this._dialog)
				return;
			this.game.sfx('resources/translune/static/ping');
			this._dialog = new Lunar.Scene.Dialog(this.game, {
				message: 'Describe yourself to others',
				prompt: {
					initial: this._playerDetails.description,
					placeholder: 'add...',
					style: Lunar.FontStyle.input,
					minlength: 1,
					maxlength: 50
				},
				choices: [
					{
						text: 'Keep old description',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/cancel');
							_this._dialog = undefined;
							dialog.close();
						}
					},					
					{
						text: 'Change description',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/confirm');
							const desc = dialog.prompt.text;
							_this._dialog = undefined;
							dialog.close();
							_this._changeDescription(desc);
						},
					}
				]
			});
			this.game.pushScene(this._dialog);
		}
		
		_changeDescription(description) {
			const _this = this;
			if (description === this._playerDetails.description || !description)
				return;
			this.game.net.dispatchMessage(Lunar.Message.updateData, {
				update: Lunar.UpdateType.playerDescription,
				details: description
			}).then(response => {
				_this._playerDetails.description = response.data.data;
				_this.hierarchy.description.$text.text = `»${_this._playerDetails.description}«`;
			}).catch(() => {
				_this.showConfirmDialog("Could not change the description, please try again later.");
			});
		}
	}
})(window.Lunar, window);