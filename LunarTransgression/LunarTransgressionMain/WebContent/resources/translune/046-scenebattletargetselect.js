/**
 * Fades out the opponent cube.
 * new PIXI.Sprite(this._battle.resources.packed.spritesheet.textures['battlelogo.png']);
 */
(function(Lunar, window, undefined) {
	const PULSE = 0.1;
	const WIDTH = 0.13;
	const HEIGHT = 0.13;
	const ROTATE_SPEED = 2*Math.PI*3;
	const SCALE_SPEED = 2*Math.PI*2;
	Lunar.Scene.BattleTargetSelect = class extends Lunar.Scene.Base {
		constructor(battle, actionTarget, user) {
			super(battle.game);
			this._selectedBattlers = [];
			this._battle = battle;
			this._user = user;
			this._actionTarget = actionTarget;
		}
		
		destroy() {
			this._battler = undefined;
			this._user = undefined;
			this._selectedBattlers = [];
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
		}

		update(delta) {
			super.update(delta);
		}
		
		onRemove() {
			for (let battler of this._battle._battlers) {
				battler.view.interactive = false;
				battler.view.buttonMode = false;
			}
			this._battle = undefined;
			super.onRemove();
		}
		
		update(delta) {
			this.layout();
			super.update(delta);
			const alpha = this.game.fmath.sin(SCALE_SPEED*this.time);
			for (let entry of this.hierarchy.targets) {
				entry.$icon.rotate += delta*ROTATE_SPEED;
				entry.$icon.scale.set(entry.originalScale*(alpha*PULSE+1));
			}
		}
		
		layout() {
			super.layout();
			const h = this.hierarchy;
			const width = this.game.dx(WIDTH);
			const height = this.game.dy(HEIGHT);
			for (let entry of h.targets) {
				const pos = entry.$battler.getPosition();
				this.geo(entry.$icon, {x: pos.x, y:pos.y, w:width, h:height}, {proportional: true});
				entry.$icon.anchor.set(0.5);
				entry.originalScale = entry.$icon.scale.x;
			}
		}
		
		_initScene() {
			const textureTarget = this._battle.resources.packed.spritesheet.textures['target.png'];
			const targets = [];
			for (let battler of this._battle._battlers) {
				if (!battler.dead && this._accepts(battler)) {
					const spriteTarget = new PIXI.Sprite(textureTarget);
					spriteTarget.interactive = true;
					spriteTarget.buttonMode = true;
					spriteTarget.on('pointertap', () => this._onClickBattle(battler), this);
					this.view.addChild(spriteTarget);
					targets.push({$icon: spriteTarget, $battler: battler});
				}
			}
			
			this.hierarchy = {
				targets: targets
			};
		}
		
		_onClickBattle(battler) {
			const isSelected = Lunar.Array.containsElement(this._selectedBattlers, battler);
			if (isSelected) {
				this.game.sfx('resources/translune/static/cancel');
				Lunar.Array.removeElement(this._selectedBattlers, battler);
			}
			else {
				this.game.sfx('resources/translune/static/ping');
				this._selectedBattlers.push(battler);
			}
			if (this._accepts(this._selectedBattlers)) {
				this.game.sfx('resources/translune/static/confirm');
				this.emit('char-selected', this._selectedBattlers, this._user);
			}
		}
		
		_accepts(battler) {
			const battlers = Array.isArray(battler) ? battler : [battler];
			return this._actionTarget.accepts(battlers.map(b => ({
				isUser: b.characterState.id === this._user.characterState.id,  
				isPlayer: b.isPlayer
			})));
		}
	}
})(window.Lunar, window);