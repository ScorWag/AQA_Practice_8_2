package ru.netology;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.ClearDataBase;
import ru.netology.data.DataHelper;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferApiTest {

    private static final RequestSpecification requestSpec = DataHelper.getReqSpec().getRequestSpecification();
    private static String newToken;

    public DataHelper.CardResponseInfo[] checkCards(String token) {
        DataHelper.CardResponseInfo[] cards =
                given()
                        .spec(requestSpec)
                        .auth().oauth2(token)
                        .when()
                        .get("api/cards")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .as(DataHelper.CardResponseInfo[].class);
        return cards;
    }

    @BeforeAll
    public static void authorization() {
        given()
                .spec(requestSpec)
                .body(DataHelper.getAuthInfo())
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);

        DataHelper.TokenInfo getToken = given()
                .spec(requestSpec)
                .body(DataHelper.getVerificationInfo())
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(DataHelper.TokenInfo.class);

        newToken = getToken.getToken();
    }

    @AfterAll
    static void clearDataBase() {
        ClearDataBase.clearBase();
    }

    @Test
    void shouldTransfer() {
        String id = DataHelper.getFirstCard().getId();

        DataHelper.CardResponseInfo[] cardsBefore = checkCards(newToken);
        int balanceBefore = DataHelper.getCardBalance(cardsBefore, id);

        String cardFrom = DataHelper.getFirstCard().getNumber();
        String cardTo = DataHelper.getAnotherCard().getNumber();
        int amount = 5000;

        given()
                .spec(requestSpec)
                .auth().oauth2(newToken)
                .body(DataHelper.getTransferInfo(cardFrom, cardTo, amount))
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);

        DataHelper.CardResponseInfo[] cardsAfter = checkCards(newToken);
        int balanceAfter = DataHelper.getCardBalance(cardsAfter, id);

        int balanceExpected = balanceBefore - amount;

        assertEquals(balanceExpected, balanceAfter);
    }

    @Test
    void shouldBadRequestWithNoMoneyForTransfer() {
        String id = DataHelper.getFirstCard().getId();

        DataHelper.CardResponseInfo[] cardsBefore = checkCards(newToken);
        int balanceBefore = DataHelper.getCardBalance(cardsBefore, id);

        String cardFrom = DataHelper.getFirstCard().getNumber();
        String cardTo = DataHelper.getAnotherCard().getNumber();
        int amount = balanceBefore + 5000;

        given()
                .spec(requestSpec)
                .auth().oauth2(newToken)
                .body(DataHelper.getTransferInfo(cardFrom, cardTo, amount))
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(400);

        DataHelper.CardResponseInfo[] cardsAfter = checkCards(newToken);
        int balanceAfter = DataHelper.getCardBalance(cardsAfter, id);

        int balanceExpected = balanceBefore;

        assertEquals(balanceExpected, balanceAfter);
    }
}
