package tobyspring.hellospring.payment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTest {
    Clock clock;

    @BeforeEach
    void beforeEach() {
        this.clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    @Test
    void convertedAmount() throws IOException {
        testPayment(valueOf(500), valueOf(5_000), clock);
        testPayment(valueOf(1_000), valueOf(10_000), clock);
        testPayment(valueOf(3_000), valueOf(30_000), clock);
    }

    @Test
    void validUntil() throws IOException {
        PaymentService paymentService = new PaymentService(new ExRateProviderStub(valueOf(1_000)), clock);

        Payment payment = paymentService.prepare(1L, "USD", TEN);

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expectedValidUntil = now.plusMinutes(30);

        Assertions.assertThat(payment.getValidUntil().isEqual(expectedValidUntil));
    }

    private static void testPayment(BigDecimal exRate, BigDecimal convertedAmount, Clock clock) throws IOException {
        PaymentService paymentService = new PaymentService(new ExRateProviderStub(exRate), clock);

        Payment payment = paymentService.prepare(1L, "USD", TEN);

        // 환율 정보를 가져온다
        assertThat(payment.getExRate()).isEqualByComparingTo(exRate);

        // 원화 환산 금액 계산
        assertThat(payment.getConvertedAmount()).isEqualByComparingTo(payment.getExRate().multiply(payment.getForeignCurrencyAmount()));
        assertThat(payment.getConvertedAmount()).isEqualByComparingTo(convertedAmount);
    }
}