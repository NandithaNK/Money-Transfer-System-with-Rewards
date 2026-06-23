import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define the RewardResponse interface (matches the backend DTO!)
export interface RewardResponse {
  rewardId: number;
  transactionId: number;
  accountId: number;
  pointsEarned: number;
  createdAt: string; // LocalDateTime comes as a string from JSON
  redeemed: boolean;
}

export interface RedemptionResponse{
  cashbackAmount: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class RewardService {
  private baseUrl = 'http://localhost:9090/api/v1/rewards';

  constructor(private http: HttpClient) { }

  // Get all rewards for a specific account ID
  getRewardsByAccountId(accountId: number): Observable<RewardResponse[]> {
    const token = sessionStorage.getItem('jwt');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<RewardResponse[]>(`${this.baseUrl}/account/${accountId}`, { headers });
  }

  //to get total unredeemed points
  getTotalUnredeemedPoints(accountId: number): Observable<number>{
    const token = sessionStorage.getItem('jwt');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.get<number>(`${this.baseUrl}/account/${accountId}/total-unredeemed`, {headers});
  }

  //to redeem rewards:
  redeemRewards(accountId: number): Observable<RedemptionResponse> {
    const token = sessionStorage.getItem('jwt');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post<RedemptionResponse>(`${this.baseUrl}/account/${accountId}/redeem`, {}, { headers });
  }

}