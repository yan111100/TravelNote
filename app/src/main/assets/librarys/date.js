Date.prototype.format = function(format){ 
    var o = { 
        "M+" : pad((this.getMonth()+1), 2), //month 
        "d+" : pad(this.getDate(), 2), //day 
        "h+" : pad(this.getHours(), 2), //hour 
        "m+" : pad(this.getMinutes(), 2), //minute 
        "s+" : pad(this.getSeconds(), 2), //second 
        "q+" : pad(Math.floor((this.getMonth()+3)/3), 1), //quarter 
        "S" : pad(this.getMilliseconds(), 3) //millisecond 
    } 

    if(/(y+)/.test(format)) { 
        format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
    } 

    for(var k in o) { 
        if(new RegExp("("+ k +")").test(format)) { 
            format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
        } 
    } 
    
    return format; 
} 

Date.prototype.toLocaleTimeString = function(){
    return this.format('h:m:s');
}

Date.prototype.toLocaleString = function(){
    return this.format('yyyy-M-d h:m:s');
}

Date.prototype.toLocaleDateString = function(){
    return this.format('yyyy-M-d');
}

function pad(num, n) {
    var len = num.toString().length;
    while(len < n) {
        num = "0" + num;
        len++;
    }
    return num;
}