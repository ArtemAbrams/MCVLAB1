package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WordUserServiceTest {

    private static WordUserService wordUserService;
    private static final String USER_ID = "1";
    private static final String USER_NAME = "Test User";
    private static final int USER_AGE = 30;
    private static User testUser;

    @BeforeAll
    static void setUpBeforeClass(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("testlab.docx");
        wordUserService = new WordUserService(testFile.toString());
        testUser = new User(USER_ID, USER_NAME, USER_AGE);
    }

    @BeforeEach
    void setUp() throws IOException {
        wordUserService.addUser(testUser);
    }

    @Test
    void addUserAndRetrieveUser() throws IOException {
        User retrievedUser = wordUserService.getUser(0);
        assertNotNull(retrievedUser, "User should not be null after adding");
        assertEquals(testUser.toString(), retrievedUser.toString());
    }
    @AfterEach
    void tearDown(@TempDir Path tempDir) {
        Path testFile = tempDir.resolve("test.docx");
        File file = new File(testFile.toUri());
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void checkUserNotExists() {
        assertNull(wordUserService.getUser(1000), "User should be null if not exists");
    }

    @Test
    void removeUserAndCheckExistence() throws IOException {
        assertThrows(IndexOutOfBoundsException.class, () -> wordUserService.removeUser(-10));
    }

    @Test
    void getAllUsersCheckSize() {
        List<User> users = wordUserService.getAllUsers();
        assertFalse("User list should not be empty after adding users", ((List<?>) users).isEmpty());
    }

    @Test
    void operationShouldNotThrowException() {
        assertDoesNotThrow(() -> wordUserService.addUser(new User("2", "Another User", 25)), "Adding a user should not throw an exception");
    }

    @ParameterizedTest
    @CsvSource({
            "1, Test User One, 25",
            "2, Test User Two, 30",
            "3, Test User Three, 35"
    })
    void addUserAndRetrieveUser(String id, String name, int age) throws IOException {
        User user = new User(id, name, age);
        wordUserService.addUser(user);

        int userIndex = wordUserService.getAllUsers().size() - 1;
        User retrievedUser = wordUserService.getUser(userIndex);

        assertNotNull(retrievedUser, "User should not be null after adding");
        assertEquals(user.toString(), retrievedUser.toString());
    }
    @Test
    void getAllUsersShouldMatchSpecificCriteria() throws IOException {
        wordUserService.addUser(new User("1", "John Doe", 30));
        wordUserService.addUser(new User("2", "Jane Doe", 25));
        List<User> users = wordUserService.getAllUsers();

        assertThat(users, hasItem(hasProperty("name", is("John Doe"))));
        assertThat(users, hasItem(hasProperty("age", greaterThan(20))));

        assertThat(users, hasItem(hasProperty("name", startsWith("Jane"))));
    }
    @Test
    void getAllUsersShouldMatchSpecificCriteria_2() throws IOException {
        wordUserService.addUser(new User("1", "John Doe", 30));
        wordUserService.addUser(new User("2", "Jane Doe", 25));
        List<User> users = wordUserService.getAllUsers();

        assertThat(users, everyItem(hasProperty("age", greaterThan(15))));
    }
}
