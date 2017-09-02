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
	
	Lunar.String = {
		rjust: (string, paddingCharacter, targetLength) => {
			const s = String(string);
			if (targetLength < s.length)
				return s;
			return paddingCharacter.repeat(targetLength-s.length).concat(s);
		},
		ljust: (string, paddingCharacter, targetLength) => {
			if (targetLength < string.length)
				return string;
			return string.concat(paddingCharacter.repeat(targetLength-String(string).length));
		}
	};
	
	/**
	 * @const {IObject<string, ?>}
	 */
	Lunar.Constants = {
		minReleaseLevel: 80,
		reponseTimeout: 10, // seconds
		queueTimeout: 5, // seconds
		queueInterval: 0.25, // seconds,
		pi2: 2*Math.PI
	};
	
	Lunar.FontStyle = {
		layout: function(game) {
			Lunar.FontStyle.dialog.fontSize = game.dx(0.040);
			Lunar.FontStyle.dialog.wordWrapWidth = game.dx(0.8);
			Lunar.FontStyle.load.fontSize = game.dx(0.028);
			Lunar.FontStyle.load.wordWrapWidth = game.w;
			Lunar.FontStyle.button.fontSize = game.dx(0.032);
			Lunar.FontStyle.button.wordWrapWidth = game.w;
			Lunar.FontStyle.buttonActive.fontSize = game.dx(0.036);
			Lunar.FontStyle.buttonActive.wordWrapWidth = game.w;
		},
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
		/**
		 * @return {array<{x:number,y:number,w:number,h:number}>}
		 */
		layoutVbox: ({relative=false, dimension=[1], box: {x:bx=0,y:by=0,w=1,h=1}={}, padding: {y:py=0,left=0,right=0,top=0,bottom=0} = {}} = {}) => {
			const layout = [];
			const padLeft = left < 1 ? left*w : left;
			const padRight = right < 1 ? right*w : right;
			const padTop = top < 1 ? top*h : top;
			const padBottom = bottom < 1 ? bottom*h : bottom;
			const padY = py < 1 ? py * h : py;			
			const dim = Array.isArray(dimension) ? dimension : Array(parseInt(dimension)).fill(1);
			const width = w - padLeft - padRight;
			const availHeight = h - padY*(dim.length-1) - padTop - padBottom;
			const total = dim.reduce((sum,r) => sum + r);
			const x = (relative ? 0 : bx) + padLeft
			let y = (relative ? 0 : by) + padTop;
			return dim.map(r => {
				const height = availHeight*r/total;
				const curY = y;
				y = y + height + padY;
				return {
					x: x,
					y: curY,
					w: width,
					h: height
				};
			});
		},
		
		/**
		 * @return {array<{x:number,y:number,w:number,h:number}>}
		 */
		layoutHbox: ({relative=false, dimension=[1], box: {x:bx=0,y:by=0,w=1,h=1}={}, padding: {x:px=0,left=0,right=0,top=0,bottom=0} = {}} = {}) => {
			const layout = [];
			const padLeft = left < 1 ? left*w : left;
			const padRight = right < 1 ? right*w : right;
			const padTop = top < 1 ? top*h : top;
			const padBottom = bottom < 1 ? bottom*h : bottom;
			const padX = px < 1 ? px * w : px;
			const dim = Array.isArray(dimension) ? dimension : Array(parseInt(dimension)).fill(1);
			const height = h - padTop - padBottom;
			const availWidth = w - padX*(dim.length-1) - padLeft - padRight;
			const total = dim.reduce((sum,r) => sum + r);
			const y = (relative ? 0 : by) + padTop
			let x = (relative ? 0 : bx) + padLeft;
			return dim.map(r => {
				const width = availWidth*r/total;
				const curX = x;
				x = x + width + padX;
				return {
					x: curX,
					y: y,
					w: width,
					h: height
				};
			});
		},
		
		/**
		 * @param {dimension:{n:number, m:number, merge: {i:number,j:number,columns:number,rows:number}}, box: {x:number,y:number,w:number,h:number}, padding: {x: number, y: number, top: number, bottom:number, left: number, right: number}} options
		 * @return
		 */
		layoutGrid: ({relative = false, dimension: {n=1,m=1,merge}={}, box: {x:bx=0,y:by=0,w=1,h=1}={}, padding: {x:px=0,y:py=0,top=0,left=0,bottom=0,right=0}={}}={}) => {
			const padLeft = left < 1 ? left*w : left;
			const padRight = right < 1 ? right*w : right;
			const padTop = top < 1 ? top*h : top;
			const padBottom = bottom < 1 ? bottom*h : bottom;
			const padX = px < 1 ? px * w : px;
			const padY = py < 1 ? py * h : py;
			const dimX = Array.isArray(n) ? n : Array(parseInt(n)).fill(1);
			const dimY = Array.isArray(m) ? m : Array(parseInt(m)).fill(1);
			const availWidth = w - padX*(dimX.length-1) - padLeft - padRight;
			const availHeight = h - padY*(dimY.length-1) - padTop - padBottom;
			const totalX = dimX.reduce((sum,r) => sum + r);
			const totalY = dimY.reduce((sum,r) => sum + r);
			
			let j = -1;
			let y = (relative ? 0 : by) + padTop;
			const layout = dimY.map(r => {
				const height = availHeight*r/totalY;
				const curY = y;
				y = y + height + padY;				
				let x = (relative ? 0 : bx) + padLeft;
				let i = -1;
				++j;
				return dimX.map(r => {
					const width = availWidth*r/totalX;
					const curX = x;
					x = x + width + padX;
					++i;
					return {
						x: curX,
						y: curY,
						i: i,
						j: j,
						w: width,
						h: height
					};
				});
			});			
			if (merge) {
				merge.forEach(({i,j,columns:dx=0,rows:dy=0}) => {
					const l = layout[i][j];
					let dw = 0;
					let dh = 0;
					for (let di = 1; di <= dx; ++di)
						dw += layout[j][i+di].w;
					for (let dj = 1; dj <= dy; ++dj)
						dh += layout[j+dj][i].h;
					l.w = l.w + dw;
					l.h = l.h + dh;
				});
			}
			return layout;
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