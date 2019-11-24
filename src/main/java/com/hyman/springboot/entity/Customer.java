package com.hyman.springboot.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * @author huaimin
 * @date 2019/10/31
 */
@Data
public class Customer {

    @Id
    private String id;

    private String firstName;

    private String lastName;
}
