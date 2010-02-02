/*
url-loading object and a request queue built on top of it
*/

/* namespacing object */

 var comboDoc;




function FillTerritoryPluginComboCommand(){


        
        var url = window.parent.scriptcontext + "/templatejsp/pamscombocommands.jsp";

        



        var loader1 = new
        net.ContentLoader(url,FillDropDownPluginComboCommand,null,
                    "POST",null);

      }
      function FillDropDownPluginComboCommand(){
	 
        comboDoc = this.req.responseText;
       
      }
	 

	  FillTerritoryPluginComboCommand();

	  eval(comboDoc);