$(function() {
    if (!localStorage.token) {
        $(".personal").html("<a href='login'" + "target=" + "_blank" + ">登录/注册</a>");
        $(".personal").addClass("login-register").removeClass("personal");
        $(".login-register").mouseenter(function() {
            $(".information").css("display", "none");
        });
    }
    //红色河海轮播图
    $(".aa1").click(function() {
        window.open('https://www.hhu.edu.cn/2021/0924/c165a228370/page.htm');
    });
    $(".aa2").click(function() {
        window.open('https://www.hhu.edu.cn/2021/0618/c165a225411/page.htm');
    });
    $(".aa3").click(function() {
        window.open('https://www.hhu.edu.cn/2021/0531/c165a224261/page.htm');
    });
    $(".aa4").click(function() {
        window.open('https://www.hhu.edu.cn/2021/0511/c165a223472/page.htm');
    });
    $(".aa5").click(function() {
        window.open('https://www.hhu.edu.cn/2021/0427/c165a222888/page.htm');
    });
    $(".aa6").click(function() {
        window.open('https://www.hhu.edu.cn/2021/0420/c165a222520/page.htm');
    });

    //百年党史动画
    function show(name1, name2) {
        $(name1).mouseenter(function() {
            $(name2).stop().fadeOut(300); //stop停止前面的动画
        });
        $(name1).mouseleave(function() {
            $(name2).stop().fadeIn(300); //stop停止前面的动画
        });
    }
    for (var i = 1; i <= 6; i++) {
        show(".card" + i, ".introduce" + i);
    }
    //动画回到顶部
    $(window).scroll(function() {
        console.log($(document).scrollTop())
        if ($(document).scrollTop() >= 780) {
            $(".back").fadeIn();
        } else if ($(document).scrollTop() < 780) {
            $(".back").fadeOut();
        }
    });
    $(".backToTop").click(function() {
        $("body,html").stop().animate({
            scrollTop: 0
        });
    });
    $(".callBack").click(function() {
        $("body,html").stop().animate({
            scrollTop: 830
        });
    });
    $(".newTime").click(function() {
        $("body,html").stop().animate({
            scrollTop: 1610
        });
    });
    $(".hoHai").click(function() {
        $("body,html").stop().animate({
            scrollTop: 3270
        });
    });
    //退出登录功能
    $(".log_out").click(function() {
        var a = confirm("确认退出登录吗？");
        if (a === true) {
            $.ajax({
                method: "get",
                url: "http://" + window.location.host + "/account/logout",
                headers: { "token": localStorage.token },
                success: function(msg) {
                    if (msg.code == 1) {
                        localStorage.removeItem("token");
                        alert("退出成功！");
                        localStorage.removeItem("username");
                        localStorage.removeItem("alias");
                        localStorage.removeItem("avatar");
                        if (!localStorage.token) {
                            $(".personal").html("<a href='login'" + "target=" + "_blank" + ">登录/注册</a>");
                            $(".personal").addClass("login-register").removeClass("personal");
                            $(".login-register").mouseenter(function() {
                                $(".information").css("display", "none");
                            });
                        }
                    } else {
                        alert(msg.message);
                    }
                },
                error: function(xhr) {
                    console.log(xhr.status);
                }
            })

        }
    });
    //登录成功以后将首页的登陆注册改成用户名
    if (localStorage.token) {
        $(".login-register").html(localStorage.alias + '<span class="toRight iconfont icon-xiangyou2"></span>');
        $(".login-register").addClass("personal").removeClass("login-register");
        $(".personal").mouseenter(function() {
            $(".information").css("display", "block");
            $(".personal").css({ "backgroundColor": "#b8242c", "color": "#cea871" });
            $(".toRight").css("color", "#cea871");
        });
        $(".dd").mouseenter(function() {
            $(".information").css("display", "none");
            $(".personal").css({ "backgroundColor": "#b8242c", "color": "#caa871" });
            $(".toRight").css("color", "#caa871");
        });
    }
});