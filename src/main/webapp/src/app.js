angular.module('timeSheet',
    [
        'ui.router',
        'oc.lazyLoad',
        'ngAria',
        'ngAnimate',
        'ngMessages',
        'ngMaterial',
        'toastr',
        'datatables',
        'ngSanitize',
        'ui.select',
        "ui.grid",
        "ui.grid.autoResize",
        "ui.grid.pagination",
        "angular-loading-bar",
        "ui.grid.selection",
        "ui.grid.grouping",
        "ui.grid.exporter",
        "my.popover",
        "my.tooltip",
        'ngAnimate',
        'ui.bootstrap',
        'ngFileUpload',
        'jkAngularCarousel',
        'chart.js'
    ]).filter('startFrom', function () {
        return function (input, start) {
            start = +start; //parse to int
            if (input !== undefined)
                return input.slice(start);
            else
                return null;
        }
    }).config(function (toastrConfig, $ocLazyLoadProvider, $stateProvider, $urlRouterProvider, cfpLoadingBarProvider, $httpProvider, $mdDateLocaleProvider, $popoverProvider, $tooltipProvider) {
        angular.extend($tooltipProvider.defaults, {
            trigger: "hover",
            placement: "top",
            animation: true,
            html: true
        });
        angular.extend($popoverProvider.defaults, {
            html: true,
            animation: true
        });
        cfpLoadingBarProvider.spinnerTemplate = `<div class="loader"><div class="text-center" layout="row"><img src="assets/img/loading.gif"></div></div>`;

        $httpProvider.interceptors.push(function () {
            return {
                'request': function (config) {
                    if (config.url && config.url.indexOf("src/") < 0 && config.url !== "timesheetLogin/login" && config.url.indexOf("timesheetLogin/login/forgetPassword") < 0 && window.sessionStorage.getItem("token")) {
                        config.headers['Authorization'] = 'Basic ' + window.sessionStorage.getItem("token");
                    }
                    return config;
                }
            };
        });
        $mdDateLocaleProvider.formatDate = function (date) {
            if (this.monthParser) {
                return moment(date).format("MMMM, YYYY");
            }
            return moment(date).format("DD/MM/YYYY");
        };
        angular.extend(toastrConfig, {
            allowHtml: false,
            closeButton: true,
            closeHtml: '<button>&times;</button>',
            extendedTimeOut: 1000,
            iconClasses: {
                error: 'toast-error',
                info: 'toast-info',
                success: 'toast-success',
                warning: 'toast-warning'
            },
            messageClass: 'toast-message',
            onHidden: null,
            onShown: null,
            onTap: null,
            progressBar: false,
            tapToDismiss: true,
            templates: {
                toast: 'directives/toast/toast.html',
                progressbar: 'directives/progressbar/progressbar.html'
            },
            preventDuplicates: false,
            preventOpenDuplicates: true,
            timeOut: 5000,
            titleClass: 'toast-title',
            toastClass: 'toast',
            positionClass: 'toast-top-center'
        });

        $urlRouterProvider.otherwise('/login');

        $stateProvider.state('login', {
            url: '/login',
            templateUrl: 'src/app/Login/login.html',
            controller: 'loginCtrl',
            controllerAs: 'loginCtrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Login/loginFactory.js',
                        'src/app/Login/loginCtrl.js'
                    ]);
                }
            }
        });

        $stateProvider.state('projects', {
            url: '/projects',
            templateUrl: 'src/app/Project/project.html',
            controller: 'projectCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Client/clientFactory.js',
                        'src/app/Project/projectFactory.js',
                        'src/app/Project/projectCtrl.js'
                    ]);
                }
            }
        });

        $stateProvider.state('resources', {
            url: '/resources',
            templateUrl: 'src/app/Resource/resource.html',
            controller: 'resourceCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Resource-Mapping/resource-mappingFactory.js',
                        'src/app/Resource/resourceFactory.js',
                        'src/app/Resource/resourceCtrl.js'
                    ]);
                }
            }
        });

        $stateProvider.state('changePassword', {
            url: '/changePassword',
            templateUrl: 'src/app/Change-Password/password.html',
            controller: 'passwordCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Change-Password/notequals-to.js',
                        'src/app/Change-Password/equals-to.js',
                        'src/app/Change-Password/passwordFactory.js',
                        'src/app/Change-Password/passwordCtrl.js'
                    ]);
                }
            }
        });

        $stateProvider.state('alertConfiguration', {
            url: '/alertConfiguration',
            templateUrl: 'src/app/Alert-Configuration/alertConfiguration.html',
            controller: 'alertConfigurationCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Alert-Configuration/alertConfigurationFactory.js',
                        'src/app/Alert-Configuration/alertConfigurationCtrl.js'
                    ]);
                }
            }
        });

        $stateProvider.state('task', {
            url: '/task',
            templateUrl: 'src/app/Task/task.html',
            controller: 'taskCtrl',
            controllerAs: 'taskCtrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Task/taskFactory.js',
                        'src/app/Task/taskCtrl.js',
                        'src/app/Resource-Mapping/resource-mappingFactory.js',
                        'src/app/Resource/resourceFactory.js'
                    ]);
                }
            }
        });


        $stateProvider.state('register', {
            url: '/register',
            templateUrl: 'src/app/Registration/register.html',
            controller: 'registerCtrl',
            controllerAs: 'registerCtrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Registration/register.js',
                        'src/app/Registration/registerFactory.js',
                        'src/app/Custom Factories/PrincipalInfoFactory.js'
                    ]);
                }
            }
        });

        $stateProvider.state('timesheet', {
            url: '/timesheet/:userId',
            templateUrl: 'src/app/TimeSheet/timesheet.html',
            controller: 'timesheetCtrl',
            controllerAs: 'viewObj',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/TimeSheet/timesheetCtrl.js',
                        'src/app/TimeSheet/timesheetFactory.js',
                        'src/app/Mark-Holidays/mark-holidaysFactory.js'
                    ]);
                },
                managerMode: function () {
                    return false;
                }
            }
        });

        $stateProvider.state('client', {
            url: '/client',
            templateUrl: 'src/app/Client/client.html',
            controller: 'clientCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Client/clientCtrl.js',
                        'src/app/Client/clientFactory.js'
                    ]);
                }
            }
        });

        $stateProvider.state('mapresource', {
            url: '/mapresource',
            templateUrl: 'src/app/Resource-Mapping/resource-mapping.html',
            controller: 'resourcemapCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Resource-Mapping/resource-mappingCtrl.js',
                        'src/app/Resource-Mapping/resource-mappingFactory.js'
                    ]);
                },
                pageData: function (dependancies, ResourcemapFactory) {
                    return ResourcemapFactory.getPageData();
                }
            }
        });

        $stateProvider.state('timesheetApproval', {
            url: '/timesheetapprove',
            templateUrl: 'src/app/TimeSheet/timesheet.html',
            controller: 'timesheetCtrl',
            controllerAs: 'viewObj',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/TimeSheet/timesheetCtrl.js',
                        'src/app/TimeSheet/timesheetFactory.js',
                        'src/app/Mark-Holidays/mark-holidaysFactory.js'
                    ]);
                },
                managerMode: function () {
                    return true;
                }
            }
        });

        $stateProvider.state('bulkApproval', {
            url: '/bulkapproval',
            templateUrl: 'src/app/Bulk-Approval/bulkApproval.html',
            controller: 'bulkApprovalCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Bulk-Approval/bulkApprovalCtrl.js',
                        'src/app/Bulk-Approval/bulkApprovalFactory.js'
                    ]);
                },
                managerMode: function () {
                    return true;
                }
            }
        });

        $stateProvider.state('reportSummary', {
            url: '/reportsummary',
            templateUrl: 'src/app/Report-summary/report-summary.html',
            controller: 'reportSummaryCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Report-summary/reportSummaryCtrl.js',
                        'src/app/Bulk-Approval/bulkApprovalFactory.js',
                        'src/app/Project/projectFactory.js',
                        'src/app/TimeSheet/timesheetFactory.js',
                        'src/app/Mark-Holidays/mark-holidaysFactory.js'
                    ]);
                },
                projectList: function (dependancies, ProjectsFactory) {
                    return ProjectsFactory.getApprovableProjects();
                }
            }
        });

        $stateProvider.state('monthlyView', {
            url: '/monthview',
            templateUrl: 'src/app/Report-summary/report-summary.html',
            controller: 'reportSummaryCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Report-summary/reportSummaryCtrl.js',
                        'src/app/Bulk-Approval/bulkApprovalFactory.js',
                        'src/app/Project/projectFactory.js',
                        'src/app/TimeSheet/timesheetFactory.js',
                        'src/app/Mark-Holidays/mark-holidaysFactory.js'
                    ]);
                },
                projectList: function (dependancies, ProjectsFactory, PrincipalInfofactory) {
                    PrincipalInfofactory.monthlyView = true;
                    return ProjectsFactory.getApprovableProjects();
                }
            }
        });

        $stateProvider.state('markHolidays', {
            url: '/markholidays',
            templateUrl: 'src/app/Mark-Holidays/mark-holidays.html',
            controller: 'markHolidayCtrl',
            controllerAs: 'ctrl',
            resolve: {
                dependancies: function ($ocLazyLoad) {
                    return $ocLazyLoad.load([
                        'src/app/Custom Factories/PrincipalInfoFactory.js',
                        'src/app/Mark-Holidays/mark-holidaysCtrl.js',
                        'src/app/Mark-Holidays/mark-holidaysFactory.js'
                    ]);
                }
            }
        });
        //cfpLoadingBarProvider.includeSpinner = false;
    });

angular.module('timeSheet').run(function ($rootScope, $state, $http, PrincipalInfofactory, toastr) {

    $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error, $http) {
        event.preventDefault();
    });
    $rootScope.$on('$stateChangeStart',
        function (event, toState, toParams, fromState, fromParams) {
            if (window.sessionStorage.getItem("authenticated") && !$rootScope.user && toState.name != 'login') {
                $rootScope.user = JSON.parse(window.sessionStorage.getItem("userData"));
                $rootScope.role = $rootScope.user.role.roleName;
                $rootScope.loginPage = false;
                PrincipalInfofactory.setPrincipal($rootScope.user);
                if ($rootScope.user.firstLogin) {
                    event.preventDefault();
                    toastr.warning("Welcome " + $rootScope.user.empName + ". You must change the password after first login.");
                    $state.go('changePassword');
                }
            }
            $rootScope.state = toState.name;
            if (toParams && (toParams.userId && $rootScope.state == "timesheet")) {
                $rootScope.state = "viewTimeSheet";
            }
            if (!window.sessionStorage.getItem("authenticated") && toState.name != 'login') {
                event.preventDefault();
                $state.go('login');
            }
        });
    $http.defaults.transformRequest.push(function (data) {
        $rootScope.progress = true;
        return data;
    });
    $http.defaults.transformResponse.push(function (data) {
        $rootScope.progress = false;
        return data;
    });
    document.addEventListener("click", function () {
        window.idleTime = 0;
        clearInterval(window.idleTimer);
        window.idleTimer = setInterval(timerFn, 60000);
    });
    function timerFn() {
        if (window.idleTime) {
            window.idleTime++;
        } else {
            window.idleTime = 1;
        }
        if (window.idleTime >= 15) {
            $state.go('login');
            location.reload();
            $rootScope.username = null;
            $rootScope.role = null;
            $rootScope = $rootScope.$new(true);
            window.sessionStorage.removeItem("authenticated");
            window.sessionStorage.removeItem("userData");
            window.sessionStorage.removeItem("token");
            window.idleTimer && clearInterval(window.idleTimer);
        }
    }
    window.idleTimer = setInterval(timerFn, 60000);
});

angular.module('timeSheet').filter("dayOfWeek", function () {
    return function (date) {
        if (!!date) {
            var rowDate = moment(date).format('dddd');
            return rowDate;
        }
    };
});

angular.module('timeSheet').filter("dateFormat", function () {
    return function (date) {
        if (!!date) {
            var rowDate = moment(date).format('DD-MM-YYYY');
            return rowDate;
        }
    };
});

angular.module('timeSheet').filter('round', function () {
    return function (val) {
        if (!isNaN(val)) {
            return parseFloat(val.toFixed(2));
        }
    };
});

// General Code
Date.prototype.monthNames = [
    "January", "February", "March",
    "April", "May", "June",
    "July", "August", "September",
    "October", "November", "December"
];

Date.prototype.getMonthName = function () {
    return this.monthNames[this.getMonth()] + " " + this.getFullYear();
};

Number.prototype.getPageArray = function (currentPage) {
    var arr = [];
    var min = currentPage - 2;
    if (this - currentPage < 2) {
        min = min - (2 - (this - currentPage));
    }
    for (var i = min; i <= this; i++) {
        if (i > 0) {
            arr.push(i);
        }
        if (arr.length === 5) {
            break;
        }
    }
    return arr;
}