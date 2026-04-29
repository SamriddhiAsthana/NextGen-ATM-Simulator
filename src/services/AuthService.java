package services;

import model.Account;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME = 120000;

    private Map<String, Integer> attempts = new HashMap<>();
    private Map<String, Long> lockTime = new HashMap<>();

    public Account login(String accNo, String pin) {

        if (lockTime.containsKey(accNo)) {
            long lockedAt = lockTime.get(accNo);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lockedAt < LOCK_TIME) {
                long secondsLeft = (LOCK_TIME - (currentTime - lockedAt)) / 1000;
                throw new RuntimeException("Account locked. Try again in " + secondsLeft + " sec.");
            } else {
                lockTime.remove(accNo);
                attempts.put(accNo, 0);
            }
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(System.getProperty("user.dir") + "/accounts.txt"))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(accNo)) {

                    if (data[1].equals(pin)) {
                        attempts.put(accNo, 0);

                        return new Account(
                                data[0],
                                data[1],
                                Double.parseDouble(data[2]),
                                data[3]   // ✅ name added here
                        );
                    }

                    int count = attempts.getOrDefault(accNo, 0) + 1;
                    attempts.put(accNo, count);

                    if (count >= MAX_ATTEMPTS) {
                        lockTime.put(accNo, System.currentTimeMillis());
                        throw new RuntimeException("Too many attempts. Account locked for 2 minutes.");
                    } else {
                        int left = MAX_ATTEMPTS - count;
                        throw new RuntimeException("Wrong PIN. " + left + " attempt(s) left.");
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading account data.");
        }

        throw new RuntimeException("Account not found.");
    }
}