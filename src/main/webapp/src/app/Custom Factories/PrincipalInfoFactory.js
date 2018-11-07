function PrincipalInfofactory($http, $rootScope, Upload) {

	var PrincipalInfofactory = {};

	PrincipalInfofactory.loginData = null;
	PrincipalInfofactory.token = "";
	PrincipalInfofactory.monthlyView = false;

	PrincipalInfofactory.setPrincipal = function (data) {
		PrincipalInfofactory.loginData = data;
		$rootScope.userName = data.username;

	}

	PrincipalInfofactory.resetPrincipal = function (data) {
		PrincipalInfofactory.loginData = null;
	}


	PrincipalInfofactory.getloginData = function () {
		if (PrincipalInfofactory.loginData == undefined || PrincipalInfofactory.loginData == null) {
			var data = PrincipalInfofactory.getPrincipalObject();
			if (data != undefined) {
				PrincipalInfofactory.setPrincipal(data);
				return PrincipalInfofactory.loginData;
			}
		}
		else {
			if (PrincipalInfofactory.loginData != undefined &&
				PrincipalInfofactory.loginData != null) {
				return PrincipalInfofactory.loginData;
			}
		}
	}


	PrincipalInfofactory.getUserName = function () {
		if (PrincipalInfofactory.loginData == undefined || PrincipalInfofactory.loginData == null) {
			var data = PrincipalInfofactory.getPrincipalObject();
			if (data != undefined) {
				PrincipalInfofactory.setPrincipal(data);
				return PrincipalInfofactory.loginData.username;
			}
		}
		else {
			if (PrincipalInfofactory.loginData != undefined &&
				PrincipalInfofactory.loginData != null &&
				PrincipalInfofactory.loginData.username != undefined &&
				PrincipalInfofactory.loginData.username != null) {
				return PrincipalInfofactory.loginData.username;
			}
		}
	}

	PrincipalInfofactory.getEmpId = function () {
		if (PrincipalInfofactory.loginData == undefined || PrincipalInfofactory.loginData == null) {
			var data = PrincipalInfofactory.getPrincipalObject();
			if (data != undefined) {
				PrincipalInfofactory.setPrincipal(data);
				return PrincipalInfofactory.loginData.empId;
			}
		}
		else {
			if (PrincipalInfofactory.loginData != undefined &&
				PrincipalInfofactory.loginData != null &&
				PrincipalInfofactory.loginData.empId != undefined &&
				PrincipalInfofactory.loginData.empId != null) {
				return PrincipalInfofactory.loginData.empId;
			}
		}
	}

	PrincipalInfofactory.getId = function () {
		if (PrincipalInfofactory.loginData == undefined || PrincipalInfofactory.loginData == null) {
			var data = PrincipalInfofactory.getPrincipalObject();
			if (data != undefined) {
				PrincipalInfofactory.setPrincipal(data);
				return PrincipalInfofactory.loginData.id;
			}
		}
		else {
			if (PrincipalInfofactory.loginData != undefined &&
				PrincipalInfofactory.loginData != null &&
				PrincipalInfofactory.loginData.id != undefined &&
				PrincipalInfofactory.loginData.id != null) {
				return PrincipalInfofactory.loginData.id;
			}
		}
	}




	PrincipalInfofactory.getUserRole = function () {
		if (PrincipalInfofactory.loginData == undefined || PrincipalInfofactory.loginData == null) {
			var data = PrincipalInfofactory.getPrincipalObject();
			if (data != undefined) {
				PrincipalInfofactory.setPrincipal(data);
				return PrincipalInfofactory.loginData.role;
			}
		}
		else {
			if (PrincipalInfofactory.loginData != undefined &&
				PrincipalInfofactory.loginData != null &&
				PrincipalInfofactory.loginData.role != undefined &&
				PrincipalInfofactory.loginData.role != null) {
				return PrincipalInfofactory.loginData.role;
			}
		}
	}


	PrincipalInfofactory.getPrincipalObject = function () {
		return PrincipalInfofactory.loginData;
	}

	PrincipalInfofactory.uploadJiraFile = function (reqBody) {
		return Upload.upload({
			url: "import/jiraimport",
			data: reqBody
		}).then(
			function (response) {
				return response.data;
			}
		);
	}

	PrincipalInfofactory.dataPurge = function () {
		return $http.post('purgeOldData/').then(
			function(response){
				return response.data;
			}
		);
	}

	return PrincipalInfofactory;

}

angular
	.module('timeSheet')
	.factory('PrincipalInfofactory', PrincipalInfofactory);