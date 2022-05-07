 // 判断输入内容是否符合要求
 function get(id) {
     var name = document.querySelector(".zhanghao").value;
     var regname = document.querySelector(".register_zhanghao").value;
     var pwd = document.querySelector(".mima").value;
     var pwd2 = document.querySelector(".register_mima_again").value;
     var pwd3 = document.querySelector(".register_mima").value;
     var Email = document.querySelector(".register_email").value;
     var ucode = document.querySelector(".vertify").value;
     switch (id) {
         case 'zhanghao':
             test1(/^[A-z]+[A-z0-9]{7,15}$/, name, 'testname')
             break;
         case 'regname':
             test1(/^[A-z]+[A-z0-9]{7,15}$/, regname, 'testregname')
             break;
         case 'mima':
             test1(/^.{8,16}$/, pwd, 'testpwd')
             break;
         case 'pwd2':
             test1(/^.{8,16}$/, pwd2, 'testpwd2')
             break;
         case 'pwd3':
             if (document.getElementById('testpwd2').innerText == '✔' && pwd3 == pwd2) {
                 document.getElementById('testpwd3').innerText = '✔';
                 document.getElementById('testpwd3').style.color = "green";
             } else {
                 document.getElementById('testpwd3').innerText = '✘';
                 document.getElementById('testpwd3').style.color = "red";
             }
             break;
         case 'Email':
             test1(/^\w{3,}@[A-z0-9]+(\.[A-z]{2,5}){1,2}$/, Email, 'testEmail');
             break;
         case 'ucode':
             test1(/^[0-9]{6}$/, ucode, 'testcode');
     }
 }

 function test1(ruler, value, spanid) {
     if (ruler.test(value)) {
         document.getElementById(spanid).innerText = '✔'
         document.getElementById(spanid).style.color = "green"
             //  document.querySelector(".test_name").style.display = "none";
             //  document.querySelector(".test_name").style.display = "none";//需要修改
     } else {
         document.getElementById(spanid).innerText = '✘'
         document.getElementById(spanid).style.color = "red"
             //  document.querySelector(".test_name").style.display = "block";
     }
 }

 $(function() {
     var denglu = document.querySelector('.regist2');
     var zhuce = document.querySelector('.regist');
     var register_body1 = document.querySelector('.register_body1');
     var login_body1 = document.querySelector('.login_body1');
     denglu.onclick = function() {
         register_body1.style.display = 'none';
         login_body1.style.display = 'block';
     };
     zhuce.onclick = function() {
         register_body1.style.display = 'block';
         login_body1.style.display = 'none';

     };

     // 登录功能交互
     var zhanghao = document.querySelector('.zhanghao');
     var mima = document.querySelector('.mima');
     var btn_denglu = document.querySelector('.login_button');
     btn_denglu.onclick = function() {
         //做个简单的验证，输入内容不能为空
         //  var time = new Date();
         //  var ale = document.getElementById("alert");
         if ((!zhanghao.value || !mima.value)) {
             alert("输入内容不能为空!");
         } else if (document.getElementById('testpwd').innerText == '✘' || document.getElementById('testname').innerText == '✘') {
             alert("用户名或密码不符合规范,请重新输入!");
         } else {

             $.ajax({
                 method: "post",
                 url: "http://" + window.location.host + "/account/login",

                 contentType: 'application/json',
                 data: JSON.stringify({
                     'username': zhanghao.value,
                     'password': mima.value
                 }),
                 success: function(res) {
                     if (res.code == 1) {
                         alert('登录成功!');
                         localStorage.token = res.token;
                         localStorage.username = zhanghao.value;
                         localStorage.alias = res.alias;
                         localStorage.avatar = res.avatar;
                         if (alert || !alert) {
                             window.location.href = "http://" + window.location.host; //跳转到首页
                         }
                     } else {
                         alert(res.message + '!');
                         if (alert || !alert) {
                             location.reload(); //刷新当前页面
                         }
                     }
                 },
                 error: function(msg) {
                     console.log(msg);
                 }
             });
         }
     }

     // 注册功能交互
     var register_zhanghao = document.querySelector('.register_zhanghao');
     var register_email = document.querySelector('.register_email');
     var register_mima_again = document.querySelector('.register_mima_again');
     var register_mima = document.querySelector('.register_mima');
     var btn_zhuce = document.querySelector('.register_button');
     var vertify = document.querySelector('.vertify');
     var require = document.querySelector('.require');

     btn_zhuce.onclick = function() {
         //做个简单的验证，输入内容不能为空
         // var time = new Date();
         var ale2 = document.getElementById("alert2");
         if ((!register_zhanghao.value || !register_mima.value || !register_email.value || !register_mima_again.value || !vertify.value)) {
             alert("输入内容不能为空");
         } else if (register_mima.value != register_mima_again.value) {
             alert('两次输入的密码不一致，请重新输入!!');
         } else if (document.getElementById('testpwd2').innerText == '✘' || document.getElementById('testregname').innerText == '✘' || document.getElementById('testpwd3').innerText == '✘' || document.getElementById('testEmail').innerText == '✘') {
             alert("格式不符合规范,请重新输入!");
         } else {
             $.ajax({
                 method: "post",
                 url: "http://" + window.location.host + "/account/register",
                 contentType: 'application/json',
                 data: JSON.stringify({ //设置发送给后端的数据格式
                     'username': register_zhanghao.value,
                     'password': register_mima.value,
                     'email': register_email.value,
                     'code': vertify.value
                         // create_time: time.getTime() //获取到毫秒数
                 }),
                 success: function(res) {
                     if (res.code == 1) {
                         alert("注册成功！");
                         window.location.href = "http://" + window.location.host + "/login";
                     } else {
                         alert(res.message);
                     }
                 },
                 error: function(msg) {
                     console.log(msg);
                 }
             });
         }
     }
     require.onclick = function() {
         if (!register_email.value) {
             alert("未填写邮箱");
         } else {
             $.ajax({
                 method: "post",
                 url: "http://" + window.location.host + "/account/register/requestVerificationCode",
                 contentType: 'application/json',
                 data: JSON.stringify({
                     'email': register_email.value
                 }),
                 success: function(res) {
                     if (res.code == 1) {
                         alert("获取成功");
                         var time = 59;
                         $("#btn").text(60 + "s");
                         if (time >= 0) {
                             $("#btn").css("display", "block");
                             $(".require").css("display", "none");
                             var t = setInterval(function() {
                                 console.log(time)
                                 $("#btn").text(time + "s");
                                 if (time == 0) {
                                     clearInterval(t);
                                     $('#btn').css('display', 'none');
                                     $('.require').css('display', 'block')
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
         }
     }
 });