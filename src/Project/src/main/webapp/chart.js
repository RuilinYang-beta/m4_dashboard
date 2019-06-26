$(document).ready(function() {
  $('#customFill').val("");
  $('#companyFill').val("");
  $('#locationFill').val("");
  $('#dateFromFill').val("");
  $('#dateToFill').val("");
  $('#teuFill').val("");
  $('#transportFill').val("");
  dataList('companyNameList');
});


function dataList(datalistName) {
	var mylist = ['A288','A305','A315','A332','A342','B283','B291','B321','B325','C287','C288','C290','C295','C296','C297','C310','C320','C323','C327','C330','C334','C344','D319','D326','D337','D346','E290','E298','E312','E322',
		'F289','G318','G339','G350','H291','H293','H299','H300','H301','H302','H322','H328','I292','I294','I303','I324','J328','K295','K304','M285','M286','M293','M294',
		'M305','M317','M342','M352','M355','N296','N306','N321','N333','N335','N343','O297','O304','O307','O314','O336','O338','P314','P316','P324','S298','S303','S308','S312',
		'S313','S319','S325','S326','S332','S335','S336','S338','S340','T327','T331','U299','U309','U359','V339','W307','W317','Y300','Y310','Z301','Z311',
		];
	var list = document.getElementById(datalistName);
	var sortedMyList = mylist.sort();
	sortedMyList.forEach(function(item){
	   var option = document.createElement('option');
	   option.value = item;
	   list.appendChild(option);
	});
}


//Determine the max length of an input
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

// Checkboxes will show fillins when they are checked. Empty the value in the field and hiding the fillins when they are unchecked
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

// Checks if the date input is correct
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

//Wait for .. ms
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

// Filtering functions

var book = "0";
var bruto = "0";
var netto = "0";

/* Get the results based on filters inputted
Parameters:
table: The result type --> "table" or ""
searchType: The type of search on the database --> "bookings" for booking, "brutoWeight" for total bruto, and "nettoWeight" for total netto
canvasId: the canvas for the graph (optional)
graphTitle: title for the graph (optional)
label: name for certain data on the graph(optional)
label1: same as label, but for the second data
*/


function getFilter(table, searchType, type, canvasId = "", graphTitle = "", label = "",label1=""){
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
		url += "bookings";
	} else if (searchType == "brutoWeight") {
		url += "brutoWeight";
	} else if (searchType == "nettoWeight") {
		url += "nettoWeight";
	} else if (searchType == "2yAxis") {
		url += "2yAxis";
	} else if(searchType == "topCustomerBook"){
		url += "topCustomerBook";
	} else if (searchType == "topCustomerWeight"){
		url += "topCustomerWeight";
	}
	var x;
	if(table == "table"){
		url += "&table=true";
		for(x in list ){
			if(list[x] != ""){
				url += "&" + x + "=" + list[x];
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
					  document.getElementById('resultTab').innerHTML = createTable(customerId,book,bruto,netto);
				  }

			  }
		};

	} else {
		for(x in list ){
			if(list[x] != ""){
				url += "&" + x + "=" + list[x];
			}

		}

		http.onreadystatechange = function(){
			  if (this.readyState == 4 && this.status == 200 & searchType != "2yAxis") {
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
			  }else if(this.readyState == 4 && this.status == 200 & searchType == "2yAxis"){
				  var temp = this.responseText.split("|");
				  a = temp[0].split(";");
				  b = temp[1].split(";").map(function(item) {
					    return parseInt(item, 10);
				  });

				  c = temp[2].split(";").map(function(item) {
					    return parseInt(item, 10);
				  });

				  chart2yAxis(a,canvasId,type,label,label1,b,c,graphTitle);
			  }
		};
	}

	http.open("GET", url);
	http.send();
};


//Filters on all searchType
function searchIt() {
		getFilter("table", "bookings");
		wait(10);
		getFilter("table", "brutoWeight");
		wait(10);
		getFilter("table", "nettoWeight");

		getFilter("Graph", "bookings", 'line', "container", "Cumulative Bookings per Month", "# of bookings");
		getFilter("Graph", "brutoWeight", 'line', "container1", "Total Bruto Weight", "Bruto Weight");
		getFilter("Graph", "nettoWeight", 'line', "container2", "Total Netto Weight", "Netto Weight");
		getFilter("Graph", "topCustomerWeight", 'doughnut', "container3", "Top 10 Customer(weight)", "Amount of weights");
		getFilter("Graph", "topCustomerBook", 'doughnut', "container4", "Top 10 Customer(Books)", "Amount of bookings");

}

///CREATE A TABLE WHEN SEARCH IS PRESSED
function createTable(customer, containTotal, brutoTotal, nettoTotal) {
	var table ="<tr><th>Customer</th><td>" + customer + "</td></tr><tr><th>Total Containers</th><td>" + containTotal + "</td></tr><tr><th>Total Bruto Weight</th><td>" + brutoTotal + "</td></tr><tr><th>Total Netto Weight</th><td>" + nettoTotal + "</td></tr>";
	return table;
};

//Running filters with choice as the result type
function combine(){
	searchIt();
}

//CREATE CHART with 1 data
//parameter:
//year: list of YYYY_MM
//canvas: canvas name(container,container1, etc)
//type: type of the chart
//label: same as label in getFilter
//total: the list of total given query(bookings/weights)
//chartName: title fot the chart
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

//CREATE CHART WITH 2 DATA
//parameter:
//year: list of YYYY_MM
//canvas: canvas name(container,container1, etc)
//type: type of the chart
//label: same as label in getFilter
//label1: label for second data
//total: the list of total given query(bookings/weights)
//total1: total fot second data
//chartName: title fot the chart
function chart2yAxis(year,canvas,type,label,label1,total,total1,chartName){
	  var x = generateListColor(year, type);
	  var y = generateListColor(year, type);

	  var ctx = document.getElementById(canvas).getContext('2d');
	  var myChart = new Chart(ctx, {
	    type: type,
	    data: {
	        labels: year,
	        datasets: [{
	            label: label,
	            yAxisID: 'y-axis-1',
	            backgroundColor: x,
	            hoverBackgroundColor: x,
	            borderWidth: 10,
	            fill: false,
	            data: total,
	        },{
	        	label: label1,
	            yAxisID: 'y-axis-2',
	            backgroundColor: y,
	            hoverBackgroundColor: y,
	            borderWidth: 10,
	            fill: false,
	            data: total1,


	        }]
	    },
	    options: {
			responsive: true,
			hoverMode: 'index',
			stacked: false,
			title: {
				display: true,
				text: chartName
			},
			scales: {
				yAxes: [{
					type: 'linear', // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
					display: true,
					position: 'left',
					id: 'y-axis-1',
				}, {
					type: 'linear', // only linear but allow scale type registration. This allows extensions to exist solely for log scale for instance
					display: true,
					position: 'right',
					id: 'y-axis-2',

					// grid line settings
					gridLines: {
						drawOnChartArea: false, // only want the grid lines for one axis to show up
					},
				}],
			}
		}
	  });
}

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

//add employees to authorization database
function addEmployee() {
	var http = new XMLHttpRequest();
	var url = "http://localhost:8080/Project/rest/sql/auth?";
	var name = $('#name').val();
	var mail = $('#mail').val();
	var id = $('#id').val();
	//http://localhost:8080/Project/rest/sql/auth?name=""&id=""&mail=""
	var list = {name, mail, id};
	var x;
  for(x in list){
		if(list[x] != ""){
			url +=  x + "=" + list[x] + "&" ;
		}
	}
	var newUrl = url.substring(0, url.length-1);
	http.onreadystatechange = function() {
		if(this.readyState == 4 & this.status == 200) {
		console.log(this.responseText);
		}
	};
	http.open("POST", url);
	http.send();
};

//GENERATE TOP 10 CUSTOMER WITH ID 1
$(document).ready(function() {
	 $('#container3').replaceWith('<canvas id="container3" ></canvas>');
	 $('#container4').replaceWith('<canvas id="container4" ></canvas>');
	 $(getFilter("Graph", "topCustomerWeight", 'doughnut', "container3", "Top 10 Customer(weight)", "Amount of weights"));
	 $(getFilter("Graph", "topCustomerBook", 'doughnut', "container4", "Top 10 Customer(Books)", "Amount of bookings"));

//	 $(getFilter("Graph", "2yAxis", 'line', "container3", "line chart multiple Axis", "Amount of book", "amount of weight"));

//GENERATE GRAPH BELOW THE RESULT AND UPDATE THE TOP 10 CUSTOMER
$("#search").click(function() {
	  if(dateCorrect()) {
		  combine();
		  $('#container').replaceWith('<canvas id="container" ></canvas>');
		  $('#container1').replaceWith('<canvas id="container1" ></canvas>');
		  $('#container2').replaceWith('<canvas id="container2" ></canvas>');
		  $('#container3').replaceWith('<canvas id="container3" ></canvas>');
		  $('#container4').replaceWith('<canvas id="container4" ></canvas>');

	  }
 });
});

//remove said employees
function removeEmployee() {
	var http = new XMLHttpRequest();
	var url = "http://localhost:8080/Project/rest/sql/auth?";
	var name = $('#name').val();
	var mail = $('#mail').val();
	var id = $('#id').val();
	//http://localhost:8080/Project/rest/sql/auth?name=""&id=""&mail=""
	var list = {name, mail, id};
  var x;
  for(x in list){
		if(list[x] != ""){
			url +=  x + "=" + list[x] + "&" ;
		}
	}
	var newUrl = url.substring(0, url.length-1);
	http.onreadystatechange = function() {
		if(this.readyState == 4 & this.status == 200) {
		console.log(this.responseText);
		}
	};
	http.open("DELETE", url);
	http.send();
};



/////// Settings Page \\\\\\\

function addEnvi() {
	var http = new XMLHttpRequest();
	var url = "http://localhost:8080/Project/rest/sql/update?";
	var name = $('#custname').val();
	var link = $('#httplink').val();
	alert("a");
	var B_L_A_S;
	var bookCheck = $("#bookingCheck").is(':checked');
	var locationCheck = $("#locationCheck").is(':checked');
	var actionCheck = $("#actionCheck").is(':checked');
	var linestopCheck = $("#linestopCheck").is(':checked');
	var check = [locationCheck,actionCheck,linestopCheck];
	if(bookCheck) {
		B_L_A_S = "t";
	} else {
		B_L_A_S = "f";
	}
	var a;
	for(a = 0; a < check.length; a++) {
		if(check[a]){
			B_L_A_S += "_t";
		} else {
			B_L_A_S += "_f";
		}

	}
	var list = {name,link,B_L_A_S};
	var x;
	var newUrl;
	for(x in list){
		if(x != list[list.length - 1]){
			url += x + "=" + list[x] + "&";
		}else{
			url += x + "=" + list[x];
		}
	}
	alert(url);
//	http.open("POST", url);
//	http.send();
}

//Check if input field Name and HttpLink is not empty
function inputNotEmpty() {
	var name = $('#custname').val();
	var link = $('#httplink').val();

	if (name == "") {
		alert("Name Cannot be Empty");
		return false;
	} else if (link == ""){
		alert("Link cannot be empty");
		return false;
	}  else {
		return true;
	}
}

$(document).ready(function() {
	  $("#addEnviButton").click(function() {
		  if(inputNotEmpty()) {
			  addEnvi();
		  }
	  });
	});
