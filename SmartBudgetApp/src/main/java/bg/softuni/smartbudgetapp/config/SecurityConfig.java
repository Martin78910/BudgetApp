package bg.softuni.smartbudgetapp.config;


import bg.softuni.smartbudgetapp.services.impl.SmartBudgetUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final SmartBudgetUserDetailsService userDetailsService;

    public SecurityConfig(SmartBudgetUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/about", "/contact", "/contact/send", "/users/register", "/users/login", "/error").permitAll()
                        // Разрешаваме достъп до access-denied
                        .requestMatchers("/access-denied").permitAll()

                        // Това позволява GET/POST /api/advice без логин
                        .requestMatchers("/api/advice", "/api/advice/**").permitAll()

                        // Достъпно само за ADMIN ролята => hasRole("ADMIN") == "ROLE_ADMIN"
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Само за логнати потребители:
                        .requestMatchers("/users/profile").authenticated()
                        .requestMatchers("/users/profile/update").authenticated()
                        .requestMatchers("/users/accounts").authenticated()
                        .requestMatchers("/users/budgets").authenticated()
                        .requestMatchers("/users/transactions").authenticated()
                         .requestMatchers("/users/budget-report").authenticated()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // ВАЖНО: казваме, че логин страница е /users/login
                        .loginPage("/users/login")

                        // Ако във form полето е name="email":
                        .usernameParameter("email")
                        .passwordParameter("password")

                        // Къде да праща след успех
                        .defaultSuccessUrl("/users/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // Използваме accessDeniedPage, за да покажем персонализиран access-denied шаблон
                .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))

               //.csrf(csrf -> csrf.disable());
             //.csrf(Customizer.withDefaults());

        // The key: ignoring CSRF for API calls
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }

}
