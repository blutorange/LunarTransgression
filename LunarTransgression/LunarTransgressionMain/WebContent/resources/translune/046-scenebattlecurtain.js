/**
 * 
 */
(function(Lunar, window, undefined) {
	const CURTAIN_OVERLAP = 0.05;
	Lunar.Scene.BattleCurtain = class extends Lunar.Scene.Base {
		constructor(battle, curtainLeft, curtainRight, {duration = 3} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._curtainLeft = curtainLeft;
			this._curtainRight = curtainRight;
			this._done = false;
		}
		
		destroy() {
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			this._startTime = this.time;
			super.onAdd();
		}

		onRemove() {
			super.onRemove();
		}
		
		layout() {			
			super.layout();
			
			this._curtainLeft.width = this.game.w*(0.5+CURTAIN_OVERLAP);
			this._curtainLeft.height = this.game.h;
			this._curtainLeft.y = 0;
			
			this._curtainRight.width = this.game.w*(0.5+CURTAIN_OVERLAP);
			this._curtainRight.height = this.game.h;
			this._curtainRight.y = 0;
		}
		
		update(delta) {
			super.update(delta);
			const l = this._curtainLeft;
			const r = this._curtainRight;
			let t = this._inverseDuration * (this.time-this._startTime);
			
			if (t > 1) {
				t = 1;
				if (!this._done)
					this.emit('animation-done', {scene: this});
				this._done = true;
			}

			const alpha = Lunar.Interpolation.slowInFastOutOnce(t);

//			const lFrom = this.game.dx(0.5+CURTAIN_OVERLAP);
//			const rFrom = this.game.dx(0.5-CURTAIN_OVERLAP);
//			const lTo = this.game.dx(-0.2);
//			const rTo = this.game.dx(0.2);
//			l.x = lFrom + alpha*(lTo-lFrom);
//			r.x = rFrom + alpha*(rTo-rFrom);
			l.x = this.game.w * (0.5+CURTAIN_OVERLAP) * (1-alpha);
			r.x = this.game.w * ((0.5-CURTAIN_OVERLAP) + alpha*(0.5+CURTAIN_OVERLAP));
			
			l.skew.x = -0.5*alpha;
			r.skew.x = 0.5*alpha;
			l.height = this.game.h*(1+alpha*0.2);
			r.height = this.game.h*(1+alpha*0.2);
		}		
		
		_initScene() {		
			this._curtainLeft.anchor.set(1, 0);
			this._curtainRight.anchor.set(0, 0);			
		}
	}
})(window.Lunar, window);
