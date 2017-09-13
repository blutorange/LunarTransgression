/**
 * Logo rotates in.
 */
(function(Lunar, window, undefined) {
	const CENTER_X = 0.5;
	const CENTER_Y = 0.5;
	
	Lunar.Scene.BattleLogoOut = class extends Lunar.Scene.Base {
		constructor(battle, spriteLogo, {duration = 1.5} = {}) {
			super(battle.game);
			this._spriteLogo = spriteLogo;
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._done = false;
		}
		
		destroy() {
			this._spriteLogo = undefined;
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			this._startTime = this.time;
			super.onAdd();
//			this.game.sfx('resources/translune/static/battle/battlein', 1);
		}

		onRemove() {
			this._spriteLogo.visible = false;
			super.onRemove();
		}
		
		layout() {			
			super.layout();
			const h = this.hierarchy;			
			this._spriteLogo.position.set(this.game.w*CENTER_X, this.game.h*CENTER_Y);
		}
		
		update(delta) {
			super.update(delta);
			let t = this._inverseDuration * (this.time-this._startTime);
			if (t > 1) {
				t = 1;
				if (!this._done)
					this.emit('animation-done', {scene: this});
				this._done = true;
			}
			const alpha = Lunar.Interpolation.slowInFastOutOnce(t);
			this._spriteLogo.scale.set(1-0.5*alpha);
			this._spriteLogo.alpha = 1-0.5*alpha;
			this._spriteLogo.y = this.game.h*0.5*(1-1.8*alpha);
		}		
		
		_initScene() {
			this._spriteLogo.anchor.set(0.5,0.5);
			this._spriteLogo.rotation = 0;
			this._spriteLogo.visible = true;
		}
	}
})(window.Lunar, window);