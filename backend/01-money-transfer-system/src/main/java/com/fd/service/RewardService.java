package com.fd.service;

import com.fd.model.Reward;
import com.fd.model.TransactionLog;
import com.fd.model.TransactionStatus;
import com.fd.repository.IRewardRepository;

import jakarta.transaction.Transactional;

import com.fd.model.Account;
import com.fd.repository.IAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RewardService implements IRewardService {
	
	@Autowired
	private IRewardRepository rewardRepository;

    @Autowired
    private IAccountRepository accountRepository;
	
    @Override
    public Reward calculateAndSaveReward(TransactionLog successfulTransaction, Long senderAccountId){

        if (!isEligibleForReward(successfulTransaction, senderAccountId)){
            return null;
        }

        int pointsEarned = (int) Math.floor(successfulTransaction.getAmount() / 100.0);

        Reward reward = new Reward(successfulTransaction.getTransactionId(), senderAccountId, pointsEarned);
        return rewardRepository.save(reward);
    }

    @Override
    public List<Reward> getRewardsByAccountId(Long accountId){
        return rewardRepository.findByAccountId(accountId);
    }

    private boolean isEligibleForReward(TransactionLog transaction, Long senderAccountId){
        if (transaction.getStatus() != TransactionStatus.SUCCESS){
            return false;
        }

        if (transaction.getAmount() <= 100.0){
            return false;
        }

        if (transaction.getFromAccountId() == transaction.getToAccountId()) {
            return false;
        }
        
        if (rewardRepository.existsByTransactionId(transaction.getTransactionId())){
            return false;
        }

        return true;
    }

    @Override
    @Transactional     //for atomicity
    public Double redeemRewards(Long accountId){
        //getting all unredeemed points for the account
        List<Reward> unredeemedRewards = rewardRepository.findByAccountIdAndRedeemedFalse(accountId);

        if (unredeemedRewards.isEmpty()){
            return 0.0;
        }

        //calculating total points and cashback as 1 reward point = 1 rupee
        int totalPoints = unredeemedRewards.stream().mapToInt(Reward::getPointsEarned).sum();
        double cashbackAmount = totalPoints;    //1:1 ratio

        //finding the account and adding the cashback
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not Found"));
        account.setBalance(account.getBalance() + cashbackAmount);
        account.setLastUpdated(java.time.LocalDateTime.now());
        accountRepository.save(account);

        //marking all rewards as redeemed
        unredeemedRewards.forEach(reward -> reward.setRedeemed(true));
        rewardRepository.saveAll(unredeemedRewards);
        
        //returning the cashback amount
        return cashbackAmount;
    }

    //implements total unredeemed points
    @Override
    public Integer getTotalUnredeemedPoints(Long accountId){
        return rewardRepository.getTotalUnredeemedPoints(accountId);
    }

}