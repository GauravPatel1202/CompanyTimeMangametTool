var bulkApprovalCtrl = function (blukApprovalfactory, PrincipalInfofactory, managerMode,toastr, $stateParams, $rootScope, $state, $scope, $mdDialog) {
	var self = this;
	var that = this;
	this.title = "Timesheet Bulk Approval";
	this.selectedDate = new Date();
	that.tableData = [];
	that.managerProjects = [];
	that.selectedUserIds = [];
	that.selectAll = false;
	that.rejectComment = "";
	that.resource={}
	that.descriptionUrl = "assets/task-description.html";
	that.managerMode = managerMode || false;
	/**
	 * Below section sets the current user based on the route(Timesheet Bulk Approval entry/ approval)
	 * If the route is for approval, it fetches the list of users whose Timesheet Bulk Approval can be approved by logged in user.
	 */
	if (!that.managerMode) {
		that.currentUser = PrincipalInfofactory.getId();
		loadTimeSheet(currentDate);
		that.editMode = true;
	} else {
		that.reportView = false;
		that.currentUser = $stateParams.userId;
		that.resources = [];
		blukApprovalfactory.getReportingUsers().then(
			function (users) {
				for (var i = 0; i < users.length; i++) {
					if (users[i].role.id > PrincipalInfofactory.loginData.role.id || PrincipalInfofactory.loginData.role.id === 1) {
						that.resources.push(users[i]);
					}
				}
			}
		)
	}
	
	/**
	 * 
	 * @param bulkApprovalForm 
	 * @param flag 
	 * 
	 * This function calls a factory method to fetch the timesheet of the selected user for the selected week.
	 * 
	 */
	function ResetApprovalfilter(bulkApprovalFormfilter, flag) {
		that.tableData=that.OldTableData;
		that.res={};
		that.filterDone=false;
	}
	function fetchbulkApprovalfilter(bulkApprovalFormfilter, flag) {
		
		if(that.filterDone){
			that.tableData=that.OldData
		}
		that.OldTableData=that.tableData
		that.OldData=that.OldTableData
		that.filterDone=true
		    bulkApprovalFormfilter.$setSubmitted();
		    if(that.res.resource)
	    	that.tableData=that.tableData.filter(function (Data) {
			    return (Data.userId == that.res.resource.id);
			});
		    if(that.res.selectApproved&&that.res.selectRejected){
		    	return;
		    }else{
		    	if(that.res.selectApproved){
		    		that.tableData=that.tableData.filter(function (Data) {
		    			if(Data.hasOwnProperty("approver") ){
						    return Data;
			    			}else{
			    				return Data;
			    			}
					});
		    	}
		    	if(that.res.selectRejected){
		    		that.tableData=that.tableData.filter(function (Data) {
		    			if(Data.hasOwnProperty("approver") ){
					        return Data;
		    			}else{
		    				return Data;
		    			}
		    			
					});
		    	}
		    }
		}
	
	/**
	 * 
	 * @param bulkApprovalForm 
	 * @param flag 
	 * 
	 * This function calls a factory method to fetch the timesheet of the selected user for the selected week.
	 * 
	 */
	function fetchTimesheet(bulkApprovalForm, flag) {
		that.res={};
		that.filterDone=false;
		if (!flag)
			bulkApprovalForm.$setSubmitted();
		if (flag || bulkApprovalForm.$valid) {
			var firstDay = moment(that.selectedDate).startOf('isoWeek').format("YYYY-MM-DD");
			var lastDay = moment(that.selectedDate).endOf('isoWeek').format("YYYY-MM-DD");
			blukApprovalfactory.fetchTimeSheetEntries(firstDay, lastDay).then(
				function (data) {
					that.selectAll = false;
					that.selectedUserIds = [];
					that.managerProjects = data && data.length > 0 ? data[0].projectIds : [];
					var projs = [];
					var attrs = [];
					that.row = true;
					for (var i = 0; i < data.length; i++) {
						attrs = Object.keys(data[i].work);
						that.name = data[i].work[attrs[0]][0].person.empName;
						that.userId = data[i].work[attrs[0]][0].person.id;
						that.timeSheetData = data[i];
						that.projects = angular.copy(data[i].distinctProjects);
						generateDateForMonth();
						processTimeSheetData();
						for (var j = 0; j < that.projects.length; j++) {
							that.projects[j].rowCount = that.projects.length;
							projs.push(that.projects[j]);
						}
						projs.push({
							userName: that.name,
							userId: that.userId,
							row: !that.row,
							project: {},
							task: {},
							totalHrsSpent: that.weeklySum,
							times: that.totalHours,
							totalsRow: true
						});
					}
					that.tableData = that.OldData=projs;
					
				}
			);
		}
	}

	/**
	 * This function creates the JSON for the grid to show the timesheet of the selected week.
	 */
	function generateDateForMonth() {
		var firstDay = moment(that.selectedDate).startOf('isoWeek').toDate();
		var lastDay = moment(that.selectedDate).endOf('isoWeek').toDate();
		that.timeSheet = {};
		for (var i = firstDay; i <= lastDay; i.setDate(i.getDate() + 1)) {
			var date = moment(i);
			var key = date.format("YYYY-MM-DD");
			var columns = {
				displayDate: date.format("DD-MMM"),
				day: date.weekday(),
				approval: false,
				submission: false,
				displayDay: date.format("ddd")
			}
			that.timeSheet[key] = columns;
		}
		for (var i = 0; i < that.projects.length; i++) {
			that.projects[i].times = angular.copy(that.timeSheet);
			that.projects[i].userName = that.name;
			that.projects[i].userId = that.userId;
			that.projects[i].oddRow = that.row;
			if (i === that.projects.length - 1) {
				that.row = !that.row;
			}
		}
		that.totalHours = angular.copy(that.timeSheet);
		that.approveMatrix = angular.copy(that.timeSheet);
		that.submittedMatrix = angular.copy(that.timeSheet);
		that.selectionMatrix = angular.copy(that.timeSheet);
	}

	/**
	 * This function populates the JSON values using the response for loading the grid
	 */
	function processTimeSheetData() {
		for (var key in that.timeSheetData.work) {
			for (var i = 0; i < that.timeSheetData.work[key].length; i++) {
				that.projects[that.timeSheetData.work[key][i].index - 1].times[key] = that.timeSheetData.work[key][i];
				if (that.timeSheetData.status[key] == "Pending Approval") {
					that.submittedMatrix[key].submission = true;
					that.approveMatrix[key].approval = false;
				} else if (that.timeSheetData.status[key] == "Approved") {
					that.approveMatrix[key].approval = true;
					that.submittedMatrix[key].submission = false;
				}
				if (that.timeSheetData.work[key][i].status && (that.timeSheetData.work[key][i].status.id === 4 || that.timeSheetData.work[key][i].status.id === 3)) {
					that.submitEnabled = true;
				}
			}
		}
		that.projects.forEach(function (row) {
			var times = row.times
			var keys = Object.keys(times);
			var spentHours = 0;
			keys.forEach(function (key) {
				spentHours = spentHours + (times[key].hoursSpent || 0);
				spentHours = parseFloat(spentHours.toFixed(2));
				initTotal(key,times[key],row);
			});
			row.totalHrsSpent = spentHours;
		});
	}

	/**
	 * 
	 * @param statusId 
	 * 
	 * This function generates the request body to approve/reject the entries of the week for selected user.
	 */
	function generateRequestData(statusId) {
		var dataToSend = [];
		var days = [];
		var times, entry;
		for (var i = 0; i < that.tableData.length; i++) {
			times = that.tableData[i].times;
			days = Object.keys(times);
			for (var j = 0; j < days.length; j++) {
				entry = angular.copy(times[days[j]]);
				if (entry.id && that.managerProjects.indexOf(entry.project.id) >= 0 && entry.status.id === 1 && that.selectedUserIds.indexOf(entry.person.id) >= 0) {
					entry.status = {
						id: statusId
					};
					dataToSend.push(entry);
				}
			}
		}
		return dataToSend;
	}

	/**
	 * This function calls a factory method to approve the timesheet entries of the week of a user and reloads the timesheet after approval.
	 */
	function approveEntries() {
		if (that.selectedUserIds.length === 0) {
			toastr.warning(appMessages.SELECT_USERS_TO_APPROVE, "Attention");
			return;
		}
		var dataToSend = generateRequestData(2);
		if (dataToSend.length === 0) {
			toastr.info(appMessages.NO_SUBMITTED_ENTRIES_TO_APPROVE, "Information");
			return;
		} else {
			var confirm = $mdDialog.confirm({
				templateUrl: "assets/confirm-box.template.html",
				parent: angular.element(document.body),
				locals: {
					title: appMessages.APPROVE_CONFIRMATION_TITLE,
					textContent: appMessages.APPROVE_CONFIRMATION_CONTENT,
					ok: 'Yes',
					cancel: 'No'
				}
			});

			$mdDialog.show(confirm).then(function () {
				blukApprovalfactory.bulkStatusUpdate(dataToSend).then(function () {
					fetchTimesheet(null, true);
					toastr.success(appMessages.APPROVE_SUCCESS);
				}, function (data) {
					toastr.error(data.data.message || appMessages.APPROVE_FAILURE, "Error");
				});
			});
		}
	}

	/**
	 * This function calls a factory method to reject the timesheet entries of the week of a user and reloads the timesheet after rejecting.
	 */
	function rejectEntries() {
		if (that.selectedUserIds.length === 0) {
			toastr.warning(appMessages.SELECT_USERS_TO_REJECT, "Attention");
			return;
		}
		var dataToSend = generateRequestData(3);
		if (dataToSend.length === 0) {
			toastr.info(appMessages.NO_SUBMITTED_ENTRIES_TO_APPROVE, "Information");
			return;
		} else {
			/*var confirm = $mdDialog.confirm({
				templateUrl: "assets/confirm-box.template.html",
				parent: angular.element(document.body),
				locals: {
					title: appMessages.REJECT_CONFIRMATION_TITLE,
					textContent: appMessages.REJECT_CONFIRMATION_CONTENT,
					ok: 'Yes',
					cancel: 'No'
				}
			});

			$mdDialog.show(confirm).then(function () {
				blukApprovalfactory.bulkStatusUpdate(dataToSend).then(function () {
					fetchTimesheet(null, true);
					toastr.success(appMessages.REJECT_SUCCESS);
				}, function (data) {
					toastr.error(data.data.message || appMessages.REJECT_FAILURE, "Error");
				});
			});*/
			$mdDialog.show({
				controller: rejectCtrl,
				controllerAs: 'ctrl',
				templateUrl: './src/app/TimeSheet/reject-comments.modal.html',
				parent: angular.element(document.body),
				clickOutsideToClose: false,
			}).then(function (data) {
				for (var i = 0; i < dataToSend.length; i++) {
					dataToSend[i].rejectComment = data;
				}
				blukApprovalfactory.bulkStatusUpdate(dataToSend).then(function () {
					fetchTimesheet(null, true);
					toastr.success(appMessages.REJECT_SUCCESS);
				}, function (data) {
					toastr.error(data.data.message || appMessages.REJECT_FAILURE, "Error");
				});
			}, function (data) {

			});

			function rejectCtrl($mdDialog) {
				var that = this;
				that.rejectComment = "";
				function rejectEntries(rejectForm) {
					rejectForm.$setSubmitted();
					if (rejectForm.$valid) {
						$mdDialog.hide(that.rejectComment);
					}
				}
				function close() {
					$mdDialog.cancel();
				}

				angular.extend(this, {
					"rejectEntries": rejectEntries,
					"close": close
				});
			}
		}
	}

	/**
	 * 
	 * @param event
	 * This is a event handler to select or unselect the users whose timesheet entries will be considered for approving or rejecting
	 */
	function onUserCheck(event) {
		var elem = event.currentTarget;
		var checked = elem.checked;
		var userId = parseInt(elem.value);
		if (checked) {
			that.selectedUserIds.push(userId);
		} else {
			that.selectedUserIds.splice(that.selectedUserIds.indexOf(userId), 1);
		}
		if (that.selectedUserIds.length === 0) {
			that.selectAll = false;
		}
	}

	/**
	 * 
	 * @param event 
	 * This function is a event handler to select/unselect all the users on check/uncheck of select all checkbox.
	 */
	function onToggleAll(event) {
		var elem = event.currentTarget;
		var checked = elem.checked;
		that.selectedUserIds = [];
		if (checked) {
			for (var i = 0; i < that.tableData.length; i++) {
				that.tableData[i].selected = true;
				if (that.selectedUserIds.indexOf(that.tableData[i].userId) < 0) {
					that.selectedUserIds.push(that.tableData[i].userId);
				}
			}
		} else {
			for (var i = 0; i < that.tableData.length; i++) {
				that.tableData[i].selected = false;
			}
		}
	}

	/**
	 * 
	 * @param key 
	 * @param cellData 
	 * This function calculates the total hours spent by the resource.
	 */
	function initTotal(key, cellData, row) {
		cellData.hoursSpent = cellData.hoursSpent || 0;
		if (that.totalHours[key].sum == undefined || that.totalHours[key].sum == null)
			that.totalHours[key].sum = 0;
		that.totalHours[key].sum = parseFloat((that.totalHours[key].sum + cellData.hoursSpent).toFixed(2));
		that.totalHours[key].hoursSpent = that.totalHours[key].sum;
		that.totalHours[key].status = {};
		var keys = Object.keys(row.times);
		that.weeklySum = 0;
		keys.forEach(function (key) {
			that.weeklySum = parseFloat((that.weeklySum + that.totalHours[key].sum).toFixed(2));
		});
	}

	angular.extend(this, {
		"fetchTimesheet": fetchTimesheet,
		"approveEntries": approveEntries,
		"rejectEntries": rejectEntries,
		"onUserCheck": onUserCheck,
		"onToggleAll": onToggleAll,
		"initTotal": initTotal,
		'bulkApprovalfilter':fetchbulkApprovalfilter,
		'ResetApprovalfilter':ResetApprovalfilter
	});
};
angular.module('timeSheet').controller('bulkApprovalCtrl', bulkApprovalCtrl);