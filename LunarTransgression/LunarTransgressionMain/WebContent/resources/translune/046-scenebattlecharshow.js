/**
 * Logo rotates in.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {
	const START_X = -0.1;
	const START_Y = 0.45;
	const ROTATION_SPEED = 900*Lunar.Constants.degToRad;
	const BALL_SIZE = 0.1;
	const BALL_REDUCE = 0.8;

	Lunar.Scene.BattleCharShow = class extends Lunar.Scene.Base {
		constructor(battle, battler, {duration = 0.4} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._battler = battler;
			this._initialScale = 1;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._filter = new PIXI.filters.ColorMatrixFilter();
			this._done = false;
		}
		
		destroy() {
			this._battle = undefined;
			this._battler = undefined;
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			this._startTime = this.time;
			super.onAdd();
			this.game.sfx(`resource/${this._battler.characterState.cry}`, 1);
		}

		onRemove() {
			this._battler._sprite.filters = [];
            this._battler.setScaling(1);
			super.onRemove();
		}
		
		layout() {			
			super.layout();
		}
		
		update(delta) {
			super.update(delta);
			let t = this._inverseDuration * (this.time-this._startTime);
						
			if (t > 1) {
				t = 1;
				if (!this._done) {
					this.emit('animation-done', {scene: this});
				}
				this._done = true;
			}

			const alpha = Lunar.Interpolation.fastInSlowOutOnce(t);
            this._battler.setScaling(alpha);
            this._filter.matrix[4] = 1.5*(1-alpha);
            this._filter.matrix[9] = 1.5*(1-alpha);
            this._filter.matrix[14] = 1.5*(1-alpha);
		}		
		
		_initScene() {
			this._battler._sprite.filters = [this._filter];
			this._battler.setScaling(0);
			this._battler.view.visible = true;
		}
	}
})(window.Lunar, window);