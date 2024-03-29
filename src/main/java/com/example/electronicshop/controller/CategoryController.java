package com.example.electronicshop.controller;

import com.example.electronicshop.communication.request.CategoryRequest;
import com.example.electronicshop.models.ResponseObject;
import com.example.electronicshop.notification.AppException;
import com.example.electronicshop.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping(path = "/categories/{id}")
    public ResponseEntity<ResponseObject> findCategoryById (@PathVariable("id") String id){
        return categoryService.findCategoryById(id);
    }
    @GetMapping(path = "/admin/manage/categories")
    public ResponseEntity<ResponseObject> findAll (){
        return categoryService.findAll();
    }
    @GetMapping(path = "/categories")
    public ResponseEntity<ResponseObject> findAllCategory (){
        return categoryService.findAllCategory();
    }
    @PostMapping(path = "/admin/manage/categories")
    public ResponseEntity<ResponseObject> addCategory (@RequestBody CategoryRequest req){
        return categoryService.createCategory(req);
    }
    @GetMapping(path = "/categories/admin/filterstate/{state}")
    public ResponseEntity<?> filterStateCategory (@PathVariable("state") String state){
//        if (query.isEmpty() || query.matches(".*[%<>&;'\0-].*"))
//            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid keyword");
        return categoryService.filterState(state);
    }
    @PutMapping(path = "/admin/manage/categories/{id}")
    public ResponseEntity<ResponseObject> updateCategory (@PathVariable("id") String id,
                                                          @RequestBody CategoryRequest req){
        return categoryService.updateCategory(id, req);
    }

//    @PostMapping(path = "/admin/manage/categories/uploadimage/{id}")
//    public ResponseEntity<?> updateCategoryImage (@PathVariable("id") String id, @RequestParam (value = "categoryimage") MultipartFile file){
//        return categoryService.updateCateogryImage(id, file);
//    }



    @DeleteMapping(path = "/admin/manage/categories/{id}")
    public ResponseEntity<ResponseObject> deleteCategory (@PathVariable("id") String id){
        return categoryService.deleteCategory(id);
    }

}
