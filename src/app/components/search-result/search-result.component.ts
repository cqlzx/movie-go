import { Component, OnInit } from '@angular/core';
import { Movie } from '../../models/movie.model';
import { DataService } from '../../services/data/data.service';

@Component({
  selector: 'app-search-result',
  templateUrl: './search-result.component.html',
  styleUrls: ['./search-result.component.css']
})
export class SearchResultComponent implements OnInit {
  movies: Movie[];
  constructor(
    private dataService: DataService
  ) { }

  ngOnInit() {
    this.movies = this.dataService.getMovies();
  }
}
