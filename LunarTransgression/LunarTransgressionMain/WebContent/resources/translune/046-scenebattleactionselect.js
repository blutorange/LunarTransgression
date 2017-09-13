/**
 * Fades out the opponent cube.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {	
	
	const ALPHA_ACTION = 0.85;
	const OUTLINE = 1.15;
	
	Lunar.Scene.BattleActionSelect = class extends Lunar.Scene.Base {
		constructor(battle, battler) {
			super(battle.game);
			this._battle = battle;
			this._battler = battler;
			this._skillSelect = undefined;
			this._filterAttack = new PIXI.filters.ColorMatrixFilter();
			this._filterSkill = new PIXI.filters.ColorMatrixFilter();
			this._filterItem = new PIXI.filters.ColorMatrixFilter();
			this._filterSpecial = new PIXI.filters.ColorMatrixFilter();
		}
		
		destroy() {
			this._battler = undefined;
			this._skillSelect = undefined;
			this._filterAttack = undefined;
			this._filterSkill = undefined;
			this._filterItem = undefined;
			this._filterSpecial = undefined;
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
			this._battle.textScene.clearText();
			this._battle.textScene.pushText(`What will ${this._battler.characterState.nickname} do?&`);
		}

		update(delta) {
			super.update(delta);
			const alpha1 = 0.05*this.game.fmath.sin(this.time*7.3);
			const alpha2 = 0.1*this.game.fmath.sin(this.time*7);
			const alpha3 = 0.05*this.game.fmath.sin(this.time*7.29);
			const alpha4 = 0.1*this.game.fmath.sin(this.time*6.99);
			
			this._updateIcon(this.hierarchy.$actionAttack, this.hierarchy.$actionAttackOutline, this._scaleAttack, this._scaleAttackOutline, alpha1, alpha2, alpha3, alpha4);
			this._updateIcon(this.hierarchy.$actionSkill, this.hierarchy.$actionSkillOutline, this._scaleSkill, this._scaleSkillOutline, alpha1, alpha2, alpha3, alpha4);
			this._updateIcon(this.hierarchy.$actionItem, this.hierarchy.$actionItemOutline, this._scaleItem, this._scaleItemOutline, alpha1, alpha2, alpha3, alpha4);
			this._updateIcon(this.hierarchy.$actionSpecial, this.hierarchy.$actionSpecialOutline, this._scaleSpecial, this._scaleSpecialOutline, alpha1, alpha2, alpha3, alpha4);
		}
		
		_updateIcon(icon, outline, scaleIcon, scaleOutline, alpha1, alpha2, alpha3, alpha4) {
			outline.scale.set(scaleOutline+alpha1);
			outline.skew.set(0.1*alpha2);
			icon.scale.set(scaleIcon+alpha3);
			icon.skew.set(0.3*alpha4);
		}
		
		onRemove() {
			this.game.removeScene(this._skillSelect);
			this._battle = undefined;
			this._battler = undefined;
			super.onRemove();
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			
			const geoAction = Lunar.Geometry.layoutGrid({
				box: {
					x: 0,
					y: 0,
					w: this.game.w,
					h: this.game.h - this._battle.textScene.hierarchy.$textbox.height
				},
				dimension: {
					n: 2,
					m: 2
				},
				padding: {
					x: 0.05,
					y: 0.05,
					top: 0.05,
					right: 0.05,
					bottom: 0.05,
					left: 0.05
				},
				relative: true
			});
			
			this.geo(h.$actionAttack, geoAction[0][0], {proportional: true, anchor: 0.5});
			this.geo(h.$actionSkill, geoAction[0][1], {proportional: true, anchor: 0.5});
			this.geo(h.$actionItem, geoAction[1][0], {proportional: true, anchor: 0.5});
			this.geo(h.$actionSpecial, geoAction[1][1], {proportional: true, anchor: 0.5});
			
			this.geo(h.$actionAttackOutline, geoAction[0][0], {proportional: true, anchor: 0.5, scale: OUTLINE});
			this.geo(h.$actionSkillOutline, geoAction[0][1], {proportional: true, anchor: 0.5, scale: OUTLINE});
			this.geo(h.$actionItemOutline, geoAction[1][0], {proportional: true, anchor: 0.5, scale: OUTLINE});
			this.geo(h.$actionSpecialOutline, geoAction[1][1], {proportional: true, anchor: 0.5, scale: OUTLINE});
			
			this._scaleAttack = h.$actionAttack.scale.x;
			this._scaleSkill = h.$actionSkill.scale.x;
			this._scaleItem = h.$actionItem.scale.x;
			this._scaleSpecial = h.$actionSpecial.scale.x;

			this._scaleAttackOutline = h.$actionAttackOutline.scale.x;
			this._scaleSkillOutline = h.$actionSkillOutline.scale.x;
			this._scaleItemOutline = h.$actionItemOutline.scale.x;
			this._scaleSpecialOutline = h.$actionSpecialOutline.scale.x;
		}
		
		_initScene() {
			const r = this._battle.resources;
			
			const actionAttack = new PIXI.Sprite(r.packed.spritesheet.textures['actionAttack.png']);
			const actionSkill = new PIXI.Sprite(r.packed.spritesheet.textures['actionSkill.png']);
			const actionItem = new PIXI.Sprite(r.packed.spritesheet.textures['actionItem.png']);
			const actionSpecial = new PIXI.Sprite(r.packed.spritesheet.textures['actionSpecial.png']);
			
			const actionAttackOutline = new PIXI.Sprite(r.packed.spritesheet.textures['actionAttack.png']);
			const actionSkillOutline = new PIXI.Sprite(r.packed.spritesheet.textures['actionSkill.png']);
			const actionItemOutline = new PIXI.Sprite(r.packed.spritesheet.textures['actionItem.png']);
			const actionSpecialOutline = new PIXI.Sprite(r.packed.spritesheet.textures['actionSpecial.png']);
			
			actionAttackOutline.on('pointertap', this._onClickAttack, this);
			actionSkillOutline.on('pointertap', this._onClickSkill, this);
			actionItemOutline.on('pointertap', this._onClickItem, this);
			actionSpecialOutline.on('pointertap', this._onClickSpecial, this);
			
			actionAttackOutline.filters = [this._filterAttack];
			actionSkillOutline.filters = [this._filterSkill];
			actionItemOutline.filters = [this._filterItem];
			actionSpecialOutline.filters = [this._filterSpecial];
			
			actionAttack.alpha = ALPHA_ACTION;
			actionSkill.alpha = ALPHA_ACTION;
			actionItem.alpha = ALPHA_ACTION;
			actionSpecial.alpha = ALPHA_ACTION;
					
			this.view.addChild(actionAttackOutline);
			this.view.addChild(actionSkillOutline);
			this.view.addChild(actionItemOutline);
			this.view.addChild(actionSpecialOutline);

			this.view.addChild(actionAttack);
			this.view.addChild(actionSkill);
			this.view.addChild(actionItem);
			this.view.addChild(actionSpecial);

			this._filterAttack.matrix[4] = 0.6;
            this._filterAttack.matrix[9] = 0.6;
            this._filterAttack.matrix[14] = 1;

			this._filterSkill.matrix[4] = 0.5;
            this._filterSkill.matrix[9] = 0.5;
            this._filterSkill.matrix[14] = 1;
            
			this._filterItem.matrix[4] = 0.6;
            this._filterItem.matrix[9] = 1;
            this._filterItem.matrix[14] = 1;            
            
			this._filterSpecial.matrix[4] = 1;
            this._filterSpecial.matrix[9] = 0.6;
            this._filterSpecial.matrix[14] = 1;
            
			this.hierarchy = {
				$actionAttack: actionAttack,
				$actionSkill: actionSkill,
				$actionItem: actionItem,
				$actionSpecial: actionSpecial,
				$actionAttackOutline: actionAttackOutline,
				$actionSkillOutline: actionSkillOutline,
				$actionItemOutline: actionItemOutline,
				$actionSpecialOutline: actionSpecialOutline
			};
			
			this._attachIconMouseover('Attack');
			this._attachIconMouseover('Skill');
			this._attachIconMouseover('Item');
			this._attachIconMouseover('Special');
		}
		
		_attachIconMouseover(name) {
			const icon = '$action'+name;
			const outline = '$action'+name+'Outline';
			const scaleIcon = '_scale'+name;
			const scaleOutline = '_scale'+name+'Outline';
			
			this.hierarchy[outline].interactive = true;
			this.hierarchy[outline].buttonMode = true;
			
			this.hierarchy[outline].on('pointerover', () => {
				this[scaleIcon] += 0.2;
				this[scaleOutline] += 0.2;
			},this);
			this.hierarchy[outline].on('pointerout', () => {
				this[scaleIcon] -= 0.2;
				this[scaleOutline] -= 0.2;
			},this);
		}
		
		_onClickAttack() {
			this.game.sfx('resources/translune/static/confirm');
			this._selectTarget({
				actionTarget: Lunar.TargetType.opponentsField,
				onCharSelected: targets => {
					console.log("attack", targets);
				}
			});
		}
		
		_onClickSkill() {
			this.game.sfx('resources/translune/static/confirm');
			this._skillSelect = new Lunar.Scene.SkillSelect(this.game, {
				skills: this._battler.characterState.skills,
				level: this._battler.characterState.level,
				selectText: 'Select',
				selectRequired: true
			});
			this._skillSelect.on('skill-select', this._onSkillSelect, this);
			this._skillSelect.on('skill-exit', this._onSkillExit, this);
			this.game.pushScene(this._skillSelect);
		}
		
		_onClickItem() {
			this.game.sfx('resources/translune/static/unable');
		}
		
		_onClickSpecial() {
			this.game.sfx('resources/translune/static/unable');
		}
		
		_onSkillSelect(skillSelect) {
			this._selectTarget({
				actionTarget: Lunar.TargetTypeByName(skillSelect.selectedSkill.target),
				onCharSelected: targets => {
					console.log("skill", targets);
				}
			});
			this.game.removeScene(this._skillSelect);
		}
		
		_onSkillExit() {
			this.game.removeScene(this._skillSelect);
		}
		
		_selectTarget({actionTarget, onCharSelected}) {
			for (let element of Object.values(this.hierarchy))
				element.visible = false;
			this._battle.selectTarget({
				actionTarget,
				user: this._battler,
				onCharSelected
			});
		}
	}
})(window.Lunar, window);