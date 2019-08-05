/**
 * Created by Hello world on 2017/12/17.
 */

$(function() {
    function isPhoneNo(phone) {
        var pattern = /^1[34578]\d{9}$/;
        return pattern.test(phone);
    }
    var countdown = 60;
    function settime(obj) { //发送验证码倒计时
        if (countdown == 0) {
            obj.attr('disabled',false);
            obj.removeAttr("disabled");
            obj.removeClass("weui-btn_disabled");
            obj.val("获取验证码");
            countdown = 60;
            return;
        } else {
            obj.attr('disabled',true);
            obj.addClass('weui-btn_disabled');
            obj.val("重新发送(" + countdown + ")");
            countdown--;
        }
        setTimeout(function() {
                settime(obj) }
            ,1000)
    }

    //初始化
    $(".loading_process").hide();

    $("#vcode-btn").click(function () {
        //检查参数
        var tel = $("#tel").val();
        if(!isPhoneNo(tel)){
            $.toast("请填写正确的手机号", "cancel");
            return false;
        }

        //请求
        $.ajax({
            type: "POST",
            url: $("#vcode-btn").attr("data-url"),
            async: true,
            beforeSend: function () {
                $(".loading_process").show();
            },
            data: "tel="+tel,
            success: function (data) {
                $(".loading_process").hide();
                if (data === null || data === undefined) {
                    $.toast("网络错误", "cancel");
                } else if (typeof(data) === "object") {
                    if (data.status === "error") {
                        $.toast(data.content, "cancel");
                    } else if (data.status === "success") {
                        $.toast("短信已发送", "success");
                        settime($("#vcode-btn"));
                        $("#vcode").val(data.vcode);
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
        });
        return false;
    });


})