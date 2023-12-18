package com.example.electronicshop.service;

import com.example.electronicshop.communication.StateCountAggregate;
import com.example.electronicshop.config.CloudinaryConfig;
import com.example.electronicshop.config.Constant;
import com.example.electronicshop.map.ProductMapper;
import com.example.electronicshop.models.enity.Category;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.models.product.Product;
import com.example.electronicshop.models.product.ProductImage;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.notification.NotFoundException;
import com.example.electronicshop.communication.request.ProductReq;
import com.example.electronicshop.communication.response.ProductRes;
import com.example.electronicshop.repository.CategoryRepository;
import com.example.electronicshop.repository.ProductRepository;
import com.mongodb.MongoWriteException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CloudinaryConfig cloudinary;

    public ResponseEntity<?> findAll(String state, Pageable pageable) {
        Page<Product> products;
        if (state=="all")
            products = productRepository.findAll(pageable);
        else if(state=="enable") products = productRepository.findAllByStateOrderByCreatedDateDesc(Constant.ENABLE, pageable);
        else products = productRepository.findAllByStateOrderByCreatedDateDesc(Constant.DISABLE, pageable);
        List<ProductRes> resList = products.getContent().stream().map(productMapper::toProductRes).collect(Collectors.toList());
        return addPageableToRes(products, resList);
//        ResponseEntity<?> resp = addPageableToRes(products, resList);
//        if (resp != null) return resp;
//        throw new NotFoundException("Can not found any product");
    }

    public ResponseEntity<?> findAllOrderbySoldDesc(Pageable pageable) {
        Page<Product> products = productRepository.findAllByStateOrderBySoldDesc(Constant.ENABLE, pageable);
        List<ProductRes> resList = products.getContent().stream().map(productMapper::toProductRes).collect(Collectors.toList());
        return addPageableToRes(products, resList);
//        ResponseEntity<?> resp = addPageableToRes(products, resList);
//        if (resp != null) return resp;
//        throw new NotFoundException("Can not found any product");
    }

    private ResponseEntity<?> addPageableToRes(Page<Product> products, List<ProductRes> resList) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", resList);
        resp.put("totalQuantity", products.getTotalElements());
        resp.put("totalPage", products.getTotalPages());
        if (resList.size() >0 )
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get all product success", resp));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can't not find product ", resp));
    }

    public ResponseEntity<?> findAllProductHomePage() {
        List<Category> list = categoryRepository.findCategoryByState(Constant.ENABLE);
        List<ProductRes> productRes;
        List<Map<String, Object>> resp = new ArrayList<>();
        for (Category i: list) {
            List<Product> products = productRepository.findAllByCategory_IdAndState(new ObjectId(i.getId()),Constant.ENABLE);
            if(products.size()>10)
                productRes = products.stream().map(productMapper::toProductRes).toList().subList(0,10);
            else productRes = products.stream().map(productMapper::toProductRes).toList();
            Map<String, Object> map = new HashMap<>();
            map.put("categoryId",i.getId());
            map.put("title", i.getName());
            map.put("items",productRes);
            resp.add(map);
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get all category success", resp));

    }

    public ResponseEntity<?> findById(String id) {
        Optional<Product> product = productRepository.findProductByIdAndState(id, Constant.ENABLE);
        if (product.isPresent()) {
            ProductRes res = productMapper.toProductRes(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get product success", res));
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any product with id: " + id, ""));
    }

//    public ResponseEntity<?> findByCategoryId(String id) {
//        List<Product> products;
//        try {
//            Optional<Category> category = categoryRepository.findCategoryByIdAndState(id, Constant.ENABLE);
//            if (category.isPresent()) {
//                List<ObjectId> subCat = category.get().getSubCategories()
//                        .stream().map(c -> new ObjectId(c.getId())).collect(Collectors.toList());
//                products = productRepository.findProductsByCategoryOrderByCreatedDateAsc(new ObjectId(id),
//                        subCat);
//            } else products = productRepository.findAllByCategory_IdAndStateOrderByCreatedDateAsc(new ObjectId(id),
//                    Constant.ENABLE);
//        } catch (Exception e) {
//            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Error when finding");
//        }
//
//        List<ProductRes> resList = products.stream().map(productMapper::toProductRes).collect(Collectors.toList());
//        resList.sort(Comparator.comparing(ProductRes::getCreatedDate).reversed());
//        if (!resList.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject(true, "Get all product success", resList));
//        else return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject(false, "Can not found any product with category id: "+id, resList));
//    }
    public ResponseEntity<?> searchCategoryId(String id, String sortBy, String state, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products;
        if(state.equals("")){
            products = productRepository.findAllByCategory_Id(new ObjectId(id));
        }else {
            products = productRepository.findAllByCategory_IdAndState(new ObjectId(id),state);
        }
        List<ProductRes> resList = products.stream().map(productMapper::toProductRes).collect(Collectors.toList());
        Iterator<ProductRes> iterator = resList.iterator();
        while (iterator.hasNext()) {
            ProductRes product = iterator.next();
            BigDecimal productPrice = product.getDiscount();
            if (productPrice.compareTo(minPrice) < 0 || productPrice.compareTo(maxPrice) > 0) {
                iterator.remove();
            }
        }
        resList.sort(Comparator.comparing(ProductRes::getCreatedDate).reversed());
        switch (sortBy) {
            case "oldest" -> resList.sort(Comparator.comparing(ProductRes::getCreatedDate));
            case "sold" -> resList.sort(Comparator.comparing(ProductRes::getSold).reversed());
            case "priceDesc" -> resList.sort(Comparator.comparing(ProductRes::getDiscount).reversed());
            case "priceAsc" -> resList.sort(Comparator.comparing(ProductRes::getDiscount));
            default -> resList.sort(Comparator.comparing(ProductRes::getCreatedDate).reversed());
        }
        if (!resList.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get all product success", resList));
        else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any product with category id: "+id, resList));
    }
    public ResponseEntity<?> search(String key, String sortBy, String state, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        List<Product> products;
        if(state.equals(""))
            products=productRepository.findAllByIdOrNameOrDescriptionRegex(key,key,key);
        else products=productRepository.findAllByIdOrNameOrDescriptionRegexAndState(key,key,key,state);
        List<ProductRes> resList = products.stream().map(productMapper::toProductRes).collect(Collectors.toList());
        Iterator<ProductRes> iterator = resList.iterator();
        while (iterator.hasNext()) {
            ProductRes product = iterator.next();
            BigDecimal productPrice = product.getDiscount();
            if (productPrice.compareTo(minPrice) < 0 || productPrice.compareTo(maxPrice) > 0) {
                iterator.remove();
            }
        }
        switch (sortBy) {
//            case "latest" -> resList.sort(Comparator.comparing(ProductRes::getCreatedDate).reversed());
            case "oldest" -> resList.sort(Comparator.comparing(ProductRes::getCreatedDate));
            case "sold" -> resList.sort(Comparator.comparing(ProductRes::getSold).reversed());
            case "priceDesc" -> resList.sort(Comparator.comparing(ProductRes::getDiscount).reversed());
            case "priceAsc" -> resList.sort(Comparator.comparing(ProductRes::getDiscount));
            default -> resList.sort(Comparator.comparing(ProductRes::getCreatedDate).reversed());
        }
        if (resList.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(false, "Can not found any product with: "+key, resList));
        else return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(true, "Get all product success", resList));
    }
    public ResponseEntity<?> getAllCountProducts() {
        try {
            List<StateCountAggregate> resp = new ArrayList<>();
//            resp = userRepository.countAllByState();
            resp.add(new StateCountAggregate("all",productRepository.countAllBy()));
            resp.add(new StateCountAggregate("enable",productRepository.countByState(Constant.ENABLE)));
            resp.add(new StateCountAggregate("disable",productRepository.countByState(Constant.DISABLE)));
            resp.sort(Comparator.comparing(StateCountAggregate::getCount).reversed());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Get count by Products success", resp));
        } catch (Exception e) {
            throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
        }
    }
    public ResponseEntity<?> addProduct(ProductReq req) {
        if (req != null) {
            Product product = productMapper.toProduct(req);
            try {
                productRepository.save(product);
            } catch (Exception e) {
                throw new AppException(HttpStatus.CONFLICT.value(), "Product name already exists");
            }
            ProductRes res = productMapper.toProductRes(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject(true, "Add product successfully ", res)
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject(false, "Request is null", "")
        );
    }
//    public List<ProductImage> processUploadImage (List<MultipartFile> images, Product product) {
//        if (images == null || images.isEmpty()) throw new AppException(HttpStatus.BAD_REQUEST.value(), "images is empty");
//        for (int i = 0; i < images.size(); i++) {
//            try {
//                String url = cloudinary.uploadImage(images.get(i), null);
//                if (i == 0) product.getImages().add(new ProductImage(UUID.randomUUID().toString(), url));
//                else product.getImages().add(new ProductImage(UUID.randomUUID().toString(), url));
//            } catch (IOException e) {
//                log.error(e.getMessage());
//                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload images");
//            }
//            productRepository.save(product);
//        }
//        return product.getImages();
//    }

//    public ResponseEntity<?> updateImage(String id, List<MultipartFile> file) {
//        Optional<Product> product= productRepository.findById(id);
//        if (product.isPresent()) {
//            for (int i = 0; i < file.size(); i++) {
//                try {
//                    String url = cloudinary.uploadImage(file.get(i), null);
//                    if (i == 0) product.get().getImages().add(new ProductImage(UUID.randomUUID().toString(), url));
//                    else product.get().getImages().add(new ProductImage(UUID.randomUUID().toString(), url));
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                    throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload images");
//                }
//        }
//        productRepository.save(product.get());
//            ProductRes res = productMapper.toProductRes(product.get());
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject("true", "Update user success", res));
//        }
//        throw new NotFoundException("Can not found user with id " + id );
//    }
    public ResponseEntity<?> addImagesToProduct(String id, List<MultipartFile> files) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            try {
                files.forEach(f -> {
                    try {
                        String url = cloudinary.uploadImage(f, null);
                        product.get().getImages().add(new ProductImage(UUID.randomUUID().toString(), url));
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), "Error when upload images");
                    }
                    productRepository.save(product.get());
                });
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject(true, "Add image to product successfully", product.get().getImages())
                );
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new NotFoundException("Error when save image: " + e.getMessage());
            }
        } throw new NotFoundException("Can not found product with id: " + id);
    }
    public ResponseEntity<?> deleteImageProduct(String idProduct, String imageId) {
        Optional<Product> product = productRepository.findById(idProduct);
        if (product.isPresent() && !product.get().getImages().isEmpty()) {
            try {
                Optional<ProductImage> checkDelete = product.get().getImages().stream().filter(i -> i.getId_image().equals(imageId)).findFirst();
                if (checkDelete.isPresent()) {
                    cloudinary.deleteImage(checkDelete.get().getUrl());
                    product.get().getImages().remove(checkDelete.get());
                    productRepository.save(product.get());
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject(true, "Delete image successfully", imageId)
                    );
                } else throw new NotFoundException("Can not found image in product with id: " + imageId);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new NotFoundException("Can not found product with id: " + idProduct);
            }
        } throw new NotFoundException("Can not found any image or product with id: " + idProduct);
    }
//    public ResponseEntity<ResponseObject> deleteImage (String id_product, String id_image) {
//        Optional<Product> productImage= productRepository.findByIdAndImagesId(id_product,id_image);
//
//        if (productImage.isEmpty())
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObject("failed", "Cannot find image ", "")
//            );
//        try{
//            if (productImage.get().getImages().get(0).getUrl().startsWith("https://res.cloudinary.com/dnqm1rkqr/image/upload")) {
//                cloudinary.deleteImage(productImage.get().getImages().get(0).getUrl());
//            }
////            productRepository.deleteById(productImage.get().getImages().get(0).getId_image());
////            productRepository.deleteImageById(id_image);
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject("ok", "Delete image successfully ", "")
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
//                    new ResponseObject("failed", "Cannot delete image successfully ", e.getMessage())
//            );
//        }
//    }

    @Transactional
    public ResponseEntity<?> updateStateProduct(String id) {
        Optional<Product> product = productRepository.findProductByIdAndState(id, Constant.DISABLE);
        if (product.isPresent()) {
            product.get().setState(Constant.ENABLE);
            productRepository.save(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Update State product successfully ", "")
            );
        } throw new NotFoundException("Can not found product with id: "+id);
    }

    @Transactional
    public ResponseEntity<?> updateProduct(String id, ProductReq req) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent() && req != null) {
            processUpdate(req, product.get());
            try {
                productRepository.save(product.get());
            } catch (MongoWriteException e) {
                throw new AppException(HttpStatus.CONFLICT.value(), "Product name already exists");
            } catch (Exception e) {
                throw new AppException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage());
            }
            ProductRes res = productMapper.toProductRes(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Update product successfully ", res)
            );
        }
        throw new NotFoundException("Can not found product with id: "+id);
    }

    public void processUpdate(ProductReq req, Product product) {
        if (!req.getName().equals(product.getName()))
            product.setName(req.getName());
        if (!req.getDescription().equals(product.getDescription()))
            product.setDescription(req.getDescription());
        if (!req.getPrice().equals(product.getPrice()))
            product.setPrice(req.getPrice());
        if (!req.getSlugify().equals(product.getSlugify()))
            product.setSlugify(req.getSlugify());
        product.setSale(req.getSale());
        product.setDiscount(req.subdiscount());
        product.setQuantity(req.getQuantity());
        if (!req.getCategory().equals(product.getCategory().getId())) {
            Optional<Category> category = categoryRepository.findCategoryByIdAndState(req.getCategory(), Constant.ENABLE);
            if (category.isPresent())
                product.setCategory(category.get());
            else throw new NotFoundException("Can not found category with id: "+req.getCategory());
        }
        if (req.getState() != null && !req.getState().isEmpty() &&
                (req.getState().equalsIgnoreCase(Constant.ENABLE) ||
                        req.getState().equalsIgnoreCase(Constant.DISABLE)))
            product.setState(req.getState());
        else throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid state");
    }

    public ResponseEntity<?> deactivatedProduct(String id) {
        Optional<Product> product = productRepository.findProductByIdAndState(id, Constant.ENABLE);
        if (product.isPresent()) {
            product.get().setState(Constant.DISABLE);
            productRepository.save(product.get());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Delete product successfully ", "")
            );
        } throw new NotFoundException("Can not found product with id: "+id);
    }

    @Transactional
    public ResponseEntity<?> destroyProduct(String id) {
        Optional<Product> product = productRepository.findProductByIdAndState(id, Constant.ENABLE);
        if (product.isPresent()) {
            try {
                productRepository.deleteById(product.get().getId());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new NotFoundException("Error when destroy product with id: "+id);
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(true, "Destroy product successfully ", "")
            );
        } throw new NotFoundException("Can not found product with id: "+id);
    }
}
