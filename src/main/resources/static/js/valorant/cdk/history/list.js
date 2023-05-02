$(function () {
});

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    // ,height: 500
    ,cols: [[ //表头
        {type: 'checkbox', fixed: 'left'}
        ,{field: 'cdk', title: 'CDK', width:'30%', sort: false, hide: false, align: 'center'}
        ,{field: 'accountNo', title: '拳头账号编号', width:'8%', sort: false, hide: false, align: 'center'}
        ,{field: 'redeemTime', title: '兑换时间', width:'10%', sort: false, hide: false, align: 'center'}
        ,{field: 'detail', title: '详细信息', width:'45%', sort: false, hide: false, align: 'center',
            templet: function (d) {
                return d.detail;
            }}
    ]]
    ,toolbar: '#toolbar'
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
});

// 刷新表格，不包含任何条件，恢复到初始状态
function refreshTable() {
    table.reloadData('dataTable', {
        where: {} // 清空搜索条件内容
    });
}

// 按钮的点击事件，关键数据：data-type="reload/clear"
var active = {
    reload: function(){
        table.reloadData('dataTable', {
            // page: {
            //     curr: 1 //重新从第 1 页开始
            // },
            where: { //设定异步数据接口的额外参数
                cdk: $("#cdk").val(),
                accountNo: $("#accountNo").val(),
            }
        });
    },
    clear: function () {
        $("#cdk").val("");
        $("#accountNo").val("");
        refreshTable();
    }
};

// 监听按钮的点击事件
$('#table-div .layui-btn').on('click', function(){
    let type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
});
