function PasswordFactory($http, PrincipalInfofactory) {
	var PasswordFactory = {};

	var url = "timesheetLogin/login/";

	PasswordFactory.update = function(resourceData){
		return $http.post(url+'changePassword',resourceData).then(
			function(response){
				return response.data;
			}
		);
	}

	return PasswordFactory;
}
angular.module('timeSheet').factory('PasswordFactory', PasswordFactory);
