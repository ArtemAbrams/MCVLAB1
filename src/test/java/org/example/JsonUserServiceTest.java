package org.example;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonUserServiceTest {

    private JsonUserService service;
    private static String initialJson;

    @BeforeAll
    static void setupClass() {
        initialJson = "{\"1\":{\"id\":\"1\", \"name\":\"John\", \"age\":30}}";
    }

    @BeforeEach
    void setup() throws Exception {
        service = new JsonUserService(initialJson);
    }

    @Test
    void userExistsAfterAddition() throws Exception {
        User newUser = new User("2", "Jane", 25);
        service.addUser(newUser);
        assertNotNull(service.getUser("2"));
    }

    @Test
    void userHasCorrectName() throws Exception {
        User newUser = new User("3", "Mike", 30);
        service.addUser(newUser);
        assertEquals("Mike", service.getUser("3").getName());
    }

    @Test
    void userRemovalEffectiveness() throws Exception {
        service.addUser(new User("4", "Emily", 22));
        service.removeUser("4");
        assertNull(service.getUser("4"));
    }

    @Test
    void allUsersRetrieval() {
        User user = service.getAllUsers()
                .get(0);

        assertTrue(user.getName().equals("John"));
    }

    @Test()
    void addUserAlreadyExistsThrowsException() throws Exception {
        User existingUser = new User("1", "John", 30);

        assertThrows(IllegalArgumentException.class, () -> service.addUser(existingUser));
    }

    @ParameterizedTest
    @MethodSource("provideUsersForNameCheck")
    void userHasCorrectNameParameterized(String id, String expectedName) throws Exception {
        User newUser = new User("2", "Jane", 25);
        service.addUser(newUser);

        assertEquals(expectedName, service.getUser(id).getName());
    }

    static Stream<Arguments> provideUsersForNameCheck() {

        return Stream.of(
                Arguments.of("1", "John"),
                Arguments.of("2", "Jane")
        );
    }

    @ParameterizedTest
    @CsvSource({
            "1, true",
            "2, false",
            "3, false"
    })
    void userExistenceCheck(String id, boolean expectedExistence) throws Exception {
        if (expectedExistence) {
            assertNotNull(service.getUser(id));
        } else {
            assertNull(service.getUser(id));
        }
    }

    @Test
    void userPropertiesMatchComplexConditions() throws Exception {
        User user = new User("3", "Jane Smith", 35);
        service.addUser(user);
        User fetchedUser = service.getUser("3");

        assertThat(fetchedUser, allOf(
                hasProperty("name", allOf(notNullValue(), containsString("Smith"))),
                hasProperty("age", allOf(greaterThan(30), lessThan(40)))
        ));
    }

    @Test
    void allUsersMatchComplexConditions() throws Exception {
        service.addUser(new User("2", "Jane Doe", 25));

        List<User> allUsersJson = service.getAllUsers();

        assertThat(allUsersJson, everyItem(
                both(hasProperty("name", not(emptyOrNullString())))
                        .and(hasProperty("age", greaterThan(18)))
        ));
    }
}
