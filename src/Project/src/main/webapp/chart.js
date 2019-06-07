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

function chart404(a, b) {
	  var ctx = document.getElementById('contain').getContext('2d');
	  var myChart = new Chart(ctx, {
	      type: 'bar',
	      data: {
	        labels: a, //['Red', 'Blue', 'Yellow', 'Green', 'Purple'],
	        datasets: [{
	            label: '# of Bookings',
	            data: b, //[12, 19, 3, 5, 2, 3],
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




function generateFilter() {
  var orderState = document.getElementById("orderState1").value;
  /*var bookingsper = document.getElementById("bookingsper").value;*/
  var dateFrom = new Date($('#from').val());
  var dateFromMilli = dateFrom.getTime()/1000;
  var dateTo = new Date($('#to').val());
  var dateToMilli = dateTo.getTime()/1000;
  alert(dateFromMilli + " " + dateToMilli);
  var output = "Order State: " + orderState + " From: " + dateFromMilli + " To: " + dateToMilli;

  document.getElementById("testField").innerHTML = output;
}

function chooseCheckBox() {
  if ($("#dossierCheck").is(":checked")) {
    document.getElementById("dossierId").style.display = "block";
    document.getElementById("dossierIdButton").style.display = "inline";
  } if ($("#companyCheck").is(":checked")) {
    document.getElementById("company").style.display = "block";
    document.getElementById("companyButton").style.display = "inline";
  } if ($("#customerCheck").is(":checked")) {
    document.getElementById("customer").style.display = "block";
    document.getElementById("customerButton").style.display = "inline";
  } if ($("#locationCheck").is(":checked") ){
    document.getElementById("location").style.display = "block";
    document.getElementById("locationButton").style.display = "inline";
  } if ($("#orderStateCheck").is(":checked") ){
    document.getElementById("orderState").style.display = "block";
    document.getElementById("orderStateButton").style.display = "inline";
  } if ($("#transportCheck").is(":checked") ){
    document.getElementById("transport").style.display = "block";
    document.getElementById("transportButton").style.display = "inline";
  }

  document.getElementById("testField").innerHTML = choice;

}

$(document).ready(function() {
  $("#generate1").click(function() {
    generateFilter();
  })
});

$(document).ready(function() {
  $("#add").click(function() {
    chooseCheckBox();
  })
});

$(document).ready(function() {
  $("#dossierIdButton").click(function() {
    document.getElementById("dossierId").style.display = "none";
    document.getElementById("dossierIdButton").style.display = "none";
  });
  $("#companyButton").click(function() {
    document.getElementById("company").style.display = "none";
    document.getElementById("companyButton").style.display = "none";
  });
  $("#customerButton").click(function() {
    document.getElementById("customer").style.display = "none";
    document.getElementById("customerButton").style.display = "none";
  });
  $("#locationButton").click(function() {
    document.getElementById("location").style.display = "none";
    document.getElementById("locationButton").style.display = "none";
  });

  $("#orderStateButton").click(function() {
    document.getElementById("orderState").style.display = "none";
    document.getElementById("orderStateButton").style.display = "none";
  });

  $("#transportButton").click(function() {
    document.getElementById("transport").style.display = "none";
    document.getElementById("transportButton").style.display = "none";
  });
});


function getSearchByValues(){
  var dossierId = $('#dossierId').val();
  var company = $('#company').val();
  var customer = $('#customer').val();
  var location = $('#location').val();
  var result = "ID: " + dossierId + " Company: " + company + " Container Number: " + customer + " Location: " + location;
  document.getElementById("testField").innerHTML = result;
}

$(document).ready(function() {
  $("#search").click(function() {
    getSearchByValues();
  })
});
/*
function test() {
  var myObj = httpGet("https://module4t1-test.cofanostack.com/api/bigbrother/bookings");
  var parse = JSON.parse(myObj);
  var table = '';
  for (var i = 0; i < 2; i++) {
    table += "<p>" + parse[i].customer + "</p>";
  }
  document.getElementById("test").innerHTML = table;
}


function httpGet(theUrl) {
  var xmlHttp = new XMLHttpRequest();
  xmlHttp.open("GET", theUrl, false); // false for synchronous request
  xmlHttp.send(null);
  xmlHttp.setRequestHeader('Authorization', 'Basic QmlnQnJvdGhlcjpob3R0ZW50b3R0ZW50ZW50ZW50ZW50b29uc3RlbGxpbmc=');
  return xmlHttp.responseText;
}
*/
