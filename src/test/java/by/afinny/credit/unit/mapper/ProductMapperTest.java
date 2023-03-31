package by.afinny.credit.unit.mapper;

import by.afinny.credit.dto.ProductDto;
import by.afinny.credit.entity.Product;
import by.afinny.credit.entity.constant.CalculationMode;
import by.afinny.credit.mapper.ProductMapperImpl;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Verification of correct data generation. It will pass if the fields of the entity and dto are equal")
class ProductMapperTest {

    @InjectMocks
    private ProductMapperImpl productMapper;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1)
                .name("productName")
                .minSum(BigDecimal.ONE)
                .maxSum(BigDecimal.TEN)
                .currencyCode("RUB")
                .minInterestRate(BigDecimal.valueOf(214))
                .maxInterestRate(BigDecimal.valueOf(3333))
                .needGuarantees(true)
                .deliveryInCash(false)
                .earlyRepayment(false)
                .needIncomeDetails(false)
                .minPeriodMonths(2)
                .maxPeriodMonths(5)
                .isActive(true)
                .details("details")
                .calculationMode(CalculationMode.ANNUITY)
                .gracePeriodMonths(3)
                .rateIsAdjustable(false)
                .rateBase("0123")
                .rateFixPart(BigDecimal.valueOf(4))
                .increasedRate(BigDecimal.valueOf(66)).build();

        productList = List.of(product).stream()
                .sorted(Comparator.comparing(Product::getId)).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Verification of correct data generation")
    void requestDtoToCreditOrder_checkCorrectMappingData() {
        List<ProductDto> productDtoList = productMapper.productsToProductsDto(productList);
        productDtoList.sort(Comparator.comparing(ProductDto::getId));

        for (int i = 0; i < productList.size(); i++) {
            verifyProduct(productDtoList.get(i), productList.get(i));
        }
    }

    private void verifyProduct(ProductDto productDto, Product product) {
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(productDto.getId())
                    .withFailMessage("Id should be equals")
                    .isEqualTo(product.getId());
            softAssertions.assertThat(productDto.getName())
                    .withFailMessage("Name should be equals")
                    .isEqualTo(product.getName());
            softAssertions.assertThat(productDto.getMinSum())
                    .withFailMessage("MinSum should be equals")
                    .isEqualTo(product.getMinSum());
            softAssertions.assertThat(productDto.getMaxSum())
                    .withFailMessage("MaxSum should be equals")
                    .isEqualTo(product.getMaxSum());
            softAssertions.assertThat(productDto.getCurrencyCode())
                    .withFailMessage("CurrencyCode should be equals")
                    .isEqualTo(product.getCurrencyCode());
            softAssertions.assertThat(productDto.getMinInterestRate())
                    .withFailMessage("MinInterestRate should be equals")
                    .isEqualTo(product.getMinInterestRate());
            softAssertions.assertThat(productDto.getMaxInterestRate())
                    .withFailMessage("MaxInterestRate should be equals")
                    .isEqualTo(product.getMaxInterestRate());
            softAssertions.assertThat(productDto.getNeedGuarantees())
                    .withFailMessage("NeedGuarantees should be equals")
                    .isEqualTo(product.getNeedGuarantees());
            softAssertions.assertThat(productDto.getDeliveryInCash())
                    .withFailMessage("DeliveryInCash should be equals")
                    .isEqualTo(product.getDeliveryInCash());
            softAssertions.assertThat(productDto.getEarlyRepayment())
                    .withFailMessage("EarlyRepayment should be equals")
                    .isEqualTo(product.getEarlyRepayment());
            softAssertions.assertThat(productDto.getNeedIncomeDetails())
                    .withFailMessage("NeedIncomeDetails should be equals")
                    .isEqualTo(product.getNeedIncomeDetails());
            softAssertions.assertThat(productDto.getMinPeriodMonths())
                    .withFailMessage("MinPeriodMonths should be equals")
                    .isEqualTo(product.getMinPeriodMonths());
            softAssertions.assertThat(productDto.getMaxPeriodMonths())
                    .withFailMessage("MaxPeriodMonths should be equals")
                    .isEqualTo(product.getMaxPeriodMonths());
            softAssertions.assertThat(productDto.getIsActive())
                    .withFailMessage("IsActive should be equals")
                    .isEqualTo(product.getIsActive());
            softAssertions.assertThat(productDto.getDetails())
                    .withFailMessage("Details should be equals")
                    .isEqualTo(product.getDetails());
            softAssertions.assertThat(productDto.getCalculationMode())
                    .withFailMessage("CalculationMode should be equals")
                    .isEqualTo(product.getCalculationMode());
            softAssertions.assertThat(productDto.getGracePeriodMonths())
                    .withFailMessage("GracePeriodMonths should be equals")
                    .isEqualTo(product.getGracePeriodMonths());
            softAssertions.assertThat(productDto.getRateIsAdjustable())
                    .withFailMessage("RateIsAdjustable should be equals")
                    .isEqualTo(product.getRateIsAdjustable());
            softAssertions.assertThat(productDto.getRateBase())
                    .withFailMessage("RateBase should be equals")
                    .isEqualTo(product.getRateBase());
            softAssertions.assertThat(productDto.getRateFixPart())
                    .withFailMessage("RateFixPart should be equals")
                    .isEqualTo(product.getRateFixPart());
            softAssertions.assertThat(productDto.getIncreasedRate())
                    .withFailMessage("IncreasedRate should be equals")
                    .isEqualTo(product.getIncreasedRate());
        });
    }
}