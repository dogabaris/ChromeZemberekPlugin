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
/*function SayfaFrekansListele(event) {
	$.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: "http://localhost:666/listeYenile",
            data: "{}",
            dataType: "json",
            success: function (data) {
                alert(data);
            },
            error: function (result) {
                alert("Error");
            }
        });
}*/
function SayfaFrekansListele(event) {
	$.ajax({ 
	    type: 'GET', 
	    url: 'http://localhost:666/listeYenile', 
	    data: {}, 
	    dataType: "text json",
	    contentType: "application/json; charset=utf-8", 
	    }).success(function(data) {
	    	var alldata = "";
			console.log(JSON.stringify(data));
			//$('#SayfaFrekansListele').append(JSON.stringify(data));

			for(var i=0;i< data.length;i++)
            {
                    alldata += "<li>" + data[i] + "<li><hr>";
            }	
            //$('#SayfaFrekansListele').html(alldata);
            $('#SayfaYuksekFrekanslilar').append(alldata);
            console.log(alldata);
	        //event.data.donus.txt = "Başarılı!";
	    });


	/*$(document).ajaxStop(function () {
	    document.getElementById("SayfaFrekansListele").innerHTML = event.data.donus.txt;
	});*/
	
}

$(document).ready(function() {
 	var resp = {txt:""};
  	$("#metinAlButton").click({sonuc: resp},MetinAl);
});
$(document).ready(function() {
	var donus = {txt:""};
	$("#listeYenile").click({sonuc: donus},SayfaFrekansListele);
});
// $(document).ready(function() {
//     $("#listeYenile").click(function() {                

//       $.ajax({    //create an ajax request to load_page.php
//         type: "GET",
//         url: "http://localhost:666/listeYenile",            
//         data: JSON.stringify(text),   //expect html to be returned
//         contentType: "application/json; charset=utf-8",                
//         success: function(response){                    
//             $("#responsecontainer").html(response); 
//             //alert(response);
//         }

//     });
// });

// /*$(document).ready(function() {
//     $('#listeYenile').submit(function(event) {
//     event.preventDefault();
//         var formData = $('#listeYenile').serialize();    /* capture the form data*/
//         $.getJSON('http://localhost:666/listeYenile', formData, registerResults);
//    //     $.post('/admin/user/signup', formData, registerResults);   /* get JSON back from the post method */
//     });
//     function registerResults(data) {
//             //$('#responsecontainer').fadeOut();
//             $('#responsecontainer').html(data.message);
//         }  // end of registerResults
// });
