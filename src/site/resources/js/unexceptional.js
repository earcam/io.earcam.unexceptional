function pronounce(oxfordMp3)
{
	var audio = document.createElement('audio');
	
	audio.src = '//ssl.gstatic.com/dictionary/static/sounds/oxford/' + oxfordMp3;
//	audio.id = 'audiooo';
	audio.preload = 'auto';
	audio.display = 'none';
	document.body.appendChild(audio);
	audio.play();
}
