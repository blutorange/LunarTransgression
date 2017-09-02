/*
- 1 Loader/Scene

Scene menu
 Resources:
   - 20 poke icons
   - 30 item icons
   - Menu BGM music
   - other UI elements

Scene opponent select
 Resources:
   
Scene battlePreparation
 Resources:
   - 2 * 20 poke icons (+opponent)
   - 30 item icons 
   - Prep BGM music
   - other UI elements

Scene battle
 Resources:
   - 2players * 2*backFront * 4 poke images
   - battleBackground
   - 2*4 poke cries
   - Battle BGM music
   - other UI elements
*/
(function(Lunar, window, undefined){
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
			this.addStack = [];
			this.removalStack = [];
			this.app.loader.baseUrl = this.net.httpBase;
			this.loaders['base'] = this.app.loader;
			this._prepareLoader();
			
			Lunar.FontStyle.setup(this);
			
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
	        const onFullscreenChange =
	        		document.documentElement.onfullscreenchange ? 'onfullscreenchange' :
	        		document.documentElement.onmozfullscreenchange  ? 'onmozfullscreenchange' :
	        		document.documentElement.MSFullscreenChange ? 'MSFullscreenChange' :
	        		document.documentElement.onwebkitfullscreenchange ? 'onwebkitfullscreenchange' : undefined;
	        if (onFullscreenChange) {
	        	document.documentElement[onFullscreenChange] = () => {
	    			_this.updateScreen();
	        	}
	        }
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
			Lunar.FontStyle.layout(this);
			for (let scene of this.scenes)
				scene.layout();
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
			this.addStack.push({scene: scene, container: container || this.app.stage});
		}
		
		removeScene(scene) {
			this.removalStack.push(scene);
		}
		
		/**
		 * @private
		 */
		sfx(path, volume = 0.25) {
			const base = Lunar.File.removeExtension(this.net.httpBase + "/" + path);
			let howler = this.sfx[base];
			if (!howler) {
				howler = this.sfx[base] = new Howl({
					src: [base + ".webm", base + '.mp3'],
					loop: false,
					volume: volume
				});
			}
			howler.play();
		}
		
		/**
		 * @param {string|array}
		 *            path Path to the background music to play, eg.
		 *            'static/menu/bgm1'
		 */
		switchBgm(path, volume = 0.25, fade = 700) {
			const bgmHowl = !path ? undefined : new Howl({
				src: Array.isArray(path) ?
						path.map(file => this.net.httpBase + '/' + file) :
						[this.net.httpBase + '/' + path + ".webm", path + '.mp3'],
				loop: true,
				volume: 0
			});
			const _this = this;
			const playNew = () => {
				_this.bgmId = bgmHowl.play();
				_this.bgmHowl = bgmHowl;
				_this.bgmHowl.fade(0, volume, fade, _this.bgmId);
			}
			if (this.bgmHowl) {
				if (path)
					this.bgmHowl.once('fade', playNew, this.bgmHowlId);
				this.bgmHowl.fade(this.bgmHowl.volume(this.bgmId), 0.0, fade, this.bgmId);
			}
			else if (path) {
				playNew();
			}
		}
		
		/**
		 * @private
		 */
		_addRemoveScenes() {
			for (let entry of this.addStack) {
				const delegate = entry.scene.sceneToAdd();
				this.scenes.push(delegate);
				entry.container.addChild(delegate.view);
				delegate.onAdd();
			}
			for (let scene of this.removalStack) {
				const index = this.scenes.indexOf(scene);
				if (index >= 0) {
					this.scenes.splice(index,1);
					scene.onRemove();
				}
				if (scene.view.parent)
					scene.view.parent.removeChild(scene.view);
			}
			this.addStack = [];
			this.removalStack = [];
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
		
		get h() {
			return this.app.renderer.height;
		}
		
		/**
		 * @private
		 */
		_update(delta) {
			this._addRemoveScenes();
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