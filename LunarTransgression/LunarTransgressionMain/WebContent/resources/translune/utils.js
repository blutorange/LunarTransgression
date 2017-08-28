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