import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css'],
})
export class Sidebar implements OnInit {
  isInventoryManager = false;
  isProcurement = false;
  isSales = false;
  isITSupport = false;

  /** Router for navigation and logout redirection */
  private readonly router = inject(Router);

  ngOnInit() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const roles = payload.realm_access?.roles || [];
        this.isInventoryManager = roles.includes('INVENTORY_MANAGER');
        this.isProcurement = roles.includes('PROCUREMENT');
        this.isSales = roles.includes('SALES');
        this.isITSupport = roles.includes('ADMIN') || roles.includes('IT_SUPPORT');
      } catch (e) {
        console.error('Error parsing token in sidebar', e);
      }
    }
    
    // Fallback logic for ease of development when roles might be missing
    if (!this.isInventoryManager && !this.isProcurement && !this.isSales) {
      this.isInventoryManager = true; 
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
