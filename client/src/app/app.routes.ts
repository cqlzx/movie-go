import { Routes, RouterModule } from '@angular/router';
import { SearchMainComponent } from './components/search-main/search-main.component';
import { MovieDetailComponent } from './components/movie-detail/movie-detail.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'search',
    pathMatch: 'full'
  },
  {
    path: 'search',
    component: SearchMainComponent
  },
  {
    path: 'movie/:id',
    component: MovieDetailComponent
  },
  {
    path: '**',
    redirectTo: 'search',
  },
];

export const ROUTING = RouterModule.forRoot(routes);
