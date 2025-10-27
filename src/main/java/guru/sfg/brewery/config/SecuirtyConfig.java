package guru.sfg.brewery.config;

import guru.sfg.brewery.security.sfgPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecuirtyConfig extends WebSecurityConfigurerAdapter
{

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests(authorize-> {
                    authorize.antMatchers("/" , "/webjars/**","/login","/resources/**").permitAll();
                    authorize.antMatchers(HttpMethod.GET,"/beers").permitAll();
                    authorize.antMatchers(HttpMethod.GET,"/api/v1/beer/**").permitAll();
                })
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and().formLogin()
                .and().httpBasic();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // old bcrypt(10) for existing users
        String oldBcrypt2003 = "{bcrypt}" + new BCryptPasswordEncoder(10).encode("2003");

        UserDetails hoss  = User.withUsername("Hoss")
                .password(oldBcrypt2003).roles("USER").build();

        UserDetails admin = User.withUsername("admin")
                .password(oldBcrypt2003).roles("ADMIN").build();

        // bcrypt15 for scott (uses delegating default id = bcrypt15)
        String scottHash = passwordEncoder().encode("tiger");
        UserDetails scott = User.withUsername("scott")
                .password(scottHash).roles("CUSTOMER").build();

        return new InMemoryUserDetailsManager(hoss, admin, scott);
    }



    @Bean
    PasswordEncoder passwordEncoder()
    {
        return sfgPasswordEncoder.createDelegatingPasswordEncoder();
    }
}
