import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { ClientService, Client } from '../client.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipListbox, MatChipOption } from '@angular/material/chips';

@Component({
  selector: 'app-client-details',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatListModule, MatDividerModule, DatePipe],
  template: `
    <div class="details-container" *ngIf="client">
      <h2>Client Profile: {{client.name}}</h2>
      
      <mat-card class="info-card">
        <mat-card-header>
           <mat-card-title>Overview</mat-card-title>
           <mat-card-subtitle>Calculated Risk:
             <strong [ngStyle]="{'color': client.riskCategory === 'Aggressive' ? 'red' : (client.riskCategory === 'Conservative' ? 'green' : '#ff9800')}">
               {{client.riskCategory}}
             </strong>
           </mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content style="display:flex; flex-wrap:wrap; gap: 40px; margin-top:20px;">
          
          <mat-list style="flex: 1; min-width: 250px;">
            <h3 matSubheader>Personal</h3>
            <mat-list-item><strong>DOB:</strong> {{client.dob | date:'dd-MM-yyyy'}}</mat-list-item>
            <mat-list-item><strong>Gender:</strong> {{client.gender}}</mat-list-item>
            <mat-list-item><strong>Phone:</strong> {{client.phone}}</mat-list-item>
            <mat-list-item><strong>Email:</strong> {{client.email}}</mat-list-item>
            <mat-list-item><strong>Address:</strong> {{client.address || 'N/A'}}</mat-list-item>
            <mat-list-item><strong>Parents:</strong> {{client.fatherName || 'N/A'}} / {{client.motherName || 'N/A'}}</mat-list-item>
            <mat-list-item><strong>Blood Group:</strong> {{client.bloodGroup}}</mat-list-item>
          </mat-list>

          <mat-list style="flex: 1; min-width: 250px;">
            <h3 matSubheader>Financial Details</h3>
            <mat-list-item><strong>Income:</strong> {{client.annualIncome}}</mat-list-item>
            <mat-list-item><strong>Employment:</strong> {{client.employmentStatus}}</mat-list-item>
            <mat-list-item><strong>Net Worth:</strong> {{client.approxNetWorth || 'N/A'}}</mat-list-item>
            <mat-list-item><strong>Existing Loans:</strong> {{client.existingLoans}}</mat-list-item>
            <mat-list-item><strong>Planned Investment:</strong> $\{{client.investmentAmount}}</mat-list-item>
            <mat-list-item><strong>Time Period:</strong> {{client.investmentDuration}} Months</mat-list-item>
          </mat-list>

          <mat-list style="flex: 1; min-width: 250px;">
            <h3 matSubheader>Investment Preferences</h3>
            <mat-list-item><strong>Goals:</strong> {{client.investmentGoal || 'N/A'}}</mat-list-item>
            <mat-list-item><strong>Assets:</strong> {{client.preferredAssets || 'N/A'}}</mat-list-item>
            <mat-list-item><strong>Liquidity Need:</strong> {{client.liquidityNeed || 'N/A'}}</mat-list-item>
            <mat-list-item><strong>Experience:</strong> {{client.investmentExperience}}</mat-list-item>
            <mat-list-item><strong>Drop Reaction:</strong> {{client.marketDropReaction}}</mat-list-item>
            <mat-list-item><strong>Tolerance:</strong> {{client.riskTolerance}}</mat-list-item>
          </mat-list>

          <mat-list style="flex: 1; min-width: 250px;">
            <h3 matSubheader>Identity & Compliance</h3>
            <mat-list-item><strong>Type & No:</strong> {{client.idType}} - {{client.idNumber}}</mat-list-item>
            <mat-list-item><strong>KYC Offline:</strong> {{client.kycCompleted ? 'Yes' : 'No'}}</mat-list-item>
            <mat-list-item><strong>Politically Exposed:</strong> {{client.politicallyExposed}}</mat-list-item>
            <mat-list-item><strong>Policy Opt-In:</strong> {{client.policyPreference}}</mat-list-item>
            <mat-list-item><strong>Document:</strong> {{client.documentName || 'None'}}
               <button *ngIf="client.documentName" mat-button color="accent" (click)="downloadDoc(client)">Download</button>
            </mat-list-item>
          </mat-list>
        </mat-card-content>

        <mat-card-actions>
           <button mat-button routerLink="/">Back to Dashboard</button>
           <button mat-raised-button color="primary" [routerLink]="['/edit', client.id]">Edit Record</button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [`
    .details-container { margin: 20px auto; max-width: 900px; }
    .info-card { padding: 20px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
  `]
})
export class ClientDetailsComponent implements OnInit {
  client?: Client;

  constructor(private route: ActivatedRoute, private clientService: ClientService) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
       if (params['id']) {
          this.clientService.getClientById(+params['id']).subscribe(c => this.client = c);
       }
    });
  }

  downloadDoc(client: Client) {
    if (!client.id || !client.documentName) return;
    this.clientService.downloadDocument(client.id).subscribe((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = client.documentName || 'document';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
