
/*
Ext.define('Pds.model.Toc', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'urn', type: 'string' }
    ],
    proxy: {
        type: 'ajax',
        format :'xml',
        //limitParam : '',
        //cacheString : '',
        //noCache : false,
        //startParam : undefined,
        //pageParam : undefined,
        noCache : true,
        root : 'resource',
        buildUrl: function(x) { var url = 'http://localhost:9005/pds/cite/733500'; return url; },
        reader:{ type: 'xml',  
        	record : 'urn' }
    }
});

*/

/*
//best example
Ext.define('Pds.model.Toc', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'urn', type: 'string' }
    ],
    proxy: {
        type: 'ajax',
		url: tocUrl,
		reader: {
			type: 'xml',
			root: 'resource'
 	    }
    }
});

*/


/*Ext.define('Pds.model.Toc', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'document', type: 'string' }
    ],
    data: tocData,
    proxy: {
        type: 'memory',
	reader: {
		type: 'xml',
		root: 'resource'
 	},
    }
});*/