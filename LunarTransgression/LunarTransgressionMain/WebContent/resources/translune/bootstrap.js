try {
	let gameDiv = document.getElementById('game_div');
	let translune = new Translune(window, gameDiv);
	translune.start();
}
catch (e) {
	console.error(e);
	alert("Failed to start game: " + e);
}