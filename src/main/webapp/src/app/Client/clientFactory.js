function ClientsFactory($http, PrincipalInfofactory) {
	var ClientsFactory = {};
	
	ClientsFactory.getAllClients = function(){
		return $http.get('client/clients').then(
		//return $http.get('assets/JSONs/clients.json').then(
			    function(response) {
			    	return response.data;
			    });
	}
	
	ClientsFactory.getCountryList = function(){
		return $http.get('client/countries').then(
			function(response){
				return response.data;
			}
		);
	}
	
	ClientsFactory.addNewClient = function(clientData){
		return $http.post('client/',clientData).then(
			function(response){
				return response.data;
			}
		);
	}
	
	ClientsFactory.updateClient = function(clientData){
		var userId  = PrincipalInfofactory.getId() ; 
		return $http.put('client/?personId='+userId,clientData).then(
			function(response){
				return response.data;
			}
		);
	}
	
	return ClientsFactory;
}
angular.module('timeSheet').factory('ClientsFactory', ClientsFactory);