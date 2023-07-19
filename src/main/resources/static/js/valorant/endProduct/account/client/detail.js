$(function () {
    let accountNo = window.location.search.substring(1).split("=")[1];
    if(!$.isNumeric(accountNo)) {
        return;
    }

    app.util.ajax.get(app.util.api.getAPIUrl('valorant.endProduct.account.getOne'),
        {accountNo: accountNo},
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                $('#accountNo').html(res.data.accountNo);
                $('#title').html(res.data.title);
                $('#img').html('<img src="' + res.data.img + '" onclick="previewImg(this)">');
                $('#note').html(res.data.note);
                $('#price').html("￥ " + res.data.price);
                $('#region').html(regionCodeToStr(res.data.region));
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
        null
    );
});

$('#cancel-btn').on('click', function() {
    closeLayerWindow();
});

function regionCodeToStr(region) {
    switch (region) {
        case 0: return "缅甸";
        case 1: return "马来西亚";
        case 2: return "香港";
        case 3: return "泰国";
        default: return ""
    }
}

function previewImg(obj) {
    var img = new Image();
    img.src = obj.src;
    // var height = img.height + 50; //获取图片高度
    // var width = img.width; //获取图片宽度
    let width = $(window).width()*0.85;
    let height = width*0.5625;
    var imgHtml = "<img src='" + obj.src + "' width='100%' height='100%'/>";
    //弹出层
    layer.open({
        type: 1,
        shade: 0.8,
        offset: 'auto',
        area: [width + 'px', height + 'px'],
        shadeClose: true,
        scrollbar: false,
        title: "图片预览",
        content: imgHtml, //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响
        cancel: function () {
            //layer.msg('捕获就是从页面已经存在的元素上，包裹layer的结构', { time: 5000, icon: 6 });
        }
    });
}
