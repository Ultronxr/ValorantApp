var dropdown;
layui.define(function(){
    dropdown = layui.dropdown;
});

var selectData = [
    {
        title: "步枪"
        , id: "步枪"
        , child: [
            {
                title: '暴徒'
                , id: '暴徒'
                , child: [
                    {
                        title: "掠夺印象 暴徒",
                        id: "掠夺印象 暴徒"
                    }, {
                        title: "异星霸主 暴徒",
                        id: "异星霸主 暴徒"
                    }, {
                        title: "盖亚复仇 暴徒",
                        id: "盖亚复仇 暴徒"
                    }, {
                        title: "紫金狂潮 暴徒",
                        id: "紫金狂潮 暴徒"
                    }, {
                        title: "离子圣芒 暴徒",
                        id: "离子圣芒 暴徒"
                    }, {
                        title: "海洋星球 暴徒",
                        id: "海洋星球 暴徒"
                    }, {
                        title: "恶鬼 暴徒",
                        id: "恶鬼 暴徒"
                    }, {
                        title: "电幻普普 暴徒",
                        id: "电幻普普 暴徒"
                    }, {
                        title: "至尊龙燄 暴徒",
                        id: "至尊龙燄 暴徒"
                    }, {
                        title: "虚无时界 暴徒",
                        id: "虚无时界 暴徒"
                    }, {
                        title: "起源 暴徒",
                        id: "起源 暴徒"
                    },
                ]
            },
            {
                title: '幻象',
                id: '幻象',
                child: [
                    {
                        title: "781-A协定 幻象",
                        id: "781-A协定 幻象"
                    }, {
                        title: "光炫声谱 幻象",
                        id: "光炫声谱 幻象"
                    }, {
                        title: "恶鬼 幻象",
                        id: "恶鬼 幻象"
                    }, {
                        title: "侦察行动 幻象",
                        id: "侦察行动 幻象"
                    }, {
                        title: "奇点 幻象",
                        id: "奇点 幻象"
                    }, {
                        title: "掠夺印象 幻象",
                        id: "掠夺印象 幻象"
                    }, {
                        title: "紫金狂潮//2.0 幻象",
                        id: "紫金狂潮//2.0 幻象"
                    }, {
                        title: "离子圣芒 幻象",
                        id: "离子圣芒 幻象"
                    }, {
                        title: "咻咻X 幻象",
                        id: "咻咻X 幻象"
                    }, {
                        title: "辐能危机001 幻象",
                        id: "辐能危机001 幻象"
                    }
                ]
            },
            {
                title: '斗牛犬'
                ,id: '斗牛犬'
                ,child: [
                    {
                        title: "781-A协定 斗牛犬",
                        id: "781-A协定 斗牛犬"
                    }, {
                        title: "光炫声谱 斗牛犬",
                        id: "光炫声谱 斗牛犬"
                    }, {
                        title: "异星霸主 斗牛犬",
                        id: "异星霸主 斗牛犬"
                    }, {
                        title: "恶鬼 斗牛犬",
                        id: "恶鬼 斗牛犬"
                    }, {
                        title: "太空漫游 斗牛犬",
                        id: "太空漫游 斗牛犬"
                    }, {
                        title: "电幻普普 斗牛犬",
                        id: "电幻普普 斗牛犬"
                    },
                ]
            },
            {
                title: "捍卫者"
                ,id: "捍卫者"
                ,child: [
                    {
                        title: "RGX 11z Pro 捍卫者",
                        id: "RGX 11z Pro 捍卫者"
                    }, {
                        title: "侦察行动 捍卫者",
                        id: "侦察行动 捍卫者"
                    }, {
                        title: "光炫声谱 捍卫者",
                        id: "光炫声谱 捍卫者"
                    }, {
                        title: "紫金狂潮 捍卫者",
                        id: "紫金狂潮 捍卫者"
                    }, {
                        title: "鎏金帝王 捍卫者",
                        id: "鎏金帝王 捍卫者"
                    }, {
                        title: "海洋星球 捍卫者",
                        id: "海洋星球 捍卫者"
                    }, {
                        title: "殒落诅咒 捍卫者",
                        id: "殒落诅咒 捍卫者"
                    }, {
                        title: "盖亚复仇 捍卫者",
                        id: "盖亚复仇 捍卫者"
                    }, {
                        title: "恶鬼 捍卫者",
                        id: "恶鬼 捍卫者"
                    }, {
                        title: "掠夺印象 捍卫者",
                        id: "掠夺印象 捍卫者"
                    }
                ]
            }
        ]
    },
    {
        title: "狙击枪"
        , id: "狙击枪"
        , child: [
            {
                title: "间谍"
                ,id: "间谍"
                ,child: [
                    {
                        title: "RGX 11z Pro 间谍",
                        id: "RGX 11z Pro 间谍"
                    }, {
                        title: "异星霸主 间谍",
                        id: "异星霸主 间谍"
                    }, {
                        title: "混沌序幕 间谍",
                        id: "混沌序幕 间谍"
                    }, {
                        title: "离子圣芒 间谍",
                        id: "离子圣芒 间谍"
                    }, {
                        title: "至尊龙燄 间谍",
                        id: "至尊龙燄 间谍"
                    }, {
                        title: "光之哨兵 间谍",
                        id: "光之哨兵 间谍"
                    }, {
                        title: "弃王遗思 间谍",
                        id: "弃王遗思 间谍"
                    }, {
                        title: "掠夺印象 间谍",
                        id: "掠夺印象 间谍"
                    }, {
                        title: "起源 间谍",
                        id: "起源 间谍"
                    }, {
                        title: "电幻普普 间谍",
                        id: "电幻普普 间谍"
                    },
                ]
            },
            {
                title: "警长"
                ,id: "警长"
                ,child: [
                    {
                        title: "盖亚复仇 警长",
                        id: "盖亚复仇 警长"
                    }, {
                        title: "鎏金帝王 警长",
                        id: "鎏金帝王 警长"
                    }
                ]
            }
        ]
    },
    {
        title: "手枪"
        , id: "手枪"
        , child: [
            {
                title: "制式手枪"
                ,id: "制式手枪"
                ,child: [
                    {
                        title: "光炫声谱 制式手枪",
                        id: "光炫声谱 制式手枪"
                    }, {
                        title: "紫金狂潮 制式手枪",
                        id: "紫金狂潮 制式手枪"
                    }, {
                        title: "RGX 11z Pro 制式手枪",
                        id: "RGX 11z Pro 制式手枪"
                    }, {
                        title: "电幻普普 制式手枪",
                        id: "电幻普普 制式手枪"
                    }, {
                        title: "弃王遗思 制式手枪",
                        id: "弃王遗思 制式手枪"
                    }, {
                        title: "辐能危机001 制式手枪",
                        id: "辐能危机001 制式手枪"
                    }
                ]
            },
            {
                title: "鬼魅"
                ,id: "鬼魅"
                ,child: [
                    {
                        title: "盖亚复仇 鬼魅",
                        id: "盖亚复仇 鬼魅"
                    }, {
                        title: "殒落诅咒 鬼魅",
                        id: "殒落诅咒 鬼魅"
                    }, {
                        title: "鎏金帝王 鬼魅",
                        id: "鎏金帝王 鬼魅"
                    }, {
                        title: "掠夺印象 鬼魅",
                        id: "掠夺印象 鬼魅"
                    }, {
                        title: "奇幻庞克 鬼魅",
                        id: "奇幻庞克 鬼魅"
                    }, {
                        title: "侦察行动 鬼魅",
                        id: "侦察行动 鬼魅"
                    }
                ]
            },
            {
                title: "神射"
                ,id: "神射"
                ,child: [
                    {
                        title: "781-A协定 神射",
                        id: "781-A协定 神射"
                    }, {
                        title: "奇点 神射",
                        id: "奇点 神射"
                    }, {
                        title: "光之哨兵 神射",
                        id: "光之哨兵 神射"
                    }, {
                        title: "离子圣芒 神射",
                        id: "离子圣芒 神射"
                    }, {
                        title: "掠夺印象 神射",
                        id: "掠夺印象 神射"
                    }, {
                        title: "虚无时界 神射",
                        id: "虚无时界 神射"
                    }, {
                        title: "奇幻庞克 神射",
                        id: "奇幻庞克 神射"
                    }
                ]
            }
        ]
    },
    {
        title: "刀"
        , id: "刀"
        , child: [
            {
                title: "RGX 11z Pro 萤光刃（显卡蝴蝶）",
                id: "RGX 11z Pro 萤光刃"
            }, {
                title: "RGX 11z Pro 直刀",
                id: "RGX 11z Pro 直刀"
            }, {
                title: "掠夺印象 爪刀",
                id: "掠夺印象 爪刀"
            }, {
                title: "紫金狂潮//2.0 爪刀",
                id: "紫金狂潮//2.0 爪刀"
            }, {
                title: "离子圣芒 爪刀",
                id: "离子圣芒 爪刀"
            }, {
                title: "侦察行动 蝴蝶刀",
                id: "侦察行动 蝴蝶刀"
            }, {
                title: "黑市 蝴蝶刀",
                id: "黑市 蝴蝶刀"
            }, {
                title: "盖亚之怒",
                id: "盖亚之怒"
            }, {
                title: "鬼丸国纲",
                id: "鬼丸国纲"
            }, {
                title: "天月耀春 折扇",
                id: "天月耀春 折扇"
            }, {
                title: "太极扇",
                id: "太极扇"
            }, {
                title: "殒落王者断刃",
                id: "殒落王者断刃"
            }, {
                title: "辐能危机001 球棒",
                id: "辐能危机001 球棒"
            }, {
                title: "特战英豪 GO！第1集 小刀",
                id: "特战英豪 GO！第1集 小刀"
            }, {
                title: "个人近战单位",
                id: "个人近战单位"
            },
        ]
    },
    {
        title: "皮肤系列关键词"
        , id: "皮肤系列关键词"
        , child: [
            {
                title: "781-A协定",
                id: "781-A协定"
            }, {
                title: "RGX 11z Pro",
                id: "RGX 11z Pro"
            }, {
                title: "光炫声谱",
                id: "光炫声谱"
            }, {
                title: "紫金狂潮",
                id: "紫金狂潮"
            }, {
                title: "混沌序幕",
                id: "混沌序幕"
            }, {
                title: "盖亚复仇",
                id: "盖亚复仇"
            }, {
                title: "掠夺印象",
                id: "掠夺印象"
            }, {
                title: "奇点",
                id: "奇点"
            }, {
                title: "离子圣芒",
                id: "离子圣芒"
            }, {
                title: "侦察行动",
                id: "侦察行动"
            }, {
                title: "恶鬼",
                id: "恶鬼"
            }, {
                title: "鎏金帝王",
                id: "鎏金帝王"
            }, {
                title: "海洋星球",
                id: "海洋星球"
            }, {
                title: "深度冻结",
                id: "深度冻结"
            }, {
                title: "至尊龙燄",
                id: "至尊龙燄"
            }, {
                title: "弃王遗思",
                id: "弃王遗思"
            }, {
                title: "虚无时界",
                id: "虚无时界"
            }, {
                title: "起源",
                id: "起源"
            }, {
                title: "电幻普普",
                id: "电幻普普"
            },
        ]
    },
];

$(function () {
    dropdown.render({
        elem: '#skin1' // 绑定的元素，可以是任意元素
        ,data: selectData
        ,id: 'skin1'
        //菜单被点击的事件
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
    dropdown.render({
        elem: '#skin2'
        ,data: selectData
        ,id: 'skin2'
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
    dropdown.render({
        elem: '#skin3'
        ,data: selectData
        ,id: 'skin3'
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
    dropdown.render({
        elem: '#skin4'
        ,data: selectData
        ,id: 'skin4'
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
    dropdown.render({
        elem: '#bonusSkin1'
        ,data: selectData
        ,id: 'bonusSkin1'
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
    dropdown.render({
        elem: '#bonusSkin2'
        ,data: selectData
        ,id: 'bonusSkin2'
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
    dropdown.render({
        elem: '#bonusSkin3'
        ,data: selectData
        ,id: 'bonusSkin3'
        ,click: function(obj, othis){
            $(this.elem).val(obj.id);
        }
    });
});

table.render({
    elem: '#data-table'
    ,id: 'dataTable'
    // ,height: 500
    ,toolbar: '#toolbar'
    ,defaultToolbar: [] //清空默认的三个工具栏按钮
    ,cols: [[ //表头
        {field: 'accountNo', title: '账号编号', sort: false, align: 'center', width: '5%', style: 'height:50px;',
            templet: '<div>{{=d.accountNo}}</div>'}
        ,{field: 'displayName', title: '每日商店', sort: false, align: 'center', width: '35%', style: 'height:50px;',
            templet: function (d) {
                let res = '';
                for(let i = 0; i < d.displayNameList.length; ++i) {
                    res += d.displayNameList[i] + ", ";
                }
                return res.slice(0, -2);
            }}
        ,{field: 'cost', title: '夜市', sort: false, align: 'center', width: '55%', style: 'height:50px;',
            templet: function (d) {
                if(d.bonusOffer != null) {
                    let res = '';
                    for(let i = 0; i < d.bonusOffer.displayNameList.length; ++i) {
                        res += d.bonusOffer.displayNameList[i] + ":" + d.bonusOffer.discountPercentList[i] + ", ";
                    }
                    return res.slice(0, -2);
                }
                return '夜市未开放';
            }}
        ,{title:'操作', sort: false, align: 'center', width: '5%', fixed: 'right', toolbar: '#inlineToolbar'}
    ]]
    ,totalRow: false
    ,loading: true

    ,url: app.util.api.getAPIUrl('valorant.storefront.batchQueryBoth')
    ,method: 'POST'
    ,contentType: 'application/json'
    ,headers: {}
    ,where: {
        skin1: $('#skin1').val(),
        skin2: $('#skin2').val(),
        skin3: $('#skin3').val(),
        skin4: $('#skin4').val(),
        bonusSkin1: $('#bonusSkin1').val(),
        bonusSkin2: $('#bonusSkin2').val(),
        bonusSkin3: $('#bonusSkin3').val(),
    }
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
});

// 刷新表格，不包含任何条件，恢复到初始状态
function refreshTable() {
    table.reloadData('dataTable', {
        where: {} // 清空搜索条件内容
    });
}

// 按钮的点击事件，关键数据：data-type="reload/clear"
var active = {
    reload: function(){
        table.reloadData('dataTable', {
            // page: {
            //     curr: 1 //重新从第 1 页开始
            // },
            where: { //设定异步数据接口的额外参数
                skin1: $('#skin1').val(),
                skin2: $('#skin2').val(),
                skin3: $('#skin3').val(),
                skin4: $('#skin4').val(),
                bonusSkin1: $('#bonusSkin1').val(),
                bonusSkin2: $('#bonusSkin2').val(),
                bonusSkin3: $('#bonusSkin3').val(),
            }
        });
    },
    clear: function(){
        $('#skin1').val('');
        $('#skin2').val('');
        $('#skin3').val('');
        $('#skin4').val('');
        $('#bonusSkin1').val('');
        $('#bonusSkin2').val('');
        $('#bonusSkin3').val('');
        table.reloadData('dataTable', {
            where: {}
        });
    },
};

// 监听按钮的点击事件
$('#table-div .layui-btn').on('click', function(){
    let type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
});

// 顶部工具栏事件
table.on('toolbar(dataTable)', function(obj){
    switch(obj.event){
        case 'batchUpdateBoth' :
            batchUpdateBoth();
            break;
    };
});

function batchUpdateBoth() {
    app.util.ajax.post(app.util.api.getAPIUrl('valorant.storefront.batchUpdateBoth'),
        null,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                parent.layer.msg('更新数据成功。', {time: 2000});
            } else {
                parent.layer.msg('更新数据失败！', {time: 2000});
            }
        },
        function (res) {
            parent.layer.msg('请求失败！', {time: 2000});
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

// 单元格内工具栏事件
table.on('tool(dataTable)', function(obj) {
    let rowData = obj.data;
    if(obj.event === 'showPic') {
        layerOpenImg(rowData);
    }
});

function layerOpenImg(rowData) {
    let layerWidth = '1500px',
        layerHeight = '670px';
    // 无夜市数据时，缩短弹窗高度
    if(rowData.bonusOffer == null) {
        layerHeight = '340px';
    }

    layer.open({
        title: '商店信息 编号：' + rowData.accountNo,
        type: 2,
        content: ['/static/html/valorant/storefront/batch/storefrontImg.html', 'no'],
        area: [layerWidth, layerHeight],
        shadeClose: true,
        success: function (layero, index) {
            // 执行子页面的函数
            let iframe = window[layero.find('iframe')[0]['name']];
            iframe.setRowData(rowData);
        }
    });
}
