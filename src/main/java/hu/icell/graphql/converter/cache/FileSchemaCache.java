/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter.cache;

import java.io.*;
import java.util.Scanner;

public class FileSchemaCache implements SchemaCache {
    @Override
    public String getItem(String cacheKey) throws FileNotFoundException {
        assert (cacheKey != null && !cacheKey.isEmpty());
        File folder = new File("schemas");
        if (!folder.exists()) return null;
        File schemaFolder = new File(folder, cacheKey);
        if (!schemaFolder.exists()) return null;
        StringBuilder schema = new StringBuilder();
        File schemaFile = new File(schemaFolder, "schema.gql");
        if (schemaFile.exists()) {
            Scanner scanner = new Scanner(schemaFile);
            while (scanner.hasNextLine()) {
                schema.append(scanner.nextLine());
                schema.append("\n");
            }
            return schema.toString();
        }
        return null;
    }

    @Override
    public void setItem(String cacheKey, String content) throws IOException {
        assert (cacheKey != null && !cacheKey.isEmpty());
        File folder = new File("schemas");
        if(!folder.exists()) if (!folder.mkdir()) return;
        File schemaFolder = new File(folder, cacheKey);
        if(!schemaFolder.exists()) if(!schemaFolder.mkdir()) return;
        File schemaFile = new File(schemaFolder, "schema.gql");
        if(schemaFile.exists()) if(!schemaFile.delete()) return;
        if(!schemaFile.createNewFile()) return;
        BufferedWriter writer = new BufferedWriter(new FileWriter(schemaFile));
        writer.write(content);
        writer.close();
    }
}
