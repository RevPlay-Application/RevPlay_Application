package com.revature.revplay.utils;

import org.springframework.stereotype.Component;
import java.util.Base64;

@Component("base64Util")
public class Base64Util {
    public String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(data);
    }
}
