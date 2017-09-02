						/*
					}
						const l = _this.game.loaderFor('test');
						l.reset();
						l.add("test",`/resource/${characterState.character.imgFront}`).load((loader, resources) => {
							const s = new PIXI.extras.AnimatedSprite(Object.values(resources.test.spritesheet.textures));
							_this.view.addChild(s);
							s.position.set(_this.game.x(0.38),_this.game.y(0.68));
							s.anchor.set(0.5,0.5);
							s.scale.set(-3,3);
							s.play();
						});*/

(function(Lunar, window, undefined) {
	Lunar.Scene.MenuCharDetails = class extends Lunar.Scene.Base {
		constructor(game, menu, characterState) {
			super(game);
			this._menu = menu;
			this._characterState = characterState;
			this._releasable = menu.player.characterStates.length > 4 && characterState.level >= Lunar.Constants.minReleaseLevel;
		}

		onAdd() {
			this._initScene();
			super.onAdd();
		}
		
		destroy() {
			this._menu = undefined;
			this._characterState = undefined;
			super.destroy();
		}
		
		onRemove() {
			super.onRemove();
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			const panel = this.view.parent.parent; 
			const geoRoot = Lunar.Geometry.layoutVbox({
				box: {w: panel.bodyWidth, h: panel.bodyHeight},
				dimension: [2,3,1],
				relative: true
			});
			const geoHead = Lunar.Geometry.layoutHbox({
				box: geoRoot[0],
				dimension: [1,2],
				relative: true
			});
			const geoStat = Lunar.Geometry.layoutVbox({
				box: geoRoot[1],
				dimension: 5,
				relative: true
			});
			const geoControl = Lunar.Geometry.layoutHbox({
				box: geoRoot[2],
				padding: {left: 0.1, right: 0.1, top: 0.1, bottom: 0.1},
				relative: true
			});
			const geoBase = Lunar.Geometry.layoutVbox({
				box: geoHead[1],
				dimension: 2,
				relative: true
			});
			const geoTitle = Lunar.Geometry.layoutHbox({
				box: geoBase[0],
				dimension: [1,4],
				relative: true
			});
			const geoElement = Lunar.Geometry.layoutHbox({
				box: geoBase[1],
				dimension: 2,
				relative: true
			});
			const geoGrid = Lunar.Geometry.layoutGrid({
				box: geoRoot[1],
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

			const level = new PIXI.Text(cs.level);
			const name = new PIXI.Text(c.name);
			const element1 = new PIXI.Text(c.elements[0]);
			const element2 = new PIXI.Text(c.elements[1]);

			const nickname = new PIXI.Text(cs.nickname);
			const nature = new PIXI.Text(cs.nature);
			
			const labelHp = new PIXI.Text("HP");
			const labelMp = new PIXI.Text("MP");
			const labelPAtt = new PIXI.Text("P.Att.");
			const labelMAtt = new PIXI.Text("M. Att");
			const labelPDef = new PIXI.Text("P. Def");
			const labelMDef = new PIXI.Text("M. Def");
			const labelSpeed = new PIXI.Text("Speed");
			const labelExp = new PIXI.Text("EXP");
			
			const valueHp = new PIXI.Text(this.fmtStat(ms.computedMaxHp));
			const valueMp = new PIXI.Text(this.fmtStat(ms.computedMaxMp));
			const valuePAtt = new PIXI.Text(this.fmtStat(ms.computedPhysicalAttack));
			const valuePDef = new PIXI.Text(this.fmtStat(ms.computedPhysicalDefense));
			const valueMAtt = new PIXI.Text(this.fmtStat(ms.computedMagicalAttack));
			const valueMDef = new PIXI.Text(this.fmtStat(ms.computedMagicalDefense));
			const valueSpeed = new PIXI.Text(this.fmtStat(ms.computedSpeed));
			const valueExp = new PIXI.Text(cs.exp);
			
			const containerRoot = new PIXI.Container();
			const containerHead = new PIXI.Container();
			const containerStat = new PIXI.Container();
			const containerControl = new PIXI.Container();
			const containerSprite = new PIXI.Container();
			const containerBase = new PIXI.Container();
			const containerTitle = new PIXI.Container();
			const containerElement = new PIXI.Container();
			
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
						
			if (this._releasable) {
				
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
		
		/** @private */
		fmtStat(stat) {
			return Lunar.String.rjust(stat, '0', 3);
		}
	};
})(window.Lunar, window);