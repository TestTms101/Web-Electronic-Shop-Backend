package com.example.electronicshop.models.product;

import com.example.electronicshop.models.enity.Category;
import com.example.electronicshop.models.enity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  Product {
    @Id
    private String id;
    @NotBlank(message = "Name is required")
    @Indexed(unique = true)
    private String name;
    @NotBlank(message = "slugify is required")
    private String slugify;
    //    @ReadOnlyProperty
//    @DocumentReference(lookup="{'product':?#{#self._id} }", lazy = true)
//    private List<ProductImage> images = new ArrayList<>();
    private List<ProductImage> images = new ArrayList<>();

    @ReadOnlyProperty
    @DocumentReference(lookup="{'product':?#{#self._id} }", lazy = true)
    @Indexed
    private List<Comment> comment;
    @NotNull(message = "Price is required")
    private BigDecimal price;
//    @NotNull(message = "Quantity is required")
    private int quantity;
    private double sale;
    private double sold;
    private BigDecimal discount;
    private double rate = 0;
//    @NotBlank(message = "summary is required")
    private String summary;

//    private List<ProductOption> options = new ArrayList<>();
//    @NotBlank(message = "tags is required")
//    private List<String> tags;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    @DocumentReference
    private Category category;
    @NotBlank(message = "State is required")
    private String state;
    @CreatedDate
//    @JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    LocalDateTime createdDate;

    public Product(String name, String slugify, BigDecimal price,int quantity, double sale, String description, Category category, String state, LocalDateTime createdDate) {
        this.name = name;
        this.slugify = slugify;
        this.price = price;
        this.quantity = quantity;
        this.sale = sale;
//        this.summary = summary;
//        this.tags = tags;
        this.description = description;
        this.category = category;
        this.state = state;
        this.createdDate = createdDate;
    }

    public Product(String name, String slugify, BigDecimal price, int quantity, double sale, String summary, BigDecimal discount, String description, Category category, String state, LocalDateTime createdDate) {
        this.name = name;
        this.slugify = slugify;
        this.price = price;
        this.quantity = quantity;
        this.sale = sale;
        this.summary = summary;
        this.discount = discount;
//        this.tags = tags;
        this.description = description;
        this.category = category;
        this.state = state;
        this.createdDate = createdDate;
    }
//    public BigDecimal subdiscount() {
////        BigDecimal originPrice = (item.getPrice().multiply(BigDecimal.valueOf(quantity)));
//        String discountString = price.multiply(BigDecimal.valueOf((1-getSale()))).divide(BigDecimal.valueOf(1000))
//                .setScale(0, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(1000)).stripTrailingZeros().toPlainString();
//        return new BigDecimal(discountString);
//    }
    @Transient
    public int getRateCount() {
        try {
            return comment.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
