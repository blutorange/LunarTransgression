// TODO handle returned time, must be in order

/**
 * @const {IObject<string, number>}
 */
const LunarStatus = {
	OK: 0,
	GENERIC_ERROR: 20,
	ACCESS_DENIED: 21,
};

/**
 * @const {IObject<string, string>}
 */
const LunarMessage = {
	AUTHORIZE: 'AUTHORIZE',
};

/**
 * @const {IObject<string, ?>}
 */
const LunarConstants = {
	RESPONSE_TIMEOUT: 10 // seconds 	
};

class Translune {
	constructor(window, gameDiv) {
		let query = querystring.parse(window.location.search);
		let initId = query.initId;
		let nickname = query.nickname;
		let wsEndpoint = query.wsEndpoint;
		if (!initId)
			throw new TypeError("No init ID given.");
		if (!nickname)
			throw new TypeError("No nickname given.");
		if (!wsEndpoint)
			throw new TypeError("No websocket endpoint given.");
		this.reqResp = {};
		this.gameDiv = gameDiv;
		this.initId = initId;
		this.nickname = nickname;
		this.wsEndpoint = wsEndpoint;
		this.time = 0;
		this.window = window;
	}
	
	start() {
		connect();
		initUi();
		this.websocket.close(1000, "closing");
	}
	
	initUi() {
		
	}
	
	/**
	 * @param {string} type
	 * @param {IObject<string, string>} payload
	 * @return {Promise} A promise resolved when either the request succeed, fails, or times out.
	 * The callback function receives an object with the following three entries.
	 * status {int} - The status code of the response.
	 * type {string} - The type of message that was received.
	 * data {IObject<String, ?>} - The payload of the the response.   
	 */
	dispatchMessage(type, payload) {
		let time = this.time++;
		return new Promise((resolve, reject) => {
			this.reqResp[time] = {
				started: new Date(),
				resolve: resolve,
				reject: reject,
				timeout: this.window.setTimeout(() => {
					let reqResp = reqResp[time];
					delete reqResp[time];
					if (reqResp) {
						console.warn("request timed out", time);
						reqResp.reject({
							status: LunarStatus.GENERIC_ERROR,
							type: LunarMessage.UNKNOWN,
							data: {
								
							}
						});
					}
				}, LunarConstants.RESPONSE_TIMEOUT)
			};
			this.websocket.send({
				time: time,
				type: type,
				status: LunarStatus.OK,
				payload: payload
			});
		});
	}
	
	authorize() {
		this.websocket.send({
			type: LunarMessage.AUTHORIZE,
			payload: {
				nickname: this.nickname,
				initId: this.initId
			}
		});
	}
	
	connect() {
		  let websocket = new WebSocket(wsEndpoint); 
          
	      websocket.onopen = () => {
	        
	      }          
	      
	      websocket.onmessage = evt =>  {
	    	  let data = evt.data;
	    	  let payload = data.payload || {};
	    	  let type = payload.type;
	    	  let status = data.status;
	    	  let origin = payload.origin;
	    	  if (origin) {
	    		  // Message is a response to some earlier request.
	    		  let reqResp = this.reqResp[origin];
	    		  delete this.reqResp[origin];
	    		  if (reqResp) {
	    			  this.window.clearTimeout(reqResp.timeout);
	    			  if (type === LunarStatus.OK) 
	    				  reqResp.resolve({
	    					  status: status,
	    					  type: type,
	    					  data: payload
	    				  });
	    			  else 
	    				  reqResp.reject({
	    					  status: status,
	    					  type: type,
	    					  data: payload
	    				  });
	    		  }
	    		  else {
	    			  console.warn("received response, but did not find corresponding request", origin);
	    		  }
	    		  return;
	    	  }
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