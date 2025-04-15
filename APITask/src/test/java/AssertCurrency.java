import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertTrue;

public class AssertCurrency {
    private String URL;
    private int minCurrencyCount;
    private List<String> currencyType;

    @SneakyThrows
    @BeforeClass
    public void loadConfig() throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/config.properties")) {
            props.load(fis);
        }
        URL = props.getProperty("URL");
        minCurrencyCount = Integer.parseInt(props.getProperty("minCurrencyCount", "20"));
        currencyType = Arrays.asList(props.getProperty("currencyType").split(","));
    }

    @SneakyThrows
    @Test
    public void assertMoreThanTwentyItems() {

        String responseJSON = given()
                .when()
                .get(URL)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        Map<String, Object> currencyMap = new ObjectMapper().readValue(responseJSON, HashMap.class);

        assertTrue(currencyMap.size() > minCurrencyCount, "Expected more than 20 currency entries");

        System.out.println("Number of currencies: " + currencyMap.size());

        List<String> list = new ArrayList<>(currencyMap.keySet());
        System.out.println(list.size());

        for (String type : currencyType) {
            boolean found = false;
            for (Map.Entry<String, Object> entry : currencyMap.entrySet()) {

                if (entry.getKey().equalsIgnoreCase(type)) {
                    String value = entry.getValue().toString();
                    System.out.println("Currency available for " + value);
                    found = true;
                }
            }
            assertTrue(found, "Currency " + type + "Not found");
        }
    }
}
