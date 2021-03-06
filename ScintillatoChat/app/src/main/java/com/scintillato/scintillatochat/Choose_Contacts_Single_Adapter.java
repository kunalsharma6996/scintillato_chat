package com.scintillato.scintillatochat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Choose_Contacts_Single_Adapter extends ArrayAdapter{

    private List<Choose_Contacts_Single_List> list=new ArrayList<Choose_Contacts_Single_List>();
    private Context ctx;
    private String cur_number;
    public Choose_Contacts_Single_Adapter(Context context, int resource) {
        super(context, resource);
        ctx=context;
        SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");

        // TODO Auto-generated constructor stub
    }

    public void add(Choose_Contacts_Single_List object)
    {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent)
    {
        View row;
        row=convertView;
        final Choose_Contacts_Holder chose_contacts_holder;
        if(row==null)
        {
            LayoutInflater layoutinflator=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutinflator.inflate(R.layout.activity_chose_contacts_single_row,parent,false );
            chose_contacts_holder= new Choose_Contacts_Holder();
            chose_contacts_holder.name=(TextView)row.findViewById(R.id.tv_chose_contacts_single_row_name);
            chose_contacts_holder.number=(TextView)row.findViewById(R.id.tv_chose_contacts_single_row_number);
            chose_contacts_holder.profile_pic=(CircleImageView)row.findViewById(R.id.iv_chose_contacts_single_row_dp);


            row.setTag(chose_contacts_holder);
        }
        else
        {
            chose_contacts_holder=(Choose_Contacts_Holder)row.getTag();
        }

        Choose_Contacts_Single_List issue_1_list=(Choose_Contacts_Single_List)this.getItem(position);
        chose_contacts_holder.name.setText(issue_1_list.get_name());
        chose_contacts_holder.number.setText(issue_1_list.get_number());
        final String num=issue_1_list.get_number();
        final String name=issue_1_list.get_name();

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");
        File mediaFile;
        String mImageName=num +".png";

        Log.d("h1","h1");
        File file = new File(mediaStorageDir.getPath()+File.separator+mImageName);
        Log.d("h1","h2");
        if(file.exists()){
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(chose_contacts_holder.profile_pic.getDrawable()).into(chose_contacts_holder.profile_pic);
            Log.d("h1","h3");
        }

            Log.d("h1","h4"+num);

            String number_trunc = num.substring(1);
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_number=" + number_trunc).placeholder(chose_contacts_holder.profile_pic.getDrawable()).into(chose_contacts_holder.profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    if(chose_contacts_holder.profile_pic!=null) {
                        chose_contacts_holder.profile_pic.buildDrawingCache();
                        Bitmap bmap = chose_contacts_holder.profile_pic.getDrawingCache();
                        if(bmap!=null)
                        storeImage(bmap, num);
                    }
                }

                @Override
                public void onError() {

                    Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                }
            });

        Log.d("yaha aaya2","yaha aaya");
        return row;
    }
    private void storeImage(Bitmap image,String number) {
        File pictureFile = getOutputMediaFile(number);

        if (pictureFile == null) {
            Log.d("herepath",pictureFile.getAbsolutePath());
            Log.d("","Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("", "Error accessing file: " + e.getMessage());
        }
    }
    private  File getOutputMediaFile(String number){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        Log.d("herepath1",mediaStorageDir.getAbsolutePath());

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName=number +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public Bitmap getProfileImage(String u_profile_pic)
    {
        byte[] decodedString = Base64.decode(u_profile_pic, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    static class Choose_Contacts_Holder
    {
        TextView name,number;
        CircleImageView profile_pic;
    }
}
