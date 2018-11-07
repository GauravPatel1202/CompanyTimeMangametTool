function AlertConfiguration($http, PrincipalInfofactory) {
	var AlertConfiguration = {};

	var url = "timesheetLogin/login/";

	AlertConfiguration.update = function(configData){
		return $http.post(url+'update',configData).then(
			function(response){
				return response.data;
			}
		);
	}

	AlertConfiguration.get = function(){
		return $http.get(url+'get').then(
			function(response){
				return response.data;
			}
		);
	}

	return AlertConfiguration;
}
angular.module('timeSheet').factory('AlertConfiguration', AlertConfiguration);
