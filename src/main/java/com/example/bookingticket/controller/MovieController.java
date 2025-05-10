package com.example.bookingticket.controller;

import com.example.bookingticket.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/movie")
    public String movieSelection(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "movie";
    }
} 