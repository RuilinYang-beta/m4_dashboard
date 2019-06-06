$(document).ready(function() {
  $("input[type='button']").click(function() {
    if (document.getElementById("radio1").checked) {
      $('#container').replaceWith('<canvas id="container"></canvas>');
      chart1();
    } else if (document.getElementById("radio2").checked) {
      $('#container').replaceWith('<canvas id="container"></canvas>');
      chart2();
    } else {
      $('#container').replaceWith('<canvas id="container"></canvas>');
      chart1();
    }
  })
});

function chart1() {
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
	alert("doing");
	var a = null;
	var b = null;
	getYear(a, b);
	alert(a + " " + b);
	var ctx = document.getElementById('container').getContext('2d');
	  var myChart = new Chart(ctx, {
	      type: 'bar',
	      data: {
	          labels: a,
	          datasets: [{
	              label: '# of Bookings',
	              data: b,
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
          /*title: {
            display: true,
            text: 'Top 5 Customers'
          },*/
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
