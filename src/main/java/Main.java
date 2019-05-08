import com.google.gson.Gson;
import com.sun.jndi.toolkit.url.Uri;
import model.Result;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Main {



    public static void main(String[] args) {
        String query;
        Scanner sc=new Scanner(System.in);
        ApiRequest apiRequest=new ApiRequest();

        System.out.println("Title Query");
        query= sc.nextLine();

        apiRequest.getAllAvailableTitles(query).forEach(System.out::println);
        sc.nextLine();
    }

    static class ApiRequest{

        List<String> getAllAvailableTitles(String title){
            List<String> titles=new ArrayList<>();
            Result apiResults=new Gson().fromJson(searchByTitle(title,1),Result.class);

            apiResults.getData().forEach(data->{
                titles.add(data.getTitle());
            });

            if(apiResults.getTotal_pages()>1){
                for(int i=2;i<=apiResults.getTotal_pages();i++){
                    Result tmpResult=new Gson().fromJson(searchByTitle(title,i),Result.class);
                    tmpResult.getData().forEach(data->{
                        titles.add(data.getTitle());
                    });
                }
            }

            return titles.stream().sorted().collect(Collectors.toList());
        }

        private String searchByTitle(String title,int page){
            try {
                String SERVER_URL = "https://jsonmock.hackerrank.com/api/movies/search";
                HttpURLConnection connection=
                        (HttpURLConnection)new URL(String.format("%s?Title=%s&page=%s", SERVER_URL,title,page)).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}
