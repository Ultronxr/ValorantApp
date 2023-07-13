$(function () {
});

/**
 * 填充行数据，此方法由父页面（list页）点击按钮时调用
 * @param rowData 父页面传递而来的行数据
 */
function setRowData(rowData) {
    ROW_DATA = rowData;
    if(ROW_DATA == null) {
        return;
    }
    $('#accountNo').val(ROW_DATA.accountNo);
    $('#title').val(ROW_DATA.title);
    $('#price').val(ROW_DATA.price);
    $('#note').val(ROW_DATA.note);
    // 设置radio选中
    form.val('dataForm', {
        'region': ROW_DATA.region
        ,'status': ROW_DATA.status
    });
}

form.on('submit(update)', function(data) {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.endProduct.account.management.update'),
        JSON.stringify(data.field),
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                parent.layer.msg('更新成功。', {icon: 1, time: 3000});
                closeLayerWindow();
                parent.refreshTable();
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
        },
        null,
        300000
    );

    return false;
});

// radio 选中事件
// form.on('radio(typeHasEmail)', function(data) {
//     let value = data.value;
//     if(value === 'true') {
//         $('.email-div').css('display', 'block');
//         $('#email').attr('lay-verify', 'required|email');
//         $('#emailPwd').attr('lay-verify', 'required');
//     } else {
//         $('.email-div').css('display', 'none');
//         $('#email').removeAttr('lay-verify');
//         $('#emailPwd').removeAttr('lay-verify');
//         $('#email').val('');
//         $('#emailPwd').val('');
//     }
// });

$('#cancel-btn').on('click', function() {
    closeLayerWindow();
});
