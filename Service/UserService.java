package com.shop.shop.Service;
import com.shop.shop.Holder.TokenHolder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.shop.shop.Model.ProductModel;
import com.shop.shop.Model.UserModel;
import com.shop.shop.Repository.ProductRepo;
import com.shop.shop.Repository.UserRepository;
import com.shop.shop.config.JwtService;


@Service
public class UserService {
    @Autowired
    private  ProductRepo productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // list of all products
       public  List <ProductModel>  getAllProducts(){
            return productRepository.findAll() ;
    }

    // list of products in the same category
    public  List<ProductModel> getbycategory(String category) {
            return productRepository.findByCategory(category);
    }

    // search for product by name
    public  ResponseEntity <ProductModel> search(String pName) {
        try {
            ProductModel product=productRepository.findBypName(pName);
            return  new ResponseEntity<>( product ,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }

    //signup
    //public UserModel signup( UserModel user) {
        
    //   return userRepository.save(user);
    // }

    //login
    //public ResponseEntity <UserModel> login(String email, String password) {
    //    try {
    //        UserModel user = userRepository.findByEmailAndPassword(email , password);
    //        return new ResponseEntity<>(user ,HttpStatus.OK);
    //    } catch (Exception e) {
    //        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    //    }

    // }
    //edit should send password and send role with data 
    public ResponseEntity<UserModel> editUesr(UserModel user) {
        try {
            
            UserModel updateUser =userRepository.findByEmail(JwtService.extractUsername(TokenHolder.getToken()));
            updateUser.setFull_name(user.getFull_name());
            updateUser.setUsername(user.getUsername());
            updateUser.setAddress(user.getAddress());
            updateUser.setEmail(user.getEmail());
            updateUser.setPhone(user.getPhone());
            updateUser.setPassword(passwordEncoder.encode(user.getPassword()));
            updateUser.setConfirm_password(passwordEncoder.encode(user.getConfirm_password()));
            updateUser.setZip_code(user.getZip_code());
            updateUser.setRole(user.getRole());
            user.setImage(user.getImage());
            userRepository.save(updateUser);
            return new ResponseEntity<>(updateUser,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(user,HttpStatus.NOT_FOUND);
        }

    }
    //deleteuser

    public ResponseEntity<Void> deleteUsr() {
        try {
            UserModel user=userRepository.findByEmail(JwtService.extractUsername(TokenHolder.getToken()));
            userRepository.delete(user);
            TokenHolder.clearToken();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("the user cant delete (not found)");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        
    }

    public void logout() {
        TokenHolder.clearToken();
    }

    public ResponseEntity<UserModel> getCurrentUser() {
        
        String email = JwtService.extractUsername(TokenHolder.getToken());
        UserModel currentUser = userRepository.findByEmail(email);

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    
}
//update the current user
public ResponseEntity<UserModel> updateCurrentUser(@RequestBody UserModel user) {
try {
    String email = JwtService.extractUsername(TokenHolder.getToken());
    UserModel currentUser = userRepository.findByEmail(email);

    if (currentUser == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    currentUser.setFull_name(user.getFull_name());
    currentUser.setUsername(user.getUsername());
    currentUser.setAddress(user.getAddress());
    currentUser.setEmail(user.getEmail());
    currentUser.setPhone(user.getPhone());
    currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
    currentUser.setConfirm_password(passwordEncoder.encode(user.getConfirm_password()));
    currentUser.setZip_code(user.getZip_code());
    currentUser.setRole(user.getRole());
    userRepository.save(currentUser);

    userRepository.save(currentUser);

    return new ResponseEntity<>(currentUser, HttpStatus.OK);
} catch (Exception e) {
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
}

}
  


}


