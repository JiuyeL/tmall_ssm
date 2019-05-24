<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isELIgnored="false" %>


<script>
    /*
                表单校验
                1.用户名：单词字符，8---20位的长度
                2.密码：单词字符，长度8--20位
                3.email:邮件格式
                4.姓名：非空
                5.手机号：手机号格式
                6.出生日期：非空
                7.验证码：非空
             */
    function checkPassword() {
        //1. 获取密码
        var password = $("#password").val();
        //2. 定义正则表达式
        var reg_password = /^\w{6,18}$/;
        //3.判断，并给出提示信息
        var flag = reg_password.test(password);
        if (flag) {
            $("#password").css("border", "");
            $("div.registerErrorMessageDiv").css("visibility", "hidden");
        } else {
            $("span.errorMessage").html("请输入密码 6到18位");
            $("div.registerErrorMessageDiv").css("visibility", "visible");
            $("#password").css("border", "1px solid red");
        }
        return flag;
    }

    function checkPasswordAgain() {
        var password1 = $("#password").val();
        var password2 = $("#repeatpassword").val();
        if (password1 == password2 && password2.length > 0) {
            $("#repeatpassword").css("border", "");
            $("div.registerErrorMessageDiv").css("visibility", "hidden");
            flag = true;
        } else {
            $("span.errorMessage").html("两次输入密码不一样，请重新输入");
            $("div.registerErrorMessageDiv").css("visibility", "visible");
            $("#repeatpassword").css("border", "1px solid red");
            flag = "false";
        }
        return flag;
    }

    function checkName() {
        //1.获取昵称name
        var name = $("#name").val();
        //2.定义正则表达式
        var reg_name = /^[A-Za-z0-9\u4e00-\u9fa5]/;
        //3.校验
        var flag = reg_name.test(name);
        if (flag) {
            $("#name").css("border", "");
        } else {
            $("span.errorMessage").html("请输入用户名");
            $("div.registerErrorMessageDiv").css("visibility", "hidden");
            $("div.registerErrorMessageDiv").css("visibility", "visible");
            $("#name").css("border", "1px solid red");
        }
        return flag;
    }

    function checkTel() {
        //1.获取昵称name
        var tel = $("#telephone").val();
        //2.定义正则表达式
        var reg_tel = /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/;
        //3.校验
        var flag = reg_tel.test(tel);
        if (flag) {
            $("#telephone").css("border", "");
            $("div.registerErrorMessageDiv").css("visibility", "hidden");
        } else {
            $("span.errorMessage").html("请输入正确的手机号");
            $("div.registerErrorMessageDiv").css("visibility", "visible");
            $("#telephone").css("border", "1px solid red");
        }
        return flag;
    }

    function checkCode() {
        //1.获取昵称name
        var checkCode = $("#check").val();
        //2.定义正则表达式
        var reg_checkCode = /^[A-Za-z0-9]/;
        //3.校验
        var flag = reg_checkCode.test(checkCode);
        if (flag) {
            $("#check").css("border", "");
            $("div.registerErrorMessageDiv").css("visibility", "hidden");
        } else {
            $("span.errorMessage").html("验证码错误");
            $("div.registerErrorMessageDiv").css("visibility", "visible");
            $("#check").css("border", "1px solid red");
        }
        return flag;
    }

    //图片点击事件,刷新验证码
    function changeCheckCode(img) {
        img.src = "checkCode?" + new Date().getTime();
    }

    $(function () {
        <c:if test="${!empty msg}">
        $("span.errorMessage").html("${msg}");
        $("div.registerErrorMessageDiv").css("visibility", "visible");
        </c:if>

        //当表单提交时校验所有的方法
        $("#btn_submit").click(function () {
            $("#registerForm").submit(function () {
                //如果这个匿名函数没有返回值或者返回值为true，则提交表单；否则，不提交表单.
                if (checkUsername() && checkPassword() && checkEmail() && checkName() && checkTel() && checkBirthday() && checkCode()) {
                    //ajax提交数据
                    $.post("foreregister", $(this).serialize(), function (data) {
                        //处理服务器返回的数据

                    })
                }
                return false;
            });
        });
        //当某个组件失去焦点时，调用对应的方法进行验证
        $("#password").blur(checkPassword);
        $("#repeatpassword").blur(checkPasswordAgain);
        $("#name").blur(checkName);
        $("#telephone").blur(checkTel);
        $("#check").blur(checkCode);
    });
</script>


<form method="post" action="foreregister" id="registerForm" class="registerForm">


    <div class="registerDiv">
        <div class="registerErrorMessageDiv">
            <div class="alert alert-danger" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"></button>
                <span class="errorMessage"></span>
            </div>
        </div>


        <table class="registerTable" align="center">
            <tr>
                <td class="registerTip registerTableLeftTD">设置会员名</td>
                <td class="registerTableRightTD"><input id="name" name="name" placeholder="会员名一旦设置成功，无法修改"></td>
            </tr>
            <tr>
                <td class="registerTip registerTableLeftTD">手机号</td>
                <td class="registerTableRightTD"><input type="text" id="telephone" name="telephone"
                                                        placeholder="请输入您的手机号"></td>
            </tr>
            <tr>
                <td class="registerTip registerTableLeftTD">设置登陆密码</td>
                <td class="registerTableRightTD"><input id="password" name="password" type="password"
                                                        placeholder="设置你的登陆密码"></td>
            </tr>
            <tr>
                <td class="registerTableLeftTD">密码确认</td>
                <td class="registerTableRightTD"><input id="repeatpassword" type="password" placeholder="请再次输入你的密码">
                </td>
            </tr>
            <tr>
                <td class="registerTableLeftTD">验证码</td>
                <td class="registerTableRightTD">
                    <input id="check" name="checkCode" type="text" placeholder="请输入验证码" autocomplete="off">
                    <span><img src="checkCode" alt="" id="checkCode_img" onclick="changeCheckCode(this)"></span>
                </td>

            </tr>
            <tr>
                <td colspan="2" class="registerButtonTD">
                    <a href="">
                        <button id="btn_submit">提交</button>
                    </a>
                </td>
            </tr>
        </table>
    </div>
</form>