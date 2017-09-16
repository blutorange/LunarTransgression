/**
 * 
 */
(function(Lunar, window, undefined) {
	
	Lunar.Scene.BattleBattler = class extends Lunar.Scene.Base {
		constructor(battle, index, back, front, battleCircle, characterState, computedStatus) {
			super(battle.game);
			this._targetHpRatio = 1;
			this._battleScale = 1;
			this._drawnHpRatio = 0;
			this._isHpRatio = 1;
			this._battle = battle;
			this._back = Object.values(back.spritesheet.textures);
			this._front = Object.values(front.spritesheet.textures);
			this._mode = 'back';
			this._characterState = characterState;
			this._computedStatus = computedStatus;
			this._sprite = new PIXI.extras.AnimatedSprite(this._back);
			this._sprite.anchor.set(0.5,0.5);
			this._barWidth = 5;
			this._barHeight = 1 ;
			this._index = index,
			this._targetWidth = this._sprite.width;
			this._targetHeight = this._sprite.height;
			this.view.width = this._sprite.width;
			this.view.height = this._sprite.height;
			this._battleCircle = battleCircle;
			this._updateLook = this._lookInward;
			this._scale = 1;
			this._angle = 0;
		}
		
		updateComputedStatus(computedStatus) {
			this._computedStatus = computedStatus;
			this.hpRatio = computedStatus.battleStatus.hp / Lunar.Constants.hpRatioDenominator;
		}
		
		moveHpRatio(targetHpRatio) {
			this._targetHpRatio = Math.clamp(targetHpRatio, 0, 1);
		}

		get isPlayer() {
			return this._index <= 3;
		}
		
		get normalizedIndex() {
			return this._index % 4;
		}
		
		set hpRatio(targetHpRatio) {
			this._targetHpRatio = Math.clamp(targetHpRatio, 0, 1);
			this._isHpRatio = this._targetHpRatio;
			this._dirty = true;
		}
		
		endHpRatioAnimation() {
			this.hpRatio = this._targetHpRatio;
		}
		
		get dead() {
			return this._targetHpRatio === 0;
		}
		
		get characterState() {
			return this._characterState;
		}
		
		ringPosition(x, y, angle, centerY, radiusY) {
			this._angle = angle;
			this.setPosition(x,y);
			this._battleScale = 1+this._battleCircle.scale*(y-centerY)/radiusY;
//			this.setScaling(1+this._battleCircle.scale*(y-centerY)/radiusY);
			this.view.zOrder = -y;
			this._updateLook(angle);
			if (angle < Lunar.Constants.deg180AsRad)
				this.setMirroring(angle > Lunar.Constants.deg90AsRad);
			else
				this.setMirroring(angle > Lunar.Constants.deg270AsRad);
			this._dirty = true;
		}
				
		_lookInward(angle) {
			if (angle < Lunar.Constants.deg180AsRad)
				this.asFront();
			else
				this.asBack();
		}
		
		_lookOutward(angle) {
			if (angle < Lunar.Constants.deg180AsRad)
				this.asBack();
			else
				this.asFront();
		}

		_lookDown(angle) {
			this.asFront();
		}

		_lookUp(angle) {
			this.asBack();
		}

		set look(value) {
			switch (value) {
			case Lunar.Scene.BattleBattler.Look.DOWN:
				this._updateLook = this._lookDown;
				break;
			case Lunar.Scene.BattleBattler.Look.UP:
				this._updateLook = this._lookUp;
				break;
			case Lunar.Scene.BattleBattler.Look.INWARD:
				this._updateLook = this._lookInward;
				break;
			case Lunar.Scene.BattleBattler.Look.OUTWARD:
				this._updateLook = this._lookDownward;
				break;
			default:
				console.error("no such look mode", value);
			}
			this._look = value;
			this._updateLook(this._angle);
		}
		
		getPosition() {
			return this.view.position;
		}
		
		asBack() {
			if (this._mode === 'back')
				return;
			this._toSwitch = this._back;
			this._mode = 'back';
		}
		
		update(delta) {
			if (this._isHpRatio !== this._targetHpRatio) {
				this._isHpRatio = Lunar.Interpolation.linearSpeed(this._isHpRatio, this._targetHpRatio, delta*0.5);
				this._dirty = true;
			}
			if (this._toSwitch)
				this._switch(this._toSwitch);
			if (this._dirty) {
				this.layout();
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
			this._layoutHpBar();
		}
		
		onAdd() {
			this.view.addChild(this._sprite);
			this._initHpBar();
			this._sprite.play();
		}
		
		destroy() {
			this._battleCircle = undefined;
			this._back = undefined;
			this._sprite = undefined;
			super.destroy();
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
			Lunar.Geometry.proportionalScale(
					this._sprite, this._targetWidth*this._scale*this._battleScale,
					this._targetHeight*this._scale*this._battleScale,
					this._targetWidth,
					this._targetHeight);
			this.hierarchy.$hpBarFill.width = this.game.dx(0.03);
			this.hierarchy.$hpBarFill.height = this._sprite.height;
			this.hierarchy.$hpBarBase.width = this.game.dx(0.03);
			this.hierarchy.$hpBarBase.height = this._sprite.height;
//			Lunar.Geometry.proportionalScale(this.hierarchy.$hpBarFill, this._targetWidth*this._scale, this._targetHeight*this._scale, this._barWidth, this._barHeight);
//			Lunar.Geometry.proportionalScale(this.hierarchy.$hpBarBase, this._targetWidth*this._scale, this._targetHeight*this._scale, this._barWidth, this._barHeight);
		}
		
		_initHpBar() {
			const hpBarBase = new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['barbase.png']);
			const hpBarFill = new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['barfill.png']);
			const hpBarMask = new PIXI.Graphics();
			
			this._barWidth = hpBarBase.width;
			this._barHeight = hpBarBase.height;
			
			hpBarBase.anchor.set(0.5,0.5);
			hpBarFill.anchor.set(0.5,0.5);
			hpBarFill.mask = hpBarMask;
			hpBarFill.alpha = 0.75;
			
			this.view.addChild(hpBarBase);
			this.view.addChild(hpBarFill);
			hpBarFill.addChild(hpBarMask);
			
			this.hierarchy = {
				$hpBarBase: hpBarBase,
				$hpBarFill: hpBarFill,
				$hpBarMask: hpBarMask,
				$sprite: this._sprite
			}
		}
		
		set alpha(value) {
			this.view.alpha = value;
		}
		
		_layoutHpBar() {
			const h = this.hierarchy;
			const pos = this._sprite.position;
			const dx = this._sprite.width*0.4;
			
			h.$hpBarBase.position.set(pos.x-dx,pos.y);
			h.$hpBarFill.position.set(pos.x-dx,pos.y);

			this._updateHpBar();
		}
		
		_updateHpBar() {
			const h = this.hierarchy;
			if (this._drawnHpRatio !== this._isHpRatio) {
				this._updateBar(h.$hpBarFill, h.$hpBarMask, this._isHpRatio);
				this._drawnHpRatio = this._isHpRatio;
			}
		}
		
		_updateBar(bar, mask, ratio) {
			const y = 0 - 0.5 * bar.height / bar.scale.y;
			const h = bar.height/bar.scale.y;
			const less = (1-ratio)*h;
			mask.clear();
		    mask.beginFill(0x000000, 1);
			mask.drawRect(
					0 - 0.5 * bar.width,
					y + less,
					bar.width,
					h - less);
			mask.endFill();
		}
	};
	
	Lunar.Scene.BattleBattler.Look = {
			DOWN: Symbol("Look-Down"),
			UP: Symbol("Look-Up"),
			INWARD: Symbol("Look-Inward"),
			OUTWARD: Symbol("Look-Outward"),
	}
})(window.Lunar, window);