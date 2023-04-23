package com.example.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

public class Controller {
    /**
     * WebRTC + WebSocket
     */
    @RequestMapping("webrtc/{username}.html")
    public ModelAndView socketChartPage(@PathVariable String username) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("webrtc.html");
        modelAndView.addObject("username",username);
        return modelAndView;
    }
}
