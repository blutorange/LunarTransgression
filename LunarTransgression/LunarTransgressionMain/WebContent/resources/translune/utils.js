(function(Lunar, window, undefined) {
	/**
	 * Adapted from https://lowrey.me/priority-queue-in-es6-javascript/
	 * Returns entries starting at the entry with the lowest priority.
	 * Undefined behavior when there are two entries with the same priority. 
	 */
	Lunar.PriorityQueue = class {  
		constructor() {
			this.data = [];
			this.dirty = false;
		}

		push(value, priority) {
			this.dirty = true;
			this.data.push({
				value: value,
				priority: priority
			});
			return value;
		}
		
		peek() {
			this.sort();
			const entry = this.data[this.data.length-1];
			return entry ? entry.value : undefined;
		}
		
		sort() {
			if (this.dirty)
				this.data.sort((item1,item2) => item2.priority - item1.priority);
			this.dirty = false;
		}
		
		clear() {
			this.data = {};
		}

		pop() {
			this.sort();
			const entry = this.data.pop();
			return entry ? entry.value : undefined;
		}

		size() {
			return this.data.length;
		}
	};
	
	Lunar.Loadable = class {
		constructor() {
			this.start = new Date().getTime();
			this.completionListeners = [];
		}

		getProgress() {
			return (new Date().getTime()-this.start)/10000.0
		}
		
		addCompletionListener(listener) {
			this.completionListeners.push(listener);
		}
		
		notifyCompletionListeners() {
			for (let listener of this.completionListeners)
				listener();
		}
	}

	Lunar.DelegateLoadable = class extends Lunar.Loadable {
		constructor(loader = undefined) {
			super();
			this._done = false;
		}
		
		getProgress() {
			return this._loadable ? this._loadable.getProgress() : 0;
		}
		
		notifyCompletionListeners() {
			super.notifyCompletionListeners();
			this._loadable && this._loadable.notifyCompletionListeners();
		}
		
		set loadable(loadable) {
			this._loadable = loadable;
		}
	};

	Lunar.ManualLoadable = class extends Lunar.Loadable {
		constructor() {
			super();
			this._progress = 0;
		}
		
		get progress() {
			return this._progress;
		}
		
		set progress(progress) {
			this._progress = progress;
		}
		
		getProgress() {
			return this.progress;
		}
	}

	Lunar.RequestLoadable = class extends Lunar.Loadable {
		constructor(game, type, data) {
			super();
			const _this = this;
			this._requestState = 0;
			this._promise = new Promise((resolve, reject) => {
				game.net.dispatchMessage(type, data).then(response => {
					_this._requestState = 1;
					resolve(response);
				})
				.catch(response => {
					_this._requestState = 2;
					reject(response);
				});
			});
		}
		
		get promise() {
			return this._promise;
		}
		
		getProgress() {
			return this._requestState === 1 ? 1 : 0;
		}
	};

	Lunar.ChainedLoadable = class extends Lunar.Loadable {
		constructor(...loadables) {
			super();
			this.loadables = loadables || [];
			this.done = false;
			const _this = this;
		}
		getProgress() {
			let progress = 0;
			for (let loadable of this.loadables) {
				const p = loadable.getProgress();
				progress += p;
			}
			if (progress === this.loadables.length && !this.done) {
				for (let loadable of this.loadables)
					loadable.notifyCompletionListeners();
				this.done = true;
			}
			return this.loadables.length === 0 || this.done ? 1 : progress / this.loadables.length;
		}
	};

	Lunar.LoaderLoadable = class extends Lunar.Loadable {
		/**
		 * @param {PIXI.loaders.Loader} loader
		 */
		constructor(loader) {
			super();
			this.loaded = 0;
			this.loader = loader;
			this.done = false;
			const _this = this;
			loader.onProgress.add(this.onLoaderProgress.bind(this));
			loader.onComplete.add(this.onLoaderComplete.bind(this));
		}
		getProgress() {
			const len = Object.keys(this.loader.resources).length;
			return this.done  || len === 0 ? 1 : this.loaded / len;
		}
		onLoaderProgress() {
			++this.loaded;
		}
		onLoaderComplete() {
			this.loaded = Object.keys(this.loader.resources).length;
			this.done = true;
		}
	};
	
	
	/**
	 * Constants for interpolating data.
	 * Takes a value between 0 and 1.
	 * @const {IObject<string, function(number)>}
	 */
	Lunar.Interpolation = {
		backAndForth: function(t) {
			return t < 0.5 ? 2*t : 2-2*t;
		},
		slowInSlowOut: function(t) {
			return 16*t*t*(t-1)*(t-1);
		},
		zeroUntil: function(until) {
			return function(t) {
				return t < until ? 0 : 1.0/(1.0-until)*(t-until);
			};
		}
	};
	
	/**
	 * @const {IObject<string, number>}
	 */
	Lunar.Status = {
		OK: 0,
		GENERIC_ERROR: 20,
		ACCESS_DENIED: 21,
		TIMEOUT: 22,
	};
	
	/**
	 * @const {IObject<string, string>}
	 */
	Lunar.Message = {
		authorize: 'AUTHORIZE',
		fetchData: 'FETCH_DATA',
		unknown: 'UNKNOWN'
	};
	
	Lunar.FetchType = {
		none: 'NONE',
		availableBgAndBgm: 'AVAILABLE_BG_AND_BGM',
		userPlayer: 'USER_PLAYER'
	};
	
	Lunar.Object = {
		/**
		 * @param {object}
		 * @return {key: object, value: object}
		 */
		randomEntry: (object) => {
			const keys = Object.keys(object);
			const key = keys[Lunar.Random.int(keys.length)];
			return {key: key, value: object[key]};
		}
	};
	
	/**
	 * @const {IObject<string, ?>}
	 */
	Lunar.Constants = {
		reponseTimeout: 10, // seconds
		queueTimeout: 5, // seconds
		queueInterval: 0.25, // seconds,
		pi2: 2*Math.PI
	};
	
	Lunar.FontStyle = {
		setup: function(game) {
			Lunar.FontStyle.dialog= new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game.dx(0.040),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#00ff99'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game.dx(0.8)
			});
			Lunar.FontStyle.load = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game.dx(0.028),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#00ff99'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game.w
			});
			Lunar.FontStyle.button = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game.dx(0.032),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#00ff99'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game.w
			});
			Lunar.FontStyle.buttonActive = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game.dx(0.036),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#00ff99', '#ffffff'], // gradient
			    stroke: '#900020',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game.w
			});
		},
	};
	
	Lunar.File = {
		removeExtension: filename => {
			const index = filename.lastIndexOf('.');
			if (index < 0)
				return filename;
			return filename.substr(0, index);
		}
	};
	
	Lunar.Geometry = {
		layoutGrid: (options, consumer) => {
			const width = options.boxWidth * (1.0 - (options.paddingLeft+options.paddingRight+options.paddingX*(options.tileX-1))) / options.tileX;
			const height = options.boxHeight * (1.0 - (options.paddingTop+options.paddingBottom+options.paddingY*(options.tileY-1))) / options.tileY;
			for (let j = 0; j < options.tileY; ++j) {
				const y = options.boxHeight*(options.paddingTop + options.paddingY*j) + (j+0.5)*height;
				for (let i = 0; i < options.tileX; ++i) {
					const x = options.boxWidth*(options.paddingLeft + options.paddingX*i) + (i+0.5)*width;
					consumer(x,y, i, j, j*options.tileX+i, width, height);
				}
			}			
		},
		proportionalScale: (scalable, targetWidth, targetHeight) => {
			let w = scalable.width;
			let h = scalable.height;
			let hTest = h*targetWidth/w;
			if (hTest <= targetHeight) {
				scalable.width = targetWidth;
				scalable.height = hTest;
			}
			else {
				scalable.width = w*targetHeight/h
				scalable.height = targetHeight;
			}
		}
	};
	
	/**
	 * @return number A random integer in the range [0,maxExclusive)
	 */
	Lunar.Random = {
		int: function(maxExclusive) {
			return Math.floor(Math.random()*maxExclusive);
		}
	};
})(window.Lunar = {}, window);