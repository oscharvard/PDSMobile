/**********************************************************************
 * Page Delivery Service (PDS-WS) mobile web view
 * Copyright 2011 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Contact information
 *
 * Office for Information Systems
 * Harvard University Library
 * Harvard University
 * Cambridge, MA  02138
 * (617)495-3724
 * hulois@hulmail.harvard.edu
 **********************************************************************/

//override for extjs xml lameness
Ext.override( Ext.data.XmlReader, {
    createAccessor: function() {
        var selectValue = function(key, root, defaultValue){
			if( key == '#' ){
				return root.tagName;
			}
			if( key.indexOf( '@' ) != -1 ){
				var property = key.split( '@' )[ 1 ];
				key = key.split( '@' )[ 0 ];
			}
			var val;
			if( key.length ){
				var node = Ext.DomQuery.selectNode(key, root);
				if( node && node.firstChild ){
					node = node.firstChild;
				}
			}
			else{
				var node = root;
			}
            if(node){
				if( typeof( node.getAttribute ) != 'undefined' && typeof( property ) != 'undefined' ){
					val = node.getAttribute( property );
				}
				else{
					val = node.nodeValue;
				}
            }
            return Ext.isEmpty(val) ? defaultValue : val;
        };

        return function(key) {
            var fn;

            if (key == this.totalProperty) {
                fn = function(root, defaultValue) {
                    var value = selectValue(key, root, defaultValue);
                    return parseFloat(value);
                };
            }

            else if (key == this.successProperty) {
                fn = function(root, defaultValue) {
                    var value = selectValue(key, root, true);
                    return (value !== false && value !== 'false');
                };
            }

            else {
                fn = function(root, defaultValue) {
                    return selectValue(key, root, defaultValue);
                };
            }

            return fn;
        };
    }(),
});


function getQuerystring(key, default_, url)
{
  if (default_==null) default_="";
  if (url==null) url=window.location.href;
  key = key.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regex = new RegExp("[\\?&]"+key+"=([^&#]*)");
  var qs = regex.exec(url);
  if(qs == null)
    return default_;
  else
    return qs[1];
}

function getItemTpl(seqNum)
{
	if ( seqNum == null) seqNum = 1;
	//find thumbnail of matching hitlist item
	var pi = pageStore.getAt(seqNum-1);
	var Tpl = '<img src=\"' + pi.get('thumb') + '\"> <br>{displayLabel}<br><p><i>{context}</i>';
	return Tpl;
}

Ext.Ajax.disableCaching = false;

var queryString = location.search;
var sequenceNumber = getQuerystring('n');
var drsId = getQuerystring('id');
if (drsId == null)
{
	drsId = '733500';
}
if ( (sequenceNumber == null) || (sequenceNumber == "") )
{
        sequenceNumber = '1';
}


//urls for web APIs
var pdsHost = 'http://pdstest.lib.harvard.edu:9005/pds/';
var tocUrl =  pdsHost + 'toc/' + drsId;
var searchUrl = pdsHost + 'find/' + drsId + '?F=X&J=100&Q='; //+ drsId;
var citeUrl = pdsHost + 'cite/' + drsId;
var pageUrl = pdsHost + 'get/' + drsId;
var relatedUrl = pdsHost + 'related/' + drsId;
var linksUrl = pdsHost + 'related/' + drsId;


Ext.setup({

	 icon: 'images/harvard_shield.jpg',
	 glossOnIcon: true,
	 
	 
	 onReady: function() 
   {
   
    /* gui setup */
    	//top nav bar
	 var topNavBar = new Ext.Toolbar({
	    defaults: {
                    iconMask: true,
                    ui: 'plain'
                },
            dock: 'top',
            items:[ {xtype: 'spacer'}, {iconCls: 'action', 
		handler: function(btn, event) 
			 {
			   Optionsoverlay.setCentered(false);
            		   Optionsoverlay.showBy(btn);
			 }


	    }]
        });
	 
	 //set up the pop ups
	 var TOCoverlayTb = new Ext.Toolbar({
            dock: 'top'
        });
        
        var TOCoverlay = new Ext.Panel({
            floating: true,
            flex:1,
            modal: true,
            layout: 'fit',
            centered: false,
            width: Ext.is.Phone ? 260 : 260,
            height: Ext.is.Phone ? 220 : 500,
            styleHtmlContent: true,
            dockedItems: TOCoverlayTb,
            scroll: 'none',
            //contentEl: 'lipsum',
            //cls: 'htmlcontent'
        });
        
		

        var showTOCOverlay = function(btn, event) {
            TOCoverlay.setCentered(false);
            TOCoverlayTb.setTitle(btn.getText());
 	    TOCoverlay.showBy(btn);
        };

	
	var Optionsoverlay = new Ext.Panel({
            floating: true,
            flex:1,
	    //defaults: { flex: 1 },
	    pack: 'center',
	    //cls: 'card1',
            modal: false,
            layout: 'card',
	    align: 'stretch',
            centered: false,
            width: 166, //Ext.is.Phone ? 210 : 210,
            height: 208, //Ext.is.Phone ? 180 : 460,
            styleHtmlContent: true,
            scroll: false,
	    //html: 'options menu goes here',
	    items:[ 

		  {     //buttons
		  	items: [	
					{text: 'About', xtype: 'button', handler: function(btn, event) {showCenteredOverlay(btn,event);Optionsoverlay.hide();} }, 
					{xtype: 'panel', height: 10 },
					{text: 'Share', xtype: 'button', handler: function(btn, event) {showCenteredOverlay(btn,event);Optionsoverlay.hide();} },
					{xtype: 'panel', height: 10 },
					{text: 'Copyright', xtype: 'button', handler: function(btn, event) {showCenteredOverlay(btn,event);Optionsoverlay.hide();}  },
					{xtype: 'panel', height: 10 },
                                        {text: 'Contact Us', xtype: 'button', handler: function(btn, event) {showCenteredOverlay(btn,event);Optionsoverlay.hide();} },
					{xtype: 'panel', height: 10 },
					{text: 'Help', xtype: 'button', handler: function(btn, event) {showCenteredOverlay(btn,event);Optionsoverlay.hide();} }	
			]
			
		  }
   	    ]
        }); 

           

        var showOptionsOverlay = function(btn, event) {
            Optionsoverlay.setCentered(false);
            //OptionsoverlayTb.setTitle(btn.getText());
            //tContents.show();
            Optionsoverlay.showBy(btn);
        };
	 
	 
	var searchOverlayTb = new Ext.Toolbar({
            dock: 'top',
            layout: 'fit',
            items:[ {xtype: 'searchfield', itemId: 'searchbox', width: Ext.is.Phone ? 240 : 700,
                    listeners:{
                            keyup: function(sBox, e){
                                //alert('query is: ' + sBox.getValue());
                                if (e.browserEvent.keyCode == 13) {
                                    //alert('enter key pressed- query sent!\n' +
                                    //    'query is: ' + sBox.getValue());
                                   // e.preventDefault();
                                    //sBox.on('beforequery', function(q){q.cancel=true;},this);
                                    //search FTS
                                    searchResultStore.proxy.url = searchUrl + encodeURIComponent(sBox.getValue());
                                    console.log("fts query: " + searchResultStore.proxy.url);
                                    searchResultStore.load();
				    searchResultStore.sync();
                                    //searchResultStore.read();
                                    hitlist.show();
                                    //searchOverlayTbBottom.show();
                                    //searchOverlay.doLayout();
                                }

                            }
                        }
                    }
            ]
        });

        /*var searchOverlayTbBottom = new Ext.Toolbar({
            dock: 'top', layout: 'hbox', align: 'stretch' });*/

        
        var searchOverlay = new Ext.Panel({
            floating: true,
            flex:1,
            modal: true,
            centered: true,
            width: Ext.is.Phone ? 260 : 750,
            height: Ext.is.Phone ? 220 : 500,
            styleHtmlContent: true,
            dockedItems: [searchOverlayTb],
            scroll: 'vertical',
            contentEl: 'lipsum',
            layout: 'fit',
            cls: 'htmlcontent'
        });
	 
	 var showSearchOverlay = function(btn, event) {
            searchOverlay.setCentered(true);
            //searchOverlayTb.setTitle(btn.getText());
            //tContents.show();
            var sField = searchOverlayTb.getComponent('searchbox');
            sField.reset();
            hitlist.hide();
            //searchOverlayTbBottom.hide();
	    //searchOverlay.doComponentLayout();
            searchOverlay.show();
        };
	 
	 
	 
	 
	 var overlayTb = new Ext.Toolbar({
            dock: 'top'
        });
        
        var overlay = new Ext.Panel({
            floating: true,
            flex:1,
            modal: true,
            centered: false,
            width: Ext.is.Phone ? 260 : 450,
            height: Ext.is.Phone ? 220 : 500,
            styleHtmlContent: true,
            dockedItems: overlayTb,
            scroll: 'vertical',
            contentEl: 'lipsum',
            cls: 'htmlcontent'
        });

        var showOverlay = function(btn, event) {
            overlay.setCentered(false);
            overlayTb.setTitle(btn.getText());
           
            overlay.showBy(btn);
        };
        
        var showCenteredOverlay = function(btn, event) {
            overlay.setCentered(true);
            overlayTb.setTitle(btn.getText());
            if ( btn.getText() == 'OCR' )
            {
            	var ocrTxt = '';
            	var cMain = panel.getComponent('carouselMain');
            	var pgIndex = cMain.getActiveIndex() + 1;
                //pageInfoStore.proxy.url = pageUrl + "?n=" + pgIndex;
                //pageInfoStore.load();
            	var pgInfo = pageInfoStore.getAt(0);
            	ocrTxt = pgInfo.get('text');
                if ((ocrTxt == '') || (ocrTxt==null))
                    ocrTxt = 'No OCR text available';
            	overlay.update(ocrTxt);
		overlay.doLayout();
            }
            else if ( btn.getText() == 'Citation' )
            {
				var resRec = resourceCitationStore.getAt(0);
				var pagRec = pageCitationStore.getAt(0);
				var citationTxt = '<b>Description</b>: ' + resRec.get('description') + '<br>' +
                                    '<b>Page</b>: ' + pagRec.get('pagelabel') + '<br>' +
                                    '<b>URN</b>: <a href=\"'+ resRec.get('urn') +'\">' + resRec.get('urn') + '</a><br>' +
                                    '<b>Repository</b>: ' + resRec.get('repository') + '<br>' + 
                                    '<b>Institution</b>: ' + resRec.get('institution') + '<br>' + 
                                    '<b>Accessed</b>: ' + resRec.get('accessed') + '<br>';
				overlay.update(citationTxt);
				overlay.doLayout();
            }
	    else if ( btn.getText() == 'Copyright' )
            {
                var copyrightTxt = 'This material is owned, held, or licensed by the President and Fellows of Harvard College. It is being provided solely for the purpose of teaching or individual research. Any other use, including commercial reuse, mounting on other systems, or other forms of redistribution requires permission of the appropriate office of Harvard University.';
                overlay.update(copyrightTxt);
                overlay.doLayout();
            }
	    else if ( btn.getText() == 'Share' )
            {
                var shareTxt = 'Social Media Functions go here';
                overlay.update(shareTxt);
                overlay.doLayout();
            }
	    else if ( btn.getText() == 'About' ) 
            {   
                var aboutTxt = 'Page Delivery Service (PDS) Mobile was developed by Chip Goines & John Overholt as part of a May 2011 Harvard Library Lab grant.'; 
                overlay.update(aboutTxt);
                overlay.doLayout();
            }  
            else if ( btn.getText() == 'Help' ) 
            {   
                var helpTxt = 'Help text goes here'; 
                overlay.update(helpTxt);
                overlay.doLayout();
            }  
	    else if ( btn.getText() == 'Contact Us' ) 
            {   
                var contactTxt = '<center><a href="mailto:chip_goines@harvard.edu?subject=PDS mobile help&body=email text here">Contact the PDS staff</a></center>';
                overlay.update(contactTxt);
                overlay.doLayout();
            }
            else //related links
            {
                var relTxt = '';
                
                relatedStore.data.each(function(item, index, totalItems ) {
        				relTxt = relTxt + item.get('type')  + ' <a href=\"' +   item.get('value') + '\">' + item.get('identifier') + '</a><p/>';
   				 });

            	overlay.update(relTxt);
		overlay.doLayout();
            }
            overlay.show();
        };
        
        
       /* if (Ext.is.Phone) {
            var dockedItems = [{
                dock: 'top',
                xtype: 'toolbar',
                items: [{
                    text: 'showBy',
                    handler: showOverlay                    
                }]
            }, {
                dock: 'bottom',
                xtype: 'toolbar',
                items: [{
                    text: 'show (centered)',
                    handler: showCenteredOverlay             
                }, {xtype: 'spacer'}, {
                    text: 'showBy',
                    handler: showOverlay
                }]
            }];
        }
        else { */
            var dockedItems = [ topNavBar,
            	{dock: 'bottom',
                xtype: 'toolbar',
                items: [
                {text: 'Table of Contents',
                    handler: showTOCOverlay}, 
                    
                  {xtype: 'spacer'},
                  {text: 'Search', handler: showSearchOverlay},
                  
                  {xtype: 'spacer'}, 
                  {text: 'Citation',
                    handler: showCenteredOverlay},
                  {xtype: 'spacer'}, 
                  {text: 'OCR',
                    handler: showCenteredOverlay},
		  {xtype: 'spacer'}, 
		  /*{text: 'Copyright',
                    handler: showCenteredOverlay},
		  {xtype: 'spacer'}, 
		  {text: 'Share',
                    handler: showCenteredOverlay},
                  {xtype: 'spacer'},*/
                  {text: 'Related Links', handler: showCenteredOverlay}
                ]
            }];
            
   		var panel = new Ext.Panel({

		//cls: 'card',
                //flex:1,
                centered: true,
                //scroll: true,



            fullscreen: true,
            layout: {
        		type: 'vbox',
        		align: 'stretch'
            	},
            dockedItems: dockedItems
 
        });
	
   
   
   
   
   /*sencha v 1.1 readers */

Ext.regModel('Toc', {

	fields: [ {name: 'title', mapping: '@title'}, {name: 'pages', mapping: '@pages'}]

 });


Ext.regModel('Page', {
	
		fields: [ 
				{name: 'label', mapping: '@label'} , 
				{name: 'pagelabel', mapping: '@pagelabel'},
				{name: 'sequence', mapping: '@sequence' },
				{name: 'pagenum', mapping: '@pagenum'},
				{name: 'thumb', mapping: '@thumb'},
				{name: 'image', mapping: '@image'},
				{name: 'link', mapping: '@link'}
			 ]
	});
	
	
Ext.regModel('Section', {
	
		fields: [
				{name: 'label', mapping: '@label'},
				{name: 'pagestart',  mapping: '@pagestart'},
				{name: 'pageend', mapping: '@pageend'},
				{name: 'seqstart', mapping: '@seqstart'},
				{name: 'seqend', mapping: '@seqend'},
				{name: 'labelrange', mapping: '@labelrange'},
				{name: 'seqrange', mapping: '@seqrange'},
				{name: 'link', mapping: '@link'}
			]
	});



Ext.regModel('SearchResult', {
    
    fields: [
        { name: 'deliveryUri', type: 'string' },
        { name: 'displayLabel', type: 'string' },
        { name: 'score', type: 'string' },
        { name: 'securityFlag', type: 'string' },
        { name: 'context', type: 'string' },
	{ name: 'thumb', type: 'string' , defaultValue: '' }
    ]
});

Ext.regModel('ResourceCitation', {
    
    fields: [
        { name: 'urn', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'repository', type: 'string' },
        { name: 'institution', type: 'string' },
        { name: 'accessed', type: 'string' },
        { name: 'id', type: 'string' }
    ]
});

Ext.regModel('RelatedItem', {
    
    fields: [
        { name: 'identifier', type: 'string' },
        { name: 'type', type: 'string'},
        { name: 'value', type: 'string' },
        { name: 'description', type: 'string' }
    ]
});


Ext.regModel('PageCitation', {
    
    fields: [
        { name: 'urn', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'repository', type: 'string' },
        { name: 'pagelabel', type: 'string' },
        { name: 'institution', type: 'string' },
        { name: 'accessed', type: 'string' },
        { name: 'id', type: 'string' }
    ]
});



Ext.regModel('PageInfo', {
	
		fields: [ 
				{name: 'fulltitle',type: 'string'} , 
				{name: 'displaylabel', type: 'string'},
				{name: 'sequence', type: 'string' },
				{name: 'lastpage', type: 'string'},
				{name: 'orderlabel', type: 'string'},
				{name: 'thumb', type: 'string'},
				{name: 'image', type: 'string'},
				{name: 'small', type: 'string'},
				{name: 'medium', type: 'string'},
				{name: 'large', type: 'string'},
				{name: 'max', type: 'string'},
				{name: 'img_mimetype', type: 'string'},
				{name: 'text', type: 'string'},
				{name: 'nextlink', type: 'string'},
				{name: 'prevlink', type: 'string'}
			 ]
	});


/* sencha v 1.1 format stores */

var tocStore = new Ext.data.Store({
    model: 'Toc',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        //buildUrl: function(x) { var url = tocUrl ; return url; },
        url: tocUrl,
        //buildUrl: function(x) { var url = tocUrl + "/" + drsId; return url; },
		reader: {
			type: 'xml',
			record: 'document'
 		}
 	},
 	listeners: {
                single: true,
                datachanged: function(){ console.log('added toc')
                
                	tocStore.each(function(rec){
                        console.log('title: ' + rec.get('title') + ' pages: ' + rec.get('pages'));
                        topNavBar.setTitle('PDS: ' + rec.get('title') );
                        });
                	//console.log("items: " + this.getCount() );
                }
    }
});


var pageStore = new Ext.data.Store({
    model: 'Page',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        buildUrl: function(x) { var url = tocUrl ; return url; },
        //url: tocUrl,
        //buildUrl: function(x) { var url = tocUrl + "/" + drsId; return url; },
		reader: {
			type: 'xml',
			record: 'page',
			//root: 'section'
 		}
 	},
 	listeners: {
                single: true,
                datachanged: function(){ console.log('added page ')

                     Ext.getBody().mask('Loading...', 'x-mask-loading', false);
                	var items = [];
			var images = []; //for pinchCarousel
                	this.each(function(rec){
                        //console.log('label: ' + rec.get('label') + ' seq: ' + rec.get('sequence') + ' img:' + rec.get('image') )
                        // add images to carousel
                        //items.push({
                        //	title: rec.get('label'), scroll: 'vertical',
                        //    html: '<center><img src=\"' + rec.get('image') + '\"></center>'
                            //html: '<center><iframe src=\"' + rec.get('image') + '\" width=\"100%\" height=\"100%\" scrolling=\"yes\" align=middle /></center>'
                            //cls: 'card ' + rec.get('cls')
                        //});
			//add images to pinchCarousel, grab large res url to render 
			images.push(rec.get('image'));
			/* this fails about 50% of the time
			console.log("CALL TO PAGE INFO: " + pageUrl + "?n=" + rec.get('sequence') );
			pageInfoStore.proxy.url = pageUrl + "?n=" + rec.get('sequence');
                        pageInfoStore.load();
			var pgInfo = pageInfoStore.getAt(0);
                	var largeUrl = pgInfo.get('large');
			if ((largeUrl == '') || (largeUrl==null)) 
			{
				images.push( rec.get('image') );
				console.log("LOADING REGULAR URL " + rec.get('image') );
			}
			else
			{
				images.push( largeUrl );
				console.log("LOADING LARGE URL " +  largeUrl );
			}
			*/
                    });
                	console.log("pages added: " + this.getCount() );
			var carouselMain = new Jarvus.mobile.PinchCarousel({
                		defaults: {cls: 'card'},
            			flex:1,
                                centered: true,
            			scroll: true,
                                fullscreen: true,
                        /*  items: items, */  
			images: images,
                        itemId: 'carouselMain',
                        listeners:{
                            beforecardswitch: function (c,newcard, oldcard,index)
                            {
                                var pgIndex = index + 1;
                                resourceCitationStore.proxy.url = citeUrl + "?n=" + pgIndex;
                                resourceCitationStore.load();
                                pageCitationStore.proxy.url = citeUrl + "?n=" + pgIndex ;
                                pageCitationStore.load();
                                pageInfoStore.proxy.url = pageUrl + "?n=" + pgIndex ;
                                pageInfoStore.load();
                            }

                        }
                    });
                    console.log("adding carousel");
                    panel.add(carouselMain);
                    panel.doLayout();
 
                    var tContents = new Ext.List({
    						flex:1,
    						itemTpl : '{label}<br><img src=\"{thumb}\"> ',
    						grouped : false,
    						indexBar: false,
    						scroll: 'vertical',
                                                layout: 'fit',
    						store: pageStore,
    						listeners:{
            					itemtap: function(record, index){
                                                        //set the toc, cite and ocr popopvers to point to new page
            						carouselMain.setActiveItem(index);
                                                        var pgIndex = carouselMain.getActiveIndex() + 1;
                                                        resourceCitationStore.proxy.url = citeUrl + "?n=" + pgIndex;
                                                        resourceCitationStore.load();
                                                        pageCitationStore.proxy.url = citeUrl + "?n=" + pgIndex ;
                                                        pageCitationStore.load();
                                                        pageInfoStore.proxy.url = pageUrl + "?n=" + pgIndex ;
                                                        pageInfoStore.load();
            					}
        					}
						});
					TOCoverlay.add(tContents);
					//TOCoverlay.doLayout();

                      Ext.getBody().unmask();
                }
    }
});

var sectionStore = new Ext.data.Store({
    model: 'Section',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        buildUrl: function(x) { var url = tocUrl ; return url; },
        //url: tocUrl,
        //buildUrl: function(x) { var url = tocUrl + "/" + drsId; return url; },
		reader: {
			type: 'xml',
			record: 'section',
			//root: 'section'
 		}
 	},
 	listeners: {
                single: true,
                datachanged: function(){ console.log('added section ')
                
                	this.each(function(rec){
                        //console.log('label: ' + rec.get('label') + ' link:' + rec.get('link') )
                        });
                	console.log("sections found: " + this.getCount() );
                }
    }
});



var searchResultStore = new Ext.data.Store({
    model: 'SearchResult',
    autoLoad: false,
    autoSave: true,
    totalRecords: '@matches',
    proxy: {
        type: 'ajax',
        limitParam : '',
        //buildUrl: function(x) { var url = searchUrl; return url; },
        //buildUrl: function(x) { var url = buildUrl + "/" + drsId + "?n=" + sequenceNumber; return url; },
        reader: { type: 'xml',
            //root: 'page/results',
            record: 'result'
        }
      },
      listeners: {
		load: function() {
			console.log("loading new search hitlist thumbnails");
			this.each(function(rec){
				//plug in thumb url from pageStore
                                var seq  = getQuerystring('n', null, rec.get('deliveryUri'));
                                if (seq == null) seq = 1;
                                var pi = pageStore.getAt(seq - 1 );
				rec.set('thumb', pi.get('thumb') );
                                //rec.dirty = true;
				console.log('search thumb: ' + rec.get('thumb') );
                                //this.sync();
			});
		},
                single: false,
                datachanged: function(){
               
                	/*this.each(function(rec){
                        	console.log('label: ' + rec.get('label') + ' link:' + rec.get('link') + ' thumb: ' + rec.get('thumb') );
			    }
                        );*/
                	console.log("search hits found: " + this.getCount() );
                        //searchOverlayTbBottom.setTitle(this.getCount() + " hits found");
                }
      }
    
}); 

var resourceCitationStore = new Ext.data.Store({
    model: 'ResourceCitation',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        url: citeUrl,
        //buildUrl: function(x) { var url = citeUrl; return url; },
         //buildUrl: function(x) { var url = citeUrl + "/" + drsId + "?n=" + sequenceNumber; return url; },
        reader : { type: 'xml',
        	root: 'citation',
        	record: 'resource'
       }
    },
    listeners: {
                single: true,
                datachanged: function(){ console.log('added resource citation ')
                
                	this.each(function(rec){
                        //console.log('label: ' + rec.get('label') + ' link:' + rec.get('link') )
                        });
                	console.log("res citations found: " + this.getCount() );
                }
    }
});

var relatedStore = new Ext.data.Store({
    model: 'RelatedItem',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        buildUrl: function(x) { var url = relatedUrl; return url; },
        //buildUrl: function(x) { var url = relatedUrl + "/" + drsId + "?n=" + sequenceNumber; return url; },
        reader: { type:'xml',
         	record: 'link'}
    },
    listeners: {
                single: true,
                datachanged: function(){ console.log('added link ')
                
                	this.each(function(rec){
                        //console.log('label: ' + rec.get('label') + ' link:' + rec.get('link') )
                        });
                	console.log("links found: " + this.getCount() );
                }
    }
});


var pageCitationStore = new Ext.data.Store({
    model: 'PageCitation',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        url: citeUrl + "?n=" + sequenceNumber,
        //buildUrl: function(x) { var url = citeUrl + "/" + drsId + "?n=" + sequenceNumber; return url; },
        //buildUrl: function(x) { var url = citeUrl; return url; },
        reader : { type:'xml',
        	  root: 'citation',
              record: 'page'
         }
    },
    listeners: {
                single: true,
                datachanged: function(){ console.log('added page citation ')
                
                	this.each(function(rec){
                        //console.log('label: ' + rec.get('label') + ' link:' + rec.get('link') )
                        });
                	console.log("page citations found: " + this.getCount() );
                }
    }
});


var pageInfoStore = new Ext.data.Store({
    model: 'PageInfo',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        limitParam : '',
        url: pageUrl + "?n=" + sequenceNumber,
        //buildUrl: function(x) { var url = pageUrl ; return url; },
        //url: tocUrl,
        //buildUrl: function(x) { var url = pageUrl + "/" + drsId; return url; },
		reader: {
			type: 'xml',
			record: 'page',
			//root: 'section'
 		}
 	},
 	listeners: {
                single: true,
                datachanged: function(){ console.log('added page info ')
                
                	this.each(function(rec){
                        //console.log('label: ' + rec.get('label') + ' link:' + rec.get('link') )
                        });
                	console.log("page info found: " + this.getCount() );
                }
    }
});



    //search result list
    var hitlist = new Ext.List({
        flex:1,
        itemTpl :  '<img src=\"{thumb}\"/ align=\"left\" hspace=\"4\" >{displayLabel}<br><p><i>{context}</i>',
	//itemTpl :  '{displayLabel}<br><p><i>{context}</i>',
        //itemTpl : '<img src=\"{thumb}\"/> <i>{label}</i>',
    	grouped : false,
    	indexBar: false,
    	scroll: 'vertical',
        layout: 'fit',
    	store: searchResultStore,
	emptyText: "<center><b>Sorry, but no results matched your query.</b></center>",
	loadingText: "Searching...",
	
        //store: pageStore,
    	listeners:{
            itemtap: function(record, index){
                //carouselMain.setActiveItem(index);
                //alert('going to page ' + index);
                var hit = searchResultStore.getAt(index);
                var newSequenceNumber = getQuerystring('n',
                    null, hit.get('deliveryUri') );
                if (newSequenceNumber == null) newSequenceNumber = 1;
                //set the toc, cite and ocr popopvers to point to new page
                var cMain = panel.getComponent('carouselMain');
                console.log('jumping to ' + newSequenceNumber + " : " + hit.get('deliveryUri') );
                cMain.setActiveItem(newSequenceNumber - 1);
                resourceCitationStore.proxy.url = citeUrl + "?n=" + newSequenceNumber;
                resourceCitationStore.load();
                pageCitationStore.proxy.url = citeUrl + "?n=" + newSequenceNumber ;
                pageCitationStore.load();
                pageInfoStore.proxy.url = pageUrl + "?n=" + newSequenceNumber ;
                pageInfoStore.load();
                hitlist.hide();
                //searchOverlayTbBottom.hide();
                searchOverlay.hide();

                //if this was a global search, jump to page
                //location.url = record('deliveryUri');
            	}
        }
    });
    hitlist.hide();
    //searchOverlayTbBottom.hide();
    searchOverlay.add(hitlist);
    //searchOverlay.doComponentLayout();
	 
       //}
         
   }//onReady

});//end
