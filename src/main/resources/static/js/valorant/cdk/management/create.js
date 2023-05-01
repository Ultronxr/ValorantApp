$(function () {
});

form.on('submit(create)', function(data) {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.create'),
        JSON.stringify(data.field),
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                parent.layer.msg('新增CDK数量：' + res.data, {time: 3000});
                closeLayerWindow();
                parent.refreshTable();
            } else {
                parent.layer.msg(res.msg, {time: 2000});
            }
        },
        function (res) {
            parent.layer.msg("请求失败！", {time: 2000});
        },
        function () {
            this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            layer.close(this.layerIndex);
        }
    );

    return false;
});

// radio 选中事件
form.on('radio(typeReusable)', function(data) {
    let value = data.value;
    if(value === 'true') {
        $('#reuseRemainingTimes-div').css('display', 'block');
    } else {
        $('#reuseRemainingTimes-div').css('display', 'none');
    }
});

$('#cancel-btn').on('click', function() {
    closeLayerWindow();
});
