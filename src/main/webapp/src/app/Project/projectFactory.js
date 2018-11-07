function ProjectsFactory($http, PrincipalInfofactory) {
	var ProjectsFactory = {};

	var url = "project/";
	ProjectsFactory.getAllProjects = function(userId){
		return $http.get(url+'getProjectsByPerson/'+userId).then(
	    function(response) {
	    	return response.data;
	    });
	}

	ProjectsFactory.create = function(projectData){
		return $http.post(url+'createProject',projectData).then(
			function(response){
				return response.data;
			}
		);
	}

	ProjectsFactory.update = function(projectData){
		return $http.put(url+'updateProject',projectData).then(
			function(response){
				return response.data;
			}
		);
	}
	
	ProjectsFactory.getApprovableProjects = function(){
		return $http.get(url+'getapprovableprojects/'+PrincipalInfofactory.getId()).then(
				function(response){
					return response.data;
				}
			);
	}

	return ProjectsFactory;
}
angular.module('timeSheet').factory('ProjectsFactory', ProjectsFactory);
