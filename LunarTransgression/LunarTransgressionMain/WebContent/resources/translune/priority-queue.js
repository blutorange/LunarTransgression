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