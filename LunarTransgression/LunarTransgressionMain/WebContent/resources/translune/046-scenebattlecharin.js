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

	Lunar.Scene.BattleCharIn = class extends Lunar.Scene.Base {
		constructor(battle, ball, battler, {duration = 0.75} = {}) {
			super(battle.game);
			this._startTime = 0;
			this._battle = battle;
			this._battler = battler;
			this._initialScale = 1;
			this._ball = ball;
			this._duration = duration;
			this._inverseDuration = 1/duration;
			this._filter = new PIXI.filters.ColorMatrixFilter();
			this._done = false;
		}
		
		destroy() {
			this._spriteLogo = undefined;
			this._battle = undefined;
			this._battler = undefined;
			this._ball = undefined;
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			this._startTime = this.time;
			super.onAdd();
			this._ball.visible = true;
		}

		onRemove() {
			this._ball.filters = [];
			this._ball.visible = false;
			super.onRemove();
		}
		
		layout() {			
			super.layout();
			Lunar.Geometry.proportionalScale(this._ball, this.game.dx(BALL_SIZE), this.game.dy(BALL_SIZE));
			this._initialScale = this._ball.scale.x;
			const targetPosition = this._battler.getPosition();
			const startPositionX = this.game.x(START_X);
			const startPositionY = this.game.y(START_Y);
			const midPositionX = startPositionX + 0.5*(targetPosition.x-startPositionX);
			const midPositionY = startPositionY + 0.5*(targetPosition.x-startPositionY) - this.game.dy(0.6);
			this._bezier = Lunar.Interpolation.quadraticBezierFromPoints(
					startPositionX, startPositionY,
					midPositionX, midPositionY,
					targetPosition.x, targetPosition.y
			);
		}
		
		update(delta) {
			super.update(delta);
			let t = this._inverseDuration * (this.time-this._startTime);
						
			if (t > 1) {
				t = 1;
				if (!this._done) {
					this.game.sfx('resources/translune/static/battle/battleinned', 1);
					this.emit('animation-done', {scene: this});
				}
				this._done = true;
			}

			const alpha = Lunar.Interpolation.slowInFastOutOnce(t);
//			const alpha2 = Lunar.Interpolation.fastInSlowOutOnce(t);
			const position = this._bezier(t);
			this._ball.position.set(position.x, position.y);
            this._ball.rotation += delta*ROTATION_SPEED;
            this._ball.scale.set(this._initialScale*(1-BALL_REDUCE*alpha));
            this._filter.matrix[4] = 1.5*alpha;
            this._filter.matrix[9] = 1.5*alpha;
            this._filter.matrix[14] = 1.5*alpha;
		}		
		
		_initScene() {
			this._ball.anchor.set(0.5,0.5);
			this._ball.filters = [this._filter];
		}
	}
})(window.Lunar, window);