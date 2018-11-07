function ResourcemapFactory($http,PrincipalInfofactory){
	var ResourcemapFactory = {}
	
	ResourcemapFactory.getPageData = function() {
	 var userId  = PrincipalInfofactory.getId() ; 
	  return $http.get('user/getProjectAndResource/'+userId).then(
		 function(response) {
		   return response.data;
		  });
	 };
	 
	 ResourcemapFactory.getAllRoles = function() {
		 var userId  = PrincipalInfofactory.getId() ; 
		 return $http.get('role/getAllRoles').then(
			function(response) {
				return response.data;
			});
	 };
	 
	 ResourcemapFactory.mapResource = function(reqBody){
		 var userId  = PrincipalInfofactory.getId() ;
		 return $http.post('user/assignProjectAndResource/'+userId,reqBody).then(
			function(response) {
				return response.data;
		 });
	 }
	 
	 ResourcemapFactory.unmapResource = function(reqBody){
		 var userId  = PrincipalInfofactory.getId() ;
		 return $http.delete('user/deleteAssignedResource/'+userId,{
			 	data: reqBody,
			 	headers: {
			 		"Content-Type": "Application/json"
			 	}
			}).then(
			function(response) {
				return response.data;
		 });
	 }
	 
	 ResourcemapFactory.updateResourceMapping = function(reqBody){
		 var userId  = PrincipalInfofactory.getId() ;
		 return $http.put('user/updateAssignProjectAndResource/'+userId,reqBody).then(
			function(response) {
				return response.data;
		 });
	 }
	 
	 return ResourcemapFactory;
}
angular.module("timeSheet").factory("ResourcemapFactory",ResourcemapFactory);