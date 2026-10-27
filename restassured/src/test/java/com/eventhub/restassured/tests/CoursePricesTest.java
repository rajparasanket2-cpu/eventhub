package com.eventhub.restassured.tests;

import com.eventhub.restassured.support.CoursesApiTestBase;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;

/** Checks on the course prices in the response. */
public class CoursePricesTest extends CoursesApiTestBase {

    @Test(description = "TC76: the sum of all course prices is less than $1000")
    public void tc76_totalPriceUnder1000() {
        // -- Step 1: fetch the courses response --
        JsonPath json = given(spec)
                .when().get()
                .then().statusCode(200).contentType(ContentType.JSON)
                .extract().jsonPath();

        // -- Step 2: add up the price of every course, in every category the response has --
        BigDecimal total = BigDecimal.ZERO;
        int courseCount = 0;
        for (Object category : json.getMap("courses").keySet()) {
            List<String> prices = json.getList("courses." + category + ".price", String.class);
            for (String price : prices) {
                total = total.add(new BigDecimal(price));
                courseCount++;
            }
        }
        System.out.printf("Sum of %d course prices: $%s%n", courseCount, total);

        // -- Step 3: the sum is below $1000 --
        Assert.assertTrue(courseCount > 0, "Response has no courses to sum");
        Assert.assertTrue(total.compareTo(new BigDecimal("1000")) < 0,
                "Sum of all course prices should be less than $1000 but was $" + total);
    }
}
