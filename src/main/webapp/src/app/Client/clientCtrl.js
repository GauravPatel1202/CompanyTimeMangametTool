/**
 * 
 */
var clientCtrl = function (ClientsFactory, PrincipalInfofactory, toastr, $stateParams, $rootScope, $state, $scope, $mdDialog) {
	var self = this;

	/**
	 * 
	 * @param firstPageFlag 
	 * This function gets invoked on client screen load. It fetches the list of all clients by calling factory method.
	 */
	function init(firstPageFlag) {
		ClientsFactory.getAllClients().then(
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
	 * This is the object having the client grid options which includes, columns, sorting and pagination configurations.
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
			field: 'clientId',
			enableFiltering: true
		}, {
			displayName: 'Name',
			field: 'name',
			enableFiltering: true,
			sort: {
				direction: 'asc',
				priority: 0
			}
		}, {
			displayName: 'Email',
			field: 'emailId',
			enableFiltering: true
		}, {
			displayName: 'Phone',
			field: 'telephoneNumber',
			enableFiltering: true
		}, {
			name: 'Actions',
			headerCellClass: 'text-center',
			cellTemplate: '<span><md-tooltip md-direction="top">Edit</md-tooltip><i class="glyphicon glyphicon-pencil icon-edit-row" ng-click="grid.appScope.ctrl.openClientModal(row.entity)"></i></span>' +
				'<span><md-tooltip md-direction="top">Delete</md-tooltip><i class="glyphicon glyphicon-trash icon-delete-row" ng-click="grid.appScope.ctrl.deleteClient(row.entity)"></i></span>',
			cellClass: 'text-center action-icons',
			enableSorting: false,
			enableFiltering: false
		}]
	};
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
	 * This function is invoked on click of create new client and edit client,
	 * It opens a modal with the client creation form which will be empty while creating a new client and prepopulated if editing.
	 */
	function openClientModal(rowData) {
		$mdDialog.show({
			controller: clientFormCtrl,
			controllerAs: 'ctrl',
			templateUrl: './src/app/Client/clientForm.html',
			parent: angular.element(document.body),
			clickOutsideToClose: false,
			locals: {
				data: rowData ? angular.copy(rowData) : { "status": true },
				mode: rowData ? 'edit' : 'create'
			},
			resolve: {
				countryList: function (ClientsFactory) {
					return ClientsFactory.getCountryList();
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
	 * This function is the event handler of delete client button. It opens a confirmation popup and calls a factory method to delete the client from DB on OK.
	 */
	function deleteClient(rowData) {
		var confirm = $mdDialog.confirm({
			templateUrl: "assets/confirm-box.template.html",
			parent: angular.element(document.body),
			locals: {
				title: appMessages.DELETE_CLIENT_TITLE,
				textContent: appMessages.DELETE_CLIENT_CONTENT,
				ok: 'Yes',
				cancel: 'No'
			}
		});

		$mdDialog.show(confirm).then(function () {
			rowData.status = false;
			ClientsFactory.updateClient(rowData).then(
				function (response) {
					toastr.success(appMessages.DELETE_CLIENT_SUCCESS);
					init(true);
				},
				function (error) {
					toastr.error(appMessages.DELETE_CLIENT_FAILURE);
					init();
				}
			);
		});
	}

	/**
	 * 
	 * @param data 
	 * @param countryList 
	 * @param $mdDialog  
	 * @param mode 
	 * This is the controller for the client creation/edit form modal.
	 */
	function clientFormCtrl(data, countryList, $mdDialog, mode) {
		var that = this;
		that.formTitle = mode === "edit" ? "Update Client" : "Create New Client";
		that.countryList = countryList;
		that.data = data;
		that.disableParentScroll = false;
		that.isStateSelectable = false;
		that.statesList = appConstants.us_states;
		function close(event) {
			$mdDialog.hide();
		}
		/**
		 * 
		 * @param clientForm 
		 * This function validates the client creation form and calls a factory method to add a new client into DB.
		 */
		function addClient(clientForm) {
			clientForm.$setSubmitted();
			if (clientForm.$valid) {
				if (clientForm.$dirty) {
					if (mode === 'create') {
						data.createdBy = PrincipalInfofactory.getId();
						data.lastUpdatedBy = PrincipalInfofactory.getId();
						ClientsFactory.addNewClient(data).then(
							function (response) {
								$mdDialog.hide(true);
								toastr.success(appMessages.CREATE_CLIENT_SUCCESS);
							},
							function (error) {
								var errMsg = error.data.message || appMessages.CREATE_CLIENT_FAILURE;
								toastr.error(errMsg);
							}
						);
					} else if (mode === 'edit') {
						data.lastUpdatedBy = PrincipalInfofactory.getId();
						ClientsFactory.updateClient(data).then(
							function (response) {
								if (response && response.success) {
									toastr.success(appMessages.UPDATE_CLIENT_SUCCESS);
									$mdDialog.hide(true);
								} else {
									toastr.error(response.message || appMessages.UPDATE_CLIENT_FALIURE);
								}
							},
							function (error) {
								var errMsg = error.data.message || appMessages.UPDATE_CLIENT_FALIURE;
								toastr.error(errMsg);
							}
						);
					}
				} else {
					$mdDialog.cancel();
				}
			}
		}
		/**
		 * 
		 * @param clientForm 
		 * This function clears the client creation form.
		 */
		function clearForm(clientForm) {
			that.data = {};
			that.data.telephoneNumber = "";
			that.data.emailId = "";
			clientForm.$setUntouched();
			clientForm.$setPristine();
		}
		/**
		 * This function sets the flag to show the state field dropdown if the country chosen is USA.
		 */
		function onCountryChange(state){
			that.isStateSelectable = false;
			that.data.state = state || "";
			if(data.country && data.country === appConstants.country_usa){
				that.isStateSelectable = true;
			}
		}
		onCountryChange(that.data.state);
		angular.extend(this, {
			close: close,
			addClient: addClient,
			clearForm: clearForm,
			onCountryChange: onCountryChange
		});
	}

	function editRow(row) {
		alert("Edit " + row.name);
	}
	angular.extend(this, {
		"editRow": editRow,
		"deleteClient": deleteClient,
		"openClientModal": openClientModal
	});
}

angular
	.module('timeSheet')
	.controller('clientCtrl', clientCtrl);