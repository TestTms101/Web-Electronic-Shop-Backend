package com.example.electronicshop.communication.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductReq {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Slugify is required")
    private String slugify;
    private List<MultipartFile> images;
    @NotNull(message = "Quantity is required")
    private int quantity;
    @NotNull(message = "Price is required")
    private BigDecimal price;
    private double sale;
    @NotBlank(message = "summary is required")
    private String summary;
    private List<String> tags;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    private String state;
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime createdDate;
    public BigDecimal subdiscount() {
//        BigDecimal originPrice = (item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        String discountString = price.multiply(BigDecimal.valueOf((1-getSale()))).divide(BigDecimal.valueOf(1000))
                .setScale(0, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(1000)).stripTrailingZeros().toPlainString();
        return new BigDecimal(discountString);
    }
}
