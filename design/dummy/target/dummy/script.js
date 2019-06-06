
// url to deploy in ut server
var baseUrl = "http://farm03.ewi.utwente.nl:7034/dummy/rest";
// url to deploy in local machine
//var baseUrl = "http://localhost:8080/dummy/rest";

// first radio button
var radio = document.querySelector("input[type='radio']");
// second button, show me the chart
var btn = document.querySelectorAll('.btn')[1];

btn.addEventListener('click', function(){
	if (radio.hasAttribute("checked")){
		// send a HTTP request to server
		var http = new XMLHttpRequest();
		// prepare url
		var url = baseUrl + "/bygenre";
		// open XMLHttpRequest obj
		http.open("GET", url, true);
		// behavior on ready
		http.onreadystatechange = function(){
			if (this.readyState === 4 && this.status === 200){
				// for test; check your browser console!s
				console.log(this.responseText);
				
				var response = JSON.parse(this.responseText); // overwrite with the parsed val from json
				
				// create a chart based on the data, and change dom
				  drawChart(response);
			}
		}
		// finally, do send
		http.send();
		console.log("You have issued a HTTP GET request to movie database!")
	}
});

function drawChart(json){
	var jsonString = JSON.stringify(json);
	// change all the keys using regex
	var string = jsonString.replace(/\"genre\":/g, "\"x\":");
	var string = string.replace(/\"count\":/g, "\"value\":");

	  // create the chart
	  var chart = anychart.pie();

	  // set the chart title
	  chart.title("number of movie by genra");

	  // add the data
	  chart.data(JSON.parse(string));
	  
	  // display the chart in the container
	  chart.container('container');
	  chart.draw();
}


