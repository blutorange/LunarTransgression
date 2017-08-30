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

class TransluneSceneLoad extends TransluneSceneBase {
	/**
	 * @param {Loadable} loadable An optional object that
	 * reports the current loading progress as a number between 0
	 * and 1. If given, the progress is displayed by the load scene.
	 */
	constructor(game, loadable = undefined) {
		super(game);
		
		const style = new PIXI.TextStyle({
		    fontFamily: 'Arial',
		    fontSize: 36,
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
		    wordWrapWidth: game.x(0.8)
		});
		
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