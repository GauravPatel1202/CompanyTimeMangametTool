function LoginFactory($http, PrincipalInfofactory) {
	var LoginFactory = {};

	/*LoginFactory.authenticateUser = function(username,password) {
		return $http.get('login/login/'+username+'/'+password).then(function(response) {
		});
	};

	LoginFactory.getUsers = function(){
		return $http.get('user/getUsers').then(function(response){
			return response;
		});
	}*/

	LoginFactory.loginUser = function(username,password) {
		return $http.post("timesheetLogin/login",{
			"password": password,
			"empId": username
		}).then(
			function(response){
				return response.data;
			}
		);
	}

	LoginFactory.resetPassword = function(userId){
		return $http.get('timesheetLogin/login/forgetPassword?empId='+userId).then(
			function(response) {
				return response.data;
			});
	}

	return LoginFactory;
}
angular.module('timeSheet').factory('loginFactory', LoginFactory);
