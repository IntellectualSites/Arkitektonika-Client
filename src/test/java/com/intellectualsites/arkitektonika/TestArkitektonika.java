package com.intellectualsites.arkitektonika;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestArkitektonika {

    private static String url;

    @BeforeAll public static void setup() throws Exception {
        final Properties properties = new Properties();
        final File propertiesFile = new File( "src/test/resources/test.properties");
        try (final FileReader fileReader = new FileReader(propertiesFile)) {
            properties.load(fileReader);
        }
        url = properties.getProperty("url");
    }

    @Test public void testConstruction() throws Exception {
        final Arkitektonika arkitektonika = Arkitektonika.builder().withUrl(url).build();
        assertTrue(arkitektonika.isCompatible().get());
    }

}
