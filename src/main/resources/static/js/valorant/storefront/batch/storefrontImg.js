var ROW_DATA;

function setRowData(rowData) {
    ROW_DATA = rowData;
    // console.log(ROW_DATA);

    renderSingle();
    renderBonus();
}

function renderSingle() {
    if(ROW_DATA.displayNameList == null) {
        $('#img-list').html('无每日商店数据');
        return;
    }

    let html = '';
    for(let i = 0; i < ROW_DATA.displayNameList.length; i++) {
        let simpleTier = getSimpleTier(ROW_DATA.contentTierList[i]),
            cost = ROW_DATA.costList[i],
            skinImg = ROW_DATA.displayIconList[i],
            displayName = ROW_DATA.displayNameList[i];
        html += '<li>\n' +
            '            <div class="img-box" style="background-image: url(/static/assets/img/valorant/storefront/contentTierBackground/tier'+ simpleTier +'.png);">\n' +
            '                <div class="img-top">\n' +
            '                    <img class="vp-icon" src="/static/assets/img/valorant/storefront/VPIcon.png" alt="">\n' +
            '                    <span>'+ cost +'</span>\n' +
            '                    <img class="level-icon" src="/static/assets/img/valorant/storefront/contentTierIcon/tier'+ simpleTier +'.png">\n' +
            '                </div>\n' +
            '                <img class="skin-img" src="'+ skinImg +'" alt="">\n' +
            '                <div class="img-bottom">'+ displayName +'</div>\n' +
            '            </div>\n' +
            '        </li>';
    }
    $('#img-list').html(html);
}

function renderBonus() {
    if(ROW_DATA.bonusOffer == null) {
        $('#img-list-bonus').html('无夜市数据');
        return;
    }

    let html = '';
    for(let i = 0; i < ROW_DATA.bonusOffer.displayNameList.length; i++) {
        let simpleTier = getSimpleTier(ROW_DATA.bonusOffer.contentTierList[i]),
            cost = ROW_DATA.bonusOffer.costList[i],
            discountPercent = ROW_DATA.bonusOffer.discountPercentList[i],
            discountCost = ROW_DATA.bonusOffer.discountCostList[i],
            skinImg = ROW_DATA.bonusOffer.displayIconList[i],
            displayName = ROW_DATA.bonusOffer.displayNameList[i];
        html += '<li>\n' +
            '            <div class="img-box" style="background-image: url(/static/assets/img/valorant/storefront/contentTierBackground/tier'+ simpleTier +'.png);">\n' +
            '                <div class="img-top">\n' +
            '                    <div class="price-box">\n' +
            '                        <div class="discount">-'+ discountPercent +'%</div>\n' +
            '                        <div class="original-cost">'+ cost +'</div>\n' +
            '                    </div>\n' +
            '                    <img class="vp-icon" src="/static/assets/img/valorant/storefront/VPIcon.png" alt="">\n' +
            '                    <span>'+ discountCost +'</span>\n' +
            '                </div>\n' +
            '                <img class="skin-img" src="'+ skinImg +'" alt="">\n' +
            '                <div class="img-bottom">\n' +
            '                    <span>'+ displayName +'</span>\n' +
            '                    <img class="level-icon" src="/static/assets/img/valorant/storefront/contentTierIcon/tier'+ simpleTier +'.png">\n' +
            '                </div>\n' +
            '            </div>\n' +
            '        </li>';
    }
    $('#img-list-bonus').html(html);
}

// 武器等级
var TIER = {
    "12683d76-48d7-84a3-4e09-6985794f0445": "1", // 等级一（蓝色圆圈）
    "0cebb8be-46d7-c12a-d306-e9907bfc5a25": "2", // 等级二（绿色圆角菱形）
    "60bca009-4182-7998-dee7-b8a2558dc369": "3", // 等级三（红色三角）
    "411e4a55-4e59-7757-41f0-86a53f101bb5": "4", // 等级四（金色菱形）
    "e046854e-406c-37f4-6607-19a9ba8426fc": "5", // 等级五（橙色五边形）
};

function getSimpleTier(contentTierUuid) {
    return TIER[contentTierUuid];
}
