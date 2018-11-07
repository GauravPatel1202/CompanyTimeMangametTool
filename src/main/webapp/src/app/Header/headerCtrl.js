var HeaderCtrl = function ($state, $rootScope, $http, PrincipalInfofactory, $mdDialog, $scope, toastr, $sce) {
	var that = this;
	that.viewTimesheetClickedCheck = false;
	that.statusSelected = {};
	that.statusSelected.status = "";
	that.isFirstLogin = $rootScope.user.firstLogin;
	that.showLink = false;


	if ($rootScope.role === "Admin" || $rootScope.role === "Account Head" ||
		$rootScope.role === "Team Lead") {
		that.SuperAdminRole = true;
	} else {
		that.SuperAdminRole = false;
	}
	function alert() {
	  $state.go('task');
	}
	function logOut() {
		window.idleTimer && clearInterval(window.idleTimer);
		$state.go('login');
		$rootScope.username = null;
		$rootScope.role = null;
		$rootScope = $rootScope.$new(true);
		window.sessionStorage.removeItem("authenticated");
		window.sessionStorage.removeItem("userData");
		window.sessionStorage.removeItem("token");
	}

	function goToRegisteration() {
		that.selectedUser = null;
		$state.go('register');
	}
	function goToTask() {
		that.selectedUser = null;
		$state.go('task');
	}
	function goToProjects() {
		that.selectedUser = null;
		$state.go('projects');
	}

	function goToResourceMapping() {
		that.selectedUser = null;
		$state.go('mapresource');
	}

	function goToResource() {
		that.selectedUser = null;
		$state.go('resources');
	}

	function changePassword() {
		that.selectedUser = null;
		$state.go('changePassword');
	}

	function goToAlertConfiguration() {
		that.selectedUser = null;
		$state.go('alertConfiguration');
	}

	function viewTimeSheetClicked() {
		that.viewTimesheetClickedCheck = true;
	}


	var empId = $rootScope.user.id;
	$http.get('user/getUsersByProjectAndRole/' + empId).then(
		function (response) {
			that.usersForViewTimesheet = response.data;
		});
	$http.get('worklog/getWoklogStatus').then(
		function (response) {
			that.filterList = response.data;
			that.filterList.push({
				"status": "All Users",
				"id": 0
			});
		});

	function selectUser(user) {
		that.userSelected = user;
	}

	function selectStatus(status) {
		if (status) {
			that.selectedUser = null;
			if (status.id == 0) {
				that.statusSelected = status;
				$http.get('user/getUsersByProjectAndRole/' + empId).then(
					function (response) {
						that.usersForViewTimesheet = response.data;
					});
			} else {
				that.statusSelected = status;
				$http.get('user/getUsersByProjectAndRoleFilter/' + empId + "/" + status.id).then(
					function (response) {
						that.usersForViewTimesheet = response.data;
					});
			}
		}
	}

	function getUserBystring() {
		var stringToSearch = that.search;
		if (that.statusSelected) {
			$http.get('user/getUsersByProjectAndRoleFilter/' + empId + "/" + stringToSearch + "/" + that.statusSelected.id).then(
				function (response) {
					that.usersForViewTimesheet = response.data;
				});
		} else {
			$http.get('user/getUsersByProjectAndRoleFilter/' + empId + "/" + stringToSearch + "/" + 0).then(
				function (response) {
					that.usersForViewTimesheet = response.data;
				});
		}
	}

	function goToClient() {
		$state.go('client');
	}

	function goToTimesheetApproval() {
		$state.go('timesheetApproval');
	}

	function goToBulkApproval() {
		$state.go('bulkApproval');
	}

	function goToReportSummary() {
		var currentUrl = window.location.href;
		var chartIndex = currentUrl.indexOf("#chart");
		if (chartIndex >= 0) {
			window.location = window.location.href.slice(0, chartIndex);
		}
		setTimeout(function () {
			$state.go('reportSummary');
		}, 100);
	}

	function goToMonthlyView() {
		PrincipalInfofactory.monthlyView = true;
		$state.go('monthlyView');
	}

	function goToHolidayMark() {
		$state.go("markHolidays");
	}

	function openJiraImport() {
		$mdDialog.show({
			controller: jiraImportFormCtrl,
			controllerAs: 'ctrl',
			templateUrl: './src/app/Header/jira-import-modal.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: {
				"dialogInfo": {}
			},
			resolve: {}
		}).then(function (data) {

		}, function (data) {

		});
	}

	function jiraImportFormCtrl($mdDialog, dialogInfo, $scope) {
		var that = this;
		that.jiraInstructions = appConstants.jira_instructions;
		that.dialogInfo = dialogInfo;

		function close() {
			$mdDialog.hide();
		}

		function uploadFile(importForm) {
			importForm.$setSubmitted();
			if (importForm.$valid) {
				var fileName = that.dataFile.attachment.name || "";
				fileName = fileName.split(".");
				var file = that.dataFile.attachment;
				var extn = fileName[fileName.length - 1];
				if (extn !== "xlsx" && extn !== "xls") {
					toastr.error("Invalid file format. Please choose a excel(.xls,.xslx) file");
				} else {
					that.showLink = false;
					that.dataFile.userId = PrincipalInfofactory.getId();
					PrincipalInfofactory.uploadJiraFile(that.dataFile).then(
						function (response) {
							toastr.success("File has been uploaded. Please check the resultant file to know errors if any.");
							that.showLink = true;
							that.fileContent = $sce.trustAsUrl(response);
							that.fileName = fileName[0] + "_resultant." + fileName[1];
						},
						function (error) {

						}
					);
				}
			}
		}

		angular.extend(this, {
			"close": close,
			"uploadFile": uploadFile
		});
	}

	function purgeData() {
		$mdDialog.show({
			controller: dataPurgeCtrl,
			controllerAs: 'ctrl',
			templateUrl: './src/app/Header/data-purge-modal.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: {
				"dialogInfo": {}
			},
			resolve: {}
		}).then(function (data) {

		}, function (data) {

		});
	}

	function dataPurgeCtrl($mdDialog, dialogInfo, $scope) {
		var that = this;
		that.purge_instructions = appConstants.purge_instructions;
		that.dialogInfo = dialogInfo;
		var date = moment().subtract(13,'months').format('YYYY-MM-DD');
		that.purge_instructions[0] = that.purge_instructions[0].replace('{{date}}',date);

		function close() {
			$mdDialog.hide();
		}

		function purgeData() {
			PrincipalInfofactory.dataPurge().then(
				function () {
					toastr.success('Data beyond last 13 months has been purged');
				},
				function () {
					toastr.error('Data could not be purged at the moment. Please try again later.');
				}
			);
		}

		angular.extend(this, {
			"close": close,
			"purgeData": purgeData
		});
	}

	angular.extend(this, {
		logOut: logOut,
		goToRegisteration: goToRegisteration,
		changePassword: changePassword,
		goToTask: goToTask,
		goToProjects: goToProjects,
		viewTimeSheetClicked: viewTimeSheetClicked,
		selectUser: selectUser,
		selectStatus: selectStatus,
		getUserBystring: getUserBystring,
		goToClient: goToClient,
		goToResourceMapping: goToResourceMapping,
		goToResource: goToResource,
		goToAlertConfiguration: goToAlertConfiguration,
		goToTimesheetApproval: goToTimesheetApproval,
		goToBulkApproval: goToBulkApproval,
		goToReportSummary: goToReportSummary,
		goToMonthlyView: goToMonthlyView,
		goToHolidayMark: goToHolidayMark,
		openJiraImport: openJiraImport,
		purgeData: purgeData,
		alertTask:alert
	});
}

angular
	.module('timeSheet')
	.controller('HeaderCtrl', HeaderCtrl);
