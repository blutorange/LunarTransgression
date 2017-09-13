(function(Lunar, window, undefined) {
	const LIST_X = 2;
	const LIST_Y = 6;
	
	Lunar.Scene.SkillSelect = class extends Lunar.Scene.Base {
		constructor(game, {skills, level, selectText = 'Select', selectRequired = true}) {
			super(game);
			this._skills = [];
			this._selectedSkill = undefined;
			this._textSelect = selectText;
			this._selectRequired = selectRequired;
			for (let key of Object.keys(skills)) {
				if (key <= level) {
					const skillsForLevel = skills[key]
						.sort((s1,s2) => s1.name < s2.name ? -1 : s1.name == s2.name ? 0 : 1);
					for (let skill of skillsForLevel) {
						this._skills.push({
							level: key,
							skill: skill
						})
					}
				}
			}
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
			this._selectSkill(null);
			this.game.pushScene(this._scenePager, this.hierarchy.frame.page.$pager);
		}
		
		destroy() {
			this._scenePager = undefined;
			this._skills = undefined;
			this._selectedSkill = undefined;
			super.destroy();
		}
		
		onRemove() {
			if (this._scenePager)
				this.game.removeScene(this._scenePager);
			this._scenePager = undefined;
			super.onRemove();
		}
		
		get selectedSkill() {
			return this._selectedSkill;
		}
		
		layout() {
			super.layout();		
			const h = this.hierarchy;

			// Base frame and division into list and details
			const geoRoot = Lunar.Geometry.layoutHbox({
				box: {w: this.game.w, h: this.game.h},
				padding: {
					top:0.07,
					left: 0.07,
					right: 0.07,
					bottom: 0.07
				},
				relative: true
			});
			
			this.geo(h.$frame, geoRoot[0]);
			
			const geoFrame = Lunar.Geometry.layoutHbox({
				box: h.$frame.bodyDimension,
				dimension: [1,1],
				padding: {
					top: 0.05,
					left: 4,
					right: 4,
					bottom: 0.05,
					x: 10
				},
				relative: true
			});
			
			// Skill list
			const geoPage = Lunar.Geometry.layoutVbox({
				box: geoFrame[0],
				dimension: [8,2],
				relative: true
			});
			
			const geoList = Lunar.Geometry.layoutGrid({
				box: geoPage[0],
				dimension: {
					n: LIST_X,
					m: LIST_Y
				},
				relative: true
			}).flatten();
			
			// Skill details
			const geoSkills = Lunar.Geometry.layoutVbox({
				box: geoFrame[1],
				dimension: [10,30,45,15],
				padding: {
					y: 0.04
				},
				relative: true
			});
			
			const geoTopbar = Lunar.Geometry.layoutHbox({
				box: geoSkills[0],
				dimension: [1,5,1],
				relative: true
			});

			const geoStat = Lunar.Geometry.layoutGrid({
				box: geoSkills[1],
				dimension: {
					n: 2,
					m: 3,
				},
				relative: true
			});
									
			// Refresh overlay so that it fills the entire screen.
			h.$overlay.clear();
			h.$overlay.beginFill(0x222222, 0.85);
			h.$overlay.drawRect(0, 0, this.game.w, this.game.h);
			h.$overlay.endFill();			
			
			// Apply base
			this.geo(h.frame.$page, geoFrame[0]);
			this.geo(h.frame.$skills, geoFrame[1]);
			
			// Apply skill list
			this.geo(h.frame.page.$list, geoPage[0])
			this.geo(h.frame.page.$pager, geoPage[1]);
			h.frame.page.list.forEach((element, index) => this.geo(element.$text, geoList[index], {keepSize: true, anchor: 0.5}));
			
			// Apply skill details
			this.geo(h.frame.skills.$topbar, geoSkills[0]);
			this.geo(h.frame.skills.$stat, geoSkills[1]);
			this.geo(h.frame.skills.$description, geoSkills[2], {keepSize: true, anchor: [0.5,0]});
			this.geo(h.frame.skills.$select, geoSkills[3]);
			
			this.geo(h.frame.skills.topbar.$physical, geoTopbar[0], {proportional: true, anchor: 1});
			this.geo(h.frame.skills.topbar.$magical, geoTopbar[0], {proportional: true, anchor: 1});
			this.geo(h.frame.skills.topbar.$title, geoTopbar[1], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.skills.topbar.$exit, geoTopbar[2], {proportional: true, anchor: 0.5});
			
			this.geo(h.frame.skills.stat.$power, geoStat[0][0], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.skills.stat.$accuracy, geoStat[0][1], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.skills.stat.$condition, geoStat[1][0], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.skills.stat.$conditionChance, geoStat[1][1], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.skills.stat.$element, geoStat[2][0], {keepSize: true, anchor: 0.5});
			this.geo(h.frame.skills.stat.$mp, geoStat[2][1], {keepSize: true, anchor: 0.5});
			
			this.layoutButtonText(h.frame.skills.select.$text, true);
		}
		
		/**
		 * @private
		 */
		_initScene() {
			const _this = this;
			
			// Overlay to block other UI elements.
			const overlay = new PIXI.Graphics();
			overlay.interactive = true;

			// Elements
			const frame = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const iconExit = this._createIconExit();
			const iconSword = new PIXI.Sprite(this.game.baseLoader.resources.packed.spritesheet.textures['sword.png']);
			const iconWand = new PIXI.Sprite(this.game.baseLoader.resources.packed.spritesheet.textures['wand.png']);
			const textTitle = new PIXI.Text("", Lunar.FontStyle.skillTitle);
			const textPower = new PIXI.Text("", Lunar.FontStyle.skillStat);
			const textAccuracy = new PIXI.Text("", Lunar.FontStyle.skillStat);
			const textCondition = new PIXI.Text("", Lunar.FontStyle.skillStat);
			const textElement = new PIXI.Text("", Lunar.FontStyle.skillStat);
			const textConditionChance = new PIXI.Text("", Lunar.FontStyle.skillStat);
			const textMp = new PIXI.Text("", Lunar.FontStyle.skillStat);
			const textDescription = new PIXI.Text("", Lunar.FontStyle.skillDesc);
			const buttonSelect = new PIXI.NinePatch(this.game.baseLoader.resources.textbox);
			const textSelect = this.createButtonText(buttonSelect, this._textSelect);
			
			this._scenePager = new Lunar.Scene.Pager(this.game, {
				style: Lunar.FontStyle.control,
				prev: this.game.baseLoader.resources.packed.textures['prev.png'],
				next: this.game.baseLoader.resources.packed.textures['next.png'],
				pageCount: this._skills.length / (LIST_X*LIST_Y)
			});
			
			buttonSelect.on('pointertap', this._onClickSelect, this);
			this._scenePager.on('page-change', this._onPageChange, this);
						
			// Container
			const containerPage = new PIXI.ClipContainer();
			const containerSkills = new PIXI.ClipContainer();
			const containerTopbar = new PIXI.ClipContainer();
			const containerStat = new PIXI.ClipContainer();		
			const containerList = new PIXI.ClipContainer();	
			const containerPager = new PIXI.ClipContainer();	
					
			// Root
			this.view.addChild(overlay);
			this.view.addChild(frame);
			frame.body.addChild(containerPage);
			frame.body.addChild(containerSkills);
			
			// Skills
			containerSkills.addChild(containerTopbar);
			containerSkills.addChild(containerStat);
			containerSkills.addChild(textDescription);
			containerSkills.addChild(buttonSelect);
			containerTopbar.addChild(iconSword);
			containerTopbar.addChild(iconWand);
			containerTopbar.addChild(textTitle);
			containerTopbar.addChild(iconExit);
			containerStat.addChild(textPower);
			containerStat.addChild(textAccuracy);
			containerStat.addChild(textCondition);
			containerStat.addChild(textConditionChance);
			containerStat.addChild(textElement);
			containerStat.addChild(textMp);
			buttonSelect.body.addChild(textSelect);
			
			// Details
			containerPage.addChild(containerList);
			containerPage.addChild(containerPager);
			const textSkills = [];
			for (let j = 0; j < LIST_Y; ++j) {
				for (let i = 0; i < LIST_X; ++i) {
					const textSkill = this._createTextSkill(() => this._onClickSkill(j*LIST_X+i));
					textSkills.push({
						$text: textSkill
					});
					containerList.addChild(textSkill);
				}
			}
			
			this.hierarchy = {
				frame: {
					page: {
						list: textSkills, // [{$text: text}, ...]
						$list: containerList,
						$pager: containerPager 
					},
					skills: {
						topbar: {
							$physical: iconSword,
							$magical: iconWand,
							$title: textTitle,
							$exit: iconExit
						},
						stat: {
							$power: textPower,
							$accuracy: textAccuracy,
							$condition: textCondition,
							$conditionChance: textConditionChance,
							$element: textElement,
							$mp: textMp,
						},
						select: {
							$text: textSelect
						},
						$topbar: containerTopbar,
						$stat: containerStat,
						$description: textDescription,
						$select: buttonSelect
					},
					$page: containerPage,
					$skills: containerSkills
				},
				$overlay: overlay,
				$frame: frame
			};
		}
		
		_createIconExit() {
			const exit = new PIXI.Sprite(this.game.baseLoader.resources.packed.spritesheet.textures['close.png'])
			exit.interactive = true;
			exit.buttonMode = true;
			exit.on('pointerover', () => {
				exit.width = exit.width*9/8;
				exit.height = exit.height*9/8;
				exit.rotation = 0.2;
			});
			exit.on('pointerout', () => {
				exit.width = exit.width*8/9;
				exit.height = exit.height*8/9;
				exit.rotation = 0.0;
			});
			exit.on('pointertap', this._onClickExit, this);
			return exit;
		}
		
		_createTextSkill(callback) {
			const text = new PIXI.Text("", Lunar.FontStyle.skillList);
			text.interactive = true;
			text.buttonMode = true;
			text.on('pointerover', () => {
				text.width = text.width * 6/5,
				text.height = text.height * 6/5
			});
			text.on('pointerout', () => {
				text.width = text.width * 5/6,
				text.height = text.height * 5/6
			});
			text.on('pointertap', callback, this);
			return text;
		}
		
		_getPagedSkill(index, pager = undefined) {
			return this._skills[index + (pager||this._scenePager).page*LIST_X*LIST_Y];
		}
		
		_onPageChange(pager) {
			// Update skill list.
			const list = this.hierarchy.frame.page.list;
			for (let viewIndex = 0; viewIndex < list.length; ++viewIndex) {
				const skill = this._getPagedSkill(viewIndex, pager);
				const text = list[viewIndex].$text; 
				if (skill) {
					text.text = skill.skill.name;
					text.style = Lunar.FontStyle[`skillStat${skill.skill.element}`] || text.style;
					text.visible = true;
				}
				else {
					text.visible = false;
				}
			}
			this.layout();
		}
		
		_onClickSelect() {
			if (!this.selectedSkill && this._selectRequired) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/confirm');
			this.emit('skill-select', this);
		}
		
		_onClickExit() {
			this.game.sfx('resources/translune/static/cancel');
			this.emit('skill-exit', this);
		}
		
		_onClickSkill(index) {
			if (!this._scenePager) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/buttonclick');
			const skill = this._getPagedSkill(index);
			this._selectSkill(skill);
		}
		
		_selectSkill(data) {
			const s = this.hierarchy.frame.skills;
			if (!data) {
				// Deselect
				s.topbar.$title.visible = false;
				s.topbar.$physical.visible = false;
				s.topbar.$magical.visible = false;
				s.$stat.visible = false;
				s.$description.text = 'No skill selected.';
				this._selectedSkill = undefined;
				return;
			}
			const skill = data.skill;
			if (!this._selectedSkill) {
				s.topbar.$title.visible = true;
				s.topbar.$physical.visible = true;
				s.topbar.$magical.visible = true;
				s.$stat.visible = true;
			}
			s.topbar.$title.text = skill.name;
			s.topbar.$physical.visible = skill.isPhysical && skill.attackPower > 0;
			s.topbar.$magical.visible = !skill.isPhysical;
			s.stat.$power.text = skill.attackPower > 0 ? `${skill.attackPower} P` : '-';
			s.stat.$accuracy.text = `${skill.accuracy} %`;
			s.stat.$condition.text = skill.condition || '-';
			s.stat.$conditionChance.text = skill.condition ? `${skill.conditionChance} %` : '-';
			s.stat.$element.text = skill.element;
			s.stat.$mp.text = `${skill.mp} MP`;
			s.$description.text = skill.description;
			
			s.stat.$element.style = Lunar.FontStyle[`skillList${skill.element}`] || s.stat.$element.style;
			this._selectedSkill = skill;
		}
	};
})(window.Lunar, window);