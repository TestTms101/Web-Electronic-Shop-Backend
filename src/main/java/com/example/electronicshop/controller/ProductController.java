package com.example.electronicshop.controller;

import com.example.electronicshop.communication.request.ProductReq;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.service.ProductService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> findById (@PathVariable("id") String id){
        return productService.findById(id);
    }

    @GetMapping(path = "/category/{id}")
    public ResponseEntity<?> findByCategoryIdAndBrandId (@PathVariable("id") String id,
                                                         @ParameterObject Pageable pageable){
        return productService.findByCategoryIdOrBrandId(id, pageable);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<?> search (@RequestParam("q") String query,
                                     @PageableDefault(sort = "score") @ParameterObject Pageable pageable){
        if (query.isEmpty() || query.matches(".*[%<>&;'\0-].*"))
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid keyword");
        return productService.search(query, pageable);
    }

    @GetMapping(path = "/byState")
    public ResponseEntity<?> findAllByState (@ParameterObject Pageable pageable){
        return productService.findAll(false, pageable);
    }

    @GetMapping(path = "")
    public ResponseEntity<?> findAll (@ParameterObject Pageable pageable){
        return productService.findAll(true,pageable);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@Valid @ModelAttribute ProductReq req) {
        return productService.addProduct(req);}

    @DeleteMapping("/deleteanh/{id}/{id_image}")
    public ResponseEntity<?> deleteAnh(@PathVariable("id") String id,@PathVariable("id_image") String id_image){
        return productService.deleteImage(id,id_image);
    }
    @PostMapping(path = "/uploadimage/{id}")
    public ResponseEntity<?> updateImage (@PathVariable("id") String id,@RequestParam (value = "url") List<MultipartFile> req){
        return productService.updateImage(id, req);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id,
                                           @Valid @RequestBody ProductReq req) {
        return productService.updateProduct(id, req);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
        return productService.deactivatedProduct(id);
    }

    @DeleteMapping("/products/destroy/{id}")
    public ResponseEntity<?> destroyProduct(@PathVariable("id") String id) {
        return productService.destroyProduct(id);
    }

}
