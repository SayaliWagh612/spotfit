package com.eklavya.spotfit.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Reusable utility for converting Java values to and from Firestore REST API typed JSON format.
 */
public final class FirestoreDocumentUtil {

    private FirestoreDocumentUtil() {
    }

    public static JSONObject stringValue(String value) {
        JSONObject obj = new JSONObject();
        if (value != null) {
            obj.put("stringValue", value);
        } else {
            obj.put("nullValue", JSONObject.NULL);
        }
        return obj;
    }

    public static JSONObject booleanValue(boolean value) {
        return new JSONObject().put("booleanValue", value);
    }

    public static JSONObject integerValue(long value) {
        return new JSONObject().put("integerValue", String.valueOf(value));
    }

    public static JSONObject doubleValue(double value) {
        return new JSONObject().put("doubleValue", value);
    }

    public static JSONObject mapValue(JSONObject fields) {
        JSONObject mapVal = new JSONObject();
        mapVal.put("fields", fields != null ? fields : new JSONObject());
        return new JSONObject().put("mapValue", mapVal);
    }

    public static JSONObject stringArrayValue(List<String> values) {
        JSONObject arrayValueObj = new JSONObject();
        JSONArray valuesArray = new JSONArray();

        if (values != null) {
            for (String val : values) {
                if (val != null) {
                    valuesArray.put(stringValue(val));
                }
            }
        }
        arrayValueObj.put("values", valuesArray);
        return new JSONObject().put("arrayValue", arrayValueObj);
    }

    public static String getString(JSONObject fields, String fieldName) {
        if (fields == null || !fields.has(fieldName)) {
            return null;
        }
        JSONObject fieldObj = fields.optJSONObject(fieldName);
        if (fieldObj == null || fieldObj.has("nullValue")) {
            return null;
        }
        return fieldObj.optString("stringValue", null);
    }

    public static boolean getBoolean(JSONObject fields, String fieldName, boolean defaultValue) {
        if (fields == null || !fields.has(fieldName)) {
            return defaultValue;
        }
        JSONObject fieldObj = fields.optJSONObject(fieldName);
        if (fieldObj == null) {
            return defaultValue;
        }
        if (fieldObj.has("booleanValue")) {
            Object raw = fieldObj.opt("booleanValue");
            if (raw instanceof Boolean) {
                return (Boolean) raw;
            } else if (raw instanceof String) {
                return Boolean.parseBoolean(((String) raw).trim());
            }
            return fieldObj.optBoolean("booleanValue", defaultValue);
        } else if (fieldObj.has("stringValue")) {
            String str = fieldObj.optString("stringValue", null);
            if (str != null) {
                return Boolean.parseBoolean(str.trim());
            }
        } else if (fieldObj.has("integerValue")) {
            long val = getLong(fields, fieldName, 0L);
            return val != 0;
        }
        return defaultValue;
    }

    public static long getLong(JSONObject fields, String fieldName, long defaultValue) {
        if (fields == null || !fields.has(fieldName)) {
            return defaultValue;
        }
        JSONObject fieldObj = fields.optJSONObject(fieldName);
        if (fieldObj == null) {
            return defaultValue;
        }

        if (fieldObj.has("integerValue")) {
            Object raw = fieldObj.opt("integerValue");
            if (raw instanceof Number) {
                return ((Number) raw).longValue();
            } else if (raw instanceof String) {
                try {
                    return Long.parseLong(((String) raw).trim());
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        } else if (fieldObj.has("stringValue")) {
            String str = fieldObj.optString("stringValue", null);
            if (str != null) {
                try {
                    return Long.parseLong(str.trim());
                } catch (NumberFormatException ignored) {}
            }
        } else if (fieldObj.has("doubleValue")) {
            return (long) fieldObj.optDouble("doubleValue", defaultValue);
        }

        return defaultValue;
    }

    public static int getInt(JSONObject fields, String fieldName, int defaultValue) {
        return (int) getLong(fields, fieldName, defaultValue);
    }

    public static double getDouble(JSONObject fields, String fieldName, double defaultValue) {
        if (fields == null || !fields.has(fieldName)) {
            return defaultValue;
        }
        JSONObject fieldObj = fields.optJSONObject(fieldName);
        if (fieldObj == null) {
            return defaultValue;
        }

        if (fieldObj.has("doubleValue")) {
            return fieldObj.optDouble("doubleValue", defaultValue);
        } else if (fieldObj.has("integerValue")) {
            Object raw = fieldObj.opt("integerValue");
            if (raw instanceof Number) {
                return ((Number) raw).doubleValue();
            } else if (raw instanceof String) {
                try {
                    return Double.parseDouble((String) raw);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
        }

        return defaultValue;
    }

    public static JSONObject getMapFields(JSONObject fields, String fieldName) {
        if (fields == null || !fields.has(fieldName)) {
            return new JSONObject();
        }
        JSONObject fieldObj = fields.optJSONObject(fieldName);
        if (fieldObj == null || !fieldObj.has("mapValue")) {
            return new JSONObject();
        }
        JSONObject mapVal = fieldObj.optJSONObject("mapValue");
        if (mapVal == null || !mapVal.has("fields")) {
            return new JSONObject();
        }
        JSONObject mapFields = mapVal.optJSONObject("fields");
        return mapFields != null ? mapFields : new JSONObject();
    }

    public static List<String> getStringList(JSONObject fields, String fieldName) {
        List<String> list = new ArrayList<>();
        if (fields == null || !fields.has(fieldName)) {
            return list;
        }
        JSONObject fieldObj = fields.optJSONObject(fieldName);
        if (fieldObj == null || !fieldObj.has("arrayValue")) {
            return list;
        }

        JSONObject arrayVal = fieldObj.optJSONObject("arrayValue");
        if (arrayVal == null || !arrayVal.has("values")) {
            return list;
        }

        JSONArray valuesArray = arrayVal.optJSONArray("values");
        if (valuesArray == null) {
            return list;
        }

        for (int i = 0; i < valuesArray.length(); i++) {
            JSONObject item = valuesArray.optJSONObject(i);
            if (item != null && item.has("stringValue")) {
                String str = item.optString("stringValue", null);
                if (str != null) {
                    list.add(str);
                }
            }
        }

        return list;
    }

    public static JSONObject createDocument(JSONObject fields) {
        JSONObject doc = new JSONObject();
        doc.put("fields", (fields != null) ? fields : new JSONObject());
        return doc;
    }

    public static com.eklavya.spotfit.model.OperationResult<JSONObject> parseFirestoreDocument(JSONObject document) {
        if (document == null || !document.has("fields")) {
            return com.eklavya.spotfit.model.OperationResult.failure(
                    com.eklavya.spotfit.constants.OperationCode.INVALID_RESPONSE,
                    "Expected a Firestore document response."
            );
        }
        JSONObject fields = document.optJSONObject("fields");
        if (fields == null) {
            return com.eklavya.spotfit.model.OperationResult.failure(
                    com.eklavya.spotfit.constants.OperationCode.INVALID_RESPONSE,
                    "Expected a Firestore document response."
            );
        }
        return com.eklavya.spotfit.model.OperationResult.success(fields);
    }

    public static JSONObject getFields(JSONObject document) {
        if (document == null || !document.has("fields")) {
            return new JSONObject();
        }
        JSONObject fields = document.optJSONObject("fields");
        return (fields != null) ? fields : new JSONObject();
    }

    public static JSONObject toFirestoreValueMap(JSONObject plainJson) {
        JSONObject fields = new JSONObject();
        if (plainJson == null) {
            return fields;
        }
        for (String key : plainJson.keySet()) {
            Object val = plainJson.get(key);
            fields.put(key, toFirestoreTypedValue(val));
        }
        return fields;
    }

    public static JSONObject toFirestoreTypedValue(Object val) {
        if (val == null || val == JSONObject.NULL) {
            return new JSONObject().put("nullValue", JSONObject.NULL);
        } else if (val instanceof Boolean) {
            return booleanValue((Boolean) val);
        } else if (val instanceof Integer || val instanceof Long) {
            return integerValue(((Number) val).longValue());
        } else if (val instanceof Double || val instanceof Float) {
            return doubleValue(((Number) val).doubleValue());
        } else if (val instanceof String) {
            return stringValue((String) val);
        } else if (val instanceof java.util.Collection<?>) {
            java.util.Collection<?> col = (java.util.Collection<?>) val;
            JSONObject arrayValueObj = new JSONObject();
            JSONArray valuesArray = new JSONArray();
            for (Object item : col) {
                valuesArray.put(toFirestoreTypedValue(item));
            }
            arrayValueObj.put("values", valuesArray);
            return new JSONObject().put("arrayValue", arrayValueObj);
        } else if (val instanceof JSONArray) {
            JSONArray arr = (JSONArray) val;
            JSONObject arrayValueObj = new JSONObject();
            JSONArray valuesArray = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                valuesArray.put(toFirestoreTypedValue(arr.get(i)));
            }
            arrayValueObj.put("values", valuesArray);
            return new JSONObject().put("arrayValue", arrayValueObj);
        } else if (val instanceof JSONObject) {
            JSONObject obj = (JSONObject) val;
            if (obj.has("stringValue") || obj.has("integerValue") || obj.has("booleanValue") || obj.has("arrayValue") || obj.has("mapValue")) {
                return obj;
            }
            JSONObject mapValueObj = new JSONObject();
            mapValueObj.put("fields", toFirestoreValueMap(obj));
            return new JSONObject().put("mapValue", mapValueObj);
        }
        return stringValue(val.toString());
    }
}
