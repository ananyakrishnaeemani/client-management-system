import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Client {
  id?: number;
  name: string;
  dob: string;
  phone: string;
  email: string;
  fatherName?: string;
  motherName?: string;
  bloodGroup: string;
  idType: string;
  idNumber: string;
  investmentAmount: number;
  investmentDuration: number;
  riskTolerance: string;
  policyPreference?: string;
  riskCategory?: string;
  documentName?: string;
  gender: string;
  address?: string;
  annualIncome: string;
  employmentStatus: string;
  approxNetWorth?: string;
  existingLoans?: string;
  investmentGoal?: string;
  preferredAssets?: string;
  liquidityNeed?: string;
  marketDropReaction: string;
  investmentExperience: string;
  kycCompleted: boolean;
  politicallyExposed: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = 'http://localhost:8080/api/clients';

  constructor(private http: HttpClient) { }

  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getClientById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, client);
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  uploadDocument(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${id}/document`, formData);
  }

  downloadDocument(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/document`, { responseType: 'blob' });
  }
}
