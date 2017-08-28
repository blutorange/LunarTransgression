class TransluneNet {
	constructor(window) {
		const _this = this;
		this.window = window;
		const query = querystring.parse(window.location.search);
		const initId = query.initId;
		const nickname = query.nickname;
		const wsEndpoint = query.wsEndpoint;
		const base = query.base;
		if (!initId)
			throw new TypeError("No init ID given.");
		if (!nickname)
			throw new TypeError("No nickname given.");
		if (!wsEndpoint)
			throw new TypeError("No websocket endpoint given.");
		if (!base)
			throw new TypeError("No base given.");
		this.reqResp = {};
		this.initId = initId;
		this.nickname = nickname;
		this.base = base;
		this.wsEndpoint = ((window.location.protocol === "https:") ? "wss://" : "ws://") + window.location.host + base +wsEndpoint
		this._httpBase = window.location.protocol + "//" + window.location.host + base;
		this.time = 0;
		this.serverTime = 0;
		this.messageHandlers = [];
		this.messageQueue = new PriorityQueue();
		this.waitingForAuthorization = 0;
		this.connectQueue = [];
		this.connectListener = [];
		this.finalizeListener = [];
		this.finalized = false;
		this.messageQueueJob = window.setInterval(() => _this._processMessageQueue(), Lunar.Constants.queueInterval*1000);
	}
	
	get httpBase() {
		return this._httpBase;
	}
	
	/**
	 * @param {string}
	 *            type
	 * @param {IObject
	 *            <string, string>} payload
	 * @return {Promise} A promise resolved when either the request succeed,
	 *         fails, or times out. The callback function receives an object
	 *         with the following three entries. status {int} - The status code
	 *         of the response. type {string} - The type of message that was
	 *         received. data {IObject<String, ?>} - The payload of the the
	 *         response.
	 */
	dispatchMessage(type, payload) {
		const _this = this;
		const time = _this.time++;
		return new Promise((resolve, reject) => {
			const timeout = _this.window.setTimeout(() => {
				const reqResp = _this.reqResp[time];
				delete _this.reqResp[time];
				window.console.warn("request timed out", time);
				reject({
					status: Lunar.Status.GENERIC_ERROR,
					type: Lunar.Message.unknown,
					data: {
					}
				});
			}, Lunar.Constants.reponseTimeout*1000);
			_this._connect(websocket => {
				_this.reqResp[time] = {
					started: new Date(),
					resolve: resolve,
					reject: reject,
					timeout: timeout
				};
				websocket.send(JSON.stringify({
					time: time,
					type: type,
					status: Lunar.Status.OK,
					payload: JSON.stringify(payload)
				}));
			});
		});
	}
	
	/**
	 * @private
	 */
	_authorize() {
		const _this = this;
		this.websocket.send(JSON.stringify({
			type: Lunar.Message.authorize,
			status: Lunar.Status.OK,
			time: _this.time++,
			payload: JSON.stringify({
				nickname: _this.nickname,
				initId: _this.initId
			})
		}));
	}
	
	/**
	 * Connects and calls all connect listeners on connect.
	 */
	connect() {
		this._connect();
	}

	/**
	 * Call this when done to close all open connections.
	 */
	finalize() {
		if (this.finalized)
			return;
		this.finalized = true;
		const websocket = this.websocket;
		this.messageHandlers = {};
		this.connectListener = [];
		if (websocket && (websocket.readyState === WebSocket.OPEN || websocket.readyState === WebSocket.CONNECTING))
			websocket.close(1000, "game done");
		this.window.clearTimeout(this.messageQueueJob);
		for (let listener of this.finalizeListener)
			listener();
		this.finalizeListener = [];
	}
	
	/**
	 * @private
	 */
	_connect(whenOpen) {
		const _this = this;
		
		if (this.finalized) {
			console.warn("net closed, cannot connect anymore");
			return;
		}
		if (this.websocket && this.websocket.readyState ===  WebSocket.OPEN) {
			whenOpen(this.websocket);
			return;
		}
		
		if (whenOpen)
			this.connectQueue.push(whenOpen);
		
		const websocket = new WebSocket(this.wsEndpoint); 
      
		websocket.onopen = () => {
			this._authorize();
		}
      
		websocket.onmessage = evt =>  {
			let data = null;
			let payload = null;
			try {
				data = JSON.parse(evt.data);
				payload = JSON.parse(data.payload || "{}");
			}
			catch (e) {
				console.error("could not parse message received", e);
				return;
			}
			const type = payload.type;
			const status = data.status;
			const origin = payload.origin;
			const serverTime = data.time;
			// Handle authorization.
			if (_this.waitingForAuthorization >= 0) {
				this.serverTime = serverTime + 1;
				if (status === Lunar.Status.OK) {
					_this.waitingForAuthorization = -1;
					const cq = Array.from(_this.connectQueue);
					this.connectQueue = [];
					for (let cl of _this.connectListener)
						cl();
					for (let wo of cq)
						wo(websocket);
				}
				else {
					if (_this.waitingForAuthorization > 10) {
						console.error("failed to authorize session");
						_this.finalize();
					}
					_this.waitingForAuthorization++;
					_this._authorize();
				}
				return;
			}
			// Message is a response to some earlier request by us.
			if (origin) {
				const reqResp = _this.reqResp[origin];
				delete _this.reqResp[origin];
				if (reqResp)
					_this.window.clearTimeout(reqResp.timeout);
				_this.messageQueue.push({
					resolve: () => {
						if (reqResp) {
							if (status === Lunar.Status.OK) {
								reqResp.resolve({
									status: status,
									type: type,
									data: payload
								});
							}
							else {
								reqResp.reject({
									status: status,
									type: type,
									data: payload
								});
							}
						}
						else {
							_this.window.console.info("received response, but did not find corresponding request", origin);
						}						
					},
					reject: () => {
						if (reqResp) {
							reqResp.reject({
								status: Lunar.Status.TIMEOUT,
								type: type,
								data: payload
							});
						}
						else {
							_this.window.console.info("received response, but did not find corresponding request", origin);
						}
					},
					serverTime: serverTime,
					start: new Date().getTime()
				}, serverTime);
				return;
			}
			// Message initiated by the server
			_this.messageQueue.push({
				resolve: () => {
					const handlers = _this.messageHandlers[type];
					if (!handlers || handlers.length === 0) {
						window.console.warn(`received message of ${type}, but no handlers were registered`);
						return;
					}
					for (let handler of handlers) {
						if (status === Lunar.Status.OK)  {
							handler.handle({
								status: status,
								type: type,
								data: payload
							});
						}
						else {
							handler.error(status);
						}
					}
				},
				reject: () => {
					handler.error(Lunar.Status.TIMEOUT)
				},
				serverTime: serverTime,
				start: new Date().getTime()
			}, serverTime);
		};
      
		websocket.onclose = () => {
			window.console.info("connection closed");
			_this.websocket = null;
		};

		websocket.onerror = evt => {
			console.error("error during server communication", evt);
		};
      
		this.websocket = websocket;
	}
	
	/**
	 * @private
	 */
	_processMessageQueue() {
		const message = this.messageQueue.peek();
		if (!message)
			return;
		const threshold = new Date().getTime() - Lunar.Constants.queueTimeout*1000;
		if (message.start < threshold) {
			this.serverTime = message.serverTime;
		}
		if (message.serverTime < this.serverTime) {
			this.serverTime = message.serverTime + 1;			
			messageQueue.pop().reject();
			this._processMessageQueue();
			return;
		}
		if (message.serverTime === this.serverTime) {
			this.serverTime++;
			this.messageQueue.pop().resolve();
			this._processMessageQueue();
			return;
		}
	}
	
	/**
	 * @param {string}
	 *            type The type of message this handler handles.
	 * @param {handle:
	 *            function({status:number,type:string,data:Object})} handler The
	 *            handler for this type of message.
	 */
	registerMessageHandler(type, handler) {
		if (!this.messageHandlers)
			this.messageHandlers = {};
		this.message.push(handler);
	}
	
	registerConnectListener(listener) {
		this.connectListener.push(listener);
	}
	
	registerFinalizeListener(listener) {
		this.finalizeListener.push(listener);
	}
}