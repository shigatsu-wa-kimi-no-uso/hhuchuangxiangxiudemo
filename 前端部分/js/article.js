var k;
if (!localStorage.token) {
    alert("请先登录");
    window.location.href = "http://" + window.location.host + "/login";
}
var title;


//自动推荐新闻分类
function Recommend() {
    if ($("#title").val() && $("#title").val() != title) {
        title = $("#title").val();
        $.ajax({
            method: 'post',
            url: "http://" + window.location.host + "/article/getclassification",
            contentType: 'application/json',
            data: JSON.stringify({
                "title": $("#title").val()
            }),
            success: function(msg) {

                console.log(msg);
                if (msg.code == 1) {
                    k = msg.categoryId;
                    $.each($(".recommend span"), function(key) {
                        $("span[index=" + key + "]").css({ "backgroundColor": "#F4F4F4", "color": "#08A4D7" })
                    })
                    $("span[index=" + k + "]").css({ "backgroundColor": "#08a4d7", "color": "#fff" })
                } else if (msg.code == 0x39) {
                    alert("请先登录！");
                } else {
                    console.log(msg.message);
                }
            },
            error: function(xhr) {
                console.log(xhr.status);
            }
        });
    } else {}
}
$(function() {
    //推荐分类按钮
    $.each($(".recommend span"), function(key) {
        $(this).attr("index", key + 1)
        console.log(parseInt($(this).attr("index"))) //这里的index值是字符串格式
    });

    $(".recommend span").click(function() {
        $(this).css({ "backgroundColor": "#08a4d7", "color": "#fff" })
        $(this).siblings().css({ "backgroundColor": "#f4f4f4", "color": "#08a4d7" })
        k = $(this).attr("index");
    });

});