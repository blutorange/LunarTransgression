/**
 * Logo rotates in.
 */
(function(Lunar, window, undefined) {
	const CURTAIN_OVERLAP = 0.05;
	Lunar.Scene.BattleLogoOut = class extends Lunar.Scene.Base {
		constructor(battle, {duration = 1} = {}) {
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
            h.$battleText.position.set(this.game.w*0.5, this.game.h*0.5);
		}
		
		update(delta) {
			super.update(delta);
			let t = this._inverseDuration * (this.time-this._startTime);
			if (t > 1) {
				t = 1;
				if (!this._done)
					this.emit('logo-out');
				this._done = true;
			}
			const alpha = Lunar.Interpolation.slowInFastOutOnce(t);
            h.$logo.scale.set(1-alpha2);
            h.$logo.alpha = 1-alpha;
		}		
		
		_initScene() {
            const logo = new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battletext.png']);
			this.view.addChild(battleText);
            logo.anchor.set(0.5,0.5);
            logo.rotation = 0;
			this.hierarchy = {
				$logo: logo,
			};
		}
	}
})(window.Lunar, window);
