/*
 * Copyright (C) 2015 Loopin Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.antoniovm.util;

/*
 * Created by Antonio Vicente Martin on 03/05/15.
 *
 * Loopin Software. All rights reserved.
 */
public class Math {

    /**
     * Increments the value and return the modulus operation
     *
     * @param value     The value to increment
     * @param modValue  The top range value
     * @param increment The top range value
     * @return The incremented value modulus modValue
     */
    public static int increaseMod(int value, int increment, int modValue) {
        return increaseMod(value, increment, modValue, true);
    }

    /**
     * Increments the value and return the modulus operation
     *
     * @param value       The value to increment
     * @param modValue    The top range value
     * @param increment   The top range value
     * @param positiveMod Whether the modulus will be positive
     * @return The incremented value modulus modValue
     */
    public static int increaseMod(int value, int increment, int modValue, boolean positiveMod) {
        int incremented = (value + increment) % modValue;
        return (positiveMod && increment < 0) ? incremented + modValue : incremented;
    }

    /**
     * Increments the value and return the modulus operation
     *
     * @param value    The value to increment
     * @param modValue The top range value
     * @return The incremented value modulus modValue
     */
    public static int increaseMod(int value, int modValue) {
        return increaseMod(value, 1, modValue);
    }

    /**
     * Decrements the value and return the modulus operation
     *
     * @param value    The value to increment
     * @param modValue The top range value
     * @return The incremented value modulus modValue
     */
    public static int decreaseMod(int value, int modValue) {
        return increaseMod(value, -1, modValue);
    }

}
