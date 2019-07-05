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
			  alert(this.responseText);
			  var temp = this.responseText.split("|");
			  a = temp[0].split(";");
			  b = temp[1].split(";").map(function(item) {
				    return parseInt(item, 10);
			  });
			  alert(a + " " + b);
			  alert("execute chart404");
			  dostuff();
			  chart404(a,b);
		  }
		};

	http.open("GET", url);
	http.send();
	

}

