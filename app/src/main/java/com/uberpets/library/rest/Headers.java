package com.uberpets.library.rest;

//import com.uberpets.login.Session;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    public final Map<String, String> headers = new HashMap<>();

    public Headers() {
    }

    public Headers add(String key, String val) {
        headers.put(key, val);
        return this;
    }

    public Headers authorization(String value) {
        return add("Authorization", value);
    }

    public static Headers Authorization(String value) {
        return new Headers().authorization(value);
    }

    public static Headers Authorization(/*Session value*/) {
        /*if (value != null)
            return new Headers().authorization(value.getSessionToken());*/
        return new Headers();
    }
}
