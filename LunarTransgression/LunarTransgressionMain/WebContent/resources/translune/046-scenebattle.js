/**
 * 
 */
(function(Lunar, window, undefined) {
	/**
	 * The main menu.
	 */
	Lunar.Scene.Battle = class extends Lunar.Scene.Base {
		constructor(game) {
			super(game);
			this._loaded = false;
			this._loadScene = undefined
		}
		
		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				 fetch: Lunar.FetchType.availableBgAndBgm
			});
//			const requestLoadableInvitation = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
//				 fetch: Lunar.FetchType.openInvitations
//			});
			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, delegateLoadable);
			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);
			
			requestLoadable.promise.then(response => _this._initLoader(delegateLoadable, response));
//			requestLoadableInvitation.promise.then(response => this._invitations = response.data.data);
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		destroy() {
			//
			this._loadScene = undefined
			super.destroy();
		}
		
		onAdd() {
			super.onAdd();
		}
		
		onRemove() {
			super.onRemove();
		}

		update(delta) {
			super.update(delta);
		}

		layout() {
			super.layout();
			const h = this.hierarchy;
			h.$background.width = this.game.w;
			h.$background.height = this.game.h;

		}

		/**
		 * @private
		 */
		_initLoader(delegateLoadable, response) {		
			const background = Lunar.Object.randomEntry(response.data.data.bgBattle).value;
			const music = Lunar.Object.randomEntry(response.data.data.bgmBattle).value;
			
			this.game.switchBgm(music.map(file => `resource/${file}`));
			
			const loader = this.game.loaderFor("menu");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable; 
			loader.reset();
			loader.add("bg", `resource/${background}`);
			loader.load();
		}
		
		_initScene() {
			this._loadScene = undefined;
			this._loaded = true;
			this.game.pushScene(this);
			const _this = this;			

			//Background
			const bg = new PIXI.Sprite(this.game.loaderFor("menu").resources.bg.texture);
			
			this.view.addChild(bg);

			this.hierarchy = {
				$background: bg
			};
		}
	}
})(window.Lunar, window);