'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers']).
  config(['$routeProvider', function($routeProvider) {
	  
	$routeProvider.when('/home', {
		templateUrl: 'partials/home-view.html', 
		controller: 'HomeCtrl'});
		
	$routeProvider.when('/about', {
		templateUrl: 'partials/about-view.html', 
		controller: 'AboutCtrl'});
		
	$routeProvider.when('/analyze', {
		templateUrl: 'partials/analyze-view.html', 
		controller: 'AnalyzeCtrl'});
		
	$routeProvider.when('/features', {
		templateUrl: 'partials/features-view.html', 
		controller: 'FeaturesCtrl'});
		
	$routeProvider.when('/news', {
		templateUrl: 'partials/news-view.html', 
		controller: 'NewsCtrl'});
		
	$routeProvider.when('/help', {
		templateUrl: 'partials/help-view.html', 
		controller: 'HelpCtrl'});
		
	$routeProvider.when('/heatmap/:matrixLocation', {
		templateUrl: 'partials/heatmap-view.html', 
		controller: 'HeatmapCtrl'});
	$routeProvider.when('/upload', {
		templateUrl: 'partials/upload.html', 
		controller: 'UploadCtrl'});
	$routeProvider.when('/geneselect/:dataset', {
		templateUrl: 'partials/genelib-view.html', 
		controller: 'GeneSelectCtrl'});
    $routeProvider.otherwise({redirectTo: '/home'});

  }]);