/**
 * Smoothly transitions to the target transform values.
 */
(function(Lunar, window, undefined) {
	PIXI.Tween = class {
		
		constructor(delegate, tweenFactors) {
			this._tweenFactors = Object.assign({
				position: 0,
				rotation: 0,
				scale: 0,
				dimension: 0,
				alpha: 0,
				skew: 0
			}, tweenFactors);
			this._delegate = delegate;
			this._position = new PIXI.ObservablePoint(this._onChange, this);
			this._scale = new PIXI.ObservablePoint(this._onChange, this);
			this._skew = new PIXI.ObservablePoint(this._onChange, this);
			this._rotation = delegate.rotation;
			this._width = delegate.width;
			this._height = delegate.height;
			this._alpha = delegate.alpha;
			this._position.copy(delegate.position);
			this._skew.copy(delegate.skew);
			this._scale.copy(delegate.scale);
//			this.on('added', this._onAdded.bind(this));
//			this.on('removed', this._onRemoved.bind(this));
		}
		
//		_onAdded() {
//			this._delegate.parent = this.parent;
//		}
		
//		_onRemoved() {
//			this._delegate.parent = undefined;
//		}
		
		_onChange() {
			
		}
		
		get position() {
			return this._position;
		}
		
		get scale() {
			return this._scale;
		}
		
		get skew() {
			return this._skew;
		}
		
		get rotation() {
			return this._rotation;
		}
		
		get alpha() {
			return this._alpha;
		}
		
		get width() {
			return this._width;
		}
		
		get height() {
			return this._height;
		}
		
		set position(value) {
			this._position.copy(value);
		}
		
		set scale(value) {
			this._scale.copy(value);
		}
		
		set skew(value) {
			this._skew.copy(value);
		}
		
		set rotation(value) {
			this._rotation = value;
		}
		
		set alpha(value) {
			this._alpha = value;
		}
		
		set width(value) {
			this._width = value;
		}
		
		set height(value) {
			this._height = value;
		}
		
		get renderable() {
			return this._delegate.renderable;
		}
		
		set renderable(value) {
			this._delegate.renderable = value;
		}
				
		get visible() {
			return this._delegate.visible;
		}
		
		set visible(value) {
			this._delegate.visible = value;
		}
		
		get worldVisible() {
			return this._delegate.worldVisible;
		}
		
		set worldVisible(value) {
			this._delegate.worldVisible = value;
		}

		get transform() {
			return this._delegate.transform;
		}
		
		set transform(value) {
			this._delegate.transform = value;
		}
		
		get parent() {
			return this._delegate.parent;
		}
		
		set parent(value) {
			this._delegate.parent = value;
		}
		
		get anchor() {
			return this._delegate.anchor;
		}
		
		set anchor(value) {
			this._delegate.anchor = value;
		}
		
		get pivot() {
			return this._delegate.pivot;
		}
		
		set pivot(value) {
			this._delegate.pivot = value;
		}
		
		get worldAlpha() {
			return this._delegate.worldAlpha;
		}
		
		set worldAlpha(value) {
			this._delegate.worldAlpha = value;
		}
		
		on(event, fn, context) {
			return this._delegate.on(event, fn, context);
		}
		
		off(event, fn, context, once) {
			return this._delegate.on(event, fn, context, once);
		}
		
		once(event, fn, context) {
			return this._delegate.once(event, fn, context);
		}
		
		addListener(event, fn, context) {
			this._delegate.addListener(event, fn, context);
		}
		
		removeListener(event, fn, context, once) {
			this._delegate.removeListener(event, fn, context, once);
		}
		
		removeAllListeners(event) {
			this._delegate.removeAllListeners(event);
		}
		
		listeners(event, exists) {
			return this._delegate.listener(event, exists);
		}
		
		emit(event, ...args) {
			return this._delegate.emit(event, ...args);
		}
		
		eventNames() {
			return this._delegate.eventNames();
		}
		
		get delegate() {
			return this._delegate;
		}
		
		get tweenFactors() {
			return this._tweenFactors;
		}
		
		get x() {
			return this.position.x;
		}
		
		get y() {
			return this.position.y;
		}
		
		set x(value) {
			this.position.x = value;
		}
		
		set y(value) {
			this.position.y = value;
		}
		
		renderWebGL(renderer) {
			this._delegate.renderWebGL(renderer);
		}
		
		renderCanvas(renderer) {
			this._delegate.renderWebGL(renderer);
		}
		
		getLocalBounds(rect) {
			return this._delegate.getLocalBounds(rect);
		}
		
		getBounds(rect) {
			return this._delegate.getBounds(rect);
		}
		
		updateTransform() {
			this._update();
			this._delegate.updateTransform();
		}
		
		toGlobal(position, point, skipUpdate) {
			return this._delegate.toGlobal(position, point, skipUpdate);
		}
		
		_recursivePostUpdateTransform() {
			this._delegate_recursivePostUpdateTransform();
		}
		
		toLocal(position, from, point, skipUpdate) {
			return this._delegate.toLocal(position, from, point, skipUpdate);
		}
		
		destroy() {
			this._delegate.destroy();
			this._delegate = undefined;
		}
		
		get buttonMode() {
			return this._delegate.buttonMode;
		}
		
		set buttonMode(value) {
			this._delegate.buttonMode = value;
		}
		
		get interactive() {
			return this._delegate.interactive;
		}
		
		set interactive(value) {
			this._delegate.interactive = value;
		}
		
		get cacheAsBitmap() {
			return this._delegate.cacheAsBitmap;
		}
		
		set cacheAsBitmap(value) {
			this._delegate.cacheAsBitmap = value;
		}
		
		get cursor() {
			return this._delegate.cursor;
		}
		
		set cursor(value) {
			this._delegate.cursor = value;
		}
		
		get filterArea() {
			return this._delegate.filterArea;
		}
		
		set filterArea(value) {
			this._delegate.filterArea = value;
		}
		
		get filters() {
			return this._delegate.filters;
		}
		
		set filters(value) {
			this._delegate.filters = value;
		}
		
		get hitArea() {
			return this._delegate.hitArea;
		}
		
		set hitArea(value) {
			this._delegate.hitArea = value;
		}
		
		get mask() {
			return this._delegate.mask;
		}
		
		set mask(value) {
			this._delegate.mask = value;
		}
		
		get localTransform() {
			return this._delegate.localTransform;
		}
		
		set localTransform(value) {
			this._delegate.localTransform = value;
		}
		
		_update() {
			const f = this._tweenFactors;
			if (f.position != 0)
				this._delegate.position.set(
						this._tween(this._delegate.position.x, this.position.x, f.position),
						this._tween(this._delegate.position.y, this.position.y, f.position));
			if (f.skew != 0)
				this._delegate.skew.set(
						this._tween(this._delegate.skew.x, this.skew.x, f.skew),
						this._tween(this._delegate.skew.y, this.skew.y, f.skew));
			if (f.scale != 0)
				this._delegate.scale.set(
						this._tween(this._delegate.scale.x, this.scale.x, f.scale),
						this._tween(this._delegate.scale.y, this.scale.y, f.scale));
			if (f.rotation != 0)
				this._delegate.rotation = this._tween(this._delegate.rotation, this.rotation, f.rotation);
			if (f.dimension != 0) {
				this._delegate.width = this._tween(this._delegate.width, this.width, f.dimension);
				this._delegate.height = this._tween(this._delegate.height, this.height, f.dimension);
			}
			if (f.alpha != 0)
				this._delegate.alpha = this._tween(this._delegate.alpha, this.alpha, f.alpha);
		}
		
		_tween(from, to, factor) {
			const delta = to-from;
			return Math.abs(delta) < 1E-2 ? to : (from + factor*delta);
		}
	}
})(window.Lunar, window);