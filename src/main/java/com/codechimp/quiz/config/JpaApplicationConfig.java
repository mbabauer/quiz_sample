package com.codechimp.quiz.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Configuration class for JPA.
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
@Configuration
@EnableJpaRepositories("com.codechimp.quiz.domain")
public class JpaApplicationConfig {
    public static Log _log = LogFactory.getLog(JpaApplicationConfig.class);
}
