package com.shop.shop.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.shop.Model.CarModel;
import com.shop.shop.Model.CarProductModel;
import com.shop.shop.Model.ProductModel;
import com.shop.shop.Model.UserModel;
import com.shop.shop.Repository.CarProductRepository;
import com.shop.shop.Repository.CarRepository;
import com.shop.shop.Repository.ProductRepo;
import com.shop.shop.Repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CarProductRepository carProductRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    public void addProductToCar(Long userId, Long productId, int quantity) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        ProductModel product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        CarModel car = user.getCars().stream()
                .filter(c -> !c.isStatus())
                .findFirst()
                .orElse(null);

        if (car == null) {
            car = new CarModel();
            car.setUserModel(user);
            car.setStatus(false);
            car.setTotalAmount(0.0);
            car.setCarDate(LocalDateTime.now());
            carRepository.save(car);
        }

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getpName());
        }

        CarProductModel carProduct = carProductRepository.findByCarModelIdAndProductModelId(car.getId(), productId);

        if (carProduct != null) {
            int newQuantity = carProduct.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getpName());
            }
            carProduct.setQuantity(newQuantity);
        } else {
            carProduct = new CarProductModel();
            carProduct.setCarModel(car);
            carProduct.setProductModel(product);
            carProduct.setQuantity(quantity);
        }

        carProductRepository.save(carProduct);

        double totalAmount = car.getTotalAmount() + (product.getPrice() * quantity);
        car.setTotalAmount(totalAmount);
        carRepository.save(car);
    }

    @Transactional
    public void editQuantity(Long userId, Long productId, int quantity) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CarModel activeCar = user.getCars().stream()
                .filter(car -> !car.isStatus())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active car found for this user"));

        CarProductModel carProduct = carProductRepository.findByCarModelIdAndProductModelId(activeCar.getId(), productId);
        if (carProduct == null) {
            throw new RuntimeException("Car does not contain the product with ID: " + productId);
        }

        ProductModel product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getpName());
        }

        int oldQuantity = carProduct.getQuantity();
        carProduct.setQuantity(quantity);
        carProductRepository.save(carProduct);

        double totalAmount = activeCar.getTotalAmount();
        double price = product.getPrice();

        totalAmount -= (price * oldQuantity); 
        totalAmount += (price * quantity); 
        activeCar.setTotalAmount(totalAmount);

        carRepository.save(activeCar);
    }



    @Transactional
    public void buyCar(Long carId) {
        CarModel car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found"));
        if(car.isStatus()){
            throw new RuntimeException("This car has already been sold");
        }
        List<CarProductModel> carProducts = car.getCarProducts();

        for (CarProductModel carProduct : carProducts) {
            ProductModel product = carProduct.getProductModel();
            int quantity = carProduct.getQuantity();
            int stockQuantity = product.getStockQuantity();
            int quantitySolid = product.getQuantitySold() + quantity;
            if (stockQuantity < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getpName());
            }

            product.setStockQuantity(stockQuantity - quantity);
            product.setQuantitySold(quantitySolid);
            productRepo.save(product);
        }

        car.setStatus(true);
        carRepository.save(car);
    }


    public List<CarModel> getAllCarsByUserId(Long userId){
        List<CarModel> cars = carRepository.findByUserModelId(userId);
        if (cars == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        else {
            return cars;
        }
    }

    @Transactional
    public void deleteProductFromCar(Long userId, Long productId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CarModel activeCar = user.getCars().stream()
                .filter(car -> !car.isStatus())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active car found for this user"));

        CarProductModel carProduct = carProductRepository.findByCarModelIdAndProductModelId(activeCar.getId(), productId);

        if (carProduct == null) {
            throw new RuntimeException("Product not found in the car");
        }

        double totalAmount = activeCar.getTotalAmount();
        double price = carProduct.getProductModel().getPrice();
        int quantity = carProduct.getQuantity();

        totalAmount -= (price * quantity);
        activeCar.setTotalAmount(totalAmount);

        carRepository.save(activeCar);

        carProductRepository.delete(carProduct);
    }


    @Transactional
    public void deleteCar(Long carId) {
        CarModel car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found: " + carId));

        if (car.isStatus()) {
            throw new RuntimeException("Cannot delete a car that has already been sold.");
        }

        carRepository.delete(car);
    }



}
