package demoweb1.demo01.controller;

import com.example.demo.rest.WorldVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rest.RestTraceClient;

@RequestMapping(value = "/demo1")
@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(String name) {
        logger.info("hello method hahaha");
        String url = "http://localhost:8081/demo2/world";
        WorldVo worldVo = new WorldVo();
        worldVo.setAge(323);
        worldVo.setName("hr");
        String result = RestTraceClient.request(url, HttpMethod.POST, worldVo, String.class);
        System.out.println(result);
        WorldVo worldVo2 = new WorldVo();
        worldVo2.setAge(11111);
        worldVo2.setName("hr222");
        String result2 = RestTraceClient.request(url, HttpMethod.POST, worldVo, String.class);
        System.out.println(result2);
        return "hello:" + result;
    }
}
