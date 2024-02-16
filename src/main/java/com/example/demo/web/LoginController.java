package com.example.demo.web;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.common.Constants;
import com.example.demo.services.MobileCodeSendService;
import com.example.demo.services.UserService;
import com.example.demo.vo.CheckCode;
import com.example.demo.vo.ResultData;
import com.google.code.kaptcha.impl.DefaultKaptcha;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private MobileCodeSendService mobileCodeSendService;  // 模拟手机短信验证码发送服务
    @Autowired
    private UserService userService;

    @GetMapping("/mobile/page")
    public String mobileLoginPage() {  // 跳转到手机短信验证码登录页面
        return "login-mobile";
    }

    @GetMapping("/code/mobile")
    @ResponseBody
    public Object sendMoblieCode(String mobile, HttpServletRequest request) {
        // 随机生成一个 4 位的验证码
        String code = RandomStringUtils.randomNumeric(4);

        // 将手机验证码文本存储在 Session 中，设置过期时间为 10 * 60s
        CheckCode mobileCode = new CheckCode(code, 10 * 60);
        request.getSession().setAttribute(Constants.MOBILE_SESSION_KEY, mobileCode);

        // 判断该手机号是否注册
        if(!userService.isExistByMobile(mobile)) {
            return new ResultData<>(1, "该手机号不存在！");
        }

        // 模拟发送手机短信验证码到指定用户手机
        mobileCodeSendService.send(mobile, code);
        return new ResultData<>(0, "发送成功！");
    }

    @GetMapping("/code/image")
    public void imageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 创建验证码文本
        String capText = defaultKaptcha.createText();
        // 创建验证码图片
        BufferedImage image = defaultKaptcha.createImage(capText);

        // 将验证码文本放进 Session 中
        CheckCode code = new CheckCode(capText);
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, code);

        // 将验证码图片返回，禁止验证码图片缓存
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpg", response.getOutputStream());
    }

    @GetMapping("/login/page")
    public String loginPage() {  // 获取登录页面
        return "login";
    }
}
