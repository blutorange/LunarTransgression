/**
 * The main battle scene.
 */
(function(Lunar, window, undefined) {
	Lunar.Scene.Battle = class extends Lunar.Scene.Base {
		constructor(game, battleData) {
			super(game);
			this._battleData = battleData;
			this._loaded = false;
			this._loadScene = undefined
			this._textScene = undefined
			this._field = undefined;
			this._fieldRotation = 0;
			this._battlers = [];
			this._childScenes = [];
			this._camera = {
				degree: 0, // current angle
				moveStartAngle: 0, // transition start angle
				moveTargetAngle: 0, // target angle in degree
				moveStartTime: 0, // transition start time
				moveDuration: 0, // transition time in seconds , 0 to disable
				moveInverseDuration: 0, // seconds
			};
			this._battleCircle = {
					centerX: 0,
					centerY: 0.2,
					radiusX: 0.42,
					radiusY: 0.12,
					scale: 0.4
			};
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
			this._field.destroy();
			this._camera = {};
			this._battlers = [];
			this._childScenes = [];
			this._loadScene = undefined
			this._textScene = undefined;
			this._field = undefined;
			super.destroy();
		}
		
		onAdd() {
			super.onAdd();
			
//			// TODO remove this is debug
//			if (true || this.game.debug) {
//				let g=()=>{let f;let a=this;window.keep=true;let d=0;return f=()=>{a.positionBattlers(d+=1.4);a.positionBackground(d);if(window.keep)window.setTimeout(f, 20)}}
//				g()();
//			}
			this._pushChildScene(new Lunar.Scene.BattleCurtain(this), this.hierarchy.$front);
			this.positionCameraOpponent(1, 8);
		}
	
		onRemove() {
			this._battlers.forEach(battler => this.game.removeScene(battler));
			this._childScenes.forEach(scene => this.game.removeScene(scene));
			this.game.removeScene(this._textScene);
			this._battlers = [];
			this._childScenes = [];
			super.onRemove();
		}

		update(delta) {
			super.update(delta);
			this._updateCamera(this._camera);
//			this._field.update();
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
		
		get resources() {
			return this.game.loaderFor('battle').resources;
		}
		
		moveCameraPlayer(index, seconds = 1, deltaDegree = 0, shortest = true) {
			this.moveCamera(180+95+16*(index+1) + deltaDegree, seconds, undefined, shortest);
		}
		
		moveCameraOpponent(index, seconds = 1, deltaDegree = 0, shortest = true) {
			this.moveCamera(95+16*(index+1) + deltaDegree, seconds, undefined, shortest);
		}
		
		moveCamera(targetDegree, seconds = 1, startDegree = undefined, shortest = true) {
			startDegree = startDegree  !== undefined ? startDegree : this._camera.degree;
			if (shortest) {
				startDegree = Lunar.Math.modulo(startDegree, 360);
				targetDegree = Lunar.Math.modulo(targetDegree, 360);
				if (targetDegree - startDegree > 180)
					targetDegree -= 360;
				else if (startDegree - targetDegree > 180)
					targetDegree += 360;
			}
			this._camera.moveTargetAngle = targetDegree;
			this._camera.moveStartAngle = startDegree;
			this._camera.moveStartTime = this.time;
			this._camera.moveDuration = seconds;
			this._camera.moveInverseDuration = 1/seconds;
		}
		
		positionCameraPlayer(index, deltaDegree = 0) {
			this.positionCamera(180+95+16*(index+1) + deltaDegree);
		}
		
		positionCameraOpponent(index, deltaDegree = 0) {
			this.positionCamera(95+16*(index+1) + deltaDegree);
		}
		
		positionCamera(cameraDegree) {
			this.positionBackground(cameraDegree);
			this.positionBattlers(cameraDegree);
			this._camera.degree = cameraDegree;
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
			const centerX = this.game.x(this._battleCircle.centerX);
			const centerY = this.game.y(this._battleCircle.centerY);
			const radiusX = this.game.dx(this._battleCircle.radiusX);
			const radiusY = this.game.dy(this._battleCircle.radiusY);
			
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
		
		_updateCamera(camera) {
			// Transition smoothly
			if (camera.moveDuration > 0) {
				let t = (this.time - camera.moveStartTime) * camera.moveInverseDuration
				if (t >= 1) {
					this.positionCamera(camera.moveTargetAngle);
					camera.moveDuration = 0;
					this.emit('camera-reached');
				}
				else {
					const diff = camera.moveTargetAngle-camera.moveStartAngle;
					const newAngle = camera.moveStartAngle + diff * Lunar.Interpolation.slowInSlowOutOnce(t);
					this.positionCamera(newAngle);
				}
			}
		}
		
		/**
		 * We keep a reference to the pushed scene so that we
		 * can clean up later.
		 */
		_pushChildScene(scene) {
			this.game.pushScene(scene)
			this._childScenes.push(scene);
		}
		
		_removeChildScene(scene) {
			this.game.removeScene(scene)
			Lunar.Array.removeElement(this._childScenes, scene);
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
			
			actionAvatar.alpha = 0.7;
			containerBattlers.displayList = new PIXI.DisplayList();
			containerUi.visible = false;
			
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
				new Lunar.Scene.BattleBattler(this.game, resources.me_back_1, resources.me_front_1, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.me_back_2, resources.me_front_2, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.me_back_3, resources.me_front_3, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.me_back_4, resources.me_front_4, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.u_back_1, resources.u_front_1, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.u_back_2, resources.u_front_2, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.u_back_3, resources.u_front_3, this._battleCircle),
				new Lunar.Scene.BattleBattler(this.game, resources.u_back_4, resources.u_front_4, this._battleCircle)
			];
			
			for (let i = 0; i < this._battlers.length; ++i) {
				this.game.pushScene(this._battlers[i], containerBattlers);
				this._battlers[i].view.displayGroup = groupBattler;
				this._battlers[i].view.visible = false;
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
	}
})(window.Lunar, window);
