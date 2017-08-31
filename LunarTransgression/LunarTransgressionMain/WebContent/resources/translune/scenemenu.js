/**
 * 
 */
class TransluneSceneMenu extends TransluneSceneBase {
	constructor(game) {
		super(game);
		this.loaded = false;
	}
	
	sceneToAdd() {
		if (this.loaded)
			return super.sceneToAdd();
		const loader = this.game.loaderFor("menu");
		loader.reset();
		loader.add("bg", `resource/bgm/bg${Lunar.Random.int(17)+1}.jpg`);
		loader.add("packed", 'resources/translune/static/menu/packed.json');
		const loadable = new LoaderLoadable(loader);
		loadable.addCompletionListener(this.init.bind(this));
		this.game.switchBgm(`/resource/bmm/bgm${Lunar.Random.int(20)+1}`);
		loader.load();		
		return new TransluneSceneLoad(this.game, loadable);
	}
	
	/**
	 * @private
	 */
	init() {
		this.loaded = true;
		this.game.pushScene(this);
		
		const topButtonPadding = 10;
		const topButtonHeight  = 0.9;
		const widthTopBar      = 0.9;
		const widthExitBar     = 1-widthTopBar;
		const heightTopBar     = 0.15;
		const heightBottomBar  = 0.15;
		const widthLeftPanel   = 0.6;
		const widthRightPanel  = 1-widthLeftPanel;
		const heightPanel      = 0.7;
		
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
		
		// Tab buttons & Text
		const buttonChar = new PIXI.NinePatch(this.game.baseLoader.resources.button, (topBar.bodyWidth-topButtonPadding*5)/4, topBar.bodyHeight*topButtonHeight);
		const textButtonChar = new PIXI.Text("Pokémon", Lunar.FontStyle.button);
		buttonChar.position.set(topButtonPadding, topBar.bodyHeight*(1-topButtonHeight)/2);
		this.buttonCommons(buttonChar, textButtonChar);
		buttonChar.on('pointerdown', this.onClickChar.bind(this));
								
		const buttonItem = new PIXI.NinePatch(this.game.baseLoader.resources.button, buttonChar.width, buttonChar.height);
		const textButtonItem = new PIXI.Text("Item", Lunar.FontStyle.button);
		buttonItem.position.set(buttonChar.x+buttonChar.width+topButtonPadding, buttonChar.y);
		this.buttonCommons(buttonItem, textButtonItem);
		buttonItem.on('pointerdown', this.onClickItem.bind(this));
		
		const buttonInvite = new PIXI.NinePatch(this.game.baseLoader.resources.button, buttonChar.width, buttonChar.height);
		const textButtonInvite = new PIXI.Text("Invite", Lunar.FontStyle.button);
		buttonInvite.position.set(buttonItem.x+buttonItem.width + topButtonPadding, buttonChar.y);
		this.buttonCommons(buttonInvite, textButtonInvite);
		buttonInvite.on('pointerdown', this.onClickInvite.bind(this));
		
		const buttonReserved = new PIXI.NinePatch(this.game.baseLoader.resources.button, buttonChar.width, buttonChar.height);
		const textButtonReserved = new PIXI.Text("???", Lunar.FontStyle.button);
		buttonReserved.position.set(buttonInvite.x+buttonInvite.width + topButtonPadding, buttonChar.y);
		textButtonReserved.anchor.set(0.5,0.5);
		textButtonReserved.position.set(buttonReserved.bodyWidth/2,buttonReserved.bodyHeight/2);
		
		// Close button
		const buttonExit = new PIXI.Sprite(this.game.loaderFor("menu").resources.packed.spritesheet.textures['close.png']);
		buttonExit.width = exitBar.bodyWidth*0.9;
		buttonExit.height = exitBar.bodyHeight*0.9;
		buttonExit.interactive = true;
		buttonExit.buttonMode = true;
		buttonExit.on('pointerover', () => {
			buttonExit.width = exitBar.bodyWidth*1.0;
			buttonExit.height = exitBar.bodyHeight*1.0;
			buttonExit.rotation = 0.2;
		});
		buttonExit.on('pointerout', () => {
			buttonExit.width = exitBar.bodyWidth*0.9;
			buttonExit.height = exitBar.bodyHeight*0.9;
			buttonExit.rotation = 0.0;
		});
		buttonExit.anchor.set(0.5,0.5);
		buttonExit.position.set(exitBar.bodyWidth/2,exitBar.bodyHeight/2);
		buttonExit.on('pointerdown', this.onClickExit.bind(this));
		
		//Background
		const bg = new PIXI.Sprite(this.game.loaderFor("menu").resources.bg.texture);
		bg.width = this.game.w;
		bg.height = this.game.h;
		
		this.view.addChild(bg);
		this.view.addChild(topBar);
		this.view.addChild(exitBar);
		this.view.addChild(leftPanel);
		this.view.addChild(rightPanel);
		this.view.addChild(bottomBar);
		
		topBar.body.addChild(buttonChar);
		topBar.body.addChild(buttonItem);
		topBar.body.addChild(buttonInvite);
		topBar.body.addChild(buttonReserved);
				
		buttonChar.body.addChild(textButtonChar);
		buttonItem.body.addChild(textButtonItem);
		buttonInvite.body.addChild(textButtonInvite);
		buttonReserved.body.addChild(textButtonReserved);
		
		exitBar.body.addChild(buttonExit);
	}
	
	/**
	 * @private
	 */
	onClickExit() {
		const _this = this;
		this.game.sfx('static/confirm');
		const id = this.game.window.setTimeout(() => _this.game.exit(), 10000);
		const start = new Date().getTime() + 10000;
		this.game.pushScene(new TransluneSceneConfirmDialog(this.game, { 
			message: "Exiting in 10 seconds...",
			update: (delta, textMessage, textChoices) =>
				textMessage.text = `Exiting in ${Math.round((start - new Date().getTime())/1000.0)} seconds...`,
			choices: [
				{
					text: "Keep playing",
					callback: close => {
						_this.game.window.clearTimeout(id);
						_this.game.sfx('static/ping');
						close();
					}
				},
//				{
//					text: "Exit now",
//					callback: close => {
//						_this.game.window.clearTimeout(id);
//						_this.game.sfx('static/ping');
//						_this.game.exit();
//					}
//				}
			]
		}));
	}
	
	/**
	 * @private
	 */
	onClickChar() {
		this.game.sfx('static/buttonclick');
		const _this = this;
		//this.game.pushScene(new TransluneSceneLoad(new Loadable()));
		this.game.net.dispatchMessage(Lunar.Message.fetchData, {
			fetch: Lunar.FetchType.userPlayer		
		}).then(response => {
			console.log(response.data);
		}).catch(() => {
			_this.game.pushScene(new TransluneSceneConfirmDialog(_this.game, { 
					message: "Could not load the game characters data, please try again later.",
					choices: [
						{
							text: "Ok",
							callback: close => close()
						}
					]
				}
			));
		}).then(() => {
			// always executed.
		});
	}
	
	/**
	 * @private
	 */
	onClickItem() {
		this.game.sfx('static/buttonclick');
	}
	
	/**
	 * @private
	 */
	onClickInvite() {
		this.game.sfx('static/buttonclick');
	}

	/**
	 * @private
	 */
	buttonCommons(button, text) {
		button.interactive = true;
		button.buttonMode = true;
		button.on('pointerover', () => text.style = Lunar.FontStyle.buttonActive);
		button.on('pointerout', () => text.style = Lunar.FontStyle.button);
		text.anchor.set(0.5,0.5);
		text.position.set(button.bodyWidth/2,button.bodyHeight/2);
	}
	
	update(delta) {
		super.update(delta);
	}
}