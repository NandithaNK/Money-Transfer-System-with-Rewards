package com.fd.controller;

import com.fd.dto.RewardResponse;
import com.fd.model.Reward;
import com.fd.service.IRewardService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {
    @Autowired
    private IRewardService rewardService;

    //getting all rewards for a specific account ID 
    @GetMapping("/account/{accountId}")
    public List<RewardResponse> getRewardsByAccountID(@PathVariable Long accountId){
        List<Reward> rewards = rewardService.getRewardsByAccountId(accountId);
        return rewards.stream().map(RewardResponse::fromEntityToDTO).collect(Collectors.toList());
    }

    //getting total unredeemed points for an account
    @GetMapping("account/{accountId}/total-unredeemed")
    public ResponseEntity<Integer> getTotalUnredeemedPoints(@PathVariable Long accountId){
        Integer totalPoints = rewardService.getTotalUnredeemedPoints(accountId);
        return ResponseEntity.ok(totalPoints);
    }

    //to redeem all unredeemed points
    @PostMapping("/account/{accountId}/redeem")
    @Transactional
    public ResponseEntity<Map<String, Object>> redeemRewards(@PathVariable Long accountId){
        Double cashback = rewardService.redeemRewards(accountId);
        return ResponseEntity.ok(Map.of("cashbackAmount", cashback, "message", cashback > 0? "Rewards redeemed successfully!" : "No rewards to redeem"));
    }

}
