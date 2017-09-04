/**
 * 
 */
(function(Lunar, window, undefined) {
	Lunar.Scene.MenuInviteDetails= class extends Lunar.Scene.Base {
		constructor(game, menu) {
			super(game);
			this._menu = menu;
			this._loadScene = undefined;
		}
		
		destroy() {
			this._loadScene = undefined;
			this._menu = undefined;
			this.game.loaderFor("menu-tab").reset();
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
		}
		
		onRemove() {
			if (this._loadScene)
				this.game.removeScene(this._loadScene);
			super.onRemove();
		}
		
		layout() {
			const h = this.hierarchy;
			const panel = this.view.parent.parent;
			const _this = this;
			
			super.layout();
		}
		
		get menu() {
			return this._menu;
		}

		/**
		 * @private
		 */
		_initScene() {
			
			this.hierarchy = {
			};	
		}
		
	}
})(window.Lunar, window);