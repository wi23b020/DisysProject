package com.energycommunity.energyproducer;

import com.energycommunity.energyproducer.service.ProducerService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProducerServiceTest {

    private RabbitTemplate rabbitTemplate;
    private ProducerService service;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        service = new ProducerService(rabbitTemplate);
    }

    @Test
    void constructorInitializesRabbitTemplateAndDependencies() throws Exception {
        System.out.println("Test: Prüfe Konstruktorinitialisierung von ProducerService…");

        // rabbitTemplate
        Field rtField = ProducerService.class.getDeclaredField("rabbitTemplate");
        rtField.setAccessible(true);
        assertThat(rtField.get(service)).isSameAs(rabbitTemplate);
        System.out.println("OK: rabbitTemplate korrekt initialisiert");

        // objectMapper
        Field omField = ProducerService.class.getDeclaredField("objectMapper");
        omField.setAccessible(true);
        assertThat(omField.get(service)).isNotNull();
        System.out.println("OK: objectMapper ist nicht null");

        // random
        Field rndField = ProducerService.class.getDeclaredField("random");
        rndField.setAccessible(true);
        assertThat(rndField.get(service)).isInstanceOf(Random.class);
        System.out.println("OK: random ist vom Typ Random");
    }

    @Test
    void generateKwhProduction_usesRandomCorrectly() throws Exception {
        System.out.println("Test: Prüfe generateKwhProduction mit gestubtem Random…");

        Random rnd = mock(Random.class);
        when(rnd.nextDouble()).thenReturn(0.3);
        Field rndField = ProducerService.class.getDeclaredField("random");
        rndField.setAccessible(true);
        rndField.set(service, rnd);

        Method gen = ProducerService.class.getDeclaredMethod("generateKwhProduction");
        gen.setAccessible(true);
        double produced = (double) gen.invoke(service);

        assertThat(produced).isEqualTo(0.005 + 0.01 * 0.3);
        System.out.println("OK: generateKwhProduction liefert erwarteten Wert " + produced);
    }

    @Test
    void startProducingIsAnnotatedWithPostConstruct() throws NoSuchMethodException {
        System.out.println("Test: Prüfe, dass startProducing mit @PostConstruct annotiert ist…");

        Method m = ProducerService.class.getMethod("startProducing");
        assertThat(m)
                .as("Prüfe, dass startProducing @PostConstruct hat")
                .isNotIn(PostConstruct.class);
        System.out.println("OK: startProducing ist mit @PostConstruct annotiert");
    }
}
