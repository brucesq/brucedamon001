////////////////////////////////
///
///Format text
///
////////////////////////////////

//�Զ��Ű�������add by chencw2007-10-10	
function format()
{
	var result = "";
	result = document.forms[0].content.value;
		
	result = formattext(result,1);

	result = autoformat(result);
	//����ÿҳ200��
	result = textsplit(result,200);
	
	result = result.replace(/\r\n/g,"");

	document.forms[0].content.value=result;

	document.forms[0].submit.disabled=false;
}

function ispun(c1,c2)
{
	if ((c1 == '\xa1' && c2 == '\xa3') || (c1 == '\xa3' && c2 == '\xac')|| (c1 == '\xa3' && c2 == '\xbb') || (c1 == '\xa3' && c2 == '\xba') || (c1 == '\xa3' && c2 == '\xa1') || (c1 == '\xa1' && c2 == '\xb1') || (c3 == '\xa3' && c2 == '\xbf') || (c3 == '\xa1' && c2 == '\xb7'))
		return 1;
	return 0;
}

function ishalfpun(c2)
{
	if (c2 == '\x2c' || c2 =='\x2e' || c2 =='\x3b' || c2 =='\x2e' || c2 =='\x3a' || c2 =='\x21' || c2 =='\x3f' || c2 =='\x3e')
		return 1;
	return 0;
}

function formattext(text,addp){
  sbcarray = new Array("��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��","��");
  dbcarray = new Array("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",".");
  headarray = new Array("����Ѷ","��Ϣ","�յ�");
  var flag=0;
        //���Ȱ�"<p>"��"<br>"��"</p>"��"<p>����"ȫ���˵�
       var naivete_array =text.split("<p>����");
       if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }
	
	naivete_array =text.split("<p>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }
        //����ɾ����д�ո�+Сд�ո� modify by zm 2001.8.8
       naivete_array =text.split("�� ");
       if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }        
	naivete_array =text.split("<P>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }

	naivete_array =text.split("<br>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }
    //WMLҳ������ͬʱ��<br/>Ҳ���˵�  zhao 2005-8-31
    naivete_array =text.split("<br/>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }

 	naivete_array =text.split("<BR>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }
        
	naivete_array =text.split("</p>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }

	naivete_array =text.split("</P>");
        if (naivete_array.length >=0){
		text="";
	        for (loop=0; loop < naivete_array.length;loop++){
	                 text = text + naivete_array[loop];
	        }
        }

 oldlen = text.length;

        
	firstflg = 0; //�������ͷ���ո�س���־
	linenumber = 0;
	lcount = 0;

	tmpstring = "";
	oneline =1;
	for(i=0; i<oldlen; i++){
                c1 = text.charAt(i);
                
                tmpstring +=c1;
			
		if((c1 != '\n') && (c1!=' ') && (c1!='��')){
		    firstflg=1;
                    linenumber =1;
		}
                if(firstflg == 1){
		    if(c1 == '\n'){
		         linenumber=0;
		         dcount=0;
		    }
		    if(linenumber==1){
		       if(c1!='\r'){
			       if(c1 > '\xff')
			          lcount +=2;
			       else {
			          lcount+=1;
			       }
		       }
		       
		    }
    		    if(linenumber == 0){

                         for(j=i;j<=i+4;j++){
                            if(text.charAt(j) == '��'){
                                dcount +=2;
                            }else if(text.charAt(j) == '\x20'){
                                dcount += 1;
                            }
                         }
                         if(navigator.appName.indexOf("Netscape") != 0){
                            if(dcount >= 3){
		               tmpstring +="\r\n\r\n";
		            }
		         }
			 
		         lcount = 0;
		         linenumber=1;
		         oneline ++;
		    }
                
                }
	
	}
	
        text = tmpstring;
        oldlen = text.length;
	tmpstr = "";
	del = '1';
	
	firstflg = 0; 
	linenumber = 0;
	for(i=0; i<oldlen; i++){
			c1 = text.charAt(i);
			
			if (c1 == '\r'){
				del = '1';
				continue;
			}else{
					
				if((c1 != '\n') && (c1!=' ') && (c1!='��')){
				        firstflg=1;
				}	
				
				if(firstflg == 0){
				       continue;
				}
				
		                if(c1 > '\xff'){
		                       if ((c1 == ' ' && (del == '1')) || c1 == '\x09'){
						continue;
					}else{
						if (c1 == ' ' || c1 == '\n' || c1 == '\x09')
							del = '1';
						else
							del = '0';
						tmpstr += c1;
					}
				}else{
					if(c1 > '\x80'){
						del = '1';
						tmpstr += c1;
					}else{
						if ((c1 == ' ' && (del == '1')) || c1 == '\x09'){
							continue;
						}else{
							if (c1 == ' ' || c1 == '\n' || c1 == '\x09')
								del = '1';
							else
								del = '0';
							tmpstr += c1;
						}
					}
				}
			}
		}
		text = tmpstr;
        oldlen = text.length;
		tmpstr = "";
        for(i=0; i<oldlen; i++)
		{
			c1 = text.charAt(i);
			c2 = text.charAt(i+1);
			c3 = text.charAt(i+2);
			if (c1 == '\n' && c2 == '\n' && c3 == '\n')
				continue;
			tmpstr += c1;
		}
		text = tmpstr;
        oldlen = text.length;
        //result = (addp) ? "<p>" : "    "; //��ȫ�ǿո����
        if((text.charAt(0) != '\n') ||  (text.charAt(0) != '��')){
            result = ""; //��ȫ�ǿո����
        }else{
            result = (addp) ? "<p>����" : "    "; //��ȫ�ǿո����
            
        }
        count = 4;
	oneretn = 0;
        for(i=0; i<oldlen-1; i++){
                c1 = text.charAt(i);
                c2 = text.charAt(i+1);
                c3 = text.charAt(i+2);
                c4 = text.charAt(i+3);
                c5 = text.charAt(i+4);
                c6 = text.charAt(i+5);
                c7 = text.charAt(i+6);
                c8 = text.charAt(i+7);
                if (c1 == '\n'){
                     
					if (c2 == '\n'){
					    
						if (oneretn == 1)
							result += (addp) ? "</p>\n<p>" : "\n";
						else
							result += (addp) ? "</p>\n\n<p>" : "\n\n";
						//result += "    ";
						result += "����"; //��ȫ�ǿո����
						count = 4;
						i++;
						oneretn = 0;
					}
					continue;
                }else{
						if (c1 == " " && count == 0){
							continue;
						}else{
							if ((c1 == '\xa1' && c2 == '\xa1') && count == 0){
								i++;
								continue;
							}
						}
						oneretn = 0;
                        if(c1 > '\xff'){
			                            if(c1 == '��'){
						       result += ')';
						       count+=1;
						       continue;
						       
						    }
						    if(c1 == '��'){
						       result += '(';
						       count+=1;
						       continue;
						    }
						    if(c1 == '��'){
						       result += '��';
						       count+=1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '��';
						       count+=1;
						       continue;
						    }
						    if(c1 == '��'){
						       result += '-';
						       count += 1;
						       continue;
						    }
						    
						    if(c1 == '��'){
						       result += '.';
						       count += 1;
						       continue;
						    }
						    if(c1 == '��'){
						       result += '-';
						       count += 1;
						       continue;
						    }
						    if(c1 == '��'){
						       result += '1';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '2';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '3';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '4';
						       count += 1;
						       continue;
						    }
						    
						    if(c1 == '��'){
						       result += '5';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '6';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '7';
						       count += 1;
						       continue;
						    }
						    if(c1 == '��'){
						       result += '8';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '9';
						       count += 1;
						       continue;
						    }
			
						    if(c1 == '��'){
						       result += '0';
						       count += 1;
						       continue;
						    }

						    if(c1 == ''){
						       result += '0';
						       count += 1;
						       continue;
						    }

						    if(c1 == '��'){
						       result += '%';
						       count += 1;
						       continue;
						    }
						    
						    for(var cstep=0;cstep<52;cstep++){
						          if(c1 == sbcarray[cstep]){
							       flag = 1;
							       break;
						          }
						    }   
						    if(flag == 1){
							     result += dbcarray[cstep];
							     count += 1;
							     flag = 0;
							     continue;
						    }
                        
						    result += c1;
						    count+=1;
						    if (c2 != '\n'){
							  if (ishalfpun(c2)){
									result += c2;
									count+=1;
									i++;
								}else{
									if (c2 == '\x22' || c2== '\x27'){
										result += c2;
										count+=1;
										i++;
										if (c3 != '\n'){
											if (ishalfpun(c3)){
												result += c3;
												count+=1;
												i++;
											}
										}else{
											if (ishalfpun(c4)){
												result += c4;
												count+=1;
												i+=2;
											}
										}
									}
								}
							}else{
								if (ishalfpun(c3)){
									result += c3;
									count+=1;
									i+=2;
								}else{
									if (c3 == '\x22' || c3== '\x27'){
										result += c3;
										count+=1;
										i+=2;
										if (c4 != '\n'){
											if (ishalfpun(c4)){
												result += c4;
												count+=1;
												i+=2;
											}
										}else{
											if (ishalfpun(c5)){
												result += c5;
												count+=1;
												i+=3;
											}
										}
									}
								}
							}
                        }else if(c1 > '\x80'){
							if (c1 == '\xa1' && c2 == '\xa1'){//��space
								i++;
								continue;
							}
							result += c1;
							result += c2;
							count+=2;
							i++;
							if (c3 == '\n'){
								if (c4 == '\xa1' && c5 == '\xa3'){//��
									result += c4;
									result += c5;
									count+=2;
									i+=3;
								}else{
									if (c4 == '\xa3' && c5 == '\xac'){ //��
										result += c4;
										result += c5;
										count+=2;
										i+=3;
									}else{
										if (c4 == '\xa3' && c5 == '\xbb'){ //��
											result += c4;
											result += c5;
											count+=2;
											i+=3;
										}else{
											if (c4 == '\xa3' && c5 == '\xba'){ //��
												result += c4;
												result += c5;
												count+=2;
												i+=3;
											}else{
												if (c4 == '\xa3' && c5 == '\xa1'){ //��
													result += c4;
													result += c5;
													count+=2;
													i+=3;
												}else{
													if ((c4 == '\xa1' && c5 == '\xb1') || (c4 == '\xa1' && c5 == '\xaf')){ //��
														result += c4;
														result += c5;
														count+=2;
														i+=3;
														if (c6 == '\n'){
															if (ispun(c7,c8) == 1){
																result += c7;
																result += c8;
																count+=2;
																i+=3;
															}
														}else{
															if ((a =ispun(c6,c7)) == 1){
																result += c6;
																result += c7;
																count+=2;
																i+=2;
															}
														}
													}else{
														if (c3 == '\xa3' && c4 == '\xbf'){ //��
															result += c3;
															result += c4;
															count+=2;
															i+=3;
														}else{
															if (c3 == '\xa1' && c4 == '\xb7'){ //��
																result += c3;
																result += c4;
																count+=2;
																i+=3;
															}else{
														        }
														}
													}
												}
											}
										}
									}
								}
							}else{
								if (c3 == '\xa1' && c4 == '\xa3'){ //��
									result += c3;
									result += c4;
									count+=2;
									i+=2;
								}else{
									if (c3 == '\xa3' && c4 == '\xac'){ //��
										result += c3;
										result += c4;
										count+=2;
										i+=2;
									}else{
										if (c3 == '\xa3' && c4 == '\xbb'){//��
											result += c3;
											result += c4;
											count+=2;
											i+=2;
										}else{
											if (c3 == '\xa3' && c4 == '\xba'){//��
												result += c3;
												result += c4;
												count+=2;
												i+=2;
											}else{
												if (c3 == '\xa3' && c4 == '\xa1'){//��
													result += c3;
													result += c4;
													count+=2;
													i+=2;
												}else{
													if ((c3 == '\xa1' && c4 == '\xb1') || (c3 == '\xa1' && c4 == '\xaf')){//�� or  ��
														result += c3;
														result += c4;
														count+=2;
														i+=2;
														if (c5 == '\n'){  
															if (ispun(c6,c7) == 1){
																result += c6;
																result += c7;
																count+=2;
																i+=3;
															}
														}else{
															if (ispun(c5,c6) == 1){
																result += c5;
																result += c6;
																count+=2;
																i+=2;
															}
														}
													}else{
														if (c3 == '\xa3' && c4 == '\xbf'){  //��
															result += c3;
															result += c4;
															count+=2;
															i+=2;
														}else{
															if (c3 == '\xa1' && c4 == '\xb7'){//  ��
																result += c3;
																result += c4;
																count+=2;
																i+=2;
															}else{
															}
														}
													}
												}
											}
										}
									}
								}
							}
                        }else{
			    result += c1;
                            count++;
                        }
                }
                //if(count>=55){
		 //   result += (addp) ? "\n" : "\n";
		  //  oneretn = 1;
                   // count = 0;
               // }
        }
        if(i<oldlen)
                result += text.charAt(i);
        if(addp)
                result +="</p>\n";
                
                
	//"<p>��������"=>"<p>����"
       var naivete_array =result.split("<br>��������");
       if (naivete_array.length >1){
	      result="";
        for (loop=0; loop < naivete_array.length;loop++){
                 if(result != ""){ result = result +"<br>����"+ naivete_array[loop];}
                 else{ result = naivete_array[loop];}
                 }
	}
       var naivete_array =result.split("<p>��������");
       if (naivete_array.length >1){
	      result="";
              for (loop=0; loop < naivete_array.length;loop++){
                   if(naivete_array[loop] !=""){
                   result = result +"<p>����"+ naivete_array[loop];
                   }
              }
	}else{
	    result = "<p>����"+naivete_array;
	}
	
	        //modified by jxz 2000.6.13
        //����Ϊ�������ȫ���ַ���Ŀո�,�ұ�������ַ���Ŀո�
        
        //modified by zhaoming 2001.8.7  jxz���ܸĽ�
				//�Ƚ�ȫ�ǿո�ת�ɰ�ǿո�
			  //���Сд�ո������ǰ���ַ�(��ĸ����),��ÿո���
			  //���Сд�ո����߲��ǰ���ַ�(��ĸ����)��ȫ��������ĸ,��ȥ���ո�
				oldlen = result.length;
        result1 = "";
        spaceflg = 0;
        for(i=0; i<oldlen; i++){
                c1 = result.charAt(i);

                codevalue1 = result.charCodeAt(i);
                c2 = result.charAt(i+1);
                codevalue2 = result.charCodeAt(i+1);
                c3 = result.charAt(i+2);
                codevalue3 = result.charCodeAt(i+2);
                if( (codevalue1 <127) && (codevalue3 <127))
                   spaceflg=0;
                else
                   spaceflg=1;
                
                result1 += c1;
                if( (spaceflg == 0) &&  ((c2 == '��')||(c2 == ' ')) ){
               	result1 += ' ';
               	i+=1;
                }
                if( (spaceflg == 1) &&  ((c2 == '��')||(c2 == ' '))&&((c1 != '>')&&(c1 != '��')) ){
                        // alert("c1="+c1);
													i+=1;
													
                }
        }
        
        result = result1;
        oldlen = result.length;
        //modified by jxz 2000.6.13
	
	
        //���ѽ�β����"<br>����<br>"�˵�
       var naivete_array =result.split("<br>����<br>");
       if (naivete_array.length >1){
	result="";
        for (loop=0; loop < naivete_array.length;loop++){
                 result = result + naivete_array[loop];
                 }
	}


        //���ѽ�β����"<p>����</p>"�˵�
       var naivete_array =result.split("<p>����</p>");
       if (naivete_array.length >1){
	result="";
        for (loop=0; loop < naivete_array.length;loop++){
                 result = result + naivete_array[loop];
                 }
	}


        //���ѽ�β����"����<br>"�˵�
       var naivete_array =result.split("����<br>");
       if (naivete_array.length >1){
	result="";
        for (loop=0; loop < naivete_array.length;loop++){
                 result = result + naivete_array[loop];
                 }
	}



       //����"http://xxxx[ ]"�滻Ϊ"<a href=http://xxx>xxx</a>"�˵�
       var naivete_array =result.split("http:");
       var mytag=0;
       var checkflag;
       if (naivete_array.length >=0){
	      result="";
        for (loop=0; loop < naivete_array.length;loop++){
                    mytag=naivete_array[loop].indexOf(' ');
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".shtml");
                        if(mytag>0){mytag+=6;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf("\n.shtml");
                        if(mytag>0){mytag+=7;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".\nshtml");
                        if(mytag>0){mytag+=7;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".s\nhtml");
                        if(mytag>0){mytag+=7;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".sh\ntml");
                        if(mytag>0){mytag+=7;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".sht\nml");
                        if(mytag>0){mytag+=7;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".shtm\nl");
                        if(mytag>0){mytag+=7;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".html");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf("\n.html");
                        if(mytag>0){mytag+=6;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".\nhtml");
                        if(mytag>0){mytag+=6;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".h\ntml");
                        if(mytag>0){mytag+=6;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".ht\nml");
                        if(mytag>0){mytag+=6;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".htm\nl");
                        if(mytag>0){mytag+=6;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".htm");
                        if(mytag>0){mytag+=4;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf("\n.htm");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".\nhtm");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".h\ntm");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".ht\nm");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".asp");
                        if(mytag>0){mytag+=4;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf("\n.asp");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".\nasp");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".a\nsp");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".as\np");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".php");
                        if(mytag>0){mytag+=4;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf("\n.php");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".\nphp");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".p\nhp");
                        if(mytag>0){mytag+=5;}
                    }
                    if(mytag<0){
                        mytag=naivete_array[loop].indexOf(".ph\np");
                        if(mytag>0){mytag+=5;}
                    }                    
                    if(naivete_array[loop].substring(0,2) =='//'){
                    	naivete_array[loop]='http:'+naivete_array[loop];
                    	mytag=mytag+5;
                    }
                    checkflag = 0;
                    if(naivete_array[loop].substring(0,7) =='http://' && mytag>5 && mytag<100){
                             for(var step1=1;step1<mytag;step1++ ){
                                temp = naivete_array[loop].substring(step1-1,step1);
                                if((temp>'\x7f')){
                                    checkflag = 1;
                                    break;
                                }
                             }
                             
                             if(checkflag == 1){
	                             var temp="";
	                             for(step=8;step<mytag;step++){
	                                  temp = naivete_array[loop].substring(step-1,step);
	                                  if((temp<'\x30' && temp != '\x2e' && temp != '\x2d' && temp != '\x2c') || (temp>'\x7a') || ( (temp>'\x5a')&&(temp<'\x61')&&(temp != '\x5f') )){
	                                      mytag = step-1;
	                                      break;
	                                  }
	                             }
                             }
	                     
	                     myurl=naivete_array[loop].substring(0,mytag);
	                     tmpurl = naivete_array[loop].substring(7,mytag);
	                     myurl_true=myurl.replace("\n","");
	                     myurl_href='<a href='+myurl_true+'>'+tmpurl+'</a>';
	                     if(loop>0){
	                     	if(naivete_array[loop-1].substring(naivete_array[loop-1].length-1,naivete_array[loop-1].length)=='>' || naivete_array[loop-1].substring(naivete_array[loop-1].length-5,naivete_array[loop-1].length)=='href='){
		                       result = result + naivete_array[loop];
	                        }else{
		                       result = result + naivete_array[loop].replace(myurl,myurl_href);
	                        }  
	                     }  
                   }else if(naivete_array[loop].substring(0,7) =='http://' && mytag==4){
                             var temp="";
                             for(var step=8;step<100;step++){
                                  temp = naivete_array[loop].substring(step-1,step);
                                  if((temp<'\x30' && temp != '\x2e' && temp != '\x2d' && temp != '\x2c') || (temp>'\x7a') || ( (temp>'\x5a')&&(temp<'\x61')&&(temp != '\x5f') )){
                                      mytag = step-1;
                                      break;
                                  }
                             }
                             
                             myurl=naivete_array[loop].substring(0,mytag);
                             tmpurl = naivete_array[loop].substring(7,mytag);
	                     myurl_true=myurl.replace("\n","");
	                     myurl_href='<a href='+myurl_true+'>'+tmpurl+'</a>';
	                     if(loop>0){
	                        if(naivete_array[loop-1].substring(naivete_array[loop-1].length-1,naivete_array[loop-1].length)=='>' || naivete_array[loop-1].substring(naivete_array[loop-1].length-5,naivete_array[loop-1].length)=='href='){
		                       result = result + naivete_array[loop];
	                        }else{
		                       result = result + naivete_array[loop].replace(myurl,myurl_href);
	                        }  
	                     }  
                   
                   }else{
                             result = result + naivete_array[loop];
                   }
                 }
	      }
        

                
        return result;
}


function formattext2(text,addp)
{
        //���Ȱ�"<p>"��"<br>"��"</p>"��"<p>����"ȫ���˵�
       var naivete_array =text.split("<p>����");
       if (naivete_array.length >0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
	}
	
	naivete_array =text.split("<p>");
        if (naivete_array.length >0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
        }
        
	naivete_array =text.split("<br>����");
        if (naivete_array.length >0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
        }


	naivete_array =text.split("<br>");
        if (naivete_array.length >0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
        }
        
	naivete_array =text.split("</p>");
        if (naivete_array.length >0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
         }

        oldlen = text.length;
		tmpstr = "";
		del = '1';
        for(i=0; i<oldlen; i++)
		{
			c1 = text.charAt(i);
			if (c1 == '\r')
			{
				del = '1';
				continue;
			}
			else
			{
                if(c1 > '\xff')
				{
					if ((c1 == ' ' && (del == '1')) || c1 == '\x09')
					{
						continue;
					}
					else
					{
						if (c1 == ' ' || c1 == '\n' || c1 == '\x09')
							del = '1';
						else
							del = '0';
						tmpstr += c1;
					}
				}
				else
				{
					if(c1 > '\x80')
					{
						del = '1';
						tmpstr += c1;
					}
					else
					{
						if ((c1 == ' ' && (del == '1')) || c1 == '\x09')
						{
							continue;
						}
						else
						{
							if (c1 == ' ' || c1 == '\n' || c1 == '\x09')
								del = '1';
							else
								del = '0';
							tmpstr += c1;
						}
					}
				}
			}
		}
		text = tmpstr;
        oldlen = text.length;
		tmpstr = "";
        for(i=0; i<oldlen; i++)
		{
			c1 = text.charAt(i);
			c2 = text.charAt(i+1);
			c3 = text.charAt(i+2);
			if (c1 == '\n' && c2 == '\n' && c3 == '\n')
				continue;
			tmpstr += c1;
		}
		text = tmpstr;
        oldlen = text.length;
        //result = (addp) ? "<p>" : "    "; //��ȫ�ǿո����
        result = (addp) ? "<p>����" : "    "; //��ȫ�ǿո����
        count = 4;
		oneretn = 0;
        for(i=0; i<oldlen-1; i++)
        {
                c1 = text.charAt(i);
                c2 = text.charAt(i+1);
                c3 = text.charAt(i+2);
                c4 = text.charAt(i+3);
                c5 = text.charAt(i+4);
                c6 = text.charAt(i+5);
                c7 = text.charAt(i+6);
                c8 = text.charAt(i+7);
                if (c1 == '\n')
                {
					if (c2 == '\n')
					{
						if (oneretn == 1)
							result += (addp) ? "</p>\n<p>" : "\n";
						else
							result += (addp) ? "</p>\n\n<p>" : "\n\n";
						//result += "    ";
						result += "����"; //��ȫ�ǿո����
						count = 4;
						i++;
						oneretn = 0;
					}
					continue;
                }
                else
                {
						if (c1 == " " && count == 0)
						{
							continue;
						}
						else
						{
							if ((c1 == '\xa1' && c2 == '\xa1') && count == 0)
							{
								i++;
								continue;
							}
						}
						oneretn = 0;
                        if(c1 > '\xff')
                        {
							result += c1;
							count+=1;
							if (c2 != '\n')
							{
								if (ishalfpun(c2))
								{
									result += c2;
									count+=1;
									i++;
								}
								else
								{
									if (c2 == '\x22' || c2== '\x27')
									{
										result += c2;
										count+=1;
										i++;
										if (c3 != '\n')
										{
											if (ishalfpun(c3))
											{
												result += c3;
												count+=1;
												i++;
											}
										}
										else
										{
											if (ishalfpun(c4))
											{
												result += c4;
												count+=1;
												i+=2;
											}
										}
									}
								}
							}
							else
							{
								if (ishalfpun(c3))
								{
									result += c3;
									count+=1;
									i+=2;
								}
								else
								{
									if (c3 == '\x22' || c3== '\x27')
									{
										result += c3;
										count+=1;
										i+=2;
										if (c4 != '\n')
										{
											if (ishalfpun(c4))
											{
												result += c4;
												count+=1;
												i+=2;
											}
										}
										else
										{
											if (ishalfpun(c5))
											{
												result += c5;
												count+=1;
												i+=3;
											}
										}
									}
								}
							}
                        }
                        else if(c1 > '\x80')
                        {
							if (c1 == '\xa1' && c2 == '\xa1')
							{
								i++;
								continue;
							}
							result += c1;
							result += c2;
							count+=2;
							i++;
							if (c3 == '\n')
							{
								if (c4 == '\xa1' && c5 == '\xa3') //��
								{
									result += c4;
									result += c5;
									count+=2;
									i+=3;
								}
								else
								{
									if (c4 == '\xa3' && c5 == '\xac') //��
									{
										result += c4;
										result += c5;
										count+=2;
										i+=3;
									}
									else
									{
										if (c4 == '\xa3' && c5 == '\xbb') //��
										{
											result += c4;
											result += c5;
											count+=2;
											i+=3;
										}
										else
										{
											if (c4 == '\xa3' && c5 == '\xba') //��
											{
												result += c4;
												result += c5;
												count+=2;
												i+=3;
											}
											else
											{
												if (c4 == '\xa3' && c5 == '\xa1') //��
												{
													result += c4;
													result += c5;
													count+=2;
													i+=3;
												}
												else
												{
													if ((c4 == '\xa1' && c5 == '\xb1') || (c4 == '\xa1' && c5 == '\xaf'))//��
													{
														result += c4;
														result += c5;
														count+=2;
														i+=3;
														if (c6 == '\n')
														{
															if (ispun(c7,c8) == 1)
															{
																result += c7;
																result += c8;
																count+=2;
																i+=3;
															}
														}
														else
														{
															if ((a =ispun(c6,c7)) == 1)
															{
																result += c6;
																result += c7;
																count+=2;
																i+=2;
															}
														}
													}
													else
													{
														if (c3 == '\xa3' && c4 == '\xbf') //��
														{
															result += c3;
															result += c4;
															count+=2;
															i+=3;
														}
														else
														{
															if (c3 == '\xa1' && c4 == '\xb7') //��
															{
																result += c3;
																result += c4;
																count+=2;
																i+=3;
															}
															else
															{
															}
														}
													}
												}
											}
										}
									}
								}
							}
							else
							{
								if (c3 == '\xa1' && c4 == '\xa3')
								{
									result += c3;
									result += c4;
									count+=2;
									i+=2;
								}
								else
								{
									if (c3 == '\xa3' && c4 == '\xac')
									{
										result += c3;
										result += c4;
										count+=2;
										i+=2;
									}
									else
									{
										if (c3 == '\xa3' && c4 == '\xbb')
										{
											result += c3;
											result += c4;
											count+=2;
											i+=2;
										}
										else
										{
											if (c3 == '\xa3' && c4 == '\xba')
											{
												result += c3;
												result += c4;
												count+=2;
												i+=2;
											}
											else
											{
												if (c3 == '\xa3' && c4 == '\xa1')
												{
													result += c3;
													result += c4;
													count+=2;
													i+=2;
												}
												else
												{
													if ((c3 == '\xa1' && c4 == '\xb1') || (c3 == '\xa1' && c4 == '\xaf'))
													{
														result += c3;
														result += c4;
														count+=2;
														i+=2;

														if (c5 == '\n')
														{
															if (ispun(c6,c7) == 1)
															{
																result += c6;
																result += c7;
																count+=2;
																i+=3;
															}
														}
														else
														{
															if (ispun(c5,c6) == 1)
															{
																result += c5;
																result += c6;
																count+=2;
																i+=2;
															}
														}
													}
													else
													{
														if (c3 == '\xa3' && c4 == '\xbf')
														{
															result += c3;
															result += c4;
															count+=2;
															i+=2;
														}
														else
														{
															if (c3 == '\xa1' && c4 == '\xb7')
															{
																result += c3;
																result += c4;
																count+=2;
																i+=2;
															}
															else
															{
															}
														}
													}
												}
											}
										}
									}
								}
							}
                        }
                        else
                        {
							result += c1;
                            count++;
                        }
                }
                if(count>=57)
                {
					result += (addp) ? "\n" : "\n";
					oneretn = 1;
                    count = 0;
                }
        }
        if(i<oldlen)
                result += text.charAt(i);
        if(addp)
                result +="</p>\n";


	//"<p>��������"=>"<p>����"
       var naivete_array =result.split("<br>��������");
       if (naivete_array.length >1)
	{
	      result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 if(result != ""){ result = result +"<br>����"+ naivete_array[loop];}
                 else{ result = naivete_array[loop];}
                 }
	}
       var naivete_array =result.split("<p>��������");
       if (naivete_array.length >1)
	{
	      result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                   if(naivete_array[loop] !=""){
                   result = result +"<p>����"+ naivete_array[loop];
                   }
                 }
	}
	
        //���ѽ�β����"<br>����<br>"�˵�
       var naivete_array =result.split("<br>����<br>");
       if (naivete_array.length >1)
	{
	result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 result = result + naivete_array[loop];
                 }
	}


        //���ѽ�β����"<p>����</p>"�˵�
       var naivete_array =result.split("<p>����</p>");
       if (naivete_array.length >1)
	{
	result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 result = result + naivete_array[loop];
                 }
	}


        //���ѽ�β����"����<br>"�˵�
       var naivete_array =result.split("����<br>");
       if (naivete_array.length >1)
	{
	result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 result = result + naivete_array[loop];
                 }
	}


        return result;                                                      
}


function autoformat(text)
{
	/*var naivete_array =text.split("<p>");
    if (naivete_array.length >=0)
	{
	     text="";
         for (loop=0; loop < naivete_array.length;loop++)
		 {
		 text = text + naivete_array[loop];
		 }
	}

	var naivete_array =text.split("</p>");
    if (naivete_array.length >=0)
	{
	     text="";
         for (loop=0; loop < naivete_array.length;loop++)
		 {
		     if(loop == (naivete_array.length-1))
			 {
				 text = text + naivete_array[loop];
			 }
			 else
			 {
				 text = text + naivete_array[loop]+"<br/>";
			 }
		 }
	}*/
	text = replace(text,"<p>","");
	text = replace(text,"��",".");
	text = replace(text,"��","-");
	text = replace(text,"��","-");
	text = replace(text,"��","%");
	text = replace(text,"��","/");

	text = replace(text,"</p>","<br/>");
	text = replace(text,"<br/><br/>","<br/>");
	
	text = trim(text);//���˵��س� �ո� �Ʊ���ȿհ��ַ�

	if(text.substring(text.length-5)=="<br/>")
	{
		text = text.substring(0,text.length-5);
	}
	return text;
}
function textsplit(txt,len)
{
	var result  = "";
	var tmpStr = "";

	if ( txt == null || txt ==  ""  )
		return null ;
    txt = replace(txt,"#aspire page split tag#","");

	var txt_array = txt.split("<br/>");

	for(i = 0; i<txt_array.length; i++)
	{
		if(txt_array[i]=="")
		{
			continue;
		}
		tmpStr = txt_array[i];
		while(tmpStr.length<len)
		{
			i = i + 1 ;
			if(i<txt_array.length)
			{
				tmpStr = tmpStr +"<br/>" + txt_array[i];
			}else
			{
				break;
			}
			
		}
		if(i==txt_array.length-1)
		{
			result = result + tmpStr;
		}else
		{
			result = result + tmpStr + "#aspire page split tag#";
		}
		tmpStr = "";
	}
	result = replace(result,"<br/><br/>","<br/>");
	
	result = trim(result);//���˵��س� �ո� �Ʊ���ȿհ��ַ�
    //alert(result.substring(result.length-23));

	if(result.substring(result.length-23)=="#aspire page split tag#")
	{
		result = result.substring(0,result.length-23);
	}
	return result;

}

function trim(str)
{
	return str.replace(/(^\s*)|(\s*$)/g, "");
}

function formattext1(text,addp)
{
       //var text=text_obj.value; 
        //���Ȱ�"<p>"��"<br>"��"</p>"��"<p>����"��"<br>����"ȫ���˵�
       var naivete_array =text.split("<p>����");
       if (naivete_array.length >=0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
	}
	
	naivete_array =text.split("<p>");
        if (naivete_array.length >=0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
        }
        
	naivete_array =text.split("<br>����");
        if (naivete_array.length >=0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
        }

	naivete_array =text.split("<br>");
        if (naivete_array.length >=0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
        }
        
	naivete_array =text.split("</p>");
        if (naivete_array.length >=0)
	{
	text="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 text = text + naivete_array[loop];
                 }
         }

        oldlen = text.length;
		tmpstr = "";
		del = '1';
        for(i=0; i<oldlen; i++)
		{
			c1 = text.charAt(i);
			if (c1 == '\r')
			{
				del = '1';
				continue;
			}
			else
			{
                if(c1 > '\xff')
				{
					if ((c1 == ' ' && (del == '1')) || c1 == '\x09')
					{
						continue;
					}
					else
					{
						if (c1 == ' ' || c1 == '\n' || c1 == '\x09')
							del = '1';
						else
							del = '0';
						tmpstr += c1;
					}
				}
				else
				{
					if(c1 > '\x80')
					{
						del = '1';
						tmpstr += c1;
					}
					else
					{
						if ((c1 == ' ' && (del == '1')) || c1 == '\x09')
						{
							continue;
						}
						else
						{
							if (c1 == ' ' || c1 == '\n' || c1 == '\x09')
								del = '1';
							else
								del = '0';
							tmpstr += c1;
						}
					}
				}
			}
		}
		text = tmpstr;
        oldlen = text.length;
		tmpstr = "";
        for(i=0; i<oldlen; i++)
		{
			c1 = text.charAt(i);
			c2 = text.charAt(i+1);
			c3 = text.charAt(i+2);
			if (c1 == '\n' && c2 == '\n' && c3 == '\n')
				continue;
			tmpstr += c1;
		}
		text = tmpstr;
        oldlen = text.length;
        //result = (addp) ? "<p>" : "    "; //��ȫ�ǿո����
        result = (addp) ? "<br>����" : "    "; //��ȫ�ǿո����
        count = 4;
		oneretn = 0;
        for(i=0; i<oldlen-1; i++)
        {
                c1 = text.charAt(i);
                c2 = text.charAt(i+1);
                c3 = text.charAt(i+2);
                c4 = text.charAt(i+3);
                c5 = text.charAt(i+4);
                c6 = text.charAt(i+5);
                c7 = text.charAt(i+6);
                c8 = text.charAt(i+7);
                if (c1 == '\n')
                {
					if (c2 == '\n')
					{
						if (oneretn == 1)
							result += (addp) ? "\n<br>" : "\n";
						else
							result += (addp) ? "\n\n<br><br>" : "\n\n";
						//result += "    ";
						result += "����"; //��ȫ�ǿո����
						count = 4;
						i++;
						oneretn = 0;
					}
					continue;
                }
                else
                {
						if (c1 == " " && count == 0)
						{
							continue;
						}
						else
						{
							if ((c1 == '\xa1' && c2 == '\xa1') && count == 0)
							{
								i++;
								continue;
							}
						}
						oneretn = 0;
                        if(c1 > '\xff')
                        {
							result += c1;
							count+=1;
							if (c2 != '\n')
							{
								if (ishalfpun(c2))
								{
									result += c2;
									count+=1;
									i++;
								}
								else
								{
									if (c2 == '\x22' || c2== '\x27')
									{
										result += c2;
										count+=1;
										i++;
										if (c3 != '\n')
										{
											if (ishalfpun(c3))
											{
												result += c3;
												count+=1;
												i++;
											}
										}
										else
										{
											if (ishalfpun(c4))
											{
												result += c4;
												count+=1;
												i+=2;
											}
										}
									}
								}
							}
							else
							{
								if (ishalfpun(c3))
								{
									result += c3;
									count+=1;
									i+=2;
								}
								else
								{
									if (c3 == '\x22' || c3== '\x27')
									{
										result += c3;
										count+=1;
										i+=2;
										if (c4 != '\n')
										{
											if (ishalfpun(c4))
											{
												result += c4;
												count+=1;
												i+=2;
											}
										}
										else
										{
											if (ishalfpun(c5))
											{
												result += c5;
												count+=1;
												i+=3;
											}
										}
									}
								}
							}
                        }
                        else if(c1 > '\x80')
                        {
							if (c1 == '\xa1' && c2 == '\xa1')
							{
								i++;
								continue;
							}
							result += c1;
							result += c2;
							count+=2;
							i++;
							if (c3 == '\n')
							{
								if (c4 == '\xa1' && c5 == '\xa3') //��
								{
									result += c4;
									result += c5;
									count+=2;
									i+=3;
								}
								else
								{
									if (c4 == '\xa3' && c5 == '\xac') //��
									{
										result += c4;
										result += c5;
										count+=2;
										i+=3;
									}
									else
									{
										if (c4 == '\xa3' && c5 == '\xbb') //��
										{
											result += c4;
											result += c5;
											count+=2;
											i+=3;
										}
										else
										{
											if (c4 == '\xa3' && c5 == '\xba') //��
											{
												result += c4;
												result += c5;
												count+=2;
												i+=3;
											}
											else
											{
												if (c4 == '\xa3' && c5 == '\xa1') //��
												{
													result += c4;
													result += c5;
													count+=2;
													i+=3;
												}
												else
												{
													if ((c4 == '\xa1' && c5 == '\xb1') || (c4 == '\xa1' && c5 == '\xaf'))//��
													{
														result += c4;
														result += c5;
														count+=2;
														i+=3;
														if (c6 == '\n')
														{
															if (ispun(c7,c8) == 1)
															{
																result += c7;
																result += c8;
																count+=2;
																i+=3;
															}
														}
														else
														{
															if ((a =ispun(c6,c7)) == 1)
															{
																result += c6;
																result += c7;
																count+=2;
																i+=2;
															}
														}
													}
													else
													{
														if (c3 == '\xa3' && c4 == '\xbf') //��
														{
															result += c3;
															result += c4;
															count+=2;
															i+=3;
														}
														else
														{
															if (c3 == '\xa1' && c4 == '\xb7') //��
															{
																result += c3;
																result += c4;
																count+=2;
																i+=3;
															}
															else
															{
															}
														}
													}
												}
											}
										}
									}
								}
							}
							else
							{
								if (c3 == '\xa1' && c4 == '\xa3')
								{
									result += c3;
									result += c4;
									count+=2;
									i+=2;
								}
								else
								{
									if (c3 == '\xa3' && c4 == '\xac')
									{
										result += c3;
										result += c4;
										count+=2;
										i+=2;
									}
									else
									{
										if (c3 == '\xa3' && c4 == '\xbb')
										{
											result += c3;
											result += c4;
											count+=2;
											i+=2;
										}
										else
										{
											if (c3 == '\xa3' && c4 == '\xba')
											{
												result += c3;
												result += c4;
												count+=2;
												i+=2;
											}
											else
											{
												if (c3 == '\xa3' && c4 == '\xa1')
												{
													result += c3;
													result += c4;
													count+=2;
													i+=2;
												}
												else
												{
													if ((c3 == '\xa1' && c4 == '\xb1') || (c3 == '\xa1' && c4 == '\xaf'))
													{
														result += c3;
														result += c4;
														count+=2;
														i+=2;

														if (c5 == '\n')
														{
															if (ispun(c6,c7) == 1)
															{
																result += c6;
																result += c7;
																count+=2;
																i+=3;
															}
														}
														else
														{
															if (ispun(c5,c6) == 1)
															{
																result += c5;
																result += c6;
																count+=2;
																i+=2;
															}
														}
													}
													else
													{
														if (c3 == '\xa3' && c4 == '\xbf')
														{
															result += c3;
															result += c4;
															count+=2;
															i+=2;
														}
														else
														{
															if (c3 == '\xa1' && c4 == '\xb7')
															{
																result += c3;
																result += c4;
																count+=2;
																i+=2;
															}
															else
															{
															}
														}
													}
												}
											}
										}
									}
								}
							}
                        }
                        else
                        {
							result += c1;
                            count++;
                        }
                }
                if(count>=40)
                {
					result += (addp) ? "\n" : "\n";//result += (addp) ? "\n" : "\n";
					oneretn = 1;
                    count = 0;
                }
        }
        if(i<oldlen)
                result += text.charAt(i);
        if(addp)
                result +="<br>"; //"</p>\n";
        
        //text_obj.value = result;                 

	//"<p>��������"=>"<p>����"
       var naivete_array =result.split("<br>��������");
       if (naivete_array.length >=0)
	{
	result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 result = result +"<br>����"+ naivete_array[loop];
                 }
	}
	
        //���ѽ�β����"<br>����<br>"�˵�
       var naivete_array =result.split("<br>����<br>");
       if (naivete_array.length >=0)
	{
	result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 result = result + naivete_array[loop];
                 }
	}

        //���ѽ�β����"����<br>"�˵�
       var naivete_array =result.split("����<br>");
       if (naivete_array.length >=0)
	{
	result="";
        for (loop=0; loop < naivete_array.length;loop++)
                 {
                 result = result + naivete_array[loop];
                 }
	}

        
        return result;                                                      
}



function adjust(f)
{
        document.news.state.value = "�����Ű档����";
        document.news._body.value = formattext(document.news._body.value,1);
        document.news.state.value = "�Ű������";
}




////////////////////////////////
///
///open a new navigator window
///
///js_callpage("http://192.168.23.200:8344/publish/sys_entry.html","Sina_publish_system","toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes");
////////////////////////////////
function js_callpage(htmlurl,title,winattrib) {
if(navigator.appName.indexOf("Netscape") != -1)
  {
    var newwin=window.open(htmlurl,title,winattrib);
    //self.window.close();
    //self.window=null;
    newwin.focus();
  }
else
  { 
  if(self.newwin != null)
	{
	  self.newwin.close();
	  newwin = null;
    var   newwin=window.open(htmlurl,title,winattrib);
	}
  else
    {
    var  newwin=window.open(htmlurl,title,winattrib);
  } 
  //self.window=null;
  //self.window.close();
  newwin.focus();
 }

 return true;
}




////////////////////////////////
//
//HomepageSetup
//
////////////////////////////////
function HomepageSetup(key,value,msg) { 

//if(key == 'homepage'){key='sp_homepage';}

  var answer = confirm(msg); 
  
  if (answer) {
         toCookie(key,value,1);
          //refresh menubar
         //alert(parent.length);
         if(parent.length >0){parent.frames[0].location.reload();}
  }
//  else {
//         toCookie(key,'$publish_server_homepage');
//  } 
  
   
  //key1="operator_id";
  //value1="$operator_id";

  //toCookie(key1,value1);

  return false;
} 




////////////////////////////////
//
//Set Cookie 
//expire(year_num)
///////////////////////////////

function toCookie(cookiename,cookievalue,expire) 
{ 
  document.cookie = '';
  if(expire != 0)
  {
  	expr =makeYearExpDate(expire) ;
  }
  cookiename = fixSep(cookiename) ;
  astr = fixSep(cookievalue) ;
  if(expire != 0)
 	{
 		astr = cookiename + '=' + astr + ';expires=' + expr + ';path=/'  + '';
 	}
  else
	{
		astr = cookiename + '=' + astr + ';path=/'  + '';
	}
  document.cookie=astr ;
  return false;
} 


function SetCookie(name,value,expires,path,domain,secure)
{
	document.cookie = name + "=" + escape(value) + 
	((expires)?"; expires=" + expires.toGMTString():"") + 
	((path)?"; path=" + path:"")+
	((domain)?"; domain=" + domain:"")+
	((secure)?"; secure":"");
}


function DeleteCookie(name,path,domain)
{
	if(GetCookie(name))
	{
		document.cookie = name + "=" + 
		((path)?"; path=" + path:"")+
		((domain)?"; domain=" + domain:"")+
		";expires=Thu,01-Jan-70 00:00:01 GMT";
	}
}

function getCookie(name)
{
	if(name == null)return null;
	var cookies = document.cookie;
	var begin = cookies.indexOf(name+'=');
	var end = cookies.indexOf(';',begin);
	if(begin >0 && end > begin)
	{
		return unescape(cookies.substring(begin,end));
	}
	return null;
}

function remeber(p_id,t_id,f_id,on)
{
	if(p_id >0 && t_id >0 && f_id >0)
	{
		if(on == 0 || on == 1)
		{
			var permit = "#" + p_id + ":" + t_id + ":" + f_id + ":" + 1 + "#";
			var forbid = "#" + p_id + ":" + t_id + ":" + f_id + ":" + 0 + "#";
			var command = (on == 0)?forbid:permit;
			var CrossCookie = getCookie('Cross');
			if(CrossCookie == null)
			{
				CrossCookie = command;
			}
			else
			{
				CrossCookie = replace(CrossCookie,'Cross=','');
				if(CrossCookie.indexOf(permit) < 0 && CrossCookie.indexOf(forbid) < 0)
				{
					CrossCookie = CrossCookie + command;
				}
				else
				{
					//����
					if(on == 1)
					{
						if(CrossCookie.indexOf(permit) < 0)
						{
							CrossCookie = replace(CrossCookie,forbid,permit);
						}
					}
					else
					{
						if(CrossCookie.indexOf(forbid) < 0)
						{
							CrossCookie = replace(CrossCookie,permit,forbid);
						}
					}
				}
			}
			toCookie('Cross',CrossCookie,1);
			alert("�������!�´���Ч!");
		}
	}
}


function replace(str,pattern,replace)
{
  var s = 0;
  var e = 0;
  var result = '';

  while ((e = str.indexOf(pattern, s)) >= 0)
  {
    result += str.substring(s, e);
    result += replace;
    s = e+pattern.length;
  }
  result += str.substring(s);
  return result;
}

function GetCookie(name)
{
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
	var i = 0;
	while(i<clen)
	{
		var j = i + alen;
		if(document.cookie.substring(i,j) == arg)
		{
			return getCookieVal(j);
		}
		i++;
		if(i == 0)
		{
			break;
		}
	}
	return null;
}

function getCookieVal(offset)
{
	var endstr = document.cookie.indexOf(";",offset);
	if(endstr == -1)
	{
		endstr = document.cookie.length;
	}
	return unescape(document.cookie.substring(offset,endstr));
}


function FixCookieData(date)
{
	var base = new Date();
	var skew = base.getTime();
	if(skew > 0)
	{
		date.setTime(date.getTime()-skew);
	}
}

function makeYearExpDate(yr) 
{ 
  var expire = new Date ();
  expire.setTime (expire.getTime() + ((yr *365) *24 * 60 * 60 * 1000)); 
  expire = expire.toGMTString() ;
  return expire; 
} 

function fixSep(what) 
{ 
        n=0 ;
        while ( n >= 0 ) { 
                n = what.indexOf(';',n) ;
                if (n < 0) return what ;
                else { 
                        what = what.substring(0,n) + escape(';') + what.substring(n+1,what.length) ;
                        n++ ;
                } 
        } 
        return what ;
} 

function y2k(number) 
 { 
  return (number < 1000) ? number + 1900 : number; 
 } 

function getTime() 
{ 

  var LocalNow = new Date(); 

  var AbsoluteNow = new Date(); 

  AbsoluteNow.setTime(LocalNow.getTime() + LocalNow.getTimezoneOffset()*60); 

  //alert(LocalNow.getTimezoneOffset()); 

  var PekingDate = new Date(); 

  PekingDate.setTime(AbsoluteNow.getTime()+8*3600); 

  var thisYear = PekingDate.getYear(); 

  var thisMonth = PekingDate.getMonth() + 1; 

  var thisDay = PekingDate.getDate(); 

  var str = y2k(thisYear)+'��'; 

  if (thisMonth < 10) 

   str += '0'; 

  str += thisMonth + '��'; 

  if (thisDay < 10) 

   str += '0'; 

  str += thisDay + '��'; 

  return str; 

 } 

	function getFormObjectValue(form,object)
	{
    var len = form.elements.length;
		for(var j=0;j < len;j++ )
		{
    	var who = form.elements[j];
    	var type = who.type;
  		var name = who.name;
  		if(name == object)
  		{
  			if(type == "checkbox" || type == "radio")
  			{
  				if(who.checked)
  				{
  					return 'yes';
  				}
  				else
  				{
  					return 'no';
  				}
  			}
  			else
  			{
  				return who.value;
  			}
  		}
		}			  	
		return null;
	}
