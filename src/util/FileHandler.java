package util;

import java.io.*;
import java.util.*;

public class FileHandler {

    private static final String ACCOUNT_FILE = "accounts.txt";
    private static final String TRANSACTION_FILE = "transactions.txt";

    // Get account by account number
    public String[] getAccount(String accNo) {
        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(ACCOUNT_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(accNo)) {
                    return data;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update balance
    public void updateBalance(String accNo, double newBalance) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(ACCOUNT_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(accNo)) {
                    lines.add(
                            data[0] + "," +
                            data[1] + "," +
                            newBalance + "," +
                            data[3]
                    );
                } else {
                    lines.add(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(ACCOUNT_FILE))) {

            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save transaction
    public void saveTransaction(String accNo,
                                String type,
                                double amount) {
        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(
                                     TRANSACTION_FILE,
                                     true))) {

            String timestamp =
                    java.time.LocalDateTime.now()
                            .format(
                                    java.time.format.DateTimeFormatter
                                            .ofPattern(
                                                    "dd MMM yyyy | hh:mm a"
                                            )
                            );

            bw.write(
                    accNo + "," +
                    type + "," +
                    amount + "," +
                    timestamp
            );

            bw.newLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get all account lines
    public List<String> getAllLines() {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(ACCOUNT_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lines;
    }

    // Get transaction history
    public String getTransactions(String accNo) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(TRANSACTION_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(accNo)) {
                    String type = data[1];
                    String amount = data[2];
                    String time =
                            data.length >= 4
                                    ? data[3]
                                    : "Unknown Time";

                    sb.append("[")
                            .append(time)
                            .append("] ")
                            .append(type)
                            .append(" ₹")
                            .append(amount)
                            .append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // Update PIN
    public void updatePin(String accNo, String newPin) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(ACCOUNT_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equals(accNo)) {
                    lines.add(
                            data[0] + "," +
                            newPin + "," +
                            data[2] + "," +
                            data[3]
                    );
                } else {
                    lines.add(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(ACCOUNT_FILE))) {

            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Remove last withdraw entry (for rollback)
    public void removeLastWithdrawEntry(String accNo) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(TRANSACTION_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            for (int i = lines.size() - 1; i >= 0; i--) {

                String[] data = lines.get(i).split(",");

                if (data[0].equals(accNo)
                        && data[1].equals("WITHDRAW")) {
                    lines.remove(i);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(TRANSACTION_FILE))) {

            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ NEW: Remove last deposit entry (for rollback fix)
    public void removeLastDepositEntry(String accNo) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(
                             new FileReader(TRANSACTION_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            for (int i = lines.size() - 1; i >= 0; i--) {

                String[] data = lines.get(i).split(",");

                if (data[0].equals(accNo)
                        && data[1].equals("DEPOSIT")) {
                    lines.remove(i);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw =
                     new BufferedWriter(
                             new FileWriter(TRANSACTION_FILE))) {

            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}