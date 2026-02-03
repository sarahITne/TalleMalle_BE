package com.tallemalle.api.user.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
}
