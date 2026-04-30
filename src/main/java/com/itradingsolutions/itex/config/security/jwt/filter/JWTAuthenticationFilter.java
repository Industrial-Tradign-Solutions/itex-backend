package com.itradingsolutions.itex.config.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDetailDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.requests.AuthenticationRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.AuthenticationResponse;
import com.itradingsolutions.itex.api.common.util.models.responses.ErrorResponse;
import com.itradingsolutions.itex.api.common.util.services.IHistoryService;
import com.itradingsolutions.itex.config.security.jwt.service.JWTService;
import com.itradingsolutions.itex.config.websocket.WebSocketHandlerItex;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final WebSocketHandlerItex socketHandler;
    private final IHistoryService historyService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService, IHistoryService historyService) {
        this.authManager = authenticationManager;
        this.historyService = historyService;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        this.jwtService = jwtService;
        this.socketHandler = new WebSocketHandlerItex(this.jwtService);
    }
    
    private void validateSystemOut() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(23, 50);
        LocalTime end = LocalTime.of(23, 59);
        if (!now.isBefore(start) && !now.isAfter(end))
            throw new UsernameNotFoundException("The system is in a reboot, it will be enabled after 12:00AM.");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        validateSystemOut();
        AuthenticationRequest authenticationRequest;
        Authentication authResp;
        try {
            authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), AuthenticationRequest.class);
        } catch (IOException e) {
            throw new UsernameNotFoundException("Problems reading the data, check that all are correct: 'Username', 'Password'");
        }

        try {
            authResp = authManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authenticationRequest.getUsername(),
                                    authenticationRequest.getPassword()
                            )
                    );
        } catch (AuthenticationException ae) {
            if (ae.getMessage().equals("User is disabled"))
                throw new UsernameNotFoundException("User is disabled!", ae);
            else if (ae.getMessage().equals("Bad credentials"))
                throw new UsernameNotFoundException("Bad credentials!", ae);
            else
                throw new UsernameNotFoundException("Error entering your credentials!", ae);
        }
        return authResp;
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        UserDetailDTO userData = ((UserDetailDTO) authResult.getPrincipal());
        String token = jwtService.createToken(authResult);
        response.getWriter().write(new ObjectMapper().writeValueAsString(getAuthenticationResponse(token, userData)));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        historyService.saveHistoryNotData(HistoryActions.LOGIN, userData.getUsername());
    }

    private AuthenticationResponse getAuthenticationResponse(String token, UserDetailDTO userData) {
        AuthenticationResponse body = new AuthenticationResponse();

        body.setToken(token);
        body.setEmail(userData.getEmail());
        body.setId(userData.getId());
        body.setFullName(userData.getName() + " " + userData.getLastName());
        body.setRole(userData.getRole().getName());
        body.setRoleId(userData.getRole().getId());
        body.setPassChanged(userData.isPassChanged());
        body.setUser(userData.getUser());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date expirationToken = new Date(jwtService.getExpirationTokenMillis());

        body.setExpirationToken(sdf.format(expirationToken));
        new Thread(() -> socketHandler.closeSessionUser(token, userData.getId(), WebSocketMessageValue.NEW_LOGIN)).start();
        return body;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        new ErrorResponse(
                                "¡Invalid credentials! \n " + failed.getMessage(),
                                HttpStatus.FORBIDDEN.value(), null
                        )
                )
        );
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
    }
}
