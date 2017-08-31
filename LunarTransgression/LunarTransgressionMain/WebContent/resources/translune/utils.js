/**
 * Adapted from https://lowrey.me/priority-queue-in-es6-javascript/
 * Returns entries starting at the entry with the lowest priority.
 * Undefined behavior when there are two entries with the same priority. 
 */
class PriorityQueue {  
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
}

class Loadable {
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

class ManualLoadable extends Loadable {
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

class DelegateLoadable extends Loadable {
	constructor(progressReporter) {
		super();
		this.progressReporter = progressReporter; 
	}
	
	getProgress() {
		const progress = this.progressReporter();
		if (progress === true) return 1;
		if (progress === false) return 0;
		return progress;
	}
}

class ChainedLoadable extends Loadable {
	constructor(...loadables) {
		super();
		this.loadables = loadables || [];
		this.loaded = 0;
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
}

class LoaderLoadable extends Loadable {
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
}

window.Lunar = {};

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
			return t < until ? 0 : 1.0/(1.0-until)*(t-until)
		};
	}
}

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
	userPlayer: 'USER_PLAYER'
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

/**
 * @return number A random integer in the range [0,maxExclusive)
 */
Lunar.Random = {
	int: function(maxExclusive) {
		return Math.floor(Math.random()*maxExclusive);
	}
}