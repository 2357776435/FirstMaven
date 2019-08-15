package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.dto.SecKillResult;
import org.seckill.entity.SecKill;
import org.seckill.enums.SecKillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller// @Service @Component 注解注入到spring容器中,控制层的职责(接收参数,根据一些验证和判断做跳转的控制)
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分   /seckill/list
public class SecKillController {
    //写入日志标识当前类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    /**
     * 列表页的请求处理
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<SecKill> list = secKillService.getSecKillList();
        model.addAttribute("list", list);
        //list.jsp + model =ModelAndView
        return "list";//   WEB-INF/jsp/"list".jsp
    }

    /**
     * 详情页
     *
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            //  "/seckill" 指代最上层的秒杀模块 ,redirect重定向到 "/list" 上,默认回到列表页
            return "redirect:/seckill/list";
        }
        SecKill seckill = secKillService.getById(seckillId);
        if (seckill == null) {//防止用户随便传一个id
            //forward请求转发
            return ":forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";//  WEB-INF/jsp/"/{seckillId}/detail".jsp
    }

    /**
     * 输入秒杀地址的请求 ajax转换成json数据
     *
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody//告诉springMVC返回类型作为一个ajax输出,而默认的ajax输出是json格式
    public SecKillResult<Exposer> exposer(@PathVariable Long seckillId) {
        SecKillResult<Exposer> result;
        try {
            Exposer exposer = secKillService.exportSecKillUrl(seckillId);
            result = new SecKillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SecKillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    /**
     * 获取秒杀地址
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SecKillResult<SecKillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone", required = false) Long userPhone,
                                                   @PathVariable("md5") String md5) {
        //springMVC valid(有效)
        if (userPhone == null) {
            return new SecKillResult<SecKillExecution>(false, "未注册");
        }
        SecKillResult<SecKillExecution> result;
        try {
            SecKillExecution execution = secKillService.executeSecKill(seckillId, userPhone, md5);
            return new SecKillResult<SecKillExecution>(true, execution);
        } catch (RepeatKillException e) {//重复秒杀异常
            SecKillExecution execution = new SecKillExecution(seckillId, SecKillStateEnum.REPEAT_KILL);
            return new SecKillResult<SecKillExecution>(true, execution);
        } catch (SecKillCloseException e) {//秒杀关闭异常
            SecKillExecution execution = new SecKillExecution(seckillId, SecKillStateEnum.END);
            return new SecKillResult<SecKillExecution>(true, execution);
        } catch (Exception e) {//内部系统异常
            logger.error(e.getMessage(), e);
            SecKillExecution execution = new SecKillExecution(seckillId, SecKillStateEnum.INNER_ERROR);
            return new SecKillResult<SecKillExecution>(true, execution);
        }
    }

    /**
     * 获取系统的当前时间
     *
     * @return
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SecKillResult<Long> time() {
        Date now = new Date();
        return new SecKillResult(true, now.getTime());
    }
}
