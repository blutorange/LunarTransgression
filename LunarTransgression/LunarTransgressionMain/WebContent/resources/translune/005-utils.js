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
		
		removeAllCompletionListeners() {
			this.completionListeners = [];
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
					resolve(response);
					_this._requestState = 1;
				})
				.catch(response => {
					reject(response);
					_this._requestState = 2;
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
		linearSpeed: (from, to, speed) => {
			const sign = Math.sign(to-from);
			from += sign*speed;
			if (sign !== Math.sign(to-from))
				from = to;
			return from;
		},
		exponentialSpeed: (from, to, speed, threshold = 1E-2) => {
			from += speed*(to-from);
			if (Math.abs(to-from) < threshold)
				from = to;
			return from;
		},
		backAndForth: t => {
			return t < 0.5 ? 2*t : 2-2*t;
		},
		slowInSlowOut: t => {
			return 16*t*t*(t-1)*(t-1);
		},
		slowInSlowOutOnce: t => {
			return t*t*(t-2)*(t-2);
		},
		slowInFastOutOnce: t => {
			return t*t;
		},
		fastInSlowOutOnce: t => {
			return -t * (t-2);
		},
		zeroUntil: until => {
			return t => {
				return t < until ? 0 : 1.0/(1.0-until)*(t-until);
			};
		},
		quadraticBezierFromPoints(x1,y1,x2,y2,x3,y3) {
			return t => {
				const a = (1-t)*(1-t);
				const b = 2*t*(1-t);
				const c = t*t;
				return {
					x: x1*a+x2*b+x3*c,
					y: y1*a+y2*b+y3*c,
				};
			};
		},
		// not symmetric!!
		parabolaFromPoints(x1,y1,x2,y2,x3,y3) {
			// sort
			let tmp1,tmp2;
			if (x2 < x1) {
				tmp1 = x1;
				tmp2 = y1;
				x1 = x2;
				y1 = y2;
				x2 = tmp1;
				y2 = tmp2;
			}
			if (x3 < x1) {
				tmp1 = x1;
				tmp2 = y3;
				x1 = x3;
				y1 = y3;
				x3 = tmp1;
				y3 = tmp2;
			}
			else if (x3 < x2) {
				tmp1 = x2;
				tmp2 = y2;
				x2 = x3;
				y2 = y3;
				x3 = tmp1;
				y3 = tmp2;
			}
			const da = ((x1*x1-x2*x2)*(x2-x3)-(x2*x2-x3*x3)*(x1-x2));
			const db = (x1-x2);
			if (Math.abs(da) < 1E-5 || Math.abs(db) < 1E-5) {
				// Points lie on a vertical line
				const d2 = y2-y1;
				const d3 = y3-y1;
				const dist = (Math.abs(d2) > Math.abs(d3)) ? d2 : d3;					
				const x = (x1+x2+x3)/3;
				return t => ({
					x: x,
					y: y1 + t*dist
				});
			}
			const a = ((y1-y2)*(x2-x3)-(y2-y3)*(x1-x2)) / da;
			const b = (y1-y2-a*(x1*x1-x2*x2)) / db;
			const c = y1-a*x1*x1-b*x1;
			return t => {
				const x = x1+(x3-x1)*t;
				return {
					x: x,
					y: (a*x+b)*x+c	
				};
			};
		}
	};
	
	/**
	 * @const {IObject<string, number>}
	 */
	Lunar.Status = {
		OK: 0,
		WARN: 1,
		GENERIC_ERROR: 20,
		ACCESS_DENIED: 21,
		TIMEOUT: 22,
	};
	
	/**
	 * @const {IObject<string, string>}
	 */
	Lunar.Message = {
		authorize: 'authorize',
		fetchData: 'fetch-data',
		updateData: 'update-data',
		
		invite: 'invite',
		inviteRetract: 'invite-retract',
		inviteReject: 'invite-reject',
		inviteAccept: 'invite-accept',
		
		invited: 'invited',
		inviteAccepted: 'invite-accepted',
		inviteRetracted: 'invite-retracted',
		inviteRejected: 'invite-rejected',
		
		cancelBattlePreparation: 'cancel-battle-preparation',
		prepareBattle: 'prepare-battle',
		stepBattle: 'step-battle',
		loot: 'loot',
		
		battlePreparationCancelled: 'battle-preparation-cancelled',
		battleCancelled: 'battle-cancelled',
		battlePrepared: 'battle-prepared',
		battleStepped: 'battle-stepped',
		battleEnded: 'battle-ended',
		
		unknown: 'unknown'
	};
	
	Lunar.FetchType = {
		none: 'none',
		openInvitations: 'open-invitations',
		availableBgAndBgm: 'available-bg-and-bgm',
		userPlayer: 'user-player',
		activePlayer: 'active-player',
		playerDetail: 'player-detail',
		lootableStuff: 'lootable-stuff'
	};
	
	Lunar.UpdateType = {
		none: 'none',
		playerDescription: 'player-description',
		characterNickname: 'character-nickname',		
	};
	
	Lunar.CommandType = {
		basicAttack: 'basic-attack',
		skill: 'skill',
		item: 'item',
		special: 'special',
		defend: 'defend'
	};
	
	Lunar.StatusDeltaType = {
		computed: 'COMPUTED',
		computedBattle: 'COMPUTED_BATTLE',
		battleStatus: 'BATTLE_STATUS',
	};
	
	Lunar.StatusValue = {
			hp: 'HP',
			mp: 'MP',
			evasion: 'EVASION',
			accuracy: 'ACCURACY',
			speed: 'SPEED',
			physicalAttack: 'PHYSICAL_ATTACK',
			physicalDefense: 'PHYSICAL_DEFENSE',
			magicalAttack: 'MAGICAL_ATTACK',
			magicalDefense: 'MAGICAL_DEFENSE',
		};
	
	Lunar.Array = {
		/**
		 * @return {boolean} True iff the array contained the element.  
		 */
		removeElement: (array, element) => {
			const index = array.indexOf(element);
			if (index >= 0)
				array.splice(index, 1);
			return index >= 0;
		},
		containsElement: (array, element) => array.indexOf(element) >= 0,
		last: array => array[array.length-1],
		mapLast: (array,mapper) => array[array.length-1] = mapper(array[array.length-1]) 
	};
	
	Lunar.Object = {
		/**
		 * @param {object}
		 * @return {key: object, value: object}
		 */
		randomEntry: object => {
			const keys = Object.keys(object);
			const key = keys[Lunar.Random.int(keys.length)];
			return {key: key, value: object[key]};
		},
		isEmpty: object => {
			for (let $ in object) return false;
			return true;
		},
		length: object => {
			return Object.keys(object).length;
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
		hpRatioDenominator: 999999,
		minReleaseLevel: 80,
		reponseTimeout: location.pathname.endsWith("_debug.html") ? 1999 : 20, // seconds
		queueTimeout: location.pathname.endsWith("_debug.html") ? 999 : 10, // seconds
		queueInterval: 0.25, // seconds,
		pi2: 2*Math.PI,
		degToRad: Math.PI/180,
		deg90AsRad: Math.PI/2,
		deg180AsRad: Math.PI,
		deg270AsRad: Math.PI*270/180,
		deg135AsRad: Math.PI*135/180,
		deg315AsRad: Math.PI*315/180
	};
	
	Lunar.TargetType = {
		allOpponents: {
			accepts: targets => targets.length === 0,
			name: 'ALL_OPPONENTS',
		},
		allOtherPokemon: {
			accepts: targets => targets.length === 1 && !targets[0].isUser,
			name: 'ALL_OTHER_POKEMON',
		},
		allPokemon: {
			accepts: targets => targets.length === 0,
			name: 'ALL_POKEMON'
		},
		ally: {
			accepts: targets => targets.length === 1 && !targets[0].isUser && targets[0].isPlayer,
			name: 'ALLY',
		},
		entireField: {
			accepts: targets => targets.length === 0,
			name: 'ENTIRE_FIELD',
		},
		opponentsField: {
			accepts: targets => targets.length === 1 && !targets[0].isPlayer, 
			name: 'OPPONENTS_FIELD',
		},
		randomOpponent: {
			accepts: targets => targets.length === 0, 
			name: 'RANDOM_OPPONENT',
		},
		selectedPokemon: {
			accepts: targets => targets.length === 1,
			name: 'SELECTED_POKEMON',
		},
		selectedPokemonMeFirst: {
			accepts: targets => targets.length === 0,
			name: 'SELECTED_POKEMON_ME_FIRST'
		},
		specificMove: {
			accepts: targets => targets.length === 0,
			name: 'SPECIFIC_MOVE'
		},
		user: {
			accepts: targets => targets.length === 0,
			name: 'USER',
		},
		userAndAllies: {
			accepts: targets => targets.length === 0,
			name: 'USER_AND_ALLIES'
		},
		userOrAlly: {
			accepts: targets => targets.length === 1 && targets[0].isPlayer,
			name: 'USER_OR_ALLY'
		},
		usersField: {
			accepts: targets => targets.length === 1 && targets[0].isPlayer,
			name: 'USERS_FIELD'	
		}
	};
	
	const targetTypeMap = {};
	for (let key of Object.keys(Lunar.TargetType)) {
		targetTypeMap[Lunar.TargetType[key].name] = Lunar.TargetType[key]; 
	}
	Lunar.TargetTypeByName = name => {
		return targetTypeMap[name.toUpperCase()];
	};
	
	Lunar.FontStyle = {
		_setup: function(game) {
			Lunar.FontStyle.charIconLevel = new PIXI.TextStyle({
				fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.020,
			    fontWeight: 'bold',
			    fill: '#ffffff',
			    stroke: '#777777',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 3,
			    wordWrap: false
			});
			Lunar.FontStyle.stat = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.dx(0.020),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#ff9900'],
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false
			});
			Lunar.FontStyle.statValue = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.dx(0.020),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#99ff00'],
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false
			});
			Lunar.FontStyle.battleTextbox = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.026),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#ff9900'],
			    stroke: '#4a1850',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false
			});			
			Lunar.FontStyle.dialog = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.040),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#00ff99'],
			    stroke: '#4a1850',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game => game.dx(0.8)
			});
			Lunar.FontStyle.input = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.030),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: '#99ff00',
			    stroke: '#000000',
			    strokeThickness: 2,
			    align: 'center',
			    dropShadow: true,
			    dropShadowColor: '#558800',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game => game.dx(0.8)
			});
			Lunar.FontStyle.playerSearch = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.026),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: '#99ff99',
			    stroke: '#000000',
			    strokeThickness: 2,
			    align: 'left',
			    dropShadow: true,
			    dropShadowColor: '#558800',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false,
			});
			Lunar.FontStyle.load = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.028),
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
			    wordWrapWidth: game => game.w
			});
			Lunar.FontStyle.control = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.029),
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#99ff66'],
			    stroke: '#4a1850',
			    strokeThickness: 5,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game => game.w
			});
			Lunar.FontStyle.controlActive = Object.assign(Lunar.FontStyle.control.clone(), {
				fill: ['#99ff66', '#ffffff'],
				fontSize: game => game.dx(0.032),
			}); 
			Lunar.FontStyle.button = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.032),
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
			    wordWrapWidth: game => game.w
			});
			Lunar.FontStyle.buttonDisabled = Object.assign(Lunar.FontStyle.button.clone(), {
				fill: ['#eeeeee', '#999999']
			});
			Lunar.FontStyle.buttonActive = new PIXI.TextStyle({
			    fontFamily: 'Arial',
			    fontSize: game => game.dx(0.036),
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
			    wordWrapWidth: game => game.w
			});
			Lunar.FontStyle.inviteReceiveTitle = new PIXI.TextStyle({
				fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.032,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#c0c0c0'],
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false
			});
			Lunar.FontStyle.inviteReceiveNickname = new PIXI.TextStyle({
				fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.045,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#ff9900'],
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false
			});			
			Lunar.FontStyle.playerDesc = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.022,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#C0C0C0'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    breakWords: true,
			    wordWrap: true,
			    wordWrapWidth: game => game.dx(0.36)
			});
			Lunar.FontStyle.playerTitle = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.035,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#ff9900'],
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false
			});			
			Lunar.FontStyle.playerList = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.022,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#99ff66'],
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false,
			    wordWrapWidth: game => game.dx(0.4)
			});
			Lunar.FontStyle.playerListActive = Object.assign({}, Lunar.FontStyle.playerList, {
				fill: ['#99ff66', '#ffffff']
			});
			Lunar.FontStyle.skillDesc = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.025,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#ff9900'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: true,
			    wordWrapWidth: game => game.dx(0.4)
			});
			Lunar.FontStyle.skillStat = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.030,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#C0C0C0'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false,
			    wordWrapWidth: game => game.dx(0.4)
			});
			Lunar.FontStyle.skillList = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.025,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#C0C0C0'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false,
			    wordWrapWidth: game => game.dx(0.4)
			});
			Lunar.FontStyle.skillTitle = new PIXI.TextStyle({
			    fontFamily: 'Arial,sans-serif',
			    fontSize: game => game.wh*0.040,
			    fontStyle: '',
			    fontWeight: 'bold',
			    fill: ['#ffffff', '#ff9900'], // gradient
			    stroke: '#4a1850',
			    strokeThickness: 2,
			    dropShadow: true,
			    dropShadowColor: '#000000',
			    dropShadowBlur: 4,
			    dropShadowAngle: Math.PI / 6,
			    dropShadowDistance: 6,
			    wordWrap: false,
			    wordWrapWidth: game => game.dx(0.4)
			});
			// Element variations
			Lunar.FontStyle._elements(Lunar.FontStyle.stat, 'stat');
			Lunar.FontStyle._elements(Lunar.FontStyle.skillStat, 'skillStat');
			Lunar.FontStyle._elements(Lunar.FontStyle.skillList, 'skillList');
			// Store computed properties.
			Object.keys(Lunar.FontStyle).forEach(type => {
				if (type.startsWith('_'))
					return;
				const style = Lunar.FontStyle[type];
				Object.keys(style).forEach(key => {
					const value = style[key];
					if (typeof(value) === 'function') {
						Lunar.FontStyle._geo[type] = Lunar.FontStyle._geo[type] || {}
						Lunar.FontStyle._geo[type][key] = value;
					}
				});
			});
			// Compute current property values.
			Lunar.FontStyle._layout(game);
		},
		_elements: (baseStyle, baseName) => {
			Lunar.FontStyle[baseName+'NORMAL'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#8b8b5a']
			});
			Lunar.FontStyle[baseName+'POISON'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#823482']
			});
			Lunar.FontStyle[baseName+'FIRE'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#dd6711']
			});
			Lunar.FontStyle[baseName+'WATER'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#396deb']
			});
			Lunar.FontStyle[baseName+'ELECTRIC'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#f1c209']
			});
			Lunar.FontStyle[baseName+'GRASS'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#5daa36']
			});
			Lunar.FontStyle[baseName+'ICE'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#6cc7c7']
			});
			Lunar.FontStyle[baseName+'FIGHTING'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#9e2822']
			});
			Lunar.FontStyle[baseName+'GROUND'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#d5a933']
			});
			Lunar.FontStyle[baseName+'FLYING'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#9381c7']
			});
			Lunar.FontStyle[baseName+'PSYCHIC'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#f62160']
			});
			Lunar.FontStyle[baseName+'BUG'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#8e9b1b']
			});
			Lunar.FontStyle[baseName+'ROCK'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#96832e']
			});
			Lunar.FontStyle[baseName+'GHOST'] = Object.assign(baseStyle.clone(), {
				fill: ['#ffffff', '#574577']
			});
			Lunar.FontStyle[baseName+'DRAGON'] = Object.assign(Lunar.FontStyle.stat.clone(), {
				fill: ['#ffffff', '#4d0af0']
			});
			Lunar.FontStyle[baseName+'DARK'] = Object.assign(Lunar.FontStyle.stat.clone(), {
				fill: ['#ffffff', '#534136']
			});
			Lunar.FontStyle[baseName+'STEEL'] = Object.assign(Lunar.FontStyle.stat.clone(), {
				fill: ['#ffffff', '#9898bb']
			});
			Lunar.FontStyle[baseName+'FAIRY'] = Object.assign(Lunar.FontStyle.stat.clone(), {
				fill: ['#ffffff', '#de6fde']
			});	
		},
		_geo: {}, 
		_layout: game => {
			Object.keys(Lunar.FontStyle._geo).forEach(type => {
				const properties = Lunar.FontStyle._geo[type];
				Object.keys(properties).forEach(key => {
					const value = properties[key];
					Lunar.FontStyle[type][key] = value(game);
				});
			});
		}		
	};
	
	Lunar.File = {
		removeExtension: filename => {
			const slash = filename.lastIndexOf('/');
			const dot = filename.lastIndexOf('.');
			if (dot < 0 || dot < slash)
				return filename;
			return filename.substr(0, dot);
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
			if (dim.length === 0)
				return [];
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
			if (dim.length === 0)
				return [];
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
			if (dimX.length === 0 || dimY.length === 0)
				return [];
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
		
		proportionalScale: (scalable, targetWidth, targetHeight, w = undefined, h = undefined) => {
			w = w !== undefined ? w : scalable.width;
			h = h !== undefined ? h : scalable.height;
			if (w*h < 1E-2) {
				scalable.width = 0;
				scalable.height = 0;
				return;
			}
			let hTest = h*targetWidth/w;
			if (hTest <= targetHeight) {
				scalable.width = targetWidth;
				scalable.height = hTest;
			}
			else {
				scalable.width = w*targetHeight/h
				scalable.height = targetHeight;
			}
		},
		
		apply(dimensionable, geometry, {rotation, scale, anchor, proportional, keepSize} = {}) {
			let sx = 1;
			let sy = 1;
			if (scale !== undefined) {
				if (Array.isArray(scale)) {
					sx = scale[0];
					sy = scale[1];
				}
				else
					sx = sy = scale;
			}
			if (dimensionable.constructor !== PIXI.Container && !keepSize) {
				if (proportional)
					Lunar.Geometry.proportionalScale(dimensionable, geometry.w*sx, geometry.h*sy);	
				else {
					dimensionable.width = geometry.w*sx;
					dimensionable.height = geometry.h*sy;
				}
			}
			const a = anchor !== undefined ? Array.isArray(anchor) ? anchor : [anchor] : [0];
			const ax = a[0];
			const ay = a.length > 1 ? a[1] : ax;
			dimensionable.position.set(geometry.x + ax*geometry.w, geometry.y + ay*geometry.h)
			if (rotation !== undefined)
				dimensionable.rotation = rotation;
//			if (scale !== undefined)
//				dimensionable.scale.set(...Array.isArray(scale)?scale:[scale]);
			if (anchor !== undefined)
				dimensionable.anchor.set(...a);
			return dimensionable;
		}
	};
	
	Lunar.Math = {
		modulo: (x,m) => x < 0 ? (x%m + m) : (x % m) 
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