/**
 * A container that does not scale its contents when its width
 * or height is set. Instead, it clips the content.
 */
(function(PIXI, window, undefined) {
	PIXI.ClipContainer = class extends PIXI.Container {
		constructor() {
			super();
			this._clip = false;
			this._cachedWidth = -1;
			this._cachedHeight = -1;
		}
		
		updateTransform() {
			const w = this.width;
			const h = this.height;
			if (w !== this._cachedWidth || h !== this._cachedHeight) {
				if (this._clip) {
					const mask = this.mask || new PIXI.Graphics();
					mask.clear();
					mask.beginFill(0x000000, 1);
					mask.drawRect(0, 0, this.width, this.height);
					mask.endFill();
					this.mask = mask;
				}
				else {
					if (this.mask)
						this.mask.destroy();
					this.mask = undefined;
				}
				this._cachedWidth = w;
				this._cachedHeight = h;
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
			this._clip = !!value;
		}
		
		get clip() {
			return this._clip;
		}
	}
})(window.PIXI, window);