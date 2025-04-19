package com.shop.shop.auth;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.shop.shop.Holder.TokenHolder;
import com.shop.shop.Model.Role;
import com.shop.shop.Model.UserModel;
import com.shop.shop.Repository.UserRepository;
import com.shop.shop.config.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
@Autowired
private UserRepository userRepository;

@Autowired
private PasswordEncoder passwordEncoder;

@Autowired
private JwtService jwtService;

@Autowired
private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(UserModel request) {
        UserModel user = new UserModel();
        user.setFull_name(user.getFull_name());
        user.setUsername(user.getUsername());
        user.setAddress(user.getAddress());
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirm_password(passwordEncoder.encode(user.getConfirm_password()));
        user.setZip_code(user.getZip_code());
        user.setImage(user.getImage());
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse token=AuthenticationResponse.builder().token(jwtToken).build();
        TokenHolder.setToken(jwtToken);
        UserModel person =userRepository.findByEmail(JwtService.extractUsername(jwtToken));
        String State =person.getRole().toString();
        TokenHolder.setState(State);
        System.out.println("the token is  "+TokenHolder.getToken() + "  the email is "+JwtService.extractUsername(jwtToken) + State);
        return token;
    }
    public ResponseEntity<Map<String, String>> authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserModel user = userRepository.findByEmail(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        AuthenticationResponse token =AuthenticationResponse.builder().token(jwtToken).build();
        TokenHolder.setToken(jwtToken);
        UserModel person =userRepository.findByEmail(JwtService.extractUsername(jwtToken));
        String State =person.getRole().toString();
        TokenHolder.setState(State);
        System.out.println("the token is  "+TokenHolder.getToken() + "  the email is "+JwtService.extractUsername(jwtToken) + State);
        Map<String, String> response = new HashMap<>();
        response.put("role", State);
        response.put("token", jwtToken);
        return ResponseEntity.ok(response);
    }

}
