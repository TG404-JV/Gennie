package com.android.assistyou.sahayAi.pyDataFetcher;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LLMDataFetcher {

    private static final String TAG = "LLMDataFetcher";
    private Context context;
    public LLMDataFetcher()
    {

    }

    // Constructor to initialize context (needed if you use a JSON file from assets)
    public LLMDataFetcher(Context context) {
        this.context = context;
    }

    // Method to fetch data (either from assets or from an actual URL)
    public void fetchDataFromLLM() {
        // Uncomment one of the following lines based on what you're using:
        new FetchLLMDataTask().execute("Sahay_LLM"); // If using local file in assets
        // new FetchLLMDataTask().execute("https://example.com/api/llm"); // If using real URL
    }

    // AsyncTask to fetch LLM data in background
    private class FetchLLMDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String source = params[0];
            if (source.equals("Sahay_Img_LLm")) {
                // Fetch data from assets (for simulation)
                return loadDummyDatasetFromAssets();
            } else {
                // Fetch data from URL
                return fetchDataFromURL(source);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Parse the fetched data (JSON format expected)
                JSONObject jsonObject = new JSONObject(result);
                JSONArray dataArray = jsonObject.getJSONArray("data");

                // Loop through the data array
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);
                    String inputText = item.getString("input");
                    String outputText = item.getString("output");
                    Log.d(TAG, "Input: " + inputText + ", Output: " + outputText);
                    // Here, you can update your UI or pass data to other parts of your app
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing data: " + e.getMessage());
            }
        }

        private String loadDummyDatasetFromAssets() {
            StringBuilder jsonString = new StringBuilder();
            try {
                InputStream inputStream = context.getAssets().open("dataset.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonString.append(line);
                }
                reader.close();
            } catch (Exception e) {
                Log.e(TAG, "Error loading dataset from assets: " + e.getMessage());
            }
            return jsonString.toString();
        }

        private String fetchDataFromURL(String urlString) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching data from URL: " + e.getMessage());
            }
            return response.toString();
        }
    }
}

