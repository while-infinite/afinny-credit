package by.afinny.credit.controller;

import by.afinny.credit.dto.ProductDto;
import by.afinny.credit.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth/credit-products")
@Slf4j
public class ProductController {

    public static final String URL_CREDIT_PRODUCTS = "/auth/credit-products/";

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> productDto = productService.getProducts();
        return ResponseEntity.ok(productDto);
    }
}
