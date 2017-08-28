const transluneNet = new TransluneNet(window);
const transluneGame = new TransluneGame(window, transluneNet);
try {
	transluneGame.start();
}
catch (e) {
	console.error(e);
	alert("Failed to start game: " + e);
	transluneNet.finalize();
}