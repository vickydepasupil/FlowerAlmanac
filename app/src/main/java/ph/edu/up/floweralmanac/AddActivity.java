package ph.edu.up.floweralmanac;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static ph.edu.up.floweralmanac.FlowerMainActivity.mDBApi;

public class AddActivity extends AppCompatActivity {

    public final static String ID = "ph.edu.up.addactivity.ID";
    public final static String NAME = "ph.edu.up.addactivity.NAME";
    public final static String REV = "ph.edu.up.addactivity.REV";
    public final static String EASE = "ph.edu.up.addactivity.EASE";
    public final static String INST = "ph.edu.up.addactivity.INST";
    public final static String PATH = "ph.edu.up.addactivity.PATH";

    public static String userChoice = "";
    public static String path = "";

    private static final int REQUEST_CAMERA = 5;
    private static final int SELECT_FILE = 6;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save:
                saveChanges();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flower);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.addPhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        /*Button button1 = (Button) findViewById(R.id.clear_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePhoto();
            }
        });*/
        path = "";

        Intent intent1 = getIntent();

        int idFlower = intent1.getIntExtra(FlowerMainActivity.IDFLOWER, 0);
        String nameFlower = intent1.getStringExtra(FlowerMainActivity.NAMEFLOWER);
        String easeFlower = intent1.getStringExtra(FlowerMainActivity.EASEFLOWER);
        String instFlower = intent1.getStringExtra(FlowerMainActivity.INSTFLOWER);
        String revFlower = intent1.getStringExtra(FlowerMainActivity.REVFLOWER);

        if (nameFlower != null && !nameFlower.isEmpty()) {

            EditText textView = (EditText) findViewById(R.id.editName);
            textView.setText(nameFlower);

            Spinner textView1 = (Spinner) findViewById(R.id.editEase);

            if (easeFlower.equals("Easy")) {
                textView1.setSelection(0);
            } else if (easeFlower.equals("Medium")) {
                textView1.setSelection(1);
            } else if (easeFlower.equals("Difficult")) {
                textView1.setSelection(2);
            }

            EditText textView2 = (EditText) findViewById(R.id.editInst);
            textView2.setText(instFlower);

            TextView textView3 = (TextView) findViewById(R.id.idField);
            textView3.setText(String.valueOf(idFlower));

            TextView textView4 = (TextView) findViewById(R.id.revField);
            textView4.setText(revFlower);

            ImageView imageView = (ImageView) findViewById(R.id.photoView);

            Log.e("SENDER", revFlower);

            if (!revFlower.equals("")) {
                path = getPhoto(nameFlower, idFlower);
                Log.e("SENDER", path);
                new Download(getApplicationContext(), imageView, mDBApi).execute();
            }
        }
    }

    public void saveChanges() {

        EditText textView = (EditText) findViewById(R.id.editName);
        String fname = textView.getText().toString();

        Spinner textView1 = (Spinner) findViewById(R.id.editEase);
        String ease = textView1.getSelectedItem().toString();

        EditText textView2 = (EditText) findViewById(R.id.editInst);
        String inst = textView2.getText().toString();

        TextView textView3 = (TextView) findViewById(R.id.idField);
        String id = textView3.getText().toString();

        TextView textView4 = (TextView) findViewById(R.id.revField);
        String rev = textView4.getText().toString();

        Intent intent = new Intent();

        if (fname.isEmpty() || fname == "") {
            setResult(Activity.RESULT_CANCELED, intent);
            finish();

        } else {

            try {

                int flowerID = Integer.parseInt(id);

                intent.putExtra(ID, flowerID);
                intent.putExtra(NAME, fname);
                intent.putExtra(EASE, ease);
                intent.putExtra(INST, inst);
                intent.putExtra(PATH, path);
                intent.putExtra(REV, rev);

                setResult(Activity.RESULT_OK, intent);
                path = "";
                finish();

            } catch (NumberFormatException ex) {

                int flowerID = -1;

                intent.putExtra(ID, flowerID);
                intent.putExtra(NAME, fname);
                intent.putExtra(EASE, ease);
                intent.putExtra(INST, inst);
                intent.putExtra(PATH, path);
                intent.putExtra(REV, rev);

                setResult(Activity.RESULT_OK, intent);
                path = "";
                finish();
            }
        }
    }

    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Upload from Library"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission(AddActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoice = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Upload from Library")) {
                    userChoice = "Upload from Library";
                    if (result)
                        galleryIntent();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoice.equals("Take Photo")) {
                        cameraIntent();
                    } else if (userChoice.equals("Upload from Library")) {
                        galleryIntent();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Permission required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;

        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                Uri pathUri = data.getData();
                path = getPathThruURI(getApplicationContext(), pathUri);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        Bitmap rotated = ExifUtil.rotateBitmap(path, bitmap);
        imageView.setImageBitmap(rotated);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        Uri filePath = Uri.fromFile(destination);
        path = getPathThruURI(getApplicationContext(), filePath);

        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        Bitmap rotated = ExifUtil.rotateBitmap(path, thumbnail);
        imageView.setImageBitmap(rotated);
    }

    public static String getPathThruURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public String getPhoto(String name, int id) {

        File file = new File(Environment.getExternalStorageDirectory(), name+"_"+String.valueOf(id)+".jpg");
        FileOutputStream outputStream;
        String remotePath = "";

        try {
            outputStream = new FileOutputStream(file);
            DropboxAPI.DropboxFileInfo info = null;
            info = mDBApi.getFile(name+"_"+String.valueOf(id)+".jpg", null, outputStream, null);
            remotePath = info.getMetadata().path;
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (DropboxException de) {
            de.printStackTrace();
        }
        return remotePath;
    }

    public class Download extends AsyncTask<String, Void, Bitmap> {
        private Context dContext;
        private View rootView;
        DropboxAPI<AndroidAuthSession> dDBApi;

        public Download(Context context, View view, DropboxAPI<AndroidAuthSession> mDBApi) {
            this.dContext = context;
            this.rootView = view;
            this.dDBApi = mDBApi;
        }

        protected void onPreExecute(){}

        protected Bitmap doInBackground(String... arg0) {
            try {
                InputStream inputStream = mDBApi.getThumbnailStream(path, DropboxAPI.ThumbSize.ICON_256x256, DropboxAPI.ThumbFormat.JPEG);
                return BitmapFactory.decodeStream(inputStream);
            } catch (DropboxException de) {
                de.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.photoView);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public void removePhoto() {
        ImageView imageView = (ImageView) findViewById(R.id.photoView);
        TextView textView = (TextView) findViewById(R.id.revField);

        try {
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

            if (bitmap != null) {
                imageView.setImageBitmap(null);
                imageView.setImageResource(R.drawable.flower);
                textView.setText(null);
                path = "";
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }

    public String checkRev(TextView view) {
        String rev = "";

        try {
            rev = view.getText().toString();
        } catch (NullPointerException ne) { }

        return rev;
    }
}

