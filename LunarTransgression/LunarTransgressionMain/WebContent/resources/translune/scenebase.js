/**
 * Example for the structure of a scene with all methods
 * that must be implemented.
 */
class TransluneSceneBase {
	constructor(game) {
		this._game = game;
		this._view = new PIXI.Container();
		this._time = 0;
	}
	
	destroy() {
		this._game = undefined;
		this._view.destroy();
	}
	
	sceneToAdd() {
		return this;
	}
	
	onAdd() {}
	onRemove() {}
	
	update(delta) {
		this._time += delta;
	}
	
	get time() {
		return this._time;
	}
	
	get game() {
		return this._game;
	}
	
	get view() {
		return this._view;
	}
}

class TransluneSceneConfirmDialog extends TransluneSceneBase {
	/**
	 * @param {text: string, callback: function()} choices 
	 */
	constructor(game, options) {
		super(game);
	
		const overlay = new PIXI.Graphics();
		overlay.beginFill(0x222222, 0.75);
		overlay.drawRect(game.x(-1), game.y(-1), game.x(1)-game.x(-1), game.y(1)-game.y(-1));
		overlay.endFill();
		this.view.addChild(overlay);

		const style = options.style || Lunar.FontStyle.dialog;
		const textMessage = new PIXI.Text(options.message, style);
		textMessage.x = game.x(0);
		textMessage.y = game.y(0.2);
		textMessage.anchor.set(0.5,0.5);
		this.view.addChild(textMessage);
		
		this.updateCallback = options.update;
		this.textMessage = textMessage;
		this.textChoices = this._makeButtons(options.choices);
	}
	
	update(delta) {
		if (this.updateCallback) {
			this.updateCallback(delta, this.textMessage, this.textChoices);
		}
	}
	
	/**
	 * @private
	 */
	_makeButtons(choices) {
		const _this = this;
		const paddingInner = 10;
		const paddingLeft = 0.2;
		const paddingRight = 0.2;
		const choiceButtons = [];
		// choices.length*width + (choices.length-1) * padding + (paddingLeft + paddingRight)*this.game.w = this.game.w
		const width = (this.game.w - (choices.length-1) * paddingInner - (paddingLeft + paddingRight) * this.game.w) / choices.length;
		const height = this.game.dy(0.15);
		const y = this.game.y(-0.2);
		const close = this._close.bind(this);
		let x = paddingLeft * this.game.w;
		return choices.map(choice => {
			const button = new PIXI.NinePatch(_this.game.loaderFor('base').resources.textbox, width, height);
			button.position.set(x, y);		
			button.interactive = true;
			button.buttonMode = true;
			button.on('pointerover', () => text.style = choice.styleActive || Lunar.FontStyle.buttonActive);
			button.on('pointerout', () => text.style = choice.style || Lunar.FontStyle.button);
			button.on('pointerdown', () => choice.callback(close));
			
			const text = new PIXI.Text(choice.text, choice.style || Lunar.FontStyle.button);
			text.anchor.set(0.5, 0.5);
			text.position.set(0.5*button.bodyWidth, 0.5*button.bodyHeight);
			
			button.body.addChild(text);
			_this.view.addChild(button);
			
			x = x + width + paddingInner;
			return text;
		});
	}
	
	/**
	 * @private
	 */
	_close() {
		this.game.removeScene(this);
	}
}

class TransluneSceneLoad extends TransluneSceneBase {
	/**
	 * @param {Loadable} loadable An optional object that
	 * reports the current loading progress as a number between 0
	 * and 1. If given, the progress is displayed by the load scene.
	 */
	constructor(game, loadable = undefined) {
		super(game);
		
		const style = Lunar.FontStyle.load;
		
		const overlay = new PIXI.Graphics();
		overlay.beginFill(0x222222, 0.75);
		overlay.drawRect(game.x(-1), game.y(-1), game.x(1)-game.x(-1), game.y(1)-game.y(-1));
		overlay.endFill();
		this.view.addChild(overlay);
		
		const loadText = new PIXI.Text('Now loading...', style);
		loadText.x = game.x(0);
		loadText.y = game.y(0);
		loadText.anchor.set(0.5,0.5);
		this.view.addChild(loadText);
			
		this.loadText = loadText;
		this.overlay = overlay;
		this.loadable = loadable;
		this.zeroUntil = Lunar.Interpolation.zeroUntil(0.55);
		this.done = false;
	}
	
	destroy() {
		super.destroy();
		this.loadable = undefined;
	}
	
	update(delta) {
		super.update(delta);
		if (this.loadable) {
			const raw = this.loadable.getProgress();
			const progress = Math.round(100.0*raw);
			this.loadText.text = `Now loading... ${progress < 0 ? 0 : progress > 100 ? 100 : progress}%`;
			if (raw === 1 && !this.done) {
				this.loadable.notifyCompletionListeners();
				this.done = true;
				this.game.removeScene(this);
			}
		}
		this.loadText.alpha = 0.25+0.75*Lunar.Interpolation.slowInSlowOut(Lunar.Interpolation.backAndForth((this.time%3)/3.0));
		this.loadText.scale.set(1.0+0.2*Lunar.Interpolation.slowInSlowOut(Lunar.Interpolation.backAndForth((this.time%2)/2.0)));
		this.loadText.rotation = Lunar.Constants.pi2*Lunar.Interpolation.slowInSlowOut(this.zeroUntil((this.time%2.5)/2.5));
	}
}