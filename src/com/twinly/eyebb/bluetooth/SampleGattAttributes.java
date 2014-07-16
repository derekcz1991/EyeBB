/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.twinly.eyebb.bluetooth;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "0000C004-0000-1000-8000-00805f9b34fb";
   // public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "D509EE05-E5A1-D507-0DF6-F4731CDD53D7";
    
   // public static String CLIENT_CHARACTERISTIC_CONFIG = "0xE2C56DB5-DFFB-48D2-B060-D0F5A71096E0";
    static {
        // Sample Services.
//        attributes.put("180a", "Device Information");
//        attributes.put("1803", "Link Loss");
//        attributes.put("1802", "Immediate Alert");
//        attributes.put("1804", "Tx Power");
//        attributes.put("180f", "Battery Service");
//        attributes.put("2a19", "Battery Life");
//        attributes.put("ffa0", "RealTag APIS");
//        attributes.put("ffb0", "Pair Key");
//        attributes.put("ffb1", "Major & Minor ID");
//        attributes.put("ffb2", "iBeacon UUID");
//        attributes.put("ffb3", "Advertising Interval");
//        attributes.put("ffb4", "Device ID");
//        attributes.put("ffb5", "Deployment mode");
//        attributes.put("ffb6", "MPU6050 Sensor Data");
//        attributes.put("ffb7", "BMP180 Sensor Data");
//        attributes.put("ffb8", "Tx Power");
        
        
        attributes.put("180a", "Device Information");
        attributes.put("2a23", "System ID");
        attributes.put("2a24", "Model Number String");
        attributes.put("2a25", "Serial Number String");
        attributes.put("2a26", "Firmware Revision String");
        attributes.put("2a27", "Hardware Revision String");
        attributes.put("2a28", "Software Revision String");
        attributes.put("2a29", "Manufacturer Name String");
        attributes.put("2a2a", "IEEE");
        attributes.put("2a50", "PhP ID");
        attributes.put("ffb3", "Advertising Interval");
        attributes.put("ffb4", "Device ID");
        attributes.put("ffb5", "Deployment mode");
        attributes.put("ffb6", "MPU6050 Sensor Data");
        attributes.put("ffb7", "BMP180 Sensor Data");
        attributes.put("ffb8", "Tx Power");
    }
    
    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

 