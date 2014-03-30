package org.springframework.samples.portfolio.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;


/**
 * ServletContext initializer for Spring Security specific configuration such as
 * the chain of Spring Security filters.
 * <p>
 * The Spring Security configuration is customized with
 * {@link org.springframework.samples.portfolio.config.WebSecurityConfig}.
 *
 * @author Rob Winch
 */
public class WebSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
}
