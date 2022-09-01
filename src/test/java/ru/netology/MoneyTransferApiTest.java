package ru.netology;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataBaseManager;
import ru.netology.data.DataHelper;
import ru.netology.data.RequestApi;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class MoneyTransferApiTest {

    private static String newToken;
    String idFirstCard = DataHelper.getFirstCard().getId();
    String idSecondCard = DataHelper.getSecondCard().getId();

    @BeforeAll
    public static void authorization() {
        RequestApi.authorization();
        String token = RequestApi.getToken();
        newToken = token;
    }
//    @AfterAll
//    static void clearDataBase() {
//        DataBaseManager.clearBase();
//    }

    @Test
    void shouldTransfer() {
        DataHelper.CardResponseInfo[] cardsBefore = RequestApi.checkCards(newToken);

        int firstCardBalanceBefore = DataHelper.getCardBalance(cardsBefore, idFirstCard);
        int secondCardBalanceBefore = DataHelper.getCardBalance(cardsBefore, idSecondCard);
        String cardFrom = DataHelper.getFirstCard().getNumber();
        String cardTo = DataHelper.getAnotherCard().getNumber();
        int amount = 5000;

        RequestApi.moneyTransfer(newToken, cardFrom, cardTo, amount, 200);

        DataHelper.CardResponseInfo[] cardsAfter = RequestApi.checkCards(newToken);

        List<Integer> actualCardsBalance = Arrays.asList(DataHelper.getCardBalance(cardsAfter, idFirstCard),
                DataHelper.getCardBalance(cardsAfter, idSecondCard));
        List<Integer> expectedCardsBalance = Arrays.asList(firstCardBalanceBefore - amount,
                secondCardBalanceBefore);

        assertIterableEquals(expectedCardsBalance, actualCardsBalance);
    }

    @Test
    void shouldBadRequestWithNoMoneyForTransfer() {
        DataHelper.CardResponseInfo[] cardsBefore = RequestApi.checkCards(newToken);

        int firstCardBalanceBefore = DataHelper.getCardBalance(cardsBefore, idFirstCard);
        int secondCardBalanceBefore = DataHelper.getCardBalance(cardsBefore, idSecondCard);

        String cardFrom = DataHelper.getFirstCard().getNumber();
        String cardTo = DataHelper.getAnotherCard().getNumber();
        int amount = firstCardBalanceBefore + 5000;

        RequestApi.moneyTransfer(newToken, cardFrom, cardTo, amount, 400);

        DataHelper.CardResponseInfo[] cardsAfter = RequestApi.checkCards(newToken);

        List<Integer> actualCardsBalance = Arrays.asList(DataHelper.getCardBalance(cardsAfter, idFirstCard),
                DataHelper.getCardBalance(cardsAfter, idSecondCard));
        List<Integer> expectedCardsBalance = Arrays.asList(firstCardBalanceBefore,
                secondCardBalanceBefore);

        assertIterableEquals(expectedCardsBalance, actualCardsBalance);
    }
}
