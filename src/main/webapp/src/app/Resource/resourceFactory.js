function ResourceFactory($http, PrincipalInfofactory) {
	var ResourceFactory = {};

	var url = "user/";
	ResourceFactory.all = function(){
		return $http.get(url+'resources').then(
	    function(response) {
	    	return response.data;
	    });
	}

	ResourceFactory.create = function(resourceData){
		return $http.post(url+'createUser',resourceData).then(
			function(response){
				return response.data;
			}
		);
	}

	ResourceFactory.update = function(resourceData){
		return $http.put(url+'updateUser',resourceData).then(
			function(response){
				return response.data;
			}
		);
	}
	
	ResourceFactory.getAllDesignations = function(){
		return $http.get(url+'getDesignationAndSkill').then(
			function(response){
				return response.data.designationList;
			}
		);
	}

	return ResourceFactory;
}
angular.module('timeSheet').factory('ResourceFactory', ResourceFactory);
