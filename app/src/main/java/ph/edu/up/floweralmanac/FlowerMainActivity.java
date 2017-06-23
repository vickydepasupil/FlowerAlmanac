package ph.edu.up.floweralmanac;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import ph.edu.up.floweralmanac.models.Flower;

public class FlowerMainActivity extends AppCompatActivity {

    private ListView listView;
    private ListFlowerAdapter flowerAdapter;
    private List<Flower> flowerArrayList;
    private SQLiteHelper sqLiteHelper;

    final static private String APP_KEY = "77dykoko5st852n";
    final static private String APP_SECRET = "31kvdt1y8id6f3s";

    public static DropboxAPI<AndroidAuthSession> mDBApi;

    DropboxAPI.Entry response;

    public int VIEW = 1;
    public int ADD = 2;
    public final static String IDFLOWER = "ph.edu.up.flowermainactivity.IDFLOWER";
    public final static String NAMEFLOWER = "ph.edu.up.flowermainactivity.NAMEFLOWER";
    public final static String EASEFLOWER = "ph.edu.up.flowermainactivity.EASEFLOWER";
    public final static String INSTFLOWER = "ph.edu.up.flowermainactivity.INSTFLOWER";
    public final static String REVFLOWER = "ph.edu.up.flowermainactivity.REVFLOWER";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_flower_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize_session();

        sqLiteHelper = new SQLiteHelper(this);
        listView =(ListView) findViewById(R.id.layout_main);

        flowerArrayList = sqLiteHelper.getAllRecords();

        showMessage();

        flowerAdapter = new ListFlowerAdapter(this, flowerArrayList);
        listView.setAdapter(flowerAdapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Flower flower = (Flower) parent.getAdapter().getItem(position);
                String nameFlower = flower.getName();
                String easeFlower = flower.getEase();
                String instFlower = flower.getInstructions();
                int idFlower = flower.getId();
                String revFlower = flower.getRev();

                Intent intent = new Intent(FlowerMainActivity.this, ViewActivity.class);
                intent.putExtra(IDFLOWER, idFlower);
                intent.putExtra(NAMEFLOWER, nameFlower);
                intent.putExtra(EASEFLOWER, easeFlower);
                intent.putExtra(INSTFLOWER, instFlower);
                intent.putExtra(REVFLOWER, revFlower);

                startActivityForResult(intent, VIEW);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlowerMainActivity.this, AddActivity.class);
                startActivityForResult(intent, ADD);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        mDBApi.getSession().setOAuth2AccessToken(getAccessToken());

        if (requestCode == VIEW) {
            if (resultCode == Activity.RESULT_OK) {

                String status = intent.getStringExtra(ViewActivity.DEL);
                int saveID = intent.getIntExtra(ViewActivity.ID, 0);
                String saveName = intent.getStringExtra(ViewActivity.NAME);
                String saveEase = intent.getStringExtra(ViewActivity.EASE);
                String saveInst = intent.getStringExtra(ViewActivity.INST);
                String saveRev = intent.getStringExtra(ViewActivity.REV);

                if (status.equals("true")) {
                    Flower flower = new Flower();

                    flower.setName(saveName);
                    flower.setRev(saveRev);
                    flower.setId(saveID);
                    flower.setEase(saveEase);
                    flower.setInstructions(saveInst);

                    sqLiteHelper.deleteRecords(flower);
                    refreshList();
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();

                } else if (status.equals("false")) {
                    Intent intent1 = new Intent(FlowerMainActivity.this, AddActivity.class);
                    intent1.putExtra(IDFLOWER, saveID);
                    intent1.putExtra(NAMEFLOWER, saveName);
                    intent1.putExtra(EASEFLOWER, saveEase);
                    intent1.putExtra(INSTFLOWER, saveInst);
                    intent1.putExtra(REVFLOWER, saveRev);

                    startActivityForResult(intent1, ADD);
                }
            }
        }

        if (requestCode == ADD){
            if (resultCode == Activity.RESULT_OK) {

                int saveID = intent.getIntExtra(AddActivity.ID, 0);
                String saveName = intent.getStringExtra(AddActivity.NAME);
                String saveEase = intent.getStringExtra(AddActivity.EASE);
                String saveInst = intent.getStringExtra(AddActivity.INST);
                String savePath = intent.getStringExtra(AddActivity.PATH);

                flowerArrayList = sqLiteHelper.getAllRecords();
                int[] idList = new int[100];
                for (int i = 0; i < flowerArrayList.size(); i++) {
                    idList[i] = flowerArrayList.get(i).getId();
                }

                int maximum = maxVal(idList)+1;

                if (saveID == -1) {
                    String revIdSave = upload(savePath, saveName, maximum);

                    saveItem(maximum, saveName, revIdSave, saveEase, saveInst);
                    refreshList();
                } else {
                    String revIdUpdate = upload(savePath, saveName, maximum);

                    updateInfo(saveID, saveName, revIdUpdate, saveEase, saveInst);
                    refreshList();
                    Toast.makeText(getApplicationContext(), "Changes saved", Toast.LENGTH_LONG).show();
                }
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Blank item not saved", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Select Action");
        menu.add(0, view.getId(), 0, "Edit");
        menu.add(0, view.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit"){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Flower flower = (Flower) flowerAdapter.getItem(info.position);

            int idFlower = flower.getId();
            String nameFlower = flower.getName();
            String easeFlower = flower.getEase();
            String instFlower = flower.getInstructions();

            Intent intent = new Intent(FlowerMainActivity.this, AddActivity.class);
            intent.putExtra(IDFLOWER, idFlower);
            intent.putExtra(NAMEFLOWER, nameFlower);
            intent.putExtra(EASEFLOWER, easeFlower);
            intent.putExtra(INSTFLOWER, instFlower);

            startActivityForResult(intent, ADD);

        } else if (item.getTitle() == "Delete") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Flower flower = (Flower) flowerAdapter.getItem(info.position);
            sqLiteHelper.deleteRecords(flower);

            refreshList();

            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }

        return true;
    }

    public void updateInfo(int id, String name, String rev, String ease, String inst) {
        Flower flower = new Flower();
        flower.setName(name);
        flower.setRev(rev);
        flower.setEase(ease);
        flower.setInstructions(inst);
        flower.setId(id);

        sqLiteHelper.updateRecords(flower);
    }

    public void saveItem(int id, String name, String rev, String ease, String inst) {

        String status = sqLiteHelper.searchRecords(name);

        if (status.equals("exists")) {
            Toast.makeText(getApplicationContext(), "Flower already exists.", Toast.LENGTH_LONG).show();
        } else if (status.equals("none")){
            Flower flower = new Flower();

            flower.setId(id);
            flower.setName(name);
            flower.setRev(rev);
            flower.setEase(ease);
            flower.setInstructions(inst);

            sqLiteHelper.insertItems(flower);
        }
    }

    public int maxVal (int[] array) {
        //AUTO INCREMENT not working, substituting auto function with math.max sorting method
        int max = 0;
        for (int value: array) {
            max = Math.max(max, value);
        }
        return max;
    }

    protected void initialize_session() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);

        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        mDBApi.getSession().startOAuth2Authentication(FlowerMainActivity.this);

        if(!getAccessToken().equalsIgnoreCase("")) {
            mDBApi.getSession().setOAuth2AccessToken(getAccessToken());
        } else {
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(FlowerMainActivity.this);
        }
    }

    protected void onResume() {

        super.onResume();

        if (getAccessToken().equalsIgnoreCase("")){
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    mDBApi.getSession().finishAuthentication();

                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                    saveAccessToken(accessToken);



                } catch (IllegalStateException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    private void saveAccessToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("accessToken", accessToken).commit();
    }

    private String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

    public String upload(String path, String name, int id) {
        File file = new File(path);
        FileInputStream inputStream = null;
        String revId = "";

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            response = mDBApi.putFile(name+"_"+String.valueOf(id)+".jpg", inputStream, file.length(), null, null);
            if(response.rev != null) {
                Toast.makeText(this, "Photo uploaded to Dropbox", Toast.LENGTH_LONG).show();
                revId = response.rev;
            }
        } catch (DropboxException de) {
            de.printStackTrace();
        } catch (IllegalArgumentException ie){
            Toast.makeText(this, "No photo attached to item", Toast.LENGTH_LONG).show();
        }

        return revId;
    }

    public void refreshList() {
        //clears current list
        flowerArrayList.clear();
        //repopulates with updated set of records
        flowerArrayList = sqLiteHelper.getAllRecords();

        showMessage();

        flowerAdapter = new ListFlowerAdapter(this, flowerArrayList);
        listView.setAdapter(flowerAdapter);
        flowerAdapter.notifyDataSetChanged();
    }

    public void showMessage() {
        if (flowerArrayList.size() == 0) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_main);

            TextView textView = new TextView(FlowerMainActivity.this);
            textView.setText("No flowers saved yet.");
            textView.setId(R.id.temporary_textView);
            textView.setTextSize(16);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);

        } else {

            try {
                TextView textView = (TextView) findViewById((R.id.temporary_textView));
                if (textView != null) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_main);
                    linearLayout.removeView(textView);
                }
            } catch (Exception e) {}
        }
    }
}
