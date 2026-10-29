package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
private EditText poleMiasto;
private Button przyciskPobierz;
private TextView tekstStatus;
private TextView tekstMiasto;
private TextView tekstSzerokosc;
private TextView tekstDlugosc;
private TextView tekstTemperatura;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        poleMiasto = findViewById(R.id.txtMiasto);
        przyciskPobierz = findViewById(R.id.btnPobierz);
        tekstStatus = findViewById(R.id.txtWynikStatus);
        tekstMiasto = findViewById(R.id.txtWynikMiasto);
        tekstSzerokosc = findViewById(R.id.txtWynikSzerokosc);
        tekstDlugosc = findViewById(R.id.txtWynikDlugosc);
        tekstTemperatura = findViewById(R.id.txtWynikTemperatura);

        przyciskPobierz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nazwa = poleMiasto.getText().toString();

                if(nazwa.length() < 2) {
                    tekstStatus.setText("Wpisz co najmniej 2 znaki nazwy miejscowosci");
                    return;
                }

                przyciskPobierz.setEnabled(false);
                tekstStatus.setText("Pobieranie danych");
                tekstMiasto.setText("");
                tekstSzerokosc.setText("");
                tekstDlugosc.setText("");
                tekstTemperatura.setText("");

                new Thread(() -> {
                    try {
                        ApiClient api = new ApiClient();
                        api.pobierzPogode(nazwa);

                        runOnUiThread(() ->{
                            if(isDestroyed() || isFinishing()) return;

                            tekstMiasto.setText("Miejscowosc: " + api.miasto);
                            tekstSzerokosc.setText("Szerokosc: " + api.szerokosc);
                            tekstDlugosc.setText("Dlugosc: " + api.dlugosc);
                            tekstTemperatura.setText("Temperatura: " + api.temperatura);
                            tekstStatus.setText("Dane pobrane pomyslne");
                            przyciskPobierz.setEnabled(true);
                        });
                    } catch (Exception ex) {
                        runOnUiThread(() -> {
                            if(isDestroyed() || isFinishing()) return;
                            tekstStatus.setText("Błąd: " + ex.getMessage());
                            przyciskPobierz.setEnabled(true);
                        });
                    }
                }).start();
            }
        });
    }
}