package com.bit.joe.shoppingmall.service.schedule;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.repository.UserRepository;

@Service
public class ScheduleWithdrawService {

    UserRepository userRepository;

    @Scheduled(cron = "0 0 0 1 * *") // every month's 1st day at midnight
    public void withdraw() {
        System.out.println("Withdraw developping ...");

        List<User> users = userRepository.findByActive(false);
        // get all users whoes active is false

        // if there is no user whoes active is false, return
        if (users.isEmpty()) {
            return;
        }

        // we do not need to delete all users whoes active is false
        // userRepository.deleteAll(users);
        // delete all users whoes active is false
    }
}
