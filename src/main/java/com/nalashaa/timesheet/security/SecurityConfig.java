package com.nalashaa.timesheet.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableGlobalMethodSecurity(prePostEnabled =true)
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder()).usersByUsernameQuery(
				   "select emp_id,password, true from person where emp_id=?")
		  .authoritiesByUsernameQuery(
		   "select emp_id,role_name from person, role where person.role_id=role.id and person.emp_id=?");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception  {
			http.csrf().disable()
			.httpBasic()
			.and()
			.authorizeRequests()
			.antMatchers("/client/**", "/project/**", "/report/**", "/role/**", "/user/**", "/worklog/**", "/task/**").authenticated()
			.and()
			.authorizeRequests().antMatchers("/**").permitAll();
		
	}
}
