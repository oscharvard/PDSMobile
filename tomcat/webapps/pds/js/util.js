function deleteCookie(name, path, domain)
{
    if (getCookie(name))
    {
        document.cookie = name + "=" +
            ((path) ? "; path=" + path : "") +
            ((domain) ? "; domain=" + domain : "") +
            "; expires=Thu, 01-Jan-70 00:00:01 GMT";
    }
}

function getFrameSize()
{
	if (self.innerHeight) // all except IE
	{
		setCookie("navwidth",parent.frames[1].innerWidth);
		setCookie("citheight",parent.frames[0].innerHeight);
		setCookie("navscroll",parent.frames[1].window.pageYOffset);
	}
	else if (window.XMLHttpRequest) //IE 7
	{
		//disabled due to not being able to get correct dimensions to restore frame sizes 
		//setCookie("navwidth",parent.frames[1].document.documentElement.offsetWidth);
		//setCookie("citheight",parent.frames[0].document.documentElement.offsetHeight);
		setCookie("navscroll",parent.frames[1].document.documentElement.scrollTop);
	}
	else if (document.documentElement && document.documentElement.clientHeight) // IE 6 Strict Mode
	{
		setCookie("navwidth",parent.frames[1].document.documentElement.offsetWidth);
		setCookie("citheight",parent.frames[0].document.documentElement.offsetHeight);
		setCookie("navscroll",parent.frames[1].document.documentElement.scrollTop);
	}
	else if (document.body) // other Explorers
	{
		setCookie("navwidth",parent.frames[1].document.body.offsetWidth);
		setCookie("citheight",parent.frames[0].document.body.offsetHeight);
		setCookie("navscroll",parent.frames[1].document.body.scrollTop);	
	}	
}

function setFrameSize()
{
	width = readCookie('navwidth');
	height = readCookie('citheight');
	if(width==null) {
		width="226";
	}
	if(height==null) {
		height="136";
	}
	//var browser;
	//if(checkBrowser('safari')){
	//	browser = "Safari"
	//}
	//if(browser==null) { //for all browsers except safari
		top.document.getElementById('fs2').cols = width+", *";
		top.document.getElementById('fs1').rows = height+", *";
	//}
}

function scrollNav()
{
	window.scrollTo(0,readCookie('navscroll'));
}

function checkBrowser(string)
{
	var detect = navigator.userAgent.toLowerCase();
	place = detect.indexOf(string) + 1;
	thestring = string;
	return place;
}

function setCookie(name, value, expires, path, domain, secure)
{
    document.cookie= name + "=" + escape(value) +
    ((expires) ? "; expires=" + expires.toGMTString() : "") +
    ("; path=/pds") +
    ((domain) ? "; domain=" + domain : "") +
    ((secure) ? "; secure" : "");
}

function readCookie(name)
{
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++)
	{
		var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}
