package com.example.electronicshop.map;

import com.example.electronicshop.config.Constant;
import com.example.electronicshop.models.enity.Category;
import com.example.electronicshop.models.enity.Brand;
import com.example.electronicshop.models.product.Product;
import com.example.electronicshop.models.product.ProductImage;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.communication.request.ProductReq;
import com.example.electronicshop.communication.response.ProductRes;
import com.example.electronicshop.repository.BrandRepository;
import com.example.electronicshop.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductMapper {
    private final CategoryRepository categoryRepository;

    public Product toProduct(ProductReq req) {
        Optional<Category> category = categoryRepository.findCategoryByIdAndState(req.getCategory(), Constant.ENABLE);
        if (category.isEmpty())
            throw new NotFoundException("Can not found category or brand");
        return new Product(req.getName(),req.getSlugify(),req.getPrice(),req.getSale(),req.getSummary(),
                req.getTags(),req.getDescription(),category.get(),Constant.ENABLE,req.getCreatedDate());
    }

//    public ProductRes toProductListRes(Product req) {
////        List<ProductImage> images = req.getImages().stream()
////                .filter(ProductImage::isThumbnail).distinct().collect(Collectors.toList());
////        HashSet<Object> seen=new HashSet<>();
////        images.removeIf(e->!seen.add(e.getImageId()));
//
////        String discountString = req.getPrice().multiply(BigDecimal.valueOf((double) (100- req.getDiscount())/100))
////                .stripTrailingZeros().toPlainString();
////        BigDecimal discountPrice = new BigDecimal(discountString);
//        return new ProductListRes(req.getId(), req.getName(), req.getDescription(),
//                req.getPrice(),discountPrice, req.getDiscount(), req.getRate(), req.getRateCount(), req.getCategory().getName(),
//                req.getBrand().getName(), req.getState(), req.getCreatedDate(), req.getAttr());
//    }

    public ProductRes toProductRes(Product req) {
//        String discountString = req.getPrice().multiply(BigDecimal.valueOf((double) (100- req.getDiscount())/100))
//                .stripTrailingZeros().toPlainString();
//        BigDecimal discountPrice = new BigDecimal(discountString);
        return new ProductRes(req.getId(), req.getName(), req.getSlugify(), req.getImages(),req.getPrice(), req.getSale(),
                req.getRate(), req.getSummary(), req.getOptions(),req.getTags(),req.getDescription(),req.getCategory().getName(),
                req.getState(),req.getCreatedDate());
    }
}
