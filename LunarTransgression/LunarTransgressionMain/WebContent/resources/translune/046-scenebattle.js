/**
 * 
 */
(function(Lunar, window, undefined) {
	/**
	 * The main menu.
	 */
	const BATTLE_CIRCLE_CENTER_X = 0;
	const BATTLE_CIRCLE_CENTER_Y = 0.2;
	const BATTLE_CIRCLE_RADIUS_X = 0.42;
	const BATTLE_CIRCLE_RADIUS_Y = 0.12;
	const BATTLE_CIRCLE_SCALE = 0.4;
	
	Lunar.Scene.Battle = class extends Lunar.Scene.Base {
		constructor(game, battleData) {
			super(game);
			this._battleData = battleData;
			this._loaded = false;
			this._loadScene = undefined
			this._fieldRotation = 0;
		}
		
		sceneToAdd() {
			if (this._loaded)
				return super.sceneToAdd();	
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				 fetch: Lunar.FetchType.availableBgAndBgm
			});

			const delegateLoadable = new Lunar.DelegateLoadable();
			const chainedLoadable = new Lunar.ChainedLoadable(requestLoadable, delegateLoadable);
			this._loadScene = new Lunar.Scene.Load(this.game, chainedLoadable);
			
			requestLoadable.promise
				.then(response => 
					_this._initLoader(delegateLoadable, response))
				.catch(error => {
					console.log("could not fetch available bg and bgm", e);
					this.showConfirmDialog("Could not load game, please try again later.", () => {
						window.location.reload();
					});
				});
			chainedLoadable.addCompletionListener(this.method('_initScene'));
			
			return this._loadScene;
		}
		
		destroy() {
			//
			this._loadScene = undefined
			super.destroy();
		}
		
		onAdd() {
			super.onAdd();
			if (true || this.game.debug) {
				let g=()=>{let f;let a=this;window.keep=true;let d=0;return f=()=>{a.positionBattlers(d+=1.4);a.positionBackground(d);if(window.keep)window.setTimeout(f, 20)}}
				g()();
			}
		}
		
		onRemove() {
			super.onRemove();
		}

		update(delta) {
			super.update(delta);
		}

		layout() {
			super.layout();
			const h = this.hierarchy;
			
			const geoUiBottom = Lunar.Geometry.layoutHbox({
				box: {
					x: 0,
					y: this.game.h - this._textScene.boxHeight,
					w: this.game.w,
					h: this._textScene.boxHeight,
				},
				dimension: [20,80]
			});
			
			this.geo(h.ui.$action, geoUiBottom[0]);
			this.geo(h.ui.action.$avatar, geoUiBottom[0], {anchor: 0.5});
						
			h.$backgroundNormal.width = this.game.w;
			h.$backgroundNormal.height = this.game.h;
			if (h.$backgroundMirrored.scale.x < 0)
				h.$backgroundMirrored.scale.x *= -1;
			h.$backgroundMirrored.width = this.game.w;
			h.$backgroundMirrored.height = this.game.h;
			h.$backgroundMirrored.scale.x *= -1;	
			
			this._field.resetWorldTransform();
			this._field.resetLocalTransform();

			this._field.setDimensions(this.game.dx(0.7),this.game.dy(0.025), this.game.dx(0.80));
			this._field.translate3(this.game.x(-0.1), this.game.y(-0.0), this.game.dx(0.50+0.20));
		    this._field.rotate3Deg(10, [1,0,0]);
		    this._field.rotate3Deg(5, [0,0,1]);
		}
		
		positionBackground(cameraDegree) {
			const h = this.hierarchy;
			const position = Lunar.Math.modulo(cameraDegree, 360.0) / 180;
			if (position < 1) {
				h.$backgroundNormal.position.x = -this.game.w * position;
				h.$backgroundMirrored.position.x = this.game.w * (2-position);
			}
			else {
				h.$backgroundNormal.position.x = h.$backgroundMirrored.position.x = this.game.w * (2-position);
			}
		}
		
		positionBattlers(cameraDegree) {
			const _this = this;
			
			Lunar.Math.modulo(cameraDegree, 360.0);
			const fmath = this.game.fmath;
			const centerX = this.game.x(BATTLE_CIRCLE_CENTER_X);
			const centerY = this.game.y(BATTLE_CIRCLE_CENTER_Y);
			const radiusX = this.game.dx(BATTLE_CIRCLE_RADIUS_X);
			const radiusY = this.game.dy(BATTLE_CIRCLE_RADIUS_Y);
			
			for (let i = 4; i --> 0;) {
				const angle = Lunar.Math.modulo(-cameraDegree+185+(i+1)*16,360)*Lunar.Constants.degToRad;
				const x = centerX + radiusX * fmath.cos(angle);
				const y = centerY - radiusY * fmath.sin(angle);
				this._battlers[i].ringPosition(x, y, angle, centerY, radiusY);
			}
			
			for (let i = 4; i --> 0;) {
				const angle = Lunar.Math.modulo(-cameraDegree+5+(i+1)*16, 360)*Lunar.Constants.degToRad;
				const x = centerX + radiusX * fmath.cos(angle);
				const y = centerY - radiusY * fmath.sin(angle);
				this._battlers[4+i].ringPosition(x, y, angle, centerY, radiusY); 					
			}
		
	        this._field.preRotate3Deg(cameraDegree-this._fieldRotation, [0,0,1]);
	        this._fieldRotation = cameraDegree;
			this._field.update();
		}
		
		/**
		 * @private
		 */
		_initLoader(delegateLoadable, response) {		
			const background = Lunar.Object.randomEntry(response.data.data.bgBattle).value;
			const music = Lunar.Object.randomEntry(response.data.data.bgmBattle).value;
			
			this.game.switchBgm(music.map(file => `resource/${file}`));
			
			const loader = this.game.loaderFor("battle");
			const loaderLoadable = new Lunar.LoaderLoadable(loader);
			delegateLoadable.loadable = loaderLoadable; 
			loader.reset();
			loader.add("bg", `resource/${background}`)
				.add('packed', 'resources/translune/static/battle/packed.json')
				.add('field', 'resources/translune/static/battle/packed_field.json')
				.add('cursor', 'resources/translune/static/battle/cursor.json')
				.add('me_ava', `resource/${this._battleData.player.imgAvatar}`)
				.add('u_ava', `resource/${this._battleData.opponent.imgAvatar}`)
				.add('me_front_1', `resource/${this._battleData.player.characterStates[0].imgFront}`)
				.add('me_front_2', `resource/${this._battleData.player.characterStates[1].imgFront}`)
				.add('me_front_3', `resource/${this._battleData.player.characterStates[2].imgFront}`)
				.add('me_front_4', `resource/${this._battleData.player.characterStates[3].imgFront}`)
				.add('me_back_1', `resource/${this._battleData.player.characterStates[0].imgBack}`)
				.add('me_back_2', `resource/${this._battleData.player.characterStates[1].imgBack}`)
				.add('me_back_3', `resource/${this._battleData.player.characterStates[2].imgBack}`)
				.add('me_back_4', `resource/${this._battleData.player.characterStates[3].imgBack}`)
				.add('u_front_1', `resource/${this._battleData.opponent.characterStates[0].imgFront}`)
				.add('u_front_2', `resource/${this._battleData.opponent.characterStates[1].imgFront}`)
				.add('u_front_3', `resource/${this._battleData.opponent.characterStates[2].imgFront}`)
				.add('u_front_4', `resource/${this._battleData.opponent.characterStates[3].imgFront}`)
				.add('u_back_1', `resource/${this._battleData.opponent.characterStates[0].imgBack}`)
				.add('u_back_2', `resource/${this._battleData.opponent.characterStates[1].imgBack}`)
				.add('u_back_3', `resource/${this._battleData.opponent.characterStates[2].imgBack}`)
				.add('u_back_4', `resource/${this._battleData.opponent.characterStates[3].imgBack}`)
				.load();
		}
		
		_initScene() {
			this._loadScene = undefined;
			this._loaded = true;
			this.game.pushScene(this);
			const _this = this;			
			const resources = this.game.loaderFor("battle").resources;
			
			this._field = new Lunar.PixiCube({
				top: resources.field.spritesheet.textures['field_main.png'],
				bottom: resources.me_ava.texture,
				back: resources.field.spritesheet.textures["field_back.png"],
				left: resources.field.spritesheet.textures["field_left.png"],
				front: resources.field.spritesheet.textures["field_front.png"],
				right: resources.field.spritesheet.textures["field_right.png"],
			});
			
			const actionAvatar = new PIXI.Sprite(resources.me_ava.texture);
			const bgNormal = new PIXI.Sprite(resources.bg.texture);
			const bgMirrored = new PIXI.Sprite(resources.bg.texture);
			const containerBack = new PIXI.ClipContainer();
			const containerUi = new PIXI.ClipContainer();
			const containerFront = new PIXI.ClipContainer();
			
			const containerAction = new PIXI.ClipContainer();
			const containerBattlers = new PIXI.ClipContainer();
			const groupBattler = new PIXI.DisplayGroup(0, true);
			containerBattlers.displayList = new PIXI.DisplayList();
			
			actionAvatar.alpha = 0.7;
			
			containerUi.addChild(containerAction);
			containerUi.addChild(actionAvatar);
			this._textScene = new Lunar.Scene.BattleText(this.game);		
			this.game.pushScene(this._textScene, containerUi);
			
			this.view.addChild(bgNormal);
			this.view.addChild(bgMirrored);
			this.view.addChild(containerBack);
			this.view.addChild(containerUi);
			this.view.addChild(containerFront);
			
			containerBack.addChild(this._field.container);
			containerBack.addChild(containerBattlers);
			
			this._battlers = [
				new Lunar.Scene.Battler(this.game, resources.me_back_1, resources.me_front_1),
				new Lunar.Scene.Battler(this.game, resources.me_back_2, resources.me_front_2),
				new Lunar.Scene.Battler(this.game, resources.me_back_3, resources.me_front_3),
				new Lunar.Scene.Battler(this.game, resources.me_back_4, resources.me_front_4),
				new Lunar.Scene.Battler(this.game, resources.u_back_1, resources.u_front_1),
				new Lunar.Scene.Battler(this.game, resources.u_back_2, resources.u_front_2),
				new Lunar.Scene.Battler(this.game, resources.u_back_3, resources.u_front_3),
				new Lunar.Scene.Battler(this.game, resources.u_back_4, resources.u_front_4)
			];
			
			for (let i = 0; i < this._battlers.length; ++i) {
				this.game.pushScene(this._battlers[i], containerBattlers);
				this._battlers[i].view.displayGroup = groupBattler;
			}
			
			this.hierarchy = {
				ui: {
					action: {
						$avatar: actionAvatar
					},
					$action: containerAction
				},
				$backgroundNormal: bgNormal,
				$backgroundMirrored: bgMirrored,
				$back: containerBack,
				$front: containerFront,
				$ui: containerUi
			};
		}
		
		_pushBattler(battler) {
			
			battlers.push(container);
		}
	}
	
	Lunar.Scene.Battler = class extends Lunar.Scene.Base {
		constructor(game, back, front) {
			super(game);
			this._back = Object.values(back.spritesheet.textures);
			this._front = Object.values(front.spritesheet.textures);
			this._mode = 'back';
			this._sprite = new PIXI.extras.AnimatedSprite(this._back);
			this._sprite.anchor.set(0.5,0.5);
			this.view.width = this._sprite.width;
			this.view.height = this._sprite.height;
			this._scale = 1;
		}
		
		ringPosition(x, y, angle, centerY, radiusY) {
			this.setPosition(x,y);
			this.setScaling(1+BATTLE_CIRCLE_SCALE*(y-centerY)/radiusY);
			this.view.zOrder = -y;
			if (angle < Lunar.Constants.deg180AsRad) {
				this.asFront();
				this.setMirroring(angle > Lunar.Constants.deg90AsRad);
			} 
			else {
				this.asBack();
				this.setMirroring(angle > Lunar.Constants.deg270AsRad);
			}
		}
		
		ringPositionOutwards(x, y, angle, centerY, radiusY) {
			this.setPosition(x,y);
			this.setScaling(1+BATTLE_CIRCLE_SCALE*(y-centerY)/radiusY);
			this.view.zOrder = -y;
			if (angle < Lunar.Constants.deg180AsRad) {
				this.asBack();
				this.setMirroring(angle > Lunar.Constants.deg90AsRad);
			} 
			else {
				this.asFront();
				this.setMirroring(angle > Lunar.Constants.deg270AsRad);
			}
		}


		asBack() {
			if (this._mode === 'back')
				return;
			this._toSwitch = this._back;
			this._mode = 'back';
		}
		
		update(delta) {
			if (this._toSwitch)
				this._switch(this._toSwitch);
			if (this._dirty) {
				this._rescale();
				this._dirty = false;
			}
		}
		
		asFront() {
			if (this._mode === 'front')
				return;
			this._toSwitch = this._front;
			this._mode = 'front';
		}
		
		layout() {
			this._rescale();
		}
		
		onAdd() {
			this.view.addChild(this._sprite);
			this._sprite.play();
		}
		
		setScaling(scale) {
			this._scale = scale;
			this._dirty = true;
		}
		
		setMirroring(doMirror) {
			const isMirrored = this._sprite.scale.x < 0;
			if (isMirrored != doMirror)
				this._sprite.scale.x *= -1;
		}
		
		setPosition(x,y) {
			this.view.position.set(x,y);
		}
		
		_switch(textures) {
			this._sprite.stop();
			const frame = this._sprite.currentFrame;
			this._sprite.textures = textures;
			this._sprite.gotoAndPlay(frame % this._sprite.totalFrames);
			this._toSwitch = null;
			this._dirty = true;
		}
		
		_rescale() {
			Lunar.Geometry.proportionalScale(this._sprite, this.view.width*this._scale, this.view.height*this._scale);
		}		
	};
})(window.Lunar, window);
