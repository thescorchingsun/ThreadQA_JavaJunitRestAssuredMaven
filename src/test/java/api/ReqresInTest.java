package api;

import api.pojo.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresInTest {

    private final static String URL = "https://reqres.in/";

    @Test
    @DisplayName("Email пользователей имеет окончание reqres.in")
    public void checkDomainOfEmailTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        List<UserDataResponse> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserDataResponse.class);
        Assertions.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));

    }

    @Test
    @DisplayName("Совпадение имен файлов-аватаров пользователей с ID")
    public void checkAvatarAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        List<UserDataResponse> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserDataResponse.class);
        //1 способ. Напрямую через Stream достается из списка отдельно аватар и ID.
        users.stream().forEach(x -> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));
/*
        2 способ. Получение списка с аватарами и ID.
        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());
        for(int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
 */
    }

    @Test
    @DisplayName("Успешная регистрация в системе")
    public void successRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Registr user = new Registr("eve.holt@reqres.in", "pistol");
        SuccessRegResponse successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessRegResponse.class);
        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());

        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());
    }

    @Test
    @DisplayName("Регистрация в системе с ошибкой, почта и пустой пароль")
    public void unSuccessRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Registr user = new Registr("sydney@fife", "");
        UnSuccessRegResponse unSuccessReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessRegResponse.class);
        Assertions.assertNotNull(unSuccessReg.getError());
        Assertions.assertEquals("Missing password", unSuccessReg.getError());
    }

    @Test
    @DisplayName("Операция LIST<RESOURCE> возвращает данные отсортированные по годам")
    public void sortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());
        List<ColorsDataResponse> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsDataResponse.class);
        List<Integer> years = colors.stream().map(ColorsDataResponse::getYear).collect(Collectors.toList()); //Получение списка
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());//Сортировка по возрастанию
        Assertions.assertEquals(sortedYears, years);
        System.out.println(sortedYears);
        System.out.println(years);
    }

    @Test
    @DisplayName("Удаление пользователя")
    public void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUniqueCode(204));
        given()
                .delete("/api/users/2")
                .then().log().all();
    }

}


