package com.example.bookingticket.repository;

import com.example.bookingticket.model.Seat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

public class SeatRepository {
    private final Map<Long, List<Seat>> movieSeats = new ConcurrentHashMap<>();
    private final int seatsPerMovie;

    public SeatRepository(int seatsPerMovie) {
        this.seatsPerMovie = seatsPerMovie;
    }

    public List<Seat> getSeats(Long movieId) {
        return movieSeats.computeIfAbsent(movieId, k -> {
            List<Seat> seats = new ArrayList<>();
            for (int i = 1; i <= seatsPerMovie; i++) {
                seats.add(new Seat(i));
            }
            return seats;
        });
    }

    public Seat getSeat(Long movieId, int seatNumber) {
        List<Seat> seats = getSeats(movieId);
        return seats.stream()
                .filter(seat -> seat.getSeatNumber() == seatNumber)
                .findFirst()
                .orElse(null);
    }
}
