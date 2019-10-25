package com.example.demo.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/demo2")
public class WorldService {
    private static final Logger logger = LoggerFactory.getLogger(WorldService.class);
    @RequestMapping(value = "/world",method = RequestMethod.POST)
    public String world(@RequestBody WorldVo worldVo){
        logger.info("world:"+worldVo.toString());
        return "world"+worldVo.getName()+":"+worldVo.getAge();
    }
}
