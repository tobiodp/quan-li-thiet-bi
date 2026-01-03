package com.example.ResourcesManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để đơn giản hóa cho API và Form
                .formLogin(form -> form
                        .loginPage("/viewLogin") // 1. URL dẫn đến trang giao diện
                        .loginProcessingUrl("/perform_login") // 2. URL để form POST dữ liệu vào
                        .defaultSuccessUrl("/login-success", true) // Đăng nhập xong thì về controller xử lý
                        .failureUrl("/viewLogin?error=true") // Đăng nhập thất bại
                        .permitAll() // Cho phép tất cả mọi người truy cập vào trang login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/viewLogin?logout=true")
                        .permitAll()
                )
                .authorizeHttpRequests(auth -> auth
                        // === 1. PUBLIC (Không cần đăng nhập) ===
                        .requestMatchers(
                                "/login", "/viewLogin",
                                "/register", "/viewRegister",
                                "/logout",
                                "/login-success",
                                "/device/qr/**", // Cho phép xem thông tin thiết bị khi quét QR (public)
                                "/css/**", "/js/**", "/images/**", "/img/**", "/static/**" // Cho phép tài nguyên tĩnh
                        ).permitAll()

                        // === 2. TRANG DÀNH RIÊNG CHO USER (Và Admin cũng vào được để test) ===
                        .requestMatchers("/user-dashboard").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/request-device/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/user-requests").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/user-device-history").hasAnyRole("USER", "ADMIN")

                        // === 3. CHỨC NĂNG DÙNG CHUNG (User xem được danh sách) ===
                        .requestMatchers(HttpMethod.GET, "/viewDevices", "/devices").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/viewChapters", "/viewDepartments").hasAnyRole("ADMIN", "USER")

                        // === 4. TRANG QUẢN TRỊ (CHỈ ADMIN) ===
                        // Dashboard
                        .requestMatchers("/dashboardController").hasRole("ADMIN")

                        // Lịch sử mượn/trả
                        .requestMatchers("/admin/device-history").hasRole("ADMIN")

                        // Quản lý nhân viên
                        .requestMatchers("/viewEmployees", "/employees/**", "/user/**").hasRole("ADMIN")

                        // Quản lý thiết bị (Thêm/Sửa/Xóa)
                        .requestMatchers("/devices/add", "/devices/edit/**", "/devices/delete/**", "/saveDevice").hasRole("ADMIN")

                        // Quản lý phòng ban (Thêm/Sửa/Xóa)
                        .requestMatchers("/chapter/**", "/chapters/**").hasRole("ADMIN")

                        // Quản lý yêu cầu (Duyệt/Từ chối)
                        .requestMatchers(HttpMethod.GET,"/viewRequests").hasRole("ADMIN")
                        .requestMatchers("/api/request/**").hasRole("ADMIN") // Các API duyệt, từ chối, check-stock
                        .requestMatchers(HttpMethod.POST ,"/api/request/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST ,"/admin/request/reject").hasRole("ADMIN")

                        // Quản lý thông báo
                        .requestMatchers("/user-notifications").hasAnyRole("ADMIN" , "USER")

                        // Quản lý thông báo
                        .requestMatchers("/admin/notifications/**").hasRole("ADMIN")

                        //Trả thiết bị
                        .requestMatchers(HttpMethod.GET ,"/admin/borrowed-devices").hasRole("ADMIN")

                        // === 5. MẶC ĐỊNH: BẮT BUỘC ĐĂNG NHẬP ===
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}