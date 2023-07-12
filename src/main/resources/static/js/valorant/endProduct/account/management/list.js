$(function () {
   loadAllSelect();
});

function loadAllSelect() {
    // let typeHasEmail = [
    //     {name: "未验证初邮CDK（黄金版）", value: false},
    //     {name: "带初邮CDK（完全版）", value: true}
    // ];
    let region = [
        {name: "缅甸", value: 0},
        {name: "马来西亚", value: 1},
        {name: "香港", value: 2},
        {name: "泰国", value: 3},
    ];
    let status = [
        {name: "在售", value: 1},
        {name: "出租", value: 2},
        {name: "已售出", value: 10},
    ];
    loadSelectFromJson(region, $("#region"), "name", "value");
    loadSelectFromJson(status, $("#status"), "name", "value");
}

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    ,height: 700
    ,even: true
    ,size: 'lg'
    ,cols: [[ //表头
        {type: 'checkbox', fixed: 'left'}
        ,{field: 'accountNo', title: '账号编号', width: '7%', sort: true, hide: false, align: 'center'}
        ,{field: 'region', title: '地区', sort: true, align: 'center',
            templet: function (d) {
                switch (d.typeReusable) {
                    case 0: return "缅甸";
                    case 1: return "马来西亚";
                    case 2: return "香港";
                    case 3: return "泰国";
                    default: return ""
                }
            }
        }
        ,{field: 'price', title: '价格', sort: true, align: 'center'}
        ,{field: 'status', title: '状态', sort: true, align: 'center',
            templet: function (d) {
                switch (d.typeReusable) {
                    case 1: return "在售";
                    case 2: return "出租";
                    case 10: return "已售出";
                    default: return ""
                }
            }
        }
        ,{field: 'title', title: '标题', width: '7%', sort: true, hide: false, align: 'center'}
        ,{field: 'img', title: '账号截图', width:'40%', sort: false, hide: false, align: 'center'}
        // ,{field: 'typeHasEmail', title: '是否带初邮', sort: true, align: 'center',
        //     templet: function (d) {
        //         if(d.typeHasEmail != null && d.typeHasEmail === true) {
        //             return '带初邮CDK（完全版）';
        //         }
        //         return '未验证初邮（黄金版）';
        //     }
        // }
        ,{title:'操作', width: '10%', align: 'center', fixed: 'right', toolbar: '#inlineToolbar'}
    ]]
    ,toolbar: '#toolbar'
    ,defaultToolbar: [] //清空默认的三个工具栏按钮
    ,totalRow: false
    ,loading: true

    ,url: app.util.api.getAPIUrl('valorant.endProduct.account.management.query')
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
                accountNo: $("#accountNo").val(),
                region: $("#region").val(),
                status: $("#status").val(),
                priceLow: $("#priceLow").val(),
                priceHigh: $("#priceHigh").val(),
            }
        });
    },
    clear: function () {
        $("#accountNo").val("");
        $("#region").val("");
        $("#status").val("");
        $("#priceLow").val("");
        $("#priceHigh").val("");
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
// function deleteRows(toBeDeletedIdList) {
//     app.util.ajax.delete(app.util.api.getAPIUrl('valorant.endProduct.account.management.delete'),
//         {accountNoList: toBeDeletedIdList.join()},
//         function (res) {
//             // console.log(res);
//             if(res.code === app.RESPONSE_CODE.SUCCESS) {
//                 layer.msg('删除成功。', {icon: 1, time: 2000});
//                 refreshTable();
//             }
//         },
//         function (res) {
//             if(res.status === 401) {
//                 layer.msg('未授权！', {icon:2, time: 2000});
//             } else {
//                 layer.msg('请求失败！', {icon:2, time: 2000});
//             }
//             refreshTable();
//         }
//     );
// }

// 修改选中的行
// function updateRowsReuseRemainingTimes(toBeUpdatedCDKNoList, reuseRemainingTimes) {
//     app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.updateReuseRemainingTimes'),
//         {reusableCDKNoList: toBeUpdatedCDKNoList.join(), reuseRemainingTimes: reuseRemainingTimes},
//         function (res) {
//             // console.log(res);
//             if(res.code === app.RESPONSE_CODE.SUCCESS) {
//                 layer.msg('修改成功。', {icon: 1, time: 2000});
//                 refreshTable();
//             }
//         },
//         function (res) {
//             if(res.status === 401) {
//                 layer.msg('未授权！', {icon:2, time: 2000});
//             } else {
//                 layer.msg('请求失败！', {icon:2, time: 2000});
//             }
//             refreshTable();
//         }
//     );
// }

// 顶部工具栏事件
table.on('toolbar(dataTable)', function(obj){
    let id = obj.config.id; // 获取表格ID
    let checkStatus = table.checkStatus(id); // 获取表格中复选框选中的行
    switch(obj.event) {
        case 'create' : {
            layer.open({
                title: '新增成品号',
                type: 2,
                content: 'create.html',
                area: ['700px', '800px']
            });
            break;
        }
        // case 'editReuseRemainingTimesBatch': {
        //     let checkedRows = checkStatus.data;
        //     // console.log(checkedRows);
        //     if(checkedRows.length === 0) {
        //         layer.msg('未选中任何一行！', {icon: 2, time: 2000});
        //     } else {
        //         let toBeUpdatedIdList = [];
        //         for(let row of checkedRows) {
        //             if(row.typeReusable === false) {
        //                 layer.msg('选中的CDK中包含一次性CDK，无法修改可用次数！', {icon:2, time: 3000});
        //                 return;
        //             }
        //             toBeUpdatedIdList.push(row.cdkNo);
        //         }
        //         layer.prompt({
        //             formType: 0,
        //             value: '',
        //             title: '批量修改CDK可用次数，选中数量：' + toBeUpdatedIdList.length,
        //         }, function(value, index, elem){
        //             if(!isPositiveInteger(value)) {
        //                 layer.msg('请输入一个正整数！', {icon:2, time: 2000});
        //                 return;
        //             }
        //             updateRowsReuseRemainingTimes(toBeUpdatedIdList, value);
        //             layer.close(index);
        //             refreshTable();
        //         });
        //     }
        //     break;
        // }
        // case 'delete': {
        //     let checkedRows = checkStatus.data;
        //     // console.log(checkedRows);
        //     if(checkedRows.length === 0) {
        //         layer.msg('未选中任何一行！', {icon: 2, time: 2000});
        //     } else {
        //         layer.confirm('你选中了 ' + checkedRows.length + ' 行，确认删除？', {icon: 3, title:'提示'}, function (index) {
        //             let toBeDeletedIdList = [];
        //             for(let row of checkedRows) {
        //                 toBeDeletedIdList.push(row.accountNo);
        //             }
        //             deleteRows(toBeDeletedIdList);
        //         });
        //     }
        //     break;
        // }
    };
});

// 单元格内工具栏事件
table.on('tool(dataTable)', function(obj) {
    let rowData = obj.data;
    switch (obj.event) {
        case 'update': {
            layer.open({
                title: '修改成品号信息',
                type: 2,
                content: 'update.html?accountNo=' + rowData.accountNo,
                area: ['700px', '800px']
            });
            break;
        }
        case 'redeem': {
            layer.prompt({
                formType: 0,
                value: '',
                title: '正在提号，需要验证提号密码：',
            }, function(value, index, elem) {
                if(value === null || value === '') {
                    layer.msg('提号密码不能为空！', {icon:2, time: 2000});
                    return;
                }
                app.util.ajax.post(app.util.api.getAPIUrl('valorant.endProduct.account.management.redeem'),
                    JSON.stringify({accountNo: rowData.accountNo, xSecret: value}),
                    function (res) {
                        // console.log(res);
                        if(res.code === app.RESPONSE_CODE.SUCCESS) {
                            layer.open({
                                title: '兑换成功！'
                                ,content: res.data.detail
                                ,btn: ['复制账号信息', '确定']
                                ,yes: function(index, layero) {
                                    copyRedeemInfoToClipboard(res.data.detail, index);
                                    return false;
                                }
                            });
                        } else {
                            parent.layer.msg(res.msg, {icon: 2, time: 2000});
                        }
                    },
                    function (res) {
                        if(res.status === 401) {
                            parent.layer.msg("未授权！", {icon: 2, time: 2000});
                        } else {
                            parent.layer.msg("请求失败！", {icon: 2, time: 2000});
                        }
                    },
                    function () {
                        this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
                    },
                    function () {
                        layer.close(this.layerIndex);
                    }
                );
            });
            break;
        }
        // case 'del': {
        //     layer.confirm('确认删除此行？', {icon: 3, title:'提示'}, function(index) {
        //         let toBeDeletedIdList = [rowData.accountNo];
        //         deleteRows(toBeDeletedIdList);
        //     });
        //     break;
        // }
        // case 'editReuseRemainingTimes': {
        //     if(rowData.typeReusable === false) {
        //         layer.msg('该CDK是一次性CDK，无法修改可用次数！', {icon:2, time: 3000});
        //         return;
        //     }
        //     layer.prompt({
        //         formType: 0,
        //         value: rowData.reuseRemainingTimes,
        //         title: '修改CDK可用次数，编号：' + rowData.cdkNo,
        //     }, function(value, index, elem){
        //         if(!isPositiveInteger(value)) {
        //             layer.msg('请输入一个正整数！', {icon:2, time: 2000});
        //             return;
        //         }
        //         let toBeUpdatedIdList = [rowData.cdkNo];
        //         updateRowsReuseRemainingTimes(toBeUpdatedIdList, value);
        //         layer.close(index);
        //         refreshTable();
        //     });
        //     break;
        // }
    }
});

// 触发表格复选框选择事件
// table.on('checkbox(accountTable)', function(obj){
//     console.log(obj)
// });

// 复制兑换成功的内容到剪切板
function copyRedeemInfoToClipboard(redeemInfo, layero) {
    redeemInfo = redeemInfo.substring(redeemInfo.indexOf("拳头账号"));
    redeemInfo = redeemInfo.replaceAll("<br/>", "");

    if(navigator.clipboard && window.isSecureContext) {
        copyToClipboardUsingAPI(redeemInfo, layero);
        return;
    }
    copyToClipboardUsingExecCommand(redeemInfo, layero);
}

function copyToClipboardUsingAPI(content) {
    navigator.permissions.query({ name: "clipboard-write" }).then((result) => {
        if (result.state == "granted" || result.state == "prompt") {
            console.log("已获取剪切板写入权限。");
        }
        // 如果没有权限会自动获取，这里不需要 else 操作
    });
    navigator.clipboard.writeText(content).then(() => {
        // 剪切板写入成功回调
        // layer.msg("已复制账号信息！", {icon:1, time: 2000});
        alert("已复制账号信息 ！");
    },() => {
        // 剪切板写入失败（拒绝）回调
        // layer.msg("复制账号信息失败（剪切板写入失败），请手动选择文本内容并复制！", {icon:2, time: 2000});
        alert("复制账号信息失败（剪切板写入失败），请手动选择文本内容并复制 ！");
    });
}

function copyToClipboardUsingExecCommand(content) {
    $.copy({
        content: content,
        callback: function() {
            // layer.msg("已复制账号信息！", {icon:1, time: 2000});
            alert("已复制账号信息！");
        }
    });
}
