package com.uberpets.library.rest;

public class Query {
    public final String string;

    public static class Path extends Query {
        Path(String path) {
            super(path);
        }

        public Query and(String key, Object value) {
            assertNotBlank(key);
            if (value == null)
                return this;

            return new Query(string + "?" + key + "=" + value);
        }
    }


    private Query(String path) {
        this.string = path;
    }

    public Query and(String key, Object value) {
        assertNotBlank(key);
        if (value == null)
            return this;

        return new Query(string + "&" + key + "=" + value);
    }

    private static void assertNotBlank(String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Query params cant be empty");
        }
    }

    public static Query query(String key, Object value) {
        return new Path("?").and(key, value);
    }

    public static Query query(String path) {
        return new Path(path);
    }

}
