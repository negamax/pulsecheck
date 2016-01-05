/**
 * Created by mohitaggarwal on 01/01/2016.
 */
var app = angular.module('pulsecheck', ['ui.bootstrap']);

//service to generate random numbers
app.factory('randomnumber', function(){
    var obj = {};

    obj.getRandomNumber = function() {
        return Math.floor((Math.random() * Number.MAX_VALUE))
    };

    return obj;
});

app.controller('SitesStatusController', ['$scope', '$http', 'randomnumber', function($scope, $http, random) {
        $http.get('/sites?token=' + random.getRandomNumber()).then(function(response){
            $scope.sites = response.data;
        });

        $scope.findHighlighter = function(value) {
            if(value) {
                return "glyphicon glyphicon-remove-sign error-color";
            }
            return "glyphicon glyphicon-ok-sign success-color";
        }
}]);

app.controller('JavaScriptDetailsController', ['$scope', '$http', 'randomnumber', function($scope, $http, random) {
    $http.get('/javascripterrors?token=' + random.getRandomNumber()).then(function(response){
        var jserrorsLocal = angular.fromJson(response.data);

        $scope.jserrors = [];

        angular.forEach(jserrorsLocal, function(value, key) {
            $scope.jserrors.push(angular.fromJson(value[0]));
        });
    });
}]);

app.controller('SSLDetailsController', ['$scope', '$http', 'randomnumber', function($scope, $http, random) {
    $http.get('/sslcertconnectionerrors?token=' + random.getRandomNumber()).then(function(response){
        $scope.sslconnerrors = angular.fromJson(response.data);
    });

    $http.get('/sslcertexpirationerrors?token=' + random.getRandomNumber()).then(function(response){
        $scope.sslexerrors = angular.fromJson(response.data);
    });
}]);

app.controller('HTTPDetailsController', ['$scope', '$http', 'randomnumber', function($scope, $http, random) {
    $http.get('/httperrors?token=' + random.getRandomNumber()).then(function(response){
        $scope.httperrors = angular.fromJson(response.data);
    });
}]);
