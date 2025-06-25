package com.energycommunity.energyproducer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class EnergyProducerApplicationTest {

    @Test
    void hasSpringBootApplicationAnnotation() {
        System.out.println("Test: Prüfe, ob @SpringBootApplication auf der Klasse sitzt…");
        assertThat(EnergyProducerApplication.class)
                .hasAnnotation(SpringBootApplication.class);
        System.out.println("OK: @SpringBootApplication ist vorhanden");
    }

    @Test
    void mainMethodExistsAndIsStaticVoid() throws NoSuchMethodException {
        System.out.println("Test: Prüfe, dass main(String[] args) existiert und static void ist…");
        Method main = EnergyProducerApplication.class.getMethod("main", String[].class);

        // Prüfen auf static
        assertThat(Modifier.isStatic(main.getModifiers()))
                .withFailMessage("Erwarte, dass main static ist")
                .isTrue();
        System.out.println("OK: main ist static");

        // Prüfen auf Rückgabetyp void
        assertThat(main.getReturnType())
                .withFailMessage("Erwarte, dass main void zurückgibt")
                .isEqualTo(void.class);
        System.out.println("OK: main gibt void zurück");
    }
}
