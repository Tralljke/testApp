package com.petrbel.shop.service;

import com.petrbel.shop.model.dto.SearchRequestDto;
import com.petrbel.shop.model.dto.StatRequestDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class JSONMapper {

    public List<SearchRequestDto> searchParser(JSONObject jsonObject) {
        JSONArray criterias = (JSONArray) jsonObject.get("criterias");
        List<SearchRequestDto> searchList = new ArrayList<>();
        JSONObject object = (JSONObject)criterias.get(0);
        Set fieldSet = object.keySet();
        fieldSet.forEach(item -> {
            searchList.add(new SearchRequestDto((String)item, (String)object.get(item)));
        });
        return searchList;
    }

    public StatRequestDto statParser(JSONObject jsonObject) {
        StatRequestDto statRequestDto = new StatRequestDto();
        statRequestDto.setStartDate((LocalDate)jsonObject.get("startDate"));
        statRequestDto.setEndDate((LocalDate)jsonObject.get("endDate"));
        return statRequestDto;
    }

}
