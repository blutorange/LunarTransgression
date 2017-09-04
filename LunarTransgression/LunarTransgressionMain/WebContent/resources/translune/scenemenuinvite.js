/**
 * 
 */
(function(Lunar, window, undefined) {
	// TODO reset this back to sane values, eg. x=2, y=7
	const LIST_X = 1;
	const LIST_Y = 1;
	
	Lunar.Scene.MenuInvite= class extends Lunar.Scene.Base {
		constructor(game, menu) {
			super(game);
			this._menu = menu;
			this._loadScene = undefined;
			this._scenePager = undefined;
			this._pageableResult = undefined;
		}
		
		destroy() {
			this._loadScene = undefined;
			this._menu = undefined;
			this._scenePager = undefined;
			this._pageableResult = undefined;
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
			this.game.pushScene(this._scenePager, this.hierarchy.$pager);
		}
		
		onRemove() {
			this.game.removeScene(this._scenePager);
			this.game.removeScene(this._loadScene);
			super.onRemove();
		}
		
		layout() {
			const h = this.hierarchy;
			const panel = this.view.parent.parent;

			const geoPage = Lunar.Geometry.layoutVbox({
				box: panel.bodyDimension,
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
			
			
			this.geo(h.$list, geoPage[0])
			this.geo(h.$pager, geoPage[1]);
			h.list.forEach((element, index) =>
				this.geo(element.$text, geoList[index], {keepSize: true, anchor: 0.5})
			);
			
			super.layout();
		}
		
		get menu() {
			return this._menu;
		}

		/**
		 * @private
		 */
		_initScene() {
			this._scenePager = new Lunar.Scene.Pager(this.game, {
				style: Lunar.FontStyle.control,
				prev: this.game.baseLoader.resources.packed.textures['prev.png'],
				next: this.game.baseLoader.resources.packed.textures['next.png'],
				pageCount: 1
			});
			
			this._scenePager.on('page-change', this._onPageChange, this);
			
			const containerList = new PIXI.ClipContainer();	
			const containerPager = new PIXI.ClipContainer();
			
			this.view.addChild(containerList);
			this.view.addChild(containerPager);
			
			const elementPlayer = [];
			for (let j = 0; j < LIST_Y; ++j) {
				for (let i = 0; i < LIST_X; ++i) {
					const textPlayer = this._createTextPlayer(() => this._onClickPlayer(j*LIST_X+i));
					elementPlayer.push({
						$text: textPlayer
					});
					containerList.addChild(textPlayer);
				}
			}
			
			this.hierarchy = {
				list: elementPlayer, // [{$text: text}, ...]
				$list: containerList,
				$pager: containerPager
			};			
		}

		_onClickPlayer(index) {
			if (!this._scenePager) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/buttonclick');
			// TODO Show details
//			const skill = this._getPagedSkill(index);
//			this._selectSkill(skill);
		}
		
		_createTextPlayer(callback) {
			const text = new PIXI.Text('', Lunar.FontStyle.playerList);
			text.interactive = true;
			text.buttonMode = true;
			text.visible = false;
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
		
		_applyPage() {
			this.hierarchy.list.forEach((element, index) => {
				const player = this._pageableResult.list[index];
				if (player) {
					element.$text.text = player;
					element.$text.visible = true;
				}
				else
					element.$text.visible = false;
			});
			this._scenePager.pageCount = this._pageableResult.filteredTotal/(LIST_X*LIST_Y);
		}
		
		_onPageChange() {
			if (!this._scenePager)
				return;
			if (this._loadScene)
				return;
			const _this = this;
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				fetch: Lunar.FetchType.activeOpponent,
				details: JSON.stringify({
					offset: this._scenePager.page * LIST_X * LIST_Y, //TODO
					count: LIST_X * LIST_Y,
					orderBy: [{
						name: 'nickname',
						direction: 'ASC'
					}]
				})
			});
			const loadScene = new Lunar.Scene.Load(this.game, requestLoadable);
			requestLoadable.promise
				.then(response => _this._pageableResult = response.data.data)
				.catch(response =>  {
					_this.game.removeScene(_this._scenePager);
					_this.game.removeScene(_this._loadScene);					
					_this.game.removeScene(_this);
					_this.showConfirmDialog("Could not load opponent data, please try again later.");
				});
			requestLoadable.addCompletionListener(() => {
				_this._loadScene = undefined;
				_this._applyPage();
			});
			
			this.game.pushScene(this._loadScene = loadScene);
		}
	}
})(window.Lunar, window);