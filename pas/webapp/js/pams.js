  function CloseSearchbar(element) 
  {	
	document.getElementById('Searchbar').style.display='none';
	parent.document.getElementById('SearchButton').className = "Search_normal";
	parent.searchState = "closed";
  }