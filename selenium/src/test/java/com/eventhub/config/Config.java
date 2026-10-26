package com.eventhub.config;

import java.time.Duration;

/** Test settings. Override URLs and headed mode with -DbaseUrl, -DapiUrl, -Dheaded=true. */
public final class Config {
    private Config() {}

    public static final String BASE_URL = System.getProperty("baseUrl", "https://eventhub.rahulshettyacademy.com");
    public static final String API_URL  = System.getProperty("apiUrl",  "https://api.eventhub.rahulshettyacademy.com/api");
    public static final boolean HEADED  = Boolean.getBoolean("headed");
    public static final Duration WAIT   = Duration.ofSeconds(5);

    public static final String USER_EMAIL    = "rahulshetty1@gmail.com";
    public static final String USER_PASSWORD = "Magiclife1!";

    /** Second account, for cross-user (sandbox isolation) tests. */
    public static final String OTHER_USER_EMAIL    = "rahulshetty1@yahoo.com";
    public static final String OTHER_USER_PASSWORD = "Magiclife1!";
}
