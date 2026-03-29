import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ClientService } from '../client.service';
import { CommonModule } from '@angular/common';

import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatInputModule, MatSelectModule, MatRadioModule, 
    MatCheckboxModule, MatButtonModule, MatDatepickerModule, MatNativeDateModule
  ],
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.css']
})
export class ClientFormComponent implements OnInit {
  clientForm!: FormGroup;
  isEditMode = false;
  clientId!: number;
  fileName = '';
  selectedFile: File | null = null;

  bloodGroups = ['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'];
  idTypes = ['SSN', 'Passport', 'Driver License'];
  riskTolerances = ['Low', 'Medium', 'High'];

  incomeRanges = ['Under $50k', '$50k - $100k', '$100k - $250k', 'Over $250k'];
  employmentStatuses = ['Employed', 'Self-Employed', 'Student'];
  netWorthOptions = ['Under $100k', '$100k - $500k', '$500k - $1M', 'Over $1M'];
  liquidityNeeds = ['High', 'Medium', 'Low'];
  marketDropReactions = ['Sell everything / Panic', 'Hold and wait', 'Buy more / Invest'];
  investmentExperiences = ['None / Beginner', 'Intermediate', 'Advanced / Professional'];


  constructor(
    private fb: FormBuilder,
    private clientService: ClientService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.clientId = +params['id'];
        this.loadClientData();
      }
    });
  }

  initForm() {
    this.clientForm = this.fb.group({
      name: ['', Validators.required],
      dob: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9\-]+$')]],
      email: ['', [Validators.required, Validators.email]],
      bloodGroup: ['', Validators.required],
      idType: ['', Validators.required],
      idNumber: ['', Validators.required],
      investmentAmount: ['', [Validators.required, Validators.pattern('^[0-9]+(\\.[0-9]{1,2})?$')]],
      investmentDuration: ['', [Validators.required, Validators.pattern('^[1-9][0-9]*$')]],
      riskTolerance: ['', Validators.required],
      policyPreference: [false],
      documentName: [''],

      gender: ['', Validators.required],
      address: [''],
      annualIncome: ['', Validators.required],
      employmentStatus: ['', Validators.required],
      approxNetWorth: [''],
      existingLoans: ['No'],

      goalGrowth: [false],
      goalRetirement: [false],
      goalTaxSaving: [false],

      assetStocks: [false],
      assetMutualFunds: [false],
      assetBonds: [false],

      liquidityNeed: [''],
      marketDropReaction: ['', Validators.required],
      investmentExperience: ['', Validators.required],
      kycCompleted: [false],
      politicallyExposed: ['No', Validators.required],

      acceptTerms: [false, Validators.requiredTrue]
    });
  }

  loadClientData() {
    this.clientService.getClientById(this.clientId).subscribe({
      next: (data) => {
        const dataStrGoals = data.investmentGoal || '';
        const dataStrAssets = data.preferredAssets || '';
        this.clientForm.patchValue({
          ...data,
          policyPreference: data.policyPreference === 'Yes',
          goalGrowth: dataStrGoals.includes('Growth'),
          goalRetirement: dataStrGoals.includes('Retirement'),
          goalTaxSaving: dataStrGoals.includes('Tax Saving'),
          assetStocks: dataStrAssets.includes('Stocks'),
          assetMutualFunds: dataStrAssets.includes('Mutual Funds'),
          assetBonds: dataStrAssets.includes('Bonds')
        });
        if(data.documentName) {
           this.fileName = data.documentName;
        }
      },
      error: (err) => alert('Error loading client data: ' + err.message)
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      if (!['image/jpeg', 'application/pdf'].includes(file.type)) {
         alert('Only PDF and JPG are allowed.');
         event.target.value = '';
         return;
      }
      if (file.size > 5 * 1024 * 1024) { // 5MB limit
         alert('File size exceeds 5MB limit.');
         event.target.value = '';
         return;
      }
      this.fileName = file.name;
      this.selectedFile = file;
      this.clientForm.patchValue({ documentName: file.name });
    }
  }

  submissionError = false;

  onSubmit() {
    if (this.clientForm.invalid) {
      this.clientForm.markAllAsTouched();
      this.submissionError = true;
      // Auto-dismiss after 5 seconds
      setTimeout(() => this.submissionError = false, 5000);
      // Scroll to first invalid field
      const firstInvalid = document.querySelector('.ng-invalid:not(form)');
      if (firstInvalid) firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }
    this.submissionError = false;
    
    const formValue = { ...this.clientForm.value };
    formValue.policyPreference = formValue.policyPreference ? 'Yes' : 'No';
    formValue.investmentAmount = parseFloat(formValue.investmentAmount);
    formValue.investmentDuration = parseInt(formValue.investmentDuration, 10);

    const goals = [];
    if (formValue.goalGrowth) goals.push('Growth');
    if (formValue.goalRetirement) goals.push('Retirement');
    if (formValue.goalTaxSaving) goals.push('Tax Saving');
    formValue.investmentGoal = goals.join(', ');

    const assets = [];
    if (formValue.assetStocks) assets.push('Stocks');
    if (formValue.assetMutualFunds) assets.push('Mutual Funds');
    if (formValue.assetBonds) assets.push('Bonds');
    formValue.preferredAssets = assets.join(', ');

    delete formValue.goalGrowth; delete formValue.goalRetirement; delete formValue.goalTaxSaving;
    delete formValue.assetStocks; delete formValue.assetMutualFunds; delete formValue.assetBonds;
    delete formValue.acceptTerms;

    if (this.isEditMode) {
      this.clientService.updateClient(this.clientId, formValue).subscribe((client: any) => {
        this.handleFileUpload(client.id || this.clientId);
      });
    } else {
      this.clientService.createClient(formValue).subscribe((client: any) => {
        this.handleFileUpload(client.id);
      });
    }
  }

  handleFileUpload(id: number) {
    if (this.selectedFile) {
      this.clientService.uploadDocument(id, this.selectedFile).subscribe({
        next: () => this.router.navigate(['/']),
        error: (err) => {
          alert('Client saved but document upload failed: ' + err.message);
          this.router.navigate(['/']);
        }
      });
    } else {
      this.router.navigate(['/']);
    }
  }
}
