package com.shop.shop.Repository;

import com.shop.shop.Model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<CarModel, Long> {
    List<CarModel> findByUserModelId(Long userId);
}
