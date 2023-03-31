package by.afinny.credit.mapper;

import by.afinny.credit.dto.ProductDto;
import by.afinny.credit.entity.Product;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    List<ProductDto> productsToProductsDto(List<Product> products);
}
