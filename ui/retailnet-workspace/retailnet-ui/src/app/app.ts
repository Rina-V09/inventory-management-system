import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NotificationComponent } from './shared/components/notification';

@Component({
  standalone: true,
  imports: [RouterModule, NotificationComponent], 
  selector: 'app-root', 
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App {
  title = 'ui';
}