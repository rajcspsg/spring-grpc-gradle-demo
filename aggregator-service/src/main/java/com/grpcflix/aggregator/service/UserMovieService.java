package com.grpcflix.aggregator.service;

import com.grpcflix.aggregator.dto.RecommendedMovie;
import com.grpcflix.aggregator.dto.UserGenre;
import com.raj.grpcflix.common.Genre;
import com.raj.grpcflix.movie.MovieSearchRequest;
import com.raj.grpcflix.movie.MovieSearchResponse;
import com.raj.grpcflix.movie.MovieServiceGrpc;
import com.raj.grpcflix.user.UserGenreUpdateRequest;
import com.raj.grpcflix.user.UserResponse;
import com.raj.grpcflix.user.UserSearchRequest;
import com.raj.grpcflix.user.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMovieService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @GrpcClient("movie-service")
    private MovieServiceGrpc.MovieServiceBlockingStub movieStub;

    public List<RecommendedMovie> getUserMovieSuggestions(String loginId) {
        UserSearchRequest.Builder userSearchRequest = UserSearchRequest.newBuilder().setLoginId(loginId);
        UserResponse userResponse = this.userStub.getUserGenre(userSearchRequest.build());
        MovieSearchRequest msr = MovieSearchRequest.newBuilder().setGenre(userResponse.getGenre()).build();
        MovieSearchResponse movieSearchResponse = this.movieStub.getMovies(msr);
        return movieSearchResponse.getMovieList().stream()
                .map(movieDto -> new RecommendedMovie(movieDto.getTitle(), movieDto.getYear(), movieDto.getRating()))
                .collect(Collectors.toList());
    }

    public void setUserGenre(UserGenre userGenre) {
        UserGenreUpdateRequest request = UserGenreUpdateRequest.newBuilder()
                .setLoginId(userGenre.getLoginId())
                .setGenre(Genre.valueOf(userGenre.getGenre().toUpperCase())).build();
        UserResponse userResponse = this.userStub.updateUserGenre(request);
    }
}
