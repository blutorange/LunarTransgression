(function(Lunar, window, undefined) {
	Lunar.Scene.SkillSelect = class extends Lunar.Scene.Base {
		constructor(game, characterState) {
			super(game);
			this._skills = [];
			for (let key of Object.keys(characterState.character.skills)) {
				if (key <= characterState.level) {
					for (let skill of characterState.character.skills[key]) {
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
		}
		
		destroy() {
			this._character = undefined;
			super.destroy();
		}
		
		onRemove() {
			super.onRemove();
		}
		
		layout() {
			super.layout();		
			const h = this.hierarchy;

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

			const geoBase = Lunar.Geometry.layoutVbox({
				box: h.$frame.bodyDimension,
				dimension: [6,1],
				padding: {
					y: 0.05
				},
				relative: true
			});
			
			h.$overlay.clear();
			h.$overlay.beginFill(0x222222, 0.85);
			h.$overlay.drawRect(0, 0, this.game.w, this.game.h);
			h.$overlay.endFill();			
		}
		
		/**
		 * @private
		 */
		_initScene() {
			const _this = this;
			
			const overlay = new PIXI.Graphics();
			const frame = new PIXI.NinePatch(this.game.loaderFor('base').resources.textbox);
			
			this.view.addChild(overlay);
			this.view.addChild(frame);
			
			this.hierarchy = {
				frame: [
					
				],
				$overlay: overlay,
				$frame: frame
			};
		}
		
	};
})(window.Lunar, window);