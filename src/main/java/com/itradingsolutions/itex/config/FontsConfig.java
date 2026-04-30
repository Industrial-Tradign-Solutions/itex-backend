package com.itradingsolutions.itex.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@Component
public class FontsConfig {

    @PostConstruct
    public void configureFonts() throws MalformedURLException {
        URLClassLoader loader = new URLClassLoader(
                new URL[]{new File("src/main/resources/fonts/verdana-fonts.jar").toURI().toURL()},
                Thread.currentThread().getContextClassLoader()
        );
        Thread.currentThread().setContextClassLoader(loader);
    }
}
