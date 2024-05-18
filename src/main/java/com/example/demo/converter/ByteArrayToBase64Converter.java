package com.example.demo.converter;

import org.modelmapper.AbstractConverter;

import java.util.Base64;

public class ByteArrayToBase64Converter extends AbstractConverter<byte[], String> {

    @Override
    protected String convert(final byte [] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
