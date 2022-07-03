package com.revo.authservice.domain;

import com.revo.authservice.domain.dto.AuthorizedUser;
import com.revo.authservice.domain.exception.BadLoginException;
import com.revo.authservice.domain.exception.EmailInUseException;
import com.revo.authservice.domain.port.Encoder;
import com.revo.authservice.domain.port.Jwt;
import com.revo.authservice.domain.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    private static final String TEST_USER_EMAIL = "TEST_EMAIL@EMAIL.PL";
    private static final String TEST_USER_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_USER_NAME = "TEST_NAME";
    private static final String TEST_USER_ID = "TEST_ID";
    private static final String TEST_TOKEN = "TEST_TOKEN";

    @Mock
    private UserRepository userRepository;
    @Mock
    private Jwt jwt;
    @Mock
    private Encoder encoder;

    @InjectMocks
    private UserServiceImp userServiceImp;

    private User user = new User(TEST_USER_ID, TEST_USER_NAME, TEST_USER_PASSWORD, TEST_USER_EMAIL);

    @Test
    void shouldCreateUser() {
        //given
        //when
        Mockito.when(userRepository.userExistsByEmail(Mockito.anyString())).thenReturn(Mono.just(false));
        Mockito.when(userRepository.userExistsByUsername(Mockito.anyString())).thenReturn(Mono.just(false));
        Mockito.when(userRepository.saveUser(Mockito.any(User.class))).thenReturn(Mono.just(user));
        //then
        Mono<User> userMono = userServiceImp.createUser(user);
        StepVerifier.create(userMono)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void shouldThrowErrorWhileCreatingUserIfUsernameIsInUse(){
        //given
        //when
        Mockito.when(userRepository.userExistsByEmail(Mockito.anyString())).thenReturn(Mono.just(false));
        Mockito.when(userRepository.userExistsByUsername(Mockito.anyString())).thenReturn(Mono.just(true));
        //then
        Mono<User> userMono = userServiceImp.createUser(user);
        StepVerifier.create(userMono)
                .expectError();
    }

    @Test
    void shouldThrowErrorWhileCreatingUserIfEmailIsInUse(){
        //given
        //when
        Mockito.when(userRepository.userExistsByEmail(Mockito.anyString())).thenReturn(Mono.just(true));
        Mockito.when(userRepository.userExistsByUsername(Mockito.anyString())).thenReturn(Mono.just(false));
        //then
        Mono<User> userMono = userServiceImp.createUser(user);
        StepVerifier.create(userMono)
                .expectError();
    }

    @Test
    void shouldGetUsernameFromToken() {
        //given
        //when
        Mockito.when(userRepository.getUserByUsername(TEST_USER_NAME)).thenReturn(Mono.just(user));
        Mockito.when(jwt.getSubjectFromToken(TEST_TOKEN)).thenReturn(TEST_USER_NAME);
        //then
        Mono<AuthorizedUser> authorizedUserMono = userServiceImp.getUsernameFromToken(TEST_TOKEN);
        StepVerifier.create(authorizedUserMono)
                .expectNext(new AuthorizedUser(TEST_USER_NAME))
                .verifyComplete();
    }

    @Test
    void shouldThrowErrorWhileGettingUsernameFromToken() {
        //given
        //when
        Mockito.when(userRepository.getUserByUsername(TEST_USER_NAME)).thenReturn(Mono.empty());
        Mockito.when(jwt.getSubjectFromToken(TEST_TOKEN)).thenReturn(TEST_USER_NAME);
        //then
        Mono<AuthorizedUser> authorizedUserMono = userServiceImp.getUsernameFromToken(TEST_TOKEN);
        StepVerifier.create(authorizedUserMono)
                .expectError();
    }

// Nothing was tested here!
//    @Test
//    void shouldGetTokenFromUsername() {
//        //given
//        //when
//        Mockito.when(jwt.createTokenFromUsername(TEST_USER_NAME)).thenReturn(TEST_TOKEN);
//        //then
//        String token = userServiceImp.getTokenFromUsername(TEST_USER_NAME);
//        assertEquals(token, TEST_TOKEN);
//    }

    @Test
    void shouldLoginUser() {
        //given
        //when
        Mockito.when(userRepository.getUserByUsername(TEST_USER_NAME)).thenReturn(Mono.just(user));
        Mockito.when(encoder.passwordMatches(TEST_USER_PASSWORD, TEST_USER_PASSWORD)).thenReturn(true);
        //then
        Mono<User> userMono = userServiceImp.loginUser(user);
        StepVerifier.create(userMono)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void shouldThrowErrorWhileLoggingUser() {
        //given
        //when
        Mockito.when(userRepository.getUserByUsername(TEST_USER_NAME)).thenReturn(Mono.just(user));
        //then
        Mono<User> userMono = userServiceImp.loginUser(user);
        StepVerifier.create(userMono)
                .expectError(BadLoginException.class);
    }
}