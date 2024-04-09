package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String name;
    private int age;
    @Override
    public String toString() {
        return id + "," + name + "," + age;
    }

    public static User fromString(String userData) {
        String[] parts = userData.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Неправильний формат даних користувача: " + userData);
        }
        return new User(parts[0], parts[1], Integer.parseInt(parts[2]));
    }
}
