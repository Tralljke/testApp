import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.SearchRequest;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import static service.JSONService.*;


public class Main {
    public static void main(String[] args) throws SQLException {

        select(args[0],args[1],args[2]);

    }



}