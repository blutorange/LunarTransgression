//g=()=>{a=Lunar.instance.scenes[0];window.keep=true;d=0;return f=()=>{a.positionBattlers(d+=2.4);a.positionBackground(d);if(window.keep)window.setTimeout(f, 20)}}
(function(Lunar, window) {
	const net = new Lunar.Net(window);
	const game = new Lunar.Game(window, net);
	Lunar.instance = game;
	try {
		game.start();
		if (game.debug)
			doDebugStuff();
	}
	catch (e) {
		console.error(e);
		alert("Failed to start game: " + e);
		net.finalize();
	}
	
	function doDebugStuff() {
		function debug1() {
		    hardcore(() => {
		        Lunar.instance.scenes[0]._onClickInvite();
		        hardcore(() => {
		            Lunar.instance.scenes[1].hierarchy.list[1].$text.emit('pointertap');
		            hardcore(() => {
		                if (!Lunar.instance.scenes[0].actionButton1.visible)
		                    throw new Error("no invite button");
		                Lunar.instance.scenes[0].actionButton1.emit('pointertap');
		                hardcore(() => {
		                    Lunar.instance.scenes[5]._onClickBattle();
		                });
		            });
		        });
		    });
		}

		function debug2() {
		    hardcore(() => {
		        if (Lunar.instance.scenes[0]._invitations <= 0)
		            throw new Error("no invitations");
		        Lunar.instance.scenes[0]._onClickBattle();
		        hardcore(() => {
		            Lunar.instance.scenes[2]._onClickAccept();
		            hardcore(() => {
		                Lunar.instance.scenes[2]._onClickBattle();
		            });
		        });
		    });
		}

		function hardcore(runnable, limit = 40) {
			window.setTimeout(() => {
			    try {
			        runnable();
			    }
			    catch (e) {
			        if (limit < 0) {
			            console.error("!!failed to be hard!!!", e);
			            return;
			        }
			        window.setTimeout(() => hardcore(runnable, limit - 1), 300);
			    }				
			}, 300);
		}

		if (document.webkitCancelFullScreen)
		    debug1();
		else
		    debug2();		
	}
	
})(window.Lunar, window);