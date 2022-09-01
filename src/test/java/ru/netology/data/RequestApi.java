package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;


public class RequestApi {
    private RequestApi() {
    }

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static DataHelper.CardResponseInfo[] checkCards(String token) {
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

    public static void authorization() {
        given()
                .spec(requestSpec)
                .body(DataHelper.getAuthInfo())
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String getToken() {
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
        String token = getToken.getToken();
        return token;
    }

    public static void moneyTransfer(String token, String cardFrom, String cardTo, int amount, int expectedStatusCode) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .body(DataHelper.getTransferInfo(cardFrom, cardTo, amount))
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(expectedStatusCode);
    }
}
