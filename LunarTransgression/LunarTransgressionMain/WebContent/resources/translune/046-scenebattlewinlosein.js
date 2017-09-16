/**
 * Logo rotates in.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {
	const CENTER_X = 0.5;
	const CENTER_Y = 0.4;

	const MAX_ROTATION = (360*4-15)*Lunar.Constants.degToRad;
	const MAX_SCALE = 5;
	
	Lunar.Scene.BattleWinLoseIn = class extends Lunar.Scene.Base {
		constructor(battle, isVictory, {duration = 1.9} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._isVictory = isVictory;
			this._done = false;
		}
		
		destroy() {
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			this._startTime = this.time;
			super.onAdd();
			this.game.switchBgm(`static/battle/theme${this._isVictory ? 'win' : 'lose'}`);
		}

		onRemove() {
			this.game.sfx(`resources/translune/static/battle/${this._isVictory ? 'win' : 'lose'}`, 1);
			super.onRemove();
		}
		
		layout() {			
			super.layout();
			const logo = this._isVictory ? this._battle.hierarchy.other.$win : this._battle.hierarchy.other.$lose;
			logo.position.set(this.game.w*CENTER_X, this.game.h*CENTER_Y);
			Lunar.Geometry.proportionalScale(logo, this.game.w * 1, this.game.h * 0.55);
		}
		
		update(delta) {
			super.update(delta);
			const logo = this._isVictory ? this._battle.hierarchy.other.$win : this._battle.hierarchy.other.$lose;
			let t = this._inverseDuration * (this.time-this._startTime);
						
			if (t > 1) {
				t = 1;
				if (!this._done)
					this.emit('animation-done', {scene: this});
				this._done = true;
			}

			const alpha1 = Lunar.Interpolation.slowInFastOutOnce(t);
			const alpha2 = Lunar.Interpolation.fastInSlowOutOnce(t);

            logo.rotation = alpha2*MAX_ROTATION;
            logo.scale.set(1+(MAX_SCALE-1)*(1-alpha2));
            logo.alpha = alpha1;
		}		
		
		_initScene() {
			const logo = this._isVictory ? this._battle.hierarchy.other.$win : this._battle.hierarchy.other.$lose;
			logo.anchor.set(0.5,0.5);
			logo.visible = true;
		}
	}
})(window.Lunar, window);