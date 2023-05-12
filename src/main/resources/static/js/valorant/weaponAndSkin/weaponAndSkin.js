function updateDB() {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.weaponAndSkin.updateDB'),
        null,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                parent.layer.msg('更新数据成功。', {icon:1, time: 2000});
            } else {
                parent.layer.msg('更新数据失败！', {icon:2, time: 2000});
            }
        },
        function (res) {
            if(res.status === 401) {
                parent.layer.msg('未授权！', {icon:2, time: 2000});
            } else {
                parent.layer.msg('请求失败！', {icon:2, time: 2000});
            }
        },
        function () {
            this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            layer.close(this.layerIndex);
        },
        true,
        200000
    );
}

function batchUpdateBoth() {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.storefront.batchUpdateBoth'),
        null,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                parent.layer.msg('更新数据成功。', {icon: 1, time: 2000});
            } else {
                parent.layer.msg('更新数据失败！', {icon: 2, time: 2000});
            }
        },
        function (res) {
            if(res.status === 401) {
                parent.layer.msg('未授权！', {icon:2, time: 2000});
            } else {
                parent.layer.msg('请求失败！', {icon: 2, time: 2000});
            }
        },
        function () {
            this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            layer.close(this.layerIndex);
        },
        true,
        8000000
    );
}
