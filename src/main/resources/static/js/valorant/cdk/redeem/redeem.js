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
                // layer.msg(res.data.state.msg, {icon:2, time: 2000});
                alert(res.data.state.msg);
            }
        },
        function (res) {
            // layer.msg("请求失败！", {icon:2, time: 2000});
            alert("请求失败！");
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
                // layer.msg(res.data.state.msg, {icon:2, time: 2000});
                alert(res.data.state.msg);
            }
        },
        function (res) {
            // layer.msg("请求失败！", {icon:2, time: 2000});
            alert("请求失败！");
        },
        function () {
            // this.layerIndex = layer.load(2, { shade: [0.2, '#ccc'] });
        },
        function () {
            // layer.close(this.layerIndex);
        }
    );
}

// 复制兑换成功的内容到剪切板
function copyRedeemInfoToClipboard(redeemInfo, layero) {
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
    // // 创建text area
    // let textArea = document.createElement("textarea");
    // textArea.value = content;
    // // 使text area不在viewport，同时设置不可见
    // textArea.style.position = "absolute";
    // textArea.style.opacity = '0';
    // textArea.style.left = "-999999px";
    // textArea.style.top = "-999999px";
    // document.body.appendChild(textArea);
    // textArea.focus();
    // textArea.select();
    // new Promise((res, rej) => {
    //     // 执行复制命令并移除文本框
    //     document.execCommand('copy') ? res(null) : rej();
    //     textArea.remove();
    // }).then(() => {
    //     /* Resolved - text copied to clipboard */
    //     parent.layer.msg("已复制账号信息！(2)", {icon:1, time: 2000});
    // }, () => {
    //     /* Rejected - clipboard failed */
    //     parent.layer.msg("复制账号信息进剪切板失败！(2)", {icon:2, time: 2000});
    // });
}

// 查询CDK兑换历史记录
form.on('submit(history)', function(data) {
    $("#history-list").html("");
    requestData = data.field;
    requestData["current"] = 1;
    requestData["size"] = 99999;
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.cdk.historyAndMoreCDKInfo'),
        JSON.stringify(requestData),
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                let records = res.data.history.records;
                if(records != null && records.length > 0) {
                    let historyListHtml = '当前CDK剩余可用次数：' + res.data.reuseRemainingTimes + '<br/><br/>';
                    records.forEach(function (value) {
                        historyListHtml += value.detail;
                        historyListHtml += "兑换时间：" + value.redeemTime + "<br/><br/>"
                    });
                    $("#history-list").html(historyListHtml);
                } else {
                    layer.msg('无数据！', {icon:2, time: 2000});
                }
            } else {
                layer.msg(res.msg, {icon:2, time: 2000});
            }
        },
        function (res) {
            layer.msg("请求失败！", {icon:2, time: 2000});
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
