var element, util, $ = layui.$;
layui.define(function() {
    element = layui.element;
    util = layui.util;
});

$(function () {
    // active.addTab("/static/html/valorant/cdk/redeem/redeem.html", "提号", "layMenuID01");
    active.addTab("/static/html/valorant/storefront/batch/both.html", "选号", "layMenuID02");
});

var active = {
    /**
     * 点击左侧垂直导航菜单栏时触发的事件：自动联动tab标签页
     */
    addTab: function(menuUrl, menuName, menuID) {
        //新增一个Tab项
        if(isEmpty(menuUrl)) {
            menuUrl = $(this).attr('data-url');
        }
        if(isEmpty(menuName)) {
            menuName = $(this).attr('data-name');
        }
        if(isEmpty(menuID)) {
            menuID = $(this).attr('data-id');
        }

        // 先判断是否已经有了这个 tab
        // 把当前已打开的所有 tab 的 ID 全部存入一个数组，然后检查即将打开的 tab ID 是否存在于这个数组内
        let arrayObj = [];
        $(".layui-tab-title").find('li').each(function() {
            let tabLayID = $(this).attr("lay-id");
            arrayObj.push(tabLayID);
        });

        let have = $.inArray(menuID, arrayObj);
        if (have >= 0) {
            // 已有相同tab
            element.tabChange('tabFilter', menuID); // 切换到当前点击的页面
            // console.log("跳转已有tab选项卡，layMenuID=" + menuID);
        } else{
            // 没有相同tab
            let contentHtml = '<iframe style="width: 100%;height: 100%;" src='+menuUrl+' ></iframe>';
            // 首页
            if(menuID === "layMenuID00") {
                contentHtml = $("#indexTab").html();
            }
            element.tabAdd('tabFilter', {
                title: menuName
                ,content: contentHtml
                ,id: menuID
            });
            element.tabChange('tabFilter', menuID); // 切换到当前点击的页面
            // console.log("新建tab选项卡，layMenuID=" + menuID);
        }
        element.render("tab", "tabFilter");
    }
};

/**
 * 点击左侧垂直导航菜单栏时触发的事件：自动联动tab标签页
 */
element.on('nav(leftNavFilter)', function(elem){
    // 需要联动 tab 选项卡的 nav 菜单栏必须加上 class="left-nav-menu" 才能生效
    if($(elem).hasClass("left-nav-menu")) {
        let menuUrl = $(elem).attr('data-url'),
            menuName = $(elem).attr('data-name'),
            menuID = $(elem).attr('data-id');
        active.addTab(menuUrl, menuName, menuID);
    }
});

/**
 * 点击tab标签页时触发的事件：自动联动左侧垂直导航菜单栏
 */
element.on('tab(tabFilter)', function(data) {
    let layID = $(this).attr("lay-id");
    let navTree = $(".layui-nav-tree");

    // 移除其他选中的 nav 菜单，并且折叠菜单组
    navTree.find(".layui-this").removeClass("layui-this");
    navTree.find(".layui-nav-itemed").removeClass("layui-nav-itemed");

    // 选中点击的 tab 对应的 nav 菜单，并且展开菜单组
    let kv = "a[data-id='" + layID + "']";
    navTree.find(kv).parent().addClass("layui-this");
    navTree.find(kv).parents(".layui-nav-item").addClass("layui-nav-itemed");

    element.render("nav", "navFilter");
    // console.log("联动左侧垂直导航栏，layMenuID=" + layID);
});

function isEmpty (str) {
    return str === undefined || str === null || str === "";
}
