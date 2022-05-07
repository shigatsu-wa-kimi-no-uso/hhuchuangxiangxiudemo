$(function() {

    //回到顶部开始
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


    function red(element) {
        $(".news").css("color", "#fff");
        $(".military").css("color", "#fff");
        $(".people").css("color", "#fff");
        $(".economy").css("color", "#fff");
        $(".society").css("color", "#fff");
        $(".internation").css("color", "#fff");
        $(".epidemic").css("color", "#fff");
        $(element).css("color", "#9b1a1d");
    }

    function hideAll() {
        $(".news_body").css("display", "none");
        $(".military_body").css("display", "none");
        $(".people_body").css("display", "none");
        $(".economy_body").css("display", "none");
        $(".society_body").css("display", "none");
        $(".internation_body").css("display", "none");
        $(".epidemic_body").css("display", "none");
    }

    var totalAddress;
    var articleId;
    //获取新闻并且展示在页面上
    $(".military").click(function() {
        showNews('article/category/1', '.military', '.military_body', ".military_body .new_left:first");
    });
    $(".people").click(function() {
        showNews('article/category/2', '.people', '.people_body', ".people_body .new_left:first");
    });
    $(".economy").click(function() {
        showNews('article/category/3', '.economy', '.economy_body', ".economy_body .new_left:first");
    });
    $(".society").click(function() {
        showNews('article/category/4', '.society', '.society_body', ".society_body .new_left:first");
    });
    $(".internation").click(function() {
        showNews('article/category/5', '.internation', '.internation_body', ".internation_body .new_left:first");
    });

    //获取新闻并展示的函数
    function showNews(url, category, categoryBody, hide) {
        hideAll();
        red(category);
        $(categoryBody).css("display", "block");
        $.ajax({
            type: 'post',
            url: url,
            contentType: 'application/json',
            data: JSON.stringify({
                "time": requestDate(),
                "number": 10
            }),
            success: function(obj) {
                console.log(obj);
                if (obj[0].code == 1) {
                    $.each(obj, function(key, value) {
                        totalAddress = obj[key].url;
                        articleId = obj[key].articleId;
                        //根据内容创建节点
                        var briefNews = create(value);
                        briefNews.get(0).obj = value;
                        //插入新闻简介
                        $(categoryBody).append(briefNews);
                    });
                    $(hide).css("display", "none");
                } else {
                    console.log(obj.message);
                }
            },
            error: function(xhr) {
                console.log(xhr.status);
            }
        });
    }

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
            '<p class="brief">' + "obj.articleAbstract" + '</p>' +
            '</a>' +
            '<div class="new_bottom">' +
            '<li class="date fl">2022-04-24</li>' +
            '<li class="country fl">' + obj.articleSymbol + '<>' +
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