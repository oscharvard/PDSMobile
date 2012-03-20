//controllers


Ext.define('Pds.controller.Page', {
    	extend: 'Ext.app.Controller',
    	requires : ['Pds.store.*','Pds.model.*'],
    
	models: ['Pds.model.Toc'],// 'Pds.model.Page','Pds.model.SearchResult','Pds.model.PageCitation','Pds.model.ResourceCitation','Pds.model.RelatedItem'],
    	stores: ['Pds.store.Tocs'],// 'Pds.store.Pages','Pds.store.SearchResults','Pds.store.PageCitations','Pds.store.ResourceCitations','Pds.store.RelatedItems'],

    	init: function() {
        	console.log('Initialized Users! This happens before the Application launch function is called');
        	Ext.Msg.alert('controller init');
    	}
});