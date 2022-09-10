package com.meteorcat.mixdns.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author MeteorCat
 */
@Controller
@RequestMapping({"","/"})
public class IndexController {

    @RequestMapping({"","/","/index"})
    public String index(){
        return "index";
    }


}
