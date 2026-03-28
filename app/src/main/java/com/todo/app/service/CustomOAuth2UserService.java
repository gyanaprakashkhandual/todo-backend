package com.todo.app.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.todo.app.model.AuthProvider;
import com.todo.app.model.User;
import com.todo.app.repository.UserRepository;
import com.todo.app.security.UserPrincipal;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();
        Map<String, Object> attrs = oAuth2User.getAttributes();

        String email;
        String name;
        String imageUrl;
        String providerId;
        AuthProvider provider;

        if ("google".equals(registrationId)) {
            email = (String) attrs.get("email");
            name = (String) attrs.get("name");
            imageUrl = (String) attrs.get("picture");
            providerId = (String) attrs.get("sub");
            provider = AuthProvider.GOOGLE;
        } else if ("github".equals(registrationId)) {
            email = (String) attrs.get("email");
            name = (String) attrs.get("login");
            imageUrl = (String) attrs.get("avatar_url");
            providerId = String.valueOf(attrs.get("id"));
            provider = AuthProvider.GITHUB;
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        User user = userRepository.findByEmail(email)
                .map(existing -> updateExistingUser(existing, name, imageUrl))
                .orElseGet(() -> registerNewUser(email, name, imageUrl, provider, providerId));

        return UserPrincipal.create(user, attrs);
    }

    private User registerNewUser(String email, String name, String imageUrl,
            AuthProvider provider, String providerId) {
        User user = User.builder()
                .email(email)
                .name(name)
                .imageUrl(imageUrl)
                .provider(provider)
                .providerId(providerId)
                .emailVerified(true)
                .build();
        return userRepository.save(user);
    }

    private User updateExistingUser(User user, String name, String imageUrl) {
        user.setName(name);
        user.setImageUrl(imageUrl);
        return userRepository.save(user);
    }
}