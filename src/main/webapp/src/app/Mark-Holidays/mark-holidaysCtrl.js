function markHolidaysCtrl(PrincipalInfofactory, MarkHolidaysFactory, toastr, $scope, $mdDialog) {
    var self = this;
    self.yearList = [];
    for (var i = 2018; i < 2118; i++) {
        self.yearList.push(i);
    }
    self.selectedYear = moment(new Date()).format("YYYY");
    self.holidayList = [];
    self.enableYearSelect = true;
    self.originalHolidays = [];

    function fetchHolidays() {
        self.startDate = moment(self.selectedYear + "").startOf('year').toDate();
        self.endDate = moment(self.selectedYear + "").endOf('year').toDate();
        MarkHolidaysFactory.fetchHolidaysOfYear(self.selectedYear).then(
            function (responseData) {
                self.holidayList = responseData;
                self.originalHolidays = angular.copy(responseData);
            },
            function (error) {
                self.holidayList = [];
                var msg = error.data && error.data.message ? error.data.message : appMessages.FETCH_HOLIDAY_ERROR;
                toastr.error(msg);
            }
        );
    }
    fetchHolidays();

    function addDate(addDateForm) {
        addDateForm.$setSubmitted();
        if (addDateForm.$valid) {
            var day = moment(self.selectedDate).format("YYYY-MM-DD");
            var holidays = self.holidayList.map(function (day, index) {
                return moment(day.holidayDate).format("YYYY-MM-DD");
            });
            if (holidays.indexOf(day) >= 0) {
                toastr.error("Selected day already exisits in the list");
                return;
            }
            self.selectedDate = "";
            addDateForm.$setUntouched();
            addDateForm.$setPristine();
            self.holidayList.push({
                "holidayDate": day,
                "year": self.selectedYear
            });
        }
    }

    function updateHolidays() {
        //if(self.originalHolidays.length === 0 && self.holidayList.length === 0)
        MarkHolidaysFactory.updateHolidaysOfYear(self.holidayList, self.selectedYear).then(
            function (responseData) {
                toastr.success(appMessages.UPDATE_HOLIDAY_SUCCESS);
                self.holidayList = responseData;
                self.originalHolidays = angular.copy(responseData);
                fetchHolidays();
            },
            function (error) {
                var msg = error.data && error.data.message ? error.data.message : appMessages.UPDATE_HOLIDAY_FAILURE;
                toastr.error(msg);
            }
        );
    }

    function removeHoliday(index) {
        self.holidayList.splice(index, 1);
    }

    function clearAll(addDateForm) {
        var confirm = $mdDialog.confirm({
            templateUrl: "assets/confirm-box.template.html",
            parent: angular.element(document.body),
            locals: {
                title: appMessages.CANCEL_MARK_HOLIDAY_TITLE,
                textContent: appMessages.CANCEL_MARK_HOLIDAY_CONTENT,
                ok: 'Yes',
                cancel: 'No'
            }
        });
        $mdDialog.show(confirm).then(function () {
            self.selectedDate = "";
            addDateForm.$setUntouched();
            addDateForm.$setPristine();
            fetchHolidays();
        });
    }

    angular.extend(this, {
        "fetchHolidays": fetchHolidays,
        "addDate": addDate,
        "updateHolidays": updateHolidays,
        "removeHoliday": removeHoliday,
        "clearAll": clearAll
    });
};
angular.module('timeSheet').controller('markHolidayCtrl', markHolidaysCtrl);