/**
 * 
 */
(function(Lunar, window, undefined) {
	// TODO reset this back to sane values, eg. x=2, y=7
	const LIST_X = 2;
	const LIST_Y = 8;
	
	Lunar.Scene.MenuInvite= class extends Lunar.Scene.Base {
		constructor(game, menu) {
			super(game);
			this._menu = menu;
			this._loadScene = undefined;
			this._scenePager = undefined;
			this._pageableResult = undefined;
			this._searchTimeout = undefined;
		}
		
		destroy() {
			this._loadScene = undefined;
			this._menu = undefined;
			this._scenePager = undefined;
			this._pageableResult = undefined;
			this._searchTimeout = undefined;
			super.destroy();
		}
		
		onAdd() {
			this._initScene();
			super.onAdd();
			this.game.pushScene(this._scenePager, this.hierarchy.$pager);
		}
		
		onRemove() {
			if (this._searchTimeout)
				window.clearTimeout(this._searchTimeout);
			this._searchTimeout = undefined;
			this.game.removeScene(this._scenePager);
			this.game.removeScene(this._loadScene);
			super.onRemove();
		}
		
		layout() {			
			super.layout();

			const h = this.hierarchy;
			const panel = this.view.parent.parent;

			const geoBase = Lunar.Geometry.layoutVbox({
				box: panel.bodyDimension,
				dimension: [12,73,15],
				padding: {
					top: 0.015,
					y: 0.015
				},
				relative: true
			});
			
			const geoField = Lunar.Geometry.layoutHbox({
				box: geoBase[0],
				dimension: 1,
				padding: {
					right: 8,
					left: 8,
				},
				relative: true
			});
			
			const geoSearch = Lunar.Geometry.layoutHbox({
				box: geoField[0],
				dimension: [90,10],
				padding: {
					top: 0.05,
					bottom: 0.05,
					left: 0.05,
					right: 0.02
				},
				relative: true
			});
			
			const geoList = Lunar.Geometry.layoutGrid({
				box: geoBase[1],
				dimension: {
					n: LIST_X,
					m: LIST_Y
				},
				relative: true
			}).flatten();
			
			h.search.$graphics.clear();
			h.search.$graphics.beginFill(0x111111, 1);
			h.search.$graphics.drawRoundedRect(geoField[0].x, geoField[0].y, geoField[0].w, geoField[0].h, this.game.dx(0.008));
			h.search.$graphics.endFill();
			
			this.geo(h.$search, geoBase[0]);
			this.geo(h.$list, geoBase[1]);
			this.geo(h.$pager, geoBase[2]);
			
			this.geo(h.search.$field, geoField[0]);
						
			this.geo(h.search.field.$input, geoSearch[0]);
			this.geo(h.search.field.$reset, geoSearch[1], {proportional: true, anchor: [1, 0.5]});
			
			h.list.forEach((element, index) =>
				this.geo(element.$text, geoList[index], {keepSize: true, anchor: 0.5})
			);
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
			
			const containerSearch = new PIXI.ClipContainer();
			const containerList = new PIXI.ClipContainer();	
			const containerPager = new PIXI.ClipContainer();
			const containerField = new PIXI.ClipContainer();

			const inputSearch = new PIXI.TextInput(Lunar.FontStyle.playerSearch, ['input', 'blur']);
			const graphicsSearch = new PIXI.Graphics();
			const iconReset = this._createIconReset();
			inputSearch.placeholder = 'search...';
			inputSearch.on('input', this._onChangeSearch, this);
			inputSearch.on('blur', this._onBlurSearch, this);
			
			this.view.addChild(containerSearch);
			this.view.addChild(containerList);
			this.view.addChild(containerPager);
			
			containerSearch.addChild(graphicsSearch);
			containerSearch.addChild(containerField);
			
			containerField.addChild(inputSearch);
			containerField.addChild(iconReset);

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
				search: {
					field: {
						$input: inputSearch,
						$reset: iconReset
					},
					$graphics: graphicsSearch,
					$field: containerField,
				},
				list: elementPlayer, // [{$text: text}, ...]
				$search: containerSearch,
				$list: containerList,
				$pager: containerPager
			};			
		}
		
		_createIconReset() {
			const iconReset = new PIXI.Sprite(this.game.baseLoader.resources.packed.textures['clear.png']);
			iconReset.interactive = true;
			iconReset.buttonMode = true;
			iconReset.on('pointerover', () => {
				iconReset.width = iconReset.width*9/8;
				iconReset.height = iconReset.height*9/8;
				iconReset.rotation = 0.2;
			});
			iconReset.on('pointerout', () => {
				iconReset.width = iconReset.width*8/9;
				iconReset.height = iconReset.height*8/9;
				iconReset.rotation = 0.0;
			});
			iconReset.on('pointertap', this._onClickClear, this);
			return iconReset;
		}
		
		_createTextPlayer(callback) {
			const text = new PIXI.Text('', Lunar.FontStyle.playerList);
			text.interactive = true;
			text.buttonMode = true;
			text.visible = false;
			text.on('pointerover', () => {
				text.width = text.width * 6/5,
				text.height = text.height * 6/5
				text.style = Lunar.FontStyle.playerListActive;
			});
			text.on('pointerout', () => {
				text.width = text.width * 5/6,
				text.height = text.height * 5/6
				text.style = Lunar.FontStyle.playerList;
			});
			text.on('pointertap', callback, this);
			return text;
		}

		_onBlurSearch() {
			const _this = this;
			const subject = this.hierarchy.search.field.$input.text;
			if (this._searchTimeout) {
				this._searchTimeout = undefined;
				window.clearTimeout(this._searchTimeout);
				_this._reloadPage();
			}
		}
		
		_onChangeSearch() {
			const _this = this;
			const subject = this.hierarchy.search.field.$input.text;
			if (this._searchTimeout)
				window.clearTimeout(this._searchTimeout);
			this._searchTimeout = window.setTimeout(() => {
				this._searchTimeout = undefined;
				_this._reloadPage();
			}, 2000);
		}

		_onClickClear() {
			if (this.hierarchy.search.field.$input.text) {
				this.game.sfx('resources/translune/static/cancel');
				this.hierarchy.search.field.$input.text = '';
				if (this._searchTimeout) {
					window.clearTimeout(this._searchTimeout);
					this._searchTimeout = undefined;
				}
				this._reloadPage();
			}
			else {
				this.game.sfx('resources/translune/static/unable');
			}
		}
		
		_onClickPlayer(index) {
			if (!this._pageableResult) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			const nickname = this._pageableResult.list[index];
			if (!nickname) {
				this.game.sfx('resources/translune/static/unable');
				return;
			}
			this.game.sfx('resources/translune/static/buttonclick');
			this.menu._onSelectInvite(nickname);
		}
		
		_applyPage() {
			this.hierarchy.list.forEach((element, index) => {
				const player = this._pageableResult.list[index];
				if (player) {
					element.$text.text = player;
					element.$text.visible = true;
				}
				else {
					element.$text.visible = false;
				}
			});
			this._scenePager.pageCount = this._pageableResult.filteredTotal/(LIST_X*LIST_Y);
		}
		
		_reloadPage() {
			if (!this._scenePager)
				return;
			if (this._loadScene)
				return;
			const _this = this;
			const details = {
				offset: this._scenePager.page * LIST_X * LIST_Y, //TODO
				count: LIST_X * LIST_Y,
				orderBy: [{
					name: 'nickname',
					direction: 'ASC'
				}],
			};
			if (this.hierarchy.search.field.$input.text) {
				details.filter = [{
					lhs: 'nickname',
					operator: 'like',
					rhs: this.hierarchy.search.field.$input.text
				}];
			}
			const requestLoadable = new Lunar.RequestLoadable(this.game, Lunar.Message.fetchData, {
				fetch: Lunar.FetchType.activePlayer,
				details: JSON.stringify(details)
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
		
		_onPageChange() {
			this._reloadPage();
		}
	}
})(window.Lunar, window);