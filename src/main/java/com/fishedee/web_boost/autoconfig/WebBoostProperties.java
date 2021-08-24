package com.fishedee.web_boost.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix="spring.web-boost")
public class WebBoostProperties {
    private boolean enable;
}
