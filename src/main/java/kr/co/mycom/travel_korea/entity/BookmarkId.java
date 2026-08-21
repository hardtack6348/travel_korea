package kr.co.mycom.travel_korea.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkId implements Serializable {
    private Long content_id;
    private Long user_id;
}
