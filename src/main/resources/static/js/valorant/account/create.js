$(function () {
    loadAllSelect();
});

function loadAllSelect() {
    let region = [
        {name: "缅甸", value: 0},
        {name: "马来西亚", value: 1},
        // {name: "香港", value: 2},
        // {name: "泰国", value: 3},
    ];
    loadSelectFromJson(region, $("#region"), "name", "value");
}

form.on('submit(create)', function(data) {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.account.create'),
        JSON.stringify(data.field),
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                parent.layer.msg('新增成功。', {icon:1, time: 2000});
                closeLayerWindow();
                parent.refreshTable();
            } else {
                parent.layer.msg(res.msg, {icon:2, time: 2000});
            }
        },
        function (res) {
            if(res.status === 401) {
                parent.layer.msg('未授权！', {icon:2, time: 2000});
            } else {
                parent.layer.msg("请求失败！", {icon:2, time: 2000});
            }
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

form.on('submit(multiFactor)', function(data) {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.account.multiFactor'),
        JSON.stringify(data.field),
        function (res) {
            parent.layer.msg(res.msg, {icon:1, time: 2000});
        },
        function (res) {
            parent.layer.msg('请求错误！', {icon:2, time: 2000});
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

$('#cancel-btn').on('click', function() {
    closeLayerWindow();
});
