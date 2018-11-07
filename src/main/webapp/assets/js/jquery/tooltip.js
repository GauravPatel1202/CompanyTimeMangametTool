'use strict';

var app = angular.module("my.tooltip", []);
app.provider("$tooltip", [function() {
    var defaults = this.defaults = {
        trigger: "hover",
        placement: "top",
        animation: true,
        html: true
    };

    this.$get = function() {
        function tooltipFactory(config) {
            var tool = {};

            var options = angular.extend({}, defaults, config);

            tool.options = options;

            return tool;
        }
        return tooltipFactory;
    }
}]);

app.directive("tooltip", ["$tooltip", "$parse", function($tooltip, $parse) {
    return {
        restrict: "A",
        link: function(scope, element, attr) {
            if(!!attr.tooltipHtml) {
              if(attr.tooltipHtml === "true") {
                attr.tooltipHtml = true;
              }
              else if(attr.tooltipHtml === "false") {
                attr.tooltipHtml = false;
              }
              else {
                attr.tooltipHtml = false;
              }
            }
            var options = {
                title: attr.tooltipTitle || "",
                delay: attr.tooltipDelay || 500,
                trigger: attr.tooltipTrigger || "hover",
                placement: attr.tooltipPlacement || "top",
                html: attr.tooltipHtml || false,
                animation: attr.tooltipAnimation || true
            }
            element.tooltip($tooltip(options).options);
        }
    }
}]);