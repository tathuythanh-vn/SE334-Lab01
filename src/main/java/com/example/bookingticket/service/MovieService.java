package com.example.bookingticket.service;

import com.example.bookingticket.model.Movie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MovieService {
    private final List<Movie> movies = Arrays.asList(
        new Movie(1L, "Avengers: Endgame", "Action", 19.99, "/image/movies/avengers.jpg", 
            "After the devastating events of Infinity War, the Avengers must assemble once more to undo Thanos' actions and restore balance to the universe.", 181),
        new Movie(2L, "The Lion King", "Animation", 15.99, "/image/movies/lionking.jpg",
            "A young lion prince must overcome tragedy and take his rightful place as king of the Pride Lands.", 118),
        new Movie(3L, "Joker", "Drama", 17.99, "/image/movies/joker.jpg",
            "In Gotham City, mentally troubled comedian Arthur Fleck is disregarded and mistreated by society. He then embarks on a downward spiral of revolution and bloody crime.", 122),
        new Movie(4L, "Spider-Man: Far From Home", "Action", 18.99, "/image/movies/spiderman.jpg",
            "Following the events of Avengers: Endgame, Spider-Man must step up to take on new threats in a world that has changed forever.", 129),
        new Movie(5L, "The Nun", "Horror", 14.99, "/image/movies/thenun.jpg",
            "A priest with a haunted past and a novice on the threshold of her final vows are sent by the Vatican to investigate the death of a young nun in Romania.", 96),
        new Movie(6L, "Vòng Tay Nắng", "Drama", 14.99, "/image/movies/vongtaynang.jpg",
            "A touching story about family bonds and the journey of self-discovery in modern Vietnam.", 120),
        new Movie(7L, "Mission Impossible: The Final Reckoning", "Action", 14.99, "/image/movies/thefinalreckoning.jpg",
            "Ethan Hunt and his IMF team must track down a terrifying new weapon that threatens all of humanity before it falls into the wrong hands.", 163),
        new Movie(8L, "Toy Story 4", "Animation", 14.99, "/image/movies/toystory.jpg",
            "When a new toy called Forky joins Woody and the gang, a road trip alongside old and new friends reveals how big the world can be for a toy.", 100)
    );

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Movie getMovieById(Long id) {
        return movies.stream()
                .filter(movie -> movie.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
} 