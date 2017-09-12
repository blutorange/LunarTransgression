/**
 * Fades out the opponent cube.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {	
	const AMPLITUDE = 35;
	const TILT_FRONT = 20;
	const WIDTH = 0.13;
	const HEIGHT = 0.13;
	const DEPTH = 0.01;
	const CENTER_X = -0.1;
	const CENTER_Y = 0.45;
	const CENTER_Z = 0.50+0.20;
	const MAX_SCALE = 25;

	Lunar.Scene.BattleOpponentOut = class extends Lunar.Scene.Base {
		constructor(battle, cube, {duration = 1, frequency = 5} = {}) {
			super(battle.game);
			this._cube = cube;
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._frequency2Pi = 2*Math.PI*frequency;
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
		
		layout() {
			super.layout();
			const h = this.hierarchy;			

			const cubeSize = Math.min(this.game.dx(WIDTH), this.game.dy(HEIGHT));
			
			this._cube.resetWorldTransform();
			this._cube.resetLocalTransform();
			this._cube.setDimensions(cubeSize, cubeSize, this.game.dx(DEPTH));
			this._cube.translate3(this.game.x(CENTER_X), this.game.y(CENTER_Y), this.game.dx(CENTER_Z));
		    this._cube.rotate3Deg(TILT_FRONT, [1,0,0]);
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
			
			const alpha = Lunar.Interpolation.slowInFastOutOnce(t);
            const angle = AMPLITUDE * this.game.fmath.sin(this._frequency2Pi*time);
            this._cube.preRotate3Deg(angle, [0,0,1]);
            this._cube.translate3(0, this.game.h*alpha*2, 0);
            this._cube.scale3(1+MAX_SCALE*alpha);
			this._cube.container.alpha = 1-alpha;
            this._cube.update();
		}
		
		_initScene() {
		}
	}
})(window.Lunar, window);