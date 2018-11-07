var timesheetCtrl = function (TimeSheetFactory, PrincipalInfofactory, toastr, $stateParams, $rootScope, $state, $scope, managerMode, $mdDialog, $timeout, MarkHolidaysFactory) {

	var that = this;
	var currentDate = new Date();
	that.displayMonth = currentDate.getMonthName();
	that.timeSheet = {};
	that.projects = null;
	that.projectlist;
	that.taskList = [];
	that.totalHours = {};
	that.approveMatrix = {};
	that.submittedMatrix = {};
	that.selectionMatrix = {};
	that.timeSheetData = null;
	that.currentUser = null;
	that.selectedColumns = 0;
	that.selectedDate = new Date();
	that.days = [];
	that.saveEnabled = false;
	that.initialData = {};
	that.maxDate = moment(new Date()).startOf("isoWeek").add(6, "days").toDate();
	that.submitEnabled = false;
	that.title = "Timesheet";
	that.resource = {};
	that.managerMode = managerMode || false;
	that.editMode = false;
	that.timeSheetFrom;
	that.timeSheetTo;
	that.dateOfJoining = PrincipalInfofactory.loginData.dateOfJoining;
	that.holidayList = [];
	that.descriptionUrl = "assets/task-description.html";
	that.hoursDescriptionUrl = "assets/worklog-comment.template.html";
	that.canAddRows = true;

	/**
	 * Below section sets the current user based on the route(timesheet entry/ approval)
	 * If the route is for approval, it fetches the list of users whose timesheet can be approved by logged in user.
	 */
	if (!that.managerMode) {
		that.currentUser = PrincipalInfofactory.getId();
		loadTimeSheet(currentDate);
		that.editMode = true;
	} else {
		that.reportView = false;
		that.currentUser = $stateParams.userId;
		that.resources = [];
		TimeSheetFactory.getReportingUsers().then(
			function (users) {
				for (var i = 0; i < users.length; i++) {
					if (users[i].role.id > PrincipalInfofactory.loginData.role.id || PrincipalInfofactory.loginData.role.id === 1) {
						that.resources.push(users[i]);
					}
				}
			}
		);
		that.reportViewCols = [{
			displayName: 'Date',
			field: 'date',
			sort: {
				direction: 'asc',
				priority: 0
			},
			enableFiltering: false,
			enableSorting: true,
			cellTemplate: '<div><div ng-if="!col.grouping || col.grouping.groupPriority === undefined || col.grouping.groupPriority === null || ( row.groupHeader && col.grouping.groupPriority === row.treeLevel )" class="ui-grid-cell-contents">{{COL_FIELD CUSTOM_FILTERS}}</div></div>',
			grouping: {
				groupPriority: 0
			},
			width: "10%",
			cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
				var className = '';
				var date;
				if (row.groupHeader) {
					date = moment(row.treeNode.aggregations[0].groupVal);
				} else {
					date = moment(row.entity.date);
				}
				if (date.weekday() === 6 || date.weekday() === 0 || that.holidays.indexOf(date.format("YYYY-MM-DD")) >= 0) {
					className += " weekend";
				}
				return className;
			}
		}, {
			displayName: 'Project',
			field: 'project.projectName',
			width: "15%",
			cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
				var className = '';
				var date;
				if (row.groupHeader) {
					date = moment(row.treeNode.aggregations[0].groupVal);
				} else {
					date = moment(row.entity.date);
				}
				if (date.weekday() === 6 || date.weekday() === 0 || that.holidays.indexOf(date.format("YYYY-MM-DD")) >= 0) {
					className += " weekend";
				}
				return className;
			}
		}, {
			displayName: 'Task Name',
			field: 'task.taskName',
			width: "15%",
			cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
				var className = '';
				var date;
				if (row.groupHeader) {
					date = moment(row.treeNode.aggregations[0].groupVal);
				} else {
					date = moment(row.entity.date);
				}
				if (date.weekday() === 6 || date.weekday() === 0 || that.holidays.indexOf(date.format("YYYY-MM-DD")) >= 0) {
					className += " weekend";
				}
				return className;
			}
		}, {
			displayName: 'Task Description',
			field: 'task.description',
			cellTemplate: '<md-tooltip md-direction="top" class="task-desc-tooltip" ng-if="COL_FIELD">{{COL_FIELD}}</md-tooltip><div class="ui-grid-cell-contents">{{COL_FIELD}}</div>',
			width: "25%",
			cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
				var className = '';
				var date;
				if (row.groupHeader) {
					date = moment(row.treeNode.aggregations[0].groupVal);
				} else {
					date = moment(row.entity.date);
				}
				if (date.weekday() === 6 || date.weekday() === 0 || that.holidays.indexOf(date.format("YYYY-MM-DD")) >= 0) {
					className += " weekend";
				}
				return className;
			}
		}, {
			displayName: 'Hours Spent',
			field: 'hoursSpent',
			cellTemplate: '<div class="ui-grid-cell-contents">{{COL_FIELD}}</div>',
			cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
				var className = 'text-center time-box';
				var status;
				if (row.entity && row.entity.status) {
					status = row.entity && row.entity.status;
				}
				if (!status) {
					className += " no-value";
				} else if (status.id === 1) {
					className += " submitted";
				} else if (status.id === 2) {
					className += " approved";
				} else if (status.id === 3) {
					className += " rejected";
				} else if (status.id === 4) {
					className += " saved";
				}

				if (row.groupHeader) {
					date = moment(row.treeNode.aggregations[0].groupVal);
				} else {
					date = moment(row.entity.date);
				}
				if (date.weekday() === 6 || date.weekday() === 0 || that.holidays.indexOf(date.format("YYYY-MM-DD")) >= 0) {
					className += " weekend";
				}

				return className;
			},
			width: "10%"
		}, {
			displayName: 'Comments',
			field: 'comments',
			width: "22%",
			cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
				var className = '';
				var date;
				if (row.groupHeader) {
					date = moment(row.treeNode.aggregations[0].groupVal);
				} else {
					date = moment(row.entity.date);
				}
				if (date.weekday() === 6 || date.weekday() === 0 || that.holidays.indexOf(date.format("YYYY-MM-DD")) >= 0) {
					className += " weekend";
				}
				return className;
			}
		}];
		that.reportGridOptions = {
			enableColumnMenus: false,
			enableFiltering: true,
			enableSorting: false,
			rowHeight: 45,
			columnDefs: that.reportViewCols,
			data: []
		};
		that.reportGridOptions.onRegisterApi = function (gridApi) {
			that.reportGridApi = gridApi;
		};
	}

	/**
	 * This function calls a factory method to fetch the timesheet of the selected user for the selected week.
	 */
	function loadTimeSheet() {
		currentDate = that.selectedDate;
		var currentMonth = moment(that.selectedDate).startOf('isoWeek').format("YYYY-MM-DD");
		TimeSheetFactory.getProjects(currentMonth, that.currentUser).then(function (data) {
			that.rejectComment = "";
			that.projectlist = angular.copy(data);
			var firstDay = moment(that.selectedDate).startOf('isoWeek').toDate();
			var lastDay = moment(that.selectedDate).endOf('isoWeek').toDate();
			firstDay = moment(firstDay).format("YYYY-MM-DD");
			lastDay = moment(lastDay).format("YYYY-MM-DD");
			that.timeSheetFrom = moment(firstDay).format("MMMM DD");
			that.timeSheetTo = moment(lastDay).format("MMMM DD");
			TimeSheetFactory.getTimeSheet(firstDay, lastDay, that.currentUser).then(function (data) {
				that.timeSheetData = data;
				that.initialData = angular.copy(data);
				that.projects = angular.copy(data.distinctProjects);
				that.managerProjects = angular.copy(data.projectIds);
				MarkHolidaysFactory.fetchHolidaysOfYear(moment(firstDay).format("YYYY")).then(
					function (holidays) {
						that.holidayList = holidays;
						that.holidays = that.holidayList.map(function (holiday) {
							return holiday.holidayDate;
						});
						generateDateForMonth();
						processTimeSheetData();
						that.canAddRows = true;
						var days = Object.keys(that.timeSheet);
						that.approvedProjects = [];
						if (that.projects.length) {
							for (var i = 0; i < that.projects.length; i++) {
								var lastDayIndex = 4;
								while (that.holidays.indexOf(days[lastDayIndex]) >= 0) {
									lastDayIndex--;
								}
								var lastDay = days[lastDayIndex];
								var status = that.projects[i].times[lastDay].status;
								if (status && (status.id === 1 || status.id === 2)) {
									that.canAddRows = false;
								}
								if (status && status.id === 2) {
									that.approvedProjects.push(that.projects[i].project.id);
								}
							}
							if (!that.canAddRows) {
								for (var i = 0; i < that.projects.length; i++) {
									for (var j = 0; j < days.length; j++) {
										status = that.projects[i].times[days[j]].status;
										if (status && (status.id === 3 || status.id === 4)) {
											that.canAddRows = true;
										}
									}
								}
							}
							var projList = []
							for (var i = 0; i < that.projectlist.length; i++) {
								if (that.approvedProjects.indexOf(that.projectlist[i].id) < 0) {
									projList.push(that.projectlist[i]);
								}
							}
							that.projectlist = angular.copy(projList);
						} else {
							that.canAddRows = true;
						}
						if (!that.managerMode && that.canAddRows) {
							var emptyRow = 5 - that.projects.length;
							for (var i = 0; i < emptyRow; i++) {
								addRow();
							}
						}
						if (that.rejectComment) {
							that.tooltipVisible = true;
							$timeout(function () {
								that.tooltipVisible = false;
							}, 5000);
						}
						if (that.managerMode) {
							var workLogs = [];
							var dayLog = [];
							for (var i = 0; i < days.length; i++) {
								dayLog = that.timeSheetData.work[days[i]];
								if (dayLog) {
									for (var j = 0; j < dayLog.length; j++) {
										workLogs.push(dayLog[j]);
									}
								} /* else {
									var date = moment(days[i]);
									workLogs.push({
										displayDate: date.format("DD-MMM"),
										day: date.weekday(),
										approval: false,
										submission: false,
										displayDay: date.format("ddd"),
										date: date.format("YYYY-MM-DD"),
										hoursSpent: "NA",
										project: {
											projectName: 'NA'
										},
										task: {
											taskName: "NA",
											description: "NA"
										},
										comments: "NA"
									});
								} */
							}
							that.reportGridOptions.data = workLogs;
						}
					}
				);
			});
		});
	}

	function changeMonth(forward) {
		if (forward) {
			currentDate.setMonth(currentDate.getMonth() + 1);
			that.displayMonth = currentDate.getMonthName();
			loadTimeSheet(currentDate);
		} else {
			currentDate.setMonth(currentDate.getMonth() - 1);
			that.displayMonth = currentDate.getMonthName();
			loadTimeSheet(currentDate);
		}
	}

	/**
	 * This function creates the JSON for the grid to show the timesheet of the selected week.
	 */
	function generateDateForMonth() {
		var currDate = new Date(currentDate);
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
		that.submitEnabled = false;
		var index;
		function findProjectIndex(entryIndex) {
			for (var i = 0; i < that.projects.length; i++) {
				if (that.projects[i].index === entryIndex) {
					return i;
				}
			}
		}
		for (var key in that.timeSheetData.work) {
			for (var i = 0; i < that.timeSheetData.work[key].length; i++) {
				index = -1;
				index = findProjectIndex(that.timeSheetData.work[key][i].index);
				if (index >= 0) {
					that.projects[index].times[key] = that.timeSheetData.work[key][i];
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
		}
		that.projects.forEach(function (row) {
			var times = row.times
			var keys = Object.keys(times);
			var spentHours = 0;
			keys.forEach(function (key) {
				if (times[key].hoursSpent && times[key].hoursSpent > 0) {
					spentHours = parseFloat((spentHours + times[key].hoursSpent).toFixed(2)) || 0;
				}
				if (!that.managerMode && times[key].rejectComment && times[key].status && times[key].status.id === 3) {
					that.rejectComment = times[key].rejectComment;
				}
			});
			row.totalHrsSpent = spentHours;
		});
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
		var keys = Object.keys(row.times);
		that.weeklySum = 0;
		keys.forEach(function (key) {
			that.weeklySum = parseFloat((that.weeklySum + that.totalHours[key].sum).toFixed(2));
		});
	}

	/**
	 * 
	 * @param key 
	 * @param newValue 
	 * @param old 
	 * @param obj 
	 * @param element 
	 * @param row 
	 * This function checks whether the entered hours is valid or not such as should not be more than 24 on a single day, cannot cross the estimated hours of the task and so on.
	 */
	function updateTotal(key, newValue, old, obj, event, row, noConfirmFlag) {
		var keys = Object.keys(row.times);
		function validateEntry() {
			if (old != newValue && (isNaN(parseFloat(newValue)) || (parseFloat(newValue) < 0 && !(obj.day === 0 || obj.day === 6 || keys.indexOf(key) === 5 || keys.indexOf(key) === 6)))) {
				toastr.error(appMessages.MIN_HOURS, 'Error');
				obj.hoursSpent = old;
				return;
			}
			if(row.task.jiraImport){
				obj.hoursSpent = old;
				return;
			}
			var hoursRegex = /^[0-9]{1,2}([\.][0-9][0-9]?)?$/;
			if (!hoursRegex.test(newValue)) {
				toastr.error(appMessages.HOURS_VALIDATION, 'Error');
				obj.hoursSpent = old;
				return;
			}
			newValue = parseFloat(newValue);
			old = parseFloat(old);
			if (old === newValue) {
				return;
			}
			var total = parseFloat((that.totalHours[key].sum - old + newValue).toFixed(2));
			var newHoursLeft = parseFloat((row.task.hoursLeft + old - newValue).toFixed(2));
			var spentHours = 0;
			if (!row.project.globalProject && newHoursLeft < 0) {
				toastr.error(appMessages.EFFORTS_EXCEEDING, 'Error');
				obj.hoursSpent = old;
				return;
			}
			if (newValue > 24 || total > 24) {
				toastr.error(appMessages.MAX_HOURS_PER_DAY, 'Error');
				obj.hoursSpent = old;
				return;
			}
			if (newValue < 0) {
				toastr.error(appMessages.MIN_HOURS, 'Error');
				obj.hoursSpent = old;
				return;
			}
			that.saveEnabled = true;
			that.totalHours[key].sum = total;
			if (old != newValue) {
				that.submittedMatrix[key].isDirty = true;
			}
			that.weeklySum = 0;
			keys.forEach(function (key) {
				spentHours = parseFloat(parseFloat(parseFloat(spentHours) + parseFloat(row.times[key].hoursSpent)).toFixed(2));
				that.weeklySum = parseFloat(parseFloat(that.weeklySum + that.totalHours[key].sum).toFixed(2));
			});
			if (!row.project.globalProject)
				row.task.hoursLeft = parseFloat((row.task.hoursLeft + (old - newValue)).toFixed(2));
			row.totalHrsSpent = parseFloat(parseFloat(spentHours).toFixed(2));
		}
		/*if (!noConfirmFlag && old != newValue && (obj.day === 0 || obj.day === 6 || keys.indexOf(key) === 5 || keys.indexOf(key) === 6 || that.holidays.indexOf(key) >= 0 )) {
			event.stopPropagation();
			event.preventDefault();
			var confirm = $mdDialog.confirm({
				templateUrl: "assets/confirm-box.template.html",
				parent: angular.element(document.body),
				locals: {
					title: appMessages.HOLIDAY_HOURS_CONFIRMATION_TITLE,
					textContent: appMessages.HOLIDAY_HOURS_CONFIRMATION_CONTENT,
					ok: 'Yes',
					cancel: 'No'
				},
				clickOutsideToClose: false,
				escapeToClose: false
			});
			$mdDialog.show(confirm).then(
				function () {
					validateEntry();
				},
				function () {
					obj.hoursSpent = old;
					return;
				}
			);
		} else {*/
		validateEntry();
		//}
	}

	/**
	 * This function creates a new empty row to enter the timesheet for different project/task.
	 */
	function addRow() {
		var row = {};
		row.newRow = true;
		that.projects = that.projects || [];
		that.projects.push(row);
		row.times = angular.copy(that.timeSheet);
	}

	function submitColumn(key) {
		if (!that.submittedMatrix[key].isDirty) {
			toastr.error(appMessages.ENTER_BEFORE_SUBMIT, "Error");
			return;
		}
		var dataToSend = [];
		for (var i = 0; i < that.projects.length; i++) {
        	/* 1. if project 2. if task 3.if project 4.if task 5.if row generated 
        	 *  6.if hours not spent and id is not generated 7.if no approval permission continue*/
			if (that.projects[i].project == undefined || that.projects[i].task == undefined
				|| that.projects[i].project == null || that.projects[i].task == null
				|| that.projects[i].times[key] == undefined || that.projects[i].times[key] == null
				|| (that.projects[i].times[key].hoursSpent == 0 && that.projects[i].times[key].id == undefined)
				|| (that.projects[i].approver && that.projects[i].approver == false)) {
				continue;
			}
			var data = that.projects[i].times[key];
			data.index = i + 1;
			data.date = key;
			data.description = that.projects[i].description;
			data.id = data.id || null;
			data.person = {};
			data.person.id = that.currentUser;
			data.project = that.projects[i].project;
			data.status = {};
			data.status.id = 1;
			data.task = that.projects[i].task;
			data.lastUpdatedBy = {};
			data.lastUpdatedBy.id = PrincipalInfofactory.getId();
			data.lastUpdatedTime = new Date();
			data.createdBy = {};
			data.createdBy.id = PrincipalInfofactory.getId();
			data.createdDate = new Date();
			dataToSend.push(data);
		}
		TimeSheetFactory.submitDay(dataToSend).then(function () {
			loadTimeSheet();
			toastr.success(appMessages.TIMESHEET_UPDATE_SUCCESS);
			that.submittedMatrix[key].submission = that.submittedMatrix[key].submission == true ? false : true;
		}, function (data) {
			toastr.error(data.data.message, "Error");
			loadTimeSheet();
		});
	}

	function approve(key, doRevoke) {
		var dataToSend = {};
		dataToSend.personId = that.currentUser;
		dataToSend.dates = [];
		dataToSend.dates.push(key);
		dataToSend.status = {};
		if (doRevoke) {
			dataToSend.status.id = 3;
		} else {
			dataToSend.status.id = 2;
		}
		dataToSend.updatedBy = {};
		dataToSend.updatedBy.id = PrincipalInfofactory.getId();
		dataToSend.loggedUserId = PrincipalInfofactory.getId();
		dataToSend.updatedDate = new Date().valueOf()
		TimeSheetFactory.approve(dataToSend).then(function () {
			loadTimeSheet();
			that.approveMatrix[key].approval = that.approveMatrix[key].approval == true ? false : true;
			that.submittedMatrix[key].submission = false;
			that.selectionMatrix[key].isColumnSelected = false;
			if (doRevoke) {
				toastr.success(appMessages.REJECT_SUCCESS);
			} else {
				toastr.success(appMessages.APPROVE_SUCCESS);
			}
		}, function (data) {
			toastr.error(data.data.message);
		});
	}

	/**
	 * 
	 * @param project 
	 * @param id 
	 * @param elem 
	 * This function calls a factory method to fetch the list of active tasks for which user is assigned.
	 */
	function getTaskList(project, id, elem, row) {
		//var currentMonth = moment(currentDate).date(1).format("YYYY-MM-DD");
		var currentMonth = moment(that.selectedDate).startOf('isoWeek').format("YYYY-MM-DD");
		var personId = that.managerMode ? that.resource.id : PrincipalInfofactory.getId();
		TimeSheetFactory.getTasks(project.id, currentMonth, personId).then(function (data) {
			that.projects[id].taskList = filterTasks(data);
		});
		row.task = null;
		row.totalHrsSpent = 0;
		var days = Object.keys(row.times);
		days.forEach(function (day, index) {
			row.times[day].hoursSpent = 0;
		});
	}

	function approveAll() {
		var isSelected = false;
		var dataToSend = {};
		dataToSend.personId = that.currentUser;
		dataToSend.dates = [];
		dataToSend.status = {};
		dataToSend.status.id = 2;
		dataToSend.updatedBy = {};
		dataToSend.updatedBy.id = PrincipalInfofactory.getId();
		dataToSend.loggedUserId = PrincipalInfofactory.getId();
		dataToSend.updatedDate = new Date().valueOf()
		for (var key in that.selectionMatrix) {
			if (that.selectionMatrix[key].isSelected == true) {
				isSelected = true;
				dataToSend.dates.push(key);
			}
		}
		if (!isSelected) {
			toastr.error("No Date is Selected for Approval", "Error");
		} else {
			TimeSheetFactory.approve(dataToSend).then(function () {
				loadTimeSheet();
				toastr.success(appMessages.APPROVE_SUCCESS);
				that.approveMatrix[key].approval = that.approveMatrix[key].approval == true ? false : true;
				that.submittedMatrix[key].submission = false;
				that.selectionMatrix[key].isColumnSelected = false;
			}, function (data) {
				toastr.error(data.data.message);
			});
		}
	}

	/**
	 * 
	 * @param from 
	 * @param to 
	 * @param date 
	 * @param projectStart 
	 * @param projectEnd 
	 * @param project 
	 * This function checks whether the user can log hours against the selected proejct and task for a particular day.
	 */
	function isValidDate(from, to, date, projectStart, projectEnd, project) {
		//used in Ng-disabled so returns false
		var fromMoment;
		var toMoment;
		if (!project.globalProject && date > moment(new Date).format("YYYY-MM-DD")) {
			return true;
		}
		if (to != null) {
			fromMoment = moment(from);
			toMoment = moment(to);
			if (projectEnd != null && (moment(projectEnd) < moment(to))) {
				toMoment = moment(projectEnd);
			}
		} else if (to == null && projectEnd == null) {
			return false;
		} else if (to == null && projectEnd != null && from != null) {
			fromMoment = moment(from);
			toMoment = moment(projectEnd);
		} else if (to == null && projectEnd != null && from == null) {
			fromMoment = moment(projectStart);
			toMoment = moment(projectEnd);
		}
		var dateMoment = moment(date, "YYYY-MM-DD");
		if (dateMoment < moment(that.dateOfJoining, "YYYY-MM-DD")) {
			return true;
		}
		if (dateMoment >= fromMoment && dateMoment <= toMoment) {
			return false;
		} else {
			return true;
		}
	}

	function filterTasks(tasks) {
		for (var i = 0; i < that.projects.length; i++) {
			for (var j = 0; j < tasks.length; j++) {
				if (that.projects[i].task && that.projects[i].task.id == tasks[j].id) {
					tasks.splice(j, 1);
				}
			}
		}
		return tasks;
	}

	function columnSelected(value) {
		if (value == true) {
			that.selectedColumns = that.selectedColumns + 1;
		} else {
			that.selectedColumns = that.selectedColumns - 1;
		}
	}

	/**
	 * This function is to load the timesheet data for the selected week on date change for the timesheet entry screen.
	 */
	function setSelectedWeek() {
		if (!that.managerMode) {
			that.saveEnabled = false;
			loadTimeSheet();
		}
	}

	/**
	 * This function calls a factory method to save the changes made on the timesheet entry of the selected week.
	 */
	function saveWeekEntry() {
		var dataToSend = generateRequestData(4);
		if (that.nonWorkingHrs) {
			var confirm = $mdDialog.confirm({
				templateUrl: "assets/confirm-box.template.html",
				parent: angular.element(document.body),
				locals: {
					title: appMessages.HOLIDAY_HOURS_CONFIRMATION_TITLE,
					textContent: appMessages.HOLIDAY_HOURS_CONFIRMATION_CONTENT,
					ok: 'Yes',
					cancel: 'No'
				},
				clickOutsideToClose: false,
				escapeToClose: false
			});
			$mdDialog.show(confirm).then(
				function () {
					saveEntries();
				}
			);
		} else {
			saveEntries();
		}
		function saveEntries() {
			if (dataToSend.length === 0) {
				toastr.error(appMessages.ENTER_BEFORE_SAVE, "Error");
				return;
			} else {
				TimeSheetFactory.submitDay(dataToSend).then(function () {
					if (that.managerMode) {
						that.editMode = false;
						loadTimeSheet(currentDate);
					} else {
						loadTimeSheet();
					}
					toastr.success(appMessages.TIMESHEET_SAVE_SUCCESS);
				}, function (data) {
					toastr.error(data.data.message || appMessages.TIMESHEET_SAVE_FAILURE, "Error");
				});
			}
		}
		/*if (dataToSend.length === 0) {
			toastr.error(appMessages.ENTER_BEFORE_SAVE, "Error");
			return;
		} else {
			TimeSheetFactory.submitDay(dataToSend).then(function () {
				if (that.managerMode) {
					that.editMode = false;
					loadTimeSheet(currentDate);
				} else {
					loadTimeSheet();
				}
				toastr.success(appMessages.TIMESHEET_SAVE_SUCCESS);
			}, function (data) {
				toastr.error(data.data.message || appMessages.TIMESHEET_SAVE_FAILURE, "Error");
			});
		}*/
	}
	/**
	 * This function calls a factory method to submit the changes made on the timesheet entry of the selected week.
	 */
	function submitWeekEntry() {
		var confirm = $mdDialog.confirm({
			templateUrl: "assets/confirm-box.template.html",
			parent: angular.element(document.body),
			locals: {
				title: appMessages.SUBMIT_CONFIRMATION_TITLE,
				textContent: appMessages.SUBMIT_CONFIRMATION_CONTENT,
				ok: 'Yes',
				cancel: 'No'
			},
			clickOutsideToClose: false,
			escapeToClose: false
		});
		$mdDialog.show(confirm).then(
			function () {
				var dataToSend = generateRequestData(1);
				if (that.nonWorkingHrs) {
					var confirm = $mdDialog.confirm({
						templateUrl: "assets/confirm-box.template.html",
						parent: angular.element(document.body),
						locals: {
							title: appMessages.HOLIDAY_HOURS_CONFIRMATION_TITLE,
							textContent: appMessages.HOLIDAY_HOURS_CONFIRMATION_CONTENT,
							ok: 'Yes',
							cancel: 'No'
						},
						clickOutsideToClose: false,
						escapeToClose: false
					});
					$mdDialog.show(confirm).then(
						function () {
							submitEntries();
						}
					);
				} else {
					submitEntries();
				}
				function submitEntries() {
					if (dataToSend.length === 0) {
						toastr.error(appMessages.ENTER_BEFORE_SUBMIT, "Error");
						return;
					} else {
						var days = Object.keys(that.totalHours);
						var errorDays = [];
						days.forEach(function (day, index) {
							var dataForDay = {};
							for (var i = 0; i < dataToSend.length; i++) {
								if (dataToSend[i].date === day) {
									dataForDay = dataToSend[i];
									break;
								}
							}
							var prevIndex = index - 1;
							if (index === 6) {
								prevIndex -= 1;
							}
							while (that.holidays.indexOf(days[prevIndex]) >= 0) {
								prevIndex -= 1;
							}
							if (index !== 0 && prevIndex >= 0 && that.totalHours[day].sum > 0 && that.totalHours[days[prevIndex]].sum === 0 && that.dateOfJoining !== days[index] && errorDays.indexOf(that.totalHours[days[prevIndex]].displayDate) < 0) {
								errorDays.push(that.totalHours[days[prevIndex]].displayDate);
							}
							if ((that.totalHours[day].sum > 0 && that.totalHours[day].sum < 8 && index !== 5 && index !== 6 && that.holidays.indexOf(day) < 0) && errorDays.indexOf(that.totalHours[day].displayDate) < 0) {
								errorDays.push(that.totalHours[day].displayDate);
							}
							if (dataForDay.id && that.totalHours[day].sum < 8 && errorDays.indexOf(that.totalHours[day].displayDate) < 0) {
								errorDays.push(that.totalHours[day].displayDate);
							}
						});
						if (errorDays.length > 0) {
							toastr.error("Please enter at least 8 hours for " + errorDays.join(", "));
							return;
						}
						TimeSheetFactory.submitDay(dataToSend).then(function () {
							if (that.managerMode) {
								that.editMode = false;
								loadTimeSheet(currentDate);
							} else {
								loadTimeSheet();
							}
							toastr.success(appMessages.TIMESHEET_SUBMIT_SUCCESS);
						}, function (data) {
							toastr.error(data.data.message || appMessages.TIMESHEET_SUBMIT_FAILURE, "Error");
						});
					}
				}
			}
		);
	}
	/**
	 * 
	 * @param statusId 
	 * This function generates the request body to be sent to the service to save/submit/approve/reject the entries of a week for the user.
	 */
	function generateRequestData(statusId) {
		var dataToSend = [];
		var keys = Object.keys(that.submittedMatrix);
		that.nonWorkingHrs = false;
		keys.forEach(function (key) {
			var enteredProjects = [];
			for (var i = 0; i < that.projects.length; i++) {
				if (that.projects[i].project && ((that.projects[i].totalHrsSpent && that.projects[i].totalHrsSpent > 0) || !that.projects[i].newRow)) {
					enteredProjects.push(angular.copy(that.projects[i]));
				}
			}
			for (var i = 0; i < enteredProjects.length; i++) {
				if (enteredProjects[i].project == undefined || enteredProjects[i].task == undefined
					|| enteredProjects[i].project == null || enteredProjects[i].task == null
					|| enteredProjects[i].times[key] == undefined || enteredProjects[i].times[key] == null
					|| ((enteredProjects[i].times[key].hoursSpent == 0 && !enteredProjects[i].times[key].status) && enteredProjects[i].times[key].id == undefined)
					|| (enteredProjects[i].approver && enteredProjects[i].approver == false)) {
					continue;
				}
				if ((statusId === 1 || statusId === 4) && enteredProjects[i].times[key].status && ((!that.managerMode) && enteredProjects[i].times[key].status.id === 1 || enteredProjects[i].times[key].status.id === 2)) {
					continue;
				}
				if ((statusId === 2 || statusId === 3) && enteredProjects[i].times[key].status && (enteredProjects[i].times[key].status.id === 2 || enteredProjects[i].times[key].status.id === 3 || enteredProjects[i].times[key].status.id === 4)) {
					continue;
				}
				if (that.managerMode && that.managerProjects.indexOf(enteredProjects[i].project.id) < 0) {
					continue;
				}
				if ((statusId === 1 || statusId === 4) && (enteredProjects[i].times[key].day === 6 || enteredProjects[i].times[key].day === 0 || that.holidays.indexOf(key) >= 0)) {
					that.nonWorkingHrs = true;
				}
				var data = angular.copy(enteredProjects[i].times[key]);
				if ((statusId === 2 || statusId === 3) && (!data || !data.id)) {
					continue;
				}
				data.index = i + 1;
				data.date = key;
				data.description = enteredProjects[i].description;
				data.id = data.id || null;
				data.person = that.managerMode ? that.resource : PrincipalInfofactory.loginData;
				data.project = enteredProjects[i].project;
				data.status = {
					id: statusId
				};
				data.task = enteredProjects[i].task;
				if (!checkDuplicateEntries(dataToSend, data)) {
					dataToSend.push(data);
				}
			}
		});
		return dataToSend;
	}

	function checkDuplicateEntries(data, entry) {
		var isDuplicate = false;
		for (var i = 0; i < data.length; i++) {
			if (data[i].person.id === entry.person.id && data[i].task.id === entry.task.id && data[i].date === entry.date && data[i].project.id === entry.project.id) {
				isDuplicate = true;
			}
		}
		return isDuplicate;
	}

	function clearInputs() {
		loadTimeSheet();
	}

	/**
	 * This function calls a factory method to fetch the timesheet data of the selected week for the selected user on the approval screen.
	 */
	function fetchTimesheet(timeSheetForm) {
		that.dateOfJoining = that.resource.dateOfJoining;
		if (that.editMode) {
			var confirm = $mdDialog.confirm({
				templateUrl: "assets/confirm-box.template.html",
				parent: angular.element(document.body),
				locals: {
					title: appMessages.TIMESHEET_EDIT_CANCEL_TITLE,
					textContent: appMessages.TIMESHEET_EDIT_CANCEL_CONTENT,
					ok: 'Yes',
					cancel: 'No'
				}
			});

			$mdDialog.show(confirm).then(function () {
				that.editMode = false;
				timeSheetForm.$setSubmitted();
				if (timeSheetForm.$valid) {
					that.currentUser = that.resource.id;
					loadTimeSheet(currentDate);
				}
			});
		} else {
			timeSheetForm.$setSubmitted();
			if (timeSheetForm.$valid) {
				that.currentUser = that.resource.id;
				loadTimeSheet(currentDate);
			}
		}
	}

	/**
	 * 
	 * @param timeSheetForm 
	 * This function makes the timsheet grid editable for the manager if he/she needs to edit the entries of a particular user.
	 */
	function editTimesheet(timeSheetForm) {
		timeSheetForm.$setSubmitted();
		if (!that.editMode && timeSheetForm.$valid) {
			addRow();
			that.editMode = true;
		}
	}

	/**
	 * This function calls a factory method to approve the timesheet entries of the selected week.
	 */
	function approveEntries() {
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
				TimeSheetFactory.submitDay(dataToSend).then(function () {
					that.editMode = false;
					loadTimeSheet(currentDate);
					toastr.success(appMessages.APPROVE_SUCCESS);
				}, function (data) {
					toastr.error(data.data.message || appMessages.APPROVE_FAILURE, "Error");
				});
			});
		}
	}

	/**
	 * This function calls a factory method to reject the timesheet entries of the selected week.
	 */
	function rejectEntries() {
		var dataToSend = generateRequestData(3);
		if (dataToSend.length === 0) {
			toastr.info(appMessages.NO_SUBMITTED_ENTRIES_TO_APPROVE, "Information");
			return;
		} else {
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
				TimeSheetFactory.submitDay(dataToSend).then(function () {
					that.editMode = false;
					loadTimeSheet(currentDate);
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

	function removeRow(index, row, event) {
		if (row.newRow) {
			var keys = Object.keys(row.times);
			var column;
			keys.forEach(function (key) {
				column = row.times[key];
				updateTotal(key, "0", column.hoursSpent, column, event, row, true);
			});
			that.projects.splice(index, 1);
		} else {
			var entryIdList = [];
			var entries = Object.keys(row.times);
			entries.forEach(function (entry) {
				if (row.times[entry].status && (row.times[entry].status.id === 4 || row.times[entry].status.id === 3)) {
					entryIdList.push(row.times[entry].id);
				}
			});
			if (entryIdList.length > 0) {
				var confirm = $mdDialog.confirm({
					templateUrl: "assets/confirm-box.template.html",
					parent: angular.element(document.body),
					locals: {
						title: appMessages.TIMESHEET_DELETE_CONFIRM_TITLE,
						textContent: appMessages.TIMESHEET_DELETE_CONFIRM_CONTENT,
						ok: 'Yes',
						cancel: 'No'
					}
				});
				$mdDialog.show(confirm).then(function () {
					TimeSheetFactory.deleteSavedEntries(entryIdList).then(
						function (response) {
							if (that.managerMode) {
								that.editMode = false;
								loadTimeSheet(currentDate);
							} else {
								loadTimeSheet();
							}
							toastr.success(appMessages.TIMESHEET_DELETE_SUCCESS);
						},
						function (error) {
							toastr.error(appMessages.TIMESHEET_DELETE_FAILURE);
						}
					);
				});
			} else {
				toastr.info(appMessages.NO_SAVED_ENTRIES_TO_DELETE, "Information");
				return;
			}
		}
	}

	function enableSave() {
		that.saveEnabled = true;
	}

	angular.extend(this, {
		"changeMonth": changeMonth,
		"initTotal": initTotal,
		"updateTotal": updateTotal,
		"addRow": addRow,
		"submitColumn": submitColumn,
		"approve": approve,
		"getTaskList": getTaskList,
		"approveAll": approveAll,
		"isValidDate": isValidDate,
		"columnSelected": columnSelected,
		"setSelectedWeek": setSelectedWeek,
		"saveWeekEntry": saveWeekEntry,
		"submitWeekEntry": submitWeekEntry,
		"clearInputs": clearInputs,
		"fetchTimesheet": fetchTimesheet,
		"editTimesheet": editTimesheet,
		"approveEntries": approveEntries,
		"rejectEntries": rejectEntries,
		"removeRow": removeRow,
		"enableSave": enableSave
	});
};

angular
	.module('timeSheet')
	.controller('timesheetCtrl', timesheetCtrl);