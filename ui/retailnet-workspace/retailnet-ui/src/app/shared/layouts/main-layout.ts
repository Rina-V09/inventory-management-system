import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Sidebar } from '../components/sidebar';
import { TopNavbar } from '../components/top-navbar';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, Sidebar, TopNavbar],
  templateUrl: './main-layout.html',
  styleUrls: ['./main-layout.css'],
})
export class MainLayoutComponent implements OnInit {
  userRole = 'User';
  themeClass = 'theme-inventory-manager';
  isSidebarOpen = false;

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  ngOnInit() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const roles = payload.realm_access?.roles || [];
        if (roles.includes('INVENTORY_MANAGER')) {
          this.userRole = 'Inventory Manager';
          this.themeClass = 'theme-inventory-manager';
        } else if (roles.includes('PROCUREMENT')) {
          this.userRole = 'Procurement';
          this.themeClass = 'theme-procurement-team';
        } else if (roles.includes('SALES')) {
          this.userRole = 'Sales';
          this.themeClass = 'theme-sales-team';
        }
      } catch (e) {
        console.error('Error parsing token', e);
      }
    }
  }
}