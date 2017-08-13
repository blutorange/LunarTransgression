class Translune {
	constructor(gameDiv) {
		let query = querystring.parse(window.location.search);
		let initId = query.initId;
		let wsEndpoint = query.wsEndpoint;
		if (!initId)
			throw new TypeError("No init ID given.");
		if (!wsEndpoint)
			throw new TypeError("No websocket endpoint given.");
		this.gameDiv = gameDiv;
		this.initId = initId;
		this.wsEndpoint = wsEndpoint;
	}
	
	start() {
		connect();
		initUi();
		this.websocket.close(1000, "closing");
	}
	
	initUi() {
		
	}
	
	connect() {
		  let websocket = new WebSocket(wsEndpoint); 
          
	      websocket.onopen = () => {
	        
	      }          
	      
	      websocket.onmessage = evt =>  {
	    	  evt.data;
	      };
	      
	      websocket.onclose = () => {
	    	  alert("Connection closed, please try again");
	    	  window.close();
	      };

	      websocket.onerror = evt => {
	    	  console.error("error during server communication", evt);
	      };
	      
	      this.websocket = websocket;
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