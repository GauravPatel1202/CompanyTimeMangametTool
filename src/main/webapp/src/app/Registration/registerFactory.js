function RegisterFactory($http, PrincipalInfofactory) {
	var RegisterFactory = {};
	
    RegisterFactory.saveNewUser = function(data) {
		return $http.post('user/createUser', data).then(
				function(response) {
					return response.data;
				});
	}

    
	return RegisterFactory;
}
angular.module('timeSheet').factory('RegisterFactory', RegisterFactory);