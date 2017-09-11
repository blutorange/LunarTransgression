/**
 * 
 */
(function(Lunar, window, undefined) {
	const LINE_THRESHOLD = 75;
	const State = {
		IDLE: Symbol("State-IDLE"),
		SETTLING: Symbol("State-Settling"),
		PRINT_CHARACTERS: Symbol("State-PRINT_CHARACTERS"),
		WAIT_AFTER_LINE: Symbol("State-WAIT_AFTER_LINE")
	};
	
	/**
	 * The main menu.
	 * @param {Lunar.Game} game The context.
	 * @param {PIXI.Sprite} Cursor sprite.
	 * @param {number} Maximum number of lines shown at once.
	 */
	Lunar.Scene.BattleText = class extends Lunar.Scene.Base {
		constructor(game, cursor, lines = 3) {
			super(game);
			this._lineCount = lines;
			this._cursorX = 0;
			this._cursorY = 0;
			this._positionLine = 0;
			this._positionSentence = 0;
			this._activeSentence = [];
			this._state = State.IDLE;
			this._sentences = [];
			this._displayedLine = -1;
			this._waitAfterLine = 1.000;
			this._waitAfterCharacter = 1/30;
			this._wantsAdvance = false;
			this._sfxCharHowler = undefined;
			this._sfxCharId = undefined;
			this._timeout = 0;
		}
		
		set waitAfterLine(seconds) {
			this._waitAfterSentence = seconds < 0 ? 0 : seconds;
		}

		/**
		 * Time in seconds to wait after a line
		 * before proceeding to the next line.
		 */
		get waitAfterLine() {
			return this._waitAfterSentence;
		}
		
		set waitAfterCharacter(seconds) {
			this._waitAfterCharacter = seconds < 0 ? 0 : seconds;
		}
		
		/**
		 * Time in seconds to wait after a character
		 * before proceeding to the next character.
		 */
		get waitAfterCharacter() {
			return this._waitAfterCharacter;
		}
		
		destroy() {
			this._loadScene = undefined
			this._sfxCharHowler = undefined;
			this._sfxCharId = undefined;			
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
		}
		
		onRemove() {
			if (this._sfxCharHowler) {
				this._sfxCharHowler.stop(this._sfxCharId);
				this._sfxCharHowler = undefined;
				this._sfxCharId = undefined;
			}
			super.onRemove();
		}

		update(delta) {
			super.update(delta);
			this._timeout -= delta;
			this._updateCursor(delta);
			switch (this._state) {
			// Simple state machine.
			case State.IDLE:
				this._stateIdle();
				break;
			case State.SETTLING:
				this._stateSettling();
				break;
			case State.PRINT_CHARACTERS:
				this._statePrintCharacters();
				break;
			case State.WAIT_AFTER_LINE:
				this._stateWaitAfterLine();
				break;
			default:
				console.error("unknown state", this._state);
			this._switchState(State.IDLE);
			}
		}
		
		get boxHeight() {
			return this._lineCount * this.game.dy(0.07);
		}

		layout() {
			super.layout();
			const h = this.hierarchy;
			
			const height = this.boxHeight;
			h.$textbox.position.set(this.game.w*0.2, this.game.h - height);
			h.$textbox.width = this.game.w * 0.8;
			h.$textbox.height = height;
			
			const boxBody = h.$textbox.bodyDimension;
			
			const geoLines = Lunar.Geometry.layoutVbox({
				box: boxBody,
				dimension: this._lineCount,
				padding: {
					left: 0.03,
					right: 0.03,
					top: 0.03,
					bottom: 0.03
				},
				relative: true
			});
			
			for (let i = this._lineCount; i-->0;)
				this.geo(h.textbox.lines[i].$text, geoLines[i], {keepSize: true, anchor: [0, 0.5]});
			
			Lunar.Geometry.proportionalScale(this.hierarchy.textbox.$cursor, boxBody.w * 0.06, boxBody.h * 0.30);
			this._cursorX = boxBody.x+boxBody.w - 48;
			this._cursorY = boxBody.y+boxBody.h - 48;
		}
		
		clearText() {
			this._positionLine = 0;
			this._positionSentence = 0;
			this._activeSentence = [];
			this._sentences = [];
			this._displayedLine = -1;
			this._wantsAdvance = false;
			if (this._sfxCharHowler) {
				this._sfxCharHowler.stop(this._sfxCharId);
				this._sfxCharHowler = undefined;
				this._sfxCharId = undefined;
			}
			this._switchState(State.IDLE);
			for (let line of this.hierarchy.textbox.lines)
				line.$text.text = '';
		}

		pushText(text) {
			if (text.length === 0)
				return;
			const sentences = this._splitTextIntoSentences(text);
			if (sentences.length === 0)
				return;
			sentences.forEach(s => this._sentences.push(s));
		}
		
		_updateCursor(delta) {
			const cursor = this.hierarchy.textbox.$cursor;
			cursor.rotation += delta * 6;
			cursor.position.set(this._cursorX, this._cursorY + this.game.fmath.sin(this.time*4)*this.game.dy(0.0075));
		}
		
		_statePrintCharacters() {
			const activeLine = this._activeSentence[this._positionSentence];
			// Are we done printing the line?
			if (this._positionLine >= activeLine.length || this._wantsAdvance) {
				this._wantsAdvance = false;
				this._setActiveLineText(activeLine);
				this._switchState(State.WAIT_AFTER_LINE, this._waitAfterLine);
				this._sfxCharHowler.stop(this._sfxCharId);
				this._sfxCharHowler = undefined;
				this._sfxCharId = undefined;
				return;
			}
			if (!this._sfxCharHowler) {
				this._sfxCharHowler = this.game.sfx('resources/translune/static/battle/textbox', {volume: 0.60, play: false});
				this._sfxCharHowler._sprite.loop = [21,120, true];
				this._sfxCharId = this._sfxCharHowler.play("loop");
			}
			while (this._timeout <= 0) {
				// Print next character
				this._setActiveLineText(activeLine.substr(0, ++this._positionLine));
				this._timeout += this._waitAfterCharacter;
			}
		}	
		
		_stateWaitAfterLine() {
			// Are we done processing all line from the current sentence?
			if (this._positionSentence >= this._activeSentence.length - 1) {
				this._wantsAdvance = false;
				this._switchState(State.SETTLING);
				return;
			}
			if (this._timeout <= 0) {
				// Done waiting, proceed to print the next line.
				this._switchState(State.PRINT_CHARACTERS);
				this._positionSentence += 1;
				this._positionLine = 0;
				this._nextDisplayLine();
			}
		}

		_stateSettling() {
			// Are there any more sentences waiting to be printed?
			if (this._sentences.length > 0) {
				if (this._wantsAdvance || this._displayedLine < 0) {
					this._wantsAdvance = false;
					this.hierarchy.textbox.$cursor.visible = false;
					this._prepareNextSentence();
					this.emit('text-advance');
					this._switchState(State.PRINT_CHARACTERS);
				}
				else {
					this.hierarchy.textbox.$cursor.visible = true;
				}
			}
			else {
				this._switchState(State.IDLE);
				this.emit('text-processed');
			}
		}
		
		_stateIdle() {
			if (this._sentences.length > 0) {
				this._switchState(State.SETTLING);
			}
		}
		
		_prepareNextSentence() {
			this._activeSentence = this._sentences.shift();
			this._positionLine = 0;
			this._positionSentence = 0;
			this._switchState(State.PRINT_CHARACTERS);
			this._nextDisplayLine();
		}
		
		_switchState(state, timeout = 0) {
			this._state = state;
			this._timeout = timeout;
		}
		
		_nextDisplayLine() {
			// If no empty lines are left, remove the oldest line at the top.
			const maxLines = this.hierarchy.textbox.lines.length;
			if (this._displayedLine >= maxLines - 1) {
				this._shiftUpText();
			}
			this._displayedLine += 1;
		}
		
		_shiftUpText() {
			const lines = this.hierarchy.textbox.lines;
			for (let i = 1; i < lines.length;++i)
				lines[i-1].$text.text = lines[i].$text.text;
			lines[lines.length-1].$text.text = '';
			this._displayedLine -= 1;
		}
		
		_setActiveLineText(text) {
			this.hierarchy.textbox.lines[this._displayedLine].$text.text = text; 			
		}
		
		/**
		 * @param {string} text Text to split across multiple lines.
		 */
		_splitTextIntoSentences(text) {
			// Split into sentences and trim spaces.
			return (text.match(/[^.!?]+[.!?]+/g)||[text])
				.map(s => s.trim())
				// Split sentences so that the maximum line length is not exceeded.
				.map(sentence => {
					const lines = [];
					const line = [];
					sentence.split(/\s+/).forEach(word => {
						// Does this word fit on the current line?
						if (this._fitsLine(line, word))
							line.push(word);
						// We break the sentence here.
						else {
							lines.push(line.join(' '))
							// Check if the next word by itself fits the line.
							if (this._fitsLine([], word)) {
								line.length = 0;
								line.push(word);							
							}
							// Words does not fit on a line, interword break
							else {
								line.length = 0;
								line.push(this._fitSingleWord(lines, word));
							}
						}
					});
					if (line.length > 0)
						lines.push(line.join(' '));
					return lines;
				});
		}
		
		/**
		 * @param {array<string>} lines Array to which new lines are added, 
		 * @param {string} word Long words that need to be split.
		 */
		_fitSingleWord(lines, word) {
			let index = 0;
			do {
				let part = word.substr(index, LINE_THRESHOLD-1);
				index += LINE_THRESHOLD - 1;
				if (index >= word.length)
					return part;
				part += '-';	
				lines.push(part);
			} while (true);
		}
		
		/**
		 * @param {array<string>} line
		 * @param {string} word
		 */
		_fitsLine(words, lastWord) {
			// Make sure to account for the space character between words.
			return words.reduce((sum,word) => sum + word.length + 1, 0) + lastWord.length <= LINE_THRESHOLD;
		}
		
		/**
		 * @private
		 */
		_initScene() {
			const textbox = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const lines = [];
			const cursor = new PIXI.extras.AnimatedSprite(Object.values(this.game.loaderFor('battle').resources.cursor.spritesheet.textures));
			
			cursor.visible = false;
			cursor.anchor.set(0.5,0.5);
			cursor.alpha = 0.5;
			cursor.play();
			
			textbox.interactive = true;
			textbox.buttonMode = true;
			textbox.on('pointertap', () => {
				if (this._sentences.length > 0 || this._state === State.PRINT_CHARACTERS)
					this.game.sfx('resources/translune/static/ping');
				else
					this.game.sfx('resources/translune/static/unable');
				this._wantsAdvance = true;
			}, this);
			
			this.view.addChild(textbox);
			textbox.body.addChild(cursor);
			for (let i = 0; i < this._lineCount; ++i) {
				const text = new PIXI.Text('', Lunar.FontStyle.battleTextbox);
				lines.push({$text: text});
				textbox.body.addChild(text);
			}
			
			this.hierarchy = {
				textbox: {
					lines: lines, // { $text: PIXI.Text}
					$cursor: cursor
				},
				$textbox: textbox
			};
		}
	}
})(window.Lunar, window);