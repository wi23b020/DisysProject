package org.example.energyuser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class EnergyUserApplicationTest {

    @Test
    void hasSpringBootApplicationAnnotation() {
        System.out.println("Test: Prüfe, ob @SpringBootApplication auf der Klasse sitzt…");
        assertThat(EnergyUserApplication.class)
                .hasAnnotation(SpringBootApplication.class);
        System.out.println("OK: @SpringBootApplication ist vorhanden");
    }

    @Test
    void hasEnableSchedulingAnnotation() {
        System.out.println("Test: Prüfe, ob @EnableScheduling auf der Klasse sitzt…");
        assertThat(EnergyUserApplication.class)
                .hasAnnotation(EnableScheduling.class);
        System.out.println("OK: @EnableScheduling ist vorhanden");
    }

    @Test
    void mainMethodExistsAndIsStaticVoid() throws NoSuchMethodException {
        System.out.println("Test: Prüfe, dass main(String[] args) existiert und static void ist…");
        Method main = EnergyUserApplication.class.getMethod("main", String[].class);

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
