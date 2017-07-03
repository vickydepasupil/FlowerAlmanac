package ph.edu.up.floweralmanac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;


public class LoginActivity extends AppCompatActivity {
    //For incorporation in the future - login separated from main activity

    final static public String APP_KEY = "77dykoko5st852n";
    final static public String APP_SECRET = "31kvdt1y8id6f3s";

    public final static String TOKEN = "ph.edu.up.loginactivity.TOKEN";

    public static DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        /*initialize_session();*/

        Button fab = (Button) findViewById(R.id.button);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
                AndroidAuthSession session = new AndroidAuthSession(appKeyPair);

                mDBApi = new DropboxAPI<AndroidAuthSession>(session);

                mDBApi.getSession().startOAuth2Authentication(LoginActivity.this);

                if(!getAccessToken().equalsIgnoreCase("")) {
                    mDBApi.getSession().setOAuth2AccessToken(getAccessToken());
                } else {
                    mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                    mDBApi.getSession().startOAuth2Authentication(LoginActivity.this);
                }
            }
        });
    }

    protected void onResume() {

        super.onResume();

        if (getAccessToken().equalsIgnoreCase("")){

                try {
                    if (mDBApi.getSession().authenticationSuccessful()) {
                        mDBApi.getSession().finishAuthentication();

                        String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                        saveAccessToken(accessToken);

                        Intent intent = new Intent(LoginActivity.this, FlowerMainActivity.class);
                        String token = getAccessToken();
                        intent.putExtra(TOKEN, token);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }

                } catch (IllegalStateException ie) {
                    ie.printStackTrace();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();

            }
        } else {
            Intent intent = new Intent(LoginActivity.this, FlowerMainActivity.class);
            String token = getAccessToken();

            intent.putExtra(TOKEN, token);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }

    public void saveAccessToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("accessToken", accessToken).commit();
    }

    public String getAccessToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }
}
