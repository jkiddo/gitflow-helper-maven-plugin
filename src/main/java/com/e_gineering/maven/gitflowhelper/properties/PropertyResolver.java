package com.e_gineering.maven.gitflowhelper.properties;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Properties;

/**
 * Shamelessly cribbed from https://raw.githubusercontent.com/mojohaus/properties-maven-plugin/master/src/main/java/org/codehaus/mojo/properties/PropertyResolver.java
 */
public class PropertyResolver {

    public static String resolveValue(String value, Properties properties, Properties environment) {
        return resolveValue(new ExpansionBuffer(value), new CircularDefinitionPreventer(), properties, environment);
    }

    private static String resolveValue(ExpansionBuffer buffer, CircularDefinitionPreventer circularDefinitionPreventer, Properties properties, Properties environment) {
        while (buffer.hasMoreLegalPlaceholders()) {
            String newKey = buffer.extractPropertyKey();
            String newValue = fromPropertiesThenSystemThenEnvironment(newKey, properties, environment);

            circularDefinitionPreventer.visited(newKey, newValue);

            buffer.add(newKey, newValue);
        }

        return buffer.toString();

    }

    private static String fromPropertiesThenSystemThenEnvironment(String key, Properties properties, Properties environment) {
        String value = properties.getProperty(key);

        // try global environment
        if (value == null) {
            value = System.getProperty(key);
        }

        // try environment variable
        if (value == null && key.startsWith("env.") && environment != null) {
            value = environment.getProperty(key.substring(4));
        }

        return value;
    }
}