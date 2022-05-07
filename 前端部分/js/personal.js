$(function() {
    $(".first").css("border-left", "4px #0097f6 solid");
    $(".first").click(function() {
        $(this).css("border-left", "4px #0097f6 solid");
        $(this).siblings().css("border", "4px solid transparent");
        $(".right_bottom1").css("display", "block");
        $(".right_bottom2").css("display", "none");
        $(".right_bottom3").css("display", "none");
        $(".right_bottom4").css("display", "none");
    });
    $(".second").click(function() {
        $(this).css("border-left", "4px #0097f6 solid");
        $(this).siblings().css("border", "4px solid transparent");
        $(".right_bottom1").css("display", "none");
        $(".right_bottom3").css("display", "none");
        $(".right_bottom2").css("display", "block");
        $(".right_bottom4").css("display", "none");
    });
    $(".third").click(function() {
        $(this).css("border-left", "4px #0097f6 solid");
        $(this).siblings().css("border", "4px solid transparent");
        $(".right_bottom1").css("display", "none");
        $(".right_bottom2").css("display", "none");
        $(".right_bottom3").css("display", "block");
        $(".right_bottom4").css("display", "none");
    });
    $(".fourth").click(function() {
        $(this).css("border-left", "4px #0097f6 solid");
        $(this).siblings().css("border", "4px solid transparent");
        $(".right_bottom1").css("display", "none");
        $(".right_bottom2").css("display", "none");
        $(".right_bottom3").css("display", "none");
        $(".right_bottom4").css("display", "block");
    });

    //推荐分类按钮
    var flag = 0;
    $(".category").click(function() {
        if (flag == 0) {
            $(this).css({ "backgroundColor": "#00a1d6", "color": "#ffffff" });
            flag = 1;
        } else if (flag == 1) {
            $(this).css({ "backgroundColor": "rgb(192, 185, 185)", "color": "#ffffff" });
            flag = 0;
        }
    });

    // 获取用户个人信息
    var sex;
    $.ajax({
        method: "post",
        url: "http://" + window.location.host + "/op/account",
        headers: {
            "token": localStorage.token
        },
        success: function(res) {
            if (res.code == 1) {
                if (!res.avatar) localStorage.avatar = "default.jpg";
                else localStorage.avatar = res.avatar;
                console.log(res);
                $(".uname").text(res.userName);
                $(".myname").val(res.alias);
                $(".row3 textarea").val(res.signature);
                $(".photo").get(0).src = "http://" + window.location.hostname + ":80/src/avatar/" + localStorage.avatar;
                sex = res.gender;
                if (sex == "M") {
                    $(".male").css({ "backgroundColor": "#22a1d6", "color": "#fff" });
                    $(".male").siblings().css({ "backgroundColor": "#f4f4f4", "color": "#797979" });
                } else if (sex == "F") {
                    $(".female").css({ "backgroundColor": "#22a1d6", "color": "#fff" });
                    $(".female").siblings().css({ "backgroundColor": "#f4f4f4", "color": "#797979" });
                } else if (sex == "N") {
                    $(".secret").css({ "backgroundColor": "#22a1d6", "color": "#fff" });
                    $(".secret").siblings().css({ "backgroundColor": "#f4f4f4", "color": "#797979" });
                }
                $(".change_age").val(res.age);
            } else if (res.code == 0x39) {
                alert("请先登录");
                window.location.href = "http://" + window.location.host + "/login"
            } else {
                console.log(res);
                alert(res.message);
            }
        },
        error: function(msg) {
            console.log(msg);
        }
    });
    // 获取用户发表的文章
    $.ajax({
        method: "post",
        url: "http://" + window.location.host + "/op/getArticleList",
        contentType: "application/json",
        data: JSON.stringify({
            "number": 100,
            "time": requestDate()
        }),
        headers: { "token": localStorage.token },
        success: function(obj) {
            if (obj[0].code == 1) { //json数组
                $.each(obj, function(key, value) {
                    article = createEle(value);
                    article.get(0).obj = value;
                    //插入评论
                    $('tbody').append(article);
                });
                $("tbody tr:first").remove();
            } else if (obj[0].code == 0x39) {
                alert("请先登录");
            } else {
                console.log(obj.message);
            }
        },
        error: function(xhr) {
            console.log(xhr.message);
        }
    });
    //创建表格的函数
    function createEle(obj) {
        var remark = $('<tr><td><a href=' + obj.url + '>' + obj.title + '</a></td><td>' + formartDate(obj.postTime) + '</td><td>' + obj.comments + '</td><td><a class="delete" href="javascript:;">删除</a></td></tr>');
        return remark;
    }
    //删除文章按钮
    $("body").delegate(".delete", "click", function() {
        console.log("ok");
        if (confirm("确定删除此文章吗？")) {
            $(this).parents('tr').remove();
            var obj = $(this).parents('tr').get(0).obj;

            $.ajax({
                type: 'post',
                url: 'http://' + window.location.host + "/op/article/delete",
                contentType: 'application/json',
                data: JSON.stringify({
                    "articleId": obj.articleId,
                }),
                headers: { "token": localStorage.token },
                success: function(msg) {
                    if (msg.code != 1) {
                        alert(msg.message);
                    }
                    console.log(msg.code);
                },
                error: function(xhr) {
                    alert(xhr.status);
                }
            });
        }
    });
    //修改按钮
    $(".change").click(function() {
        $(".change_age").val($(".age").text());
        $(".change_age").css("display", "block");
    });
    $(".changeName").click(function() {
        $(".changeUname").css("display", "block");
        $(".changeUname").val($(".uname").text());
    });

    //性别按钮
    $(".sex").on('click', 'button', function() {
        $(this).css({ "backgroundColor": "#22a1d6", "color": "#fff" });
        $(this).siblings().css({ "backgroundColor": "#f4f4f4", "color": "#797979" });
    });

    $.each($(".row4 button"), function(index) {
        $(".row4 button").attr("index=" + index + 1);
    });
    var index;
    $(".row4 button").click(function() {
        index = $(this).attr("index");
        if (index == 1) {
            index = "M"
        } else if (index == 2) {
            index = "F"
        } else if (index == 3) {
            index = "N"
        }
    });

    //保存修改(基本信息)
    $(".store").click(function() {
        $.ajax({
            method: "post",
            //这个地方你改一下
            url: "http://" + window.location.host + "/op/account/updateEx",
            headers: {
                "token": localStorage.token
            },
            contentType: 'application/json',
            data: JSON.stringify({
                "alias": $(".myname").val(),
                "signature": $(".row3 textarea").val(),
                "gender": index, //把index值传给后端，1代表男，2代表女，3代表保密
                "age": $(".change_age").val(),
            }),

            success: function(res) {
                if (res.code == 1) {
                    $(".uname").val(res.name);
                    $(".row3 textarea").val(res.signature);
                    $(res.gender).css("backgroundColor", "#22a1d6");
                    $(".change_age").val(res.age);
                    localStorage.alias = $(".myname").val();
                    alert("保存成功！");
                    location.reload();
                } else {
                    alert(res.message);
                }
            },
            error: function(msg) {
                console.log(msg);
            }
        });

    });


    //获取验证码
    $(".code").click(function() {
        $.ajax({
            method: "post",
            url: "http://" + window.location.host + "/op/account/requestVerificationCode",
            contentType: 'application/json',
            headers: { "token": localStorage.token },
            success: function(res) {
                if (res.code == 1) {
                    alert("获取成功");
                    var time = 59;
                    $("#btn").text(60 + "s");
                    if (time >= 0) {
                        $("#btn").css("display", "block");
                        $(".code").css("display", "none");
                        var t = setInterval(function() {
                            console.log(time)
                            $("#btn").text(time + "s");
                            if (time == 0) {
                                clearInterval(t);
                                $('#btn').css('display', 'none');
                                $('.code').css('display', 'block')
                            }
                            time = time - 1;
                        }, 1000)
                    }
                } else {
                    alert("获取失败");
                }
            },
            error: function(msg) {
                console.log(msg);
            }
        });
    });

    //保存修改(密码)
    $(".storage").click(function() {
        if ($(".newPwd").val() != $(".newPwd2").val()) {
            alert("两次输入的密码不一致！");
        } else if ($(".verifyCode").val() == "") {
            alert("请输入验证码");
        } else {
            $.ajax({
                method: "post",
                url: "http://" + window.location.host + "/op/account/updatepassword",
                contentType: 'application/json',
                headers: { "token": localStorage.token },
                data: JSON.stringify({
                    "password": $(".oldPwd").val(),
                    "newPassword": $(".newPwd").val(),
                    "code": $(".verifyCode").val()
                }),
                success: function(msg) {
                    if (msg.code == 1) {
                        alert("修改成功,请重新登录")
                        localStorage.removeItem("token");
                        window.location.href = "http://" + window.location.host + "/login";
                    } else {
                        alert(msg.message);
                        console.log(msg.message);
                    }
                },
                error: function(xhr) {
                    console.log(xhr.status);
                }
            });
        }
    });


    //头像上传
    $(".storePhoto").click(function() {
        let file = document.getElementById('file').files;
        console.log(file[0].name)
        console.log(file[0].size)
        const index = file[0].name.lastIndexOf('.') // 根据文件名找到最后一个‘.’的索引
        const suffixName = file[0].name.substr(index) // 根据索引截取，得到后缀名
        const verifyImg = /.(jpg|jpeg|bmp|png)$/i
            // 验证
        if (verifyImg.test(suffixName)) { // 为真表示验证通过
            if (file[0].size <= 2097152) {
                console.log($("img").get(0).src)
                    //验证成功
                let fileForm = $("#uploadFileForm");
                var formData = new FormData(fileForm[0]);

                console.log(fileForm[0])
                console.log(formData);
                $.ajax({
                    url: 'http://' + window.location.host + '/op/account/uploadAvatar',
                    type: 'post',
                    contentType: false,
                    processData: false,
                    data: formData,
                    headers: {
                        "token": localStorage.token
                    },
                    success: function(res) {
                        console.log(res);
                        if (res.code == 1) {
                            if (!res.avatar) {
                                localStorage.avatar = "default.jpg";
                            }
                            localStorage.avatar = res.avatar;
                            $(".photo").get(0).src = "http://" + window.location.hostname + ":80/src/avatar/" + res.avatar;
                            $(".showPhoto").css({ "display": "none" });
                            $(".first-change-lb_img").css("display", "block");
                            console.log(res);
                        } else {
                            alert(res.message);
                            console.log(res);
                        }

                    },
                    error: function(err) {
                        console.log(err)
                    }
                });

            } else {
                alert("请上传小于2M的图片!")
            }
        } else {
            alert("文件格式错误!")
        }
    })

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


function showFilename() {
    var file = document.getElementById('file').files;
    $(".filename").text(file[0].name);
    var reader = new FileReader()
    reader.readAsDataURL(file[0])
    reader.onload = function() {
        var fileSrc = reader.result
        $(".showPhoto").css({ "display": "block" });
        $(".showPhoto").get(0).src = fileSrc;
        $(".first-change-lb_img").css("display", "none");
    }
}

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