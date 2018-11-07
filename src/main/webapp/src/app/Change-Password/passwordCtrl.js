/**
 *
 */
var passwordCtrl = function (PasswordFactory, PrincipalInfofactory, toastr, $state, $rootScope) {
	var self = this;
	self.data = {};
	self.isFirstLogin = $rootScope.user.firstLogin;

	function cancel() {
		let userId = PrincipalInfofactory.getEmpId();
		$state.go('timesheet', { 'userId': userId });
	}

	/**
	 * 
	 * @param form 
	 * This function calls a factory method th change the existing password of the user.
	 */
	function update(form) {
		form.$setSubmitted();
		let user = PrincipalInfofactory.getloginData();
		user.newPassword = self.data.newPassword;
		user.password = self.data.oldPassword;

		if (form.$valid) {
			if (form.$dirty) {
				PasswordFactory.update(user).then(
					function (response) {
						clearForm(form);
						if(user.firstLogin){
							toastr.success(appMessages.RELOGIN_AFTER_RESET);
							$state.go('login');
							$rootScope.username = null;
							$rootScope.role = null;
							$rootScope = $rootScope.$new(true);
							window.sessionStorage.removeItem("authenticated");
							window.sessionStorage.removeItem("userData");
							window.sessionStorage.removeItem("token");
						}else{
							toastr.success(appMessages.UPDATE_PASSWORD_SUCCESS);
							$state.go("timesheet");
						}
					},
					function (error) {
						let msg = error.data.message || appMessages.UPDATE_PASSWORD_FAILURE;
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
	 * @param form 
	 * This is the event handler of clear button which clears all the user input fields.
	 */
	function clearForm(form) {
		self.data = {};
		self.data.confirmPassword = "";
		self.data.newPassword = "";
		form.$setUntouched();
		form.$setPristine();
	}

	angular.extend(this, {
		"clearForm": clearForm,
		"update": update,
		"cancel": cancel
	});
}

angular
	.module('timeSheet')
	.controller('passwordCtrl', passwordCtrl);
