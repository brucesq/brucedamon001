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
 * FCKToolbarPanelButton Class: Handles the Fonts combo selector.
 */

var FCKToolbarTestCombo = function( tooltip, style,tarNameList,tarEnNameList)
{
	//alert("113388:" + tarNameList[1]);
	if ( tooltip === false )
		return ;

	this.tagNames = tarNameList;
	this.tagEnNames = tarEnNameList;

	this.CommandName = 'TestCombo' ;
	this.Label		= this.GetLabel() ;
	this.Tooltip	= tooltip ? tooltip : this.Label ;
	this.Style		= style ? style : FCK_TOOLBARITEM_ICONTEXT ;

	this.DefaultLabel = FCKConfig.DefaultStyleLabel || '' ;
}

// Inherit from FCKToolbarSpecialCombo.
FCKToolbarTestCombo.prototype = new FCKToolbarSpecialCombo ;

FCKToolbarTestCombo.prototype.GetLabel = function()
{
	return this.Tooltip ;
}

FCKToolbarTestCombo.prototype.GetStyles = function()
{
	var styles = {} ;
	//var allStyles = this.tagNames;
	var allStyles = FCK.ToolbarSet.CurrentInstance.Styles.GetStyles() ;

	for ( var styleName in allStyles )
	{
		var style = allStyles[ styleName ] ;
		if ( !style.IsCore )
			styles[ styleName ] = style ;
	}


	
	
	return styles ;
}

FCKToolbarTestCombo.prototype.CreateItems = function( targetSpecialCombo )
{
	var targetDoc = targetSpecialCombo._Panel.Document ;

	// Add the Editor Area CSS to the panel so the style classes are previewed correctly.
	FCKTools.AppendStyleSheet( targetDoc, FCKConfig.ToolbarComboPreviewCSS ) ;
	FCKTools.AppendStyleString( targetDoc, FCKConfig.EditorAreaStyles ) ;
	targetDoc.body.className += ' ForceBaseFont' ;

	// Add ID and Class to the body.
	FCKConfig.ApplyBodyAttributes( targetDoc.body ) ;

	// Get the styles list.
	var styles = this.GetStyles() ;

	
    // var jj = 0;
	//for ( var styleName in styles )
	//alert("画数据:" + this.tagNames.length);
	                                
    for ( var jj = 0; jj<this.tagNames.length;jj++ )
	{
	//	alert("画数据");

		//jj++;
		//var style = styles[ styleName ] ;

		//alert("样式:" + style);

		// Object type styles have no preview.
		//var caption = style.GetType() == FCK_STYLE_OBJECT ? 
		//	styleName : 
		//	FCKToolbarTestCombo_BuildPreview( style, style.Label || styleName ) ;
	
		//第一个参数传路径,第二个为参数的名称
		var item = targetSpecialCombo.AddItem( this.tagEnNames[jj], this.tagNames[jj] ) ;

		//item.Style = style ;
	}

	// We must prepare the list before showing it.
	targetSpecialCombo.OnBeforeClick = this.StyleCombo_OnBeforeClick ;
}

FCKToolbarTestCombo.prototype.RefreshActiveItems = function( targetSpecialCombo )
{
	
					

	
	targetSpecialCombo.SetLabel( this.DefaultLabel ) ;
}

FCKToolbarTestCombo.prototype.StyleCombo_OnBeforeClick = function( targetSpecialCombo )
{
	// Two things are done here:
	//	- In a control selection, get the element name, so we'll display styles
	//	  for that element only.
	//	- Select the styles that are active for the current selection.
	
	// Clear the current selection.
	
}

function FCKToolbarTestCombo_BuildPreview( style, caption ) 
{
	//var styleType = style.GetType() ;
	var html = [] ;
	
//	if ( styleType == FCK_STYLE_BLOCK )
	//	html.push( '<div class="BaseFont">' ) ;
	
	var elementName = style.Element ;
	
	// Avoid <bdo> in the preview.
	if ( elementName == 'bdo' )
		elementName = 'span' ;

	html = [ '<', elementName ] ;

	// Assign all defined attributes.
	//var attribs	= style._StyleDesc.Attributes ;
	//if ( attribs )
	//{
	//	for ( var att in attribs )
	//	{
	//		html.push( ' ', att, '="', style.GetFinalAttributeValue( att ), '"' ) ;
	//	}
	//}

	// Assign the style attribute.
	if ( style._GetStyleText().length > 0 )
	;	
	//html.push( ' style="', style.GetFinalStyleValue(), '"' ) ;

	html.push( '>', caption, '</', elementName, '>' ) ;

	//if ( styleType == FCK_STYLE_BLOCK )
		//html.push( '</div>' ) ;

	return html.join( '' ) ;
}