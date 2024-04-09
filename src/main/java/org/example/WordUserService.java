package org.example;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class WordUserService {

    private XWPFDocument document;
    private String filePath;

    public WordUserService(String filePath) throws IOException {
        this.filePath = filePath;
        File file = new File(filePath);
        if (file.exists()) {
            this.document = new XWPFDocument(new FileInputStream(file));
        } else {
            file.createNewFile();
            this.document = new XWPFDocument();
            saveDocument();
        }
    }

    public void addUser(User user) throws IOException {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(user.toString());
        saveDocument();
    }

    public void removeUser(int index) throws IOException {
        if (index >= 0 && index < document.getParagraphs().size()) {
            document.removeBodyElement(index);
            saveDocument();
        } else {
            throw new IndexOutOfBoundsException("Індекс за межами діапазону: " + index);
        }
    }

    public User getUser(int index) {
        if (index < 0 || index >= document.getParagraphs().size()) {
            return null;
        }
        XWPFParagraph paragraph = document.getParagraphs().get(index);
        return User.fromString(paragraph.getText());
    }

    public List<User> getAllUsers() {
        return document.getParagraphs().stream()
                .map(paragraph -> User.fromString(paragraph.getText()))
                .collect(Collectors.toList());
    }

    private void saveDocument() throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        }
    }
}
