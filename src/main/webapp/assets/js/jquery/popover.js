'use strict';

var app = angular.module("my.popover", []);

app.provider("$popover", [function() {
  var defaults = this.defaults = {
    trigger: "click",
    placement: "bottom"
  }
  
  this.$get = function() {
    function factory(config) {
      var poppy = {};
      
      var options = angular.extend({}, defaults, config);
      
      poppy.options = options;
      
      return poppy;
    }
    return factory;
  }
}]);

app.directive("myPopover", ["$popover", function($popover) {
  return {
    restrict: "AE",
    scope: {
      popoverTitle: "@",
      popoverContent: "@",
      popoverTrigger: "@",
      popoverPlacement: "@",
      myPopover: "="
    },
    link: function(scope, element, attr) {
      var options;
      console.log(attr);
      if(angular.isDefined(scope.myPopover) && angular.isObject(scope.myPopover)) {
        options = $popover(scope.myPopover).options;
        element.popover(options);
      }
      else {
        options = {
          title: attr.popoverTitle || "",
          content: attr.popoverContent || "",
          trigger: attr.popoverTrigger || "click",
          placement: attr.popoverPlacement || "right"
        };
        element.popover($popover(options).options);
      }
    }
  }
}])