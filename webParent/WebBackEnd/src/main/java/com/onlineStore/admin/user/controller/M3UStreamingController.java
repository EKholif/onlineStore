//package com.onlineStore.admin.user.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//@RequestMapping("/stream")
//public class M3UStreamingController {
//
//    @Controller
//    public class M3UController {
//
//GetMapping("/m3u")
//            @    public String showM3UPage(ModelAndView model) {
//            // Replace the URL with the actual URL of your M3U file
//            String m3uUrl = "https://iptv-org.github.io/iptv/languages/ara.m3u";
//
//            // Make an HTTP request to the URL
//            ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(m3uUrl, String.class);
//
//            System.out.println(responseEntity);
//            // Get the body of the response
//            String m3uContent = responseEntity.getBody();
//
//            // Add M3U content to the model
//            model.addObject("m3uContent", m3uContent);
//
//            return "m3u";
//        }
//    }
//}
