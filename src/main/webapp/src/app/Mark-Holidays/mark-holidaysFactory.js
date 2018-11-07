function markHolidaysFactory($http, PrincipalInfofactory) {
    var MarkHolidaysFactory = {};

    MarkHolidaysFactory.fetchHolidaysOfYear = function (year) {
        return $http.get('holidaylist/getHolidayList/' + year).then(
            function (response) {
                for(var i = 0; i < response.data.length; i++){
                    response.data[i].holidayDate = moment(response.data[i].holidayDate).format("YYYY-MM-DD");
                }
                return response.data;
            }
        );
    }

    MarkHolidaysFactory.updateHolidaysOfYear = function (holidayList, year) {
        var personId = PrincipalInfofactory.getId();
        return $http.post('holidaylist/updateHolidayList/' + year + "/" + personId,holidayList).then(
            function (response) {
                for(var i = 0; i < response.data.length; i++){
                    response.data[i].holidayDate = moment(response.data[i].holidayDate).format("YYYY-MM-DD");
                }
                return response.data;
            }
        );
    }

    return MarkHolidaysFactory;
};
angular.module('timeSheet').factory('MarkHolidaysFactory', markHolidaysFactory);