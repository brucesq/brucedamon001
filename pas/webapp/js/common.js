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
				return confirm("��ȷ��Ҫɾ����¼��");
			}
          //����columnIndex����ʱ�ٶ�������Ϊ����
       isInput = event.srcElement.parentElement.parentElement.parentElement.children[2].innerHTML;
                //�ж��Ƿ������
       if((isInput.indexOf("<INPUT"))>=0){
           mytitle = event.srcElement.parentElement.parentElement.parentElement.children[2].children[0].value;
        }else{
           mytitle = event.srcElement.parentElement.parentElement.parentElement.children[2].innerText;
    }
     }else{
		 if (event.srcElement.parentElement.parentElement.parentElement.children[columnIndex] == null)
			{
				return confirm("��ȷ��Ҫɾ����¼��");
			}
         isInput = event.srcElement.parentElement.parentElement.parentElement.children[columnIndex].innerHTML;
         if((isInput.indexOf("<INPUT"))==0){
         mytitle = event.srcElement.parentElement.parentElement.parentElement.children[columnIndex].children[0].value;
    }else{
        mytitle = event.srcElement.parentElement.parentElement.parentElement.children[columnIndex].innerText; 
  }
   }
   return confirm("��ȷ��Ҫɾ��:["+mytitle.replace(/\s*$/,"")+"]������¼��");
   
}


function showConfirmation(info){
	return confirm("��ȷ��Ҫ����"+info+"����");
}


function copy(txt)
{
    window.clipboardData.setData("Text",txt);

    alert("�ɹ�����URL " +txt+" ��������");
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