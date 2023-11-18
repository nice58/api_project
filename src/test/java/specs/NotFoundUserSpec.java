package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;

public class NotFoundUserSpec {

        public static RequestSpecification notFoundUserRequestSpec = with()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .contentType(JSON);

        public static ResponseSpecification notFoundUserResponseSpec = new ResponseSpecBuilder()
                .log(STATUS)
                .log(BODY)
                .expectStatusCode(404)
                .build();
}
