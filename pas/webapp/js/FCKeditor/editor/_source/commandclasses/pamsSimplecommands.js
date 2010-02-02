/**
 *
 *处理不弹对话框命令
 */

var FCKSimpleCommand = function( wmlvalue )
{
	this.wmlvalue = wmlvalue ;


}


FCKSimpleCommand.prototype =
{
	Execute : function()
	{

        var tagAll = "$#"+ this.wmlvalue+"#";

		var indexPos = tagAll.indexOf(".");

		//获得标签的名称
							if (indexPos > -1)
							{
								 tagEnname = tagAll.substr(2,tagAll.indexOf(".")-2);
							}
							else
							{
								 tagEnname = tagAll.substr(2,tagAll.length-3);
							}

		if (FCKTagNames.getTagType(tagEnname) == 'Dialog')
		{
                             
							
		   
			
			    
			    diaAttr = 'dialogWindow="dialogWindow"';
			
		}
		else
		{
		    var tagEnname = tagAll.substr(2,tagAll.length-3);
		}

		//发送请求
		FillTerritory(tagAll);
		//alert("返回值1" + tagResponseValue);
	        //alert("simplecommands.js");
		var oActiveEl = FCK.EditorDocument.createElement( 'Fieldset' ) ;
		oActiveEl.type = "Fieldset" ;
		oActiveEl = FCK.InsertElement( oActiveEl ) ;
		
		//oActiveEl.innerHTML = '<legend>66666666666666</legend>';
		//oActiveEl.innerHTML = '<legend>'+ FCKTagNames.GetName(this.wmlvalue) + '</legend>';
		oActiveEl.innerHTML = '<legend>' + FCKTagNames.GetName(this.wmlvalue) + '</legend>' + tagResponseValue + '&nbsp;';
		//oActiveEl.innerText='&nbsp;';
		oActiveEl.style.display = 'inline' ;
		
		SetAttribute( oActiveEl, 'name', FCKTagNames.GetName(tagEnname) ) ;
		SetAttribute( oActiveEl, 'contenteditable', 'false' ) ;
		SetAttribute( oActiveEl, 'title', tagEnname) ;
		//SetAttribute( oActiveEl, 'onclick', "return false;") ;
		oActiveEl._aspirepamstag = tagAll;
		// Save an undo snapshot before doing anything.
		//FCKUndo.SaveUndoStep() ;

		//FCK.Focus() ;
		//FCK.SwitchEditMode() ;
	
        //FCK.SwitchEditMode() ;
	},

	GetState : function()
	{
		// Disabled if not WYSIWYG.
		if ( FCK.EditMode != FCK_EDITMODE_WYSIWYG || ! FCK.EditorWindow )
			return FCK_TRISTATE_DISABLED ;
		
		return FCK_TRISTATE_OFF ;
	}
} ;


function SetAttribute( element, attName, attValue )
{
	if ( attValue == null || attValue.length == 0 )
		element.removeAttribute( attName, 0 ) ;			// 0 : Case Insensitive
	else
		element.setAttribute( attName, attValue, 0 ) ;	// 0 : Case Insensitive
}