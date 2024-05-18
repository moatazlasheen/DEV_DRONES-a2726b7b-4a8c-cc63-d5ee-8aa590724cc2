package com.example.demo.converter;

import org.modelmapper.AbstractConverter;

import java.util.Base64;

public class Base64ToByteArrayConverter extends AbstractConverter<String, byte[]> {

    @Override
    protected byte[] convert(final String s) {
        return Base64.getDecoder().decode(s);
    }
}
