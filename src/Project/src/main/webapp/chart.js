$(document).ready(function() {
  $("#generate").click(function() {
    if (document.getElementById("radio1").checked) {
      $('#container').replaceWith('<canvas id="container"></canvas>');
      chart1();
    } else if (document.getElementById("radio2").checked) {
      $('#container').replaceWith('<canvas id="container"></canvas>');
      chart2();
    } else if (document.getElementById("radio3").checked) {
      $('#container').replaceWith('<canvas id="container"></canvas>');
      chart1();
    } else if (document.getElementById("radio4").checked) {
      $('#container').replaceWith('<canvas id="container" width= "800" height="400"></canvas>');
      chart4();
    }
  })
});



function chart1(container) {
  var ctx = document.getElementById('container').getContext('2d');
  var myChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple'],
      datasets: [{
        label: '# of Bookings',
        data: [12, 19, 3, 5, 2, 3],
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)',
          'rgba(54, 162, 235, 0.2)',
          'rgba(255, 206, 86, 0.2)',
          'rgba(75, 192, 192, 0.2)',
          'rgba(153, 102, 255, 0.2)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
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


function chart2() {
  var ctx = document.getElementById('container').getContext('2d');
  var myChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple'],
      datasets: [{
        label: '# of Bookings',
        data: [20, 12, 9, 1, 16],
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)',
          'rgba(54, 162, 235, 0.2)',
          'rgba(255, 206, 86, 0.2)',
          'rgba(75, 192, 192, 0.2)',
          'rgba(153, 102, 255, 0.2)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
        ],
        borderWidth: 1
      }]
    },
    options: {
      title: {
        display: true,
        text: 'Top 5 Customers'
      },
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

function chart3() {
  var ctx = document.getElementById('contain').getContext('2d');
  var myChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple'],
      datasets: [{
        label: '# of Bookings',
        data: [20, 12, 9, 1, 16],
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)',
          'rgba(54, 162, 235, 0.2)',
          'rgba(255, 206, 86, 0.2)',
          'rgba(75, 192, 192, 0.2)',
          'rgba(153, 102, 255, 0.2)',
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)',
          'rgba(153, 102, 255, 1)',
        ],
        borderWidth: 1
      }]
    },
    options: {
      title: {
        display: true,
        text: 'Top 5 Customers'
      },
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

function chart4() {
  var ctx = document.getElementById('container').getContext('2d');
  var myPieChart = new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: ["Train", "Truck", "Barge"],
      datasets: [{
        label: "# Containers per Mode of Transport",
        data: [1910, 892, 2450],
        backgroundColor: ["rgb(255, 99, 132)", "rgb(54, 162, 235)", "rgb(255, 205, 86)"]
      }]
    }
  });
}

$(document).ready(function() {
  $('#weightfromFill').val("");
  $('#weighttoFill').val("");
  $('#orderStateFill').val("");
  $('#companyFill').val("");
  $('#locationFill').val("");
  $('#dateFromFill').val("");
  $('#dateToFill').val("");
  $('#teuFill').val("");
  $('#transportFill').val("");

});




function generateFilter() {
  var orderState = document.getElementById("orderState1").value;
  /*var bookingsper = document.getElementById("bookingsper").value;*/
  var dateFrom = new Date($('#from').val());
  var dateFromMilli = dateFrom.getTime() / 1000;
  var dateTo = new Date($('#to').val());
  var dateToMilli = dateTo.getTime() / 1000;
  alert(dateFromMilli + " " + dateToMilli);
  var output = "Order State: " + orderState + " From: " + dateFromMilli + " To: " + dateToMilli;

  document.getElementById("testField").innerHTML = output;
}

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
  if ($("#weightCheck").is(":checked")) {
    if($("#bruttoCheck").is(":checked")) {
        document.getElementById("brutto").style.display = "block";
        document.getElementById("netto").style.display = "none";
    } else {
        document.getElementById("brutto").style.display = "none";
        document.getElementById("netto").style.display = "block";
    }
    $('#bruttofromFill').val('');
    $('#bruttotoFill').val('');
    $('#nettofromFill').val('');
    $('#nettotoFill').val('');
    document.getElementById("weightButton").style.display = "inline";
  }
  if ($("#dateCheck").is(":checked")) {
    document.getElementById("dateFrom").style.display = "inline";
    document.getElementById("dateTo").style.display = "inline";
    document.getElementById("dateButton").style.display = "inline";
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

function weightCheck() {
  var buttons = document.querySelector('.weightChoice');
  document.getElementById("brutto").checked = true;
  if (document.getElementById("weightCheck").checked == true) {
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
  $("#weightButton").click(function() {
    $('#bruttofromFill').val('');
    $('#bruttotoFill').val('');
    $('#nettofromFill').val('');
    $('#nettotoFill').val('');
    document.getElementById("brutto").style.display = "none";
    document.getElementById("netto").style.display = "none";
    document.getElementById("weightButton").style.display = "none";
  });
  $("#dateButton").click(function() {
    $('#dateFromFill').val('');
    document.getElementById("dateFrom").style.display = "none";
    $('#dateToFill').val('');
    document.getElementById("dateTo").style.display = "none";
    document.getElementById("dateButton").style.display = "none";
  });
});


function getSearchByValues() {
  var booking = $('#identificationFill').val();;
  var dossier = $('#identification1Fill').val();;
  var bruttoFrom;
  var bruttoTo;
  var nettoFrom;
  var nettoTo;
  if (document.getElementById("brutto").checked == true) {
    bruttoFrom = $('#weightfromFill').val();
    bruttoTo = $('#weighttoFill').val();
    netto = "empty";
  } else {
    brutto = "empty";
    nettoFrom = $('#weightfromFill').val();
    nettoTo = $('#weighttoFill').val();
  }
  var order = $('#orderStateFill').val();
  var company = $('#companyFill').val();
  var location = $('#locationFill').val();
  var dateFrom = new Date($('#dateFromFill').val());
  var fromD = dateFrom.getTime() / 1000;
  var dateTo = new Date($('#dateToFill').val());
  var toD = dateTo.getTime() / 1000;
  var result = "ID: " + booking + " " + dossier + "Weight: From: " + bruttoFrom + " To: " + bruttoTo + " Company: " + company + " Location: " + location + "Order State: " + order + " From Date: " + fromD + " To Date: " + toD;
  document.getElementById("testField").innerHTML = result;
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
	var fromN = $('#nettofromFill').val();
	var toN = $('#nettotoFill').val();
	var fromB = $('#bruttofromFill').val();
	var toB = $('#bruttotoFill').val();
	var ordState = $('#orderStateFill').val();
	var teu = $('#teuFill').val();
	var shipComp= $('#companyNameFill').val();
	var shipCompId = $('#companyIdFill').val();
	var shipCompScac = $('#companyScacFill').val();
	var list = {fromD, toD, dosId, ordState, fromN, toN, fromB, toB, teu, shipComp, shipCompId, shipCompScac};
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
	alert(newUrl);
	http.onreadystatechange = function(){
		  if (this.readyState == 4 && this.status == 200) {
			  var text = this.responseText;
			  alert(text);
		  }
	};

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
	var list = {fromD, toD, dosId, ordState, fromN, toN, fromB, toB, teu, shipComp, shipCompId, shipCompScac};
	var url = "http://localhost:8080/Project/rest/sql/brutoWeight?";
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
	alert(newUrl);
	http.onreadystatechange = function(){
		  if (this.readyState == 4 && this.status == 200) {
			  var text = this.responseText;
			  alert(text);
		  }
	};

	http.open("GET", url);
	http.send();

};

function getNettoFilter(){
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
	var list = {fromD, toD, dosId, ordState, fromN, toN, fromB, toB, teu, shipComp, shipCompId, shipCompScac};
	var url = "http://localhost:8080/Project/rest/sql/nettoWeight?";
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
	alert(newUrl);
	http.onreadystatechange = function(){
		  if (this.readyState == 4 && this.status == 200) {
			  var text = this.responseText;
			  alert(text);
		  }
	};

	http.open("GET", url);
	http.send();

};

$(document).ready(function() {
  $("#search").click(function() {
    getBookFilter();
  })
});



