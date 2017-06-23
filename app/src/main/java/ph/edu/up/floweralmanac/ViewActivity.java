package ph.edu.up.floweralmanac;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import static ph.edu.up.floweralmanac.FlowerMainActivity.mDBApi;

public class ViewActivity extends AppCompatActivity {

    public final static String ID = "ph.edu.up.viewactivity.ID";
    public final static String NAME = "ph.edu.up.viewactivity.NAME";
    public final static String EASE = "ph.edu.up.viewactivity.EASE";
    public final static String INST = "ph.edu.up.viewactivity.INST";
    public final static String REV = "ph.edu.up.viewactivity.DEL";
    public final static String DEL = "ph.edu.up.viewactivity.DEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Intent intent = getIntent();

        int idFlower = intent.getIntExtra(FlowerMainActivity.IDFLOWER, 0);
        String nameFlower = intent.getStringExtra(FlowerMainActivity.NAMEFLOWER);
        String easeFlower = intent.getStringExtra(FlowerMainActivity.EASEFLOWER);
        String instFlower = intent.getStringExtra(FlowerMainActivity.INSTFLOWER);
        String revFlower = intent.getStringExtra(FlowerMainActivity.REVFLOWER);

        TextView userText = (TextView) findViewById(R.id.flowerName);
        userText.setText(nameFlower);

        TextView userText1 = (TextView) findViewById(R.id.flowerEase);
        userText1.setText(easeFlower);

        TextView userText2 = (TextView) findViewById(R.id.flowerInst);
        userText2.setText(instFlower);

        TextView userText3 = (TextView) findViewById(R.id.idField);
        userText3.setText(String.valueOf(idFlower));

        TextView userText4 = (TextView) findViewById(R.id.revField);
        userText4.setText(revFlower);

        if (revFlower.equals("")) {
            ImageView imageView = (ImageView) findViewById(R.id.image);
            if (easeFlower.equals("Easy")) {
                imageView.setImageResource(R.mipmap.inspired);
            } else if (easeFlower.equals("Medium")) {
                imageView.setImageResource(R.mipmap.happy);
            } else if (easeFlower.equals("Difficult")){
                imageView.setImageResource(R.mipmap.laugh);
            }
        } else {
            String path = getPhoto(nameFlower, idFlower);

            try {
                InputStream inputStream = mDBApi.getThumbnailStream(path, DropboxAPI.ThumbSize.ICON_256x256, DropboxAPI.ThumbFormat.JPEG);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = (ImageView) findViewById(R.id.image);
                imageView.setImageBitmap(bitmap);
            } catch (DropboxException de) {
                de.printStackTrace();
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_flower_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Intent intent = new Intent();

            TextView textView = (TextView) findViewById(R.id.flowerName);
            String name = textView.getText().toString();

            TextView textView1 = (TextView) findViewById(R.id.flowerEase);
            String ease = textView1.getText().toString();

            TextView textView2 = (TextView) findViewById(R.id.flowerInst);
            String inst = textView2.getText().toString();

            TextView textView3 = (TextView) findViewById(R.id.idField);
            String idFlower = textView3.getText().toString();
            int flowerID = Integer.parseInt(idFlower);

            TextView textView4 = (TextView) findViewById(R.id.revField);
            String rev = textView4.getText().toString();

            intent.putExtra(ID, flowerID);
            intent.putExtra(NAME, name);
            intent.putExtra(EASE, ease);
            intent.putExtra(INST, inst);
            intent.putExtra(REV, rev);
            intent.putExtra(DEL, "false");

            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;

        } else if (id == R.id.action_delete) {
            Intent intent = new Intent();

            TextView textView = (TextView) findViewById(R.id.flowerName);
            String name = textView.getText().toString();

            TextView textView1 = (TextView) findViewById(R.id.flowerEase);
            String ease = textView1.getText().toString();

            TextView textView2 = (TextView) findViewById(R.id.flowerInst);
            String inst = textView2.getText().toString();

            TextView textView3 = (TextView) findViewById(R.id.idField);
            String idFlower = textView3.getText().toString();
            int flowerID = Integer.parseInt(idFlower);

            TextView textView4 = (TextView) findViewById(R.id.revField);
            String rev = textView4.getText().toString();

            intent.putExtra(ID, flowerID);
            intent.putExtra(NAME, name);
            intent.putExtra(EASE, ease);
            intent.putExtra(INST, inst);
            intent.putExtra(REV, rev);
            intent.putExtra(DEL, "true");

            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
