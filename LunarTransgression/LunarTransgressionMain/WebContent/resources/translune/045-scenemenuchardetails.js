(function(Lunar, window, undefined) {
	Lunar.Scene.MenuCharDetails = class extends Lunar.Scene.Base {
		constructor(game, menu, characterState) {
			super(game);
			this._dialog = undefined;
			this._menu = menu;
			this._characterState = characterState;
			this._releasable = menu.player.characterStates.length > 4 && characterState.level >= Lunar.Constants.minReleaseLevel;
		}

		onAdd() {
			this._initScene();
			super.onAdd();
			this._dynaLoad();
		}
		
		destroy() {
			this._dialog = undefined;
			this._menu = undefined;
			this._characterState = undefined;
			this._sceneSkill = undefined;
			this.game.loaderFor('menu-detail').reset();
			super.destroy();
		}
		
		onRemove() {
			this.game.removeScene(this._sceneSkill);
			this.game.removeScene(this._dialog);
			super.onRemove();
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			const panel = this.view.parent.parent; 
			const geoRoot = Lunar.Geometry.layoutVbox({
				box: {w: panel.bodyWidth, h: panel.bodyHeight},
				dimension: [2,3,1],
				padding: {
					y: 0.02
				},
				relative: true
			});
			const geoHead = Lunar.Geometry.layoutHbox({
				box: geoRoot[0],
				dimension: [2,3],
				relative: true
			});
			const geoControl = Lunar.Geometry.layoutHbox({
				box: geoRoot[2],
				relative: true
			});
			const geoButton = Lunar.Geometry.layoutHbox({
				box: geoControl[0],
				padding: {left: 0.1, right: 0.1, top: 0.1, bottom: 0.1},
				relative: true
			});
			const geoSprite = Lunar.Geometry.layoutHbox({
				box: geoHead[0],
				relative: true
			});			
			const geoBase = Lunar.Geometry.layoutVbox({
				box: geoHead[1],
				dimension: 2,
				padding: {
					top: 0.2,
					bottom: 0.1
				},				
				relative: true
			});
			const geoTitle = Lunar.Geometry.layoutHbox({
				box: geoBase[0],
				dimension: [1,4],
				padding: {
					x: 16
				},
				relative: true
			});
			const geoElement = Lunar.Geometry.layoutHbox({
				box: geoBase[1],
				dimension: 2,
				relative: true
			});
			const geoGrid = Lunar.Geometry.layoutGrid({
				box: geoRoot[1],
				padding: {
					left: 0.05,
					right: 0.05,
					x: 0.2
				},
				dimension: {
					n: [1,2,1,2],
					m: 5,
					merge: [
						{
							i: 0,
							j: 0,
							columns: 1						
						},
						{
							i: 2,
							j: 0,
							rows: 1
						}
					]
				},
				
				relative: true
			});	
			
			this.geo(h.$head, geoRoot[0], {keepSize: true});
			this.geo(h.$stat, geoRoot[1], {keepSize: true});
			this.geo(h.$control, geoRoot[2], {keepSize: true});
			
			this.geo(h.head.$sprite, geoHead[0], {keepSize: true});
			this.geo(h.head.$base, geoHead[1], {keepSize: true});
			
			this.geo(h.head.base.$title, geoBase[0], {keepSize: true});
			this.geo(h.head.base.$element, geoBase[1], {keepSize: true});
			
			this.geo(h.head.base.title.$level, geoTitle[0], {keepSize: true});
			this.geo(h.head.base.title.$name, geoTitle[1], {keepSize: true});
			
			this.geo(h.head.base.element.$one, geoElement[0], {keepSize: true});
			this.geo(h.head.base.element.$two, geoElement[1], {keepSize: true});
			
			this.geo(h.control.$button, geoButton[0]);
			this.layoutButtonText(h.control.button.$text, true);
			
			if (h.head.sprite)
				this.geo(h.head.sprite.$sprite, geoSprite[0], {proportional: true, anchor: 0.5})
			
			geoGrid.flatten().forEach(geo => {
				const element = h.stat.grid[geo.j][geo.i];
				if (element)
					this.geo(element, geo, {keepSize: true, anchor: 0.0});
			});
		}
		
		/**
		 * @private
		 */
		_initScene() {
			const _this = this;
			const panel = this.view.parent.parent;
			const cs = this._characterState;
			const c = cs.character;
			const ms = cs.computedStatus;
			
			const style = Lunar.FontStyle.stat;
			const styleValue = Lunar.FontStyle.statValue;

			const level = new PIXI.Text(`Lv ${cs.level}`, style);
			const name = new PIXI.Text(c.name, style);
			const element1 = new PIXI.Text(c.elements[0], Lunar.FontStyle[`stat${c.elements[0]}`] || style);
			const element2 = new PIXI.Text(c.elements[1], Lunar.FontStyle[`stat${c.elements[1]}`] || style);

			const nickname = new PIXI.Text(`»${cs.nickname}«`, style);
			const nature = new PIXI.Text(cs.nature, style);
			
			const labelHp = new PIXI.Text("HP", style);
			const labelMp = new PIXI.Text("MP", style);
			const labelPAtt = new PIXI.Text("P.Att.", style);
			const labelMAtt = new PIXI.Text("M. Att", style);
			const labelPDef = new PIXI.Text("P. Def", style);
			const labelMDef = new PIXI.Text("M. Def", style);
			const labelSpeed = new PIXI.Text("Speed", style);
			const labelExp = new PIXI.Text("EXP", style);
			
			const valueHp = new PIXI.Text(this.fmtStat(ms.computedMaxHp), styleValue);
			const valueMp = new PIXI.Text(this.fmtStat(ms.computedMaxMp), styleValue);
			const valuePAtt = new PIXI.Text(this.fmtStat(ms.computedPhysicalAttack), styleValue);
			const valuePDef = new PIXI.Text(this.fmtStat(ms.computedPhysicalDefense), styleValue);
			const valueMAtt = new PIXI.Text(this.fmtStat(ms.computedMagicalAttack), styleValue);
			const valueMDef = new PIXI.Text(this.fmtStat(ms.computedMagicalDefense), styleValue);
			const valueSpeed = new PIXI.Text(this.fmtStat(ms.computedSpeed), styleValue);
			const valueExp = new PIXI.Text(cs.exp, styleValue);
			
			const buttonSkills = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const buttonTextSkills = this.createButtonText(buttonSkills, "Moves", Lunar.FontStyle.control, Lunar.FontStyle.controlActive);
			
			const containerRoot = new PIXI.ClipContainer();
			const containerHead = new PIXI.ClipContainer();
			const containerStat = new PIXI.ClipContainer();
			const containerSprite = new PIXI.ClipContainer();
			const containerControl = new PIXI.ClipContainer();
			const containerBase = new PIXI.ClipContainer();
			const containerTitle = new PIXI.ClipContainer();
			const containerElement = new PIXI.ClipContainer();
						
			this.view.addChild(containerHead);
			this.view.addChild(containerStat);
			this.view.addChild(containerControl);
			
			containerHead.addChild(containerSprite);
			containerHead.addChild(containerBase);
			
			containerBase.addChild(containerTitle);
			containerBase.addChild(containerElement);
			
			containerTitle.addChild(level);
			containerTitle.addChild(name);
			
			containerElement.addChild(element1);
			containerElement.addChild(element2);
			
			containerControl.addChild(buttonSkills);
			buttonSkills.body.addChild(buttonTextSkills);
			
			containerStat.addChild(nickname);
			containerStat.addChild(nature);
			containerStat.addChild(labelHp);
			containerStat.addChild(valueHp);
			containerStat.addChild(labelMp);
			containerStat.addChild(valueMp);
			containerStat.addChild(labelPAtt);
			containerStat.addChild(valuePAtt);
			containerStat.addChild(labelMAtt);
			containerStat.addChild(valueMAtt);
			containerStat.addChild(labelPDef);
			containerStat.addChild(valuePDef);
			containerStat.addChild(labelMDef);
			containerStat.addChild(valueMDef);
			containerStat.addChild(labelSpeed);
			containerStat.addChild(valueSpeed);
			containerStat.addChild(labelExp);
			containerStat.addChild(valueExp);
			
			nickname.interactive = true;
			nickname.buttonMode = true;
			nickname.on('pointerdown', this._onClickNickname, this);
			
			buttonSkills.on('pointerdown', this._onClickSkills, this);
			
			if (this._releasable) {
				this.menu.actionButton1.visible = true;
				this.menu.actionButton1.body.children[0].text = 'Release';
				this.menu.actionButton1.on('pointertap', this._onClickRelease, this);
			}
			
			this.hierarchy = {
				head: {
					base: {
						title: {
							$level: level,
							$name: name
						},
						element: {
							$one: element1,
							$two: element2,
						},
						$title: containerTitle,
						$element: containerElement
					},
					$sprite: containerSprite,
					$base: containerBase
				},
				stat: {
					grid: [
						[nickname, null, nature, null],
						[labelHp, valueHp, labelMp, valueMp],
						[labelPAtt, valuePAtt, labelMAtt, valueMAtt],
						[labelPDef, valuePDef, labelMDef, valueMDef],
						[labelSpeed, valueSpeed, labelExp, valueExp]
					],
					$nickname: nickname,
					$nature: nature,
				},
				control: {
					$button: buttonSkills,
					button: {
						$text: buttonTextSkills
					}
				},
				$head: containerHead,
				$stat: containerStat,
				$control: containerControl
			};
		}
		
		get menu() {
			return this._menu;
		}
		
		get releasable() {
			return this._releaseable;
		}
		
		/**
		 * @private
		 */
		_onClickSkills() {
			if (this._sceneSkill) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/buttonclick');
			this._sceneSkill = new Lunar.Scene.SkillSelect(this.game, this._characterState, 'Close', false);
			this._sceneSkill.on('skill-select', scene => {
				this._sceneSkill = undefined;
				this.game.removeScene(scene);
			}, this);
			this._sceneSkill.on('skill-exit', scene => {
				this._sceneSkill = undefined;
				this.game.removeScene(scene);
			}, this);
			this.game.pushScene(this._sceneSkill);
		}
		
		_onClickRelease() {
			const _this = this;
			if (this._dialog)
				return;
			this.game.sfx('resources/translune/static/ping');
			this._dialog = new Lunar.Scene.Dialog(this.game, {
				message: `Release ${_this._characterState.nickname} into the wild? You cannot make ${_this._characterState.character.name} battle anymore!`,
				choices: [
					{
						text: 'Keep',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/cancel');
							_this._dialog = undefined;
							dialog.close();
						},
					},
					{
						text: 'Release',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/confirm');
							_this._dialog = undefined;
							dialog.close();
							_this._performRelease();							
						}
					}
				]
			});
			this.game.pushScene(this._dialog);
		}
		
		_performRelease() {
			// TODO implement me
			console.error("TODO - not yet implemented!")
		}
		
		/**
		 * @private
		 */
		_onClickNickname() {
			const _this = this;
			if (this._dialog)
				return;
			this.game.sfx('resources/translune/static/ping');
			this._dialog = new Lunar.Scene.Dialog(this.game, {
				message: `Give ${this._characterState.nickname} a new name`,
				prompt: {
					initial: this._characterState.nickname,
					style: Lunar.FontStyle.input,
					placeholder: 'nickname...',
					setup: input => {
						input.minlength = 1;
						input.maxlength = 24;
					}
				},
				choices: [
					{
						text: 'Keep old name',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/cancel');
							dialog.close();
							_this._dialog = undefined
						}
					},
					{
						text: 'Change name',
						callback: dialog => {
							_this.game.sfx('resources/translune/static/confirm');
							_this._changeNickname(dialog.prompt.text);
							_this._dialog = undefined;
							dialog.close();
						},
					}
				]
			});
			this.game.pushScene(this._dialog);
		}
		
		/**
		 * @private
		 */
		_changeNickname(newNickname) {
			const _this = this;
			if (newNickname === this._characterState.nickname || !newNickname)
				return;
			this.game.net.dispatchMessage(Lunar.Message.updateData, {
				update: Lunar.UpdateType.characterNickname,
				details: JSON.stringify({
					id: this._characterState.id,
					nickname: newNickname
				})
			}).then(response => {
				_this._characterState.nickname = response.data.data;
				_this.hierarchy.stat.$nickname.text = `»${_this._characterState.nickname}«`;
			}).catch(() => {
				_this.showConfirmDialog("Could not change the nickname, please try again later.");
			});
		}
		
		/**
		 * @private
		 */
		_dynaLoad() {
			const _this = this;
			const loader = this.game.loaderFor('menu-detail');
			loader.reset();
			loader.add("sprite",`/resource/${_this._characterState.character.imgFront}`).load((_, resources) => {
				const sprite = new PIXI.extras.AnimatedSprite(Object.values(resources.sprite.spritesheet.textures));
				_this.hierarchy.head.sprite = {
					$sprite: sprite
				};
				_this.hierarchy.head.$sprite.addChild(sprite);
				sprite.play();
				_this.layout();
			});
		}
		
		/** @private */
		fmtStat(stat) {
			return Lunar.String.rjust(stat, '0', 3);
		}
	};
})(window.Lunar, window);