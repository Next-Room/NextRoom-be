package com.nextroom.nextRoomServer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.INTERNAL_SERVER_ERROR;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> dataList) {
        try {
            if (dataList == null || dataList.isEmpty()) {
                return null;
            }
            return mapper.writeValueAsString(dataList);
        } catch (JsonProcessingException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String data) {
        try {
            if (data == null || data.isEmpty()) {
                return new ArrayList<>();
            }
            return mapper.readValue(data, List.class);
        } catch (JsonProcessingException e) {
            throw new CustomException(INTERNAL_SERVER_ERROR);
        }
    }
}
