package com.example.bookingticket.repository;

import com.example.bookingticket.model.CartItem;
import com.example.bookingticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.user = :user ORDER BY c.addedTime DESC")
    List<CartItem> findByUserOrderByAddedTimeDesc(@Param("user") User user);
    
    void deleteByUser(User user);
} 