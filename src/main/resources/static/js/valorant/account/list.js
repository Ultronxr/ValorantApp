var upload;
layui.define(function(){
    upload = layui.upload;

});

$(function () {
   loadAllSelect();
    upload.render({
        elem: '#import'
        ,url: app.util.api.getAPIUrl('valorant.account.import')
        ,headers: {}
        ,data: {}
        ,field: 'file'
        ,auto: true
        ,accept: 'file'
        ,acceptMime: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        ,exts: 'xlsx'
        ,multiple: false
        ,before: function (obj) {
            layer.load(2);
        }
        ,done: function(res, index, upload){
            layer.closeAll('loading');
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                layer.msg('导入成功。', {time: 2000});
                refreshTable();
            } else {
                layer.msg(res.msg, {time: 2000});
            }
        }
        ,error: function(index, upload){
            layer.closeAll('loading');
            layer.msg('文件上传失败！', {icon:2, time: 2000});
        }
    });
});

function loadAllSelect() {
    let hasEmailSelectOptions = [
        {name: "未验证初邮", value: false},
        {name: "带初邮", value: true}
    ];
    let isAuthFailureSelectOptions = [
        {name: "登录验证成功", value: false},
        {name: "用户名或密码错误", value: true}
    ];
    loadSelectFromJson(hasEmailSelectOptions, $("#hasEmail"), "name", "value");
    loadSelectFromJson(isAuthFailureSelectOptions, $("#isAuthFailure"), "name", "value");
}

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    ,height: 700
    ,even: true
    ,size: 'lg'
    ,cols: [[ //表头
        {type: 'checkbox', fixed: 'left'}
        ,{field: 'accountNo', title: '编号', width: '7%', sort: true, hide: false, align: 'center'}
        ,{field: 'userId', title: 'ID', sort: true, hide: false, align: 'center'}
        ,{field: 'username', title: '用户名', sort: true, align: 'center'}
        ,{field: 'hasEmail', title: '初邮', sort: true, align: 'center',
            templet: function (d) {
                if(d.hasEmail != null && d.hasEmail === true) {
                    return d.email;
                }
                return '未验证初邮';
            }
        }
        ,{field: 'isAuthFailure', title: '登录验证', sort: false, align: 'center',
            templet: function (d) {
                if(d.isAuthFailure != null && d.isAuthFailure === true) {
                    return '用户名或密码错误';
                }
                return '登录验证成功';
            }
        }
        ,{title:'操作', width: 125, minWidth: 125, fixed: 'right', toolbar: '#inlineToolbar', align: 'center'}
    ]]
    ,toolbar: '#toolbar'
    ,defaultToolbar: [] //清空默认的三个工具栏按钮
    ,totalRow: false
    ,loading: true

    ,url: app.util.api.getAPIUrl('valorant.account.query')
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
                accountNo: $("#accountNo").val(),
                userId: $("#userId").val(),
                username: $("#username").val(),
                hasEmail: $("#hasEmail").val(),
                isAuthFailure: $("#isAuthFailure").val(),
            }
        });
    },
    clear: function () {
        $("#accountNo").val("");
        $("#userId").val("");
        $("#username").val("");
        $("#hasEmail").val("");
        $("#isAuthFailure").val("");
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
    app.util.ajax.delete(app.util.api.getAPIUrl('valorant.account.delete'),
        {idList: toBeDeletedIdList.join()},
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

// 顶部工具栏事件
table.on('toolbar(dataTable)', function(obj){
    let id = obj.config.id; // 获取表格ID
    let checkStatus = table.checkStatus(id); // 获取表格中复选框选中的行
    switch(obj.event){
        case 'create' :
            layer.open({
                title: '新增拳头账号',
                type: 2,
                content: 'create.html',
                area: ['800px', '500px']
            });
            break;
        case 'delete':
            let checkedRows = checkStatus.data;
            // console.log(checkedRows);
            if(checkedRows.length === 0) {
                layer.msg('未选中任何一行！', {time: 2000});
            } else {
                layer.confirm('你选中了 ' + checkedRows.length + ' 行，确认删除？', {icon: 3, title:'提示'}, function (index) {
                    let toBeDeletedIdList = [];
                    for(let row of checkedRows) {
                        toBeDeletedIdList.push(row.userId);
                    }
                    deleteRows(toBeDeletedIdList);
                });
            }
            break;
    };
});

// 单元格内工具栏事件
table.on('tool(dataTable)', function(obj) {
    let rowData = obj.data;
    if(obj.event === 'del'){
        layer.confirm('确认删除此行？', {icon: 3, title:'提示'}, function(index) {
            let toBeDeletedIdList = [rowData.userId];
            deleteRows(toBeDeletedIdList);
        });
    } else if(obj.event === 'edit'){
        layer.open({
            title: '更新拳头账号',
            type: 2,
            content: 'update.html',
            area: ['800px', '500px'],
            success: function (layero, index) {
                // 往子页面添加内容
                // let body = layer.getChildFrame('body', index);
                // body.find('#content').append(editor.txt.html());
                // 执行子页面的函数
                let iframe = window[layero.find('iframe')[0]['name']];
                iframe.loadSelectAndSetRowData(rowData);
            }
        });
    }
});

function downloadImportResult () {
    var url = app.util.api.getAPIUrl("valorant.account.importResult");
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.responseType = "blob";  // 返回类型blob
    // xhr.setRequestHeader("Content-type", "application/json;charset=utf-8"); //这个看后端接口情况决定要不要写
    // 定义请求完成的处理函数，请求前也可以增加加载框/禁用下载按钮逻辑
    xhr.onload = function () {
        // 请求完成
        if (this.status === 200) {
            // 返回200
            var blob = this.response;
            var reader = new FileReader();
            reader.readAsDataURL(blob);  // 转换为base64，可以直接放入a表情href
            reader.onload = function (e) {
                // 转换完成，创建一个a标签用于下载
                var a = document.createElement('a');
                a.download = '拳头账号导入结果.xlsx';
                a.href = e.target.result;
                $("body").append(a);  // 修复firefox中无法触发click
                a.click();
                $(a).remove();
            }
        }
    };
    // 发送ajax请求
    // xhr.send(JSON.stringify(outData))
    xhr.send();
}

// 触发表格复选框选择事件
// table.on('checkbox(accountTable)', function(obj){
//     console.log(obj)
// });
