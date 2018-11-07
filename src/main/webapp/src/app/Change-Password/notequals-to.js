var notequalsTo = function() {
    return {
        require: "ngModel",
        scope: {
            oModelValue: "=notequalsTo"
        },
        link: function(scope, element, attributes, ngModel) {

            ngModel.$validators.notequalsTo = function(modelValue) {
                return modelValue == scope.oModelValue;
            };

            scope.$watch("oModelValue", function() {
                ngModel.$validate();
            });
        }
    };
};

angular
	.module('timeSheet')
    .directive("notequalsTo", notequalsTo);
