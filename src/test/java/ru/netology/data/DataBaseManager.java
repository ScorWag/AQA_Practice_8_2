package ru.netology.data;

import lombok.SneakyThrows;

import java.sql.DriverManager;

public class DataBaseManager {

    private DataBaseManager() {
    }

    @SneakyThrows
    public static void clearBase() {
        var clearAuthCodes = "DELETE FROM auth_codes;";
        var clearCards = "DELETE FROM cards;";
        var clearCardTransactions = "DELETE FROM card_transactions;";
        var clearUsers = "DELETE FROM users;";
        try (
                var conn = DriverManager
                        .getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
                var clearAuthCodesStmt = conn.prepareStatement(clearAuthCodes);
                var clearCardsStmt = conn.prepareStatement(clearCards);
                var clearCardTransactionsStmt = conn.prepareStatement(clearCardTransactions);
                var clearUsersStmt = conn.prepareStatement(clearUsers)
        ) {
            clearAuthCodesStmt.executeUpdate();
            clearCardsStmt.executeUpdate();
            clearCardTransactionsStmt.executeUpdate();
            clearUsersStmt.executeUpdate();
        }
    }

    @SneakyThrows
    public static String getVerificationCode() {
        String verificationCode = null;
        var verificationCodeSQL = "SELECT code FROM auth_codes ORDER BY created DESC LIMIT 1;";
        try (
                var conn = DriverManager
                        .getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
                var getVerCode = conn.prepareStatement(verificationCodeSQL)
        ) {
            try (var verCode = getVerCode.executeQuery()) {
                if (verCode.next()) {
                    verificationCode = verCode.getString("code");
                }
            }
        }
        return verificationCode;
    }
}
