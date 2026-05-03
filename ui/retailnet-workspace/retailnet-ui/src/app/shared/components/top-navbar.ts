import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-top-navbar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header class="top-navbar">
      <div class="left-section">
        <button class="menu-toggle" (click)="toggleMenu()">
          <span class="material-icons">menu</span>
        </button>
        <div class="search-bar" style="visibility: hidden; pointer-events: none;">
          <span class="material-icons search-icon">search</span>
          <input type="text" placeholder="Search..." />
        </div>
      </div>
      <div class="user-profile">
        <span class="role-badge">{{ role }}</span>
        <div class="avatar">
          <span class="material-icons">person</span>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .top-navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 24px;
      height: 60px;
      background-color: #ffffff;
      border-bottom: 1px solid #e0e0e0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.02);
    }
    .left-section {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    .menu-toggle {
      display: none;
      background: none;
      border: none;
      cursor: pointer;
      color: #1e293b;
      padding: 8px;
      border-radius: 4px;
    }
    .menu-toggle:hover {
      background-color: #f1f3f5;
    }
    .search-bar {
      display: flex;
      align-items: center;
      background-color: #f1f3f5;
      border-radius: 6px;
      padding: 6px 12px;
      width: 300px;
    }
    .search-icon {
      color: #adb5bd;
      margin-right: 8px;
    }
    .search-bar input {
      border: none;
      background: none;
      outline: none;
      width: 100%;
      font-size: 14px;
      color: #495057;
    }
    .user-profile {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    .role-badge {
      font-size: 12px;
      font-weight: 600;
      background-color: #e3f2fd;
      color: #1976d2;
      padding: 4px 8px;
      border-radius: 4px;
      text-transform: capitalize;
    }
    .avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background-color: #dee2e6;
      display: flex;
      justify-content: center;
      align-items: center;
      color: #495057;
      cursor: pointer;
    }
    @media (max-width: 992px) {
      .menu-toggle {
        display: block;
      }
      .search-bar {
        display: none;
      }
    }
  `]
})
export class TopNavbar {
  @Input() role = 'User';
  @Output() menuToggle = new EventEmitter<void>();

  toggleMenu() {
    this.menuToggle.emit();
  }
}
