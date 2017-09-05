/**
 * A container that does not scale its contents when its width
 * or height is set. Instead, it clips the content.
 */
(function(PIXI, window, undefined) {
	PIXI.ClipContainer = class extends PIXI.Container {
		constructor() {
			super();
			this._clip = false;
			this._cachedX = 0;
			this._cachedY = 0;
			this._cachedWidth = -1;
			this._cachedHeight = -1;
			this._scrollX = false;
			this._scrollY = false;
			this._scrollPositionX = 0;
			this._scrollPositionY = 0; 
		}
		
		updateTransform() {
			if (this._clip) {
				if (this.width !== this._cachedWidth || this.height !== this._cachedHeight
						|| this.x !=  this._cachedX || this.y !=  this._cachedY) {
					const mask = this.mask || new PIXI.Graphics();
					mask.clear();
					mask.beginFill(0x000000, 1);
					mask.drawRect(this.x, this.y, this.width, this.height);
					mask.endFill();
					this.mask = mask;
					this._cachedX = this.x;
					this._cachedY = this.y;
					this._cachedWidth = this.width;
					this._cachedHeight = this.height;
				}
			}
			super.updateTransform();
		}
		
		/** 
		 * @return {w: number, h: number, x: number, y: number}
		 */
		get dimensions() {
			return {
				x: this.x,
				y: this.y,
				w: this.width,
				h: this.height
			};
		}
		
		get scrollPositionX() {
			return this._scrollPositionX;
		}
		
		get scrollPositionY() {
			return this._scrollPositionY;
		}
		
		set scrollPositionX(value) {
			this._scrollPositionX = value;
			if (this._scrollX) {
				for (let child of this.children) {
					if (child.width > this.width)
						child.position.x = -value*(child.width - this.width);
					else
						child.position.x = 0;
				}
			}
		}
		
		set scrollPositionY(value) {
			this._scrollPositionY = value;
			if (this._scrollY) {
				for (let child of this.children) {
					if (child.height > this.height)
						child.position.y = -value*(child.height - this.height);
					else
						child.position.y = 0;
				}
			}
		}
		
		get scrollX() {
			return this._scrollX;
		}
		
		get scrollY() {
			return this._scrollY;
		}
		
		set scrollX(value) {
			value = !!value;
			if (value && !this._scrollX) {
				// Enable
				this.interactive = true;
				if (!this.scrollX)
					this.scrollListenOn();
			}
			else if (!value && this._scrollX) {
				// Disable
				this.interactive = this.scrollX;
				if (!this.scrollX)
					this.scrollListenOff();
				this.scrollPositionX = 0;
			}
			this._scrollX = value;
		}
		
		set scrollY(value) {
			value = !!value;
			if (value && !this._scrollY) {
				// Enable
				this.interactive = true;
				if (!this.scrollY)
					this.scrollListenOn();
			}
			else if (!value && this._scrollY) {
				// Disable
				this.interactive = this.scrollY;
				if (!this.scrollY)
					this.scrollListenOff();
				this.scrollPositionY = 0;
			}
			this._scrollY = value;
		}
		
		get width() {
			return this._width;
		}
		
		get height() {
			return this._height;
		}
		
		set width(value) {
			this._width = value;
		}
		
		set height(value) {
			this._height = value;
		}
		
		set clip(value) {
			value = !!value;
			if (value && !this._clip) {
				this._cachedWidth = -1;
			}
			else if (!value && this._clip) {
				if (this.mask)
					this.mask.destroy();
				this.mask = undefined;
			}
			this._clip = value;
		}
		
		get clip() {
			return this._clip;
		}

		scrollListenOn() {
//			this.on('pointermove', this._onPointerMove, this);
		}
		
		scrollListenOff() {
//			this.off('pointermove', this._onPointerMove, this);
		}
	}
})(window.PIXI, window);