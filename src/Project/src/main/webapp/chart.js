$(document).ready(function() {
  $('#customFill').val("");
  $('#companyFill').val("");
  $('#locationFill').val("");
  $('#dateFromFill').val("");
  $('#dateToFill').val("");
  $('#teuFill').val("");
  $('#transportFill').val("");

});


function maxLengthCheck(object, maxLength) {
	if(object.value.length > maxLength) {
        object.value = object.value.substr(0, maxLength);
    }
  }

function validate(evt) {
	  var theEvent = evt || window.event;

	  // Handle paste
	  if (theEvent.type === 'paste') {
	      key = event.clipboardData.getData('text/plain');
	  } else {
	  // Handle key press
	      var key = theEvent.keyCode || theEvent.which;
	      key = String.fromCharCode(key);
	  }
	  var regex = /[0-9]|\./;
	  if( !regex.test(key) ) {
	    theEvent.returnValue = false;
	    if(theEvent.preventDefault) theEvent.preventDefault();
	  }
	}

function check(divClass, checkboxClass){
	var buttons = document.querySelector(divClass);
	if (document.getElementById(checkboxClass).checked == true) {
	    buttons.style.display = 'block';
	} else {
	    buttons.style.display = 'none';
	}
}

function checkWithSelect(divClass, checkboxClass, fillInId){
	var buttons = document.querySelector(divClass);
	if (document.getElementById(checkboxClass).checked == true) {
	    buttons.style.display = 'block';	
	    $(fillInId).val(1);
	} else {
	    buttons.style.display = 'none';
	    
	}
}



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


var book;
var weight;


function getBookFilter(a){
	var http = new XMLHttpRequest();
	var dateFrom = new Date($('#dateFromFill').val());
	var dateTo = new Date($('#dateToFill').val());
	var fromD = "";
	var toD = "";
	if(dateFrom != "Invalid Date" && dateTo != "Invalid Date") {
		fromD = dateFrom.getTime() / 1000;
		toD = dateTo.getTime() / 1000;	
	}
	var dosId = $('#customFill').val();
	var ordState = $('#orderStateFill').val();
	var teu = $('#teuFill').val();
	var shipComp= $('#companyNameFill').val();
	var shipCompId = "";
	var shipCompScac = "";
	var list = {fromD, toD, dosId, ordState, teu, shipComp, shipCompId, shipCompScac};
	var url = "http://localhost:8080/Project/rest/sql/select?goal=bookings&";
	var x;
	if(a == "table"){
		url += "table=true&";
		for(x in list ){
			if(list[x] != ""){
				if(x != list[list.length - 1]){
					url += x + "=" + list[x] + "&";
				}
			}
			var newUrl = url.substring(0, url.length - 1);
		}
		
		http.onreadystatechange = function(){
			  if (this.readyState == 4 && this.status == 200) {
				  book = this.responseText;
				  
				  document.getElementById('resultTab').innerHTML = createTable(book,weight);  
			  }
		};
		
	}else{
		for(x in list ){
			if(list[x] != ""){
				if(x != list[list.length - 1]){
					url += x + "=" + list[x] + "&";
				}
			}
			var newUrl = url.substring(0, url.length - 1);
		}
		
		http.onreadystatechange = function(){
			  if (this.readyState == 4 && this.status == 200) {
				  var temp = this.responseText.split("|");
				  a = temp[0].split(";");
				  b = temp[1].split(";");
				  alert(a);
				  alert(b);
				  lineChart(a,b,"container");
			  }
		};
	}
	
	http.open("GET", url);
	http.send();
};

function getBrutoFilter(a){
	var http = new XMLHttpRequest();
	var dateFrom = new Date($('#dateFromFill').val());
	var dateTo = new Date($('#dateToFill').val());
	var fromD = "";
	var toD = "";
	if(dateFrom != "Invalid Date" && dateTo != "Invalid Date") {
		fromD = dateFrom.getTime() / 1000;
		toD = dateTo.getTime() / 1000;	
	}
	var dosId = $('#customFill').val();
	var ordState = $('#orderStateFill').val();
	var teu = $('#teuFill').val();
	var shipComp= $('#companyNameFill').val();
	var shipCompId = "";
	var shipCompScac = "";
	var list = {fromD, toD, dosId, ordState, teu, shipComp, shipCompId, shipCompScac};
	var url = "http://localhost:8080/Project/rest/sql/select?goal=brutoWeight&";
	var x
	if(a == "table"){
		url += "table=true&";
	}else{
		url;
	}
	for(x in list ){
		if(list[x] != ""){
			if(x != list[list.length-1]){
				url += x + "=" + list[x] + "&";
			} 
		}
	
		var newUrl = url.substring(0, url.length - 1);
		
	}
	
	
	http.onreadystatechange = function(){
		  if (this.readyState == 4 && this.status == 200) {
			  weight = this.responseText;
			  document.getElementById('resultTab').innerHTML = createTable(book,weight);  
		  }
	};
	
	http.open("GET", newUrl);
	http.send();
};


function createTable(containTotal, weightTotal) {
	var table ="<tr><th>Total Containers</th><td>" + containTotal + "</td></tr><tr><th>Total Weight</th><td>" + weightTotal + "</td></tr>";
	return table;
};

function createResult(containTotal, weightTotal) {
	var x = "Total Number of Containers: " + containTotal + "\n Total Weight: " + weightTotal;
	return x;
}

function combine(){
	getBrutoFilter();
	getBookFilter();
	getBookFilter("table");

}

$(document).ready(function() {
  $("#search").click(function() { 
	  combine();
	  $('#container').replaceWith('<canvas id="container"></canvas>');
  })
  
});


////// Create Charts


function lineChart(year, total, canvas) {
	  var ctx = document.getElementById(canvas).getContext('2d');
	  var myChart = new Chart(ctx, {
	    type: 'line',
	    data: {
	        labels: year,
	        datasets: [{
	            label: '# of Containers',
	            data: total,
	            backgroundColor: [
	                'rgba(255, 99, 132, 0.2)',
	                'rgba(54, 162, 235, 0.2)',
	                'rgba(255, 206, 86, 0.2)',
	                'rgba(75, 192, 192, 0.2)',
	                'rgba(153, 102, 255, 0.2)',
	                'rgba(255, 159, 64, 0.2)'
	            ],
	            borderColor: [
	                'rgba(255, 99, 132, 1)',
	                'rgba(54, 162, 235, 1)',
	                'rgba(255, 206, 86, 1)',
	                'rgba(75, 192, 192, 1)',
	                'rgba(153, 102, 255, 1)',
	                'rgba(255, 159, 64, 1)'
	            ],
	            borderWidth: 1
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                    beginAtZero: true
	                }
	            }]
	        }
	    }
	  });
};