var mod = angular.module("app", ['ngResource', 'ui.bootstrap']);

mod.controller("TranslateCtrl", ["$scope", "$resource", "$timeout", function($scope, $resource, $timeout) {
  var TranslateFile = $resource('/api/translates/:id', {id : "@id"});


  $scope.$watch("radioModel", function(v){
    $scope.files = TranslateFile.query({"tag": v});
  });
  
  $scope.radioModel = "required";

  $scope.markDelete = function(f, flag) {
    f.deleted = flag;
    f.$save();
  }
  
  $scope.updateText = function(f) {
    f.saving = true;
    f.$save(function(){
      f.saving = false;
      f.showSaved = true;
      $timeout(function(){
        f.showSaved = false;
      }, 1000);
    });
  }
  
  $scope.batchMarkRequired = function(flag) {
    angular.forEach($scope.files, function(f) {
      if (f.selected) {
        var isRequired = false;
        if (flag == "required") {
          isRequired = true;
        }
        f.isRequired = isRequired;
        f.$save(function(){
          f.showNotRequired = !isRequired;
          f.selected = false;
        });
      }
    })

  }

}]);