$(function () {
   loadAllSelect();
});

function loadAllSelect() {
    let region = [
        {name: "缅甸", value: 0},
        {name: "马来西亚", value: 1},
        {name: "香港", value: 2},
        {name: "泰国", value: 3},
    ];
    let priceOrder = [
        {name: "升序", value: 0},
        {name: "降序", value: 1},
    ];
    loadSelectFromJson(region, $("#region"), "name", "value");
    loadSelectFromJson(priceOrder, $("#priceOrder"), "name", "value");
}

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    // ,height: 'full'
    ,skin: 'nob'
    ,even: true
    ,size: 'lg'
    ,cols: [[ //表头
        {type: 'checkbox', fixed: 'left', hide: true}
        ,{field: 'accountNo', title: '', width: '10%', sort: false, hide: false, align: 'center'}
        // ,{field: 'title', title: '标题（点击可查看账号详情）', width: '40%', sort: false, hide: false, align: 'center',
        //     templet: function (d) {
        //         return '<a target="_blank" onclick="getOne(' + d.accountNo + ')" style="cursor: pointer; text-decoration: solid #cccccc; font-weight: bold;">' + d.title + '</a>';
        //     }}
        ,{field: 'price', title: '', width: '10%', sort: false, align: 'center',
            templet: function (d) {
                return '<div style="color: red; font-size: 25px; font-weight: bold;">￥ ' + d.price + '</div>';
            }}
        ,{field: 'img', title: '', width:'40%', sort: false, hide: false, align: 'center', style: 'height:310px;',
            templet: function (d) {
                return '<img src="' + d.img + '" onclick="previewImg(this)" width="500px" height="375px">';
            }}
        ,{field: 'region', title: '', width: '20%', sort: false, align: 'center',
            templet: function (d) {
                return regionCodeToStr(d.region);
            }
        }
        ,{field: 'status', title: '', width: '20%', sort: false, align: 'center',
            templet: function (d) {
                return statusCodeToStr(d.status);
            }
        }
    ]]
    // ,toolbar: '#toolbar'
    ,defaultToolbar: [] //清空默认的三个工具栏按钮
    ,totalRow: false
    ,loading: true

    ,url: app.util.api.getAPIUrl('valorant.endProduct.account.query')
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
                skin: $("#skin").val(),
                accountNo: $("#accountNo").val(),
                region: $("#region").val(),
                status: $("#status").val(),
                priceLow: $("#priceLow").val(),
                priceHigh: $("#priceHigh").val(),
            }
        });
    },
    clear: function () {
        $("#skin").val("");
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

// 顶部工具栏事件
table.on('toolbar(dataTable)', function(obj){
    let id = obj.config.id; // 获取表格ID
    let checkStatus = table.checkStatus(id); // 获取表格中复选框选中的行
    switch(obj.event) {

    }
});

// 单元格内工具栏事件
table.on('tool(dataTable)', function(obj) {
    let rowData = obj.data;
    switch (obj.event) {

    }
});

// 触发表格复选框选择事件
// table.on('checkbox(accountTable)', function(obj){
//     console.log(obj)
// });

function getOne(accountNo) {
    layer.open({
        title: '成品号详细信息，编号：' + accountNo,
        type: 2,
        content: 'detail.html?accountNo=' + accountNo,
        area: ['1200px', '900px'],
        shadeClose: true
    });
}

function regionCodeToStr(region) {
    switch (region) {
        case 0: return "缅甸";
        case 1: return "马来西亚";
        case 2: return "香港";
        case 3: return "泰国";
        default: return ""
    }
}

function statusCodeToStr(status) {
    switch (status) {
        case 1: return "在售";
        case 2: return "出租";
        case 10: return "已售出";
        default: return ""
    }
}

var bodyWidth = $('body').width(),
    bodyHeight = $('body').height(),
    htmlWidth = $('html').width(),
    htmlHeight = $('html').height();
console.log('bodyWidth='+bodyWidth + ' bodyHeight='+bodyHeight + ' htmlWidth='+htmlWidth + ' htmlHeight='+htmlHeight);

function previewImg(obj) {
    var img = new Image();
    img.src = obj.src;
    // var height = img.height + 50; //获取图片高度
    // var width = img.width; //获取图片宽度
    let width = bodyWidth*0.7;
    let height = width*0.75;
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
