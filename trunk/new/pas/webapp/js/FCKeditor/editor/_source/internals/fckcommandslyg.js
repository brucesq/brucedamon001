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
 * Define all commands available in the editor.
 */

var FCKCommandslyg  = FCK.Commandslyg=new Object() ;
FCKCommandslyg.LoadedCommands = new Object() ;

FCKCommandslyg.RegisterCommand = function( commandName, command )
{
	this.LoadedCommands[ commandName ] = command ;
}

FCKCommandslyg.GetCommand = function( commandName )
{
	var oCommand = FCKCommands.LoadedCommands[ commandName ] ;

	if ( oCommand )
		return oCommand ;

	switch ( commandName )
	{

		case 'Superscript'	: oCommand = 'Superscript' ; break ;


		default:
		
				oCommand="ha ha " ;
			
			
	}

	FCKCommandslyg.LoadedCommands[ commandName ] = oCommand ;

	return oCommand ;
}

// Gets the state of the "Document Properties" button. It must be enabled only
// when "Full Page" editing is available.
FCKCommandslyg.GetFullPageState = function()
{
	return FCKConfig.FullPage ? FCK_TRISTATE_OFF : FCK_TRISTATE_DISABLED ;
}


FCKCommandslyg.GetBooleanState = function( isDisabled )
{
	return isDisabled ? FCK_TRISTATE_DISABLED : FCK_TRISTATE_OFF ;
}
