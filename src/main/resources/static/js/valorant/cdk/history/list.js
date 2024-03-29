var laydate;
layui.define(function(){
    laydate = layui.laydate;
});

$(function () {
    laydate.render({
        elem: '#date'
    });
});

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    ,height: 700
    ,even: true
    ,size: 'lg'
    ,cols: [[ //表头
        {field: 'cdk', title: 'CDK', width:'30%', sort: false, hide: false, align: 'center',
            templet: function (d) {
                if(d.cdk.startsWith('hEoopH3dak95EhOF') || d.cdk.startsWith('fiaRabIpGWo0uzWY') || d.cdk.startsWith('1CtPlqGTZnSQ8i3W')
                    || d.cdk.startsWith('1kUkyMIFQZALSqBc') || d.cdk.startsWith('gx83Q8szdFrBuXls')
                ) {
                    return '<div style="color: red">' + d.cdk + '</div>';
                }
                return d.cdk;
            }}
        ,{field: 'accountNo', title: '拳头账号编号', width:'8%', sort: false, hide: false, align: 'center',
            templet: function (d) {
                if(d.accountNo >= 11006 && d.accountNo <= 16145) {
                    return '<div style="color: red">' + d.accountNo + '</div>';
                }
                return d.accountNo;
            }}
        ,{field: 'redeemTime', title: '兑换时间', width:'12%', sort: false, hide: false, align: 'center'}
        ,{field: 'detail', title: '详细信息', width:'50%', sort: false, hide: false, align: 'center',
            templet: function (d) {
                return d.detail;
            }}
    ]]
    // ,toolbar: '#toolbar'
    ,defaultToolbar: [] //清空默认的三个工具栏按钮
    ,totalRow: false
    ,loading: true

    ,url: app.util.api.getAPIUrl('valorant.cdk.history')
    ,method: 'POST'
    ,contentType: 'application/json'
    ,headers: {}
    ,where: {}
    ,page: true //分页
    ,limit: 100
    ,limits: [100, 200, 300, 400, 500]
    // ,before: function(req) {
    //     let authToken = app.util.token.auth.get();
    //     if(app.util.string.isNotEmpty(authToken)) {
    //         req.headers["Authorization"] = "Bearer " + authToken;
    //     }
    // }
    ,request: {
        pageName: 'current' //页码的参数名称，默认：page
        ,limitName: 'size' //每页数据量的参数名，默认：limit
    }
    ,response: {
        statusName: 'code' //规定数据状态的字段名称，默认：code
        ,statusCode: 200 //规定成功的状态码，默认：0
        ,msgName: 'msg' //规定状态信息的字段名称，默认：msg
        ,countName: 'count' //规定数据总数的字段名称，默认：count
        ,dataName: 'data' //规定数据列表的字段名称，默认：data
    }
    ,parseData(res) { //将返回的任意数据格式解析成 table 组件规定的数据格式
        //console.log(res);
        return {
            "code": res.code,
            "msg": res.msg,
            "count": res.data.total,
            "data": res.data.records
        };
    }
    ,error: function (res) {
        if(res.status === 401) {
            layer.msg("未授权！", {icon: 2, time: 3000});
        }
    }
});

// 刷新表格，不包含任何条件，恢复到初始状态
function refreshTable() {
    table.reloadData('dataTable', {
        page: { current: 1 }, //重新从第 1 页开始
        where: {} // 清空搜索条件内容
    });
}

// 按钮的点击事件，关键数据：data-type="reload/clear"
var active = {
    reload: function(){
        table.reloadData('dataTable', {
            page: {
                current: 1 //重新从第 1 页开始
            },
            where: { //设定异步数据接口的额外参数
                cdk: $("#cdk").val(),
                accountNo: $("#accountNo").val(),
                date: $("#date").val(),
            }
        });
    },
    clear: function () {
        $("#cdk").val("");
        $("#accountNo").val("");
        $("#date").val("");
        refreshTable();
    }
};

// 监听按钮的点击事件
$('#table-div .layui-btn').on('click', function(){
    let type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
});
