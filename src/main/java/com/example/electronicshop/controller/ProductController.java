package com.example.electronicshop.controller;

import com.example.electronicshop.communication.request.ProductReq;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.service.ProductService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    @GetMapping(path = "/products/{id}")
    public ResponseEntity<?> findById (@PathVariable("id") String id){
        return productService.findById(id);
    }

//    @GetMapping(path = "/products/category/{id}")
//    public ResponseEntity<?> findByCategoryId (@PathVariable("id") String id){
//        return productService.findByCategoryId(id);
//    }
    @GetMapping(path = "/products/searchcategory")
    public ResponseEntity<?> findByCategoryId (@RequestParam("id") String id,@RequestParam("sortBy") String sortBy,
                                               @RequestParam(value = "state", defaultValue = "") String state,
                                               @RequestParam(value = "minPrice", defaultValue = "0") BigDecimal minPrice,
                                               @RequestParam(value = "maxPrice", defaultValue = "99999999999999") BigDecimal maxPrice
                                               ){
        return productService.searchCategoryId(id,sortBy,state,minPrice,maxPrice);
    }
    @GetMapping(path = "/products/home")
    public ResponseEntity<?> findAllProductHomePage (){
        return productService.findAllProductHomePage();
    }

    @GetMapping(path = "/admin/products/search")
    public ResponseEntity<?> searchadmin (@RequestParam("q") String query,@RequestParam("sortBy") String sortBy,
                                          @RequestParam(value = "minPrice", defaultValue = "0") BigDecimal minPrice,
                                          @RequestParam(value = "maxPrice", defaultValue = "99999999999999") BigDecimal maxPrice,
                                          @RequestParam(value = "state", defaultValue = "") String state,
                                          @ParameterObject Pageable pageable){
//        if (query.isEmpty() || query.matches(".*[%<>&;'\0-].*"))
//            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid keyword");
        return productService.search(query, sortBy, state,minPrice,maxPrice,pageable);
    }

    @GetMapping(path = "/products/search")
    public ResponseEntity<?> search (@RequestParam("q") String query,@RequestParam("sortBy") String sortBy,
                                     @RequestParam(value = "minPrice", defaultValue = "0") BigDecimal minPrice,
                                     @RequestParam(value = "maxPrice", defaultValue = "99999999999999") BigDecimal maxPrice,
                                     @ParameterObject Pageable pageable){
//        if (query.isEmpty() || query.matches(".*[%<>&;'\0-].*"))
//            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid keyword");
        return productService.search(query, sortBy, "enable",minPrice,maxPrice,pageable);
    }

    @GetMapping(path = "/admin/products/count")
    public ResponseEntity<?> getCountByState (){
        return productService.getAllCountProducts();
    }

    @GetMapping(path = "/products/soldDesc")
    public ResponseEntity<?> findAllByStateOrderBySoldDesc (@ParameterObject Pageable pageable){
        return productService.findAllOrderbySoldDesc(pageable);
    }

    @GetMapping(path = "/products/byStateEnable")
    public ResponseEntity<?> findAllByState (@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                                 @ParameterObject Pageable pageable){
        return productService.findAll("enable", pageable);
    }

    @GetMapping(path = "/products")
    public ResponseEntity<?> findAllState (@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                               @ParameterObject Pageable pageable){
        return productService.findAll("all",pageable);
    }

    @GetMapping(path = "/products/byStateDisable")
    public ResponseEntity<?> findAll (@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC)
                                          @ParameterObject Pageable pageable){
        return productService.findAll("disable",pageable);
    }

    @PostMapping("/products/add")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductReq req) {
        return productService.addProduct(req);}

//    @DeleteMapping("/deleteanh/{id}/{id_image}")
//    public ResponseEntity<?> deleteAnh(@PathVariable("id") String id,@PathVariable("id_image") String id_image){
//        return productService.deleteImage(id,id_image);
//    }

    @DeleteMapping("/products/deleteimage/{id}/{id_image}")
    public ResponseEntity<?> deleteAnh(@PathVariable("id") String id,
                                       @PathVariable("id_image") String id_image){
        return productService.deleteImageProduct(id,id_image);
    }

    @PostMapping(path = "/products/uploadimage/{id}")
    public ResponseEntity<?> updateImage (@PathVariable("id") String id,@RequestParam (value = "url") List<MultipartFile> req){
        return productService.addImagesToProduct(id, req);
    }

    @PutMapping("/products/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id,
                                           @Valid @RequestBody ProductReq req) {
        return productService.updateProduct(id, req);
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
        return productService.deactivatedProduct(id);
    }

    @PutMapping("/products/setstateenable/{id}")
    public ResponseEntity<?> setStateProduct(@PathVariable("id") String id) {return productService.updateStateProduct(id);}

    @DeleteMapping("/products/destroy/{id}")
    public ResponseEntity<?> destroyProduct(@PathVariable("id") String id) {
        return productService.destroyProduct(id);
    }
}
