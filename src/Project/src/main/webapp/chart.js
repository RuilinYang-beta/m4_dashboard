$(document).ready(function() {
  $('#customFill').val("");
  $('#companyFill').val("");
  $('#locationFill').val("");
  $('#dateFromFill').val("");
  $('#dateToFill').val("");
  $('#teuFill').val("");
  $('#transportFill').val("");
  //dataList('companyNameList')
});


function dataList(datalistName) {
	var mylist = ['A288','A305','A315','A332',
		'A342',
		'B283',
		'B291',
		'B321',
		'B325',
		'C287',
		'C288',
		'C290',
		'C295',
		'C296',
		'C297',
		'C310',
		'C320',
		'C323',
		'C327',
		'C330',
		'C334',
		'C344',
		'D319',
		'D326',
		'D337',
		'D346',
		'E290',
		'E298',
		'E312',
		'E322',
		'F289',
		'G318',
		'G339',
		'G350',
		'H291',
		'H293',
		'H299',
		'H300',
		'H301',
		'H302',
		'H322',
		'H328',
		'I292',
		'I294',
		'I303',
		'I324',
		'J328',
		'K295',
		'K304',
		'M285',
		'M286',
		'M293',
		'M294',
		'M305',
		'M317',
		'M342',
		'M352',
		'M355',
		'N296',
		'N306',
		'N321',
		'N333',
		'N335',
		'N343',
		'O297',
		'O304',
		'O307',
		'O314',
		'O336',
		'O338',
		'P314',
		'P316',
		'P324',
		'S298',
		'S303',
		'S308',
		'S312',
		'S313',
		'S319',
		'S325',
		'S326',
		'S332',
		'S335',
		'S336',
		'S338',
		'S340',
		'T327',
		'T331',
		'U299',
		'U309',
		'U359',
		'V339',
		'W307',
		'W317',
		'Y300',
		'Y310',
		'Z301',
		'Z311',
		'S333'];
	var list = document.getElementById(datalistName);
	var sortedMyList = mylist.sort();
	sortedMyList.forEach(function(item){
	   var option = document.createElement('option');
	   option.value = item;
	   list.appendChild(option);
	});
}

// Determine the max length of an input
function maxLengthCheck(object, maxLength) {
	if(object.value.length > maxLength) {
        object.value = object.value.substr(0, maxLength);
    }
  }

// Validate whether the input matches the regexValue parameter, e.g, /[1-2]|\./ where the values allowed are those specified inside the [].
function validateEvent(evt,regexValue) {
	  var theEvent = evt || window.event;

	  // Handle paste
	  if (theEvent.type === 'paste') {
	      key = event.clipboardData.getData('text/plain');
	  } else {
	  // Handle key press
	      var key = theEvent.keyCode || theEvent.which;
	      key = String.fromCharCode(key);
	  }
	  var regex = regexValue;
	  if( !regex.test(key) ) {
	    theEvent.returnValue = false;
	    if(theEvent.preventDefault) theEvent.preventDefault();
	  }
	}

// Validate on certain input.value.length, e.g, only Uppercase under length 1 and digit above length 1
function restrictInput(obj, evt, regexA, rangeA, regexB) {
	if(obj.value.length < rangeA) {
		validateEvent(evt,regexA);
	} else {
		validateEvent(evt,regexB)
	}
}

// Show input fields when checkbox is checked
function check(divClass, checkboxClass){
	var buttons = document.querySelector(divClass);
	if (document.getElementById(checkboxClass).checked == true) {
	    buttons.style.display = 'block';
	} else {
	    buttons.style.display = 'none';
	}
}

// same as check() but for select types (still not working)
function checkWithSelect(divClass, checkboxClass, fillInId){
	$(checkboxClass).on('change', function (e) {
		if(this.checked) {
			 $(divClass).css("display", "block");
		} else {
			$(divClass).css("display", "none");
			$(fillInId).val('');
		}
	});
}

function removeVal(fillId){
	$(fillId).on('change', function (e) {
        $("#emailid").val('');
    });
}


function dateCorrect() {
	var dateFrom = new Date($('#dateFromFill').val());
	var dateTo = new Date($('#dateToFill').val());
	var today = new Date();
	var dateF = dateFrom.getTime();
	var dateT = dateTo.getTime();	

	if (dateF > dateT) {
		alert("Date From cannot be larger than date To");
		$('#dateFromFill').val('');
		$('#dateToFill').val('');
		return false;
	} else if (dateF > today.getTime()){
		alert("Date From cannot be larger than Today's Date");
		$('#dateFromFill').val('');
		return false;
	} else if (dateT > today.getTime()){
		alert("Date To cannot be larger than Today's Date");
		$('#dateToFill').val('');
		return false;
	} else {
		return true;
	}
}


function wait(ms){
	   var start = new Date().getTime();
	   var end = start;
	   while(end < start + ms) {
	     end = new Date().getTime();
	  }
	}




// Get the count on number of bookings
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

// Get the ....
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


// Filtering functions

var book = "0";
var bruto = "0";
var netto = "0";

/* Get the results based on filters inputted
Parameters:
a: The result type --> "table" or ""
searchType: The type of search on the database --> "bookings" for booking, "brutoWeight" for total bruto, and "nettoWeight" for total netto
canvasId: the canvas for the graph (optional)
graphTitle: titl for the graph (optional) 
*/
function getFilter(table, searchType, type, canvasId = "", graphTitle = "", label = ""){
	var http = new XMLHttpRequest();
	var customerId = $('#customerFill').val();
	var dateFrom = new Date($('#dateFromFill').val());
	var dateTo = new Date($('#dateToFill').val());
	var fromD = "";
	var toD = "";
	if(dateFrom != "Invalid Date") {
		fromD = dateFrom.getTime() / 1000;	
	}
	if (dateTo != "Invalid Date") {
		toD = dateTo.getTime() / 1000;	
	}
	var customer = $('#customFill').val();
	var ordState = $('#orderStateFill').val();
	var teu = $('#teuFill').val();
	var shipComp= $('#companyNameFill').val();
	var shipCompId = "";
	var shipCompScac = "";
	var list = {fromD, toD, customer, ordState, teu, shipComp, shipCompId, shipCompScac, customerId};
	var url = "http://localhost:8080/Project/rest/sql/select?goal=";
	if(searchType == "bookings") {
		url += "bookings&";
	} else if (searchType == "brutoWeight") {
		url += "brutoWeight&";
	} else if (searchType == "nettoWeight") {
		url += "nettoWeight&";
	} 
	var x;	
	if(table == "table"){
		url += "table=true&";
		for(x in list ){
			if(list[x] != ""){
				if(x != list[list.length - 1]){
					url += x + "=" + list[x] + "&";
				}else{
					url += x + "=" + list[x];
				}
			}

		}
		
		http.onreadystatechange = function(){
			  if (this.readyState == 4 && this.status == 200) {
				  if(searchType == "bookings") {
					  book = this.responseText;
				  } else if(searchType == "brutoWeight") {
					  bruto = this.responseText;
				  } else if(searchType == "nettoWeight") {
					  netto = this.responseText;  
					  document.getElementById('resultTab').innerHTML = createTable(book,bruto,netto);
				  }
				  
			  }
		};
		
	} else {
		for(x in list ){
			if(list[x] != ""){
				if(x != list[list.length - 1]){
					url += x + "=" + list[x] + "&";
				}else{
					url += x + "=" + list[x];
				}
			}
			
		}
		
		http.onreadystatechange = function(){
			  if (this.readyState == 4 && this.status == 200) {
				  var temp = this.responseText.split("|");
				  a = temp[0].split(";");
				  b = temp[1].split(";").map(function(item) {
					    return parseInt(item, 10);
				  });
				  
				  var i;
				  for(i = 1; i<b.length ; i++){
					  b[i] = b[i] + b[i-1];
				  };
				  chart(a,b,canvasId,graphTitle, type, label);
			  }
		};
	}
	
	http.open("GET", url);
	http.send();
};

//Filters on all searchType
function searchIt(searchResult) { 
	if(searchResult == "table") {
		getFilter(searchResult, "bookings");
		wait(10);
		getFilter(searchResult, "brutoWeight");
		wait(10);
		getFilter(searchResult, "nettoWeight");
		
	} else {
		getFilter(searchResult, "bookings", 'pie', "container", "Cumulative Bookings per Month", "# of bookings");
		getFilter(searchResult, "brutoWeight", 'bar', "container1", "Total Bruto Weight", "Bruto Weight");
		getFilter(searchResult, "nettoWeight", 'doughnut', "container2", "Total Netto Weight", "Netto Weight");
	}
}


function createTable(containTotal, brutoTotal, nettoTotal) {
	var table ="<tr><th>Total Containers</th><td>" + containTotal + "</td></tr><tr><th>Total Bruto Weight</th><td>" + brutoTotal + "</td></tr><tr><th>Total Netto Weight</th><td>" + nettoTotal + "</td></tr>";
	return table;
};

// doughnut chart for top 10 customer
function topTen(goal, container, title){
	var http = new XMLHttpRequest();
	var url = "http://localhost:8080/Project/rest/sql/select?goal=";
	if(goal == "topCustomerBook"){
		url += "topCustomerBook";
	}else if (goal == "topCustomerWeight"){
		url += "topCustomerWeight";
	}
	http.onreadystatechange = function(){
	  if (this.readyState == 4 && this.status == 200) {
		  
		  var temp = this.responseText.split("|");
		  a = temp[0].split(";");
		  b = temp[1].split(";").map(function(item) {					  
		  return parseInt(item, 10);
		  });

		  chart(a,b,container,title,'doughnut');
		 
	  }
	};

	
	http.open("GET", url);
	http.send();
};

//Running filters with choice as the result type
function combine(choice){
	searchIt(choice);	
}

$(document).ready(function() {
	 $('#container3').replaceWith('<canvas id="container3" ></canvas>');
	 $('#container4').replaceWith('<canvas id="container4" ></canvas>');
	 $(topTen("topCustomerWeight", "container3", "Top 10 Customer(Weight)"));
	 $(topTen("topCustomerBook", "container4", "Top 10 Customer(Books)"));
	 
//	 $(getFilter("graph", "topCustomerWeights", 'doughnut', "container4", "Top 10 Customer", "Amount of Weight"));
  $("#search").click(function() { 
	  if(dateCorrect()) {
		  combine($('#resultFill').val()); 
		  $('#container').replaceWith('<canvas id="container" ></canvas>');
		  $('#container1').replaceWith('<canvas id="container1" ></canvas>');
		  $('#container2').replaceWith('<canvas id="container2" ></canvas>');
		 
	  }  
  });
});


////// Create Charts


function chart(year, total, canvas, chartName, type, label) {
	  var x = generateListColor(year, type);
	  var ctx = document.getElementById(canvas).getContext('2d');
	  var myChart = new Chart(ctx, {
	    type: type,
	    data: {
	        labels: year,
	        datasets: [{
	            label: label,
	            data: total,
	            backgroundColor: x,
	            hoverBackgroundColor: x,
	            borderWidth: 2
	        }]
	    },
	    options: {
	        responsive: true,
	        title: {
	            display: true,
	            text: chartName
	          }
	    }
	  });
};


// generate random color
function generateListColor(year, type){
	var x;
	var list = [];
	
	for(x in year){
		if(type == 'doughnut'){
			if(list.includes(getRandomColor())){
				getRandomColor();
			}else{
				list.push(getRandomColor());
			}
		}else{
			if(list.includes(random_rgba())){
				random_rgba();
			}else{
				list.push(random_rgba());
			}
		}
		
	}
	return list;
}
/// random color with #
function getRandomColor() {
	  var letters = '0123456789ABCDEF';
	  var color = '#';
	  for (var i = 0; i < 6; i++) {
	    color += letters[Math.floor(Math.random() * 16)];
	  }
	  return color;
	}
//random color with rgba
function random_rgba() {
  var o = Math.round, r = Math.random, s = 255;
  return 'rgba(' + o(r()*s) + ',' + o(r()*s) + ',' + o(r()*s) + ',' + r().toFixed(1) + ')';
}