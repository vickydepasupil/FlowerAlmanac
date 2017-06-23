package ph.edu.up.floweralmanac;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import ph.edu.up.floweralmanac.models.Flower;

import static ph.edu.up.floweralmanac.FlowerMainActivity.mDBApi;

/**
 * Created by fulltime on 07/06/2017.
 */

public class ListFlowerAdapter extends BaseAdapter {

    private Context context;
    private List<Flower> flowerList;

    public ListFlowerAdapter(Context context, List<Flower> flowerList) {
        this.context = context;
        this.flowerList = flowerList;
    }

    @Override
    public int getCount() {
        return flowerList.size();
    }

    @Override
    public Object getItem(int position) {
        return flowerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return flowerList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.content_flower_main, null);
        TextView textName = (TextView) view.findViewById(R.id.item_name);
        TextView textEase = (TextView) view.findViewById(R.id.item_ease);
        ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);

        textName.setText(flowerList.get(position).getName());
        textEase.setText(flowerList.get(position).getEase());

        String rev = flowerList.get(position).getRev();

        if (!rev.equals("")) {
            String path = getPhoto(flowerList.get(position).getName(), flowerList.get(position).getId());

            try {
                InputStream inputStream = mDBApi.getThumbnailStream(path, DropboxAPI.ThumbSize.ICON_32x32, DropboxAPI.ThumbFormat.JPEG);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (DropboxException de) {
                de.printStackTrace();
            }
        } else {

            String ease = flowerList.get(position).getEase();

            if (ease.equals("Easy")) {
                imageView.setImageResource(R.mipmap.inspired);
            } else if (ease.equals("Medium")) {
                imageView.setImageResource(R.mipmap.happy);
            } else if (ease.equals("Difficult")){
                imageView.setImageResource(R.mipmap.laugh);
            }
        }
        return view;
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

}
