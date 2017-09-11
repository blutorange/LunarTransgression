/**
 * 
 */
(function(Lunar, window, undefined) {
	const CURTAIN_OVERLAP = 0.05;
	Lunar.Scene.BattleCurtain = class extends Lunar.Scene.Base {
		constructor(battle, {duration = 3} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
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
			const h = this.hierarchy;
			
			h.$curtainLeft.width = this.game.w*(0.5+CURTAIN_OVERLAP);
			h.$curtainLeft.height = this.game.h;
			h.$curtainLeft.y = 0;
			
			h.$curtainRight.width = this.game.w*(0.5+CURTAIN_OVERLAP);
			h.$curtainRight.height = this.game.h;
			h.$curtainRight.y = 0;
		}
		
		update(delta) {
			super.update(delta);
			const l = this.hierarchy.$curtainLeft;
			const r = this.hierarchy.$curtainRight;
			let t = this._inverseDuration * (this.time-this._startTime);
			
			if (t > 1) {
				t = 1;
				if (!this._done)
					this.emit('curtain-done');
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
			const curtainLeft = new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['curtainleft.png']);
			const curtainRight = new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['curtainright.png']);
			
			this.view.addChild(curtainRight);
			this.view.addChild(curtainLeft);
			
			curtainLeft.anchor.set(1, 0);
			curtainRight.anchor.set(0, 0);
			
			this.hierarchy = {
				$curtainLeft: curtainLeft,
				$curtainRight : curtainRight,
			};
		}
	}
})(window.Lunar, window);