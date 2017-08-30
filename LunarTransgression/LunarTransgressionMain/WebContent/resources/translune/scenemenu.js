/**
 * 
 */
class TransluneSceneMenu extends TransluneSceneBase {
	constructor(game) {
		super(game);
		this.loader = this.game.loaderFor("menu");
		this.loader.reset();
		this.loader.add("bg", "resources/translune/static/menu/bg1.jpg");
		const loadable = new LoaderLoadable(this.loader);
		loadable.addCompletionListener(this.init.bind(this));
		game.pushScene(new TransluneSceneLoad(game, loadable));
		this.loader.load();
	}
	
	/**
	 * @private
	 */
	init() {
		this.game.pushScene(this);
		
		const widthTopBar     = 0.9;
		const widthExitBar    = 1-widthTopBar;
		const heightTopBar    = 0.15;
		const heightBottomBar = 0.15;
		const widthLeftPanel  = 0.6;
		const widthRightPanel = 1-widthLeftPanel;
		const heightPanel     = 0.7;
		
		// Top
		const topBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthTopBar), this.game.dy(heightTopBar));
		topBar.position.set(this.game.x(-1), this.game.y(1));
		topBar.alpha = 0.5;
		
		const exitBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthExitBar), this.game.dy(heightTopBar));
		exitBar.position.set(this.game.x(-1+2*widthTopBar), this.game.y(1));
		exitBar.alpha = 0.5;

		// Left
		const leftPanel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthLeftPanel), this.game.dy(heightPanel));
		leftPanel.position.set(this.game.x(-1), this.game.y(1-2*heightTopBar));
		leftPanel.alpha = 0.65;
		
		// Right
		const rightPanel = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.dx(widthRightPanel), this.game.dy(heightPanel));
		rightPanel.position.set(this.game.x(-1+2*widthLeftPanel), this.game.y(1-2*heightTopBar));
		rightPanel.alpha = 0.8;
		
		// Bottom
		const bottomBar = new PIXI.NinePatch(this.game.baseLoader.resources.textbox, this.game.w, this.game.dy(heightBottomBar));
		bottomBar.position.set(this.game.x(-1), this.game.y(1-2*(heightTopBar+heightPanel)));
		bottomBar.alpha = 0.5;
		
		const bg = new PIXI.Sprite(this.loader.resources.bg.texture);
		bg.width = this.game.w;
		bg.height = this.game.h;
		
		this.view.addChild(bg);
		this.view.addChild(topBar);
		this.view.addChild(exitBar);
		this.view.addChild(leftPanel);
		this.view.addChild(rightPanel);
		this.view.addChild(bottomBar);
	}
	
	update(delta) {
		super.update(delta);
	}
}