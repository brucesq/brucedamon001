/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2007 Frederico Caldeira Knabben
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * Creation and initialization of the "FCK" object. This is the main object
 * that represents an editor instance.
 */

//标签解析后返回的值
var  tagResponseValue;

//标签之间的分割符
var TAG_SEEARAAOR= "[tag-separator]";

    function FillTerritory(tagCollection)
	{

		//栏目或产品或专题id
		var referenceId = window.parent.document.all.referenceId.value;

		//栏目所属产品的id
		var columnResideProductId = window.parent.document.all.columnResideProductId.value;

		//类型，用于区分是产品，栏目还是专题
		var referenceType = window.parent.document.all.referenceType.value;

		//模板类型id,需要转化成l等样式
		var templateTypeId = window.parent.document.all.templateTypeId.value;

		//预览的时候需要带的参数
		var viewpara = "";

		if (referenceType > 0)
		{
			//如果是产品,则只需要带一个参数
		    if (referenceType == 999)
		    {
                viewpara = "?p="  + referenceId;
		    }
		    else
		    {
			    viewpara = "?p=" + columnResideProductId + "&c=" + referenceId;
		    }
		}

		
        
        //预览服务器的地址
		var url = window.parent.frontaddress + viewpara;
		//alert(url);

		tagCollection = "<wml>" + tagCollection;

        var strParams = "tmpl=" + encodeURIComponent(tagCollection);



        var loader1 = new
        net.ContentLoader(url,FillDropDown,null,
                    "POST",strParams);

      }
      function FillDropDown()
	  {
	 
           tagResponseValue = this.req.responseText;

		  if (tagResponseValue.indexOf("<wml>") == 0)
		  {
		      tagResponseValue = tagResponseValue.substr(5,tagResponseValue.length-5);
		  }

       
      }

// FCK represents the active editor instance.
var FCK =
{
	Name			: FCKURLParams[ 'InstanceName' ],
	Status			: FCK_STATUS_NOTLOADED,
	EditMode		: FCK_EDITMODE_WYSIWYG,
	Toolbar			: null,
	HasFocus		: false,
	DataProcessor	: new FCKDataProcessor(),

	AttachToOnSelectionChange : function( functionPointer )
	{
		this.Events.AttachEvent( 'OnSelectionChange', functionPointer ) ;
	},

	GetLinkedFieldValue : function()
	{     // alert("this.LinkedField.value=="+this.LinkedField.value);
		return this.LinkedField.value ;
	},

	GetParentForm : function()
	{
		return this.LinkedField.form ;
	} ,

	// # START : IsDirty implementation

	StartupValue : '',

	IsDirty : function()
	{
		if ( this.EditMode == FCK_EDITMODE_SOURCE )
			return ( this.StartupValue != this.EditingArea.Textarea.value ) ;
		else
		{
			// It can happen switching between design and source mode in Gecko
			if ( ! this.EditorDocument )
				return false ;

			return ( this.StartupValue != this.EditorDocument.body.innerHTML ) ;
		}
	},

	ResetIsDirty : function()
	{
		if ( this.EditMode == FCK_EDITMODE_SOURCE )
			this.StartupValue = this.EditingArea.Textarea.value ;
		else if ( this.EditorDocument.body )
			this.StartupValue = this.EditorDocument.body.innerHTML ;
	},

	// # END : IsDirty implementation

	StartEditor : function()
	{     // alert("StartEditor");
		this.TempBaseTag = FCKConfig.BaseHref.length > 0 ? '<base href="' + FCKConfig.BaseHref + '" _fcktemp="true"></base>' : '' ;

		// Setup the keystroke handler.
		var oKeystrokeHandler = FCK.KeystrokeHandler = new FCKKeystrokeHandler() ;
		oKeystrokeHandler.OnKeystroke = _FCK_KeystrokeHandler_OnKeystroke ;

		// Set the config keystrokes.
		oKeystrokeHandler.SetKeystrokes( FCKConfig.Keystrokes ) ;

		// In IE7, if the editor tries to access the clipboard by code, a dialog is
		// shown to the user asking if the application is allowed to access or not.
		// Due to the IE implementation of it, the KeystrokeHandler will not work
		//well in this case, so we must leave the pasting keys to have their default behavior.
		if ( FCKBrowserInfo.IsIE7 )
		{
			if ( ( CTRL + 86 /*V*/ ) in oKeystrokeHandler.Keystrokes )
				oKeystrokeHandler.SetKeystrokes( [ CTRL + 86, true ] ) ;

			if ( ( SHIFT + 45 /*INS*/ ) in oKeystrokeHandler.Keystrokes )
				oKeystrokeHandler.SetKeystrokes( [ SHIFT + 45, true ] ) ;
		}

		// Retain default behavior for Ctrl-Backspace. (Bug #362)
		oKeystrokeHandler.SetKeystrokes( [ CTRL + 8, true ] ) ;

		this.EditingArea = new FCKEditingArea( document.getElementById( 'xEditingArea' ) ) ;
		this.EditingArea.FFSpellChecker = FCKConfig.FirefoxSpellChecker ;

		// Set the editor's startup contents.
		
		this.SetData( this.GetLinkedFieldValue(), true ) ;

		// Tab key handling for source mode.
		FCKTools.AddEventListener( document, "keydown", this._TabKeyHandler ) ;

		// Add selection change listeners. They must be attached only once.
		this.AttachToOnSelectionChange( _FCK_PaddingNodeListener ) ;
		if ( FCKBrowserInfo.IsGecko )
			this.AttachToOnSelectionChange( this._ExecCheckEmptyBlock ) ;

	},

	Focus : function()
	{
		FCK.EditingArea.Focus() ;
	},

	SetStatus : function( newStatus )
	{
		this.Status = newStatus ;
	        //alert('newStatus='+newStatus);
		if ( newStatus == FCK_STATUS_ACTIVE )
		{
			FCKFocusManager.AddWindow( window, true ) ;

			if ( FCKBrowserInfo.IsIE )
				FCKFocusManager.AddWindow( window.frameElement, true ) ;

			// Force the focus in the editor.
			if ( FCKConfig.StartupFocus )
				FCK.Focus() ;
		}

		this.Events.FireEvent( 'OnStatusChange', newStatus ) ;

	},

	// Fixes the body by moving all inline and text nodes to appropriate block
	// elements.
	FixBody : function()
	{
		var sBlockTag = FCKConfig.EnterMode ;

		// In 'br' mode, no fix must be done.
		if ( sBlockTag != 'p' && sBlockTag != 'div' )
			return ;

		var oDocument = this.EditorDocument ;

		if ( !oDocument )
			return ;

		var oBody = oDocument.body ;

		if ( !oBody )
			return ;

		FCKDomTools.TrimNode( oBody ) ;

		var oNode = oBody.firstChild ;
		var oNewBlock ;

		while ( oNode )
		{
			var bMoveNode = false ;

			switch ( oNode.nodeType )
			{
				// Element Node.
				case 1 :
					if ( !FCKListsLib.BlockElements[ oNode.nodeName.toLowerCase() ] && 
							!oNode.getAttribute('_fckfakelement') &&
							oNode.getAttribute('_moz_dirty') == null )
						bMoveNode = true ;
					break ;

				// Text Node.
				case 3 :
					// Ignore space only or empty text.
					if ( oNewBlock || oNode.nodeValue.Trim().length > 0 )
						bMoveNode = true ;
			}

			if ( bMoveNode )
			{
				var oParent = oNode.parentNode ;

				if ( !oNewBlock )
					oNewBlock = oParent.insertBefore( oDocument.createElement( sBlockTag ), oNode ) ;

				oNewBlock.appendChild( oParent.removeChild( oNode ) ) ;

				oNode = oNewBlock.nextSibling ;
			}
			else
			{
				if ( oNewBlock )
				{
					FCKDomTools.TrimNode( oNewBlock ) ;
					oNewBlock = null ;
				}
				oNode = oNode.nextSibling ;
			}
		}

		if ( oNewBlock )
			FCKDomTools.TrimNode( oNewBlock ) ;
	},

	GetData : function( format )
	{
		// We assume that if the user is in source editing, the editor value must
		// represent the exact contents of the source, as the user wanted it to be.
		if ( FCK.EditMode == FCK_EDITMODE_SOURCE )
		{
			        //alert('GetData of Textarea.value in code model fck,js===='+FCK.EditingArea.Textarea.value);
			       
				return FCK.EditingArea.Textarea.value ;
	         }

		this.FixBody() ;

		var oDoc = FCK.EditorDocument ;
		if ( !oDoc )
			return null ;

		var isFullPage = FCKConfig.FullPage ;

		// Call the Data Processor to generate the output data.
		//alert(" oDoc.body=="+ oDoc.body);
		//alert(" oDoc.documentElement=="+ oDoc.documentElement);
		var data = FCK.DataProcessor.ConvertToDataFormat(
			isFullPage ? oDoc.documentElement : oDoc.body,
			!isFullPage,
			FCKConfig.IgnoreEmptyParagraphValue,
			format ) ;
		//alert('after ConvertToDataFormat data='+data );

		// Restore protected attributes.
		data = FCK.ProtectEventsRestore( data ) ;

		if ( FCKBrowserInfo.IsIE )
			data = data.replace( FCKRegexLib.ToReplace, '$1' ) ;

		if ( isFullPage )
		{
			if ( FCK.DocTypeDeclaration && FCK.DocTypeDeclaration.length > 0 )
				data = FCK.DocTypeDeclaration + '\n' + data ;

			if ( FCK.XmlDeclaration && FCK.XmlDeclaration.length > 0 )
				data = FCK.XmlDeclaration + '\n' + data ;
		}



        /******************add by dingjiangtao **************/


data = data.replace(/<fieldset\s.+_aspirepamstag="(.+?)"[\s\S]+?<\/fieldset>/gi,function (){
//			alert("共有 "+arguments.length+" 个参数");
//			for (var i=0;i<arguments.length;i++){
//				alert("第"+i+"个参数的值："+arguments[i]);
//			}
			//alert($1);
			return FCKTools.HTMLDecode(arguments[1]);
		});

		
		/////
		//去掉FCKEDitor加的_fcksavedurl属性
		data = data.replace(/\s_fcksavedurl="[\s\S]+?"/gi,'');

//		data = data.replace(/<(.+?)\s*?[\s\S]*?>/gi,function(){
//			alert("共有 "+arguments.length+" 个参数");
//			for (var i=0;i<arguments.length;i++){
//				alert("第"+i+"个参数的值："+arguments[i]);
//			}
//			return arguments[0].substring(0,arguments[2]+1)+arguments[1].toLowerCase()+arguments[0].substring(arguments[2]+arguments[1].length+1);
//		});
	
		//将没有属性的tag变为小写
		data = data.replace(/(<[^\s]+?>)/gi,function(){
//			alert("共有 "+arguments.length+" 个参数");
//			for (var i=0;i<arguments.length;i++){
//				alert("第"+i+"个参数的值："+arguments[i]);
//			}
			return arguments[1].toLowerCase();
		});

		//将有属性的tag变为小写
		data = data.replace(/(<[^\>]+?\s+?)/gi,function(){
			return arguments[1].toLowerCase();
		});

		//解决光标定位的问题

       // data = data.replace(/<customer:aspire>/gi,"<aspire>");
	//	data = data.replace(/<\/customer:aspire>/gi,"</aspire>");

		//data = data.replace(/<\?xml:namespace prefix = customer \/>/gi,"");

		

		//解决p标签的问题
		data = data.replace(new RegExp("<code>","gm"),"<p>");
		data = data.replace(new RegExp("<code><br>","gm"),"<p>");
		data = data.replace(new RegExp("<\/code>","gm"),"</p>");
		//data = data.replace(/</aspiretag_p>/gi,"</p>");
	   
		/****end*8888888**************************************/

		//江苏客户端
		//alert("data:" + data);
        data = data.replace(/<samp>/gi,"");
        data = data.replace(/<\/samp>/gi,"");

		data = data.replace(/<br>/gi,"<br/>");

		data = data.replace(/\|\|-/gi,"<");
		data = data.replace(/-\|\|/gi,">");

       // alert("要处理的数据:" + data);

	    //对html中的标签做还原处理,也就是说html中的标签不需要做处理
	   data = data.replace(new RegExp("=\"#---@","gm"),"=\"$#");

		//将图片替换为分页符
		 
		
		 data = data.replace(new RegExp("<img class=FCK__PageBreak src=\"" + FCKConfig.FullBasePath + "images/spacer.gif\" _fckfakelement=\"true\">","gm"),"#aspire page split tag#");


		//关闭html标签
		data = data.replace(/<img(.+?)>/gi,function() {return "<img " + arguments[1] + " />"});

		//处理input标签
		//data =  data.replace(/<input(.*?)>/,function() {var inputattr = arguments[1].replace(/(\s+\w+=)([^\s<>]+)/g, "$1\"$2\""); return "<input " + inputattr + " />";});

        //解决丢失属性的引号的问题
		 data =  data.replace(/<([^>]*?)\s+?(.*?)>/gi,function() {
		 var inputattr = arguments[2].replace(/(\s*\w+=)([^\s<>]+)/g, 
			 function() {if(arguments[0].indexOf("\"") > -1 || arguments[0].indexOf("'") > -1) {return arguments[0]} else {return arguments[1] + "\"" + arguments[2] + "\""}}); 
		  
		  return "<" + arguments[1] + " "  + inputattr + ">";});


		 //解决input丢失关闭符号的问题
		 data = data.replace(/<input(.*?)>/gi,function() {return arguments[0].substr(0,arguments[0].length-1) + " />"});



		 //解决<br/>定位不准的问题
		  
		 //先匹配链接
 		data = data.replace(/<a\s+[\s\S]+?<\/a>/gi,function(){
 
 		var link =arguments[0];
 		link = link.replace(/<a\s+href=([\s\S]+?)>(<[\s\S]+?>)([\s\S]+?)<\/a>/gi,function()
		{
			  
                  var newlink =arguments[2] +  '<a href=' + arguments[1] + '>' + arguments[3] + '</a>';
 
                  return newlink;
		});
 
 		return link;});

		
		//由于换行符在opera上会显示一个不可见字符，所以需要替换掉		  
		data=data.replace(/\r\n/gi,"");

		//alert("data in view model and before FCKConfig.ProtectedSource.Revert===\n"+data)
		data = FCKConfig.ProtectedSource.Revert( data );

		
		if ( FCKConfig.ForceSimpleAmpersand )
			data = data.replace( FCKRegexLib.ForceSimpleAmpersand, '&' ) ;
		return FCKCodeFormatter.Format( data )

	},

	UpdateLinkedField : function()
	{
		var value = FCK.GetXHTML( FCKConfig.FormatOutput ) ;

		if ( FCKConfig.HtmlEncodeOutput )
			value = FCKTools.HTMLEncode( value ) ;

		FCK.LinkedField.value = value ;
		FCK.Events.FireEvent( 'OnAfterLinkedFieldUpdate' ) ;
	},

	RegisteredDoubleClickHandlers : new Object(),

	OnDoubleClick : function( element )
	{
		var oHandler = FCK.RegisteredDoubleClickHandlers[ element.tagName ] ;
		if ( oHandler )
			oHandler( element ) ;
	},

	// Register objects that can handle double click operations.
	RegisterDoubleClickHandler : function( handlerFunction, tag )
	{
		FCK.RegisteredDoubleClickHandlers[ tag.toUpperCase() ] = handlerFunction ;
	},

	OnAfterSetHTML : function()
	{       //alert("OnAfterSetHTML==="+FCK.EditorDocument );
		FCKDocumentProcessor.Process( FCK.EditorDocument ) ;
		FCKUndo.SaveUndoStep() ;

		FCK.Events.FireEvent( 'OnSelectionChange' ) ;
		FCK.Events.FireEvent( 'OnAfterSetHTML' ) ;
	},

	// Saves URLs on links and images on special attributes, so they don't change when
	// moving around.
	ProtectUrls : function( html )
	{
		// <A> href
		html = html.replace( FCKRegexLib.ProtectUrlsA	, '$& _fcksavedurl=$1' ) ;

		// <IMG> src
		html = html.replace( FCKRegexLib.ProtectUrlsImg	, '$& _fcksavedurl=$1' ) ;

		// <AREA> href
		html = html.replace( FCKRegexLib.ProtectUrlsArea	, '$& _fcksavedurl=$1' ) ;

		return html ;
	},

	// Saves event attributes (like onclick) so they don't get executed while
	// editing.
	ProtectEvents : function( html )
	{        //alert("ProtectEvents==="+html);
		return html.replace( FCKRegexLib.TagsWithEvent, _FCK_ProtectEvents_ReplaceTags ) ;
	},

	ProtectEventsRestore : function( html )
	{     //alert("ProtectEventsRestore==="+html);
		return html.replace( FCKRegexLib.ProtectedEvents, _FCK_ProtectEvents_RestoreEvents ) ;
	},

	ProtectTags : function( html )
	{  //alert("ProtectTags html=="+html);
		var sTags = FCKConfig.ProtectedTags ;

		// IE doesn't support <abbr> and it breaks it. Let's protect it.
		if ( FCKBrowserInfo.IsIE )
			sTags += sTags.length > 0 ? '|ABBR|XML|EMBED' : 'ABBR|XML|EMBED' ;

		var oRegex ;
		if ( sTags.length > 0 )
		{
			oRegex = new RegExp( '<(' + sTags + ')(?!\w|:)', 'gi' ) ;
			html = html.replace( oRegex, '<FCK:$1' ) ;

			oRegex = new RegExp( '<\/(' + sTags + ')>', 'gi' ) ;
			html = html.replace( oRegex, '<\/FCK:$1>' ) ;
		}

		// Protect some empty elements. We must do it separately because the
		// original tag may not contain the closing slash, like <hr>:
		//		- <meta> tags get executed, so if you have a redirect meta, the
		//		  content will move to the target page.
		//		- <hr> may destroy the document structure if not well
		//		  positioned. The trick is protect it here and restore them in
		//		  the FCKDocumentProcessor.
		sTags = 'META' ;
		if ( FCKBrowserInfo.IsIE )
			sTags += '|HR' ;

		oRegex = new RegExp( '<((' + sTags + ')(?=\\s|>|/)[\\s\\S]*?)/?>', 'gi' ) ;
		html = html.replace( oRegex, '<FCK:$1 />' ) ;
                //alert("ProtectTags html after replace=="+html);
		return html ;
	},

	SetData : function( data, resetIsDirty )
	{
		this.EditingArea.Mode = FCK.EditMode ;
		//alert('SetData in fck,js Mode===='+this.EditingArea.Mode);
		
		// If there was an onSelectionChange listener in IE we must remove it to avoid crashes #1498
		if ( FCKBrowserInfo.IsIE && FCK.EditorDocument )
		{
				FCK.EditorDocument.detachEvent("onselectionchange", Doc_OnSelectionChange ) ;
		}

		if ( FCK.EditMode == FCK_EDITMODE_WYSIWYG )
		{
			// Save the resetIsDirty for later use (async)
			this._ForceResetIsDirty = ( resetIsDirty === true ) ;

			// Protect parts of the code that must remain untouched (and invisible)
			// during editing.
			data = FCKConfig.ProtectedSource.Protect( data ) ;

			// Call the Data Processor to transform the data.
			data = FCK.DataProcessor.ConvertToHtml( data ) ;

			// Fix for invalid self-closing tags (see #152).
			data = data.replace( FCKRegexLib.InvalidSelfCloseTags, '$1></$2>' ) ;

			// Protect event attributes (they could get fired in the editing area).
			data = FCK.ProtectEvents( data ) ;

			// Protect some things from the browser itself.
			data = FCK.ProtectUrls( data ) ;
			data = FCK.ProtectTags( data ) ;

			// Insert the base tag (FCKConfig.BaseHref), if not exists in the source.
			// The base must be the first tag in the HEAD, to get relative
			// links on styles, for example.
			if ( FCK.TempBaseTag.length > 0 && !FCKRegexLib.HasBaseTag.test( data ) )
				data = data.replace( FCKRegexLib.HeadOpener, '$&' + FCK.TempBaseTag ) ;

			// Build the HTML for the additional things we need on <head>.
			var sHeadExtra = '' ;
                       
			if ( !FCKConfig.FullPage )
				sHeadExtra += _FCK_GetEditorAreaStyleTags() ;

			if ( FCKBrowserInfo.IsIE )
				sHeadExtra += FCK._GetBehaviorsStyle() ;
			else if ( FCKConfig.ShowBorders )
				sHeadExtra += '<link href="' + FCKConfig.FullBasePath + 'css/fck_showtableborders_gecko.css" rel="stylesheet" type="text/css" _fcktemp="true" />' ;

			sHeadExtra += '<link href="' + FCKConfig.FullBasePath + 'css/fck_internal.css" rel="stylesheet" type="text/css" _fcktemp="true" />' ;

			// Attention: do not change it before testing it well (sample07)!
			// This is tricky... if the head ends with <meta ... content type>,
			// Firefox will break. But, it works if we place our extra stuff as
			// the last elements in the HEAD.
			data = data.replace( FCKRegexLib.HeadCloser, sHeadExtra + '$&' ) ;
                        //alert('SetData in fck.js before invoke EditingArea.Start data in view mode ===='+data);
                        //data = data.replace("$#doc_content.previousPage=true,nextPage=true,navigatorPage=true,gotoPage=true#","<input type=\'button\' name=\'1111\' value=\'doc contents' />");
                        //data = ConvertWmlToHtml(data);
                        //data=ConvertWmlToHtml(data);
// add by dingjiangtao
						
						
						//data = data.replace(/(\$#.+?#)/ig,'<fieldset contenteditable="false" style="width:20%" name="资源" title="资源" onresizestart="return false;" onclick="return false;" _aspirepamstag="$1"><legend>资源</legend>asdfsdf<br/><img src="http://www.baidu.com/img/logo-yy.gif" alt=""/></fieldset>');
                        // alert(document.all);
						// alert(window.parent.document.all.wapType.value);

						

						 //获得WAP版本
						 var wapType;
							 
						 if (window.parent.document.all.wapType == null)
						 {
							  //wapType = document.all.wapType.value;
						 }
						 else
			             {
							 wapType = window.parent.document.all.wapType.value;
			             }

						 //对html标签中出现的标签进行保护，也就是不做处理
						 data = data.replace(new RegExp("=\"\\$#","gm"),"=\"#---@");
						
                         var tempData = data;

						 //标签集合
						 var tagCollection = "";

                         tempData = tempData.replace(/(\$#[\s\S]+?#)/ig,function(){var tarName = arguments[1];  tagCollection = tagCollection + tarName + TAG_SEEARAAOR;  return "testTemp";});
                         
						 if (tagCollection.length > 0)
						 {
                             tagCollection = tagCollection.substr(0,tagCollection.length-TAG_SEEARAAOR.length);
							 // alert("标签集合:" + tagCollection);
							   FillTerritory(tagCollection);
						 }
						

                        var tar_responseArr = new Array();
						if (tagResponseValue == null)
						{
							//alert("没有标签可解析");
						}
						else
			            {
							tar_responseArr = tagResponseValue.split(TAG_SEEARAAOR);
			            }
						//alert("响应的标签的个数" + tar_responseArr.length);
						//alert("解析后返回的值11:" + tagResponseValue);
						  //alert("999tempData:" + tempData);
						  var i = -1;
						data = data.replace(/(\$#[\s\S]+?#)/ig,function() {var tarAll= arguments[1];  var diaAttr=""; var tagEnname;var indexPos = tarAll.indexOf(".");  tarAll = tarAll.replace(/</ig,"||-");
							 tarAll = tarAll.replace(/>/ig,"-||");
							// alert("111:" + tarAll);

							//获得标签的名称
							if (indexPos > -1)
							{
								if (tarAll.indexOf("adapter_block") > -1)
								{
									var pos = tarAll.indexOf("=");

                                    var blockStrid = tarAll.substr(pos + 1,tarAll.length-pos-2);

									

	
	                               //通过区块ID获得区块名称
	                                askBlockName(blockStrid);

									

                                    //tagEnname = blockName;

									

								}
								
								 tagEnname = tarAll.substr(2,tarAll.indexOf(".")-2);
								
							}
							else
							{
								 tagEnname = tarAll.substr(2,tarAll.length-3);
							}
							 if (FCKTagNames.getTagType(tagEnname) == 'Dialog')
						    {

                             
						
							    
							    diaAttr = 'dialogWindow="dialogWindow"';
							
						}
						else
						{
							  tagEnname = tarAll.substr(2,tarAll.length-3);
						}
						//alert(tagEnname);
						var tagCnName="";
						if (tarAll.indexOf("adapter_block") > -1)
							{
							    tagCnName=blockName;
								
							}
							else
							{
								tagCnName=FCKTagNames.GetName(tagEnname);
							}
						 i++;  var tarContent =  tar_responseArr[i];  return '<fieldset contenteditable="false" '+ diaAttr + ' style="display:inline;" name="' + tagCnName + '" title="' + tagCnName + '"  _aspirepamstag="' +tarAll + '"><legend>' + tagCnName + '</legend>' + tarContent + '&nbsp;</fieldset>'});
			
			
			// Load the HTML in the editing area.
			this.EditingArea.OnLoad = _FCK_EditingArea_OnLoad ;

			data = data.replace(/<p>/gi,"<code><br\/>");
			//data = data.replace(new RegExp("<\/p>[\s|\r|\n]*?","gm"),"</code>");
			data = data.replace(new RegExp("<\/p>","gm"),"</code>");

			//第一次的p不换行
			//alert(data);
           //data = data.replace("<code>[\s|\r|\n]*?<br\/>","<code>");
		   data = data.replace("<code><br\/>","<code>");

		   //解决光标定位的问题

       // data = data.replace(/<aspire>/gi,"<customer:aspire>");
		//data = data.replace(/<\/aspire>/gi,"</customer:aspire>");


		//处理江苏客户端问题
		
		//data = data.replace(new RegExp("<xpc xmlns:xsi=\"http:\/\/www.w3.org\/2001\/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"\">","gm"),"<xpc xmlns:xsi=\"http:\/\/www.w3.org\/2001\/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"\"><code>");
        //data = data.replace(new RegExp("<\/xpc>","gm"),"</code><\/xpc>");
		   
		   data = data.replace(/\n/gi,"");

		   data = data.replace(/\r/gi,"");
		  // data = data.replace(/> /gi,"");
//		   data = data.replace(/ /gi,"");

            //data = data.replace(/<fieldset\s.+_aspirepamstag="(.+?)"[\s\S]+?<\/fieldset>/gi,"");

			//alert("data:" + data);

			//解决光标定位不准的问题
			data = data.replace(/\s{4}/gi,"");


			//解决手机客户端光标定位问题

			data = data.replace(/<aspire><\/aspire>/,"<aspire><samp><\/samp></aspire>");


			//解决江苏手机报光标定位问题

           data = data.replace(/(<!--{[\d]+?}-->)(<!--{[\d]+?}-->)/,
		   function() 
		   {  
			  return arguments[1] + "<samp><\/samp>" + arguments[2];
		    }
		   );

		   //将分页符替换为图片

          data = data.replace(/#aspire page split tag#/gi,"<img class=FCK__PageBreak _fckfakelement='true' src='" + FCKConfig.FullBasePath + "images/spacer.gif'>");
		
			
			this.EditingArea.Start( data ) ;
			//alert("after data="+data)
		}
		else
		{
			// Remove the references to the following elements, as the editing area
			// IFRAME will be removed.
			FCK.EditorWindow	= null ;
			FCK.EditorDocument	= null ;
			FCKDomTools.PaddingNode = null ;

			this.EditingArea.OnLoad = null ;
			//alert('SetData in fck.js before invoke EditingArea.Start data in code mode===='+data);
			//data = data.replace("<input type=\"button\" name=\"1111\" value=\"doc contents\" />","$#doc_content.previousPage=true,nextPage=true,navigatorPage=true,gotoPage=true#");
			//data=ConvertHtmlToWml(data);
			this.EditingArea.Start( data ) ;

			// Enables the context menu in the textarea.
			this.EditingArea.Textarea._FCKShowContextMenu = true ;

			// Removes the enter key handler.
			FCK.EnterKeyHandler = null ;

			if ( resetIsDirty )
				this.ResetIsDirty() ;

			// Listen for keystroke events.
			FCK.KeystrokeHandler.AttachToElement( this.EditingArea.Textarea ) ;

			this.EditingArea.Textarea.focus() ;

			FCK.Events.FireEvent( 'OnAfterSetHTML' ) ;
		}

		if ( FCKBrowserInfo.IsGecko )
			window.onresize() ;
	},

	// For the FocusManager
	HasFocus : false,


	// This collection is used by the browser specific implementations to tell
	// which named commands must be handled separately.
	RedirectNamedCommands : new Object(),

	ExecuteNamedCommand : function( commandName, commandParameter, noRedirect, noSaveUndo )
	{
		if ( !noSaveUndo )
			FCKUndo.SaveUndoStep() ;

		if ( !noRedirect && FCK.RedirectNamedCommands[ commandName ] != null )
			FCK.ExecuteRedirectedNamedCommand( commandName, commandParameter ) ;
		else
		{
			FCK.Focus() ;
			FCK.EditorDocument.execCommand( commandName, false, commandParameter ) ;
			FCK.Events.FireEvent( 'OnSelectionChange' ) ;
		}

		if ( !noSaveUndo )
		FCKUndo.SaveUndoStep() ;
	},

	GetNamedCommandState : function( commandName )
	{
		try
		{

			// Bug #50 : Safari never returns positive state for the Paste command, override that.
			if ( FCKBrowserInfo.IsSafari && FCK.EditorWindow && commandName.IEquals( 'Paste' ) )
				return FCK_TRISTATE_OFF ;

			if ( !FCK.EditorDocument.queryCommandEnabled( commandName ) )
				return FCK_TRISTATE_DISABLED ;
			else
			{
				return FCK.EditorDocument.queryCommandState( commandName ) ? FCK_TRISTATE_ON : FCK_TRISTATE_OFF ;
			}
		}
		catch ( e )
		{
			return FCK_TRISTATE_OFF ;
		}
	},

	GetNamedCommandValue : function( commandName )
	{
		var sValue = '' ;
		var eState = FCK.GetNamedCommandState( commandName ) ;

		if ( eState == FCK_TRISTATE_DISABLED )
			return null ;

		try
		{
			sValue = this.EditorDocument.queryCommandValue( commandName ) ;
		}
		catch(e) {}

		return sValue ? sValue : '' ;
	},

	Paste : function( _callListenersOnly )
	{
		// First call 'OnPaste' listeners.
		if ( FCK.Status != FCK_STATUS_COMPLETE || !FCK.Events.FireEvent( 'OnPaste' ) )
			return false ;

		// Then call the default implementation.
		return _callListenersOnly || FCK._ExecPaste() ;
	},

	PasteFromWord : function()
	{
		FCKDialog.OpenDialog( 'FCKDialog_Paste', FCKLang.PasteFromWord, 'dialog/fck_paste.html', 400, 330, 'Word' ) ;
	},

	Preview : function()
	{
		//alert('Preview in fck.js ');
		var iWidth	= FCKConfig.ScreenWidth * 0.8 ;
		var iHeight	= FCKConfig.ScreenHeight * 0.7 ;
		var iLeft	= ( FCKConfig.ScreenWidth - iWidth ) / 2 ;
		var oWindow = window.open( '', null, 'toolbar=yes,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=' + iWidth + ',height=' + iHeight + ',left=' + iLeft ) ;

		var sHTML ;

		if ( FCKConfig.FullPage )
		{
			if ( FCK.TempBaseTag.length > 0 )
				sHTML = FCK.TempBaseTag + FCK.GetXHTML() ;
			else
				sHTML = FCK.GetXHTML() ;
		}
		else
		{
			sHTML =
				FCKConfig.DocType +
				'<html dir="' + FCKConfig.ContentLangDirection + '">' +
				'<head>' +
				FCK.TempBaseTag +
				'<title>' + FCKLang.Preview + '</title>' +
				_FCK_GetEditorAreaStyleTags() +
				'</head><body' + FCKConfig.GetBodyAttributes() + '>' +
				FCK.GetXHTML() +
				'</body></html>' ;
		}

		oWindow.document.write( sHTML );
		oWindow.document.close();
	},

	SwitchEditMode : function( noUndo )
	{//alert('SwitchEditMode=');
		
		var bIsWysiwyg = ( FCK.EditMode == FCK_EDITMODE_WYSIWYG ) ;
		//alert('SwitchEditMode in fck.js '+ bIsWysiwyg);
		// Save the current IsDirty state, so we may restore it after the switch.
		var bIsDirty = FCK.IsDirty() ;

		var sHtml ;

		// Update the HTML in the view output to show.
		if ( bIsWysiwyg )
		{
			FCKCommands.GetCommand( 'ShowBlocks' ).SaveState() ;
			if ( !noUndo && FCKBrowserInfo.IsIE )
				FCKUndo.SaveUndoStep() ;

			sHtml = FCK.GetXHTML( FCKConfig.FormatSource ) ;
                         //alert('SwitchEditMode in fck.js get  resources view=== '+sHtml);
			if ( sHtml == null )
				return false ;
		}
		else
		{
			sHtml = this.EditingArea.Textarea.value ;
			 //alert('SwitchEditMode in fck.js get  code view=== '+sHtml);
		}

		FCK.EditMode = bIsWysiwyg ? FCK_EDITMODE_SOURCE : FCK_EDITMODE_WYSIWYG ;

		FCK.SetData( sHtml, !bIsDirty ) ;

		// Set the Focus.
		FCK.Focus() ;

		// Update the toolbar (Running it directly causes IE to fail).
		FCKTools.RunFunction( FCK.ToolbarSet.RefreshModeState, FCK.ToolbarSet ) ;

		return true ;
	},

	InsertElement : function( element )
	{ //alert("InsertElement");
		// The parameter may be a string (element name), so transform it in an element.
		if ( typeof element == 'string' )
			element = this.EditorDocument.createElement( element ) ;

		var elementName = element.nodeName.toLowerCase() ;

		// Create a range for the selection. V3 will have a new selection
		// object that may internally supply this feature.
		var range = new FCKDomRange( this.EditorWindow ) ;

		if ( FCKListsLib.BlockElements[ elementName ] != null )
		{
			range.SplitBlock() ;
			range.InsertNode( element ) ;

			var next = FCKDomTools.GetNextSourceElement( element, false, null, [ 'hr','br','param','img','area','input' ] ) ;

			// Be sure that we have something after the new element, so we can move the cursor there.
			if ( !next && FCKConfig.EnterMode != 'br')
			{
				next = this.EditorDocument.body.appendChild( this.EditorDocument.createElement( FCKConfig.EnterMode ) ) ;

				if ( FCKBrowserInfo.IsGeckoLike )
					FCKTools.AppendBogusBr( next ) ;
			}

			if ( FCKListsLib.EmptyElements[ elementName ] == null )
				range.MoveToElementEditStart( element ) ;
			else if ( next )
				range.MoveToElementEditStart( next ) ;
			else
				range.MoveToPosition( element, 4 ) ;

			if ( FCKBrowserInfo.IsGecko )
			{
				if ( next )
					next.scrollIntoView( false ) ;
				element.scrollIntoView( false ) ;
			}
		}
		else
		{
			// Delete the current selection and insert the node.
			range.MoveToSelection() ;
			range.DeleteContents() ;
			range.InsertNode( element ) ;

			// Move the selection right after the new element.
			// DISCUSSION: Should we select the element instead?
			range.SetStart( element, 4 ) ;
			range.SetEnd( element, 4 ) ;
		}

		range.Select() ;
		range.Release() ;

		// REMOVE IT: The focus should not really be set here. It is up to the
		// calling code to reset the focus if needed.
		this.Focus() ;

		return element ;
	},

	_InsertBlockElement : function( blockElement )
	{
	},

	_IsFunctionKey : function( keyCode )
	{
		// keys that are captured but do not change editor contents
		if ( keyCode >= 16 && keyCode <= 20 )
			// shift, ctrl, alt, pause, capslock
			return true ;
		if ( keyCode == 27 || ( keyCode >= 33 && keyCode <= 40 ) )
			// esc, page up, page down, end, home, left, up, right, down
			return true ;
		if ( keyCode == 45 )
			// insert, no effect on FCKeditor, yet
			return true ;
		return false ;
	},

	_KeyDownListener : function( evt )
	{
		if (! evt)
			evt = FCK.EditorWindow.event ;
		if ( FCK.EditorWindow )
		{
			if ( !FCK._IsFunctionKey(evt.keyCode) // do not capture function key presses, like arrow keys or shift/alt/ctrl
					&& !(evt.ctrlKey || evt.metaKey) // do not capture Ctrl hotkeys, as they have their snapshot capture logic
					&& !(evt.keyCode == 46) ) // do not capture Del, it has its own capture logic in fckenterkey.js
				FCK._KeyDownUndo() ;
		}
		return true ;
	},

	_KeyDownUndo : function()
	{
		if ( !FCKUndo.Typing )
		{
			FCKUndo.SaveUndoStep() ;
			FCKUndo.Typing = true ;
			FCK.Events.FireEvent( "OnSelectionChange" ) ;
		}

		FCKUndo.TypesCount++ ;
		FCKUndo.Changed = 1 ;

		if ( FCKUndo.TypesCount > FCKUndo.MaxTypes )
		{
			FCKUndo.TypesCount = 0 ;
			FCKUndo.SaveUndoStep() ;
		}
	},

	_TabKeyHandler : function( evt )
	{
		if ( ! evt )
			evt = window.event ;

		var keystrokeValue = evt.keyCode ;

		// Pressing <Tab> in source mode should produce a tab space in the text area, not
		// changing the focus to something else.
		if ( keystrokeValue == 9 && FCK.EditMode != FCK_EDITMODE_WYSIWYG )
		{
			if ( FCKBrowserInfo.IsIE )
			{
				var range = document.selection.createRange() ;
				if ( range.parentElement() != FCK.EditingArea.Textarea )
					return true ;
				range.text = '\t' ;
				range.select() ;
			}
			else
			{
				var a = [] ;
				var el = FCK.EditingArea.Textarea ;
				var selStart = el.selectionStart ;
				var selEnd = el.selectionEnd ;
				a.push( el.value.substr(0, selStart ) ) ;
				a.push( '\t' ) ;
				a.push( el.value.substr( selEnd ) ) ;
				el.value = a.join( '' ) ;
				el.setSelectionRange( selStart + 1, selStart + 1 ) ;
			}

			if ( evt.preventDefault )
				return evt.preventDefault() ;

			return evt.returnValue = false ;
		}

		return true ;
	}
} ;

FCK.Events = new FCKEvents( FCK ) ;

// DEPRECATED in favor or "GetData".
FCK.GetHTML	= FCK.GetXHTML = FCK.GetData ;

// DEPRECATED in favor of "SetData".
FCK.SetHTML = FCK.SetData ;

// InsertElementAndGetIt and CreateElement are Deprecated : returns the same value as InsertElement.
FCK.InsertElementAndGetIt = FCK.CreateElement = FCK.InsertElement ;

// Replace all events attributes (like onclick).
function _FCK_ProtectEvents_ReplaceTags( tagMatch )
{
	return tagMatch.replace( FCKRegexLib.EventAttributes, _FCK_ProtectEvents_ReplaceEvents ) ;
}

// Replace an event attribute with its respective __fckprotectedatt attribute.
// The original event markup will be encoded and saved as the value of the new
// attribute.
function _FCK_ProtectEvents_ReplaceEvents( eventMatch, attName )
{
	return ' ' + attName + '_fckprotectedatt="' + encodeURIComponent( eventMatch ) + '"' ;
}

function _FCK_ProtectEvents_RestoreEvents( match, encodedOriginal )
{
	return decodeURIComponent( encodedOriginal ) ;
}

function _FCK_MouseEventsListener( evt )
{
	if ( ! evt )
		evt = window.event ;
	if ( evt.type == 'mousedown' )
		FCK.MouseDownFlag = true ;
	else if ( evt.type == 'mouseup' )
		FCK.MouseDownFlag = false ;
	else if ( evt.type == 'mousemove' )
		FCK.Events.FireEvent( 'OnMouseMove', evt ) ;
}

function _FCK_PaddingNodeListener()
{
	if ( FCKConfig.EnterMode.IEquals( 'br' ) )
		return ;
	FCKDomTools.EnforcePaddingNode( FCK.EditorDocument, FCKConfig.EnterMode ) ;

	if ( ! FCKBrowserInfo.IsIE && FCKDomTools.PaddingNode )
	{
		// Prevent the caret from going between the body and the padding node in Firefox.
		// i.e. <body>|<p></p></body>
		var sel = FCK.EditorWindow.getSelection() ;
		if ( sel && sel.rangeCount == 1 )
		{
			var range = sel.getRangeAt( 0 ) ;
			if ( range.collapsed && range.startContainer == FCK.EditorDocument.body && range.startOffset == 0 )
			{
				range.selectNodeContents( FCKDomTools.PaddingNode ) ;
				range.collapse( true ) ;
				sel.removeAllRanges() ;
				sel.addRange( range ) ;
			}
		}
	}
	else if ( FCKDomTools.PaddingNode )
	{
		// Prevent the caret from going into an empty body but not into the padding node in IE.
		// i.e. <body><p></p>|</body>
		var parentElement = FCKSelection.GetParentElement() ;
		var paddingNode = FCKDomTools.PaddingNode ;
		if ( parentElement && parentElement.nodeName.IEquals( 'body' ) )
		{
			if ( FCK.EditorDocument.body.childNodes.length == 1 
					&& FCK.EditorDocument.body.firstChild == paddingNode )
			{
				var range = FCK.EditorDocument.body.createTextRange() ;
				var clearContents = false ;
				if ( !paddingNode.childNodes.firstChild )
				{
					paddingNode.appendChild( paddingNode.ownerDocument.createTextNode( '\ufeff' ) ) ;
					clearContents = true ;
				}
				range.moveToElementText( paddingNode ) ;
				range.select() ;
				if ( clearContents )
					range.pasteHTML( '' ) ;
			}
		}
	}
}

function _FCK_EditingArea_OnLoad()
{  // alert('_FCK_EditingArea_OnLoad');
	// Get the editor's window and document (DOM)
	FCK.EditorWindow	= FCK.EditingArea.Window ;
	FCK.EditorDocument	= FCK.EditingArea.Document ;

	FCK.InitializeBehaviors() ;

	// Listen for mousedown and mouseup events for tracking drag and drops.
	FCK.MouseDownFlag = false ;
	FCKTools.AddEventListener( FCK.EditorDocument, 'mousemove', _FCK_MouseEventsListener ) ;
	FCKTools.AddEventListener( FCK.EditorDocument, 'mousedown', _FCK_MouseEventsListener ) ;
	FCKTools.AddEventListener( FCK.EditorDocument, 'mouseup', _FCK_MouseEventsListener ) ;

	// Most of the CTRL key combos do not work under Safari for onkeydown and onkeypress (See #1119)
	// But we can use the keyup event to override some of these...
	if ( FCKBrowserInfo.IsSafari )
	{
		var undoFunc = function( evt )
		{
			if ( ! ( evt.ctrlKey || evt.metaKey ) )
				return ;
			if ( FCK.EditMode != FCK_EDITMODE_WYSIWYG )
				return ;
			switch ( evt.keyCode )
			{
				case 89:
					FCKUndo.Redo() ;
					break ;
				case 90:
					FCKUndo.Undo() ;
					break ;
			}
		}

		FCKTools.AddEventListener( FCK.EditorDocument, 'keyup', undoFunc ) ;
	}

	// Create the enter key handler
	FCK.EnterKeyHandler = new FCKEnterKey( FCK.EditorWindow, FCKConfig.EnterMode, FCKConfig.ShiftEnterMode, FCKConfig.TabSpaces ) ;

	// Listen for keystroke events.
	FCK.KeystrokeHandler.AttachToElement( FCK.EditorDocument ) ;

	if ( FCK._ForceResetIsDirty )
		FCK.ResetIsDirty() ;

	// This is a tricky thing for IE. In some cases, even if the cursor is
	// blinking in the editing, the keystroke handler doesn't catch keyboard
	// events. We must activate the editing area to make it work. (#142).
	if ( FCKBrowserInfo.IsIE && FCK.HasFocus )
		FCK.EditorDocument.body.setActive() ;

	FCK.OnAfterSetHTML() ;

	// Restore show blocks status.
	FCKCommands.GetCommand( 'ShowBlocks' ).RestoreState() ;

	// Check if it is not a startup call, otherwise complete the startup.
	if ( FCK.Status != FCK_STATUS_NOTLOADED )
		return ;

	if ( FCKConfig.Debug )
		FCKDebug._GetWindow() ;

	FCK.SetStatus( FCK_STATUS_ACTIVE ) ;
}

function _FCK_GetEditorAreaStyleTags()
{
	var sTags = '' ;
	/**ע,ҪתɴģʽʱԶʽ*/
	
	var aCSSs = FCKConfig.EditorAreaCSS ;
	var sStyles = FCKConfig.EditorAreaStyles ;

	for ( var i = 0 ; i < aCSSs.length ; i++ )
		sTags += '<link href="' + aCSSs[i] + '" rel="stylesheet" type="text/css" />' ;

	if ( sStyles && sStyles.length > 0 )
		sTags += "<style>" + sStyles + "</style>" ;

	return sTags ;
}

function _FCK_KeystrokeHandler_OnKeystroke( keystroke, keystrokeValue )
{
	if ( FCK.Status != FCK_STATUS_COMPLETE )
		return false ;

	if ( FCK.EditMode == FCK_EDITMODE_WYSIWYG )
	{
		switch ( keystrokeValue )
		{
			case 'Paste' :
				return !FCK.Paste() ;

			case 'Cut' :
				FCKUndo.SaveUndoStep() ;
				return false ;
		}
	}
	else
	{
		// In source mode, some actions must have their default behavior.
		if ( keystrokeValue.Equals( 'Paste', 'Undo', 'Redo', 'SelectAll', 'Cut' ) )
			return false ;
	}

	// The return value indicates if the default behavior of the keystroke must
	// be cancelled. Let's do that only if the Execute() call explicitly returns "false".
	var oCommand = FCK.Commands.GetCommand( keystrokeValue ) ;
	return ( oCommand.Execute.apply( oCommand, FCKTools.ArgumentsToArray( arguments, 2 ) ) !== false ) ;
}

// Set the FCK.LinkedField reference to the field that will be used to post the
// editor data.
(function()
{
	// There is a bug on IE... getElementById returns any META tag that has the
	// name set to the ID you are looking for. So the best way in to get the array
	// by names and look for the correct one.
	// As ASP.Net generates a ID that is different from the Name, we must also
	// look for the field based on the ID (the first one is the ID).

	var oDocument = window.parent.document ;

	// Try to get the field using the ID.
	var eLinkedField = oDocument.getElementById( FCK.Name ) ;

	var i = 0;
	while ( eLinkedField || i == 0 )
	{
		if ( eLinkedField && eLinkedField.tagName.toLowerCase().Equals( 'input', 'textarea' ) )
		{
			FCK.LinkedField = eLinkedField ;
			break ;
		}

		eLinkedField = oDocument.getElementsByName( FCK.Name )[i++] ;
	}
})() ;

var FCKTempBin =
{
	Elements : new Array(),

	AddElement : function( element )
	{
		var iIndex = this.Elements.length ;
		this.Elements[ iIndex ] = element ;
		return iIndex ;
	},

	RemoveElement : function( index )
	{
		var e = this.Elements[ index ] ;
		this.Elements[ index ] = null ;
		return e ;
	},

	Reset : function()
	{
		var i = 0 ;
		while ( i < this.Elements.length )
			this.Elements[ i++ ] = null ;
		this.Elements.length = 0 ;
	}
} ;



// # Focus Manager: Manages the focus in the editor.
var FCKFocusManager = FCK.FocusManager =
{
	IsLocked : false,

	AddWindow : function( win, sendToEditingArea )
	{
		var oTarget ;

		if ( FCKBrowserInfo.IsIE )
			oTarget = win.nodeType == 1 ? win : win.frameElement ? win.frameElement : win.document ;
		else if ( FCKBrowserInfo.IsSafari )
			oTarget = win ;
		else
			oTarget = win.document ;

		FCKTools.AddEventListener( oTarget, 'blur', FCKFocusManager_Win_OnBlur ) ;
		FCKTools.AddEventListener( oTarget, 'focus', sendToEditingArea ? FCKFocusManager_Win_OnFocus_Area : FCKFocusManager_Win_OnFocus ) ;
	},

	RemoveWindow : function( win )
	{
		if ( FCKBrowserInfo.IsIE )
			oTarget = win.nodeType == 1 ? win : win.frameElement ? win.frameElement : win.document ;
		else
			oTarget = win.document ;

		FCKTools.RemoveEventListener( oTarget, 'blur', FCKFocusManager_Win_OnBlur ) ;
		FCKTools.RemoveEventListener( oTarget, 'focus', FCKFocusManager_Win_OnFocus_Area ) ;
		FCKTools.RemoveEventListener( oTarget, 'focus', FCKFocusManager_Win_OnFocus ) ;
	},

	Lock : function()
	{
		this.IsLocked = true ;
	},

	Unlock : function()
	{
		if ( this._HasPendingBlur )
			FCKFocusManager._Timer = window.setTimeout( FCKFocusManager_FireOnBlur, 100 ) ;

		this.IsLocked = false ;
	},

	_ResetTimer : function()
	{
		this._HasPendingBlur = false ;

		if ( this._Timer )
		{
			window.clearTimeout( this._Timer ) ;
			delete this._Timer ;
		}
	}
	
} ;

function FCKFocusManager_Win_OnBlur()
{
	if ( typeof(FCK) != 'undefined' && FCK.HasFocus )
	{
		FCKFocusManager._ResetTimer() ;
		FCKFocusManager._Timer = window.setTimeout( FCKFocusManager_FireOnBlur, 100 ) ;
	}
}

function FCKFocusManager_FireOnBlur()
{
	if ( FCKFocusManager.IsLocked )
		FCKFocusManager._HasPendingBlur = true ;
	else
	{
		FCK.HasFocus = false ;
		FCK.Events.FireEvent( "OnBlur" ) ;
	}
}

function FCKFocusManager_Win_OnFocus_Area()
{
	FCK.Focus() ;
	FCKFocusManager_Win_OnFocus() ;
}

function FCKFocusManager_Win_OnFocus()
{
	FCKFocusManager._ResetTimer() ;

	if ( !FCK.HasFocus && !FCKFocusManager.IsLocked )
	{
		FCK.HasFocus = true ;
		FCK.Events.FireEvent( "OnFocus" ) ;
	}
}

function    ConvertWmlToHtml(WmlData)
{      //alert('ConvertWmlToHtml===WmlData'+WmlData);
	//alert('GetCommand='+FCKCommandslyg.GetCommand('Superscript'));
	//Ҫ鲻ûֶڴģʽHtmlBUTTON

	var userDefTagArr = new Array('weather');
	for(var i=0;i<userDefTagArr.length;i++)
	{
	    

		WmlData = WmlData.replace(new RegExp("<" + userDefTagArr[i] + ">","gm"),"&lt;" + userDefTagArr[i] + "&gt;");
		WmlData = WmlData.replace(new RegExp("<\/" + userDefTagArr[i] + ">","gm"),"&lt;/" + userDefTagArr[i] + "&gt;");
      
	}

	alert("wml数据:" + WmlData);
        if(WmlData=="")
        {
        	return "";
        }
        var wmlStartPosition =  WmlData.indexOf("$#");
	
	if(wmlStartPosition<0)
	{
		return WmlData;
         }
         //CommonGuideRtf.htmlвʱתıǩеļŷԭɼ
	var WmlDataArray = WmlData.split("$#")
	var HtmlValue="";
	for(var i=0; i<WmlDataArray.length; i++)
	{	
		//alert("WmlDataArray["+i+"]="+WmlDataArray[i]);
		var endPosition = WmlDataArray[i].indexOf("#");
		var startPosition = WmlDataArray[i].indexOf(".");
		var midPosition = WmlDataArray[i].indexOf("=");
		var beforeMid = WmlDataArray[i].indexOf(",");
		var len = WmlDataArray[i].length;
		var tagName_No_Para ="";
		var isTag = "undefined";
		var isNestTag = "";
		if(endPosition>0)
		{
			tagName_No_Para= WmlDataArray[i].substring(0,endPosition);
			tagName_No_Para = tagName_No_Para.replace(/(^[\s]*)|([\s]*$)/g, "");
			var tmp = FCKTagNames.GetName(tagName_No_Para);
			if(tmp!=undefined)
			{
				isTag = FCKTagNames.GetName(tagName_No_Para);
				//alert('isTag='+isTag);
			}
		}
		if(i>0)
		{
			var lastComLoca= WmlDataArray[i-1].lastIndexOf("\"");
			//alert('lastComLoca='+lastComLoca);
			var tmpLen = WmlDataArray[i-1].length-1;
			//alert('tmpLen='+tmpLen);
			var tmpValue = WmlDataArray[i].charAt(endPosition+1);
			//alert("tmpValue ecp com="+tmpValue);
			var tmpValue2 = WmlDataArray[i-1].charAt(lastComLoca-1);
			//alert("tmpValue2 ecp equle =:="+tmpValue2);
			if(lastComLoca==tmpLen&&tmpValue=="\""&&tmpValue2=="=")
			{
				isNestTag = "true";
			}
		}
		if(isNestTag == "true")
		{      //alert("true");
			HtmlValue += "$#";
			HtmlValue += WmlDataArray[i];
		}
		else if(endPosition>0&&startPosition>0&&(endPosition>startPosition)&&(startPosition<beforeMid||startPosition<midPosition))
		{       //alert("normal tag");
			var tagName = WmlDataArray[i].substring(0,startPosition);
			var wmlValue = WmlDataArray[i].substring(0,endPosition);
			
			wmlValue = wmlValue.replace(/(^[\s]*)|([\s]*$)/g, "");
			wmlValue = recoverBracketInTag(wmlValue);
			tagName = tagName.replace(/(^[\s]*)|([\s]*$)/g, "");
			//alert(tagName);
			tagName= FCKTagNames.GetName(tagName);
			//alert(tagName);
			//HtmlValue +="<input type=\"button\" name=\"$#" +wmlValue +"#\" value=\""+tagName;
			HtmlValue +="<input type=\"button\" name=\"wml=\'$#" +wmlValue +"#'\"  value=\""+tagName+"\" />" ;
			var tmpV = WmlDataArray[i].substring(endPosition+1,len);
			HtmlValue =HtmlValue+tmpV;
		//֧,޲ǩ
		}else if(endPosition>0&&FCKTagNames.GetName(tagName_No_Para)!="")
		{       //alert("no para tag");
			var wmlValue = WmlDataArray[i].substring(0,endPosition);
			wmlValue = wmlValue.replace(/(^[\s]*)|([\s]*$)/g, "");
			wmlValue = recoverBracketInTag(wmlValue);
			//HtmlValue +="<input type=\"button\" name=\"$#" +wmlValue +"#\" value=\""+isTag ;
			HtmlValue +="<input type=\"button\"name=\"wml=\'$#" +wmlValue +"#'\" value=\""+isTag+"\" />" ;
			var tmpV = WmlDataArray[i].substring(endPosition+1,len);
			HtmlValue =HtmlValue+tmpV;
		}else
		{       //alert(WmlDataArray[i].indexOf("title#"));
			//if(WmlDataArray[i].indexOf("title#")==0)
			//{
				//HtmlValue += "$#";
				//HtmlValue += WmlDataArray[i];
			//}else{
			        //alert("no nest no para no normal tag ");
				HtmlValue +=WmlDataArray[i];
			//}
		}
	}
	alert('转化后的html数据:'+HtmlValue)
	return HtmlValue;
}

function  ConvertHtmlToWml(HtmlData)
{    //alert('ConvertHtmlToWml==HtmlData=\n'+HtmlData);
	//var lyg = FCKTagNames.GetName('QueryStock_Processorssrrrrr');
         //alert('FCKTagNames.GetName(QueryStock_Processor)= '+lyg);


    var userDefTagArr = new Array('weather');
	for(var i=0;i<userDefTagArr.length;i++)
	{
	    

		HtmlData = HtmlData.replace(new RegExp("&lt;" + userDefTagArr[i] + "&gt;","gm"),"<" + userDefTagArr[i] + ">");
		HtmlData = HtmlData.replace(new RegExp("&lt;\/" + userDefTagArr[i] + "&gt;","gm"),"</" + userDefTagArr[i] + ">");
      
	}

		// HtmlData = HtmlData.replace(/&lt;weather&gt;/g,"<weather>");
         //HtmlData = HtmlData.replace(/&lt;\/weather&gt;/g,"</weather>");
	
		 alert("Html数据:" + HtmlData);
        if(HtmlData=="")
        {
        	return "";
        }
        var wmlStartPosition =  HtmlData.indexOf("$#");
	if(wmlStartPosition<21)
	{
	       // alert('no include WML tag');
		if(wmlStartPosition>0)
		{
			alert('ǩ벻ȷ WMLǩӦHTML buttonǩwmlԵֵ');
		}
		return HtmlData;
         }

        var frontVave =HtmlData.substring(0, HtmlData.indexOf("<input"));
	var WmlDataArray = HtmlData.split("$#");
	var WmlValue="";
	for(var i=0; i<WmlDataArray.length; i++)
	{
		//alert("WmlDataArray["+i+"]="+WmlDataArray[i]);
		var endPosition = WmlDataArray[i].indexOf("#");
		//alert("# location="+endPosition);
		var isInclueWml = WmlDataArray[i].indexOf("<input type=");
		//alert('<input type= locaton='+isInclueWml);
		var startPosition = WmlDataArray[i].indexOf(".");
		//alert('. location='+startPosition);
		var midPosition = WmlDataArray[i].indexOf("=");
		//alert('=location='+midPosition);
		var secondmidPosition = WmlDataArray[i].indexOf("=",midPosition+1);
		//midPosition = WmlDataArray[i].indexOf("=",midPosition+1);
		//midPosition = WmlDataArray[i].indexOf("=",midPosition+1);
		//midPosition = WmlDataArray[i].indexOf("=",midPosition+1);
		var beforeMid = WmlDataArray[i].indexOf(",");
		//alert(',location ='+beforeMid);
		
		//#źǷ>,ǰһֵ=ŽβǾǶױǩ,ʱҪһ$#
		
		//IF䲻,ǰһֵ=Žβͬʱ#,޲ǩ,ʱҪúWMLǩķദ
		var tagName_No_Para ="";
		var isTag = "undefined";
		var isNestTag = "";
		if(endPosition>0)
		{
			tagName_No_Para= WmlDataArray[i].substring(0,endPosition);
			tagName_No_Para = tagName_No_Para.replace(/(^[\s]*)|([\s]*$)/g, "");
			var tmp = FCKTagNames.GetName(tagName_No_Para);
			if(tmp!=undefined)
			{
				isTag = FCKTagNames.GetName(tagName_No_Para);
				//alert('isTag='+isTag);
			}
		}
		if(i>0)
		{
			var lastComLoca= WmlDataArray[i-1].lastIndexOf("\"");
			var tmpLen = WmlDataArray[i-1].length-1;
			var tmpValue = WmlDataArray[i].charAt(endPosition+1);
			//alert(tmpValue);
			var tmpValue2 = WmlDataArray[i-1].charAt(lastComLoca-1);
			var htmlTag = "<input type=\"button\"";
			var ishtml = WmlDataArray[i-1].lastIndexOf(htmlTag);
			if(lastComLoca==tmpLen&&tmpValue=="\""&&tmpValue2=="="&&ishtml<0)
			{
				isNestTag = "true";
			}
		}
		//if֧Ƕǩ
		if(isNestTag == "true")
		{       //alert("isNestTag == \"true\"");
			WmlValue +="$#";
			if(isInclueWml<0)
			{
				
				WmlValue +=WmlDataArray[i].replace(/(^[\s]*)|([\s]*$)/g, "");
			}
			else
			{
				WmlValue +=WmlDataArray[i].substring(0,isInclueWml).replace(/(^[\s]*)|([\s]*$)/g, "");
			}
		}
		//esle if 2 ֧ıǩ
		//esle if 2 ʼ
		else if(endPosition>0&&startPosition>0&&(endPosition>startPosition)&&(startPosition<beforeMid||startPosition<midPosition)&&(endPosition>midPosition||endPosition>beforeMid))
		{   //alert('tag');
			
			if(endPosition==WmlDataArray[i].length-1)
			{       //alert('endPosition==WmlDataArray[i].length-1');
				WmlValue +="$#"+WmlDataArray[i].substring(0,endPosition)+"#";
			}
			else{
				 //alert('no endPosition==WmlDataArray[i].length-1');
				WmlValue +="$#"+WmlDataArray[i].substring(0,endPosition)+"#";
				
				if(endPosition<WmlDataArray[i].length-1)
				{      			
					var commValueStart = WmlDataArray[i].indexOf("/>");
					var commValueEnd = WmlDataArray[i].indexOf("<input type=");
					if(commValueEnd<0)
					{
						commValueEnd =WmlDataArray[i].length; 	
					}
					if(commValueStart<0)
					{
						commValueStart =endPosition+1; 	
					}else
					{
						commValueStart = commValueStart+2;
					}				
					WmlValue +=WmlDataArray[i].substring(commValueStart,commValueEnd);
				}
		        }
		
		}
		//esle if 2 
		//else if 1ʼûдκβıǩ
		else if(endPosition>0&&FCKTagNames.GetName(tagName_No_Para)!="")
		{       //ǩǼHTMLԪ,ı
			//alert('no para tag ==\n'+WmlDataArray[i]);
			if(endPosition==WmlDataArray[i].length-1)
			{
				WmlValue +="$#";	
				WmlValue +=WmlDataArray[i].substring(0,endPosition)+"#";
			}
			//ʼǼHTMLԪ	
			else{
				 //alert('no endPosition==WmlDataArray[i].length-1');
				WmlValue +="$#"+WmlDataArray[i].substring(0,endPosition)+"#";
				
				if(endPosition<WmlDataArray[i].length-1)
				{      			
					var commValueStart = WmlDataArray[i].indexOf("/>");
					var commValueEnd = WmlDataArray[i].indexOf("<input type=");
					if(commValueEnd<0)
					{
						commValueEnd =WmlDataArray[i].length; 	
					}
					if(commValueStart<0)
					{
						commValueStart =endPosition+1; 	
					}else
					{
						commValueStart = commValueStart+2;
					}				
					WmlValue +=WmlDataArray[i].substring(commValueStart,commValueEnd);
				}
		        }////ǼHTMLԪ		
		
		
		
		}//else if 1
		//ѼHTMLǩȥ,ı
		else
		//esle 1 ʼ    
		{  
			
			//alert('isInclueWml=='+isInclueWml) ;
			//if(WmlDataArray[i].indexOf("#")>0)
			//{
			//	WmlValue +="$#";
			//}
			if(isInclueWml<0)
			{       
				//alert('in ConvertHtmlToWml WmlDataArray[i]='+WmlDataArray[i]);
				//alert(WmlDataArray[i].indexOf("title#"));
		                //alert('isInclueWml<0');
				WmlValue +=WmlDataArray[i];
			}else
			{       //alert('fianl');
				WmlValue +=WmlDataArray[i].substring(0,isInclueWml);
			}
		}
		//esle 1 
	}
	//alert('after html to wml convert\n'+WmlValue);
	//alert(WmlValue);
	WmlValue = deleteEmptyTag(WmlValue);
	alert("转化成的Wml数据:" + WmlValue);
	return WmlValue;
}

function  deleteEmptyTag(HtmlData)
{
	
	var WmlDataArray = HtmlData.split("<");
	//alert("WmlDataArray.length=="+WmlDataArray.length);
	if(WmlDataArray.length<2)
	{       
		return HtmlData;
	}
	var wmlValue=WmlDataArray[0];
	for(var i=1; i<WmlDataArray.length; i++)
	{      //alert("current WmlDataArray["+i+"]="+WmlDataArray[i]);
		var l= i-1;
		//alert("befor WmlDataArray["+l+"]="+WmlDataArray[l]);
		var beforValue = WmlDataArray[i-1];
		var beforEndChar = beforValue.charAt(beforValue.length-1);
		//alert("beforEndChar="+beforEndChar);
		var currentValue = WmlDataArray[i];
		//alert(currentValue)
		var currentEndChar = currentValue.charAt(currentValue.length-1);
		//alert("currentEndChar="+currentEndChar);
		var currentStartChar = currentValue.charAt(0);
		//alert("currentStartChar="+currentStartChar);
		var currentTag = currentValue.substring(1,currentValue.length-1);
		//alert("currentTag="+currentTag);
		var beforTag = beforValue.substring(1,beforValue.length-1);
		//alert("beforTag="+beforTag);
		if(currentTag==beforTag&&beforEndChar==">"&&currentEndChar==">"&&currentStartChar=="/"&&currentTag!="p" &&currentTag!="P"&&currentTag!="BR" &&currentTag!="br")
		{       //alert("duo yu tag");
			//wmlValue +="<";
			//wmlValue+=WmlDataArray[i-1];
			if(WmlDataArray[i-1].charAt(0)!="<")
			{
				WmlDataArray[i-1] = "<" +WmlDataArray[i-1];
			}
			WmlDataArray[i]="";
		}else
		{      // alert("normal");
			//wmlValue +="<";
			WmlDataArray[i]="<"+WmlDataArray[i];
		}
		
	}
	for(var i=1; i<WmlDataArray.length; i++)
	{
		if(WmlDataArray[i]!="")
		{
			wmlValue +=WmlDataArray[i];
		}	
	}
	
	//alert("wmlValue===lyg=="+wmlValue);
	wmlValue = wmlValue.replace("</card>","");
	wmlValue = wmlValue.replace("<p><wml>","<wml>");
	wmlValue = wmlValue.replace("<p>&nbsp;</p>","");
	wmlValue = wmlValue.replace("<p></p>","");
	wmlValue = wmlValue.replace("</p><p>","<p>");
	return wmlValue +"</card></wml>";
}
//ѱǩڵļŻԭ
function  recoverBracketInTag(WmlValue)
{
	var WmlValueTmp = WmlValue;
	while(WmlValueTmp.indexOf("<")>0||WmlValueTmp.indexOf("<")>0)
	{
		WmlValueTmp = WmlValueTmp.replace("<","lygr");
		WmlValueTmp = WmlValueTmp.replace(">","lygl");
	}
	return WmlValueTmp;
}


