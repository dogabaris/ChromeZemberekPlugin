function MetinAl(event) {
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
	        event.data.sonuc.txt = msg;
	      }).error(function () {
	        event.data.sonuc.txt = "Hata!";
	      });

	});

	$(document).ajaxStop(function () {
	    document.getElementById("sonuctext").innerHTML = event.data.sonuc.txt;
	});
}
function SayfaFrekansListele(event) {
	highlights = "";
	$('#SayfaYuksekFrekanslilar').empty();
	$.ajax({ 
	    type: 'GET', 
	    url: 'http://localhost:666/listeYenile', 
	    data: {}, 
	    dataType: "text json",
	    contentType: "application/json; charset=utf-8", 
	    }).success(function(data) {
	    	sayfaFreq=data;
	    	var alldata = "";
	    	//var highlights = "";
			console.log(JSON.stringify(data));
			//$('#SayfaFrekansListele').append(JSON.stringify(data));

			for(var i=0;i< data.length;i++)
            {
                alldata += "<li>" + data[i][0] + " - " + data[i][1] + "</li><hr>";
            }	
           
            $('#SayfaYuksekFrekanslilar').append(alldata);

            for(var i=0;i< data.length;i++)
            {
            	highlights += data[i][0] + " ";
            }

            console.log(highlights);
	    });
}
function VeritabaniFrekansListele(event) {
	$('#SayfaYuksekFrekanslilar').empty();
	$.ajax({ 
	    type: 'GET', 
	    url: 'http://localhost:666/veritabanilisteYenile', 
	    data: {}, 
	    dataType: "text json",
	    contentType: "application/json; charset=utf-8", 
	    }).success(function(data) {
	    	
	    	var alldata = "";
			console.log(JSON.stringify(data));
			//$('#SayfaFrekansListele').append(JSON.stringify(data));

			for(var i=0;i< data.length;i++)
            {
                    alldata += "<li>" + data[i][0] + " - " + data[i][1] + "</li><hr>";
            }	
           
            $('#SayfaYuksekFrekanslilar').append(alldata);
            console.log(alldata);
	    });
}
function AltiniCiz(event){
	/*document.addEventListener("DOMContentLoaded", function() {
	    myHilitor = new Hilitor2("playground");
	    myHilitor.setMatchType("left");
	  }, false);
	*/
	chrome.tabs.executeScript({
		code: "var myHilitor = new Hilitor2(); myHilitor.setMatchType(\"left\"); myHilitor.apply(\""+highlights+"\");"
	});
}

$(document).ready(function() {
 	var resp = {txt:""};
  	$("#metinAlButton").click({sonuc: resp},MetinAl);
  	$("#listeYenile").click(SayfaFrekansListele);
  	$("#veritabanilisteYenile").click(VeritabaniFrekansListele);
  	$("#highlightBtn").click(AltiniCiz);
});
