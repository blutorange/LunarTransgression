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

class TransluneGame {
	/**
	 * @param {window} window
	 * @param {DOMElement} gameDiv
	 * @param {TransluneNet} net
	 */
	constructor(window, net) {
		this.net = net;
		this.window = window;
		this.scenes = [];
		this.loaders = {};
	}
	
	start() {
		PIXI.settings.SCALE_MODE = PIXI.SCALE_MODES.NEAREST;
		this.net.registerConnectListener(this._onConnect.bind(this));
		this.net.registerFinalizeListener(this._onFinalize.bind(this));
		this.app = new PIXI.Application(1280,720, {
			backgroundColor: 0x000000
		});
		this.addStack = [];
		this.removalStack = [];
		this.app.loader.baseUrl = this.net.httpBase;
		this.loaders['base'] = this.app.loader;
		this._prepareLoader();
		
		const loadableResource = new LoaderLoadable(this.app.loader, this.onLoaded.bind(this));
		const loadableNet = new ManualLoadable();
		const loadableChained = new ChainedLoadable(loadableResource, loadableNet);
		loadableChained.addCompletionListener(this.onLoaded.bind(this));
		
		this.loadableNet = loadableNet;
		this.pushScene(new TransluneSceneLoad(this, loadableChained));
		this.app.ticker.add(this._update, this);
		for (let child of this.window.document.body.children) child.remove();	
		this.net.connect();
		this.app.loader.load();
		
		this.window.document.body.appendChild(this.app.view);
	}
	
	/**
	 * @private
	 */
	onLoaded() {
		new TransluneSceneMenu(this);
	}
	
	/**
	 * @private
	 */
	_prepareLoader() {
		this.app.loader
			.add('textbox', 'resources/translune/static/textbox-blue.9.json')
			//.add()
		;
	}
	
	/**
	 * @private
	 */
	_onConnect() {
		this.loadableNet.progress = 1;
		const _this = this;
		_this.net.dispatchMessage(Lunar.Message.fetchData, {
			fetch: Lunar.FetchType.userPlayer		
		}).then(response => {
			console.log(response.data);
		}).catch(() => {
			console.log("an error occured")
		}).then(() => {
			console.log("done1");
		});
		_this.net.dispatchMessage(Lunar.Message.fetchData, {
			fetch: Lunar.FetchType.userPlayer		
		}).then(response => {
			console.log(response.data);
		}).catch(() => {
			console.log("an error occured")
		}).then(() => {
			console.log("done2");
			_this.window.setTimeout(() => _this.net.finalize(), 20000);
		});
	}
	
	pushScene(scene) {
		this.addStack.push(scene);
	}
	
	removeScene(scene) {
		this.removalStack.push(scene);
	}
	
	/**
	 * @private
	 */
	_addRemoveScenes() {
		for (let scene of this.addStack) {
			this.scenes.push(scene);
			this.app.stage.addChild(scene.view);
		}
		for (let scene of this.removalStack) {
			const index = this.scenes.indexOf(scene);
			if (index >= 0)
				this.scenes.splice(index,1);
			this.app.stage.removeChild(scene.view);
		}
		this.addStack = [];
		this.removalStack = [];
	}
	
	/**
	 * @param {string} name Name of the loader.
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
	 * Transforms from the internally used coordinates to screen
	 * coordinates.
	 * The origin of the internal coordinates is located at the center
	 * of the screen and extend from (-1,-1) to (+1,+1). Positive
	 * x-direction is to the right, positive y-direction to the top.
	 * @param {number} relativeX Coordinate X, between -1 and 1.
	 */
	x(relativeX) {
		return (this.app.renderer.width * 0.5 * (relativeX+1.0)) | 0;
	}

	/**
	 * Transforms from the internally used coordinates to screen
	 * coordinates.
	 * The origin of the internal coordinates is located at the center
	 * of the screen and extend from (-1,-1) to (+1,+1). Positive
	 * x-direction is to the right, positive y-direction to the top.
	 * @param {number} relativeY Coordinate Y, between -1 and 1.
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
	 * @private
	 */
	_onFinalize() {
		for (let scene of this.scenes)
			scene.destroy();
		for (let loader of Object.values(this.loaders))
			loader.destroy();
		this.app.destroy();
		this.window.alert("Network closed, ending game.");
		// TODO Uncomment this again
		// this.window.close();
	}
	
	static startGame(url) {
		var a = document.createElement("a");
		a.href = url;
		a.target = "_blank";
		a.setAttribute("style","display:hidden;");
		document.body.appendChild(a);
		a.click();
		document.body.removeChild(a);
	}
}