/**
 * String Util
 *
 * @author sanghyun moon <shmoon@amuzlab.com>
 * @version 1.0
 * @date 2016-08-07
 */
'use strict';

/* replaceAll */
String.prototype.replaceAll = function(str1, str2, ignore) 
{
    return this.replace(new RegExp(str1.replace(/([\/\,\!\\\^\$\{\}\[\]\(\)\.\*\+\?\|\<\>\-\&])/g,"\\$&"),(ignore?"gi":"g")),(typeof(str2)=="string")?str2.replace(/\$/g,"$$$$"):str2);
} 

String.prototype.lpad = function(padLength, padString){
    var s = this;
    while(s.length < padLength)
        s = padString + s;
    return s;
}
Date.prototype.getFromFormat = function(format) {
    var yyyy = this.getFullYear().toString();
    format = format.replace(/yyyy/g, yyyy)
    var mm = (this.getMonth()+1).toString(); 
    format = format.replace(/mm/g, (mm[1]?mm:"0"+mm[0]));
    var dd  = this.getDate().toString();
    format = format.replace(/dd/g, (dd[1]?dd:"0"+dd[0]));
    var hh = this.getHours().toString();
    format = format.replace(/hh/g, (hh[1]?hh:"0"+hh[0]));
    var ii = this.getMinutes().toString();
    format = format.replace(/ii/g, (ii[1]?ii:"0"+ii[0]));
    var ss  = this.getSeconds().toString();
    format = format.replace(/ss/g, (ss[1]?ss:"0"+ss[0]));
    return format;
};
var Log = {
		info : function(msg){
			var consoleDiv    = $('#consoleDiv');
			var height = consoleDiv[0].scrollHeight;
			$("#consoleDiv").append("<div>["+Log.getDateTime()+"] "+msg+"</div>");
			consoleDiv.scrollTop(height);
		},
		error : function(msg) {
			$('#logTab').jqxTabs('select', 1); 
			var consoleDiv    = $('#errorLogDiv');
			var height = consoleDiv[0].scrollHeight;
			$("#errorLogDiv").append("<div>"+Log.getDateTime()+"] "+msg+"</div>");
			consoleDiv.scrollTop(height);
		},
		getDateTime: function() {
			var todayDate=new Date();
			var format ="AM";
			var hour=todayDate.getHours();
			var min=todayDate.getMinutes();
			var sec= todayDate.getSeconds();
			if(hour>11){format="PM";}
			//  if (hour   > 12) { hour = hour - 12; }
			  //if (hour   == 0) { hour = 12; }  
			  if (hour < 10){hour = "0" + hour;}
			  if (min < 10){min = "0" + min;}
			  if (sec < 10){sec = "0" + sec;}
			  return (todayDate.getFullYear()+"-"+todayDate.getMonth()+1 + "-" + todayDate.getDate() +" "+hour+":"+min+":"+sec);
		}
		
}

var confirmWindow = function(msg,isAuth) {
	if(isAuth == "true") {
		if(confirm(msg)) return true;
		else return false;
	} else {
		alert("권한이 없습니다.");
		return false;
	}
}