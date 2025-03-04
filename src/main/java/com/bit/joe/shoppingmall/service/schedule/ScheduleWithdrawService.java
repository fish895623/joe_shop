package com.bit.joe.shoppingmall.service.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.repository.UserRepository;

@Service
public class ScheduleWithdrawService {

    UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 * *") // every month's 1st day at midnight
    public void withdraw() throws Exception {
        throw new Exception("Withdraw developping ...");
    }
}
