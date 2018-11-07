var reportSummaryCtrl = function (blukApprovalfactory, PrincipalInfofactory, projectList, uiGridExporterService, uiGridExporterConstants, $scope, TimeSheetFactory, MarkHolidaysFactory, $state) {
	var that = this;
	that.projectList = projectList;
	that.holidayList = [];
	if (PrincipalInfofactory.loginData.role.id === 1) {
		that.projectList.unshift({
			id: "-1",
			projectName: "All"
		});
	}
	that.projectIdList = [];
	that.projects = [];
	that.tableData = [];
	that.monthlyView = PrincipalInfofactory.monthlyView;
	PrincipalInfofactory.monthlyView = false;
	that.title = that.monthlyView ? "Monthly View" : "Report Summary";
	$scope.formatters = {};
	that.colDefs = [{
		displayName: 'Resource Name',
		field: 'userName',
		enableFiltering: false,
		enableSorting: true,
		cellTemplate: '<div><div ng-if="!col.grouping || col.grouping.groupPriority === undefined || col.grouping.groupPriority === null || ( row.groupHeader && col.grouping.groupPriority === row.treeLevel )" class="ui-grid-cell-contents">{{COL_FIELD CUSTOM_FILTERS}}</div></div>',
		grouping: {
			groupPriority: 0
		},
		width: 200,
		visible: !that.monthlyView
	}, {
		displayName: 'Monthly Hours Spent',
		field: 'totalHrsSpent',
		enableSorting: false,
		width: 100,
		cellTemplate: '<div class="ui-grid-cell-contents" title="TOOLTIP">{{grid.appScope.ctrl.getMonthlyHours(grid, row, col)}}</div>',
		visible: !that.monthlyView
	}, {
		displayName: 'Project Name',
		field: 'project.projectName',
		enableFiltering: false,
		enableSorting: false,
		width: 200
	}, {
		displayName: 'Task Id',
		field: 'task.taskId',
		enableFiltering: false,
		enableSorting: false,
		width: 200
	}, {
		displayName: 'Task Name',
		field: 'task',
		cellTemplate: '<div class="ui-grid-cell-contents"><span>{{COL_FIELD.taskName}}</span><span> </span><button ng-if="COL_FIELD.description" uib-popover-html="\'<div class=task-desc-content>{{COL_FIELD.description}}</div>\'" popover-title="Task Description" popover-append-to-body="true" popover-trigger="\'outsideClick\'" popover-placement="top" class="icon-btn"><i class="fa fa-info-circle text-info"></i></button></div>',
		enableFiltering: false,
		enableSorting: false,
		width: 200
	}];
	that.pmoReportCols = [{
		displayName: 'Date',
		field: 'date',
		width: 200
	}, {
		displayName: 'Project',
		field: 'project.projectName',
		width: 300
	}, {
		displayName: 'Task Name',
		field: 'task.taskName',
		width: 200
	}, {
		displayName: 'Task Id',
		field: 'task.taskId',
		width: 200
	}, {
		displayName: 'Task Description',
		field: 'task.description',
		width: 300
	}, {
		displayName: 'Hours Spent',
		field: 'hoursSpent',
		width: 200
	}, {
		displayName: 'Resource Name',
		field: 'person.empName',
		width: 200
	}, {
		displayName: 'Comments',
		field: 'comments',
		width: 350
	}];
	that.reportOptions = {
		enableColumnMenus: false,
		enableFiltering: false,
		data: that.tableData,
		rowHeight: 45,
		columnDefs: that.pmoReportCols,
		exporterColumnScaleFactor: 10,
		exporterExcelFilename: 'timesheetreport.xlsx',
		exporterExcelSheetName: 'report',
		exporterExcelCustomFormatters: function (grid, workbook, docDefinition) {

			var stylesheet = workbook.getStyleSheet();
			var borderStyle = stylesheet.createBorderFormatter({
				top: {
					style: "thin",
					color: "#00000000"
				},
				right: {
					style: "thin",
					color: "#00000000"
				},
				bottom: {
					style: "thin",
					color: "#00000000"
				},
				left: {
					style: "thin",
					color: "#00000000"
				}
			});

			var entryDefn = {
				"border": borderStyle.id,
				"alignment": { "wrapText": true, "vertical": "center", "horizontal": "center" }
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['entry'] = formatter;

			var boldStyle = stylesheet.createFontStyle({
				bold: true,
				color: "FFFFFFFF"
			});
			entryDefn = {
				"border": borderStyle.id,
				"alignment": { "wrapText": true, "vertical": "center", "horizontal": "center" },
				"font": boldStyle.id,
				"fill": { "type": "pattern", "patternType": "solid", "fgColor": "FF587CA0" }
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['header'] = formatter;
			$scope.formatters['headerCenter'] = formatter;
			$scope.formatters['headerRight'] = formatter;

			var fieldDefn = {
				"border": borderStyle.id,
				"alignment": { "wrapText": true, "vertical": "center", "horizontal": "left" }
			};
			formatter = stylesheet.createFormat(fieldDefn);
			$scope.formatters['field'] = formatter;

			docDefinition.styles = $scope.formatters;

			return docDefinition;
		},
		exporterFieldFormatCallback: function (grid, row, gridCol, cellValue) {
			return { metadata: { style: $scope.formatters['field'].id } };
		}
	};
	that.reportOptions.onRegisterApi = function (gridApi) {
		that.reportGridApi = gridApi;
	};


	/**
	 * This function sets the list of projects ids of which the report to be shown to the user.
	 */
	function onProjectSelect() {
		that.projectIdList = [];
		that.projectIdList = [that.selectedProject.id];
	}
	/**
	 * 
	 * @param reportForm 
	 * This function fetches the timesheet data of the selected month, selected project for all the users reporting to logged in user.
	 */
	function fetchTimesheet(reportForm) {
		reportForm.$setSubmitted();
		if (reportForm.$valid) {
			var firstDay = moment(that.selectedMonth).startOf('month').format("YYYY-MM-DD");
			var lastDay = moment(that.selectedMonth).endOf('month').format("YYYY-MM-DD");
			MarkHolidaysFactory.fetchHolidaysOfYear(moment(moment(firstDay).format("YYYY")).format("YYYY")).then(
				function (holidays) {
					for (var i = 0; i < holidays.length; i++) {
						that.holidayList.push(moment(holidays[i].holidayDate).format("YYYY-MM-DD"));
					}
				}
			);
			var req;
			if (that.monthlyView) {
				req = TimeSheetFactory.getTimeSheet(firstDay, lastDay, PrincipalInfofactory.getId());
			} else {
				blukApprovalfactory.fetchPMOReport(firstDay, lastDay, that.projectIdList[0]).then(
					function (responseData) {
						var pmoReportData = [];
						if (that.projectIdList[0] === "-1") {
							pmoReportData = responseData;
						} else {
							for (var i = 0; i < responseData.length; i++) {
								if (responseData[i].project.id === that.projectIdList[0] || responseData[i].project.globalProject) {
									pmoReportData.push(responseData[i]);
								}
							}
						}
						that.reportOptions.data = pmoReportData;
					}
				);
				req = blukApprovalfactory.fetchReportSummary(firstDay, lastDay, that.projectIdList[0]);
			}
			req.then(
				function (data) {
					if (that.monthlyView) {
						that.chartLabels = [];
						that.chartData = [];
						if (Object.keys(data.work).length === 0) {
							that.gridOptions.columnDefs = angular.copy(that.colDefs);
							that.gridOptions.data = [];
							return;
						}
						data = [data];
					}
					that.responseData = data;
					that.managerProjects = data && data.length > 0 ? data[0].projectIds : [];
					var projs = [];
					var attrs = [];
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
					}
					that.tableData = [];
					for (var i = 0; i < projs.length; i++) {
						if ((that.monthlyView || that.projectIdList.indexOf(projs[i].project.id) >= 0 || projs[i].project.globalProject || that.projectIdList[0] === "-1") && projs[i].totalHrsSpent > 0) {
							that.tableData.push(projs[i]);
						}
					}
					var days = projs && projs.length > 0 ? Object.keys(projs[0].times) : [];
					that.days = days;
					for (var i = 0; i < projs.length; i++) {
						for (var j = 0; j < days.length; j++) {
							projs[i][days[j]] = projs[i].times[days[j]];
							projs[i][days[j]].hoursSpent = projs[i][days[j]].hoursSpent || 0;
						}
					}
					that.gridOptions.columnDefs = angular.copy(that.colDefs);
					if (that.monthlyView) {
						that.gridOptions.columnDefs.push({
							displayName: 'Monthly Hours Spent',
							field: 'totalHrsSpent',
							enableSorting: false,
							width: 100,
							cellTemplate: '<div class="ui-grid-cell-contents">{{grid.appScope.ctrl.getMonthlyHours(grid, row, col)}}</div>'
						});
					}
					for (var i = 0; i < days.length; i++) {
						that.gridOptions.columnDefs.push({
							displayName: that.timeSheet[days[i]].displayDate,
							field: days[i],
							cellTemplate: '<md-tooltip md-direction="top" class="task-desc-tooltip" ng-if="row.entity.task && COL_FIELD != 0">{{COL_FIELD.comments || "No comments available"}}</md-tooltip><div class="ui-grid-cell-contents">{{COL_FIELD.hoursSpent}}</div>',
							enableFiltering: false,
							enableSorting: false,
							width: 75,
							cellClass: function (grid, row, col, rowRenderIndex, colRenderIndex) {
								var className = 'text-center time-box';
								var status;
								if (row.entity[col.field.split(".")[0]] && row.entity[col.field.split(".")[0]].status) {
									status = row.entity[col.field.split(".")[0]] && row.entity[col.field.split(".")[0]].status;
								}
								if (moment(col.field.split(".")[0]).format("e") === "0" || moment(col.field.split(".")[0]).format("e") === "6" || that.holidayList.indexOf(moment(col.field.split(".")[0]).format("YYYY-MM-DD")) >= 0) {
									className += ' weekend';
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
								return className;
							}
						});
					}
					if (that.monthlyView) {
						that.chartLabels = [];
						that.chartData = [];
						function getRandomColorsList(count) {
							var colors = [];
							for (var i = 0; i < count; i++) {
								colors.push('#' + (Math.random() * 2).toString(16).substr(2, 6));
							}
							return colors;
						}
						for (var i = 0; i < that.tableData.length; i++) {
							that.chartData.push(that.tableData[i].totalHrsSpent);
							that.chartLabels.push(that.tableData[i].project.projectName + " - " + that.tableData[i].task.taskId + " - " + that.tableData[i].task.taskName);
						}
						that.chartColors = getRandomColorsList(that.chartData.length);
						that.dataSets = {
							backgroundColor: ['#2a7dd3', '#FDB45C', '#5a7b17', '#a3daff', '#e52d2d', '#edeb78', '#5c415d'],
							hoverBackgroundColor: ['#2a7dd3', '#FDB45C', '#5a7b17', '#a3daff', '#e52d2d', '#edeb78', '#5c415d']
							//backgroundColor: ['#0055ff','#d0e88f','#ff0000','#9bedf2','#000022','#edeb78'],
							//hoverBackgroundColor: ['#0055ff','#d0e88f','#ff0000','#9bedf2','#000022','#edeb78']
						}
						that.chartOptions = {
							legend: {
								display: true,
								fullWidth: false
							},
							tooltips: {
								callbacks: {
									label: function (tooltipItem, data) {
										var label = "   " + data.labels[tooltipItem.index] + " : " + data.datasets[0].data[tooltipItem.index] + " hours";
										return label;
									}
								}
							}
						}
					}
					that.gridOptions.data = that.tableData;
					that.gridApi.grid.clearAllFilters();
				}
			);
		}
	}

	function initialMonthlyView(reportForm) {
		that.selectedMonth = new Date();
		if (that.monthlyView) {
			fetchTimesheet(reportForm);
		}
	}

	/**
	 * This function creates the JSON for the grid to show the timesheet of the selected week.
	 */
	function generateDateForMonth() {
		var firstDay = moment(that.selectedMonth).startOf('month').toDate();
		var lastDay = moment(that.selectedMonth).endOf('month').toDate();
		that.timeSheet = {};
		for (var i = firstDay; i <= lastDay; i.setDate(i.getDate() + 1)) {
			var date = moment(i);
			var key = date.format("YYYY-MM-DD");
			var columns = {
				displayDate: date.format("DD-MMM"),
				day: date.weekday(),
				approval: false,
				submission: false
			}
			that.timeSheet[key] = columns;
		}
		for (var i = 0; i < that.projects.length; i++) {
			that.projects[i].times = angular.copy(that.timeSheet);
			that.projects[i].userName = that.name;
			that.projects[i].userId = that.userId;
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
		var project;
		for (var key in that.timeSheetData.work) {
			for (var i = 0; i < that.timeSheetData.work[key].length; i++) {
				for (var k = 0; k < that.projects.length; k++) {
					if (that.projects[k].project.id === that.timeSheetData.work[key][i].project.id && that.projects[k].task.id === that.timeSheetData.work[key][i].task.id) {
						project = that.projects[k];
						break;
					}
				}
				project.times[key] = that.timeSheetData.work[key][i];
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
				spentHours += times[key].hoursSpent || 0;
			});
			row.totalHrsSpent = spentHours;
		});
	}

	/**
	 * This is the object having options of the report summary table.
	 */
	that.gridOptions = {
		enableColumnMenus: false,
		enableFiltering: false,
		data: that.tableData,
		rowHeight: 45,
		columnDefs: that.colDefs,
		exporterColumnScaleFactor: 10,
		exporterExcelFilename: 'timesheetreport.xlsx',
		exporterExcelSheetName: 'report',
		exporterExcelCustomFormatters: function (grid, workbook, docDefinition) {

			var stylesheet = workbook.getStyleSheet();
			var borderStyle = stylesheet.createBorderFormatter({
				top: {
					style: "thin",
					color: "#00000000"
				},
				right: {
					style: "thin",
					color: "#00000000"
				},
				bottom: {
					style: "thin",
					color: "#00000000"
				},
				left: {
					style: "thin",
					color: "#00000000"
				}
			});

			var entryDefn = {
				"border": borderStyle.id,
				"alignment": { "wrapText": true, "vertical": "center", "horizontal": "center" }
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['entry'] = formatter;

			/**
			 * Below is the formatter for columns with saved hours
			 */
			entryDefn.fill = {
				"type": "pattern",
				"patternType": "solid",
				"fgColor": "AAB6DAF2"
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['entrySaved'] = formatter;

			/**
			 * Below is the formatter for columns with submitted hours
			 */
			entryDefn.fill = {
				"type": "pattern",
				"patternType": "solid",
				"fgColor": "FFF9FBA6"
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['entrySubmitted'] = formatter;

			/**
			 * Below is the formatter for columns with approved hours
			 */
			entryDefn.fill = {
				"type": "pattern",
				"patternType": "solid",
				"fgColor": "FF89E5B0"
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['entryApproved'] = formatter;

			/**
			 * Below is the formatter for columns with rejected hours
			 */
			entryDefn.fill = {
				"type": "pattern",
				"patternType": "solid",
				"fgColor": "FFFCC3C3"
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['entryRejected'] = formatter;

			var boldStyle = stylesheet.createFontStyle({
				bold: true,
				color: "FFFFFFFF"
			});
			entryDefn = {
				"border": borderStyle.id,
				"alignment": { "wrapText": true, "vertical": "center", "horizontal": "center" },
				"font": boldStyle.id,
				"fill": { "type": "pattern", "patternType": "solid", "fgColor": "FF587CA0" }
			};
			formatter = stylesheet.createFormat(entryDefn);
			$scope.formatters['header'] = formatter;
			$scope.formatters['headerCenter'] = formatter;
			$scope.formatters['headerRight'] = formatter;

			var fieldDefn = {
				"border": borderStyle.id,
				"alignment": { "wrapText": true, "vertical": "center", "horizontal": "left" }
			};
			formatter = stylesheet.createFormat(fieldDefn);
			$scope.formatters['field'] = formatter;

			docDefinition.styles = $scope.formatters;

			return docDefinition;
		},
		exporterFieldFormatCallback: function (grid, row, gridCol, cellValue) {
			var formatterId = null;

			switch (gridCol.name) {
				case 'userName':
				case 'totalHrsSpent':
				case 'project.projectName':
				case 'task.taskId':
				case 'task.taskName':
					formatterId = $scope.formatters['field'].id;
					break;
				default:
					var col = gridCol;
					var status = row.entity[col.field.split(".")[0]] && row.entity[col.field.split(".")[0]].status || {};
					switch (status.id) {
						case 1:
							formatterId = $scope.formatters['entrySubmitted'].id;
							break;
						case 2:
							formatterId = $scope.formatters['entryApproved'].id;
							break;
						case 3:
							formatterId = $scope.formatters['entryRejected'].id;
							break;
						case 4:
							formatterId = $scope.formatters['entrySaved'].id;
							break;
						default:
							formatterId = $scope.formatters['entry'].id;
					}
			}
			if (formatterId) {
				return { metadata: { style: formatterId } };
			} else {
				return null;
			}
		},
		exporterFieldCallback: function (grid, row, col, input) {
			if (col.name === 'task') {
				return input.taskName;
			} else if (that.days && that.days.length > 0 && that.days.indexOf(col.name) >= 0) {
				return input.hoursSpent;
			} else {
				return input;
			}
		}
	}
	that.gridOptions.onRegisterApi = function (gridApi) {
		that.gridApi = gridApi;
	}

	/**
	 * 
	 * @param grid 
	 * @param row 
	 * @param column 
	 * This function calculates the total hours spent by the user in the selected month for particular project/task.
	 */
	function getMonthlyHours(grid, row, column) {
		if (row.treeLevel === 0) {
			var hrs = 0;
			for (var i = 0; i < row.treeNode.children.length; i++) {
				hrs += (row.treeNode.children[i].row.entity && row.treeNode.children[i].row.entity.totalHrsSpent) ? row.treeNode.children[i].row.entity.totalHrsSpent : 0;
			}
			return hrs;
		} else {
			return row.entity[column.field];
		}
	}

	function exportExcel() {
		var rowTypes = uiGridExporterConstants.ALL;
		var colTypes = uiGridExporterConstants.ALL;
		var grid = this.gridApi.grid;
		grid.options.exporterExcelFilename = "TS_Report_" + this.selectedProject.projectName + "_" + moment(this.selectedMonth).format("MMMM_YYYY") + ".xlsx";
		uiGridExporterService.excelExport(grid, rowTypes, colTypes);
	}

	function exportPMOReport() {
		var rowTypes = uiGridExporterConstants.ALL;
		var colTypes = uiGridExporterConstants.ALL;
		var grid = this.reportGridApi.grid;
		grid.options.exporterExcelFilename = "TS_Report_" + this.selectedProject.projectName + "_" + moment(this.selectedMonth).format("MMMM_YYYY") + ".xlsx";
		uiGridExporterService.excelExport(grid, rowTypes, colTypes);
	}

	function goToChart() {
		var currentUrl = window.location.href;
		var chartIndex = currentUrl.indexOf("#chart");
		if (chartIndex >= 0) {
			window.location = window.location.href.slice(0, chartIndex);
		}
		setTimeout(function () {
			window.location = window.location.href + "#chart";
		}, 10);
	}

	angular.extend(this, {
		"onProjectSelect": onProjectSelect,
		"fetchTimesheet": fetchTimesheet,
		"getMonthlyHours": getMonthlyHours,
		"export": exportExcel,
		"initialMonthlyView": initialMonthlyView,
		"exportPMOReport": exportPMOReport,
		"goToChart": goToChart
	});
};
angular.module('timeSheet').controller('reportSummaryCtrl', reportSummaryCtrl);