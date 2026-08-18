package com.example.CanchaSystem.api;

public class Params {
    private final StringBuilder str;

    public Params() {
        this.str = new StringBuilder();
    }

    public static Params of(String ... values) {
        return new Params().add(values);
    }

    private void addParam(String key, String param) {
        if (!str.isEmpty()) {
            str.append('&');
        }
        str.append(key)
                .append('=')
                .append(param);
    }

    public Params add(String key, String param) {
        addParam(key, param);
        return this;
    }

    public Params add(String ... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Parámetros de URL incompletos");
        }
        for (int i = 0; i < values.length - 1; i += 2) {
            addParam(values[i], values[i + 1]);
        }
        return this;
    }

    @Override
    public String toString() {
        return new String(str);
    }
}
