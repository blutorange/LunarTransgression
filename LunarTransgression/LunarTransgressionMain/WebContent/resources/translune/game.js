/*
- 1 Loader/Scene

Scene menu
 Resources:
   - 20 poke icons
   - 30 item icons
   - Menu BGM music
   - other UI elements

Scene opponent select
 Resources:
   
Scene battlePreparation
 Resources:
   - 2 * 20 poke icons (+opponent)
   - 30 item icons 
   - Prep BGM music
   - other UI elements

Scene battle
 Resources:
   - 2players * 2*backFront * 4 poke images
   - battleBackground
   - 2*4 poke cries
   - Battle BGM music
   - other UI elements
*/

class TransluneGame {
	/**
	 * @param {window} window
	 * @param {DOMElement} gameDiv
	 * @param {TransluneNet} net
	 */
	constructor(window, gameDiv, net) {
		this.gameDiv = gameDiv;
		this.net = net;
		this.window = window;
	}
	
	start() {
		this.net.registerConnectListener(this.onConnect.bind(this));
		this.net.registerFinalizeListener(this.onFinalize.bind(this));
		this.net.connect();
	}
	
	onConnect() {
		const _this = this;
		_this.net.dispatchMessage(Lunar.Message.fetchData, {
			fetch: Lunar.FetchType.userPlayer		
		}).then(response => {
			console.log(response.data);
		}).catch(() => {
			console.log("an error occured")
		}).then(() => {
			console.log("done1");
		});
		_this.net.dispatchMessage(Lunar.Message.fetchData, {
			fetch: Lunar.FetchType.userPlayer		
		}).then(response => {
			console.log(response.data);
		}).catch(() => {
			console.log("an error occured")
		}).then(() => {
			console.log("done2");
			_this.window.setTimeout(() => _this.net.finalize(), 5000);
		});
	}
	
	onFinalize() {
		this.window.alert("Network closed, ending game.");
		// TODO Uncomment this again
		// this.window.close();
	}
	
	static startGame(url) {
		var a = document.createElement("a");
		a.href = url;
		a.target = "_blank";
		a.setAttribute("style","display:hidden;");
		document.body.appendChild(a);
		a.click();
		document.body.removeChild(a);
	}
}