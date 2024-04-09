package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class JsonUserService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonNode rootNode;

    public JsonUserService(String json) throws Exception {
        rootNode = objectMapper.readTree(json);
    }

    public User getUser(String id) throws JsonProcessingException {
        JsonNode userNode = rootNode.path(id);
        if (userNode.isMissingNode()) {
            return null;
        }
        return objectMapper.treeToValue(userNode, User.class);
    }

    public void addUser(User user) throws Exception {
        String userId = user.getId();
        JsonNode existingUser = rootNode.path(userId);

        if (!existingUser.isMissingNode()) {
            throw new IllegalArgumentException("Користувач з ID " + userId + " вже існує");
        }

        JsonNode userNode = objectMapper.valueToTree(user);
        ((ObjectNode) rootNode).set(userId, userNode);
    }

    public void removeUser(String id) {
        ((ObjectNode) rootNode).remove(id);
    }

    public List<User>  getAllUsers() {
        List<User> users = new ArrayList<>();
        rootNode.fields().forEachRemaining(entry -> {
            JsonNode userNode = entry.getValue();
            User user = null;
            try {
                user = objectMapper.treeToValue(userNode, User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            users.add(user);

        });
        return users;
    }
}
