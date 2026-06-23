import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RewardService, RewardResponse, RedemptionResponse } from '../reward.service';

@Component({
    selector: 'app-rewards',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './rewards.component.html',
    styleUrls: ['./rewards.component.css']
})

export class RewardsComponent implements OnInit{
    rewards: RewardResponse[] = [];
    totalPoints: number = 0;
    totalUnredeemedPoints: number = 0;
    message: string = ' ';
    userName: string = ' ';

    constructor(private rewardService: RewardService){ }

    ngOnInit(): void{

        //to get the logged in user's account Id from sessionStorage
        const accountIdStr = sessionStorage.getItem('accountId');
        const userNameStr = sessionStorage.getItem('userName');
        if (userNameStr) {
            this.userName = userNameStr;
        }
        if (accountIdStr) {
            const accountId = parseInt(accountIdStr, 10);
            this.loadRewards(accountId);
            this.loadTotalUnredeemedPoints(accountId);
        }


    }

    loadRewards(accountId: number): void {
        this.rewardService.getRewardsByAccountId(accountId).subscribe({
            next: (data) => {
                this.rewards = data;
                this.totalPoints = this.rewards.reduce((sum, reward) => sum+reward.pointsEarned, 0);
            },
            error: (err) => {
                console.error('Error loading rewards:', err);
            }
        });
    }

    loadTotalUnredeemedPoints(accountId: number): void{
        this.rewardService.getTotalUnredeemedPoints(accountId).subscribe({
            next: (data) => {
                this.totalUnredeemedPoints = data;
            },
            error: (err) => {
                console.error('Error loading total unredeemed points:', err);
            }
        });
    }

    //Redeem rewards button click handler
    redeemRewards(): void {
        const accountIdStr = sessionStorage.getItem('accountId');
        if (accountIdStr) {
            const accountId = parseInt(accountIdStr, 10);
            this.rewardService.redeemRewards(accountId).subscribe({
                next: (response: RedemptionResponse) => {
                    this.message = response.message;

                    //reloading to show the updated reward points
                    this.loadRewards(accountId);

                    //clearing message after 5 seconds
                    this.loadTotalUnredeemedPoints(accountId);
                    setTimeout(() => this.message = '', 5000);
                },
                error: (err) => {
                    console.error('Error redeeming rewards: ', err);
                    this.message = "Error redeeming rewards";
                    setTimeout(() => this.message = '', 5000);
                }
            });
        }
    }
}
