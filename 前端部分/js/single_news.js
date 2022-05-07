function myfunction() {
    $('.pinglunqu').css({
        'borderColor': 'darkgray',
        'backgroundColor': '#ffffff'
    });
}

function myFunction() {
    $('.pinglunqu').css({
        "borderColor": "#ececeb",
        "backgroundColor": "#f4f5f7"
    });
}
$(function() {
    var url = window.location.pathname;
    var splits = url.split("/");
    var articleId = splits[4]; //文章的id
    var posterId = splits[3]; //文章作者的id
    var address = "http://" + window.location.hostname + ":80/src/article/" + posterId + "/" + articleId + ".md";
    //获取文章的url

    var commentTableId = posterId * 1E12 + articleId * 1E6;

    $.ajax({
        type: 'post',
        url: window.location.href + "/get", //指向文章url的地址
        async: true,
        success: function(msg) {
            if (msg.code == 1) {
                $(".single_topic").text(msg.title);
                document.title = msg.title;
            } else {
                console.log(msg.message);
            }
        },
        error: function(xhr) {
            console.log(xhr.message);
            console.log(xhr.status);
        }
    });
    //获取文章内容
    var htmlobj = $.ajax({
        url: address, //文章内容的地址
        async: false
    });

    var text = htmlobj.responseText;

    // markdown转成html开源代码开始
    var textView = editormd.markdownToHTML("test-markdown-view", {
        markdown: text, // Also, you can dynamic set Markdown text
        htmlDecode: true, // Enable / disable HTML tag encode.
        htmlDecode: "style,script,iframe", // Note: If enabled, you should filter some dangerous HTML tags for website security.
    });
    // 开源代码结束
    $(".content").html(textView["0"].innerHTML); //将文章内容放到页面相应的位置

    // //获取词云图
    var url = "http://172.20.10.2:80/WordCloud/" + posterId + "/" + articleId + ".png";
    $(".ciyun_img").css("backgroundImage", " url(" + url + ")")


    var flag = 0;
    var url2 = "http://172.20.10.2:80/WordCloud/comment/" + commentTableId + ".png";

    $(".commentWordCloud").click(function() {
        if (flag == 0) {
            $(".comment_ciyun").stop().fadeIn(500);
            $(".commentWC").css("backgroundImage", " url(" + url2 + ")");
            flag = 1;
        } else {
            $(".comment_ciyun").stop().fadeOut(500);
            flag = 0;
        }
    });

    // 获取评论
    var pictureSrc = "http://" + window.location.hostname + ":80/src/avatar/" + localStorage.avatar;
    $.ajax({
        type: 'post',
        url: window.location.href + '/getcomment',
        contentType: 'application/json',
        data: JSON.stringify({
            "number": 100,
            "time": requestDate()
        }),
        headers: { "token": localStorage.token },
        success: function(obj) {
            if (obj[0].code == 1) { //json数组
                console.log(localStorage.avatar);
                console.log(obj);
                var remark = new Array;
                var arr = new Array;
                var array = new Array;
                var dislike = new Array;
                for (var i = 1; i < obj.length; ++i) {
                    remark[i - 1] = createEle(obj[i]);
                    remark[i - 1].get(0).obj = obj[i];
                    //插入评论
                    $('.ul_comment').append(remark[i - 1]);
                    console.log(obj[i]);
                    if (obj[i].isMyComment == 0) {
                        arr[arr.length] = i - 1;
                    }
                    if (obj[i].likeState == 1) {
                        array[array.length] = i - 1;
                    } else if (obj[i].likeState == -1) {
                        dislike[dislike.length] = i - 1;
                    }
                }
                for (var i = 0; i < arr.length; ++i) {
                    remark[arr[i]][0].children[6].className = "noDel";
                }
                for (var i = 0; i < array.length; ++i) {
                    remark[array[i]][0].children[4].children[0].className = "span_icon iconfont icon-dianzan_kuai"
                }
                for (var i = 0; i < dislike.length; ++i) {
                    remark[dislike[i]][0].children[5].children[0].className = "i_icon iconfont icon-badreview-full"
                }
            } else {
                console.log(obj);
            }
        },
        error: function(xhr) {
            console.log(xhr.status);
        }
    });

    //1.监听发布按钮的点击
    $('.button1').click(function() {
        if ($('.pinglunqu').val() == '') {
            alert("请输入评论内容");
            return false;
        } else if (!localStorage.token) {
            alert("请先登录");
            return false;
        } else {
            //拿到用户输入的内容
            var $text = $('.pinglunqu').val();
            $.ajax({
                type: 'post',
                url: 'http://' + window.location.host + '/op' + window.location.pathname + '/comment/post',
                contentType: 'application/json',
                data: JSON.stringify({
                    "content": $text
                }),
                headers: { "token": localStorage.token },
                success: function(obj) {
                    console.log(obj);
                    if (obj.code == 1) {
                        console.log(obj);
                        //根据内容创建节点
                        var remark = create($text, localStorage.alias, pictureSrc);
                        var commentObj = new Object();
                        commentObj.commentId = obj.commentId;
                        commentObj.posterId = obj.posterId;
                        remark.get(0).obj = commentObj;
                        //插入评论
                        $('.ul_comment').prepend(remark);
                        //清空输入框内容
                        $('.pinglunqu').val('');
                    } else if (obj.code == 0x39) {

                        alert("请先登录");
                    } else {
                        alert(obj.message);
                    }
                },
                error: function(xhr) {
                    alert(xhr.status);
                }
            });
        }
    });
    //2.监听顶点击
    $('body').delegate('.span_icon', 'click', function() {
        if ($(this).parents(".zan_container").siblings(".cai_container").children(".i_icon")[0].className == "i_icon iconfont icon-badreview" && $(this).get(0).className == "span_icon iconfont icon-dianzan") {
            $(this).siblings().text(parseInt($(this).siblings().text()) + 1);
            $(this).removeClass('icon-dianzan');
            $(this).addClass('icon-dianzan_kuai');
            //两个都没点击时
        } else if ($(this).parents(".zan_container").siblings(".cai_container").children(".i_icon")[0].className == "i_icon iconfont icon-badreview-full" && $(this).get(0).className == "span_icon iconfont icon-dianzan") {
            $(this).parents(".zan_container").siblings(".cai_container").children(".i_icon").removeClass('icon-badreview-full');
            $(this).parents(".zan_container").siblings(".cai_container").children(".i_icon").addClass('icon-badreview');
            $(this).siblings().text(parseInt($(this).siblings().text()) + 1);
            $(this).removeClass('icon-dianzan');
            $(this).addClass('icon-dianzan_kuai');
            //点了踩时
        } else if ($(this).parents(".zan_container").siblings(".cai_container").children(".i_icon")[0].className == "i_icon iconfont icon-badreview" && $(this).get(0).className == "span_icon iconfont icon-dianzan_kuai") {
            $(this).siblings().text(parseInt($(this).siblings().text()) - 1);
            $(this).addClass('icon-dianzan');
            $(this).removeClass('icon-dianzan_kuai');
            //点了赞时
        }

        var obj = $(this).parents('.single_comment').get(0).obj;
        $.ajax({
            type: 'post',
            url: 'http://' + window.location.host + '/op' + window.location.pathname + '/comment/like',
            contentType: 'application/json',
            data: JSON.stringify({
                "commentId": obj.commentId,
                'cPosterId': obj.posterId
            }),
            headers: { "token": localStorage.token },
            success: function(msg) {
                if (msg.code == 0x39) {
                    alert("请先登录");
                }
            },
            error: function(xhr) {
                alert(xhr.status);
            }
        });
    });


    //3.监听踩点击
    $('body').delegate('.i_icon', 'click', function() {
        if ($(this).parents(".cai_container").siblings(".zan_container").children(".span_icon")[0].className == "span_icon iconfont icon-dianzan" && $(this).get(0).className == "i_icon iconfont icon-badreview") {
            $(this).removeClass('icon-badreview');
            $(this).addClass('icon-badreview-full');
        } else if ($(this).parents(".cai_container").siblings(".zan_container").children(".span_icon")[0].className == "span_icon iconfont icon-dianzan" && $(this).get(0).className == "i_icon iconfont icon-badreview-full") {
            $(this).removeClass('icon-badreview-full');
            $(this).addClass('icon-badreview');
        } else if ($(this).parents(".cai_container").siblings(".zan_container").children(".span_icon")[0].className == "span_icon iconfont icon-dianzan_kuai" && $(this).get(0).className == "i_icon iconfont icon-badreview") {
            $(this).parents(".cai_container").siblings(".zan_container").children(".span_icon").siblings().text(parseInt($(this).parents(".cai_container").siblings(".zan_container").children(".span_icon").siblings().text()) - 1);
            $(this).parents(".cai_container").siblings(".zan_container").children(".span_icon").addClass('icon-dianzan');
            $(this).parents(".cai_container").siblings(".zan_container").children(".span_icon").removeClass('icon-dianzan_kuai');
            $(this).removeClass('icon-badreview');
            $(this).addClass('icon-badreview-full');
        }

        var obj = $(this).parents('.single_comment').get(0).obj;
        $.ajax({
            type: 'post',
            url: 'http://' + window.location.host + '/op' + window.location.pathname + '/comment/bury',
            contentType: 'application/json',
            data: JSON.stringify({
                "commentId": obj.commentId,
                'cPosterId': obj.posterId
            }),
            headers: { "token": localStorage.token },
            success: function(msg) {
                if (msg.code == 0x39) {
                    alert("请先登录");
                }
            },
            error: function(xhr) {
                alert(xhr.status);
            }
        });
    });


    //4.监听删除点击
    $('body').delegate('.infoDel', 'click', function() {
        if (confirm("确定删除此评论吗？")) {
            $(this).parents('.single_comment').remove();
            var obj = $(this).parents('.single_comment').get(0).obj;
            $.ajax({
                type: 'post',
                url: 'http://' + window.location.host + '/op' + window.location.pathname + '/comment/delete',
                contentType: 'application/json',
                data: JSON.stringify({
                    "commentId": obj.commentId,
                    "articleId": articleId
                }),
                headers: { "token": localStorage.token },
                success: function(msg) {
                    if (msg.code != 1)
                        alert(msg.message);
                    console.log(msg.code);
                },
                error: function(xhr) {
                    alert(xhr.status);
                }
            });
        } else {};
    });

    //创建节点方法
    function createEle(obj) {
        var remark = $('<div class = "single_comment">\n' +
            '<img class="picture" src=' + obj.avatarUrl + '>\n' +
            '<p class = "username">' + obj.posterAlias + '</p>\n' +
            '<p class = "infoText">' + obj.content + '</p>\n' +
            '<p class = "infoOperation"\n' +
            '<span class = "infoTime">' + formartDate(obj.postTime) + '</span>\n' +
            '<div class="zan_container">' + '<span class = "span_icon iconfont icon-dianzan"></span>' + '<p class="zan_number">' + obj.likes + ' </p>' + '</div>\n' +
            '<div class="cai_container">' + '<i class = "i_icon iconfont icon-badreview"></i>' + '</div>\n' +
            '<a href = "javascript:;" class="infoDel">删除</a>\n' +
            '</p>\n' +
            '</div>');
        return remark;
    }

    //创建节点方法
    function create($text, username, pictureSrc) {
        var remark = $('<div class = "single_comment">\n' +
            '<img class="picture" src=' + pictureSrc + '>\n' +
            '<p class = "username">' + username + '</p>\n' +
            '<p class = "infoText">' + $text + '</p>\n' +
            '<p class = "infoOperation"\n' +
            '<span class = "infoTime">' + requestDate() + '</span>\n' +
            '<div class="zan_container">' + '<span class = "span_icon iconfont icon-dianzan"></span>' + '<p class="zan_number">' + 0 + ' </p>' + '</div>\n' +
            '<div class="cai_container">' + '<i class = "i_icon iconfont icon-badreview"></i>' + '</div>\n' +
            '<a href = "javascript:;" class="infoDel">删除</a>\n' +
            '</p>\n' +
            '</div>');
        return remark;
    }

    //生成时间的方法
    function formartDate(postTime) {
        var date = new Date(postTime);
        var month = date.getMonth();
        var hours = date.getHours();
        var day = date.getDate();
        var minute = date.getMinutes();
        var second = date.getSeconds();
        if (month < 10) {
            month = '0' + (month + 1);
        }
        if (day < 10) {
            day = '0' + day;
        }
        if (hours < 10 && hours > 0) {
            hours = '0' + hours;
        } else if (hours == 0) {
            hours = '00';
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
            hours + ':',
            minute + ':',
            second
        ];
        return arr.join('');
    }

    //生成请求时间的方法
    function requestDate() {
        var date = new Date();
        var month = date.getMonth();
        var hours = date.getHours();
        var day = date.getDate();
        var minute = date.getMinutes();
        var second = date.getSeconds();
        if (month < 10) {
            month = '0' + (month + 1);
        }
        if (day < 10) {
            day = '0' + day;
        }
        if (hours < 10 && hours > 0) {
            hours = '0' + hours;
        } else if (hours == 0) {
            hours = '00';
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
            hours + ':',
            minute + ':',
            second
        ];
        return arr.join('');
    }
});