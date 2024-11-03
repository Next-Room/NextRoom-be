package com.nextroom.nextRoomServer.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextroom.nextRoomServer.exceptions.CustomException;
import com.nextroom.nextRoomServer.exceptions.StatusCode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CommonObjectMapper {

    private static final CommonObjectMapper DEFAULT = new CommonObjectMapper(new ObjectMapper());

    ObjectMapper objectMapper;

    public static CommonObjectMapper getInstance() {
        return DEFAULT;
    }

    public String writeValueAsString(Object value) {
        try {
            return this.objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CustomException(StatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public <T> T readValue(Object value, Class<T> clazz) {
        return readValue(value, objectMapper.getTypeFactory().constructType(clazz));
    }

    public <T> T readEncodedValue(String value, Class<T> clazz) {
        String decodedData = Base64Decoder.decode(value);
        return readValue(decodedData, objectMapper.getTypeFactory().constructType(clazz));
    }

    public <T> T readValue(Object value, TypeReference<T> typeReference) {
        return readValue(value, objectMapper.getTypeFactory().constructType(typeReference));
    }

    public <T> T readValue(Object value, JavaType javaType) {
        try {
            if (value instanceof String str) {
                return objectMapper.readValue(str, javaType);
            }
            if (value instanceof byte[] bytes) {
                return objectMapper.readValue(bytes, javaType);
            }
            if (value instanceof File file) {
                return objectMapper.readValue(file, javaType);
            }
            if (value instanceof InputStream is) {
                return objectMapper.readValue(is, javaType);
            }
            if (value instanceof JsonParser parser) {
                return objectMapper.readValue(parser, javaType);
            }
            if (value instanceof Reader reader) {
                return objectMapper.readValue(reader, javaType);
            }
            if (value instanceof URL url) {
                return objectMapper.readValue(url, javaType);
            }

            throw new IllegalArgumentException("not support value type : " + value.getClass().getSimpleName());
        } catch (IOException e) {
            throw new CustomException(StatusCode.SERIALIZE_ERROR, e.getMessage());
        }
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType) {
        try {
            return objectMapper.convertValue(fromValue, toValueType);
        } catch (Exception e) {
            throw new CustomException(StatusCode.SERIALIZE_ERROR, e.getMessage());
        }
    }

    public <T> T convertValue(Object fromValue, TypeReference<T> typeReference) {
        try {
            return objectMapper.convertValue(fromValue, typeReference);
        } catch (Exception e) {
            throw new CustomException(StatusCode.SERIALIZE_ERROR, e.getMessage());
        }
    }

    public String toPrettyJsonString(Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (Exception e) {
            throw new CustomException(StatusCode.SERIALIZE_ERROR, e.getMessage());
        }
    }

    public String beautify(String jsonString) {
        JsonNode readTree;
        try {
            readTree = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new CustomException(StatusCode.SERIALIZE_ERROR, e.getMessage());
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(readTree);
        } catch (JsonProcessingException e) {
            throw new CustomException(StatusCode.SERIALIZE_ERROR, e.getMessage());
        }
    }

    public boolean canSerialize(Class<?> clazz) {
        return objectMapper.canSerialize(clazz);
    }

    public boolean canDeserialize(Type type) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        return objectMapper.canDeserialize(javaType);
    }
}
