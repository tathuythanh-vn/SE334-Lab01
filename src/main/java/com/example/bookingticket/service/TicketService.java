package com.example.bookingticket.service;

import com.example.bookingticket.model.Seat;
import com.example.bookingticket.model.Ticket;
import com.example.bookingticket.model.User;
import com.example.bookingticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TicketService {
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    public boolean reserveSeat(Seat seat) {
        lock.lock();
        try {
            if (seat.isAvailable()) {
                seat.reserve(); // Đánh dấu ghế là đã bán
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public Ticket bookTicket(Ticket ticket) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(auth.getName());
        
        ticket.setUser(currentUser);
        currentUser.addTicket(ticket);
        
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getCurrentUserTickets() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(auth.getName());
        return ticketRepository.findByUserOrderByBookingTimeDesc(currentUser);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    @Transactional
    public void cancelTicket(Long ticketId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(auth.getName());
        
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
            
        if (!ticket.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to cancel this ticket");
        }
        
        currentUser.removeTicket(ticket);
        ticketRepository.delete(ticket);
    }
}
