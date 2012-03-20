


//urls for web APIs
tocUrl = 'toc.xml';
searchUrl = 'search.xml';
citeUrl = 'cite.xml';
pageUrl = 'get.xml';
relatedUrl = 'related.xml';

Ext.Loader.setConfig({ enabled: true, disableCaching: true });


/*
//models
Ext.define('Pds.Page', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'sequence', type: 'int' },
        { name: 'fulltitle', type: 'string' },
        { name: 'displaylabel', type: 'string' },
        { name: 'orderlabel', type: 'string' },
        { name: 'text', type: 'string' },
        { name: 'image', type: 'string' },
        { name: 'thumb', type: 'string' },
        { name: 'prevlink', type: 'string' },
        { name: 'nextlink', type: 'string' }
    ],
    proxy: {
        type: 'rest',
        url :  pageUrl,
        reader: 'xml'
    }
});

Ext.define('Pds.Toc', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'identifier', type: 'int' },
        { name: 'value', type: 'string' },
        { name: 'description', type: 'string' },
    ],
    proxy: {
        type: 'rest',
        url : tocUrl,
        reader: 'xml'
    }
});

Ext.define('Pds.RelatedItem', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'identifier', type: 'int' },
        { name: 'value', type: 'string' },
        { name: 'description', type: 'string' },
    ],
    proxy: {
        type: 'rest',
        url : relatedUrl,
        reader: 'xml'
    }
});

Ext.define('Pds.SearchResult', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'deliveryUri', type: 'string' },
        { name: 'displayLabel', type: 'string' },
        { name: 'relevance', type: 'int' },
        { name: 'accessFlag', type: 'string' },
        { name: 'context', type: 'string' }
    ],
    proxy: {
        type: 'rest',
        url :  searchUrl,
        reader: 'xml',
        root: 'page/results/result'
    }
});

Ext.define('Pds.ResourceCitation', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'urn', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'repository', type: 'string' },
        { name: 'institution', type: 'string' },
        { name: 'accessed', type: 'string' },
        { name: 'id', type: 'string' }
    ],
    proxy: {
        type: 'rest',
        url : citeUrl,
        reader: 'xml',
        root: 'citation/resource'
    }
});

Ext.define('Pds.PageCitation', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'urn', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'repository', type: 'string' },
        { name: 'institution', type: 'string' },
        { name: 'accessed', type: 'string' },
        { name: 'id', type: 'string' }
    ],
    proxy: {
        type: 'rest',
        url : citeUrl,
        reader: 'xml',
        root: 'citation/page'
    }
});

//stores
Ext.create('Ext.data.Store', {
    model: 'Pds.Page',
    autoLoad: true
});

Ext.create('Ext.data.Store', {
    model: 'Pds.PageCitation',
    autoLoad: true
});

Ext.create('Ext.data.Store', {
    model: 'Pds.ResourceCitation',
    autoLoad: true
});


Ext.create('Ext.data.Store', {
    model: 'Pds.Toc',
    autoLoad: true
});

Ext.create('Ext.data.Store', {
    model: 'Pds.SearchResult',
    autoLoad: true
});

Ext.create('Ext.data.Store', {
    model: 'Pds.RelatedItem',
    autoLoad: true
});



//controllers
Ext.define('Pds.controller.Page', {
    	extend: 'Ext.app.Controller',
    	init: function() {
        	console.log('Initialized Users! This happens before the Application launch function is called');
        	Ext.Msg.alert('controller init');
    	}
});
*/

Ext.application(
{



    name: 'Pds',
	//models: ['Pds.model.Toc', 'Pds.model.Page','Pds.model.SearchResult','Pds.model.PageCitation','Pds.model.ResourceCitation','Pds.model.RelatedItem'],    
    //stores: ['Pds.store.Tocs', 'Pds.store.Pages','Pds.store.SearchResults','Pds.store.PageCitations','Pds.store.ResourceCitations','Pds.store.RelatedItems'],
	controllers: ['Page'],



    //set up the windowing system
    launch: function() {
    
    
       var qsHash = Ext.Object.fromQueryString("id=734515");
       var drsId = qsHash["id"];
       //Ext.Msg.alert('id', 'The pds id is: ' + drsId, Ext.emptyFn);

    	
    	<!--
    	Ext.create("Ext.TabPanel", {
            fullscreen: true,
            tabBarPosition: 'bottom',

            items: [
                    {
                    	xtype : 'toolbar',
                    	docked: 'top',
                    	title: ' محمد بن احمد بن محمد،. عمدة الطلاب في معرفة علم الحساب :.'
                    },
                {
                    title: 'Home',
                    iconCls: 'home',
                    scrollable: true,
                    html: [
                        '<img src="http://idstest.lib.harvard.edu:9001/ids/view/734355?s=.25&rotation=0&width=1200&height=1200&x=-1&y=-1&xcap=mx%2BH1zMK5j7hx82zCIFrFnVueAoTe4xt4BAJZkh2JsRrmuZGzw4qcS3z8O2oLnwSM1WQncbkuWQqsawR5vpwuuWACi3uZeBOmH%2F%2B%2BiES7xCjTOIzBgZ%2FrPobMbC8g5m2hi03JHqvpvxl2mJaptGOsNZsH3QKhCff%2FYvFUKchWnbYvqH%2B1jzwJtdM2qFEfjOPyVKJl%2BZjW%2BgXG66u12KC7OyH141ydL2DPqlxFSU8dJkIhfUsjDlzhfRDWNJ9RKlztei5uXdZxPcCLXMBy%2FS58A%3D%3D%22%3E" />'
                    ].join("")
                }
            ]
        });
    	-->
    	
    	  	
    	
    	
        
    }
});
