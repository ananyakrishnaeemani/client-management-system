import { Component } from '@angular/core';
import { RouterOutlet, RouterModule, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, MatToolbarModule, MatButtonModule, CommonModule],
  template: `
    <mat-toolbar class="glass-header">
      <div class="header-container">
        <div class="logo" routerLink="/">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: #4f46e5;">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
          </svg>
          <span class="brand-text">WealthPortal</span>
        </div>
        <span class="spacer"></span>
        <ng-container *ngIf="authService.isLoggedIn()">
          <span class="welcome-text">Hi, {{ authService.getAgentName() }}</span>
          <button mat-button class="nav-link" routerLink="/">Dashboard</button>
          <button mat-button class="nav-link nav-highlight" routerLink="/add">+ New Client</button>
          <button mat-button class="nav-link" (click)="authService.logout()">Logout</button>
        </ng-container>
        <ng-container *ngIf="!authService.isLoggedIn()">
          <button mat-button class="nav-link" routerLink="/login">Sign In</button>
          <button mat-raised-button color="primary" routerLink="/signup" style="margin-left: 12px; border-radius: 8px;">Sign Up</button>
        </ng-container>
      </div>
    </mat-toolbar>
    <div class="container animate-fade-in">
      <router-outlet></router-outlet>
    </div>
  `,
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend-portal';
  constructor(public authService: AuthService) {}
}
