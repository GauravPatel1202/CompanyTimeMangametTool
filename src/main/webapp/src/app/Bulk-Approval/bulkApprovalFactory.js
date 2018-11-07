function blukApprovalfactory($http, PrincipalInfofactory) {
	var blukApprovalfactory = {};

	blukApprovalfactory.fetchTimeSheetEntries = function (startDate, endDate) {
		var userId = PrincipalInfofactory.getId();
		return $http.get("worklog/getTimesheetForBulkApproval/" + userId + "/" + startDate + "/" + endDate).then(
			function (response) {
				return response.data;
			}
		);
	}
	blukApprovalfactory.getReportingUsers = function () {
		return $http.get('user/getUsersByProjectAndRole/' + PrincipalInfofactory.getId()).then(
			function (response) {
				return response.data;
			});
	}
	blukApprovalfactory.bulkStatusUpdate = function (data) {
		return $http.post('worklog/saveTimeSheet/' + PrincipalInfofactory.getId(), data).then(
			function (response) {
				return response.data;
			});
	};

	blukApprovalfactory.fetchReportSummary = function (startDate, endDate, projectId) {
		var userId = PrincipalInfofactory.getId();
		return $http.get('worklog/getReportSummary/' + userId + "/" + projectId + "/" + startDate + "/" + endDate).then(
			function (response) {
				return response.data;
			});
	};

	blukApprovalfactory.fetchPMOReport = function (startDate, endDate, projectId) {
		var userId = PrincipalInfofactory.getId();
		return $http.get('worklog/getpmoreport/' + userId + "/" + projectId + "/" + startDate + "/" + endDate).then(
			function (response) {
				return response.data;
			});
	};

	return blukApprovalfactory;
}
angular.module('timeSheet').factory('blukApprovalfactory', blukApprovalfactory);