// ---------------------- dev log 20190626: what can be improved ----------------------
// - need transition to make the change of graph smooth
// - belgium map need to be added
// - is there really 5 or so ports used? verify this! --- 7 ports, but the added 2 are rarely used
// - several locationid need map to unlo

var baseURL = "http://localhost:8080/Project/rest/sql";

// -- temp --
var unknowloc = [];

// ----- variable for slide bar -----
var slideBarOptions = null;
var slideBarLength = null;
var slideBar = document.querySelector('#timeslide');
var displayValue = document.getElementById("range");
var update = null;

// ----- variables for linestops data -----
var data = null;
var info1 = null;
var info2 = null;

// ----- variables for data processing functions -----
var getFiltered = null;
var getLocCount = null;
var getUnloCount = null;
var getDataPoint = null;


var result = null;

// =============== get unique sta_date =============== 
// use to populate options for slide bar
var http1 = new XMLHttpRequest();
var url1 = baseURL + "/uniquesta";
http1.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        // populate two slide bar js variables
        slideBarOptions = this.responseText.split(";");
        slideBarLength = slideBarOptions.length;

        // ------ slide bar prep: init value and slide bar length ----- 
        displayValue.innerHTML = slideBarOptions[0];
        slideBar.setAttribute('max', slideBarLength - 1);
    }
};
http1.open("GET", url1);
http1.send();

// =============== get linestops data ===============
// after data ready, prepare data processing functions
var http = new XMLHttpRequest();
var url = baseURL + "/linestops";
http.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        data = JSON.parse(this.responseText);

        // get subset of data so that dateString is inside [sta_date, std_date] 
        // helper func of getLocCount
        getFiltered = function() {
            var slideBarValue = document.getElementById('range').innerHTML;
            // var slideBarValue = "2016-02-12";
            var filtered = data.filter(function(data){
                return (data.sta_date == slideBarValue) || (data.sta_date < slideBarValue && slideBarValue <= data.std_date);
            })
            return filtered;
        }

        // helper func of getUnloCount
        // return [{key:xxxx, value:yyyy}, ...]
        // where key is locationid, value is count(on that day how many linestopid is at the location)
        getLocCount = function(data) {
            var count = d3.nest()
                          .key(function(data) {return data.locationid;})
                          .rollup(function(v) {return v.length;})
                          .entries(getFiltered());  
            return count;
        }

        // ------------- load two local json -------------
        // d3.json('./dummyMapData/locationid_to_unlo.json', function(data){
        d3.json('./dummyMapData/locationid_to_unlo_n.json', function(data){
            info1 = data;
            // helper func of getDataPoint
            getUnloCount = function(){
                var unloCount = {};
                var locCount = getLocCount();
                locCount.forEach( ele => {
                   // if this location id doesnt have a unlo mapping in info1
                   if (info1[ele.key] == undefined){
                      // console.log('locationid ' + ele.key + ' doestn have unlo in info1');
                      unknowloc.push(ele.key);
                   } else {
                      if (unloCount[info1[ele.key]] == undefined){
                          unloCount[info1[ele.key]] = ele.value;
                      } else {
                          unloCount[info1[ele.key]] += ele.value;
                      }
                   }
                });
                return unloCount;
            }
        })

        d3.json('./dummyMapData/unlo_to_latlong_n.json', function(data){
            info2 = data;
            // output: an array of json, will be used to draw geo bubble
            //         each json is {city: nn, latitude: xx, longitude: yy, count: cc}
            getDataPoint = function(){
                var dataPoint =  [];
                var unloCount = getUnloCount();
                Object.keys(unloCount).forEach( key => {
                  if (info2[key] == undefined){
                    console.log(key + 'is not in info2');
                  } else{
                    var dp = JSON.stringify(info2[key]).slice(0, -1);
                    dp += ',';
                    dp += '"count":'
                    dp += unloCount[key];
                    dp += '}'
                    dataPoint.push(JSON.parse(dp));
                  }

                })
                return dataPoint;
            }
        })

        // ==========  Map Prep: svg area ==========  
//        var width = 650 ;
        var width = d3.select('.mapGraph').node().getBoundingClientRect().width / 2;
        var	height = 580

        var svg = d3.select('#mapContainer')
                         .append('svg')
                         .attr('width', width)
                         .attr('height', height) 

        // ==========  Map Prepare: projection ==========  
        var projection = d3.geoMercator()
            .center([5, 52])                // GPS of location to zoom on
            .scale(5000)                       // This is like the zoom
            .translate([ width/2, height/2 ])

        // ==========  Circle prepare: scale for circle size ==========  
        var size = d3.scaleLinear()
          .domain([0, 15])  // per day there's at most 15 barge at any city
          .range([ 0, 50])  // Size in pixel


        // ==========  Tooltip prepare: area ==========  
        var Tooltip = d3.select('body')
                        .append('div')
                        .attr('class', 'tooltip')
                        .style('position', 'absolute')
                        .style('visibility', 'hidden')
                        .style('background-color', 'white')
                        .style('border', 'solid')
                        .style('border-width', '1px')
                        .style('border-radius', '5px')

        // ==========  Tooltip prepare: interaction ==========  
        var mouseover = function(d){
          // Tooltip.style('opacity', 1)
          Tooltip.style('visibility', 'visible')
        }
        var mousemove = function(d) {
          Tooltip.html('<b>' + d.city + '</b>' + '<br>' +
                       '#barge: '+ d.count)
                 // .style('left', (d3.mouse(this)[0]+10) + 'px')
                 // .style('top', (d3.mouse(this)[1]) + 'px')
                 .style("top", (event.pageY-10)+"px")
                 .style("left",(event.pageX+10)+"px");
        }
        var mouseleave = function(d){
          // Tooltip.style('opacity', 0)
          Tooltip.style('visibility', 'hidden')
        }


        // ==========  Slide Bar Prepare: update behavior ==========  

        function update(value) {
            displayValue.innerHTML = slideBarOptions[value];

            // remove old data
            d3.selectAll("circle").remove();

            // add new data
            svg
              .selectAll("circle")
              .data(getDataPoint())
              .enter()
              .append("circle")
                .attr("cx", function(d){ return projection([d.longitude, d.latitude])[0] })
                .attr("cy", function(d){ return projection([d.longitude, d.latitude])[1] })
                .attr("r", function(d) {
                              return size(d.count)})
                .style("fill", "69b3a2")
                .attr("stroke", "#69b3a2")
                .attr("stroke-width", 3)
                .attr("fill-opacity", .4)
                .on('mouseover', mouseover)
                .on('mousemove', mousemove)
                .on('mouseleave', mouseleave)
        }

        // ========== draw Map, Circle, activate Slide bar ========== 
        d3.json("https://www.webuildinternet.com/articles/2015-07-19-geojson-data-of-the-netherlands/townships.geojson", function(data){

            // Draw the map
            svg.append("g")
                .selectAll("path")
                .data(data.features)
                .enter()
                .append("path")
                  .attr("fill", "PeachPuff")
                  .attr("d", d3.geoPath()
                      .projection(projection)
                  )
                .style("stroke", "white")
                .style("stroke-width", ".5")
                .style("stroke-opacity", ".5")

            // Add circles only after draw map is done
            svg
              .selectAll("circle")
              .data(getDataPoint())
              .enter()
              .append("circle")
                .attr("cx", function(d){ return projection([d.longitude, d.latitude])[0] })
                .attr("cy", function(d){ return projection([d.longitude, d.latitude])[1] })
                .attr("r", function(d) {
                              return size(d.count)})
                .style("fill", "69b3a2")
                .attr("stroke", "#69b3a2")
                .attr("stroke-width", 3)
                .attr("fill-opacity", .4)
                .on('mouseover', mouseover)
                .on('mousemove', mousemove)
                .on('mouseleave', mouseleave)

            // Activate slide bar
            d3.select("#timeslide").on("input", function() {
                update(+this.value);
            });
        })
}};

http.open("GET", url);
http.send();

// -------------- leaflet.js --------------

// Initialize the map
// [50, -0.1] are the latitude and longitude
// 4 is the zoom
// mapid is the id of the div where the map will appear
// var mymap = L
//   .map('mapid')
//   .setView([50, -0.1], 3);

// // Add a tile to the map = a background. Comes from OpenStreetmap
// L.tileLayer(
//     'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
//     attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a>',
//     maxZoom: 6,
//     }).addTo(mymap);

