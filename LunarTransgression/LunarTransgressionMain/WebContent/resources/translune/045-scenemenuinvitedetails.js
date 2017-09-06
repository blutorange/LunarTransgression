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
			this.game.removeScene(this._loadScene);
			this.game.removeScene(this._dialog);
			super.onRemove();
		}
		
		layout() {
			const h = this.hierarchy;
			const panel = this.view.parent.parent;
			
			const geoBase = Lunar.Geometry.layoutVbox({
				box: panel.bodyDimension,
				dimension: [10,20,70],
				relative: true
			});
			
			const geoDesc = Lunar.Geometry.layoutHbox({
				box: geoBase[1],
				padding: {
					top: 0.2
				},
				relative: false
			});
			
			const geoIcons = Lunar.Geometry.layoutGrid({
				box: geoBase[2],
				dimension: {
					n: LIST_X,
					m: LIST_Y
				},
				relative: true
			});
			
			this.geo(h.$nickname, geoBase[0], {keepSize: true, anchor: 0.5});
			this.geo(h.$description, geoDesc[0], {keepSize: true, anchor: 0.5});
			this.geo(h.$icons, geoBase[2]);
			
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
			const nickname = new PIXI.Text(this._nickname, Lunar.FontStyle.playerTitle);
			const formatted = this._playerDetails.description  ? `»${this._playerDetails.description}«` : '(no description)'
			const description = new PIXI.Text(formatted, Lunar.FontStyle.playerDesc);
			const containerIcons = new PIXI.ClipContainer();
			
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
			
			this.view.addChild(nickname);
			this.view.addChild(description);
			this.view.addChild(containerIcons);
			
			this.hierarchy = {
				icons: icons,
				$nickname: nickname,
				$description: description,
				$icons: containerIcons
			};	
		}
		
		_onClickInvite() {
			const _this = this;
			if (this._dialog)
				return;
			this.game.sfx('resources/translune/static/confirm');
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
								_this.game.removeScene(this._dialog);
								_this._dialog = undefined;
							});
						},
					}
				]
			});
			this.game.pushScene(this._dialog);
			this.game.net.dispatchMessage(Lunar.Message.invite, {
				nickname: _this._playerDetails.nickname
			}).then(response => {
				// Server accepted the invitation, now we need to
				// wait for the opponent to answer.
			}).catch(error => {
				console.error("failed to invite player", error);
				_this.game.removeScene(this._dialog);
				_this._dialog = undefined;
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
				_this.hierarchy.$description.text = `»${_this._playerDetails.description}«`;
			}).catch(() => {
				_this.showConfirmDialog("Could not change the description, please try again later.");
			});
		}
	}
})(window.Lunar, window);