function cuspopup_window(url)
{ 
	var newWindow = window.open(url, '', 'width=800,height=570,top=100,left=100,toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no');
		newWindow.focus();
	     
}

function person_window(url)
{
	if(isIE())
	{
		 window.showModalDialog(url, window, 'dialogWidth=600px;dialogHeight=400px');
	}else{
		var newWindow = window.open(url, '', 'width=600,height=400,top=100,left=100,toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no');
		newWindow.focus();
	}
  
  
  //window.open(url,'','');
}

function getConfirmation(columnIndex){ 
	
        var mytitle ="";
        var isInput =0;
        if(columnIndex==undefined){

			if (event.srcElement.parentElement.parentElement.parentElement.children[2] == null)
			{
				return confirm("您确定要删除记录吗？");
			}
          //不传columnIndex参数时假定第三列为名称
       isInput = event.srcElement.parentElement.parentElement.parentElement.children[2].innerHTML;
                //判断是否含输入框
       if((isInput.indexOf("<INPUT"))>=0){
           mytitle = event.srcElement.parentElement.parentElement.parentElement.children[2].children[0].value;
        }else{
           mytitle = event.srcElement.parentElement.parentElement.parentElement.children[2].innerText;
    }
     }else{
		 if (event.srcElement.parentElement.parentElement.parentElement.children[columnIndex] == null)
			{
				return confirm("您确定要删除记录吗？");
			}
         isInput = event.srcElement.parentElement.parentElement.parentElement.children[columnIndex].innerHTML;
         if((isInput.indexOf("<INPUT"))==0){
         mytitle = event.srcElement.parentElement.parentElement.parentElement.children[columnIndex].children[0].value;
    }else{
        mytitle = event.srcElement.parentElement.parentElement.parentElement.children[columnIndex].innerText; 
  }
   }
   return confirm("您确定要删除:["+mytitle.replace(/\s*$/,"")+"]这条记录吗？");
   
}


function showConfirmation(info){
	return confirm("您确定要进行"+info+"操作");
}


function copy(txt)
{
    window.clipboardData.setData("Text",txt);

    alert("成功复制URL " +txt+" 到剪贴板");
}
function clearContent(componentname)
{   
	eval("document.all." + componentname + ".value='0'");
    
}
function isIE()
{
	var navigatorName = "Microsoft Internet Explorer";
    var isIE = false; 
    if( navigator.appName == navigatorName ){
		 isIE = true;    
    }   
	return isIE;
}