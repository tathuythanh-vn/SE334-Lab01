package com.example.bookingticket.controller;

import com.example.bookingticket.model.CartItem;
import com.example.bookingticket.model.Ticket;
import com.example.bookingticket.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String viewCart(Model model) {
        try {
            List<CartItem> cartItems = cartService.getCurrentUserCart();
            model.addAttribute("cartItems", cartItems);
            return "cart";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading cart: " + e.getMessage());
            model.addAttribute("cartItems", List.of());
            return "cart";
        }
    }

    @PostMapping("/add")
    public String addToCart(@ModelAttribute CartItem cartItem, RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(cartItem);
            redirectAttributes.addFlashAttribute("success", "Item added to cart successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add item to cart: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeFromCart(id);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove item from cart: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        try {
            List<Ticket> tickets = cartService.checkout();
            redirectAttributes.addFlashAttribute("success", "Successfully booked " + tickets.size() + " tickets");
            return "redirect:/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
            return "redirect:/cart";
        }
    }
} 