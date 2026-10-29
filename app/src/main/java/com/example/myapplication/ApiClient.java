package com.example.myapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiClient {
    public String miasto;
    public double szerokosc;
    public double dlugosc;
    public double temperatura;

    private String pobierzTeskts(String adres) throws IOException {
        HttpURLConnection polaczenie = (HttpURLConnection) new URL(adres).openConnection();

        try{
            polaczenie.setRequestMethod("GET");
            polaczenie.setRequestProperty("Accept", "application/json");
            polaczenie.setConnectTimeout(8000);
            polaczenie.setReadTimeout(8000);

            int kod = polaczenie.getResponseCode();

            if(kod<200 || kod>=300) {
                throw new IOException("Serwer zwrócił błąd " + kod);
            }

            StringBuilder odpowiedz = new StringBuilder();
            try(BufferedReader czytnik = new BufferedReader(new InputStreamReader(polaczenie.getInputStream(), StandardCharsets.UTF_8))) {
                String linia;
                while ((linia = czytnik.readLine()) != null) {
                    odpowiedz.append(linia);
                }
            }
            return odpowiedz.toString();
        } finally {
            polaczenie.disconnect();
        }
    }

    public void pobierzPogode(String nazwaMiasta) throws  IOException, JSONException {
        String zakodowanaNazwa = URLEncoder.encode(nazwaMiasta, "UTF-8");
        String adresMiasta = "https://geocoding-api.open-meteo.com/v1/search?name=" + zakodowanaNazwa + "&count=1&language=pl";

        String tekstMiasto = pobierzTeskts(adresMiasta);
        JSONObject jsonMiasta = new JSONObject(tekstMiasto);
        JSONArray wynik = jsonMiasta.optJSONArray("results");

        if(wynik == null || wynik.length()==0) {
            throw new IOException("Nie znaleziono miejscowosci: "+ nazwaMiasta);
        }

        JSONObject pierwszeMiasto = wynik.getJSONObject(0);
        String odczytanaNazwa = pierwszeMiasto.getString("name");
        double odczytanaSzerokosc = pierwszeMiasto.getDouble("latitude");
        double odczytanaDlugosc = pierwszeMiasto.getDouble("longitude");

        //Pobieranie temperatury dla wspolrzednych
        String adresPogody = "https://api.open-meteo.com/v1/forecast?latitude=" + odczytanaSzerokosc + "&longitude=" + odczytanaDlugosc + "&current=temperature_2m&temperature_unit=celsius";

        String tektsPogody = pobierzTeskts(adresPogody);

        JSONObject jsonPogody = new JSONObject(tektsPogody);
        JSONObject current = jsonPogody.getJSONObject("current");
        double odczytanaTemperatura = current.getDouble("temperature_2m");

        this.miasto = odczytanaNazwa;
        this.szerokosc = odczytanaSzerokosc;
        this.dlugosc = odczytanaDlugosc;
        this.temperatura = odczytanaTemperatura;
    }

}
