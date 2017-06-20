var contextPath="";
requirejs.config({
    // baseUrl: 'js/lib',
    paths: {
        jquery:'lib/jquery.min',
        easyui:'lib/jquery.easyui.min',
        easyuilangzhCN:'lib/easyui-lang-zh_CN',
        main:'main',
        mainnotsafe:'main-notsafe',
        jwt:'jwt/jwt-token',
        sjcl:'jwt-lib/sjcl/sjcl',
        timeutil:'lib/timeutil'
    },
    shim: {
    	'jquery':{
    	　　　　　　exports: '$'
    	　　　　},
    	 'main':{
  	　　　　　　exports: 'main',
  	      		deps: ['jquery']
  	　　　　},
  	     'mainnotsafe':{
 	　　　　　　exports: 'mainnotsafe',
 	      		deps: ['jquery']
 	　　　　},
    	'easyui':{
    	　　　　　　exports: 'easyui',
    	      	   deps: ['jquery']
    	　　　　},
    	'easyuilangzhCN':{
  	　　　　　　exports: 'easyui',
	      	   deps: ['jquery','easyui']
  	　　　　},
		'datagridscrollview':{
		　　　　exports: 'datagridscrollview',
		    	   deps: ['jquery','easyui']
		},
		'timeutil':{
		　　　　exports: 'timeutil'
		},
		 'jwt':{
			　exports: 'jwttoken'
		 },
		 'sjcl':{
			 exports: 'sjcltoken'
		 }
　　}
});


