package com.devjaewoo.springoauth2test.domain.client;

import com.devjaewoo.springoauth2test.global.security.config.SessionConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("")
    public ResponseEntity<?> getClient(HttpServletRequest request) throws JsonProcessingException {

        String authorities = SecurityContextHolder.getContext().getAuthentication().getName();
        Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Name: {}\n Credentials: {}\n Details: {}\n Principal: {}\n", authorities, objectMapper.writeValueAsString(credentials), objectMapper.writeValueAsString(details), objectMapper.writeValueAsString(principal));

        HttpSession session = request.getSession(false);
        SessionClient sessionClient = (SessionClient) session.getAttribute(SessionConfig.SESSION_NAME);

        Client client = clientRepository.findByEmail(sessionClient.getEmail()).orElseThrow(() -> new IllegalArgumentException("인증되지 않은 사용자입니다."));
        return ResponseEntity.ok(client.toString());
    }
}
