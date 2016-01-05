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

    console.log(random.getRandomNumber());

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

app.controller('JavaScriptDetailsController', function ($scope, $http) {
    $http.get('/javascripterrors').then(function(response){
        var jserrorsLocal = angular.fromJson(response.data);

        $scope.jserrors = [];

        angular.forEach(jserrorsLocal, function(value, key) {
            $scope.jserrors.push(angular.fromJson(value[0]));
        });
    });
});

app.controller('SSLDetailsController', function ($scope, $http) {
    $http.get('/sslcertconnectionerrors').then(function(response){
        $scope.sslconnerrors = angular.fromJson(response.data);
    });

    $http.get('/sslcertexpirationerrors').then(function(response){
        $scope.sslexerrors = angular.fromJson(response.data);
    });
});

app.controller('HTTPDetailsController', function ($scope, $http) {
    $http.get('/httperrors').then(function(response){
        $scope.httperrors = angular.fromJson(response.data);
    });
});
