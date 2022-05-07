$(function() {
    //回到顶部按钮
    $(window).scroll(function() {
        if ($(document).scrollTop() >= 490) {
            $(".back").fadeIn();
        } else if ($(document).scrollTop() < 490) {
            $(".back").fadeOut();
        }
    });
    $(".backToTop").click(function() {
        $("body,html").stop().animate({
            scrollTop: 0
        });
    });

    // 发表文章按钮
    $(".loginReg").click(function() {
        if (localStorage.token) {
            window.location.href = 'http://' + window.location.host + '/article/edit';
        } else {
            if (confirm("请先登录！")) {
                window.location.href = 'http://' + window.location.host + '/login';
            }
        }
    });
    //获取新闻并展示的函数
    var address = window.location.pathname;
    var splits = address.split("/");
    var coordinate = splits[3]; //获取种类的id
    $.each($(".kinds li"), function(key) {
        $(this).attr("index", key);
        console.log($(this).attr("index"))
    });
    var totalAddress;
    var articleId;
    $.ajax({
        type: 'post',
        url: 'http://' + window.location.host + "/article/category/" + coordinate + "/get",
        contentType: 'application/json',
        data: JSON.stringify({
            "time": requestDate(),
            "number": 40
        }),
        success: function(obj) {
            if (obj[0].code == 1) {
                console.log(obj);
                for (var i = 1; i < obj.length; ++i) {
                    totalAddress = obj[i].url;
                    articleId = obj[i].articleId;
                    obj[i].postTime = obj[i].postTime.split('T')[0];
                    if (!obj[i].firstImg || obj[i].firstImg == "NULL" || obj[i].firstImg == "UNKNOWN") {
                        obj[i].firstImg = "http://" + window.location.hostname + ":80/src/img/default.jpg";
                    }
                    if (!obj[i].articleAbstract || obj[i].articleAbstract == "NULL" || obj[i].articleAbstract == "UNKNOWN") {
                        obj[i].articleAbstract = " ";
                    }
                    if (!obj[i].articleSymbol || obj[i].articleSymbol == "NULL" || obj[i].articleSymbol == "UNKNOWN") {
                        obj[i].articleSymbol = " ";
                    }
                    //根据内容创建节点
                    var briefNews = create(obj[i]);
                    //插入新闻简介
                    $(".affairs_body").append(briefNews);
                }
                $(".kinds li[index=" + coordinate + "]").css("color", "#fff")
            } else {
                console.log(obj.message);
            }
        },
        error: function(xhr) {
            console.log(xhr.status);
        }
    });
    //创建新闻的函数
    function create(obj) {
        var createNew = $('<div class="new_left w">' +
            '<a target="_blank" class="a_image fl" href="' + totalAddress + '">' +
            '<div class="image">' +
            '<img src=' + obj.firstImg + '>' + //后端传入的图片
            '</div>' +
            '</a>' +
            '<div class="new_right fl">' +
            '<h2 class="h_topic"><a target="_blank" class="a_topic" href="' + totalAddress + '">' +
            obj.title +
            '</a></h2>' +
            '<a class="a_brief" target="_blank" href="' + totalAddress + '">' +
            '<p class="brief">' + obj.articleAbstract + '</p>' + //新闻摘要
            '</a>' +
            '<div class="new_bottom">' +
            '<li class="date fl">' + obj.postTime + '</li>' +
            '<li class="country fl">' + obj.articleSymbol + '</li>' + //文章中出现频率最高的词
            '</div>' +
            '</div>' +
            '</div>')
        return createNew;
    }
    //生成请求时间的方法
    function requestDate() {
        var date = new Date();
        var month = date.getMonth();
        var day = date.getDate();
        var minute = date.getMinutes();
        var second = date.getSeconds();
        if (month < 10) {
            month = '0' + (month + 1);
        }
        if (day < 10) {
            day = '0' + day;
        }
        if (minute < 10 && minute > 0) {
            minute = '0' + minute;
        } else if (minute == 0) {
            minute = '00';
        }
        if (second < 10 && second > 0) {
            second = '0' + second;
        } else if (second == 0) {
            second = '00';
        }
        var arr = [date.getFullYear() + '-',
            month + '-',
            day + ' ',
            date.getHours() + ':',
            minute + ':',
            second
        ];
        return arr.join('');
    }
});