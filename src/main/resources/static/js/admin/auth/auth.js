$(function () {
});

form.on('submit(login)', function(data) {
    app.util.ajax.post(app.util.api.getAPIUrl('system.adminAuth'),
        JSON.stringify(data.field),
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                layer.msg(res.msg, {icon: 1, time: 2000});
            } else {
                layer.msg(res.msg, {icon: 2, time: 2000});
            }
        },
        function (res) {
            layer.msg("请求失败！", {icon: 2, time: 2000});
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
