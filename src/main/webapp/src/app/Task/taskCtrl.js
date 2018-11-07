var taskCtrl = function ($state, $scope, $rootScope, TasksetupFactory, toastr, $mdDialog, ResourcemapFactory, ResourceFactory) {

	var self = this;
	self.title = "Tasks";
	/**
	 * 
	 * @param firstPageFlag 
	 * This function gets invoked on task screen load. It fetches the list of all tasks for which the user is project manager/team lead and all tasks if the user is admin by calling factory method.
	 */
	function init(firstPageFlag) {
		TasksetupFactory.getAllTasks().then(
			function (data) {
				var userList = [];
				var userIdList = [];
				self.tableData = [];
				for (var j = 0; j < data.length; j++) {
					userList = [];
					userIdList = [];
					if (data[j].assignedUsers && data[j].assignedUsers.length > 0) {
						for (var i = 0; i < data[j].assignedUsers.length; i++) {
							userList.push(data[j].assignedUsers[i].empName);
							userIdList.push(data[j].assignedUsers[i].id);
						}
					}
					data[j].assignedUserNames = userList.join(",");
					data[j].assignedUserIdList = userIdList;
					if(moment(data[j].project.endDate) >= moment(moment().format("YYYY-MM-DD"))){
						self.tableData.push(data[j]);
					}
				}
				self.gridOptions.data = self.tableData;
				self.gridApi.grid.clearAllFilters();
				if (firstPageFlag && self.gridApi.pagination.getPage() !== 1) {
					self.gridApi.pagination.seek(1);
				} else {
					var rowCount = self.gridApi.pagination.getLastRowIndex() - self.gridApi.pagination.getFirstRowIndex() + 1;
					if (self.gridApi.pagination.getPage() === 1) {
						rowCount = Math.min(10, data.length);
					} else {
						rowCount = Math.min(rowCount, data.length);
					}
					angular.element(document.getElementsByClassName('grid')[0]).css('height', ((rowCount + 6) * 30) + 'px');
				}
			}
		);
	}
	init();

	/**
	 * 
	 * @param rowData 
	 * This function is invoked on click of create new task and edit task,
	 * It opens a modal with the task creation form which will be empty while creating a new task and prepopulated if editing.
	 */
	function openTaskModal(rowData) {
		$mdDialog.show({
			controller: taskFormCtrl,
			controllerAs: 'ctrl',
			templateUrl: './src/app/Task/taskForm.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: {
				data: rowData ? angular.copy(rowData) : { "status": true },
				mode: rowData ? 'edit' : 'create'
			},
			resolve: {
				pageData: function (ResourcemapFactory) {
					return ResourcemapFactory.getPageData();
				},
				personList: function (ResourceFactory) {
					return ResourceFactory.all();
				},
				projectList: function (TasksetupFactory) {
					return TasksetupFactory.getAllProjects();
				},
				roleId: function (TasksetupFactory) {
					if (rowData) {
						return TasksetupFactory.getRoleInProject(rowData.project.id, $rootScope.user.id);
					} else {
						return $rootScope.user.role.id;
					}
				}
			}
		}).then(function (data) {
			var flag = rowData ? false : true;
			init(flag);
		}, function (data) {

		});
	}
	/**
	 * 
	 * @param data 
	 * @param mode 
	 * @param pageData 
	 * @param $mdDialog  
	 * @param personList 
	 * @param projectList 
	 * This is the controller for the task creation/edit form modal.
	 */
	function taskFormCtrl(data, mode, pageData, $mdDialog, personList, projectList, roleId) {
		var that = this;
		that.projects = projectList;
		that.project = data.project;
		that.resources = [];
		that.selectAll = false;
		that.data = data;
		that.selectedResources = [];
		that.mode = mode;
		that.userRoleId = roleId;
		if(mode === 'create'){
			that.data.active = true;
		}

		function close(event) {
			$mdDialog.cancel();
		}
		function clearForm(taskForm) {
			that.data = {};
			that.resourceGridOptions.data = [];
			that.resources = [];
			taskForm.$setUntouched();
			taskForm.$setPristine();
		}
		/**
		 * 
		 * @param taskForm 
		 * This function validates the task creation form and calls a factory method to add a task to DB if the form is valid.
		 */
		function save(taskForm) {
			that.selectedResources = [];
			for (var i = 0; i < that.resources.length; i++) {
				if (that.resources[i].selected) {
					that.selectedResources.push(that.resources[i]);
				}
			}
			taskForm.$setSubmitted();
			if (taskForm.$valid) {
				var reqBody = {
					"project": that.data.project,
					"taskName": that.data.taskName,
					"taskId": that.data.taskId,
					"estimatedHours": that.data.estimatedHours,
					"assignedUsers": that.selectedResources,
					"status": true,
					"description": that.data.description,
					"active": that.data.active
				};
				if (mode === 'edit') {
					that.data.assignedUsers = that.selectedResources;
					TasksetupFactory.updateTask(that.data).then(
						function (response) {
							$mdDialog.hide(true);
							toastr.success(appMessages.UPDATE_TASK_SUCCESS);
						},
						function (error) {
							let msg = error.data.message || appMessages.UPDATE_TASK_FAILURE;
							toastr.error(msg);
						}
					);
				} else if (mode === 'create') {
					TasksetupFactory.saveTask(reqBody).then(
						function (response) {
							$mdDialog.hide(true);
							toastr.success(appMessages.CREATE_TASK_SUCCESS);
						},
						function (error) {
							let msg = error.data.message || appMessages.CREATE_TASK_FAILURE;
							toastr.error(msg);
						}
					);
				}
			}
		}
		/**
		 * This function loads the list of resources assigned to the selected project before creating the task.
		 */
		function onProjectSelect() {
			that.selectAll = false;
			var allocatedResources = pageData.assignedResource[that.data.project.id] || [];
			var allocatedPersonIds = [];
			that.resources = [];
			for (var i = 0; i < allocatedResources.length; i++) {
				allocatedPersonIds.push(allocatedResources[i].personId);
			}
			for (var i = 0; i < personList.length; i++) {
				if (allocatedPersonIds.indexOf(personList[i].id) >= 0) {
					that.resources.push(angular.copy(personList[i]));
				}
			}
			that.resourceGridOptions.data = that.resources || [];
		}
		function onOwnerSelect() {
			for (var i = 0; i < that.resources.length; i++) {
				if (that.resources[i].personId === that.data.taskOwner.personId) {
					that.resources[i].selected = true;
					that.resourceGridOptions.data = that.resources
					break;
				}
			}
		}
		function assignAll() {
			for (var i = 0; i < that.resources.length; i++) {
				if (!(that.data.taskOwner && that.resources[i].personId === that.data.taskOwner.personId)) {
					that.resources[i].selected = that.selectAll;
				}
			}
			that.resourceGridOptions.data = that.resources;
		}
		that.resourceGridOptions = {
			enableColumnMenus: false,
			enableHorizontalScrollbar: 0,
			data: that.resources,
			enableFiltering: true,
			columnDefs: [{
				name: "Assign",
				headerCellClass: 'text-center',
				cellClass: 'text-center',
				cellTemplate: "<input type='checkbox' ng-model='row.entity.selected' />",
				enableSorting: false,
				enableFiltering: false,
				maxWidth: 100
			}, {
				displayName: 'Resource Name',
				field: 'empName',
				enableFiltering: true,
				sort: {
					direction: 'asc',
					priority: 0
				}
			}]
		};
		that.resourceGridOptions.onRegisterApi = function (gridApi) {
			if (that.project) {
				onProjectSelect();
				for (var i = 0; i < that.resources.length; i++) {
					if (that.data.assignedUserIdList.indexOf(that.resources[i].id) >= 0) {
						that.resources[i].selected = true;
					}
				}
				that.resourceGridOptions.data = that.resources;
			}
		}
		angular.extend(this, {
			"close": close,
			"clearForm": clearForm,
			"save": save,
			"onProjectSelect": onProjectSelect,
			"onOwnerSelect": onOwnerSelect,
			"assignAll": assignAll
		});
	}
	/**
	 * 
	 * @param rowData 
	 * This function is the event handler of delete task button. It opens a confirmation popup and calls a factory method to delete the task from DB on OK.
	 */
	function deleteTask(rowData) {
		var confirm = $mdDialog.confirm({
			templateUrl: "assets/confirm-box.template.html",
			parent: angular.element(document.body),
			locals: {
				title: appMessages.DELETE_TASK_TITLE,
				textContent: appMessages.DELETE_TASK_CONTENT,
				ok: 'Yes',
				cancel: 'No'
			}
		});

		$mdDialog.show(confirm).then(function () {
			TasksetupFactory.deleteTask(rowData.id).then(
				function (response) {
					toastr.success(appMessages.DELETE_TASK_SUCCESS);
					init(true);
				},
				function (error) {
					let msg = error.data.message || appMessages.CREATE_TASK_FAILURE;
					toastr.error(msg);
				}
			);
		});
	}

	self.gridOptions = {
		paginationPageSize: 10,
		minRowsToShow: 14,
		enableColumnMenus: false,
		enableHorizontalScrollbar: 0,
		enableVerticalScrollbar: 0,
		enableFiltering: true,
		data: self.tableData,
		paginationTemplate: 'assets/pagination.template.html',
		columnDefs: [{
			displayName: 'Project Name',
			field: 'project.projectName',
			enableFiltering: true,
			sort: {
				direction: 'asc',
				priority: 0
			}
		}, {
			displayName: 'Task ID',
			field: 'taskId',
			enableFiltering: true,
			width: 250
		}, {
			displayName: 'Task Name',
			field: 'taskName',
			enableFiltering: true
		}, {
			displayName: 'Assigned Resources',
			field: 'assignedUserNames',
			enableFiltering: true
		}, {
			displayName: 'Active?',
			field: 'active',
			cellTemplate: '<span>{{COL_FIELD ? "Yes" : "No"}}</span>',
			enableFiltering: true,
			cellClass: 'text-center',
			width: 150,
			headerCellClass: 'text-center'
		}, {
			name: 'Actions',
			headerCellClass: 'text-center',
			cellTemplate: '<span><md-tooltip md-direction="top">Edit</md-tooltip><i class="glyphicon glyphicon-pencil icon-edit-row" ng-click="grid.appScope.taskCtrl.openTaskModal(row.entity)"></i></span>' +
				'<span><md-tooltip md-direction="top">Delete</md-tooltip><i class="glyphicon glyphicon-trash icon-delete-row" ng-click="grid.appScope.taskCtrl.deleteTask(row.entity)"></i></span>',
			cellClass: 'text-center action-icons',
			enableSorting: false,
			enableFiltering: false
		}]
	}
	self.gridOptions.onRegisterApi = function (gridApi) {
		self.gridApi = gridApi;
		self.gridApi.pagination.on.paginationChanged($scope, function (pageNumber, pageSize) {
			if (self.tableData.length - (pageNumber * pageSize) < 0) {
				var rowCount = pageSize + (self.tableData.length - (pageNumber * pageSize));
				angular.element(document.getElementsByClassName('grid')[0]).css('height', ((rowCount + 6) * 30) + 'px');
			} else {
				angular.element(document.getElementsByClassName('grid')[0]).css('height', ((pageSize + 6) * 30) + 'px');
			}
		});
	}

	angular.extend(this, {
		"openTaskModal": openTaskModal,
		"deleteTask": deleteTask
	});


}
angular.module('timeSheet').controller('taskCtrl', taskCtrl);