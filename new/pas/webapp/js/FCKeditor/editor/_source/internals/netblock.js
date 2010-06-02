/*
url-loading object and a request queue built on top of it
*/

/* namespacing object */
var netblock=new Object();

netblock.READY_STATE_UNINITIALIZED=0;
netblock.READY_STATE_LOADING=1;
netblock.READY_STATE_LOADED=2;
netblock.READY_STATE_INTERACTIVE=3;
netblock.READY_STATE_COMPLETE=4;


/*--- content loader object for cross-browser requests ---*/
netblock.ContentLoader=function(url,onload,onerror,method,params,contentType){
  this.req=null;
  this.onload=onload;
  this.onerror=(onerror) ? onerror : this.defaultError;
  this.loadXMLDoc(url,method,params,contentType);
}

netblock.ContentLoader.prototype.loadXMLDoc=function(url,method,params,contentType){
  if (!method){
    method="GET";
  }
  if (!contentType && method=="POST"){
    contentType='application/x-www-form-urlencoded';
  }
  if (window.XMLHttpRequest){
    this.req=new XMLHttpRequest();
  } else if (window.ActiveXObject){
    this.req=new ActiveXObject("Microsoft.XMLHTTP");
  }
  if (this.req){
    try{
      var loader=this;
      this.req.onreadystatechange=function(){
        netblock.ContentLoader.onReadyState.call(loader);
      }
      this.req.open(method,url,false);
      if (contentType){
        this.req.setRequestHeader('Content-Type', contentType);
      }
      this.req.send(params);
    }catch (err){
      this.onerror.call(this);
    }
  }
}


netblock.ContentLoader.onReadyState=function(){
  var req=this.req;
  var ready=req.readyState;
  if (ready==netblock.READY_STATE_COMPLETE){
    var httpStatus=req.status;
    if (httpStatus==200 || httpStatus==0){
      this.onload.call(this);
    }else{
      this.onerror.call(this);
    }
  }
}

netblock.ContentLoader.prototype.defaultError=function(){
  alert("error fetching data!"
    +"\n\nreadyState:"+this.req.readyState
    +"\nstatus: "+this.req.status
    +"\nheaders: "+this.req.getAllResponseHeaders());
}


//区块的默认名称
var blockName;

function askBlockName(blockId){

        var pathforBlock = window.location.pathname;
        var posforBloclk = pathforBlock.indexOf("/",1);

        pathforBlock = pathforBlock.substr(0,posforBloclk+1);
        var url = pathforBlock + "templatejsp/getBlockName.jsp?blockId=" + blockId;

		//alert(url);

        var strParams = "tmpl=<wml>$#resource@previewUrl.resourceId=278#";



        var loader1 = new
        netblock.ContentLoader(url,getReturnBlockName,null,
                    "GET",null);

      }
      function getReturnBlockName(){
	
        blockName = this.req.responseText;

		

        //alert("data:" + xmlDoc);
       
      }



