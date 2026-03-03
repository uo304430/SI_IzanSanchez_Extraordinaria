package Izan.test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import Izan_33805.*;
import java.util.List;

public class TestHistorial {
    private HistorialModel model;

    @BeforeEach
    public void setUp() {
        model = new HistorialModel();
        // Aquí podrías resetear la base de datos si fuera necesario
    }

    @Test
    public void testGetHistorialExistente() {
        // Verifica que el método devuelve una lista (aunque esté vacía al principio)
        List<HistorialDTO> lista = model.getHistorialPorId(1);
        assertNotNull(lista);
    }
}
