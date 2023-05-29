package com.brave.www.Controller;

import com.brave.www.pojo.Result;
import com.brave.www.rtmp.ConvertVideoPakcet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/live/video")
@Slf4j
public class VideoController {
    @RequestMapping("/start")
    public Result start(@RequestBody Map<String,String> params) {
        log.info("start device ,{}",params);
        String deviceId = params.get("deviceId");
        if(null == deviceId || "".equals(deviceId)){
            return Result.fail(null,"device id is required",504);
        }
        if(null == params.get("formUrl") || "".equals(params.get("formUrl"))){
            return Result.fail(null,"formUrl is required",504);
        }
        if(null == params.get("toUrl") || "".equals(params.get("toUrl"))){
            return Result.fail(null,"toUrl is required",504);
        }

        ConvertVideoPakcet.start(deviceId,params.get("formUrl"),params.get("toUrl"));

        return Result.success(null,"start live success");
    }
}
