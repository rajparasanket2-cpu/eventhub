package com.eventhub.restassured.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Adds a book to the live Library API and reads it back by ID.
 * Unlike the course tests this calls a real server, so each run uses a unique ISBN and deletes the book afterwards.
 */
public class LibraryBookTest {
    private static final String BASE_URI = "http://216.10.245.166";

    private RequestSpecification spec;
    /** The ID of the book this test added, so it can be deleted even when an assertion fails. */
    private String addedBookId;

    @BeforeClass
    public void setUp() {
        spec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test(description = "TC82: a book added through Addbook.php is returned with the same details by GetBook.php?ID")
    public void tc82_addBookAndGetItById() {
        // -- Step 1: build a book with a unique ISBN, since the server is shared and rejects duplicate IDs --
        String name = "Learn Appium Automation with Java";
        String isbn = "ra" + UUID.randomUUID().toString().substring(0, 8);
        String aisle = "227";
        String author = "John foe";
        Map<String, String> book = Map.of("name", name, "isbn", isbn, "aisle", aisle, "author", author);

        // -- Step 2: add the book, and keep its ID before asserting so cleanup still runs if an assertion fails --
        Response addResponse = given(spec)
                .body(book)
                .when().post("/Library/Addbook.php");
        JsonPath added = addResponse.jsonPath();
        addedBookId = added.getString("ID");
        System.out.println("Added book ID: " + addedBookId);

        // -- Step 3: the book was added, and the server built its ID from isbn + aisle --
        Assert.assertEquals(addResponse.statusCode(), 200, "Addbook.php status");
        Assert.assertEquals(added.getString("Msg"), "successfully added", "Msg");
        Assert.assertEquals(addedBookId, isbn + aisle, "ID");

        // -- Step 4: get the book back by its ID; GetBook.php answers with an array of books --
        JsonPath getResponse = given(spec)
                .queryParam("ID", addedBookId)
                .when().get("/Library/GetBook.php")
                .then().statusCode(200).contentType(ContentType.JSON)
                .extract().jsonPath();

        // -- Step 5: exactly one book matches the ID, with the same details that were added --
        Assert.assertEquals(getResponse.getInt("size()"), 1, "Exactly one book should match the ID");
        Assert.assertEquals(getResponse.getString("[0].book_name"), name, "book_name");
        Assert.assertEquals(getResponse.getString("[0].isbn"), isbn, "isbn");
        Assert.assertEquals(getResponse.getString("[0].aisle"), aisle, "aisle");
        Assert.assertEquals(getResponse.getString("[0].author"), author, "author");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteAddedBook() {
        // -- Cleanup: delete the book so the shared server is left as it was --
        if (addedBookId == null) {
            return;
        }
        given(spec)
                .body(Map.of("ID", addedBookId))
                .when().post("/Library/DeleteBook.php")
                .then().statusCode(200)
                .body("msg", equalTo("book is successfully deleted"));
        addedBookId = null;
    }
}
