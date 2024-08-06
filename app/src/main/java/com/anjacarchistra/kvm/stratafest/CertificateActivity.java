
package com.anjacarchistra.kvm.stratafest;

import static com.anjacarchistra.kvm.stratafest.api.Constants.NAME_KEY;
import static com.anjacarchistra.kvm.stratafest.api.Constants.PREFS_NAME;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.anjacarchistra.kvm.stratafest.api.CertificateHandler;
import com.anjacarchistra.kvm.stratafest.api.EventHandler;
import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.Student;
import com.anjacarchistra.kvm.stratafest.handler.CertificateCallBack;
import com.anjacarchistra.kvm.stratafest.handler.EventCallback;
import com.anjacarchistra.kvm.stratafest.localdb.SQLiteHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CertificateActivity extends AppCompatActivity implements EventCallback , CertificateCallBack {

    private static final String TAG = "CertificateActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageView imageView;
    private ProgressBar progressBar;
    private SQLiteHelper databaseHelper;
    private Button generate;
    private Student student;
    private String currentStudentName = "";
    private String currentEvent;
    private ImageView certificate;

    private String currentPrize;
    private String currentCollegeName;
    private String studentName;
    private  String collegeName;
    private  String lot;
    int status;
    private List<String> eventNames = new ArrayList<>();
    List<Event> events = new ArrayList<>();
    Set<String> eventIdSet= new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.eventIdSet = sharedPreferences.getStringSet("eventid", new LinkedHashSet<>());

        studentName = sharedPreferences.getString(NAME_KEY, "");
        collegeName = sharedPreferences.getString("collegename", "");
       this.lot = sharedPreferences.getString("lot","");
        Toast.makeText(this, "Selected lot"+lot, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, eventIdSet.toString(), Toast.LENGTH_SHORT).show();
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        ImageView shareIcon = findViewById(R.id.shareIcon);
        ImageView downloadIcon = findViewById(R.id.downloadIcon);
        generate = findViewById(R.id.generate);
        certificate = findViewById(R.id.imageView);

        generate.setOnClickListener(v -> generateCertificate());
        shareIcon.setOnClickListener(v -> shareCertificate());
        downloadIcon.setOnClickListener(v -> checkPermissions());

        databaseHelper = SQLiteHelper.getInstance(this);
        if (databaseHelper.getAllEvents().isEmpty()) {
            Toast.makeText(this, "CAlling event thread", Toast.LENGTH_SHORT).show();
            new EventHandler(this, this);
        }else {
            Toast.makeText(this, "Else block", Toast.LENGTH_SHORT).show();
            runOnUiThread(this::populateEventSpinner);
        }
    }

    @Override
    public void onSuccess(List<Event> events) {

        for (Event e : events) {
            Log.d("CERFICATE",e.toString());
            if (this.eventIdSet.contains(e.getEventId())) {
                eventNames.add(e.getEventName());
                Log.d("CERTIFICATE EVENT NAME",e.getEventName());
                Toast.makeText(this, e.getEventName(), Toast.LENGTH_SHORT).show();
            }
            databaseHelper.addEvent(e);
        }
        runOnUiThread(this::populateEventSpinner);  // Ensure UI updates are done on the main thread
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void populateEventSpinner() {
        Log.d(TAG, "Event names list: " + eventNames);

        if (eventNames.isEmpty()) {
            events =databaseHelper.getAllEvents();
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            Set<String> eventIdSet = sharedPreferences.getStringSet("eventid", new LinkedHashSet<>());
            for (Event e:
                 events) {
                if (eventIdSet.contains(String.valueOf(e.getEventId())))eventNames.add(e.getEventName());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "No events found.");
        } else {
            events =databaseHelper.getAllEvents();

            for (Event e:
                    events) {
                if (this
                        .eventIdSet.contains(String.valueOf(e.getEventId())))eventNames.add(e.getEventName());
            }
            Log.d(TAG, "Total events: " + eventNames.size());
        }
        Toast.makeText(this, "EVENt names : "+eventNames.toString(), Toast.LENGTH_SHORT).show();

        Spinner eventSpinner = findViewById(R.id.EventSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(adapter);

        Log.d(TAG, "Spinner populated with events.");
        hideLoadingIndicators();
    }

    private void hideCertificate() {
        certificate.setVisibility(View.GONE);
    }

    private void showCertificate() {
        certificate.setVisibility(View.VISIBLE);
    }

    private void generateCertificate() {


        showLoadingIndicators();
        hideCertificate();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        Spinner eventSpinner = findViewById(R.id.EventSpinner);
        String selectedEventName = (String) eventSpinner.getSelectedItem();

        int eventid=-1;

        for (Event e:
             events) {
            if (e.getEventName().equals(selectedEventName)){eventid = e.getEventId();
                Toast.makeText(this, "EVent id"+eventid, Toast.LENGTH_SHORT).show();
                break;
            }

        }
        Toast.makeText(this, "EVEnt handler values "+ lot+eventid, Toast.LENGTH_SHORT).show();
            new CertificateHandler(this,this,Integer.parseInt(lot),eventid).execute();
        if (selectedEventName == null || selectedEventName.isEmpty()) {
            Toast.makeText(this, "Please select an event", Toast.LENGTH_SHORT).show();
            hideLoadingIndicators();
            return;
        }


        student = new Student(studentName, collegeName, "", selectedEventName);
        currentStudentName = studentName;
        currentCollegeName = collegeName;
        currentPrize = "";
        currentEvent = student.getEventName();

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            downloadCertificate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadCertificate();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCertificateError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCertificateSuccess(String result) {
        status= Integer.parseInt(result);
        new GenerateCertificateTask().execute(student);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    private class GenerateCertificateTask extends AsyncTask<Student, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Student... students) {
            Student student = students[0];
            Bitmap certificateBitmap =getCertificateBitmap(status);

            if (certificateBitmap == null) {
                Log.e(TAG, "Failed to decode certificate drawable.");
                return null;
            }

            Bitmap mutableBitmap = certificateBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(120);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            int bitmapWidth = mutableBitmap.getWidth();
            int bitmapHeight = mutableBitmap.getHeight();

            float nameXPos = bitmapWidth * 0.4f;
            float nameYPos = bitmapHeight * 0.56f;
            float collegeXPos = bitmapWidth * 0.1f;
            float collegeYPos = bitmapHeight * 0.635f;
            float eventXPos = bitmapWidth * 0.6f;
            float eventYPos = bitmapHeight * 0.71f;
            float prizeXPos = bitmapWidth * 0.4f;
            float prizeYPos = bitmapHeight * 0.71f;

            canvas.drawText(student.getName(), nameXPos, nameYPos, paint);
            canvas.drawText(student.getCollege(), collegeXPos, collegeYPos, paint);
            canvas.drawText(student.getEventName(), eventXPos, eventYPos, paint);
            canvas.drawText(student.getPrize(), prizeXPos, prizeYPos, paint);

            return mutableBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                showCertificate();
                Log.d(TAG, "Certificate generated ");

                // Save the bitmap as a PDF file
                String fileName = currentStudentName + ".pdf";
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
                createPdf(file, new Student(currentStudentName, currentCollegeName, "1st", currentEvent));
            } else {
                Log.e(TAG, "Failed :");
            }
            hideLoadingIndicators();
        }
    }

    private void showLoadingIndicators() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicators() {
        progressBar.setVisibility(View.GONE);
    }

    private void shareCertificate() {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), currentStudentName + ".pdf");
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Certificate"));
    }

    private void downloadCertificate() {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), currentStudentName + ".pdf");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            String text = "This is a sample text for the PDF file.";
            outputStream.write(text.getBytes());
            outputStream.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    OutputStream output = getContentResolver().openOutputStream(uri);
                    if (output != null) {
                        FileInputStream inputStream = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        output.close();
                        Log.d(TAG, "Certificate downloaded successfully.");
                        Toast.makeText(this, "Certificate downloaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to open output stream for URI.");
                    }
                } else {
                    Log.e(TAG, "Failed to insert file into MediaStore.");
                }
            } else {
                MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, (path, uri) -> {
                    Log.d(TAG, "Certificate downloaded successfully.");
                    Toast.makeText(this, "Certificate downloaded successfully", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to download certificate: " + e.getMessage());
            Toast.makeText(this, "Failed to download certificate", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdf(File file, Student student) {
        try {
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Bitmap certificateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.certificate);
            Bitmap mutableBitmap = certificateBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(mutableBitmap, 0, 0, null);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            int bitmapWidth = mutableBitmap.getWidth();
            int bitmapHeight = mutableBitmap.getHeight();

            float nameXPos = bitmapWidth * 0.4f;
            float nameYPos = bitmapHeight * 0.56f;
            float collegeXPos = bitmapWidth * 0.1f;
            float collegeYPos = bitmapHeight * 0.635f;
            float eventXPos = bitmapWidth * 0.6f;
            float eventYPos = bitmapHeight * 0.71f;
            float prizeXPos = bitmapWidth * 0.4f;
            float prizeYPos = bitmapHeight * 0.71f;

            canvas.drawText(student.getName(), nameXPos, nameYPos, paint);
            canvas.drawText(student.getCollege(), collegeXPos, collegeYPos, paint);
            canvas.drawText(student.getEventName(), eventXPos, eventYPos, paint);
            canvas.drawText(student.getPrize(), prizeXPos, prizeYPos, paint);

            pdfDocument.finishPage(page);
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();
        } catch (IOException e) {
            Log.e(TAG, "Error generating PDF: " + e.getMessage());
        }
    }

    private Bitmap getCertificateBitmap(int status) {
        Bitmap certificate = null;
        switch (status) {
            case 0:
                certificate = BitmapFactory.decodeResource(getResources(), R.drawable.certificate_participant);
                break;
            case 1:
                certificate = BitmapFactory.decodeResource(getResources(), R.drawable.certificate);
                break;
            case 2:
                certificate = BitmapFactory.decodeResource(getResources(), R.drawable.certificate2);
                break;
            default:
                System.out.println("Invalid status: " + status);
                break;
        }
        return certificate;
    }
}
