$(function () {
   loadAllSelect();
});

function loadAllSelect() {
    let typeHasEmail = [
        {name: "不带初邮CDK", value: false},
        {name: "带初邮CDK", value: true}
    ];
    let typeReusable = [
        {name: "一次性CDK", value: false},
        {name: "可重复使用CDK", value: true}
    ];
    loadSelectFromJson(typeHasEmail, $("#typeHasEmail"), "name", "value");
    loadSelectFromJson(typeReusable, $("#typeReusable"), "name", "value");
}

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    // ,height: 500
    ,cols: [[ //表头
        {type: 'checkbox', fixed: 'left'}
        ,{field: 'cdkNo', title: 'CDK编号', width: '7%', sort: true, hide: false, align: 'center'}
        ,{field: 'cdk', title: 'CDK内容', width:'40%', sort: false, hide: false, align: 'center'}
        ,{field: 'typeHasEmail', title: '是否带初邮', sort: true, align: 'center',
            templet: function (d) {
                if(d.typeHasEmail != null && d.typeHasEmail === true) {
                    return '带初邮CDK';
                }
                return '不带初邮CDK';
            }
        }
        ,{field: 'typeReusable', title: '是否可重复使用', sort: true, align: 'center',
            templet: function (d) {
                if(d.typeReusable != null && d.typeReusable === true) {
                    return '可重复使用CDK';
                }
                return '一次性CDK';
            }
        }
        ,{field: 'reuseRemainingTimes', title: '剩余可使用次数', sort: true, align: 'center'}
        ,{title:'操作', width: '10%', align: 'center', fixed: 'right', toolbar: '#inlineToolbar'}
    ]]
    ,toolbar: '#toolbar'
    // ,defaultToolbar: [] //清空默认的三个工具栏按钮
    ,totalRow: false
    ,loading: true

    ,url: app.util.api.getAPIUrl('valorant.cdk.query')
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
                cdkNo: $("#cdkNo").val(),
                cdk: $("#cdk").val(),
                typeHasEmail: $("#typeHasEmail").val(),
                typeReusable: $("#typeReusable").val(),
            }
        });
    },
    clear: function () {
        $("#cdkNo").val("");
        $("#cdk").val("");
        $("#typeHasEmail").val("");
        $("#typeReusable").val("");
        refreshTable();
        form.render('select');
    }
};

// 监听按钮的点击事件
$('#table-div .layui-btn').on('click', function(){
    let type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
});

// 删除选中的行
function deleteRows(toBeDeletedIdList) {
    app.util.ajax.delete(app.util.api.getAPIUrl('valorant.cdk.delete'),
        {cdkNoList: toBeDeletedIdList.join()},
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                layer.msg('删除成功。', {time: 2000});
                refreshTable();
            }
        },
        function (res) {
            layer.msg('删除失败！', {icon:2, time: 2000});
            refreshTable();
        }
    );
}

// 修改选中的行
function updateRowsReuseRemainingTimes(toBeUpdatedCDKNoList, reuseRemainingTimes) {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.updateReuseRemainingTimes'),
        {reusableCDKNoList: toBeUpdatedCDKNoList.join(), reuseRemainingTimes: reuseRemainingTimes},
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                layer.msg('修改成功。', {time: 2000});
                refreshTable();
            }
        },
        function (res) {
            layer.msg('修改失败！', {icon:2, time: 2000});
            refreshTable();
        }
    );
}

// 顶部工具栏事件
table.on('toolbar(dataTable)', function(obj){
    let id = obj.config.id; // 获取表格ID
    let checkStatus = table.checkStatus(id); // 获取表格中复选框选中的行
    switch(obj.event) {
        case 'create' : {
            layer.open({
                title: '新增CDK',
                type: 2,
                content: 'create.html',
                area: ['700px', '400px']
            });
            break;
        }
        case 'editReuseRemainingTimesBatch': {
            let checkedRows = checkStatus.data;
            // console.log(checkedRows);
            if(checkedRows.length === 0) {
                layer.msg('未选中任何一行！', {time: 2000});
            } else {
                let toBeUpdatedIdList = [];
                for(let row of checkedRows) {
                    if(row.typeReusable === false) {
                        layer.msg('选中的CDK中包含一次性CDK，无法修改可用次数！', {icon:2, time: 3000});
                        return;
                    }
                    toBeUpdatedIdList.push(row.cdkNo);
                }
                layer.prompt({
                    formType: 0,
                    value: '',
                    title: '批量修改CDK可用次数，选中数量：' + toBeUpdatedIdList.length,
                }, function(value, index, elem){
                    if(!isPositiveInteger(value)) {
                        layer.msg('请输入一个正整数！', {icon:2, time: 2000});
                        return;
                    }
                    updateRowsReuseRemainingTimes(toBeUpdatedIdList, value);
                    layer.close(index);
                    refreshTable();
                });
            }
            break;
        }
        case 'delete': {
            let checkedRows = checkStatus.data;
            // console.log(checkedRows);
            if(checkedRows.length === 0) {
                layer.msg('未选中任何一行！', {time: 2000});
            } else {
                layer.confirm('你选中了 ' + checkedRows.length + ' 行，确认删除？', {icon: 3, title:'提示'}, function (index) {
                    let toBeDeletedIdList = [];
                    for(let row of checkedRows) {
                        toBeDeletedIdList.push(row.cdkNo);
                    }
                    deleteRows(toBeDeletedIdList);
                });
            }
            break;
        }
    };
});

// 单元格内工具栏事件
table.on('tool(dataTable)', function(obj) {
    let rowData = obj.data;
    switch (obj.event) {
        case 'del': {
            layer.confirm('确认删除此行？', {icon: 3, title:'提示'}, function(index) {
                let toBeDeletedIdList = [rowData.cdkNo];
                deleteRows(toBeDeletedIdList);
            });
            break;
        }
        case 'editReuseRemainingTimes': {
            if(rowData.typeReusable === false) {
                layer.msg('该CDK是一次性CDK，无法修改可用次数！', {icon:2, time: 3000});
                return;
            }
            layer.prompt({
                formType: 0,
                value: rowData.reuseRemainingTimes,
                title: '修改CDK可用次数，编号：' + rowData.cdkNo,
            }, function(value, index, elem){
                if(!isPositiveInteger(value)) {
                    layer.msg('请输入一个正整数！', {icon:2, time: 2000});
                    return;
                }
                let toBeUpdatedIdList = [rowData.cdkNo];
                updateRowsReuseRemainingTimes(toBeUpdatedIdList, value);
                layer.close(index);
                refreshTable();
            });
            break;
        }
    }
});

function isPositiveInteger(number) {
    if(!$.isNumeric(number)) {
        return false;
    }
    return /^\d+$/.test(number) && number > 0;
}

// 触发表格复选框选择事件
// table.on('checkbox(accountTable)', function(obj){
//     console.log(obj)
// });
