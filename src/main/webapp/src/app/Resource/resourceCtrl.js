/**
 *
 */
var resourceCtrl = function (ResourceFactory, ResourcemapFactory, PrincipalInfofactory, toastr, $stateParams, $rootScope, $state, $scope, $mdDialog) {
	var self = this;
	/**
	 * 
	 * @param firstPageFlag 
	 * This is the initial function which calls a factory service to fetch the list of all resources from database.
	 */
	function init(firstPageFlag) {
		ResourceFactory.all(PrincipalInfofactory.getId()).then(
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
	 * This is the object having the resource grid options which includes, columns, sorting and pagination configurations.
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
			displayName: 'ID',
			field: 'empId',
			enableFiltering: true
		}, {
			displayName: 'Name',
			field: 'empName',
			enableFiltering: true,
			sort: {
				direction: 'asc',
				priority: 0
			}
		}, {
			displayName: 'Designation',
			field: 'designation.designation',
			enableFiltering: true
		}, {
			displayName: 'Skills',
			field: 'skill',
			enableFiltering: true
		}, {
			name: 'Actions',
			cellTemplate: '<span><md-tooltip md-direction="top">Edit</md-tooltip><i class="glyphicon glyphicon-pencil icon-edit-row" ng-click="grid.appScope.ctrl.openModal(row.entity)"></i></span>' +
				'<span><md-tooltip md-direction="top">Delete</md-tooltip><i class="glyphicon glyphicon-trash icon-delete-row" ng-click="grid.appScope.ctrl.remove(row.entity)"></i></span>',
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
	 * This function is invoked on click of create new resource and edit resource,
	 * It opens a modal with the resource creation form which will be empty while creating a new resource and prepopulated if editing.
	 */
	function openModal(rowData) {
		$mdDialog.show({
			controller: resourceFormCtrl,
			controllerAs: 'ctrl',
			templateUrl: './src/app/Resource/resourceForm.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: {
				data: rowData ? angular.copy(rowData) : { "status": true },
				mode: rowData ? 'edit' : 'create'
			},
			resolve: {
				designations: function (ResourceFactory) {
					return ResourceFactory.getAllDesignations();
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
	 * This function is the event handler of delete resource button. It opens a confirmation popup and calls a factory method to delete the resource from DB on OK.
	 */
	function remove(rowData) {
		/* var confirm = $mdDialog.confirm()
			.title(appMessages.DELETE_RESOURCE_TITLE)
			.textContent(appMessages.DELETE_RESOURCE_CONTENT)
			.ok('Yes')
			.cancel('No'); */
		if (rowData.role.id === 1) {
			toastr.error(appMessages.CANNOT_DELETE_ADMIN);
			return;
		}
		var confirm = $mdDialog.confirm({
			templateUrl: "assets/confirm-box.template.html",
			parent: angular.element(document.body),
			locals: {
				title: appMessages.DELETE_RESOURCE_TITLE,
				textContent: appMessages.DELETE_RESOURCE_CONTENT,
				ok: 'Yes',
				cancel: 'No'
			}
		});

		$mdDialog.show(confirm).then(function () {
			let role = PrincipalInfofactory.getUserRole();
			rowData.status = false;
			ResourceFactory.update(rowData).then(
				function (response) {
					toastr.success(appMessages.DELETE_RESOURCE_SUCCESS);
					init(true);
				},
				function (error) {
					toastr.error(appMessages.DELETE_RESOURCE_FAILURE);
					init();
				}
			);
		});
	}

	/**
	 * 
	 * @param data 
	 * @param $mdDialog  
	 * @param mode 
	 * @param designations 
	 * This is the controller for the resource creation/edit form modal.
	 */
	function resourceFormCtrl(data, $mdDialog, mode, designations) {
		var that = this;
		data.skill = data.skill ? data.skill.split(",") : [];
		that.data = data;
		that.mode = mode;
		that.designations = designations;
		that.disableParentScroll = false;
		that.skills = appConstants.skills;
		that.today = new Date();
		that.data.dateOfJoining = that.data.dateOfJoining && moment(that.data.dateOfJoining)._d;
		that.isAdmin = false;
		that.isAdminUser = PrincipalInfofactory.getUserRole().id === 1;
		if (mode === "edit" && data.role && data.role.id === 1) {
			that.isAdmin = true;
		}

		if (mode === "edit") {
			that.originalEmpId = that.data.empId;
		}

		function close(event) {
			$mdDialog.cancel();
		}

		function save(resourceForm) {
			that.mode == "edit" ? update(resourceForm) : create(resourceForm);
		}

		/**
		 * 
		 * @param resourceForm 
		 * This function validates the resource creation form and calls a factory method to add the resource to db if form is valid.
		 */
		function create(resourceForm) {
			resourceForm.$setSubmitted();
			if (resourceForm.$valid) {
				if (resourceForm.$dirty) {
					data.createdBy = PrincipalInfofactory.getId();
					data.lastUpdatedBy = PrincipalInfofactory.getId();
					data.createdDate = new Date();
					data.lastUpdatedTime = new Date();
					if (that.isAdmin) {
						data.role = {
							id: 1,
							roleName: "Admin"
						};
					} else {
						data.role = {
							id: 4,
							roleName: "Team Member"
						};
					}
					data.skill = data.skill.join(",");
					data.firstLogin = true;
					ResourceFactory.create(data).then(
						function (response) {
							$mdDialog.hide(true);
							toastr.success(appMessages.CREATE_RESOURCE_SUCCESS);
						},
						function (error) {
							let msg = error.data.message || appMessages.CREATE_RESOURCE_FAILURE;
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
		 * @param resourceForm 
		 * This function validates the resource updation form and calls a factory method to update the resource details in db if the form is valid.
		 */
		function update(resourceForm) {
			resourceForm.$setSubmitted();
			if (resourceForm.$valid) {
				if (resourceForm.$dirty) {
					if (that.isAdmin) {
						data.role = {
							id: 1,
							roleName: "Admin"
						};
					} else {
						data.role = {
							id: 4,
							roleName: "Team Member"
						};
					}
					data.lastUpdatedBy = PrincipalInfofactory.getId();
					data.lastUpdatedTime = new Date();
					data.skill = data.skill.join(",");
					ResourceFactory.update(data).then(
						function (response) {
							$mdDialog.hide(true);
							toastr.success(appMessages.UPDATE_RESOURCE_SUCCESS);
						},
						function (error) {
							let msg = error.data.message || appMessages.UPDATE_RESOURCE_FAILURE;
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
		 * @param resourceForm 
		 * This function clears the user input fields on the resource creation form.
		 */
		function clearForm(resourceForm) {
			that.data = {};
			that.data.emailAddress = "";
			resourceForm.$setUntouched();
			resourceForm.$setPristine();
		}

		function onEmpIdChange(mode) {
			if(that.data.empId !== that.originalEmpId && mode === "edit"){
				toastr.warning(appMessages.UPDATE_EMP_ID_CONFIRM);
			}
		}

		angular.extend(this, {
			"close": close,
			"save": save,
			"clearForm": clearForm,
			"onEmpIdChange": onEmpIdChange
		});
	}

	angular.extend(this, {
		"openModal": openModal,
		"remove": remove
	});
}

angular
	.module('timeSheet')
	.controller('resourceCtrl', resourceCtrl);
