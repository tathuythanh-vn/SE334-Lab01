package com.example.bookingticket.controller;

import com.example.bookingticket.model.CartItem;
import com.example.bookingticket.model.Movie;
import com.example.bookingticket.model.Seat;
import com.example.bookingticket.model.Ticket;
import com.example.bookingticket.repository.SeatRepository;
import com.example.bookingticket.service.CartService;
import com.example.bookingticket.service.MovieService;
import com.example.bookingticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final MovieService movieService;
    private final CartService cartService;
    private final SeatRepository seatRepo;

    @Autowired
    public TicketController(TicketService ticketService, MovieService movieService, CartService cartService) {
        this.ticketService = ticketService;
        this.movieService = movieService;
        this.cartService = cartService;
        this.seatRepo = new SeatRepository(48);
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) Long movieId, Model model) {
        if (movieId == null) {
            return "redirect:/movie";
        }
        
        Movie selectedMovie = movieService.getMovieById(movieId);
        if (selectedMovie == null) {
            return "redirect:/movie";
        }
            
        model.addAttribute("movie", selectedMovie);
        model.addAttribute("seats", seatRepo.getSeats(movieId));
        return "booking";
    }

    @PostMapping("/reserve")
    public String reserveSeat(@RequestParam Long movieId, @RequestParam Integer seatNumber, Model model, RedirectAttributes redirectAttributes) {
        if (seatNumber == null || movieId == null) {
            return "redirect:/movie";
        }

        Movie selectedMovie = movieService.getMovieById(movieId);
        if (selectedMovie == null) {
            return "redirect:/movie";
        }

        Seat seat = seatRepo.getSeat(movieId, seatNumber);
        if (seat == null) {
            return "redirect:/movie";
        }

        try {
            // First check if seat is available
            if (!seat.isAvailable()) {
                redirectAttributes.addFlashAttribute("error", "Seat " + seatNumber + " is already booked.");
                return "redirect:/tickets/?movieId=" + movieId;
            }

            // Reserve the seat
            if (!ticketService.reserveSeat(seat)) {
                redirectAttributes.addFlashAttribute("error", "Failed to reserve seat " + seatNumber);
                return "redirect:/tickets/?movieId=" + movieId;
            }

            // Create cart item
            CartItem cartItem = new CartItem();
            cartItem.setMovieName(selectedMovie.getTitle());
            cartItem.setSeatNumber(String.valueOf(seatNumber));
            cartItem.setPrice(selectedMovie.getTicketPrice());
            cartItem.setShowTime(LocalDateTime.now().plusDays(1));
            cartItem.setAddedTime(LocalDateTime.now());
            
            // Add to cart
            cartService.addToCart(cartItem);
            
            redirectAttributes.addFlashAttribute("success", "Seat " + seatNumber + " added to cart");
            return "redirect:/tickets/?movieId=" + movieId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add seat to cart: " + e.getMessage());
            return "redirect:/tickets/?movieId=" + movieId;
        }
    }

    @GetMapping
    public String viewTickets(Model model) {
        List<Ticket> tickets = ticketService.getCurrentUserTickets();
        model.addAttribute("tickets", tickets);
        return "tickets";
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            model.addAttribute("ticket", ticket);
            return "ticket-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ticket not found: " + e.getMessage());
            return "redirect:/tickets";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.cancelTicket(id);
            redirectAttributes.addFlashAttribute("success", "Ticket cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel ticket: " + e.getMessage());
        }
        return "redirect:/tickets";
    }
}
