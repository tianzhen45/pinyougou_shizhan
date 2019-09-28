package com.pinyougou.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name="tb_user_collection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbCollection implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long itemId;

    private String title;

    private String image;

    private Double price;

    private String brand;

    private String category;

    private String seller;

    private String sellerId;

    private String status;

    private Date createTime;
}
