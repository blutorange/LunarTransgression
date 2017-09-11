/**
 * 
 */
(function(Lunar, window, undefined) {
	
	Lunar.Scene.BattleBattler = class extends Lunar.Scene.Base {
		constructor(game, back, front, battleCircle) {
			super(game);
			this._back = Object.values(back.spritesheet.textures);
			this._front = Object.values(front.spritesheet.textures);
			this._mode = 'back';
			this._sprite = new PIXI.extras.AnimatedSprite(this._back);
			this._sprite.anchor.set(0.5,0.5);
			this.view.width = this._sprite.width;
			this.view.height = this._sprite.height;
			this._battleCircle = battleCircle;
			this._scale = 1;
		}
		
		ringPosition(x, y, angle, centerY, radiusY) {
			this.setPosition(x,y);
			this.setScaling(1+this._battleCircle.scale*(y-centerY)/radiusY);
			this.view.zOrder = -y;
			if (angle < Lunar.Constants.deg180AsRad) {
				this.asFront();
				this.setMirroring(angle > Lunar.Constants.deg90AsRad);
			} 
			else {
				this.asBack();
				this.setMirroring(angle > Lunar.Constants.deg270AsRad);
			}
		}
		
		ringPositionOutwards(x, y, angle, centerY, radiusY) {
			this.setPosition(x,y);
			this.setScaling(1+this._battleCircle.scale*(y-centerY)/radiusY);
			this.view.zOrder = -y;
			if (angle < Lunar.Constants.deg180AsRad) {
				this.asBack();
				this.setMirroring(angle > Lunar.Constants.deg90AsRad);
			} 
			else {
				this.asFront();
				this.setMirroring(angle > Lunar.Constants.deg270AsRad);
			}
		}


		asBack() {
			if (this._mode === 'back')
				return;
			this._toSwitch = this._back;
			this._mode = 'back';
		}
		
		update(delta) {
			if (this._toSwitch)
				this._switch(this._toSwitch);
			if (this._dirty) {
				this._rescale();
				this._dirty = false;
			}
		}
		
		asFront() {
			if (this._mode === 'front')
				return;
			this._toSwitch = this._front;
			this._mode = 'front';
		}
		
		layout() {
			this._rescale();
		}
		
		onAdd() {
			this.view.addChild(this._sprite);
			this._sprite.play();
		}
		
		destroy() {
			this._battleCircle = undefined;
			this._back = undefined;
			this._sprite = undefined;
		}
		
		setScaling(scale) {
			this._scale = scale;
			this._dirty = true;
		}
		
		setMirroring(doMirror) {
			const isMirrored = this._sprite.scale.x < 0;
			if (isMirrored != doMirror)
				this._sprite.scale.x *= -1;
		}
		
		setPosition(x,y) {
			this.view.position.set(x,y);
		}
		
		_switch(textures) {
			this._sprite.stop();
			const frame = this._sprite.currentFrame;
			this._sprite.textures = textures;
			this._sprite.gotoAndPlay(frame % this._sprite.totalFrames);
			this._toSwitch = null;
			this._dirty = true;
		}
		
		_rescale() {
			Lunar.Geometry.proportionalScale(this._sprite, this.view.width*this._scale, this.view.height*this._scale);
		}		
	};
})(window.Lunar, window);