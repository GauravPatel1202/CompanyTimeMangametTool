function RegisterCtrl($scope,PrincipalInfofactory, RegisterFactory,toastr,$state,$rootScope){
	var that = this ;
    this.gender = ["male","female"];
    that.user = {};
    that.todaysDate = moment().format('YYYY-MM-DD');
    
    if($rootScope.role === "Super Admin"){
    	this.role = [
                    {"id":2 , "description": "Account Head"},
                    {"id":3 , "description": "Team Lead"},
                    {"id":4 , "description": "Team Member"}];
    }else if($rootScope.role === "Account Head"){
    	this.role = [
                    {"id":3 , "description": "Team Lead"},
                    {"id":4 , "description": "Team Member"}];
    }else if($rootScope.role === "Team Lead"){
    	this.role = [
                     {"id":4 , "description": "Team Member"}];
     }
    
    function validateRegisterData(){
        var dataToSend = this.user ;
        dataToSend.role = {
        		"id" : dataToSend.role.id
        }
        
        dataToSend.createdBy = PrincipalInfofactory.getloginData();
        dataToSend.lastUpdatedTime = new Date();
        dataToSend.createdDate = new Date();
        dataToSend.lastUpdatedBy = PrincipalInfofactory.getloginData();
        dataToSend.status = true; 
        RegisterFactory.saveNewUser(dataToSend).then(function(response){
          toastr.success("User Registered Successfully");
          that.user.firstName = null ;
          that.user.lastName = null ;
          that.user.empId =null ;
          that.user.role = "";
          that.user.dateOfJoin = null ;
          that.user.email = null ;
          that.user.phone = null ;
          that.user.sex = "";
          $state.go("timesheet");
		}, function(data){
			 toastr.error(data.data.message);	
		});
    }
    
    function cancelRegisterData(){
    	$state.go('timesheet');
    }
    

     angular.extend(this, {
		 validateRegisterData : validateRegisterData,
		 cancelRegisterData : cancelRegisterData
	    });
    
}

angular.module('timeSheet').controller('registerCtrl',
		RegisterCtrl);
