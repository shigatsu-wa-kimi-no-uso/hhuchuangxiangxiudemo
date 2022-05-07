package team.hhu.chuangxiangxiudemo.homepage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController
{
    @GetMapping("/")
    public String homePageDirect()
    {
        return "/index";
    }

    @GetMapping("/news")
    public String newsDirect()
    {
        return "/news";
    }

    @GetMapping("/account/home")
    public String personalDirect()
    {
        return "/personal";
    }

}
