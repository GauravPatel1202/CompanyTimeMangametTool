var equalsTo = function() {
    return {
        require: "ngModel",
        scope: {
            otherModelValue: "=equalsTo"
        },
        link: function(scope, element, attributes, ngModel) {

            ngModel.$validators.equalsTo = function(modelValue) {
                return modelValue != scope.otherModelValue;
            };

            scope.$watch("otherModelValue", function() {
                ngModel.$validate();
            });
        }
    };
};

angular
	.module('timeSheet')
    .directive("equalsTo", equalsTo);
