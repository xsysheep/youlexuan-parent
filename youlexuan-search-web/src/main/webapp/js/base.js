var app = angular.module("youlexuan",[]);
//我们测试后发现高亮显示的html代码原样输出，这是angularJS为了防止html攻击采取的安全机制。我们如何在页面上显示html的结果呢？我们会用到$sce服务的trustAsHtml方法来实现转换。
//因为这个功能具有一定通用性，我们可以通过angularJS的过滤器来简化开发，这样只写一次，调用的时候就非常方便了，看代码：
/*$sce服务写成过滤器*/
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);