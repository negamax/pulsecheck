package com.force.guard.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Created by mohitaggarwal on 12/01/2016.
 */
@Aspect
@Component
public class LoggingAspect {
    /** Handle to the log file */
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public LoggingAspect() {

    }

    @Before("execution(* com.force.guard..*.*(..))")
    public void logMethodAccessBefore(JoinPoint joinPoint) {
        log.info("Entering " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + " *****");
    }

    @After("execution(* com.force.guard..*.*(..))")
    public void logMethodAccessAfter(JoinPoint joinPoint) {
        log.info("Leaving " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + " *****");
    }
}
