package com.android.assistyou;


import static com.android.assistyou.sahayAi.SecureFile.SecureKey.QueryResolver;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.assistyou.sahayAi.ChatGen;
import com.android.assistyou.sahayAi.ImageGeneration;
import com.android.assistyou.sahayAi.TextGeneration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int REQUEST_MICROPHONE_PERMISSION = 1;

    private ImageView send_Prompt;
    private String user_prompt, username;
    private Map<String, String> predefinedResponses;

    private AppCompatEditText prompt;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_PICK = 1;


    RelativeLayout ImageInput, ImageInput2;
    boolean Prompt_Type = false;

    private ImageView InputImage;
    private ImageView PromptImage, PromptImage2;
    private ImageView RemoveImage, RemoveImage2;
    private ProgressBar progressBar;

    private TextView timzone;
    private ImageView micBtn;
    private Toolbar header;

    Bitmap PromptImageBitmap, promptImage2;
    private PromptResultAdapter adapter;
    private List<PromptResultItem> resultList;

    private int imagePickCount = 0;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewsInit(); // Write Code Below it Gives The Views


        // Setting The Toolbar
        setSupportActionBar(header);

        // Define the colors for the gradient
        int[] colors = {Color.RED, Color.BLUE};

        // Create a LinearGradient
        Shader shader = new LinearGradient(12, 0, 0, timzone.getTextSize(), colors, null, Shader.TileMode.CLAMP);

        // Set the shader to the TextView's paint
        timzone.getPaint().setShader(shader);

        timzone.setText(TimeZone.getCurrentTime());

        // Check microphone permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE_PERMISSION);
        }


        // To delete Input Image
        RemoveImage.setOnClickListener(v -> {
            if (promptImage2 != null) {

                PromptImageBitmap = promptImage2;
                // If the second image exists, remove it
                PromptImage.setImageBitmap(PromptImageBitmap);
                promptImage2 = null;
                PromptImage2.setImageDrawable(null); // Clear the image
                ImageInput2.setVisibility(View.GONE);
                imagePickCount--;

            } else {
                // If only the first image exists, remove it
                PromptImageBitmap = null;
                PromptImage.setImageDrawable(null); // Clear the image
                ImageInput.setVisibility(View.GONE);
                Prompt_Type = false;
                imagePickCount--;
            }
        });


        RemoveImage2.setOnClickListener(v -> {

            promptImage2 = null;
            PromptImage2.setImageDrawable(null); // Clear the image
            ImageInput2.setVisibility(View.GONE);
            imagePickCount--;

        });

        // if the user enter an input that time i have to take it
        InputImage.setOnClickListener(v -> {
            // Create a dialog to choose between camera and gallery
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose Image Source");
            builder.setIcon(R.drawable.sahay_nobg);
            builder.setNegativeButton("Cancel", (dialog, id) -> {
                // Action to perform when the "Cancel" button is clicked
                dialog.cancel(); // This just dismisses the dialog
            });
            builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        // Open camera
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                        } else {
                            Toast.makeText(getApplicationContext(), "No app found to handle camera capture.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        // Open gallery
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.setType("image/*");
                        if (galleryIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
                        } else {
                            Toast.makeText(getApplicationContext(), "No app found to handle image selection from gallery.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                if (PromptImageBitmap != null && promptImage2 != null) {
                    ImageInput.setVisibility(View.VISIBLE);
                    ImageInput2.setVisibility(View.VISIBLE);

                } else if (promptImage2 == null) {
                    ImageInput.setVisibility(View.VISIBLE);

                }
            });
            builder.show();
        });

        prompt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = s.toString().trim();
                send_Prompt.setVisibility(inputText.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        micBtn.setOnClickListener(v -> startVoiceInput());


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            enablePromptButton();
        } else {
            Intent login = new Intent(MainActivity.this, Login.class);
            startActivity(login);
            finish(); // finish current activity to prevent user from coming back here if not authenticated
        }
    }

    private void enablePromptButton() {
        send_Prompt.setOnClickListener(v -> {

            if (!Prompt_Type) {
                // Play animation before generating the result
                timzone.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                send_Prompt.setVisibility(View.GONE);
                user_prompt = Objects.requireNonNull(prompt.getText()).toString();
                if (user_prompt.equalsIgnoreCase("What is Your Name") ||
                        user_prompt.equalsIgnoreCase("name") ||
                        user_prompt.equalsIgnoreCase("Your Name") ||
                        user_prompt.equalsIgnoreCase("Yours Name")) {
                    resultList.add(new PromptResultItem("SAHAY: Your Personal AI Assistant", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("Who designed You") ||
                        user_prompt.equalsIgnoreCase("Your Designer Name") ||
                        user_prompt.equalsIgnoreCase("Who Designed You") ||
                        user_prompt.equalsIgnoreCase("Designer")) {
                    resultList.add(new PromptResultItem("Sahay was designed by Tejas Kale", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                }
                else if (user_prompt.equalsIgnoreCase("Hello Sahay") ||
                        user_prompt.equalsIgnoreCase("Hey Sahay") ||
                        user_prompt.equalsIgnoreCase("Hi Sahay") ||
                        user_prompt.equalsIgnoreCase("hii sahay")) {
                    resultList.add(new PromptResultItem("Hey There ! \n How are You \n Let Me Know How I Can Assist You", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                }else if (user_prompt.equalsIgnoreCase("What can you do") ||
                        user_prompt.equalsIgnoreCase("Capabilities") ||
                        user_prompt.equalsIgnoreCase("Features")) {
                    resultList.add(new PromptResultItem("Sahay can assist you with managing tasks, providing information, setting reminders, and more.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("How do you work") ||
                        user_prompt.equalsIgnoreCase("Functionality") ||
                        user_prompt.equalsIgnoreCase("Operation")) {
                    resultList.add(new PromptResultItem("Sahay works by utilizing advanced AI algorithms to understand your commands and provide appropriate responses.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("Where can I download you") ||
                        user_prompt.equalsIgnoreCase("Download link") ||
                        user_prompt.equalsIgnoreCase("App store") ||
                        user_prompt.equalsIgnoreCase("Play store")) {
                    resultList.add(new PromptResultItem("You can download Genie from the App Store or Google Play Store.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("How do I use you") ||
                        user_prompt.equalsIgnoreCase("Instructions") ||
                        user_prompt.equalsIgnoreCase("User guide") ||
                        user_prompt.equalsIgnoreCase("Manual")) {
                    resultList.add(new PromptResultItem("Using Sahay is simple! Just speak or type your commands or questions, and Genie will provide assistance accordingly.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("Can you set reminders?") ||
                        user_prompt.equalsIgnoreCase("Reminders")) {
                    resultList.add(new PromptResultItem("Yes, Sahay can set reminders for you. Just let me know the details of the reminder, and I'll take care of it.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("Do you support multiple languages?") ||
                        user_prompt.equalsIgnoreCase("Language support")) {
                    resultList.add(new PromptResultItem("Currently, Sahay supports English, but we're working on adding support for more languages in the future.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else if (user_prompt.equalsIgnoreCase("Can you provide weather updates?") ||
                        user_prompt.equalsIgnoreCase("Weather updates")) {
                    resultList.add(new PromptResultItem("Yes, Sahay can provide weather updates for your location. Just ask, and I'll fetch the latest forecast for you.", user_prompt + " :"));
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    prompt.setText("");
                } else {


                    ChatGen ch = new ChatGen();
                    TextGeneration TextGeneration = new TextGeneration();
                    TextGeneration.generatedText(user_prompt+QueryResolver, generatedText -> runOnUiThread(() -> {
                        timzone.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        resultList.add(new PromptResultItem(generatedText, user_prompt + " :"));
                        adapter.notifyDataSetChanged();

                        prompt.setText("");
                    }));


                }
            } else {
                progressBar.setVisibility(View.VISIBLE);
                send_Prompt.setVisibility(View.GONE);
                user_prompt = Objects.requireNonNull(prompt.getText()).toString();
                ImageGeneration ImageGenerator = new ImageGeneration();

                if (promptImage2 != null) {
                    ImageGenerator.generatedText(user_prompt, PromptImageBitmap, promptImage2, generatedText -> runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        resultList.add(new PromptResultItem(generatedText, user_prompt + " :"));
                        adapter.notifyDataSetChanged();
                        Prompt_Type = false;
                        PromptImageBitmap = null;
                        promptImage2 = null;
                        PromptImage.setImageDrawable(null); // Clear the image
                        PromptImage2.setImageDrawable(null); // Clear the image
                        timzone.setVisibility(View.GONE);
                        prompt.setText("");
                        ImageInput.setVisibility(View.GONE);
                        ImageInput2.setVisibility(View.GONE);
                    }));

                } else {
                    ImageGenerator.generatedText(user_prompt, PromptImageBitmap, generatedText -> runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            resultList.add(new PromptResultItem(generatedText, user_prompt + " :"));
                            adapter.notifyDataSetChanged();
                            Prompt_Type = false;
                            PromptImageBitmap = null;
                            PromptImage.setImageDrawable(null); // Clear the image
                            PromptImage2.setImageDrawable(null);
                            prompt.setText("");
                            timzone.setVisibility(View.GONE);
                            ImageInput.setVisibility(View.GONE);
                        }


                    }));

                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem profileMenuItem = menu.findItem(R.id.profile_user);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_user) {

// Now you can use preferences to read or write data
            startActivity(new Intent(MainActivity.this,com.android.assistyou.Settings.class));
            return true;
        } else if (item.getItemId() == R.id.login) {
            startActivity(new Intent(MainActivity.this,Login.class));

        } else if (item.getItemId() == R.id.policy) {
            startActivity(new Intent(MainActivity.this,Policy.class));

        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(MainActivity.this,About.class));
        } else if (item.getItemId() == R.id.history) {
            startActivity(new Intent(MainActivity.this,histroy.class));

        } else {
            // To do
        }
        return super.onOptionsItemSelected(item);
    }

    public void ViewsInit() {
        send_Prompt = findViewById(R.id.sendPrompt);
        prompt = findViewById(R.id.prompt);
        RecyclerView recyclerView = findViewById(R.id.PromptResult);
        resultList = new ArrayList<>();
        adapter = new PromptResultAdapter(this, resultList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar);
        micBtn = findViewById(R.id.micBtn);
        header = findViewById(R.id.toolbar);

        InputImage = findViewById(R.id.inputImage);
        PromptImage = findViewById(R.id.inputPromptImage);
        ImageInput = findViewById(R.id.imagePromptInput);
        RemoveImage = findViewById(R.id.cut);
        ImageInput2 = findViewById(R.id.imagePromptInput2);
        PromptImage2 = findViewById(R.id.inputPromptImage2);
        RemoveImage2 = findViewById(R.id.cut2);

        timzone = findViewById(R.id.timeZone);

    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle speech recognition result
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert results != null;
            String spokenText = results.get(0);
            prompt.setText(spokenText);
        }

        // Handle image selection or capture result
        if ((requestCode == REQUEST_IMAGE_PICK || requestCode == REQUEST_IMAGE_CAPTURE) && resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                // Handle image selection from gallery
                Uri imageUri = data.getData();
                try {
                    // Get the bitmap from the URI

                    if (imagePickCount == 0) {
                        PromptImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        // Now you have both the bitmap and URI
                        // You can use them as per your requirement
                        // For example, set the bitmap to an ImageView
                        PromptImage.setImageBitmap(PromptImageBitmap);
                        imagePickCount++;
                        Prompt_Type = true;

                    } else {
                        promptImage2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        // Now you have both the bitmap and URI
                        // You can use them as per your requirement
                        // For example, set the bitmap to an ImageView
                        PromptImage2.setImageBitmap(promptImage2);
                        ImageInput2.setVisibility(View.VISIBLE);
                        imagePickCount--;
                        if (!Prompt_Type) {
                            Prompt_Type = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle image capture from camera
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (imagePickCount == 0) {
                        // Get the captured image as a bitmap
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        // Now you have the bitmap
                        // You can use it as per your requirement
                        // For example, set the bitmap to an ImageView
                        PromptImage.setImageBitmap(imageBitmap);
                        PromptImageBitmap = imageBitmap;
                        Prompt_Type = true;
                        imagePickCount++;
                    } else {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        PromptImage2.setImageBitmap(imageBitmap);
                        promptImage2 = imageBitmap;
                        imagePickCount--;
                        if (!Prompt_Type) {
                            Prompt_Type = true;
                        }

                    }
                }
            }
        }
    }


    private void checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
                    // Internet is available
                } else {
                    // Internet is not available
                    promptUserToEnableInternet();
                }
            } else {
                // For devices below Android M
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Internet is available
                } else {
                    // Internet is not available
                    promptUserToEnableInternet();
                }
            }
        }
    }

    private void promptUserToEnableInternet() {

        AlertDialog.Builder InternateOn = new AlertDialog.Builder(MainActivity.this);
        InternateOn.setTitle("Please Turn on Internate");


        InternateOn.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // Open Wi-Fi settings

            }
        });

        InternateOn.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });


        InternateOn.show();

    }


}