package com.intellectualsites.arkitektonika;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestArkitektonika {

    private static String url;
    private static String uploadKey;
    private static String deletionKey;

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

    @Test @Order(1) public void testUpload() throws Exception {
        final Arkitektonika arkitektonika = Arkitektonika.builder().withUrl(url).build();
        final SchematicKeys schematicKeys = arkitektonika.upload(new File("src/test/resources/test.schem")).get();
        assertNotNull(schematicKeys);
        assertFalse(schematicKeys.getAccessKey().isEmpty());
        uploadKey = schematicKeys.getAccessKey();
        deletionKey = schematicKeys.getDeletionKey();
    }

    @Test @Order(2) public void testExistence() throws Exception {
        final Arkitektonika arkitektonika = Arkitektonika.builder().withUrl(url).build();
        assertEquals(ResourceStatus.OK, arkitektonika.checkStatus(uploadKey).get());
    }

    @Test @Order(3) public void testDownload() throws Exception {
        final Arkitektonika arkitektonika = Arkitektonika.builder().withUrl(url).build();
        assertTrue(arkitektonika.download(uploadKey).get().getContent().length > 0);
    }

    @Test @Order(4) public void testDeletion() throws Exception {
        final Arkitektonika arkitektonika = Arkitektonika.builder().withUrl(url).build();
        assertTrue(arkitektonika.delete(deletionKey).get());
    }

}
