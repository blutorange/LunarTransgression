const gameDiv = document.getElementById('game_div');
const transluneNet = new TransluneNet(window);
const transluneGame = new TransluneGame(window, gameDiv, transluneNet);
try {
	transluneGame.start();
}
catch (e) {
	console.error(e);
	alert("Failed to start game: " + e);
	transluneNet.finalize();
}