package by.afinny.credit.service.impl;

import by.afinny.credit.dto.ProductDto;
import by.afinny.credit.entity.Product;
import by.afinny.credit.mapper.ProductMapper;
import by.afinny.credit.repository.ProductRepository;
import by.afinny.credit.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productDto;

    @Override
    public List<ProductDto> getProducts() {
        log.info("getProducts() method is invoked");

        List<Product> products = productRepository.findAll();
        return productDto.productsToProductsDto(products);
    }
}

