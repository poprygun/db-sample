package com.synchrony.poc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=com.synchrony.poc.config.DbConfig.class, loader=AnnotationConfigContextLoader.class)
@ActiveProfiles("default")
public class AppLoadTest {

    @Test
    public void loadApp() {
        System.out.println("===> Loaded");
    }
}
