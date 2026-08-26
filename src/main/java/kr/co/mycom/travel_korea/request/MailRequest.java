package kr.co.mycom.travel_korea.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {
    String email;
    Integer authCode;
}
