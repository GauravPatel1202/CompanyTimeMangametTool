function resourcemapCtrl(ResourcemapFactory, PrincipalInfofactory, toastr, $mdDialog, $scope, pageData) {
	var self = this;
	self.projects = pageData.project || [];
	self.project = {};
	self.tableData = [];
	self.resourceMap = pageData.assignedResource || [];
	function getAvailableRoles(){
		var userRoles = [];
		for (var i = 0; i < pageData.role.length; i++) {
			if (pageData.role[i].id > PrincipalInfofactory.loginData.role.id) {
				userRoles.push(pageData.role[i]);
			}
		}
		pageData.role = userRoles;
	}
	getAvailableRoles();
	self.userRole = PrincipalInfofactory.loginData.role.id;

	/**
	 * This is the object having the resource mapping grid options which includes, columns, sorting and pagination configurations.
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
			displayName: 'Resource ID',
			field: 'empId',
			enableFiltering: true
		}, {
			displayName: 'Resource Name',
			field: 'empName',
			enableFiltering: true,
			sort: {
				direction: 'asc',
				priority: 0
			}
		}, {
			displayName: 'Role',
			field: 'roleName',
			enableFiltering: true
		}, {
			name: 'Actions',
			headerCellClass: 'text-center',
			cellTemplate: '<span><md-tooltip md-direction="top">Edit</md-tooltip><i class="glyphicon glyphicon-pencil icon-edit-row" ng-click="grid.appScope.ctrl.openModal(row.entity, projectForm)"></i></span>' +
				'<span><md-tooltip md-direction="top">Delete</md-tooltip><i class="glyphicon glyphicon-trash icon-delete-row" ng-click="grid.appScope.ctrl.removeMapping(row.entity)"></i></span>',
			cellClass: 'text-center action-icons',
			enableSorting: false,
			enableFiltering: false,
			visible: PrincipalInfofactory.loginData.role.id === 1
		}]
	}
	self.gridOptions.multiSelect = true;
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
	 * @param projectForm 
	 * This function is invoked on click of assign new resource or edit button.
	 */
	function openModal(rowData, projectForm) {
		pageData.person = pageData.person || [];
		projectForm && projectForm.$setSubmitted();
		if ((projectForm && projectForm.$valid) || rowData) {
			var availableresources = [];
			var allocatedResourceIds = [];
			if (pageData.assignedResource && pageData.assignedResource[self.project.id] && pageData.assignedResource[self.project.id].length > 0) {
				for (var i = 0; i < pageData.assignedResource[self.project.id].length; i++) {
					allocatedResourceIds.push(pageData.assignedResource[self.project.id][i].empId);
				}
				for (var i = 0; i < pageData.person.length; i++) {
					if (allocatedResourceIds.indexOf(pageData.person[i].empId) < 0) {
						availableresources.push(pageData.person[i]);
					}
				}
			} else {
				availableresources = pageData.person;
			}
			var modalData = {
				project: angular.copy(self.project),
				projects: angular.copy(self.projects),
				resources: rowData ? angular.copy([rowData]) : angular.copy(availableresources),
				resourceAllocation: rowData ? rowData.resourceAllocation : true,
				roles: angular.copy(pageData.role),
				resource: rowData ? angular.copy(rowData.personId) : {},
				role: rowData ? angular.copy(rowData.roleId) : {},
				rowData: angular.copy(rowData)
			};
			$mdDialog.show({
				controller: resourceMapFormCtrl,
				controllerAs: 'ctrl',
				templateUrl: './src/app/Resource-Mapping/resource-map-form.html',
				parent: angular.element(document.body),
				clickOutsideToClose: false,
				locals: {
					data: modalData,
					mode: rowData ? 'edit' : 'create'
				},
				resolve: {}
			}).then(function (data) {

			}, function (data) {

			});
		}
	}

	/**
	 * 
	 * @param data 
	 * @param $mdDialog 
	 * @param mode 
	 * This is the controller function for the resource mapping modal.
	 */
	function resourceMapFormCtrl(data, $mdDialog, mode) {
		var that = this;
		that.project = data.project;
		that.resource = data.resource;
		that.resourceAllocation = data.resourceAllocation;
		that.mode = mode;
		that.data = data;
		that.roles = data.roles;
		that.resources = data.resources;
		that.resources.sort(function(a,b){
			return a.empName >= b.empName ? 1 : -1;
		});
		that.role = data.role;

		function close() {
			$mdDialog.hide();
		}

		/**
		 * 
		 * @param resourceMapForm 
		 * This function validates the form and calls a factory method to assign a resource to the selected project.
		 */
		function mapResource(resourceMapForm) {
			resourceMapForm.$setSubmitted();
			if (resourceMapForm.$valid) {
				var reqBody = [{
					"projectId": that.project.id,
					"personId": that.resource,
					"roleId": that.role,
					"resourceAllocation": that.resourceAllocation
				}];
				ResourcemapFactory.mapResource(reqBody).then(
					function (data) {
						pageData = data;
						getAvailableRoles();
						$mdDialog.hide(true);
						toastr.success(appMessages.MAP_RESOURCE_SUCCESS);
						self.resourceMap = data.assignedResource;
						self.tableData = self.resourceMap[self.project.id] || [];
						self.gridOptions.data = self.tableData;
						self.gridApi.grid.clearAllFilters();
						self.gridApi.pagination.seek(1);
						var rowCount = Math.min(10, self.tableData.length);
						angular.element(document.getElementsByClassName('grid')[0]).css('height', ((rowCount + 6) * 30) + 'px');
					},
					function (error) {
						var errMsg = error.data.genericResponseData.message || appMessages.MAP_RESOURCE_FAILURE;
						toastr.error(errMsg);
					}
				);
			}
		}

		/**
		 * 
		 * @param resourceMapForm 
		 * This function calls a factory method to update a existing mapping of the user with a project.
		 */
		function updateMapping(resourceMapForm) {
			var reqBody = data.rowData;
			reqBody.roleId = that.role;
			reqBody.resourceAllocation = that.resourceAllocation;
			ResourcemapFactory.updateResourceMapping([reqBody]).then(
				function (data) {
					pageData = data;
					getAvailableRoles();
					$mdDialog.hide(true);
					toastr.success(appMessages.UPDATE_RESOURCE_MAP_SUCCESS);
					self.resourceMap = data.assignedResource;
					self.tableData = self.resourceMap[self.project.id] || [];
					self.gridOptions.data = self.tableData;
					self.gridApi.grid.clearAllFilters();
					var rowCount = self.gridApi.pagination.getLastRowIndex() - self.gridApi.pagination.getFirstRowIndex() + 1;
					if (self.gridApi.pagination.getPage() === 1) {
						rowCount = Math.min(10, self.tableData.length);
					} else {
						rowCount = Math.min(rowCount, self.tableData.length);
					}
					angular.element(document.getElementsByClassName('grid')[0]).css('height', ((rowCount + 6) * 30) + 'px');
				},
				function (error) {
					var errMsg = error.data.genericResponseData.message || appMessages.UPDATE_RESOURCE_MAP_FAILURE;
					toastr.error(errMsg);
				}
			);
		}

		angular.extend(this, {
			'close': close,
			'mapResource': mapResource,
			'updateMapping': updateMapping
		});
	}

	/**
	 * 
	 * @param rowData 
	 * This is the event handler for remove mapping button of each row. This function opens a confirmation pop up and deletes the mapping from DB if OK.
	 */
	function removeMapping(rowData) {
		/* var confirm = $mdDialog.confirm()
			.htmlContent(appMessages.UNMAP_RESOURCE_TEMPLATE)
			.ok('Yes')
			.cancel('No'); */
		var confirm = $mdDialog.confirm({
			templateUrl: "assets/confirm-box.template.html",
			parent: angular.element(document.body),
			locals: {
				htmlContent: appMessages.UNMAP_RESOURCE_TEMPLATE,
				ok: 'Yes',
				cancel: 'No'
			}
		});

		$mdDialog.show(confirm).then(function () {
			ResourcemapFactory.unmapResource([rowData]).then(
				function (data) {
					pageData = data;
					getAvailableRoles();
					$mdDialog.hide(true);
					toastr.success(appMessages.UNMAP_RESOURCE_SUCCESS);
					self.resourceMap = data.assignedResource;
					self.tableData = (self.resourceMap && self.resourceMap[self.project.id]) || [];
					self.gridOptions.data = self.tableData;
					self.gridApi.grid.clearAllFilters();
					self.gridApi.pagination.seek(1);
					var rowCount = Math.min(10, self.tableData.length);
					angular.element(document.getElementsByClassName('grid')[0]).css('height', ((rowCount + 6) * 30) + 'px');
				},
				function (error) {
					toastr.error(appMessages.UNMAP_RESOURCE_FAILURE);
				}
			);
		});
	}

	/**
	 * This is the event handler of on change event on the project dropdown. This function loads the resource mappings of the selected project in the grid.
	 */
	function onProjectSelect() {
		self.tableData = (self.resourceMap && self.resourceMap[self.project.id]) || [];
		self.gridOptions.data = self.tableData;
		self.gridApi.grid.clearAllFilters();
		self.gridApi.pagination.seek(1);
		var rowCount = Math.min(10, self.tableData.length);
		angular.element(document.getElementsByClassName('grid')[0]).css('height', ((rowCount + 6) * 30) + 'px');
	}

	angular.extend(this, {
		"openModal": openModal,
		"onProjectSelect": onProjectSelect,
		"removeMapping": removeMapping
	});
}
angular.module("timeSheet").controller("resourcemapCtrl", resourcemapCtrl);