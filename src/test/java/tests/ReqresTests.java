package tests;

import models.LoginBodyModel;
import models.LoginResponseModel;
import models.MissingPasswordResponseModel;
import models.listmodels.ListUsersDataResponseModel;
import models.listmodels.ListUsersResponseModel;
import models.listmodels.ListUsersSupportDataResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.DeleteUserSpec.deleteUserRequestSpec;
import static specs.DeleteUserSpec.deleteUserResponseSpec;
import static specs.ListUsersSpec.listUsersRequestSpec;
import static specs.ListUsersSpec.listUsersResponseSpec;
import static specs.LoginSpec.*;
import static specs.NotFoundUserSpec.notFoundUserRequestSpec;
import static specs.NotFoundUserSpec.notFoundUserResponseSpec;

public class ReqresTests extends TestBase{

    @DisplayName("Получение списка пользователей")
    @Test
    void getListUsersTest() {
        ListUsersResponseModel responseModel = step ("Запрос на получение списка пользователей", () ->
        given(listUsersRequestSpec)
                .when()
                .get("/users?page=2")
                .then()
                .spec(listUsersResponseSpec)
                .extract().as(ListUsersResponseModel.class));

        step ("Проверка общих данных", () -> {
            assertAll(
                    ()-> assertEquals(responseModel.getPage(), (2)),
                    ()-> assertEquals(responseModel.getPerPage(), (6)),
                    ()-> assertEquals(responseModel.getTotal(), (12)),
                    ()-> assertEquals(responseModel.getTotalPages(), (2))
            );
        });

        step("Проверка данных первого пользователя из списка", () -> {
            List<ListUsersDataResponseModel> data = responseModel.getData();
            assertAll(
                    ()-> assertEquals((data.get(0).getId()), (7)),
                    ()-> assertEquals((data.get(0).getEmail()), "michael.lawson@reqres.in"),
                    ()-> assertEquals((data.get(0).getFirstName()), "Michael"),
                    ()-> assertEquals((data.get(0).getLastName()), "Lawson"),
                    ()-> assertEquals((data.get(0).getAvatar()), "https://reqres.in/img/faces/7-image.jpg")
            );
        });

        step("Проверка данных о поддержке", () -> {
            ListUsersSupportDataResponseModel support = responseModel.getSupport();
            assertAll(
                    ()-> assertEquals((support.getUrl()), "https://reqres.in/#support-heading"),
                    ()-> assertEquals((support.getText()), "To keep ReqRes free, contributions towards server costs are appreciated!")
            );
        });
    }

    @DisplayName("Поиск отсутствующего пользователя")
    @Test
    void getSingleUserNotFoundTest() {
        step("Запрос на поиск пользователя", () -> {
            given(notFoundUserRequestSpec)
                    .when()
                    .get("/users/50")
                    .then()
                    .spec(notFoundUserResponseSpec);
        });
    }

    @DisplayName("Удаление пользователя")
    @Test
    void deleteUserTest() {
        step("Запрос на удаление пользователя", () -> {
            given(deleteUserRequestSpec)
                    .when()
                    .delete("/users/5")
                    .then()
                    .spec(deleteUserResponseSpec);
        });
    }

    @DisplayName("Удачная авторизация пользователя")
    @Test
    void postLoginSuccessfullTest() {
        LoginBodyModel autData = new LoginBodyModel();
        autData.setEmail("eve.holt@reqres.in");
        autData.setPassword("cityslicka");

            LoginResponseModel responseModel = step ("Запрос логина и пароля", () ->
                given(loginRequestSpec)
                    .body(autData)
                    .when()
                    .post("/login")
                    .then()
                     .spec(loginResponseSpec)
                    .extract().as(LoginResponseModel.class));

        step ("Проверка токена", () ->
                assertThat(responseModel.getToken()).isNotNull());

    }

    @DisplayName("Неудачная авторизация пользователя (отсутствует пароль)")
    @Test
    void postLoginUnsuccessfullTest() {
        LoginBodyModel autData = new LoginBodyModel();
        autData.setEmail("eve.holt@reqres.in");

        MissingPasswordResponseModel responseModel = step ("Запрос логина", () ->
                given(loginRequestSpec)
                    .body(autData)
                    .when()
                    .post("/login")
                    .then()
                    .spec(missingPasswordResponseSpec)
                    .extract().as(MissingPasswordResponseModel.class));

        step("Проверка текста ошибки", () ->
                assertThat(responseModel.getError()).isEqualTo("Missing password"));
    }
}
