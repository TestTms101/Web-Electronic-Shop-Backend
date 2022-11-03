package com.example.electronicshop.map;

import com.example.electronicshop.config.Constant;
import com.example.electronicshop.models.enity.Category;
import com.example.electronicshop.models.enity.Brand;
import com.example.electronicshop.models.product.Product;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.communication.request.ProductReq;
import com.example.electronicshop.communication.response.ProductListRes;
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
    private final BrandRepository brandRepository;

    public Product toProduct(ProductReq req) {
        Optional<Category> category = categoryRepository.findCategoryByIdAndState(req.getCategory(), Constant.ENABLE);
        Optional<Brand> brand = brandRepository.findBrandByIdAndState(req.getBrand(), Constant.ENABLE);
        if (category.isEmpty() || brand.isEmpty())
            throw new NotFoundException("Can not found category or brand");
        return new Product(req.getName(), req.getDescription(), req.getPrice(),
                category.get(), brand.get(), Constant.ENABLE, req.getDiscount());
    }

    public ProductListRes toProductListRes(Product req) {
//        List<ProductImage> images = req.getImages().stream()
//                .filter(ProductImage::isThumbnail).distinct().collect(Collectors.toList());
        HashSet<Object> seen=new HashSet<>();
//        images.removeIf(e->!seen.add(e.getImageId()));

        String discountString = req.getPrice().multiply(BigDecimal.valueOf((double) (100- req.getDiscount())/100))
                .stripTrailingZeros().toPlainString();
        BigDecimal discountPrice = new BigDecimal(discountString);
        return new ProductListRes(req.getId(), req.getName(), req.getDescription(),
                req.getPrice(),discountPrice, req.getDiscount(), req.getRate(), req.getRateCount(), req.getCategory().getName(),
                req.getBrand().getName(), req.getState(), req.getCreatedDate(), req.getAttr());
    }

    public ProductRes toProductRes(Product req) {
        String discountString = req.getPrice().multiply(BigDecimal.valueOf((double) (100- req.getDiscount())/100))
                .stripTrailingZeros().toPlainString();
        BigDecimal discountPrice = new BigDecimal(discountString);
        return new ProductRes(req.getId(), req.getName(), req.getDescription(),
                req.getPrice(),discountPrice, req.getDiscount(), req.getRate(), req.getRateCount(), req.getCategory().getName(),
                req.getBrand().getName(), req.getState(), req.getAttr(), req.getProductOptions());
    }
}
