/*
 * MIT License
 *
 * Copyright (c) 2023 IntellectualSites
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
