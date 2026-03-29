import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatInputModule, MatButtonModule, MatCardModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  form: FormGroup;
  errorMsg = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    
    // Trim all form values to remove whitespace
    const trimmedData = {
      name: this.form.get('name')?.value?.trim() || '',
      email: this.form.get('email')?.value?.trim() || '',
      password: this.form.get('password')?.value?.trim() || ''
    };
    
    // Validate trimmed values
    if (!trimmedData.name || !trimmedData.email || !trimmedData.password) {
      this.errorMsg = 'All fields must have valid content (no empty/whitespace).';
      return;
    }
    
    console.log('Sending signup data:', trimmedData); // Debug log
    
    this.authService.signup(trimmedData).subscribe({
      next: () => this.router.navigate(['/']),
      error: (err) => {
        console.error('Signup error:', err); // Debug log
        this.errorMsg = err.error?.message || 'Signup failed';
      }
    });
  }
}
