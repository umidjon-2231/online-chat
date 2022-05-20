package com.project.onlinechat.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.project.onlinechat.dto.ApiResponse;
import com.project.onlinechat.dto.LoginDto;
import com.project.onlinechat.entity.Chat;
import com.project.onlinechat.entity.Member;
import com.project.onlinechat.entity.User;
import com.project.onlinechat.entity.enums.Permission;
import com.project.onlinechat.entity.enums.Role;
import com.project.onlinechat.entity.enums.Status;
import com.project.onlinechat.repository.ChatRepository;
import com.project.onlinechat.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.description.method.MethodDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final ChatRepository chatRepository;

    @Value("${auth.token.secret}")
    private String secretKey;
    @Value("${auth.token.expire}")
    private Long expireTime=0L;
    @Value("${auth.token.cookie}")
    private String cookieName;

    public ApiResponse<User> register(LoginDto dto) {
        Optional<User> byEmail = userRepository.findByEmail(dto.getEmail());
        if(byEmail.isPresent()){
            return ApiResponse.<User>builder()
                    .message("This email already exist")
                    .build();
        }
        Optional<User> byUsername = userRepository.findByUsername(dto.getUsername());
        if(byUsername.isPresent()){
            return ApiResponse.<User>builder()
                    .message("This username already exist")
                    .build();
        }
        User save = userRepository.save(User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(Status.ONLINE)
                .build());
        Optional<Chat> optionalChat = chatRepository.findById(1L);
        optionalChat.ifPresent(chat -> chat.getMembers().add(Member.builder()
                .role(Role.MEMBER)
                .user(save)
                .permissions(List.of(Permission.SEND_MESSAGE))
                .build()));
        return ApiResponse.<User>builder()
                .success(true)
                .data(save)
                .message("New user saved!")
                .build();
    }

    public ApiResponse<String> login(LoginDto dto) {
        Optional<User> optionalUser = userRepository.findByUsername(dto.getUsername());
        if(optionalUser.isEmpty()){
            return ApiResponse.<String>builder()
                    .message("Username or password not valid")
                    .build();
        }
        User user = optionalUser.get();
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            return ApiResponse.<String>builder()
                    .message("Username or password not valid")
                    .build();
        }
        String token=generateToken(user);
        return ApiResponse.<String>builder()
                .success(true)
                .data(token)
                .message("Success auth!")
                .build();
    }

    private String generateToken(User user) {
        JwtBuilder builder = Jwts.builder();
        final Long userId = user.getId();
        long timeMillis = System.currentTimeMillis();
        Date issued = new Date(timeMillis);
        Date expire = new Date(timeMillis+expireTime);

        Gson gson=new Gson();
        builder.setId(userId.toString()).setIssuedAt(issued).setExpiration(expire).addClaims(gson.fromJson(
                gson.toJson(user),
                new TypeToken<Map<String, Object>>(){}.getType()
                ))
                .signWith(SIGNATURE_ALGORITHM, getSecret());
        return builder.compact();
    }

    public User getByToken(HttpServletRequest req){
        Cookie[] cookies = req.getCookies();
        if(cookies==null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                Claims body = Jwts.parser().setSigningKey(getSecret()).parseClaimsJws(cookie.getValue()).getBody();
                Optional<User> optionalUser = userRepository.findById(Long.valueOf(body.getId()));
                if(optionalUser.isEmpty()){
                    return null;
                }
                return optionalUser.get();
            }
        }
        return null;
    }

    public Key getSecret(){
        final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, SIGNATURE_ALGORITHM.getJcaName());
    }
}
