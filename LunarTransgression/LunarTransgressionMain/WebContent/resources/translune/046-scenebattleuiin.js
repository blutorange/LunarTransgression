/**
 * Logo rotates in.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {
	Lunar.Scene.BattleUiIn = class extends Lunar.Scene.Base {
		constructor(battle, challenger, {duration = 1.5} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._challenger = challenger;
			this._done = false;
		}
		
		onAdd() {
			this._initScene();
			this._startTime = this.time;
			super.onAdd();
		}

		onRemove() {
			super.onRemove();
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
			this._battle.hierarchy.$ui.alpha = alpha;
		}		
		
		_initScene() {
			this._battle.hierarchy.$ui.alpha = 0;
			this._battle.hierarchy.$ui.visible = true;
			this._battle.textScene.pushText(`You were challenged by the opponent ${this._challenger}!&`)
		}
	}
})(window.Lunar, window);