package com.example.bookingticket.service;

import com.example.bookingticket.model.CartItem;
import com.example.bookingticket.model.Ticket;
import com.example.bookingticket.model.User;
import com.example.bookingticket.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    public List<CartItem> getCurrentUserCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return List.of(); // Return empty list for non-authenticated users
        }
        
        try {
            User currentUser = userService.findByEmail(auth.getName());
            if (currentUser == null) {
                return List.of(); // Return empty list if user not found
            }
            
            try {
                return cartItemRepository.findByUserOrderByAddedTimeDesc(currentUser);
            } catch (Exception e) {
                System.err.println("Database error getting cart items: " + e.getMessage());
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("Error getting current user: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public CartItem addToCart(CartItem cartItem) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("User must be logged in to add items to cart");
        }
        
        try {
            User currentUser = userService.findByEmail(auth.getName());
            if (currentUser == null) {
                throw new RuntimeException("User not found");
            }
            
            cartItem.setUser(currentUser);
            return cartItemRepository.save(cartItem);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add item to cart: " + e.getMessage());
        }
    }

    @Transactional
    public void removeFromCart(Long cartItemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(auth.getName());
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
            
        if (!cartItem.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to remove this item");
        }
        
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(auth.getName());
        cartItemRepository.deleteByUser(currentUser);
    }

    @Transactional
    public List<Ticket> checkout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByEmail(auth.getName());
        
        List<CartItem> cartItems = cartItemRepository.findByUserOrderByAddedTimeDesc(currentUser);
        List<Ticket> tickets = cartItems.stream()
            .map(cartItem -> {
                Ticket ticket = new Ticket();
                ticket.setMovieName(cartItem.getMovieName());
                ticket.setShowTime(cartItem.getShowTime());
                ticket.setSeatNumber(cartItem.getSeatNumber());
                ticket.setPrice(cartItem.getPrice());
                return ticketService.bookTicket(ticket);
            })
            .toList();
            
        clearCart();
        return tickets;
    }
} 