/**
 * 
 */
(function(PIXI, window, undefined) {
	PIXI.TextInput = class extends PIXI.DisplayObject {
		
		/**
		 * @param {PIXI.TextStyle} style
		 * @param {DOMElement} stage DOMElement to which the input is prepended. Default to the parent of the first canvas element. 
		 */
		constructor(style, enabledEvents = ['focus', 'blur', 'change'], stage = undefined) {
			super();
			const _this = this;
			this._input = window.document.createElement('input');
			this._anchor = new PIXI.ObservablePoint(this._makeDirty, this);
			this._chain(this.position, this._makeDirty, this);
			this._chain(this.scale, this._makeDirty, this);
			this._input.style.position = 'absolute';
			this._input.style.background = 'transparent';
			this._input.style.zIndex = 999;
			this._input.style.outlineWidth = '0px';
			this._input.style.border = '0px';
			this._input.style.padding = '0px';
			this._input.style.margin = '0px';
			this._style = style || new PIXI.TextStyle();
			this._dirty = true;
			this._width = 64;
			this._height = 26;
			this._stage = stage || window.document.getElementsByTagName('canvas')[0].parentElement;
			this.on('added', this._onAdded, this);
			this.on('removed', this._onRemoved, this);
			enabledEvents.forEach(event => _this._input.addEventListener(event, e => _this.emit(event, _this, e)));
		}
		
		calculateBounds() {
		}
		
		_attr(name, value) {
			this._input.setAttribute(name, value);
		}
		
		set max(value) {
			this._attr('max', parseInt(value));
		}
		
		set min(value) {
			this._attr('min', parseInt(value));
		}
		
		set minlength(value) {
			this._attr('minlength', parseInt(value));
		}
		
		set maxlength(value) {
			this._attr('maxlength', parseInt(value));
		}
		
		set type(value) {
			this._attr('type', value);
		}
		
		set css(css) {
			for (let key of Object.keys(css)) {
				this._input.style[key] = css[key];
			}
		}
		
		get css() {
			return this._input.style;
		}
		
		_chain(observable, newCallback, newScope) {
			const cb = observable.cb;
			const scope = observable.scope;
			observable.scope = newScope;
			observable.cb = () => {
				cb.call(scope);
				newCallback.call(this);
			};
		}
		
		get text() {
			return this._input.value;
		}
		
		set text(value) {
			this._input.value = value;
		}
		
		get children() {
			return [];
		}
		
		updateTransform() {
			super.updateTransform();
			if (this._dirty)
				this._update();			
		}
		
		_onAdded() {
			this._stage.prepend(this._input);
		}
		
		_onRemoved() {
			this._input.remove();
		}
		
		destroy() {
			this._input.remove();
			super.destroy();
		}
		
		_makeDirty() {
			this._dirty = true;
		}
		
		set rotation(rotation) {
			super.rotation = rotation;
			this._dirty = true;
		}
		
		get rotation() {
			return super.rotation;
		}
		
	    set style(style) {
	        style = style || {};
	        if (style instanceof PIXI.TextStyle) {
	            this._style = style;
	        }
	        else {
	            this._style = new PIXI.TextStyle(style);
	        }
	        this._dirty = true;
	    }
	    
	    set width(value) {
	        this._width = value;
	        this._dirty = true;
	    }
	    
	    get width() {
	    	return this._width;
	    }
	    
	    get placeholder() {
	    	return this._input.placeholder;
	    }
	    
	    set placeholder(value) {
			this._input.placeholder = value;
	    }
	    
	    set height(value) {
	        this._height = value;
	        this._dirty = true;
	    }
	    
	    get height() {
	    	return this._height;
	    }
	    
	    get style() {
	        return this._style;
	    }
		
	    get anchor() {
	        return this._anchor;
	    }
	    
	    set anchor(value) {
	        this._anchor.copy(value);
	        this._dirty = true;
	    }
		
		_update() {
			const s = this.style;
//			const x = this.position.x - this.anchor.x * this.width;
//			const y = this.position.y - this.anchor.y * this.height;
			const m = this.transform.worldTransform;
			let color1, color2;
			if (Array.isArray(s.fill)) {
				color1 = s.fill[0];
				color2 = s.fill[1];
			}
			else {
				color1 = s.fill;
				color2 = s.fill;
			}
			//this._input.style.transform = `translate(${x}px, ${y}px) rotate(${this.rotation||0}rad) scale(${this.scale.x}, ${this.scale.y})`;
			const transform = `matrix(${m.a},${m.b},${m.c},${m.d},${m.tx - this.anchor.x * this.width},${m.ty - this.anchor.y * this.height})`;
			const transformOrigin = `${this.anchor.x*100}% ${this.anchor.y*100}%`;
			this._input.style.webkitTransform = transform;
			this._input.style.msTransform = transform;
			this._input.style.transform = transform;
			this._input.style.webkitTransformOrigin = transformOrigin;
			this._input.style.msTransformOrigin = transformOrigin;
			this._input.style.transformOrigin = transformOrigin;
			this._input.style.width = `${this.width}px`;
			this._input.style.height = `${this.height}px`;
			this._input.style.fontSize = `${s.fontSize}px`;
			this._input.style.fontFamily = s.fontFamily;
			this._input.style.textAlign = s.align;
			this._input.style.fontVariant = s.fontVariant;
			this._input.style.fontWeight = s.fontWeight;
			this._input.style.color = color1;
			this._input.style.lineHeight = `${s.lineHeight}px`;
			this._input.style.webkitTextStrokeWidth = `${s.strokeThickness}px`;
			if (s.strokeThickness > 0)
				this._input.style.webkitTextStrokeColor = s.stroke;
			else
				this._input.style.webkitTextStrokeColor = '';
			if (s.dropShadow) {
				const shadowX = s.dropShadowDistance*Math.cos(s.dropShadowAngle);
				const shadowY = s.dropShadowDistance*Math.sin(s.dropShadowAngle);
				const shadowColor = s.dropShadowColor;
				this._input.style.textShadow = `${shadowX}px ${shadowY}px ${s.dropShadowBlur}px ${shadowColor}`;
			}
			else
				this._input.style.textShadow = '';
			this._dirty = false;
		}
		
	}
})(window.PIXI, window);