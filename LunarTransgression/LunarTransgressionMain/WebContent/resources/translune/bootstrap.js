(function(Lunar) {
	const net = new Lunar.Net(window);
	const game = new Lunar.Game(window, net);
	try {
		game.start();
	}
	catch (e) {
		console.error(e);
		alert("Failed to start game: " + e);
		net.finalize();
	}
})(window.Lunar);