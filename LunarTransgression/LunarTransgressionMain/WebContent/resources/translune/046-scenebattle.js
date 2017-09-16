/**
 * The main battle scene.
 */
(function(Lunar, window, undefined) {
	const DELAY_BALL = 0.35; // seconds
	
	Lunar.Scene.Battle = class extends Lunar.Scene.Base {
		constructor(game, battleData) {
			super(game);
			this._battleData = battleData;
			this._loaded = false;
			this._turnPreparation = false;
			this._loadScene = undefined
			this._textScene = undefined
			this._field = undefined;
			this._bgm = undefined;
			this._waitDialog = undefined;
			this._countWinLose = 0;
			this._fieldRotation = 0;
			this._battlers = [];
			this._childScenes = [];
			this._commands = [];
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
					centerY: 0.25,
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
			this._loadScene = undefined;
			this._waitDialog = undefined;
			this._bgm = undefined;
			this._textScene = undefined;
			this._field = undefined;
			super.destroy();
		}
		
		onAdd() {
			super.onAdd();
			this._attachListeners();
			this._animationOpening();
		}
		
		onRemove() {
			this._detachListeners();
			this._battlers.forEach(battler => this.game.removeScene(battler));
			this._childScenes.forEach(scene => this.game.removeScene(scene));
			this.game.removeScene(this._loadScene);
			this.game.removeScene(this._textScene);
			this.game.removeScene(this._waitDialog);
			this._battlers = [];
			this._commands = [];
			this._childScenes = [];
			super.onRemove();
		}
		
		update(delta) {
			super.update(delta);
			if (this._turnPreparation) {
				const delta = 28*this.game.fmath.sin(0.2*this.time);
				this.positionCameraPlayer(1, 8 + delta);				
			}
			this._updateCamera(this._camera);
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
		    
		    this.positionCamera(this._camera.degree);
		}
		
		get textScene() {
			return this._textScene;
		}
		
		get resources() {
			return this.game.loaderFor('battle').resources;
		}
		
		getPlayer(index) {
			return this._battlers[index];
		}
		
		getOpponent(index) {
			return this._battlers[4+index];
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
		
		selectTarget({actionTarget, user, onCharSelected = () => 0}) {
			if (actionTarget.accepts([])) {
				onCharSelected.call(this, []);
				return;
			}
			const scene = new Lunar.Scene.BattleTargetSelect(this, actionTarget, user);
			this._pushChildScene(scene, this.hierarchy.ui.$target);
			scene.once('char-selected', targets => {
				onCharSelected.call(this, targets);
				this._removeChildScene(scene);
			}, this);
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
		
		_attachListeners() {
			this.game.net.registerMessageHandler(Lunar.Message.battlePreparationCancelled, {
				handle: this.method('_onMessageCancelled'),
				error: this.method('_onMessageError')
			});
			this.game.net.registerMessageHandler(Lunar.Message.battleCancelled, {
				handle: this.method('_onMessageCancelled'),
				error: this.method('_onMessageError')
			});
			this.game.net.registerMessageHandler(Lunar.Message.battleStepped, {
				handle: this.method('_onMessageBattleStepped'),
				error: this.method('_onMessageError')
			});
			this.game.net.registerMessageHandler(Lunar.Message.battleEnded, {
				handle: this.method('_onMessageBattleEnded'),
				error: this.method('_onMessageError')
			});
		}
		
		_detachListeners() {
			this.game.net.removeMessageHandlers(Lunar.Message.battlePreparationCancelled);
			this.game.net.removeMessageHandlers(Lunar.Message.battleCancelled);
			this.game.net.removeMessageHandlers(Lunar.Message.battleStepped);
		}
		
		/**
		 * We keep a reference to the pushed scene so that we
		 * can clean up later.
		 */
		_pushChildScene(scene, container) {
			this.game.pushScene(scene, container)
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
			this._bgm = music.map(file => `resource/${file}`);
			
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
			this.game.switchBgm(this._bgm, 0.15, 4500);
			
			this._loadScene = undefined;
			this._loaded = true;
			this.game.pushScene(this);		
			const resources = this.game.loaderFor("battle").resources;
			
			// Create elements
			const actionAvatar = new PIXI.Sprite(resources.me_ava.texture);
			const bgNormal = new PIXI.Sprite(resources.bg.texture);
			const bgMirrored = new PIXI.Sprite(resources.bg.texture);
			const opponentCube = Lunar.Scene.BattleOpponentWiggle.createCube(this);
			const battleLogo = new PIXI.Sprite(resources.packed.spritesheet.textures['battlelogo.png']);
			const curtainLeft = new PIXI.Sprite(resources.packed.spritesheet.textures['curtainleft.png']);
			const curtainRight = new PIXI.Sprite(resources.packed.spritesheet.textures['curtainright.png']);
			const win = new PIXI.Sprite(resources.packed.spritesheet.textures['win.png']);
			const lose = new PIXI.Sprite(resources.packed.spritesheet.textures['lose.png']);
			const balls = [
				new PIXI.Sprite(resources.packed.spritesheet.textures['pokeball.png']),
				new PIXI.Sprite(resources.packed.spritesheet.textures['pokeball.png']),
				new PIXI.Sprite(resources.packed.spritesheet.textures['pokeball.png']),
				new PIXI.Sprite(resources.packed.spritesheet.textures['pokeball.png'])
			];
			this._battlers = [
				new Lunar.Scene.BattleBattler(this, 0, resources.me_back_1, resources.me_front_1, this._battleCircle, this._battleData.player.characterStates[0], this._battleData.player.battleStatus[0]),
				new Lunar.Scene.BattleBattler(this, 1, resources.me_back_2, resources.me_front_2, this._battleCircle, this._battleData.player.characterStates[1], this._battleData.player.battleStatus[1]),
				new Lunar.Scene.BattleBattler(this, 2, resources.me_back_3, resources.me_front_3, this._battleCircle, this._battleData.player.characterStates[2], this._battleData.player.battleStatus[2]),
				new Lunar.Scene.BattleBattler(this, 3, resources.me_back_4, resources.me_front_4, this._battleCircle, this._battleData.player.characterStates[3], this._battleData.player.battleStatus[3]),
				new Lunar.Scene.BattleBattler(this, 4, resources.u_back_1, resources.u_front_1, this._battleCircle, this._battleData.opponent.characterStates[0], this._battleData.opponent.battleStatus[0]),
				new Lunar.Scene.BattleBattler(this, 5, resources.u_back_2, resources.u_front_2, this._battleCircle, this._battleData.opponent.characterStates[1], this._battleData.opponent.battleStatus[1]),
				new Lunar.Scene.BattleBattler(this, 6, resources.u_back_3, resources.u_front_3, this._battleCircle, this._battleData.opponent.characterStates[2], this._battleData.opponent.battleStatus[2]),
				new Lunar.Scene.BattleBattler(this, 7, resources.u_back_4, resources.u_front_4, this._battleCircle, this._battleData.opponent.characterStates[3], this._battleData.opponent.battleStatus[3])
			];
			
			// Create scenes
			this._textScene = new Lunar.Scene.BattleText(this.game);
			this._field = new Lunar.PixiCube({
				top: resources.field.spritesheet.textures['field_main.png'],
				bottom: resources.me_ava.texture,
				back: resources.field.spritesheet.textures["field_back.png"],
				left: resources.field.spritesheet.textures["field_left.png"],
				front: resources.field.spritesheet.textures["field_front.png"],
				right: resources.field.spritesheet.textures["field_right.png"],
			});
			
			// Create containers
			const containerBack = new PIXI.ClipContainer();
			const containerUi = new PIXI.ClipContainer();
			const containerFront = new PIXI.ClipContainer();
			const containerAction = new PIXI.ClipContainer();
			const containerCommand = new PIXI.ClipContainer();
			const containerTarget = new PIXI.ClipContainer();
			const containerBattlers = new PIXI.ClipContainer();
			const groupBattler = new PIXI.DisplayGroup(0, true);

			// Initialize elements.
			actionAvatar.alpha = 0.7;
			battleLogo.visible = false;
			win.visible = false;
			lose.visible = false;
			containerBattlers.displayList = new PIXI.DisplayList();
			containerUi.visible = false;
			opponentCube.container.visible = false;
			balls.forEach(ball => ball.visible = false);
			for (let i = 0; i < this._battlers.length; ++i) {
				this.game.pushScene(this._battlers[i], containerBattlers);
				this._battlers[i].view.displayGroup = groupBattler;
				this._battlers[i].view.visible = false;
			}			

			// Add elements to containers
			this.view.addChild(bgNormal);
			this.view.addChild(bgMirrored);
			this.view.addChild(containerBack);
			this.view.addChild(containerUi);
			this.view.addChild(containerFront);
			
			containerBack.addChild(this._field.container);
			containerBack.addChild(opponentCube.container);
			containerBack.addChild(containerBattlers);
			balls.forEach(ball => containerBack.addChild(ball));
			containerFront.addChild(curtainLeft);
			containerFront.addChild(curtainRight);
			containerFront.addChild(battleLogo);
			containerFront.addChild(win);
			containerFront.addChild(lose);
			containerUi.addChild(actionAvatar);
			containerUi.addChild(containerAction);
			containerUi.addChild(containerCommand);
			containerUi.addChild(containerTarget);
			this.game.pushScene(this._textScene, containerUi);
			
			// Save hierarchy.
			this.hierarchy = {
				ui: {
					action: {
						$avatar: actionAvatar
					},
					$action: containerAction,
					$command: containerCommand,
					$target: containerTarget
				},
				other: {
					$curtainLeft: curtainLeft,
					$curtainRight: curtainRight,
					$battleLogo: battleLogo,
					$win: win,
					$lose: lose,
					$opponentCube: opponentCube,
					$balls: balls
				},
				$backgroundNormal: bgNormal,
				$backgroundMirrored: bgMirrored,
				$back: containerBack,
				$front: containerFront,
				$ui: containerUi
			};
		}
		
		_turn() {
			if (this._commands.length >= 4) {
				this._turnPreparation = false;
				this._performTurn();
				return;
			}
			this._turnPreparation = true;
			const battler = this.getPlayer(this._commands.length);
			if (battler.dead) {
				this._commands.push({
					action: '',
					targets: [],
					type: Lunar.CommandType.defend,	
				});
				this._turn();
				return;
			}
			const scene = new Lunar.Scene.BattleActionSelect(this, battler);
			scene.once('action-selected', command => {
				this._removeChildScene(scene);
				this._commands.push(command);
				this._turn();
			}, this);
			this._pushChildScene(scene, this.hierarchy.ui.$command);
		}
		
		_performTurn() {
			const _this = this;
			this._textScene.clearText();
			this._waitDialog = new Lunar.Scene.Dialog(this._game, { 
				message: "Waiting for the opponent to make his turn.",
				choices: [
				]
			});
			this._pushChildScene(this._waitDialog);
			this.game.net.dispatchMessage(Lunar.Message.stepBattle, {
				battleCommandCharacter1: this._commands[0],
				battleCommandCharacter2: this._commands[1],
				battleCommandCharacter3: this._commands[2],
				battleCommandCharacter4: this._commands[3],
			}).then(response => {
				// Now we wait for the actual response from the server.
				_this._commands = [];
			}).catch(error => {
				console.error("message step battle returned an error", error);
				_this.game.removeScene(_this._waitDialog);
				_this._waitDialog = undefined;
				_this._endBattle("The fight cannot be continued, please try again later.")
			});
		}
		
		_endBattle(message) {
			this.game.switchBgm(undefined, 0.01);
			this.game.clearQueuedAddScenes();
			this.game.removeScene(this);
			const menu = new Lunar.Scene.Menu(this.game);
			if (message)
				menu.showConfirmDialog(message, () => menu.game.pushScene(menu));
			else
				menu.game.pushScene(menu);
		}
		
		_animationOpening() {
			const h = this.hierarchy;
			const o = h.other;
			
			this.positionCameraOpponent(1, 8);
			
			const sceneCurtain = new Lunar.Scene.BattleCurtain(this, o.$curtainLeft, o.$curtainRight);
			const sceneLogoIn = new Lunar.Scene.BattleLogoIn(this, o.$battleLogo);
			const sceneOpponentWiggle = new Lunar.Scene.BattleOpponentWiggle(this, o.$opponentCube);
			
			sceneCurtain.once('animation-done', data => this._removeChildScene(data.scene));
			sceneLogoIn.once('animation-done', dataIn => {
				this._removeChildScene(dataIn.scene);
				const sceneLogoOut = new Lunar.Scene.BattleLogoOut(this, o.$battleLogo);
				const sceneUiIn = new Lunar.Scene.BattleUiIn(this, this._battleData.opponent.nickname);
				sceneLogoOut.once('animation-done', data => this._removeChildScene(data.scene));
				sceneUiIn.once('animation-done', data => this._removeChildScene(data.scene));
				this._pushChildScene(sceneLogoOut);
				this._pushChildScene(sceneUiIn);
			}, this);
			sceneOpponentWiggle.once('animation-done', data => {
				const textOpponent = battler => `The opponent sent out ${battler.characterState.nickname}!&`;
				this._removeChildScene(data.scene);
				for (let i = 4; i <= 7; ++i)
					this._battlers[i].look = Lunar.Scene.BattleBattler.Look.DOWN;
				this._pushCharIn(4, {delay: 0, text: textOpponent, duration: 0.75, midHeight: 0.7});
				this._pushCharIn(5, {delay: DELAY_BALL, text: textOpponent, duration: 0.75, midHeight: 0.7});
				this._pushCharIn(6, {delay: 2*DELAY_BALL, text: textOpponent, duration: 0.75, midHeight: 0.7});
				this._pushCharIn(7, {delay: 3*DELAY_BALL, text: textOpponent, duration: 0.75, midHeight: 0.7});
				const sceneOpponentOut = new Lunar.Scene.BattleOpponentOut(this, o.$opponentCube);
				sceneOpponentOut.once('animation-done', data => this._removeChildScene(data.scene));
				this.textScene.once('text-processed', () => {
					this.moveCameraPlayer(1, 2, 8);
					this.once('camera-reached', () => {
						const textPlayer = battler => `Go,  ${battler.characterState.nickname}!&`;
						const startX = this.hierarchy.ui.action.$avatar.x + 0.5*this.hierarchy.ui.action.$avatar.width;
						const startY = this.hierarchy.ui.action.$avatar.y + 0.5*this.hierarchy.ui.action.$avatar.height;
						for (let i = 4; i <= 7; ++i)
							this._battlers[i].look = Lunar.Scene.BattleBattler.Look.INWARD;
						this._pushCharIn(0, {delay: 0, text: textPlayer, midHeight: 1, startX, startY});
						this._pushCharIn(1, {delay: DELAY_BALL, text: textPlayer, midHeight: 1.5, startX, startY});
						this._pushCharIn(2, {delay: 2*DELAY_BALL, text: textPlayer, midHeight: 1.5, startX, startY});
						this._pushCharIn(3, {delay: 3*DELAY_BALL, text: textPlayer, midHeight: 1.5, startX, startY});
						this.textScene.once('text-processed', () => {
							this.moveCamera(0, 2);
							this.once('camera-reached', () => {
								this._commands = [];
								this._turn();
							});
						});
					}, this);
				}, this);
				this._pushChildScene(sceneOpponentOut);
			}, this);
			
			this._pushChildScene(sceneCurtain);
			this._pushChildScene(sceneLogoIn);
			this._pushChildScene(sceneOpponentWiggle);
		}
		
		_pushCharIn(index, options) {
			const battler = this._battlers[index];
			this.setTimeout(options.delay, () => {
				const sceneCharIn = new Lunar.Scene.BattleCharIn(this, this.hierarchy.other.$balls[index%4], battler, options);
				this.textScene.pushText(options.text(battler));
				sceneCharIn.once('animation-done', data => {
					this._removeChildScene(data.scene);
					const sceneCharShow = new Lunar.Scene.BattleCharShow(this, battler);
					sceneCharShow.once('animation-done', dataShow => this._removeChildScene(dataShow.scene), this);
					this._pushChildScene(sceneCharShow);
				}, this);
				this._pushChildScene(sceneCharIn);
			});
		}
		
		_displayBattleResult() {
			if (this._countWinLose < 2)
				return;
			this._textScene.once('text-processed', () => {
				if (this._battleResult.isVictory) {
					this._performLoot();
				}
				else {
					this._endBattle('Keep your eyes on the sun and you will not see the shadows.');
				}
			}, this);
			this._battleResult.battleResults.forEach(result => {
				result.sentences.forEach(sentence => this._textScene.pushText(sentence));
			});
			if (this._battleResult.isVictory) {
				this._textScene.pushText('You searched the fallen for spoils.&');
			}
		}

		_performLoot() {
			const spoilScene = new Lunar.Scene.BattleLoot(this);
			spoilScene.on('loot-perform', (characterStates, items) => {
				console.log("loot", characterStates, items);
				const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.loot, {
					characterState: characterStates.length > 0 ? characterStates[0].id : null,
					item: items.length > 0 ? items[0].name : null,
					dropItem: null,
					dropCharacterState: null
				});
				requestLoadable.promise.then(response => {
					this._endBattle('Both bad bananas besmirch billions of barrels.');					
				}).catch(error => {
					console.error('loot failed', error);
					this._endBattle('Cannot loot right now, please try again later.');	
				}).then(() => this.game.removeScene(this._loadScene));
				this._loadScene = new Lunar.Scene.Load(this.game, requestLoadable);
				this.game.pushScene(this._loadScene);
			}, this);
			spoilScene.on('loot-cancel', () => {
				this.game.removeScene(spoilScene);
				this._endBattle('Goodness is about how we treat other people.');
			}, this);
			spoilScene.on('loot-error', error => {
				console.error('error occured while selecting loot', error);
				this.game.removeScene(spoilScene);
				this._endBattle('Cannot loot right now, please try again later.');
			}, this);
			this.game.pushScene(spoilScene);
		}
		
		_onMessageError(status) {
			console.error("received error message from server", status);
			this._endBattle('The fight cannot be continued, please try again later.');
		}
		
		
		_onMessageCancelled(response) {
			console.error("server cancelled battle", response);
			this._endBattle('Battle was cancelled.');
		}
		
		_onMessageBattleEnded(response) {
			this._countWinLose += 1;
			this._displayBattleResult();
			this._battleResult = response.data;
		}
		
		_onMessageBattleStepped(response) {
			this.game.removeScene(this._waitDialog);
			this._waitDialog = undefined;
			this._evaluateBattleResult(response.data);
		}
		
		_updateComputedStatusPlayer(index, battleStatus) {
			this._battleData.player.battleStatus[index] = battleStatus;
			this._battlers[index].updateComputedStatus(battleStatus);
		}
		
		_updateComputedStatusOpponent(index, battleStatus) {
			this._battleData.opponent.battleStatus[index] = battleStatus;
			this._battlers[4+index].updateComputedStatus(battleStatus);
		}
		
		_evaluateBattleResult(result, index = 0) {
			if (index >= result.battleResults.length) {
				if (result.causesEnd) {
					const winLoseScene = new Lunar.Scene.BattleWinLoseIn(this, result.causesEnd > 0);
					winLoseScene.once('animation-done', () => {
						this._removeChildScene(winLoseScene);
						this._countWinLose += 1;
						this._displayBattleResult();
					}, this);
					this._pushChildScene(winLoseScene);
				}
				else {
					for (let i = 0; i < 4; ++i) {
						this._updateComputedStatusPlayer(i, result.player[i]);
						this._updateComputedStatusOpponent(i, result.opponent[i])
					}
					this.moveCamera(0, 2);
					this.once('camera-reached', () => {
						this._commands = [];
						this._turn();
					});
				}
				return;
			}
			const battleResult = result.battleResults[index];
			const battler = this._battlerById(battleResult.user);

			this._textScene.once('text-processed', () => {
				this._applyStateDelta(result.battleResults[index]);
				this._evaluateBattleResult(result, index+1);
			}, this);
			
			this._textScene.once('text-advance', () => {
				if (battler.isPlayer)
					this.moveCameraPlayer(battler.normalizedIndex, 1, 0);
				else
					this.moveCameraOpponent(battler.normalizedIndex, 1, 0);
			}, this);
			
			const len = result.battleResults[index].sentences.length;
			for (let i = 0; i < len; ++i) {
				let sentence = result.battleResults[index].sentences[i];
				if (i < len - 1)
					sentence += "&";
				this._textScene.pushText(sentence);					
			}
		}
		
		_applyStateDelta(battleResult) {
			battleResult.stateDeltas.forEach(stateDelta => {
				const battler = this._battlerById(stateDelta.characterState);
				switch (stateDelta.type) {
				case Lunar.StatusDeltaType.battleStatus:
					switch (stateDelta.value) {
					case Lunar.StatusValue.hp:
						this.game.sfx('resources/translune/static/battle/hpdown', 1);
						battler.moveHpRatio(stateDelta.after/Lunar.Constants.hpRatioDenominator);
						if (battler.dead)
							this._animateKo(battler);
					}
					break;
				}
			});
		}
		
		_animateKo(battler) {
			const scene = new Lunar.Scene.BattleKo(this, battler);
			scene.once('action-selected', command => this._removeChildScene(scene));
			this._pushChildScene(scene);
		}
		
		_battlerById(id) {
			return this._battlers.find(battler => battler.characterState.id === id);
		}
	}
})(window.Lunar, window);