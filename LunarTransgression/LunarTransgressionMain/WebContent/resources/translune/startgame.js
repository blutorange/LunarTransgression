window.Lunar = window.Lunar || {};
window.Lunar.startGame = function(url) {
	var a = document.createElement("a");
	a.href = url;
	a.target = "_blank";
	a.setAttribute("style","display:hidden;");
	document.body.appendChild(a);
	a.click();
	document.body.removeChild(a);
};