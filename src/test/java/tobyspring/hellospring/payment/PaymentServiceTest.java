package tobyspring.hellospring.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.*;

class PaymentServiceTest {

    @Test
    @DisplayName("prepare 메소드가 요구사항 3가지를 잘 충족했는지 검증")
    void convertedAmount() throws IOException {
        getPayment(valueOf(500), valueOf(5_000));
        getPayment(valueOf(1_000), valueOf(10_000));
        getPayment(valueOf(3_000), valueOf(30_000));

        // 원화 환산 금액 유효시간 계산
//        assertThat(payment.getValidUntil()).isAfter(LocalDateTime.now());
//        assertThat(payment.getValidUntil()).isBefore(LocalDateTime.now().plusMinutes(30));

    }

    private static void getPayment(BigDecimal exRate, BigDecimal convertedAmount) throws IOException {
        PaymentService paymentService = new PaymentService(new ExRateProviderStub(exRate));

        Payment payment = paymentService.prepare(1L, "USD", TEN);

        // 환율 정보를 가져온다
        assertThat(payment.getExRate()).isEqualByComparingTo(exRate);

        // 원화 환산 금액 계산
        assertThat(payment.getConvertedAmount()).isEqualByComparingTo(payment.getExRate().multiply(payment.getForeignCurrencyAmount()));
        assertThat(payment.getConvertedAmount()).isEqualByComparingTo(convertedAmount);
    }
}