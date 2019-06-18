$(document).ready(function() {
  $('#orderStateFill').val("");
  $('#companyFill').val("");
  $('#locationFill').val("");
  $('#dateFromFill').val("");
  $('#dateToFill').val("");
  $('#teuFill').val("");
  $('#transportFill').val("");

});

var book;
var weight;

function chooseCheckBox() {
  if ($("#identificationCheck").is(":checked")) {
    document.getElementById("identification").style.display = "block";
    document.getElementById("identificationButton").style.display = "inline";
  }
  if ($("#companyCheck").is(":checked")) {
    if($("#companyNameCheck").is(":checked")) {
      document.getElementById("companyName").style.display = "block";
      document.getElementById("companyId").style.display = "none";
      document.getElementById("companyScac").style.display = "none";
    } else if ($("#companyIdCheck").is(":checked")) {
      document.getElementById("companyName").style.display = "none";
      document.getElementById("companyId").style.display = "block";
      document.getElementById("companyScac").style.display = "none";
    } else {
      document.getElementById("companyName").style.display = "none";
      document.getElementById("companyId").style.display = "none";
      document.getElementById("companyScac").style.display = "block";
    }
	$('#companyNameFill').val('');
    $('#companyIdFill').val('');
    $('#companyScacFill').val('');
    document.getElementById("companyButton").style.display = "inline";
  }
  if ($("#locationCheck").is(":checked")) {
    document.getElementById("location").style.display = "block";
    document.getElementById("locationButton").style.display = "inline";
  }
  if ($("#orderStateCheck").is(":checked")) {
    document.getElementById("orderState").style.display = "block";
    document.getElementById("orderStateButton").style.display = "inline";
  }
  if ($("#transportCheck").is(":checked")) {
    document.getElementById("transport").style.display = "block";
    document.getElementById("transportButton").style.display = "inline";
  }
  if ($("#teuCheck").is(":checked")) {
    document.getElementById("teu").style.display = "block";
    document.getElementById("teuButton").style.display = "inline";
  }
}

$(document).ready(function() {
  $("#add").click(function() {
    chooseCheckBox();
  })
});

function companyCheck() {
  var buttons = document.querySelector('.companyChoice');
  document.getElementById("companyNameCheck").checked = true;
  if (document.getElementById("companyCheck").checked == true) {
    buttons.style.display = 'block';
  } else {
    buttons.style.display = 'none';
  }
};


$(document).ready(function() {
  $("#identificationButton").click(function() {
    $('#identificationFill').val('');
    document.getElementById("identification").style.display = "none";
    document.getElementById("identificationButton").style.display = "none";
  });
  $("#companyButton").click(function() {
    $('#companyNameFill').val('');
    $('#companyIdFill').val('');
    $('#companyScacFill').val('');
    document.getElementById("companyName").style.display = "none";
    document.getElementById("companyId").style.display = "none";
    document.getElementById("companyScac").style.display = "none";
    document.getElementById("companyButton").style.display = "none";
  });
  $("#locationButton").click(function() {
    $('#locationFill').val('');
    document.getElementById("location").style.display = "none";
    document.getElementById("locationButton").style.display = "none";
  });

  $("#orderStateButton").click(function() {
    $('#orderStateFill').val('');
    document.getElementById("orderState").style.display = "none";
    document.getElementById("orderStateButton").style.display = "none";
  });

  $("#transportButton").click(function() {
    $('#transportFill').val('');
    document.getElementById("transport").style.display = "none";
    document.getElementById("transportButton").style.display = "none";
  });
  $("#teuButton").click(function() {
    $('#teuFill').val('');
    document.getElementById("teu").style.display = "none";
    document.getElementById("teuButton").style.display = "none";
  });
});
function getCount() {
	var http = new XMLHttpRequest();
	var url = "http://localhost:8080/Project/rest/sql/count";
	http.onreadystatechange = function() {
		  if (this.readyState == 4 && this.status == 200) {
			  
			  document.getElementById("numcount").innerHTML = this.responseText;
			  countit(); 
		  }
		};
	http.open("GET", url);
	http.send();
};

function getYear() {
	var http = new XMLHttpRequest();
	var url = "http://localhost:8080/Project/rest/sql/year";
	http.onreadystatechange = function() {
		  if (this.readyState == 4 && this.status == 200) {
			  var temp = this.responseText.split("|");
			  a = temp[0].split(";");
			  b = temp[1].split(";").map(function(item) {
				    return parseInt(item, 10);
			  });
			  chart404(a,b);
		  }
		};

	http.open("GET", url);
	http.send();
};

function getBookFilter(){
	var http = new XMLHttpRequest();
	var dateFrom = new Date($('#dateFromFill').val());
	var dateTo = new Date($('#dateToFill').val());
	var fromD = "";
	var toD = "";
	if(dateFrom != "Invalid Date" && dateTo != "Invalid Date") {
		fromD = dateFrom.getTime() / 1000;
		toD = dateTo.getTime() / 1000;	
	}
	var dosId = $('#identificationFill').val();
	var ordState = $('#orderStateFill').val();
	var teu = $('#teuFill').val();
	var shipComp= $('#companyNameFill').val();
	var shipCompId = $('#companyIdFill').val();
	var shipCompScac = $('#companyScacFill').val();
	var list = {fromD, toD, dosId, ordState, teu, shipComp, shipCompId, shipCompScac};
	var url = "http://localhost:8080/Project/rest/sql/select?";
	var x
	for(x in list ){
		if(list[x] != ""){
			
			url += x + "=" + list[x];
			if(url[url.length - 1] != "&"){
				url += "&";
			}
		}
		var newUrl = url.substring(0, url.length - 1);
	}
	var text="";
	http.onreadystatechange = function(){
		  if (this.readyState == 4 && this.status == 200) {
			  book = this.responseText;
		  }
	};
	alert(book);
	http.open("GET", url);
	http.send();
};

function getBrutoFilter(){
	var http = new XMLHttpRequest();
	var dateFrom = new Date($('#dateFromFill').val());
	var dateTo = new Date($('#dateToFill').val());
	var fromD = "";
	var toD = "";
	if(dateFrom != "Invalid Date" && dateTo != "Invalid Date") {
		fromD = dateFrom.getTime() / 1000;
		toD = dateTo.getTime() / 1000;	
	}
	var dosId = $('#identificationFill').val();
	var ordState = $('#orderStateFill').val();
	var teu = $('#teuFill').val();
	var shipComp= $('#companyNameFill').val();
	var shipCompId = $('#companyIdFill').val();
	var shipCompScac = $('#companyScacFill').val();
	var list = {fromD, toD, dosId, ordState, teu, shipComp, shipCompId, shipCompScac};
	var url = "http://localhost:8080/Project/rest/sql/brutoWeight?";
	var x
	for(x in list ){
		if(list[x] != ""){
			if(x != list[list.length-1]){
				url += x + "=" + list[x] + "&";
			} else {
				url += x + "=" + list[x];
			}
		}
		var newUrl = url.substring(0, url.length - 1);
	}
	var text="";
	http.onreadystatechange = function(){
		  if (this.readyState == 4 && this.status == 200) {
			  weight = this.responseText;
		  }
	};
	alert(weight);
	http.open("GET", url);
	http.send();
};


function createTable(containTotal, weightTotal) {
	var table ="<tr><td>Total Containers</td><td>Total Weight</td></tr><tr><td>" + containTotal + "</td><td>" + weightTotal + "</td></tr>";
	return table;
};

function createResult(containTotal, weightTotal) {
	var x = "Total Number of Containers: " + containTotal + "\n Total Weight: " + weightTotal;
	return x;
}
	

$(document).ready(function() {
  $("#search").click(function() {
	  getBrutoFilter();
	  getBookFilter();
	  document.getElementById('resultTab').innerHTML = createTable(book,weight);  
  })
});