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
function getHex(length) {
	if (length > 12) {
		return null;
	} else {
		return COLOR_HEX_12.slice(0,length-1);
	}
}

function getRGB(length) {
	if (length > 12) {
		return null;
	} else {
		var temp = getHex(length);
		var res = [];
		for (var i = 0; i < length; i ++) {
			var hex = temp[i];
			var i1 = parseInt(hex.substring(1,3),16);
			var i2 = parseInt(hex.substring(3,5),16);
			var i3 = parseInt(hex.substring(5,7),16);
			res.push("rgba(" + i1 + "," + i2 + "," + i3 + ")");
		}
		return res;
	}
}
