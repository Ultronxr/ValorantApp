$(function () {
    app.util.ajax.get(app.util.api.getAPIUrl('valorant.weaponAndSkin.selectAllSkin'),
        null,
        function (res) {
            // console.log(res);
            if(res.code === app.RESPONSE_CODE.SUCCESS) {
                loadAllSelect(res.data);
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

function loadAllSelect(selectData) {
    loadWeaponSkinFromJson(selectData[0], $('#melee'), 'melee');
    loadWeaponSkinFromJson(selectData[1], $('#phantom'), 'phantom');
    loadWeaponSkinFromJson(selectData[2], $('#vandal'), 'vandal');
    loadWeaponSkinFromJson(selectData[3], $('#bulldog'), 'bulldog');
    loadWeaponSkinFromJson(selectData[4], $('#guardian'), 'guardian');
    loadWeaponSkinFromJson(selectData[5], $('#operator'), 'operator');
    loadWeaponSkinFromJson(selectData[6], $('#marshal'), 'marshal');
    loadWeaponSkinFromJson(selectData[7], $('#spectre'), 'spectre');
    loadWeaponSkinFromJson(selectData[8], $('#stinger'), 'stinger');
    loadWeaponSkinFromJson(selectData[9], $('#sheriff'), 'sheriff');
    loadWeaponSkinFromJson(selectData[10], $('#ghost'), 'ghost');
    loadWeaponSkinFromJson(selectData[11], $('#frenzy'), 'frenzy');
    loadWeaponSkinFromJson(selectData[12], $('#shorty'), 'shorty');
    loadWeaponSkinFromJson(selectData[13], $('#classic'), 'classic');
    loadWeaponSkinFromJson(selectData[14], $('#odin'), 'odin');
    loadWeaponSkinFromJson(selectData[15], $('#ares'), 'ares');
    loadWeaponSkinFromJson(selectData[16], $('#judge'), 'judge');
    loadWeaponSkinFromJson(selectData[17], $('#bucky'), 'bucky');
    form.render('select');
}

function loadWeaponSkinFromJson(weaponSkinSelectVO, targetSelect, skinSearchObjKey) {
    let options = '<option value="">--- 请选择 ---</option>';
    for (let i = 0; i < weaponSkinSelectVO.skinList.length; ++i) {
        if(parent.SKIN_SEARCH_OBJ[skinSearchObjKey] === weaponSkinSelectVO.skinUuidList[i]) {
            options += '<option value="' + weaponSkinSelectVO.skinUuidList[i] + '" selected>' + weaponSkinSelectVO.skinList[i] + '</option>';
        } else {
            options += '<option value="' + weaponSkinSelectVO.skinUuidList[i] + '">' + weaponSkinSelectVO.skinList[i] + '</option>';
        }
    }
    targetSelect.html(options);
}

$('#cancel-btn').on('click', function() {
    closeLayerWindow();
});

form.on('submit(confirm)', function (data) {
    parent.SKIN_SEARCH_OBJ = data.field;
    parent.SKIN_SEARCH_ARRAY = [];
    $.each(parent.SKIN_SEARCH_OBJ, function (key, value) {
        if(app.util.string.isNotEmpty(value)) {
            parent.SKIN_SEARCH_ARRAY.push(value);
        }
    });
    console.log(parent.SKIN_SEARCH_OBJ);
    console.log(parent.SKIN_SEARCH_ARRAY);
    closeLayerWindow();
});
