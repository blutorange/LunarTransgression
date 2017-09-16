(function(Lunar, window, undefined){
	// The lower, the higher the preference.
	const PREFERRED_FORMATS = {'.webm': 0, '.mp3': 1};
	
	Lunar.Game = class {
		/**
		 * @param {window}
		 *            window
		 * @param {DOMElement}
		 *            gameDiv
		 * @param {Lunar.Net}
		 *            net
		 */
		constructor(window, net) {
			this.net = net;
			this.window = window;
			this.scenes = [];
			this.loaders = {};
			this._fmath = new FMath({resolution:360*4});
			this._debug = location.pathname.endsWith("_debug.html");
		}
		
		get debug() {
			return this._debug;
		}
		
		get fmath() {
			return this._fmath;
		}
		
		start() {
			const _this = this;
			PIXI.settings.SCALE_MODE = PIXI.SCALE_MODES.NEAREST;
			this.net.registerConnectListener(this._onConnect.bind(this));
			this.net.registerFinalizeListener(this._onNetFinalize.bind(this));
			this.app = new PIXI.Application(window.innerWidth,window.innerHeight, {
				backgroundColor: 0x000000
			});
			this.finalized = false;
			this.bgmHowl = undefined;
			this.bgmId = undefined;
			this.operationStack = [];
			this.app.loader.baseUrl = this.net.httpBase;
			this.loaders['base'] = this.app.loader;
			this._prepareLoader();
			
			Lunar.FontStyle._setup(this);
			
			// Resize listener
			this.window.addEventListener("resize", () => _this._updateScreen());
			
			const loadableResource = new Lunar.LoaderLoadable(this.app.loader, this.onLoaded.bind(this));
			const loadableNet = new Lunar.ManualLoadable();
			const loadableChained = new Lunar.ChainedLoadable(loadableResource, loadableNet);
			loadableChained.addCompletionListener(this.onLoaded.bind(this));
				
			this.loadableNet = loadableNet;
			this.pushScene(new Lunar.Scene.Load(this, loadableChained));
			this.app.ticker.add(this._update, this);
			for (let child of this.window.document.body.children) child.remove();	
			this.net.connect();
			this.app.loader.load();
			
			this.window.document.body.appendChild(this.app.view);
		}
		
		goFullscreen() {
			const _this = this;
			const requestFullscreen = 
				document.documentElement.requestFullscreen ||
				document.documentElement.mozRequestFullScreen ||
				document.documentElement.webkitRequestFullscreen ||
				document.documentElement.msRequestFullscreen;
	        if (!requestFullscreen)
	        	return;
//	        document.documentElement.onfullscreenchange =
//	        	document.documentElement.onmozfullscreenchange =
//	        		document.documentElement.MSFullscreenChange =
//	        			document.documentElement.onwebkitfullscreenchange =
//	        				() => _this._updateScreen();
			requestFullscreen.call(document.documentElement);
		}
		
		leaveFullscreen() {
	        const exitFullscreen =
	        	document.exitFullscreen ||
	        	document.webkitExitFullscreen ||
	            document.mozCancelFullScreen ||
	            document.msExitFullscreen;
	        if (exitFullscreen)
	        	exitFullscreen.call(document);
		}
		
		toogleFullscreen() {
		    const isInFullScreen =
		    	(document.fullscreenElement && document.fullscreenElement !== null) ||
		    	(document.webkitFullscreenElement && document.webkitFullscreenElement !== null) ||
		    	(document.mozFullScreenElement && document.mozFullScreenElement !== null) ||
		    	(document.msFullscreenElement && document.msFullscreenElement !== null);
		    isInFullScreen ? this.leaveFullscreen() : this.goFullscreen();
		}
		
		/**
		 * @private
		 */
		_updateScreen() {
			this.app.renderer.resize(this.window.innerWidth, this.window.innerHeight);
			const isFullscreen =
				document.fullScreenElement ||
				document.webkitFullscreenElement ||
				document.mozFullScreenElement ||
				document.msFullscreenElement;
			if (isFullscreen) {
				this.app.view.classList.remove('windowed');
				this.app.view.classList.add('fullscreen');
				this.app.view.style.width = window.innerWidth + "px";
				this.app.view.style.height = window.innerHeight + "px";
			}
			else {
				this.app.view.classList.add('windowed');
				this.app.view.classList.remove('fullscreen');
				this.app.view.style.width = "";
				this.app.view.style.height = "";
			}
			Lunar.FontStyle._layout(this);
			for (let scene of this.scenes)
				scene.layout();
			this._baseLayout(this.app.stage);
		}
		
		/**
		 * @private
		 */
		_baseLayout(element) {
			for (let child of element.children||[]) {
				if (child instanceof PIXI.Text || child instanceof PIXI.TextInput) {
				 	// set dirty flag
					const text = child.text;
					child.text = "";
					child.text = text;
				}
				this._baseLayout(child);
			}
		}
		
		/**
		 * @private
		 */
		onLoaded() {
			this.pushScene(new Lunar.Scene.Menu(this));
		}
		
		/**
		 * @private
		 */
		_prepareLoader() {
			this.app.loader
				.add('textbox', 'resources/translune/static/textbox-blue.9.json')
				.add('button', 'resources/translune/static/button.9.json')
				.add('packed', 'resources/translune/static/packed.json')
				// .add()
			;
		}
		
		/**
		 * @private
		 */
		_onConnect() {
			this.loadableNet.progress = 1;
		}
		
		pushScene(scene, container = undefined) {
			this.operationStack.push({type: 'add', scene: scene, container: container || this.app.stage});
		}
		
		removeAllScenes() {
			for (let scene of this.scenes)
				this.removeScene(scene);
		}
		
		removeScene(scene) {
			if (!scene)
				return;
			this.operationStack.push({type:'del', scene: scene});
		}
		
		/**
		 * @private
		 */
		sfx(path, {volume = 0.25, play = true} = {}) {
			const _this = this;
			const base = Lunar.File.removeExtension(this.net.httpBase + "/" + path);
			let howler = this.sfx[base];
			if (!howler) {
				howler = this.sfx[base] = new Howl({
					src: [base + ".webm", base + '.mp3'],
					loop: false,
					volume: volume
				});
			}
			if (play) {
				const id = howler.play();
				howler.volume(volume, id);
			}
			return howler;
		}
		
		/**
		 * @param {string|array}
		 *            path Path to the background music to play, eg.
		 *            'static/menu/bgm1'
		 */
		switchBgm(path, volume = 0.25, fade = 700) {
			const bgmHowl = !path ? undefined : new Howl({
				src: (Array.isArray(path) ?
						path.map(file => this.net.httpBase + '/' + file) :
						[this.net.httpBase + '/' + path + ".webm", path + '.mp3']).sort(
								file => PREFERRED_FORMATS[file.substring(file.lastIndexOf('.') || 0)] || 999),
				loop: true,
				html5: true,
				volume: 0
			});
			const _this = this;
			const playNew = () => {
				_this.bgmId = bgmHowl.play();
				_this.bgmHowl = bgmHowl;
				_this.bgmHowl.fade(0, volume, fade, _this.bgmId);
			}
			if (this.bgmHowl) {
				if (path) {
					this.bgmHowl.once('fade', playNew, this.bgmHowlId);
					this.bgmHowl.fade(this.bgmHowl.volume(this.bgmId), 0.0, fade, this.bgmId);
				}
				else
					this.bgmHowl.fade(this.bgmHowl.volume(this.bgmId), volume, fade, this.bgmId);
			}
			else if (path) {
				playNew();
			}
		}
		
		clearQueuedAddScenes() {
			this.operationStack.filter(operation => operation.type !== 'add');
		}
		
		/**
		 * @private
		 */
		_applyOperations() {
			const operationStack = Array.from(this.operationStack);
			this.operationStack = [];
			for (let entry of operationStack) {
				switch (entry.type) {
				case 'add':
					const delegate = entry.scene.sceneToAdd();
					this.scenes.push(delegate);
					if (!entry.scene.isAttached) {
						entry.container.addChild(delegate.view);					
						delegate.onAdd();
					}
					break;
				case 'del':
					if (Lunar.Array.removeElement(this.scenes, entry.scene))
						if (entry.scene.isAttached)
							entry.scene.onRemove();
					if (entry.scene.view.parent)
						entry.scene.view.parent.removeChild(entry.scene.view);
					break;
				default:
					console.warn("unknown stack type", entry.type);
				}
			}
		}
		
		/**
		 * @param {string}
		 *            name Name of the loader.
		 */
		loaderFor(name) {
			const loader = this.loaders[name];
			if (loader)
				return loader;
			return this.loaders[name] = new PIXI.loaders.Loader(this.net.httpBase);
		}
		
		get baseLoader() {
			return this.loaderFor('base');
		}
		
		/**
		 * Transforms from the internally used coordinates to screen coordinates.
		 * The origin of the internal coordinates is located at the center of the
		 * screen and extend from (-1,-1) to (+1,+1). Positive x-direction is to the
		 * right, positive y-direction to the top.
		 * 
		 * @param {number}
		 *            relativeX Coordinate X, between -1 and 1.
		 */
		x(relativeX) {
			return (this.app.renderer.width * 0.5 * (relativeX+1.0)) | 0;
		}
	
		/**
		 * Transforms from the internally used coordinates to screen coordinates.
		 * The origin of the internal coordinates is located at the center of the
		 * screen and extend from (-1,-1) to (+1,+1). Positive x-direction is to the
		 * right, positive y-direction to the top.
		 * 
		 * @param {number}
		 *            relativeY Coordinate Y, between -1 and 1.
		 */
		y(relativeY) {
			return (this.app.renderer.height * 0.5 * (1.0-relativeY)) | 0;
		}
		
		get w() {
			return this.app.renderer.width;
		}
		
		dx(relative) {
			return this.app.renderer.width*relative;
		}
		
		dy(relative) {
			return this.app.renderer.height*relative;
		}
		
		get diagonal() {
			return Math.sqrt(this.w*this.w+this.h*this.h);
		}
		
		get wh() {
			return 0.5*(this.w+this.h);
		}
		
		get h() {
			return this.app.renderer.height;
		}
		
		/**
		 * @private
		 */
		_update(delta) {
			while (this.operationStack.length > 0)
				this._applyOperations();
			const deltaTime = delta / 60.0;
			for (let scene of this.scenes)
				scene.update(deltaTime);
		}
		
		/**
		 * Exits the game. Initiated by the user.
		 */
		exit() {
			this._onFinalize();
		}
		
		/**
		 * @private
		 */
		_onFinalize() {
			if (this.finalized)
				return;
			this.net.finalize();
		}
		
		/**
		 * @private
		 */
		_onNetFinalize() {
			if (this.finalized)
				return;
			this.finalized = true;
			this.operationStack = [];
			this.switchBgm();
			for (let scene of this.scenes) {
				this.removeScene(scene);
				scene.destroy();
			}
			for (let loader of Object.values(this.loaders))
				loader.destroy();
			for (let child of this.app.view.children)
				this.app.view.removeChild(child);
			this.app.render();
			this.app.destroy();
			this.window.setTimeout(() => this.window.close(), 2000);
		}
		
		/**
		 * @private
		 */
		_createMouseTracer() {
			const container = new PIXI.ParticleContainer();
			container.setProperties({
				scale: true,
				position: true,
				rotation: true,
				uvs: true,
				alpha: true
			});
			
			this.app.stage.addChild(container);
			const emitter = new PIXI.particles.Emitter(container, [
					this.loaderFor('base').resources.packed.spritesheet.textures['particle.png']
				],
				{
					"alpha": {
						"start": 0.8,
						"end": 0.1
					},
					"scale": {
						"start": 1,
						"end": 0.3,
						"minimumScaleMultiplier": 1
					},
					"color": {
						"start": "#e3f9ff",
						"end": "#0ec8f8"
					},
					"speed": {
						"start": 0,
						"end": 0,
						"minimumSpeedMultiplier": 1
					},
					"acceleration": {
						"x": 0,
						"y": 0
					},
					"maxSpeed": 0,
					"startRotation": {
						"min": 0,
						"max": 0
					},
					"noRotation": false,
					"rotationSpeed": {
						"min": 0,
						"max": 0
					},
					"lifetime": {
						"min": 0.2,
						"max": 0.2
					},
					"blendMode": "normal",
					"frequency": 0.008,
					"emitterLifetime": -1,
					"maxParticles": 1000,
					"pos": {
						"x": 0,
						"y": 0
					},
					"addAtBack": false,
					"spawnType": "point"
				}
			);
			emitter.emit = true;
			return emitter;
		}
	}
})(window.Lunar, window);