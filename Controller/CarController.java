package com.shop.shop.Controller;

import com.shop.shop.Model.CarModel;
import com.shop.shop.Service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Car")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping("/add/{userId}/{productId}/{quantity}")
    public ResponseEntity<String> addProductToCar(@PathVariable Long userId,
                                                  @PathVariable Long productId,
                                                  @PathVariable int quantity){
        carService.addProductToCar(userId, productId, quantity);
        return ResponseEntity.ok("Product added successfully!");
    }

    @PostMapping("/edit/{userId}/{productId}/{quantity}")
    public ResponseEntity<String> editQuantity(@PathVariable Long userId,
                                                  @PathVariable Long productId,
                                                  @PathVariable int quantity){
        carService.editQuantity(userId, productId, quantity);
        return ResponseEntity.ok("Quantity Edited successfully!");
    }

    @PostMapping("/buy/{carId}")
    public ResponseEntity<String> buyCar(@PathVariable Long carId){
        carService.buyCar(carId);
        return ResponseEntity.ok("Car bought successfully!");
    }

    @GetMapping("{userId}")
    public List<CarModel> getAllCars(@PathVariable Long userId){
        return carService.getAllCarsByUserId(userId);
    }

    @GetMapping("/delete/product/{userId}/{productId}")
    public ResponseEntity<String> deleteProductFromCar(@PathVariable Long userId, @PathVariable Long productId){
        carService.deleteProductFromCar(userId, productId);
        return ResponseEntity.ok("Product deleted successfully!");
    }

    @GetMapping("/delete/{carId}")
    public ResponseEntity<String> deleteCar(@PathVariable Long carId){
        carService.deleteCar(carId);
        return ResponseEntity.ok("Car deleted successfully!");
    }
}
