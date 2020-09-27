package com.petrbel.shop;

import com.petrbel.shop.service.JSONMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class TestApplication {
    public static void main(String[] args) {
        JSONMapper JSONMapper = new JSONMapper();
        JSONObject jsonInput = new JSONObject();

        try (FileReader reader = new FileReader(args[1])) {
            JSONParser parser = new JSONParser();
            jsonInput = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        switch (args[0]) {
            case "search": JSONMapper.searchParser(jsonInput);
            case "stat": JSONMapper.statParser(jsonInput);
        }
    }
}
