function LoginCtrl($state, $timeout, $http, $rootScope, PrincipalInfofactory, $window, $location, toastr, loginFactory, $state) {
	$rootScope.loginPage = true;
	this.dataArray = [
		{
			src: 'assets/img/carsl/1.png'
		}, {
			src: 'assets/img/carsl/2.png'
		}, {
			src: 'assets/img/carsl/3.png'
		}, {
			src: 'assets/img/carsl/4.png'
		}, {
			src: 'assets/img/carsl/5.png'
		}, {
			src: 'assets/img/carsl/6.png'
		}, {
			src: 'assets/img/carsl/7.png'
		}, {
			src: 'assets/img/carsl/8.png'
		}, {
			src: 'assets/img/carsl/9.png'
		}, {
			src: 'assets/img/carsl/10.png'
		}
	];
	this.templateUrl = "assets/item-template.html"

	function validateLogin(loginForm) {

		var loginObj = this;
		var credentials = { "userName": loginObj.username, "password": loginObj.password };

		loginForm.$setSubmitted();
		if (!loginForm.$valid) {
			return;
		}

		window.sessionStorage.setItem("token", btoa(loginObj.username + ":" + loginObj.password));
		loginFactory.loginUser(loginObj.username, loginObj.password).then(
			function (data) {
				if (data) {
					if(idleTimer){
						clearInterval(idleTimer);
					}
					window.idleTimer = setInterval(function () {
						if (window.idleTime) {
							window.idleTime++;
						} else {
							window.idleTime = 1;
						}
						if (window.idleTime >= 5) {
							$state.go('login');
							$rootScope.username = null;
							$rootScope.role = null;
							$rootScope = $rootScope.$new(true);
							window.sessionStorage.removeItem("authenticated");
							window.sessionStorage.removeItem("userData");
							window.sessionStorage.removeItem("token");
							window.idleTimer && clearInterval(window.idleTimer);
						}
					}, 60000);
					PrincipalInfofactory.setPrincipal(data);
					var userName = PrincipalInfofactory.loginData.empName;
					$rootScope.user = data;
					window.sessionStorage.setItem("authenticated", true);
					window.sessionStorage.setItem("userData", JSON.stringify(data));
					if (data.firstLogin) {
						toastr.warning("Welcome " + userName + ". You must change the password after first login.");
						$rootScope.loginPage = false;
						$rootScope.role = data.role.roleName;
						$state.go('changePassword');
					} else {
						if (data.role.roleName === "Admin") {
							toastr.success("Welcome " + userName);
							$rootScope.loginPage = false;
							$rootScope.role = data.role.roleName;
							$state.go('timesheet');
						} else if (data.role.roleName === "Project Manager") {
							toastr.success("Welcome " + userName);
							$rootScope.loginPage = false;
							$rootScope.role = data.role.roleName;
							$state.go('timesheet');
						} else if (data.role.roleName === "Team Lead") {
							toastr.success("Welcome " + userName);
							$rootScope.loginPage = false;
							$rootScope.role = data.role.roleName;
							$state.go('timesheet');
						} else if (data.role.roleName === "Team Member") {
							toastr.success("Welcome " + userName);
							$rootScope.loginPage = false;
							$rootScope.role = data.role.roleName;
							$rootScope.loginPage = false;
							$state.go('timesheet');
						}
					}
				}
			},
			function (data) {
				toastr.error(appMessages.INVALID_USER_LOGIN, 'Error');
				$rootScope.error = true;
				$rootScope.authenticated = false;
				window.sessionStorage.removeItem("authenticated");
				window.sessionStorage.removeItem("userData");
				window.sessionStorage.removeItem("token");
			});
	}

	function resetPassword(resetForm) {
		resetForm.$setSubmitted();
		if (!resetForm.$valid) {
			return;
		}
		var loginObj = this;
		loginFactory.resetPassword(loginObj.username).then(
			function (data) {
				toastr.success(appMessages.PASSWORD_RESET_SUCCESS);
			},
			function (error) {
				var msg = error.data.message || appMessages.PASSWORD_RESET_FAILURE;
				toastr.error(msg);
			}
		)
	}

	function registerUser() {
		$state.go('register');
		$rootScope.loginPage = false;
	}

	angular.extend(this, {
		validateLogin: validateLogin,
		registerUser: registerUser,
		resetPassword: resetPassword
	});
}

angular.module('timeSheet').controller('loginCtrl', LoginCtrl);
