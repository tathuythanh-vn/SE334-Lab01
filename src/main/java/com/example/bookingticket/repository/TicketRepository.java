package com.example.bookingticket.repository;

import com.example.bookingticket.model.Ticket;
import com.example.bookingticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
    List<Ticket> findByUserOrderByBookingTimeDesc(User user);
} 