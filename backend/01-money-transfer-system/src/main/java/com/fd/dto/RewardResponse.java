package com.fd.dto;

import com.fd.model.Reward;
import java.time.LocalDateTime;

public class RewardResponse {
    
    private Long rewardId;
    private Long transactionId;
    private Long accountId;
    private Integer pointsEarned;
    private LocalDateTime createdAt;
    private Boolean redeemed;

    public RewardResponse(Long rewardId, Long transactionId, Long accountId, Integer pointsEarned, LocalDateTime createdAt, Boolean redeemed){
        this.rewardId = rewardId;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.pointsEarned = pointsEarned;
        this.createdAt = createdAt;
        this.redeemed = redeemed;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public Long getTransactionId(){
        return transactionId;
    }

    public Long getAccountId(){
        return accountId;
    }

    public Integer getPointsEarned(){
        return pointsEarned;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public Boolean getRedeemed(){
        return redeemed;
    }



    public static RewardResponse fromEntityToDTO(Reward reward){
        if (reward == null){
            return null;
        }

        return new RewardResponse( reward.getRewardId(),
                    reward.getTransactionId(), reward.getAccountId(), 
                    reward.getPointsEarned(), reward.getCreatedAt(), reward.getRedeemed());
    }
}
