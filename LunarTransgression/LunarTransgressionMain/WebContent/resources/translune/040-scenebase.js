(function(Lunar, window, undefined) {
	Lunar.Scene = {};
	
	/**
	 * Example for the structure of a scene with all methods
	 * that must be implemented.
	 */
	Lunar.Scene.Base = class extends PIXI.utils.EventEmitter {
		constructor(game) {
			super();
			this._game = game;
			this._view = new PIXI.ClipContainer();
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
			this._view.width = this._view.parent.width;
			this._view.height = this._view.parent.height;
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
		
		get modal() {
			return this._modal;
		}
		
		set modal(value) {
			const needsUpdate = value != this._modal;
			this._modal = value;
			if (needsUpdate)
				this.updateModal();
		}
		
		updateModal() {
			// override
		}
		
		showConfirmDialog(message, onClose) {
			this._game.pushScene(new Lunar.Scene.Dialog(this._game, { 
				message: message || "Please confirm",
				choices: [
					{
						text: "Ok",
						callback: dialog => {
							this._game.sfx('resources/translune/static/ping');
							dialog.close()
							onClose && onClose();
						}
					}
				]
			}));
		}
		
		get prompt() {
			return this.hierarchy.top.$prompt;
		}
		
		geo(dimensionable, geometry, options) {
			return Lunar.Geometry.apply(dimensionable, geometry, options)
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
	};
	
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
					left: 0.1,
					right: 0.1,
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
					x: 12,
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
			const topContainer = new PIXI.ClipContainer();
			const bottomContainer = new PIXI.ClipContainer();
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
				inputPrompt.placeholder = prompt.placeholder || '';
				if (prompt.minlength)
					inputPrompt.minlength = prompt.minlength;
				if (prompt.maxlength)
					inputPrompt.maxlength = prompt.maxlength;
				//inputPrompt.on('change', input => input.text.length === 0 && (input.text = prompt.initial));
				prompt.setup && prompt.setup(inputPrompt);
			}
			
			overlay.interactive = true;

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
	};
	
	Lunar.Scene.Load = class extends Lunar.Scene.Base {
		/**
		 * @param {Loadable} loadable An optional object that
		 * reports the current loading progress as a number between 0
		 * and 1. If given, the progress is displayed by the load scene.
		 */
		constructor(game, loadable = undefined) {
			super(game);
			this._loadable = loadable;
			this._zeroUntil = Lunar.Interpolation.zeroUntil(0.55);
			this._done = false;
		}
		
		layout() {
			super.layout();
			
			const h = this.hierarchy;
			
			h.container.$overlay.clear();
			h.container.$overlay.beginFill(0x222222, 0.85);
			h.container.$overlay.drawRect(0, 0, this.game.w, this.game.h);
			h.container.$overlay.endFill();
			
			h.container.$text.x = this.game.x(0);
			h.container.$text.y = this.game.y(0);
			h.container.$text.anchor.set(0.5,0.5);
		}
		
		/**
		 * @private
		 */
		_initScene() {
			const container = new PIXI.ClipContainer();
			
			const overlay = new PIXI.Graphics();
			overlay.interactive = true;
			
			const loadText = new PIXI.Text('Now loading...', Lunar.FontStyle.load);
			
			this.game.app.stage.addChild(container);
			container.addChild(overlay);
			container.addChild(loadText);
			
			this.hierarchy = {
				container: {
					$text: loadText,
					$overlay: overlay					
				},
				$container: container
			};
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
		}
		
		destroy() {
			this._loadable = undefined;
			this._zeroUntil = undefined;
			this.game.app.stage.removeChild(this.hierarchy.container);
			this.hierarchy.$container.destroy();
			super.destroy();
		}
		
		update(delta) {
			super.update(delta);
			const loadText = this.hierarchy.container.$text;
			if (this._loadable) {
				const raw = this._loadable.getProgress();
				const progress = Math.round(100.0*raw);
				loadText.text = `Now loading... ${progress < 0 ? 0 : progress > 100 ? 100 : progress}%`;
				if (raw === 1 && !this._done) {
					this._loadable.notifyCompletionListeners();
					this._done = true;
					this.game.removeScene(this);
				}
			}
			loadText.alpha = 0.25+0.75*Lunar.Interpolation.slowInSlowOut(Lunar.Interpolation.backAndForth((this.time%3)/3.0));
			loadText.scale.set(1.0+0.2*Lunar.Interpolation.slowInSlowOut(Lunar.Interpolation.backAndForth((this.time%2)/2.0)));
			loadText.rotation = Lunar.Constants.pi2*Lunar.Interpolation.slowInSlowOut(this._zeroUntil((this.time%2.5)/2.5));
		}
	};
	
	/**
	 * Page numbers start at 0. Rendering starts at 1.
	 * Events:
	 *   - page-change(Lunar.Scene.Pager) Emitted when the user switches to a different page.
	 */	
	Lunar.Scene.Pager = class extends Lunar.Scene.Base {
		/**
		 * - prev (required) Icon for the previous page button.
		 * - next (required) Icon for the next page button.
		 * - style (optional) Text style of the indicator. Default is new text style.
		 * - format (optional) How to format the page indicator. Default is '__cur__ / __max__'
		 * - pageCount (optional) Initial page count. Default is 1.
		 * - page (optional) Initial page. Default is 0.
		 * @param {pageCount: number, page: number, format: string, prev: PIXI.Texture, next: PIXI.Texture, style: PIXI.TextStyle} options. 
		 */
		constructor(game, options) {
			super(game);
			this._pagerPageCount = 1;
			this._pagerPage = options.page||0;
			this._pagerStyle = options.style || new PIXI.TextStyle();
			this._pagerFormat = options.format || '__cur__ / __max__';
			this._pagerPrev = new PIXI.Sprite(options.prev);
			this._pagerNext = new PIXI.Sprite(options.next);
			this._pagerIndicator = new PIXI.Text("", this._pagerStyle);
			if (options.pageCount)
				this.pageCount = options.pageCount
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
			// Go to page 1 initially.
			const initialPage = this._pagerPage;
			this._pagerPage = -1;
			this.setPage(initialPage );
		}
		
		destroy() {
			this._pagerFormat = undefined;
			this._pagerPrev = undefined;
			this._pagerNext = undefined;
			this._pagerIndicator = undefined;			
			super.destroy();
		}
		
		layout() {
			super.layout();
			const layout = Lunar.Geometry.layoutHbox({
				box: {w: this.view.width, h: this.view.height},
				dimension: [1,3,1],
				padding: {
					left: 0.05,
					right: 0.05,
					top: 0.05,
					bottom: 0.05,
					x: 12
				},
				relative: true
			});
			Lunar.Geometry.apply(this._pagerPrev, layout[0], {proportional: true, anchor: 0.5});
			Lunar.Geometry.apply(this._pagerIndicator, layout[1], {keepSize: true, anchor: 0.5});
			Lunar.Geometry.apply(this._pagerNext, layout[2], {proportional: true, anchor: 0.5});
		}

		/**
		 * @private
		 */
		_initScene() {
			this.view.addChild(this._pagerPrev);
			this.view.addChild(this._pagerNext);
			this.view.addChild(this._pagerIndicator);
			this._pagerPrev.interactive = true;
			this._pagerNext.interactive = true;
			this._pagerPrev.buttonMode = true;
			this._pagerNext.buttonMode = true;
			this._pagerPrev.on('pointertap', () => this._onClickChange(-1), this);
			this._pagerNext.on('pointertap', () => this._onClickChange(1), this);
		}

		/**
		 * @private
		 */
		_onClickChange(amount) {
			this.setPage(this.page + amount, true);
		}
		
		setPage(page, playSound = false) {
			let newPage = Math.round(page);
			if (newPage < 0)
				newPage = 0;
			else if (newPage >= this._pagerPageCount)
				newPage = this._pagerPageCount - 1;
			const textIndicator = this._pagerFormat.replace('__cur__', newPage+1).replace('__max__', this._pagerPageCount);
			if (newPage === this._pagerPage && this._pagerIndicator.text === textIndicator) {
				if (playSound)
					this.game.sfx('resources/translune/static/unable');
				return;
			}		
			if (playSound)
				this.game.sfx('resources/translune/static/ping');
			this._pagerIndicator.text = textIndicator;			
			this._pagerPage = newPage;
			this._pagerPrev.visible = newPage !== 0; 
			this._pagerNext.visible = newPage < this.pageCount - 1;
			this.emit('page-change', this);
		}
		
		set page(value) {
			this.setPage(value, false);
		}
		
		get page() {
			return this._pagerPage;
		}
		
		/**
		 * @param value pageCount Number of pages. Rounded to the smallest integer equal or greater.
		 */
		get pageCount() {
			return this._pagerPageCount;
		}
		
		set pageCount(value) {
			if (value < 1) value = 1;
			this._pagerPageCount = Math.ceil(value||1);
			this.setPage(this._pagerPage, false);
		}
	};
})(window.Lunar, window);