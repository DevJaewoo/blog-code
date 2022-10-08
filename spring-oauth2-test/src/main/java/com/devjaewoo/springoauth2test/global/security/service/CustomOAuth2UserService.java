package com.devjaewoo.springoauth2test.global.security.service;

import com.devjaewoo.springoauth2test.domain.client.Client;
import com.devjaewoo.springoauth2test.domain.client.ClientRepository;
import com.devjaewoo.springoauth2test.domain.client.OAuth2Attributes;
import com.devjaewoo.springoauth2test.domain.client.SessionClient;
import com.devjaewoo.springoauth2test.global.security.config.SessionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final ClientRepository clientRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        log.info("Save user {}", oAuth2User.getAttributes());
        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Client client = saveOrUpdate(attributes);
        httpSession.setAttribute(SessionConfig.SESSION_NAME, new SessionClient(client));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(client.getRole().key)), attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private Client saveOrUpdate(OAuth2Attributes attributes) {
        Client client = clientRepository.findByEmail(attributes.getEmail())
                .map(entity -> {
                    entity.setEmail(attributes.getEmail());
                    entity.setName(attributes.getName());
                    return entity;
                })
                .orElse(attributes.toEntity());

        return clientRepository.save(client);
    }
}
