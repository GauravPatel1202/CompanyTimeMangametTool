/**
 *
 */
var alertConfigurationCtrl = function (AlertConfiguration, PrincipalInfofactory, toastr, $state) {
	var self = this;
	self.data = {};

	self.days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
	self.hours = [1, 2, 3, 4, 5, 6, 7, 8];

	function cancel() {
		let userId = PrincipalInfofactory.getEmpId();
		$state.go('timesheet', { 'userId': userId });
	}

	function load(){
		AlertConfiguration.get().then(
			function(response){
			},
			function(error){
				let msg = error.data.message || "Unable to fetch configuration data. Please try again later.";
				toastr.error(msg);
			}
		);
	}

	function update(form) {
		form.$setSubmitted();

		if (form.$valid) {
			if(form.$dirty) {
				AlertConfiguration.update(self.data).then(
					function(response){
						toastr.success("Configuration updated successfully");
					},
					function(error){
						let msg = error.data.message || "Configuration could not be updated at this moment. Please try again later.";
						toastr.error(msg);
					}
				);
			} else{
				toastr.warning("There are no changes to update");
			}
		}
	}

	// load();
	angular.extend(this, {
		"update": update,
		"cancel": cancel
	});
}

angular
	.module('timeSheet')
	.controller('alertConfigurationCtrl', alertConfigurationCtrl);
