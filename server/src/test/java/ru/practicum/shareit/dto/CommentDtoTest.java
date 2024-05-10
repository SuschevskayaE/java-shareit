package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentResponse> json;

    @Test
    void testCommentResponseDto() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        User user = new User(3L, "User 3", "user@mail.ru");
        Item item = new Item(2L, "Item 1", "Item 1 des", true, user, null, null, null, null);
        CommentResponse commentResponse = new CommentResponse(
                1L,
                "text",
                item,
                "Author name",
                time
                );

        JsonContent<CommentResponse> result = json.write(commentResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Author name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Item 1 des");
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(3);
    }
}
