function TasksetupFactory($http, PrincipalInfofactory) {
	var TasksetupFactory = {};

	TasksetupFactory.getAllTasks = function () {
		return $http.get('task/getTasksByPerson/' + PrincipalInfofactory.getId()).then(
			//return $http.get("assets/JSONs/tasks1.json").then(
			function (response) {
				return response.data;
			});
	}

	TasksetupFactory.getAllProjects = function () {
		var userId = PrincipalInfofactory.getId();
		return $http.get('task/getProjectByPersonAndRole/' + userId).then(
			function (response) {
				return response.data;
			});
	};

	TasksetupFactory.getTaskByProject = function (projectId) {
		return $http.get('task/getTasksByProject/' + projectId).then(
			function (response) {
				return response.data;
			});
	};



	TasksetupFactory.getUsersByProject = function (projectId) {
		return $http.get('user/getUsersByProject/' + projectId).then(
			function (response) {
				return response.data;
			});
	};


	TasksetupFactory.deleteTask = function (taskId) {
		return $http({
			method: 'DELETE',
			url: 'task/deleteTask/' + taskId,
		}).then(function (response) {
			return response.data;
		});
	};



	TasksetupFactory.saveTask = function (data) {
		return $http({
			method: 'POST',
			url: 'task/taskSetup',
			data: data
		}).then(function (response) {
			return response.data;
		});
	};


	TasksetupFactory.getTaskByTaskId = function (taskId) {
		return $http.get('task/getTaskSetup/' + taskId).then(
			function (response) {
				return response.data;
			});
	};

	TasksetupFactory.deleteTask = function (taskId) {
		return $http.delete('task/deleteTask/' + taskId).then(
			function (response) {
				return response.data;
			});
	};

	TasksetupFactory.updateTask = function (data) {
		return $http({
			method: 'PUT',
			url: 'task/updateTask',
			data: data
		}).then(function (response) {
			return response.data;
		});
	};

	TasksetupFactory.getRoleInProject = function (projectId, personId) {
		return $http.get('project/getRoleInProject/'+projectId+'/'+personId).then(function (response) {
			return response.data;
		});
	}

	return TasksetupFactory;
}

angular.module('timeSheet').factory('TasksetupFactory', TasksetupFactory);