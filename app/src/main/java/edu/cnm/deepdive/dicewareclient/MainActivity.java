package edu.cnm.deepdive.dicewareclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

  private ListView words;
  private Button generate;
  private DicewareService service;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupUI();
    setupService();

  }

  private void setupService() {
    Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();
    service = new Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(DicewareService.class);
  }

  private void setupUI() {
    setContentView(R.layout.activity_main);
    words = findViewById(R.id.words);
    generate = findViewById(R.id.generate);
    generate.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new GenerateTask().execute();
      }
    });
  }

  private class GenerateTask extends AsyncTask<Void, Void, String[]> {

    @Override
    protected String[] doInBackground(Void... voids) {
      String[] passphrase = null;
      try {
        Call<String[]> call = service.get(10);
        Response<String[]> response = call.execute();
        if (response.isSuccessful()) {
          passphrase = response.body();
        }
      } catch (IOException e) {
        //Do nothing; passphrase is null.
        Log.d("Diceware", e.toString());
      } finally {
        if (passphrase == null) {
          cancel(true);
        }
      }
      return passphrase;
    }

    @Override
    protected void onPostExecute(String[] strings) {
      ArrayAdapter<String> adapter =
          new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
      words.setAdapter(adapter);
    }

  }
}
