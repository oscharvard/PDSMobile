/***********************************************
* Dynamic expose and hide script 
* adapted from Dynamic Drive (www.dynamicdrive.com)
*  AND from 
*   DHTML library (http://www.dhtmlcentral.com/tutorials/tutorials.asp?page=1&id=3)
*  Written 07/03/2001 by Thomas Brattli and Sergi Meseguer. Last updated 07/10/2001.
* Bobbi
***********************************************/



//Browsercheck (needed) 
function lib_bwcheck(){ 
  this.ver=navigator.appVersion
  this.agent=navigator.userAgent
  this.dom=document.getElementById?1:0
  this.opera5=this.agent.indexOf("Opera 5")>-1
  this.ie5=(this.ver.indexOf("MSIE 5")>-1 && this.dom && !this.opera5)?1:0; 
  this.ie6=(this.ver.indexOf("MSIE 6")>-1 && this.dom && !this.opera5)?1:0;
  this.ie7=(this.ver.indexOf("MSIE 7")>-1 && this.dom && !this.opera5)?1:0;
  this.ie4=(document.all && !this.dom && !this.opera5)?1:0;
  this.ie=this.ie4||this.ie5||this.ie6
  this.mac=this.agent.indexOf("Mac")>-1
  this.ns6=(this.dom && parseInt(this.ver) >= 5) ?1:0; 
  this.ns4=(document.layers && !this.dom)?1:0;
  this.bw=(this.ie6||this.ie5||this.ie4||this.ns4||this.ns6||this.opera5|| this.ie7)
  return this
}

bw=new lib_bwcheck() //Browsercheck object


function getElement(elem) {
   var ret = null;
	if(bw.ns4) ret=document.layers[elem]
  	else if(bw.ie4) ret=document.all[elem]
	else if(document.getElementById) 
	{ ret = document.getElementById(elem);}
	return ret;
}

function getElementsByName(elem) {
	var ret = new Array();
	if (document.getElementsByName) ret = document.getElementsByName(elem);
	else {
		for (var i = 0; i < document.all.length; i++)
		{
			var obj = document.all[i];
			if (obj.name == elem) {
				ret[ret.length] =obj;
			}
		}
	}
	return ret;
}
function getElementByName(elem) {
	var arr = getElementsByName(elem);
	if (arr !=null && arr.length  == 1)
		return arr[0];
	else
		return null;
}
function getFormElementsByName(name) 
	{
		var retArr = new Array();
		var inx = 0;
		for (var i = 0; i < document.forms[0].length; i++)
		{
			if (document.forms[0][i].name == name) 
			{
				retArr[retArr.length] = document.forms[0][i];
			}	

		}
		return retArr;
	}
	
	function enableDisable(items, disable)
	{
		for (var j = 0; j < items.length; j++)
		{
			arr=getFormElementsByName(items[j]);

			for (var i = 0; i < arr.length; i++)
			{

				arr[i].disabled =  disable;

			}		
		}
	}
	
function hideElement(elem, hide) {
	var eid = getElement(elem);
	if (eid !=null) 
		eid.style.display=(hide)?"none":"block";
	}
function getElementbyClass(classname){
	ccollect=new Array();
	var inc=0;
	var alltags=document.all? document.all : document.getElementsByTagName("*");
	for (i=0; i<alltags.length; i++){
		if (alltags[i].className==classname)
			ccollect[inc++]=alltags[i];
	}
}

function contractcontent(omit){
	var inc=0;
	while (ccollect[inc]){
	if (ccollect[inc].id!=omit)
		ccollect[inc].style.display="none";
	inc++
	}
}

function expandcontent(cid){

	if (typeof ccollect!="undefined"){
		contractcontent(cid);
		if (getElement(cid) != null) 
			getElement(cid).style.display=(getElement(cid).style.display!="block")? "block" : "none";
	}
}
function radioValue(elem) {

   for (var i = 0; i<elem.length; i++) 
   {
   	if (elem[i].checked) {
 
   		return elem[i].value;
   		}
   	}
   	return 0;
}

function do_dyn_load(what){

	getElementbyClass(what);
}



