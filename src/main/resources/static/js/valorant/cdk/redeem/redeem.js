layui.use('element', function(){
    var element = layui.element;
});

$(function () {
});

var requestData;

// 确认兑换信息
form.on('submit(redeemVerify)', function(data) {
    requestData = JSON.stringify(data.field);
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.redeemVerify'),
        requestData,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                layer.open({
                    title: '确认商品信息'
                    ,content: res.data.detail
                    ,btn: ['确认信息无误', '取消']
                    ,yes: function(index, layero) {
                        redeem(index);
                    }
                });
            } else {
                layer.msg(res.data.msg, {time: 2000});
            }
        },
        function (res) {
            layer.msg("请求失败！", {time: 2000});
        },
        function () {
            // this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            // layer.close(this.layerIndex);
        }
    );

    return false;
});

// 进行兑换
function redeem(layerIndex) {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.redeem'),
        requestData,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                layer.close(layerIndex);
                layer.msg("兑换成功！", {time: 2000})
            } else {
                layer.msg(res.msg, {time: 2000});
            }
        },
        function (res) {
            layer.msg("请求失败！", {time: 2000});
        },
        function () {
            // this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            // layer.close(this.layerIndex);
        }
    );
}

// 查询CDK兑换历史记录
form.on('submit(history)', function(data) {
    $("#history-list").html("");
    requestData = JSON.stringify(data.field);
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.history'),
        requestData,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                if(res.data.records != null && res.data.records.length > 0) {
                    let historyListHtml = '';
                    res.data.records.forEach(function (value) {
                        historyListHtml += value.detail;
                        historyListHtml += "兑换时间：" + value.redeemTime + "<br/><br/>"
                    });
                    $("#history-list").html(historyListHtml);
                } else {
                    layer.msg('无数据！', {time: 2000});
                }
            } else {
                layer.msg(res.msg, {time: 2000});
            }
        },
        function (res) {
            layer.msg("请求失败！", {time: 2000});
        },
        function () {
            // this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            // layer.close(this.layerIndex);
        }
    );

    return false;
});

function navChange1() {
    $("#redeem-form").css("display", "block");
    $("#history-form").css("display", "none");
    $("#history-list").html("");
}

function navChange2() {
    $("#redeem-form").css("display", "none");
    $("#history-form").css("display", "block");
    $("#history-list").html("");
}
