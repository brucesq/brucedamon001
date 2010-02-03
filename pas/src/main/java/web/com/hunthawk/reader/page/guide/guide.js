var wml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">\r\n<wml>\r\n<card title=\"$#title#\">\r\n<p>\r\n\r\n</p>\r\n</card>\r\n</wml>";
var wml_wap2x = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.0//EN\" \"http://www.wapforum.org/DTD/xhtml-mobile10.dtd\">\r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n<head>\r\n\t\<title>$#title#<\/title>\r\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" \/>\r\n<\/head>\r\n<body>\r\n\r\n\r\n\r\n<\/body>\r\n<\/html>";
var wml_mobile = "<aspire>\r\n\r\n\r\n\r\n</aspire>";
var wml_jsClient = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xpc xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"\">\r\n\r\n\r\n\r\n<\/xpc>";
	
var back = "<do type=\"prev\" label=\"Back\"><prev/></do>\r\n";

var pp = "<p>\r\n\r\n</p>\r\n";

var home = "<a href=\"http://wap.uni-wise.com/\">联通首页#</a>\r\n";

var link_normal = "请输入链接显示的文字";

var link_normal_input = "请输入 URL";

var br = "<br/>";
function AddText(Doc,NewCode) 
{
        if(Doc.all)
		{
        	insertAtCaret(Doc.forms[0].content, NewCode);
			Doc.forms[0].content.focus();
        } else{
        	Doc.forms[0].content.value += NewCode;
			Doc.forms[0].content.focus();
        }
}

function updateText(Doc,OldTag,Num,NewCode) 
{
	var content = Doc.forms[0].content.value;
	Doc.forms[0].content.value = content.replace(OldTag,NewCode);
    
}
function insertAtCaret (textEl, text)
{

        if (textEl.createTextRange && textEl.caretPos){
                var caretPos = textEl.caretPos;
                caretPos.text += caretPos.text.charAt(caretPos.text.length - 2) == ' ' ? text + ' ' : text;
        } else if(textEl) {
                textEl.value += text;
        } else {
        	textEl.value = text;
        }
}
function storeCaret (textEl){
        if(textEl.createTextRange){
                textEl.caretPos = document.selection.createRange().duplicate();
        }
}
function hyperlink() 
{
		txt=prompt(link_normal,"");

		if (txt!=null) 
		{
			url=prompt(link_normal_input,"http://");
			if (url!=null) 
			{	
				AddTxt="<a href=\""+url+"\">"+txt+"</a>";
				AddText(document,AddTxt);
			}
		}
}

function changeContent(){
	if(document.all.wapType.value == "0"){
		document.all.content.value = wml;
	}else if(document.all.wapType.value == "1"){
		document.all.content.value = wml_wap2x;
	}else if(document.all.wapType.value == "2"){
		document.all.content.value = wml_mobile;
	}else if(document.all.wapType.value == "3"){
		document.all.content.value = wml_jsClient;
	}
	document.all.content.focus();
}

function image() 
{
	url = prompt(link_normal_input,"http://");

	if (url!=null) 
	{
		txt=prompt(link_normal,"");			
		AddTxt="<img src=\""+url+"\" alt=\""+txt+"\"/>";
		AddText(document,AddTxt);			
	}
}

function initWML()
{
	if(document.forms[0].content.value=="")
	{
		AddText(document,wml,0);
	}
}