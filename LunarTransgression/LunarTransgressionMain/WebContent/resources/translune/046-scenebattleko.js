/**
 * Fades out the opponent cube.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {	
	Lunar.Scene.BattleKo = class extends Lunar.Scene.Base {
		constructor(battle, battler, {duration = 1} = {}) {
			super(battle.game);
			this._battler = battler;
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._done = false;
		}
		
		onAdd() {
			this._startTime = this.time;
			super.onAdd();
			this.game.sfx('resources/translune/static/battle/ko', 0.75);
		}
		
		destroy() {
			this._battler = false;
			super.destroy();
		}

		onRemove() {
			super.onRemove();
		}
		
		layout() {
			super.layout();
		}
		
		update(delta) {
			super.update(delta);
			this.layout();

			let t = this._inverseDuration * (this.time-this._startTime);
			let time = this.time - this._startTime;
			
			if (t > 1) {
				t = 1;
				if (!this._done) {
					this.emit('animation-done', {scene: this});
				}
				this._done = true;
			}
			
			if (t < 0.1)
				this._battler.alpha = 0.25;
			else {
				const alpha = Lunar.Interpolation.fastInSlowOutOnce(t);
				this._battler.alpha = 1-alpha;
			}
		}
	}
})(window.Lunar, window);