/**
 *
 */
var projectCtrl = function (ProjectsFactory, ClientsFactory, PrincipalInfofactory, toastr, $stateParams, $rootScope, $state, $scope, $mdDialog) {
	var self = this;
	/**
	 * 
	 * @param firstPageFlag 
	 * This function gets invoked on project screen load. It fetches the list of all projects for which the user is project manager and all projects if the user is admin by calling factory method.
	 */
	function init(firstPageFlag) {
		ProjectsFactory.getAllProjects(PrincipalInfofactory.getId()).then(
			function (data) {
				self.tableData = data;
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
	 * This is the object having the project grid options which includes, columns, sorting and pagination configurations.
	 */
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
			displayName: 'Client Name',
			field: 'client.name',
			enableFiltering: true
		}, {
			displayName: 'Project ID',
			field: 'projectId',
			enableFiltering: true
		}, {
			displayName: 'Project Name',
			field: 'projectName',
			enableFiltering: true,
			sort: {
				direction: 'asc',
				priority: 0
			}
		}, {
			displayName: 'Start Date',
			field: 'startDate',
			cellFilter: 'dateFormat',
			enableFiltering: true
		}, {
			displayName: 'End Date',
			field: 'endDate',
			cellFilter: 'dateFormat',
			enableFiltering: true
		}, {
			name: 'Actions',
			headerCellClass: 'text-center',
			cellTemplate: '<span><md-tooltip md-direction="top">Edit</md-tooltip><i class="glyphicon glyphicon-pencil icon-edit-row" ng-click="grid.appScope.ctrl.editProject(row.entity)"></i></span>' +
				'<span><md-tooltip md-direction="top">Delete</md-tooltip><i class="glyphicon glyphicon-trash icon-delete-row" ng-click="grid.appScope.ctrl.deleteProject(row.entity)"></i></span>',
			cellClass: 'text-center',
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

	/**
	 * 
	 * @param rowData 
	 * This function is invoked on click of create new project and edit project,
	 * It opens a modal with the project creation form which will be empty while creating a new project and prepopulated if editing.
	 */
	function editProject(rowData) {
		$mdDialog.show({
			controller: projectFormCtrl,
			controllerAs: 'ctrl',
			templateUrl: './src/app/Project/projectForm.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: {
				data: rowData ? angular.copy(rowData) : { "projectStatus": true },
				mode: rowData ? 'edit' : 'create'
			},
			resolve: {
				clientList: function (ClientsFactory) {
					return ClientsFactory.getAllClients();
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
	 * @param rowData 
	 * This function is the event handler of delete project button. It opens a confirmation popup and calls a factory method to delete the project from DB on OK.
	 */
	function deleteProject(rowData) {
		/* var confirm = $mdDialog.confirm()
			.title(appMessages.DELETE_PROJECT_TITLE)
			.textContent(appMessages.DELETE_PROJECT_CONTENT)
			.ok('Yes')
			.cancel('No'); */
		var confirm = $mdDialog.confirm({
			templateUrl: "assets/confirm-box.template.html",
			parent: angular.element(document.body),
			locals: {
				title: appMessages.DELETE_PROJECT_TITLE,
				textContent: appMessages.DELETE_PROJECT_CONTENT,
				ok: 'Yes',
				cancel: 'No'
			}
		});

		$mdDialog.show(confirm).then(function () {
			rowData.projectStatus = false;
			ProjectsFactory.update(rowData).then(
				function (response) {
					toastr.success(appMessages.DELETE_PROJECT_SUCCESS);
					init(true);
				},
				function (error) {
					let msg = error.data.message || appMessages.DELETE_PROJECT_FAILURE;
					toastr.error(msg);
					init();
				}
			);
		});
	}

	/**
	 * 
	 * @param data 
	 * @param $mdDialog  
	 * @param clientList 
	 * @param mode 
	 * This is the controller for the project creation/edit form modal.
	 */
	function projectFormCtrl(data, $mdDialog, clientList, mode) {
		var that = this;
		that.clients = clientList;
		that.data = data;
		that.mode = mode;
		that.today = new Date();
		that.data.startDate = that.data.startDate && moment(that.data.startDate)._d;
		that.data.endDate = that.data.endDate && moment(that.data.endDate)._d;

		that.disableParentScroll = false;
		that.billingTypes = appConstants.billingTypes;

		function close(event) {
			$mdDialog.cancel();
		}

		function save(projectForm) {
			that.mode == "edit" ? update(projectForm) : create(projectForm);
		}

		/**
		 * 
		 * @param projectForm 
		 * This function validates the project creation form and calls the factor method to save the project if the form is valid.
		 */
		function create(projectForm) {
			projectForm.$setSubmitted();
			if (projectForm.$valid) {
				if (projectForm.$dirty) {
					data.createdBy = PrincipalInfofactory.getId();
					data.lastUpdatedBy = PrincipalInfofactory.getId();
					data.createdDate = new Date();
					data.lastUpdatedTime = new Date();

					ProjectsFactory.create(data).then(
						function (response) {
							$mdDialog.hide(true);
							toastr.success(appMessages.CREATE_PROJECT_SUCCESS);
						},
						function (error) {
							let msg = error.data.message || appMessages.CREATE_PROJECT_FAILURE;
							toastr.error(msg);
						}
					);
				} else {
					$mdDialog.cancel();
				}
			}
		}

		/**
		 * 
		 * @param projectForm 
		 * This function validates the project creation form and calls the factory method to update the project if the form is valid.
		 */
		function update(projectForm) {
			projectForm.$setSubmitted();
			if (projectForm.$valid) {
				if (projectForm.$dirty) {
					data.lastUpdatedBy = PrincipalInfofactory.getId();
					data.lastUpdatedTime = new Date();
					ProjectsFactory.update(data).then(
						function (response) {
							$mdDialog.hide(true);
							toastr.success(appMessages.UPDATE_PROJECT_SUCCESS);
						},
						function (error) {
							let msg = error.data.message || appMessages.UPDATE_PROJECT_FAILURE;
							toastr.error(msg);
						}
					);
				} else {
					$mdDialog.cancel();
				}
			}
		}

		/**
		 * 
		 * @param projectForm 
		 * This function clars the project creation form.
		 */
		function clearForm(projectForm) {
			that.data = {};
			projectForm.$setUntouched();
			projectForm.$setPristine();
		}
		angular.extend(this, {
			close: close,
			save: save,
			clearForm: clearForm
		});
	}

	angular.extend(this, {
		"editProject": editProject,
		"deleteProject": deleteProject
	});
}

angular
	.module('timeSheet')
	.controller('projectCtrl', projectCtrl);
