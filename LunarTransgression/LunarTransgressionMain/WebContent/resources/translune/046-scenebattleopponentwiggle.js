/**
 * Logo rotates in.
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
	
	Lunar.Scene.BattleOpponentWiggle = class extends Lunar.Scene.Base {
		constructor(battle, cube, {duration = 4.5, frequency = 1/2} = {}) {
			super(battle.game);
			this._cube = cube;
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._frequency2Pi = 2*Math.PI*frequency;
			this._done = false;
		}
		
		destroy() {
			this._cube = undefined;
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

			const cubeSize = Math.min(this.game.dx(WIDTH), this.game.dy(HEIGHT));
			
			this._cube.resetWorldTransform();
			this._cube.resetLocalTransform();
			this._cube.setDimensions(cubeSize, cubeSize, this.game.dx(DEPTH));
			this._cube.translate3(this.game.x(CENTER_X), this.game.y(CENTER_Y), this.game.dx(CENTER_Z));
		    this._cube.rotate3Deg(TILT_FRONT, [1,0,0]);
		    //this._cube.rotate3Deg(5, [0,0,1]);
		}
		
		update(delta) {
			super.update(delta);
			this.layout();
			
			let time = this.time - this._startTime;
			if (time >= this._duration) {
				if (!this._done)
					this.emit('animation-done', {scene: this});
				this._done = true;
			}
            const angle = AMPLITUDE*this.game.fmath.sin(this._frequency2Pi*time);
            this._cube.preRotate3Deg(angle, [0,0,1]);
            this._cube.update();
		}		
		
		_initScene() {
			this._cube.container.visible = true;
		}
		
		static createCube(battle) {
			const r = battle.resources;
            return new Lunar.PixiCube({
                front: r.u_ava.texture,
                back: PIXI.Texture.WHITE,
                left: r.packed.spritesheet.textures["avatarside.png"],
                right: r.packed.spritesheet.textures["avatarside.png"],
                top: r.packed.spritesheet.textures["avatarside.png"],
                bottom: r.packed.spritesheet.textures["avatarside.png"]
            });
		}
	}
})(window.Lunar, window);