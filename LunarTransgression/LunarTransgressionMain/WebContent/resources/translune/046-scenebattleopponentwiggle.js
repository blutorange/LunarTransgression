/**
 * Logo rotates in.
 */
(function(Lunar, window, undefined) {
	const CURTAIN_OVERLAP = 0.05;
	Lunar.Scene.BattleOpponentWiggle = class extends Lunar.Scene.Base {
		constructor(battle, {duration = 10, frequency = 1/2} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._duration = duration;
			this._frequency2Pi = 2*Math.PI*frequency;
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

			this._cube.resetWorldTransform();
			this._cube.resetLocalTransform();
			this._cube.setDimensions(this.game.dx(0.15),this.game.dy(0.15), this.game.dx(0.03));
			this._cube.translate3(this.game.x(0), this.game.y(0), this.game.dx(0.50+0.20));
		    this._cube.rotate3Deg(10, [1,0,0]);
		    this._cube.rotate3Deg(5, [0,0,1]);
		}
		
		update(delta) {
			super.update(delta);
			let time = this.time-this._startTime;
			if (time >= this._duration) {
				if (!this._done)
					this.emit('avatar-wiggle');
				this._done = true;
			}
            const angle = 25*this.game.fmath.sin(this._frequency2Pi*time);
	        this._field.preRotate3Deg(angle, [0,0,1]);
	        this._fieldRotation = cameraDegree;
			this._field.update();

		}		
		
		_initScene() {
            this._cube = new Lunar.PixiCube({
                front: this._battle.resources.packed.spritesheet.textures['u_ava.png'],
                back: PIXI.Texture.EMPTY,
                left: resources.field.spritesheet.textures["avatarside.png"]
                right: resources.field.spritesheet.textures["avatarside.png"]
                top: resources.field.spritesheet.textures["avatarside.png"]
                bottom: resources.field.spritesheet.textures["avatarside.png"]
            });
            const containerCube = this._cube.container;
			this.view.addChild(this._cube.container);
            logo.anchor.set(0.5,0.5);
			this.hierarchy = {
                $cube: containerCube
			};
		}
	}
})(window.Lunar, window);
