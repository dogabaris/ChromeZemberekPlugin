
function sendReq(event) {
	chrome.tabs.executeScript( {
	    code: "window.getSelection().toString();"
	}, function(selection) {
	 
	    	var text = {"text": selection[0]};
		    $.ajax({
	        method: "POST",
	        url: "http://localhost:666/MetinAl",
	        data: JSON.stringify(text),
	        contentType: "application/json; charset=utf-8",
	      }).done(function (msg) {
	        event.data.param1.txt = msg;
	        //alert(resp)
	      }).error(function () {
	        event.data.param1.txt = "error";
	        //alert(resp)
	      });

	});

	 $(document).ajaxStop(function () {
	    document.getElementById("output").innerHTML = event.data.param1.txt;
	 });
}

$(document).ready(function() {
  var resp = {txt:""};
  $("#metinAlButton").click({param1: resp},sendReq);
});