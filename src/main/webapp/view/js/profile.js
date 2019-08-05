/**
 * Created by Hello world on 2017/12/17.
 */

$(function() {
    var openChoiceAvatar = function () {
        $.actions({
            actions: [{
                text: "从相册选择",
                onClick: function() {
                    //do something
                    $('#avatarInput').click();
                }
            }]
        });
    }

    //初始化
    $(".loading_process").hide();

    $("#modifyAvatar").click(function () {
        openChoiceAvatar();
    });

    $("#avatarInput").change(function () {
        $("#avatarForm").submit();
    });

    $("#sexPicker").picker({
        title: "请选择您的性别",
        cols: [
            {
                textAlign: 'center',
                values: ['男', '女']
            }
        ],
        onClose:function(){//请求
            var sex = $("#sexPicker").val();
            $.ajax({
                type: "POST",
                url: $("#sexPicker").attr("data-url"),
                async: true,
                beforeSend: function () {
                    $(".loading_process").show();
                },
                data: "sex="+sex,
                success: function (data) {
                    $(".loading_process").hide();
                    if (data === null || data === undefined) {
                        $.toast("网络错误", "cancel");
                    } else if (typeof(data) === "object") {
                        if (data.status === "error") {
                            $.toast(data.content, "cancel");
                        } else if (data.status === "success") {
                            $.toast("修改成功", "success");
                        } else {
                            $.toast("未知错误", "cancel");
                        }
                    } else {
                        $.toast("发送出错", "cancel");
                    }
                },
                error: function (request, msg, e) {
                    $(".loading_process").hide();
                    if (msg === null || msg === undefined || msg === "error") {
                        $.toast("网络连接失败", "cancel");
                    } else {
                        $.toast(msg,"cancel");
                    }
                }
            });}
    });
})