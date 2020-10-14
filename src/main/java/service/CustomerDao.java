package service;

import dao.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDao {

    static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/shopDB";
    static final String USER = "postgres";
    static final String PASSWORD = "2295300";

    private static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

    private static List<Customer> toPojo(ResultSet resultSet) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        while (resultSet.next()) {
            customers.add(new Customer(resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name")));
        }
        return customers;
    }

    public static List<Customer> findByLastName(String lastName) throws SQLException {
        String query = "SELECT * FROM \"shopDB\".public.shoper " +
                "       WHERE last_name = "+"'"+lastName+"'";
        try (Statement statement = getNewConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            return toPojo(resultSet);
        }
    }

    public static List<Customer> findByProductName(String productName, int minTimes) throws SQLException {
        String query = "SELECT * FROM \"shopDB\".public.shoper\n" +
                "       INNER JOIN (\n" +
                "       SELECT shoper_id, count(*)\n" +
                "       FROM \"shopDB\".public.purchase\n" +
                "       WHERE goods_id = (SELECT id FROM \"shopDB\".public.goods WHERE name = " +"'"+productName+"'"+")\n" +
                "       GROUP BY shoper_id) as sic ON sic.shoper_id = shoper.id WHERE sic.count >="+minTimes;
        try (Statement statement = getNewConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            return toPojo(resultSet);
        }
    }

    public static List<Customer> findByExpenses(int minExpenses, int maxExpenses) throws SQLException {
        String query = "SELECT id, first_name, last_name FROM \"shopDB\".public.shoper INNER JOIN (SELECT shoper_id, sum(price) FROM \"shopDB\".public.shoper INNER JOIN (SELECT shoper_id, price FROM \"shopDB\".public.purchase INNER JOIN \"shopDB\".public.goods g on g.id = purchase.goods_id) as sigip ON shoper.id = sigip.shoper_id GROUP BY sigip.shoper_id) as price_sum ON shoper_id=shoper.id WHERE price_sum.sum > "+minExpenses+" AND price_sum.sum < "+maxExpenses;
        try (Statement statement = getNewConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            return toPojo(resultSet);
        }
    }

    public static List<Customer> findBadCustomers(int badCustomers) throws SQLException {
        String query = "SELECT id, first_name, last_name FROM \"shopDB\".public.shoper\n" +
                "    INNER JOIN\n" +
                "    (SELECT shoper_id, count(goods_id) FROM \"shopDB\".public.purchase GROUP BY shoper_id) AS goods_count\n" +
                "        ON shoper.id = goods_count.shoper_id\n" +
                "ORDER BY goods_count.count LIMIT "+badCustomers;
        try (Statement statement = getNewConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            return toPojo(resultSet);
        }
    }


        public static void searchMapping (List <SearchRequest> searchRequests) throws SQLException {

            for (int i = 0; i < searchRequests.size(); i++) {
                Map<String, String> criterias = new HashMap<>();
                SearchRequest searchRequest = searchRequests.get(i);
                switch (searchRequest.getField()) {
                    case ("lastName"):
                        System.out.println("Да есть тут имя " + searchRequest.getValue());
                        criterias.put("lastName", searchRequest.getValue());
                        ResponseDto xzt = new ResponseDto(findByLastName(searchRequest.getValue()), criterias);
                        System.out.println(xzt.toString());
                      //  return new ResponseDto
                        break;
                    case ("badCustomers"):
                        System.out.println("Да есть тут пассивные покупатели " + searchRequest.getValue());
                        criterias.put("badCustomers", searchRequest.getValue());
                        ResponseDto xzz = new ResponseDto(findBadCustomers(Integer.parseInt(searchRequest.getValue())), criterias);
                        System.out.println(xzz.toString());
                      //  return new ResponseDto(resultList, criterias);
                        break;
                    case ("maxExpenses"):
                        SearchRequest minExpenses = searchRequests.get(i - 1);
                        if (minExpenses.getField().equals("minExpenses")) {
                            System.out.println(findByExpenses(Integer.parseInt(minExpenses.getValue()),Integer.parseInt(searchRequest.getValue())));
                        } else {
                            System.out.println("Неправильный запрос");
                        }
                        criterias.put("maxExpenses", searchRequest.getValue());
                        criterias.put("minExpenses", searchRequests.get(i - 1).getValue());
                        ResponseDto xz = new ResponseDto(findByExpenses(Integer.parseInt(minExpenses.getValue()),Integer.parseInt(searchRequest.getValue())), criterias);
                        System.out.println(xz.toString());
                    //    return new ResponseDto(findByExpenses(Integer.parseInt(minExpenses.getValue()),Integer.parseInt(searchRequest.getValue())), criterias);
                        break;
                    case ("productName"):
                        SearchRequest minTimes = searchRequests.get(i - 1);
                        criterias.put("productName", searchRequest.getValue());
                        if (minTimes.getField().equals("minTimes")) {
                            System.out.println(findByProductName(searchRequest.getValue(), Integer.parseInt(minTimes.getValue())));
                            criterias.put("minTimes", minTimes.getValue());
                            ResponseDto xzs = new ResponseDto(findByProductName(searchRequest.getValue(),Integer.parseInt(minTimes.getValue())),criterias);
                            System.out.println(xzs);
                        } else {
                            System.out.println("Неправильный запрос");
                       }
                        break;
                }


            }
        }
    }


