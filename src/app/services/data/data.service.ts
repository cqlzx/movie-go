import { Injectable } from '@angular/core';
import { Movie } from '../../models/movie.model';
import { MOVIES } from '../../mock.movies';

@Injectable()
export class DataService {
  movies: Movie[] = MOVIES;
  constructor() { }

  getMovies(): Movie[] {
    return this.movies;
  }
  getMovie(id: number): Movie {
    return this.movies.find((movie) => (movie.id === id));
  }
}
