package ru.netology.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.Value;

import java.sql.DriverManager;
import java.util.Locale;


public class DataHelper {

    private static final Faker faker = new Faker(new Locale("en"));

    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationInfo {
        String login;
        String code;
    }

    @Value
    public static class TransferInfo {
        String from;
        String to;
        int amount;
    }

    @Value
    public static class CardInfo {
        String number;
        String id;
    }

    @Data
    public static class CardResponseInfo {
        String id;
        String number;
        int balance;
    }

    @Data
    public static class TokenInfo {
        String token;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static VerificationInfo getVerificationInfo() {
        String verificationCode = DataBaseManager.getVerificationCode();

        return new VerificationInfo(getAuthInfo().login, verificationCode);
    }

    public static CardInfo getFirstCard() {
        return new CardInfo("5559 0000 0000 0001", "92df3f1c-a033-48e6-8390-206f6b1f56c0");
    }

    public static CardInfo getSecondCard() {
        return new CardInfo("5559 0000 0000 0002", "0f3f5c2a-249e-4c3d-8287-09f7a039391d");
    }

    public static CardInfo getAnotherCard() {
        return new CardInfo("5559 0000 0000 0008", faker.idNumber().valid());
//        return new CardInfo(faker.business().creditCardNumber().replaceAll("-", " "), faker.idNumber()
//                .valid());
    }

    public static TransferInfo getTransferInfo(String from, String to, int amount) {
        return new TransferInfo(from, to, amount);
    }

    public static int getCardBalance(CardResponseInfo[] cards, String cardId) {
        int cardBalance = 0;
        for (CardResponseInfo card : cards) {
            if (card.getId().equals(cardId)) {
                cardBalance = card.getBalance();
                break;
            }
        }
        return cardBalance;
    }
}
