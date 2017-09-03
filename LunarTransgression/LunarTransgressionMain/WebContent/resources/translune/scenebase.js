(function(Lunar, window, undefined) {
	Lunar.Scene = {};
	
	/**
	 * Example for the structure of a scene with all methods
	 * that must be implemented.
	 */
	Lunar.Scene.Base = class {
		constructor(game) {
			this._game = game;
			this._view = new PIXI.Container();
			this._time = 0;
			this._hierarchy = {};
		}
		
		method(name) {
			return this[name].bind(this);
		}
		
		destroy() {
			this._hierarchy = {};
			this._game = undefined;
			this._view.destroy({children:true});
		}
		
		sceneToAdd() {
			return this;
		}
		
		layout() {
		}
		
		onAdd() {
			this.layout();
		}
		
		onRemove() {
			this.destroy();
		}
		
		update(delta) {
			this._time += delta;
		}
		
		get hierarchy() {
			return this._hierarchy;
		}
		
		set hierarchy(hierarchy) {
			this._hierarchy = hierarchy;
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
		
		showConfirmDialog(message) {
			this._game.pushScene(new Lunar.Scene.Dialog(this._game, { 
				message: message || "Please confirm",
				choices: [
					{
						text: "Ok",
						callback: dialog => {
							this._game.sfx('resources/translune/static/ping');
							dialog.close()
						}
					}
				]
			}));
		}
		
		get prompt() {
			return this.hierarchy.top.$prompt;
		}
		
		geo(dimensionable, geometry, {rotation, scale, anchor, proportional, keepSize} = {}) {
			if (dimensionable.constructor !== PIXI.Container && !keepSize) {
				if (proportional)
					Lunar.Geometry.proportionalScale(dimensionable, geometry.w, geometry.h);	
				else {
					dimensionable.width = geometry.w;
					dimensionable.height = geometry.h;
				}
			}
			const a = anchor !== undefined ? Array.isArray(anchor) ? anchor : [anchor] : [0];
			const ax = a[0];
			const ay = a.length > 1 ? a[1] : ax;
			dimensionable.position.set(geometry.x + ax*geometry.w, geometry.y + ay*geometry.h)
			if (rotation !== undefined)
				dimensionable.rotation = rotation;
			if (scale !== undefined)
				dimensionable.scale.set(...Array.isArray(scale)?scale:[scale]);
			if (anchor !== undefined)
				dimensionable.anchor.set(...a);
			return dimensionable;
		}

		/**
		 * @param {PIXI.text} text
		 * @param {PIXI.NinePatch} ninepatch May be omitted if the text is the immediate child of the ninepatch.
		 */
		layoutButtonText(text, keepSize = false, ninepatch = undefined) {
			ninepatch = ninepatch === undefined ? text.parent.parent : ninepatch;
			const width = ninepatch.bodyWidth || ninepatch.width;
			const height = ninepatch.bodyHeight || ninepatch.height;
			this.geo(text, {x:0,y:0,w:width,h:height}, {keepSize: keepSize, anchor: 0.5});
		}

		
		/**
		 * @param {PIXI.Container} ninepatch
		 * @param {string} text
		 * @param {PIXI.TextStyle} style
		 * @param {PIXI.TextStyle} styleActive
		 */
		createButtonText(ninepatch, text = "", style = Lunar.FontStyle.button, styleActive = Lunar.FontStyle.buttonActive) {
			const pixiText = new PIXI.Text(text, style);
			ninepatch.interactive = true;
			ninepatch.buttonMode = true;
			ninepatch.on('pointerover', () => pixiText.style = styleActive);
			ninepatch.on('pointerout', () => pixiText.style = style);
			return pixiText;
		}
	}
	
	Lunar.Scene.Dialog = class extends Lunar.Scene.Base {
		/**
		 * @param {prompt: {style:PIXI.FontStyle, initial:string}, message: string, choices: array<{text:string,style:PIXI.TextStyle,styleActive:PIXI.TextStyle,callback}>} options 
		 */
		constructor(game, options) {
			super(game);
			this.options = options;
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
		}
		
		destroy() {
			this.options = undefined;
			super.destroy();
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			const geoRoot = Lunar.Geometry.layoutVbox({
				box: {w: this.game.w, h: this.game.h},
				padding: {
					left: 0.2,
					right: 0.2,
					top: 0.2,
					bottom: 0.2
				},				
				dimension: 2,
			});
			const geoMessage = Lunar.Geometry.layoutVbox({
				box: geoRoot[0],
				dimension: this.options.prompt ? 2 : 1,
				relative: true
			});
			const geoButton = Lunar.Geometry.layoutHbox({
				box: geoRoot[1],
				dimension: this.options.choices.length,
				padding: {
					x: 6,
					top:0.2,
					bottom: 0.2
				},
				relative: true
			});
			
			h.$overlay.clear();
			h.$overlay.beginFill(0x222222, 0.85);
			h.$overlay.drawRect(0, 0, this.game.w, this.game.h);
			h.$overlay.endFill();
			
			this.geo(h.$top, geoRoot[0]);
			this.geo(h.$bottom, geoRoot[1]);
			this.geo(h.top.$message, geoMessage[0], {anchor: 0.5, keepSize: true});
			if (this.options.prompt)
				this.geo(h.top.$prompt, geoMessage[1], {anchor: 0.5});
			for (let i = this.options.choices.length; i-->0;) {
				this.geo(h.bottom.$buttons[i].$button, geoButton[i]);
				this.layoutButtonText(h.bottom.$buttons[i].button.$text, true);
			}
		}
		
		update(delta) {
			super.update(delta);
			if (this.options.update)
				this.options.update(delta, this);
		}
		
		/**
		 * @private
		 */
		_initScene() {
			const _this = this;
			const overlay = new PIXI.Graphics();
			const topContainer = new PIXI.Container();
			const bottomContainer = new PIXI.Container();
			const styleMessage = this.options.style || Lunar.FontStyle.dialog;
			const textMessage = new PIXI.Text(this.options.message, styleMessage);
			const prompt = this.options.prompt;
			
			const bottomButtons = this.options.choices.map(choice => {
				const button = new PIXI.NinePatch(_this.game.loaderFor('base').resources.textbox);
				const buttonText = _this.createButtonText(button, 
						choice.text, choice.style || Lunar.FontStyle.button,
						choice.styleActive || Lunar.FontStyle.buttonActive);
				button.on('pointerdown', () => choice.callback(_this));
				button.body.addChild(buttonText);
				bottomContainer.addChild(button);
				return {
					$button: button,
					button: {
						$text: buttonText
					}
				};
			});
			
			const inputPrompt = prompt ? new PIXI.TextInput(Lunar.FontStyle.button) : undefined;
			if (inputPrompt) {
				inputPrompt.text = prompt.initial;
				inputPrompt.style = prompt.style || Lunar.FontStyle.input;
				inputPrompt.on('change', input => input.text.length === 0 && (input.text = prompt.initial));
				prompt.setup && prompt.setup(inputPrompt);
			}

			this.view.addChild(overlay);
			this.view.addChild(topContainer);
			this.view.addChild(bottomContainer);
			topContainer.addChild(textMessage);
			if (prompt)
				topContainer.addChild(inputPrompt);

			this.hierarchy = {
				$top: topContainer,
				$bottom: bottomContainer,
				$overlay: overlay,
				top: {
					$message: textMessage,
					$prompt: inputPrompt		
				},
				bottom: {
					$buttons: bottomButtons
				}
			};
		}
		
		close() {
			this.game.removeScene(this);
		}
	}
	
	Lunar.Scene.Load = class extends Lunar.Scene.Base {
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
			this.loadable = loadable;
			this.zeroUntil = Lunar.Interpolation.zeroUntil(0.55);
			this.done = false;
		}
		
		destroy() {
			this.loadable = undefined;
			super.destroy();
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
})(window.Lunar, window);