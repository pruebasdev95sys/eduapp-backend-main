package com.school.security;

import com.school.security.jwt.JwtEntryPoint;
import com.school.security.jwt.JwtTokenFilter;
import com.school.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MainSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and() // Habilita CORS
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/**").permitAll()
            // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ NUEVAS RUTAS PERMITIDAS SIN AUTENTICACIÓN ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
            .antMatchers("/health").permitAll()           // Health check personalizado
            .antMatchers("/api/health").permitAll()       // Health check API personalizado
            .antMatchers("/actuator/health").permitAll()  // Health check de Spring Actuator
            .antMatchers("/actuator/info").permitAll()    // Info de Spring Actuator
            .antMatchers("/error").permitAll()            // Páginas de error
            // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ NUEVAS RUTAS PERMITIDAS SIN AUTENTICACIÓN ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
            .anyRequest().authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtEntryPoint)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
