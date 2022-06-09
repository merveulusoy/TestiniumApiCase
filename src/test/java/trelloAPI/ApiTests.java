package trelloAPI;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.ConfigurationReader;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = ConfigurationReader.getProperty("baseURI");
        RestAssured.basePath = ConfigurationReader.getProperty("basePath");
    }
    String boardId;
    String listId;
    String[] cardsIdArr = new String[2];


    public String createBoardAndListThenGetTheListID(){
         boardId =
                given()
                        .contentType("application/json")
                        .when()
                        .queryParam("key", ConfigurationReader.getProperty("key"))
                        .queryParam("token", ConfigurationReader.getProperty("token"))
                        .queryParam("name", "NewBoard")
                        .post("/boards")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .assertThat()
                        .body("name", equalTo("NewBoard"))
                        .extract().path("id");

         listId =
                given()
                        .contentType("application/json")
                        .when()
                        .queryParam("key", ConfigurationReader.getProperty("key"))
                        .queryParam("token", ConfigurationReader.getProperty("token"))
                        .queryParam("name","TestList")
                        .post("/boards/"+boardId+"/lists")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .assertThat()
                        .body("name", equalTo("TestList"))
                        .extract().path("id");
        return listId;
    }


    @Test(priority = 1)
    public void CreateBoard(){
        given()
                .contentType("application/json")
                .when()
                .queryParam("key", ConfigurationReader.getProperty("key"))
                .queryParam("token", ConfigurationReader.getProperty("token"))
                .queryParam("name", "BoardTest")
                .post("/boards")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body("name", equalTo("BoardTest"));
    }


    @Test(priority = 2)
    public void CreateListOnTheBoard(){
        boardId =
                given()
                        .contentType("application/json")
                        .when()
                        .queryParam("key", ConfigurationReader.getProperty("key"))
                        .queryParam("token", ConfigurationReader.getProperty("token"))
                        .queryParam("name", "BoardTest")
                        .post("/boards")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .assertThat()
                        .body("name", equalTo("BoardTest"))
                        .extract().path("id");

        given()
                .contentType("application/json")
                .when()
                .queryParam("key", ConfigurationReader.getProperty("key"))
                .queryParam("token", ConfigurationReader.getProperty("token"))
                .queryParam("name","ListTest")
                .post("/boards/"+boardId+"/lists")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body("name", equalTo("ListTest"));
    }


    @Test(priority = 3)
    public void CreateCards() {
        listId = createBoardAndListThenGetTheListID();

         // create 2 cards
        for (int i = 0; i < 2; i++) {
            cardsIdArr[i] = given()
                    .contentType("application/json")
                    .when()
                    .queryParam("key", ConfigurationReader.getProperty("key"))
                    .queryParam("token", ConfigurationReader.getProperty("token"))
                    .queryParam("name", "NewBoard" + i)
                    .queryParam("idList", listId)
                    .queryParam("desc", "initial Test Description")
                    .post("/cards")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .assertThat()
                    .body("name", equalTo("NewBoard" + i))
                    .extract().path("id");
        }

        //Edit one of the cards
        Random rd = new Random();
        int random = rd.nextInt(cardsIdArr.length);


        given()
                .contentType("application/json")
                .when()
                .queryParam("key", ConfigurationReader.getProperty("key"))
                .queryParam("token", ConfigurationReader.getProperty("token"))
                .queryParam("name", "NewBoard0")
                .queryParam("desc", "edited Test Description")
                .put("/cards/" + cardsIdArr[random])
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .assertThat()
                .body("desc", equalTo("edited Test Description"));
                //.extract().path("id");
    }

    @Test(priority = 4)
    //Delete all of the cards
    public void DeleteCards(){


        for(int i = 0; i< cardsIdArr.length; i++) {
            given()
                    .contentType("application/json")
                    .when()
                    .queryParam("key", ConfigurationReader.getProperty("key"))
                    .queryParam("token", ConfigurationReader.getProperty("token"))
                    .delete("/cards/" + cardsIdArr[i])
                    .then()
                    .statusCode(200);
        }
    }

    @Test(priority = 5)
    public void DeleteBoard(){

        given()
                .contentType("application/json")
                .when()
                .queryParam("key", ConfigurationReader.getProperty("key"))
                .queryParam("token", ConfigurationReader.getProperty("token"))
                .delete("/boards/"+boardId)
                .then()
                .statusCode(200);

        System.out.println("Board deleted");
    }
}

