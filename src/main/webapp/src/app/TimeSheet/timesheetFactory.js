function TimeSheetFactory($http, PrincipalInfofactory) {
	var TimeSheetFactory = {};

	TimeSheetFactory.getTimeSheet = function (from, to, userId) {
		return $http.get('worklog/getTimesheetByPerson/' + PrincipalInfofactory.getId() + '/' + userId + '/' + from + '/' + to).then(
			function (response) {
				return response.data;
			});
	};

	TimeSheetFactory.getProjects = function (monthStartDate, personId) {
		return $http.get('project/getProjectsByPersonActiveWithTasks/' + personId + '/' + monthStartDate).then(
			function (response) {
				return response.data;
			});
	};

	TimeSheetFactory.getTasks = function (projId, monthStartDate, personId) {
		return $http.get('task/getTasksByProjectAndPerson/' + projId + '/' + personId + '/' + monthStartDate).then(
			function (response) {
				return response.data;
			});
	};

	TimeSheetFactory.submitDay = function (data) {
		return $http.post('worklog/saveTimeSheet/' + PrincipalInfofactory.getId(), data).then(
			function (response) {
				return response.data;
			});
	};

	TimeSheetFactory.submitAll = function (data) {
		return $http.post('worklog/saveTimeSheet/' + PrincipalInfofactory.getId(), data).then(
			function (response) {
				return response.data;
			});
	};

	TimeSheetFactory.approve = function (data) {
		return $http.post('worklog/timeSheetStatus', data).then(function (response) {
			return response;
		});
	}

	TimeSheetFactory.getReportingUsers = function () {
		return $http.get('user/getUsersByProjectAndRole/' + PrincipalInfofactory.getId()).then(
			function (response) {
				return response.data;
			});
	}

	TimeSheetFactory.deleteSavedEntries = function (idList) {
		return $http.post('worklog/deletetimesheetentries/',idList).then(
			function (response) {
				return response.data;
			});
	}
	return TimeSheetFactory;
}

angular.module('timeSheet').factory('TimeSheetFactory', TimeSheetFactory);