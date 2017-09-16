/**
 * Logo rotates in.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {
	const CENTER_X = 0.5;
	const CENTER_Y = 0.5;
	const MAX_ROTATION = 360*4*Lunar.Constants.degToRad;
	const MAX_SCALE = 5;
	
	Lunar.Scene.BattleLogoIn = class extends Lunar.Scene.Base {
		constructor(battle, spriteLogo, {duration = 1.9} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._spriteLogo = spriteLogo;
			this._sfxPlayed = false;
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
			this.game.sfx('resources/translune/static/battle/sentokaisi', 1);
		}

		onRemove() {
			super.onRemove();
		}
		
		layout() {			
			super.layout();
			this._spriteLogo.position.set(this.game.w*CENTER_X, this.game.h*CENTER_Y);
		}
		
		update(delta) {
			super.update(delta);
			let t = this._inverseDuration * (this.time-this._startTime);
						
			if (t > 1) {
				t = 1;
				if (!this._done) {
					this.game.sfx('resources/translune/static/battle/battleinned', 1);
					this.emit('animation-done', {scene: this});
				}
				this._done = true;
			}
//			if (t > 0.3 && !this._sfxPlayed) {
//				this.game.sfx('resources/translune/static/battle/battlein', 1);
//				this._sfxPlayed = true;
//			}

			const alpha1 = Lunar.Interpolation.slowInFastOutOnce(t);
			const alpha2 = Lunar.Interpolation.fastInSlowOutOnce(t);

            this._spriteLogo.rotation = alpha2*MAX_ROTATION;
            this._spriteLogo.scale.set(1+(MAX_SCALE-1)*(1-alpha2));
            this._spriteLogo.alpha = alpha1;
		}		
		
		_initScene() {
			this._spriteLogo.anchor.set(0.5,0.5);
			this._spriteLogo.visible = true;
		}
	}
})(window.Lunar, window);