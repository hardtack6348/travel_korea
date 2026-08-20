package kr.co.mycom.travel_korea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID",nullable = false)
    private Long id;
    @Column(name = "EMAIL",nullable = false)
    private String email;
    @Column(name = "NICKNAME",nullable = false, length = 30)
    private String nickname;
    @Column(name = "PASSWORD",nullable = false,  length = 128)
    private String password;
    @Column(name = "INTRODUCE", length = 200)
    private String introduce;
    @Column(name = "PROFILE_IMAGE")
    private String profile_image_url;
    @Column(name = "GENDER", length = 10)
    private String gender;
    @Column(name = "GRADE")
    private String grade;


}
