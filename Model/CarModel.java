package com.shop.shop.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Entity
@Table(name="car")
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean status;
    private double totalAmount;
    private LocalDateTime carDate;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private UserModel userModel;

    @OneToMany(mappedBy = "carModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("carModel")
    private List<CarProductModel> carProducts = new ArrayList<>();

    public CarModel(){

    }

    public CarModel(Long id, LocalDateTime carDate, boolean status, double totalAmount) {
        this.id = id;
        this.carDate = carDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCarDate() {
        return carDate;
    }

    public void setCarDate(LocalDateTime carDate) {
        this.carDate = carDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public List<CarProductModel> getCarProducts() {
        return carProducts;
    }

    public void setCarProducts(List<CarProductModel> carProducts) {
        this.carProducts = carProducts;
    }
}
