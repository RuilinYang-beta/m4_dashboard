var COLOR_HEX_12 = [
		"#1395BA",
		"#117899",
		"#0F5B78",
		"#0D3C55",
		"#C02E1D",
		"#D94E1F",
		"#F16C20",
		"#EF8B2C",
		"#ECAA38",
		"#EBC844",
		"#A2B86C",
		"#5CA793"];
var COLOR_HEX_2ND = [
		"#89493D",
		"#9D535F",
		"#A26686",
		"#947FA9",
		"#749AC1",
		"#4DB4C6",
		"#40CAB8",
		"#6BDC9D",
		"#AAE97E",
		"#EFEE69"];
var COLOR_HEX_COF = [
		"#f69519",
		"#D68826",
		"#B67C32",
		"#956F3F",
		"#75634B",
		"#555658"];
var mem = -1;

// Takes a random hexadecimal number close to the colors above 
function getHex(length) {
	var i = Math.floor(Math.random() * 6);
	if (mem != -1) {
		while (mem-i >= -2 && i - mem >= -2) {
			i = (i+1) % 6;
		}
	}
	if (length > 12) {
		return COLOR_HEX_COF.slice(i,i+1);
	} else {
		return COLOR_HEX_12.slice(0,length);
	}
}

// Convert the result from the above function into rgb value. 
function getRGB(length) {
	var temp = getHex(length);
	var res = [];
	var len = 1;
	if (length <= 12) len = length;
	for (var i = 0; i < len; i ++) {
		var hex = temp[i];
		var i1 = parseInt(hex.substring(1,3),16);
		var i2 = parseInt(hex.substring(3,5),16);
		var i3 = parseInt(hex.substring(5,7),16);
		res.push("rgba(" + i1 + "," + i2 + "," + i3 + ")");
	}
	return res[0];
}
