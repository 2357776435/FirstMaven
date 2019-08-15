//存放注意交互逻辑js代码
//javascript模块化  secKill.detal.init(params);==>包名.类名.方法(参数);
var secKill = {
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId +'/'+ md5 + '/execution';
        },
    },
    //处理秒杀逻辑
    handleSecKillKill: function (seckillId, node) {
        //获取秒杀地址，控制显示逻辑。执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(secKill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    //获取秒杀地址
                    var md5=exposer['md5'];
                    var killUrl=secKill.URL.execution(seckillId,md5);
                    console.log("killUrl"+killUrl);
                    //绑定一次点击事件，防止用户一直点击多次，数据会阻塞服务器
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求
                        //1:先禁用按钮
                        $(this).addClass('disabled');
                        //2:发送秒杀请求,执行秒杀
                        $.post(killUrl,{},function (result) {
                            var killResult=result['data'];
                            var state=killResult['state'];
                            var stateInfo=killResult['stateInfo'];
                            //显示秒杀结果
                            node.html('<span class="label label-success">'+stateInfo+'</span>');
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀,防止用户Pc端时间和服务器时间有差别,做优化
                    var nowTime = exposer['now'];
                    var startTime = exposer['start'];
                    var endTime = exposer['end'];
                    //重新计算计时逻辑
                    secKill.countdown(seckillId, nowTime, startTime, endTime);
                }
            } else {
                console.log("result:" + result);
            }
        });
    },
    validatePhone: function (phone) {
        //判断手机号在cookie是否存在,输入的手机号长度为11,isNaN判断是否是非数字,此处必须是数字
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckill_box = $('#seckill_box');
        //时间判断
        if (nowTime > endTime) {
            //秒杀结束
            seckill_box.html('秒杀已结束!');
        } else if (nowTime < startTime) {
            //秒杀未开始,计时事件绑定  +1000是防止用户端的计时服务出现计时偏移
            var killTime = new Date(startTime + 1000);
            console.log(killTime);
            //countdown(基础时间,回调函数)
            seckill_box.countdown(killTime, function (event) {
                //设置时间格式,killTime(基准时间),strftime(%D %H %M %S)(天时分秒)指定现在时间到基础时间的倒计时
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                console.log(format);
                seckill_box.html(format);
            }).on('finish.countdown', function () {
                secKill.handleSecKillKill(seckillId, seckill_box);
            });
        } else {
            //秒杀开始
            secKill.handleSecKillKill(seckillId, seckill_box);
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (parms) {
            //实现手机验证和登录,计时交互，来规划我们的交互流程，并在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            //验证手机号
            if (!secKill.validatePhone(killPhone)) {//判断cookie是否存在该手机号,是否长度为11和且为纯数字，都满足，返回true,取反,为false,已经完成登录了
                //绑定phone,控制输出
                var killPhoneModal = $('#killPhoneModal');
                //显示弹出层
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var killPhoneKey = $('#killPhoneKey').val();//获取文本框输入的值
                    if (secKill.validatePhone(killPhoneKey)) {
                        //电话写入cookie,控制cookie有效期expires:7天,path:
                        $.cookie('killPhone', killPhoneKey, {
                            expires: 7,
                            path: '/seckill'
                        });
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误!</label>').show(300);
                    }
                });
            }
            //完成登录
            var seckillId = parms['seckillId'];
            var startTime = parms['startTime'];
            var endTime = parms['endTime'];
            $.get(secKill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    console.log('nowTime:');
                    //时间判断,计时交互
                    secKill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            });
        }
    }
}