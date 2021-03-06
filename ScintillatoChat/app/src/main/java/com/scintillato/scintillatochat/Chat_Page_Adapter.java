package com.scintillato.scintillatochat;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VIVEK on 14-03-2017.
 */

public class Chat_Page_Adapter extends RecyclerView.Adapter<Chat_Page_Adapter.Chat_Page_Holder> {

    private List<Chat_Page_List> list;
    public class Chat_Page_Holder extends RecyclerView.ViewHolder {
        public final View view;
        TextView name,message,count,time,read;
        ImageView iv_status;
        CircleImageView dp;
        public Chat_Page_Holder(View row) {
            super(row);
            this.view = row;
            name=(TextView)row.findViewById(R.id.tv_chat_page_row_name);
            message=(TextView)row.findViewById(R.id.tv_chat_page_row_message);
            count=(TextView)row.findViewById(R.id.tv_chat_page_row_count);
            time=(TextView)row.findViewById((R.id.tv_chat_page_row_time));
            dp=(CircleImageView)row.findViewById(R.id.iv_chat_page_row_dp);
            iv_status=(ImageView)row.findViewById(R.id.iv_char_page_row_status);
            read=(TextView)row.findViewById(R.id.tv_chat_page_row_r);
        }
    }
    private Context ctx;
    private String cur_number,cur_user_id;
    public Chat_Page_Adapter(Context ctx, List<Chat_Page_List> list) {
        this.list = list;
        this.ctx=ctx;
       /* SharedPreferences sharedpreferences = ctx.getSharedPreferences("User", Context.MODE_PRIVATE);
        cur_number = sharedpreferences.getString("number", "");
        My_Details_Execute obj=new My_Details_Execute(ctx,cur_number);
        Cursor c=obj.get_my_details(obj);
        c.moveToFirst();
        if(c.getCount()>0)
            cur_user_id=c.getString(0);*/
    }

    @Override
    public Chat_Page_Adapter.Chat_Page_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_page_row, parent, false);

        return new Chat_Page_Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Chat_Page_Adapter.Chat_Page_Holder home_page_holder, int position) {

        Chat_Page_List chat_list=list.get(position);
        if(chat_list.get_flag().equals("0"))
        {
           // home_page_holder.message.setText("Group Created");
            fetch_group_profile_pic(chat_list.get_group_id(),home_page_holder.dp);
        }
        else
        {
            home_page_holder.message.setText("");
            fetch_single_profile_pic(chat_list.get_opposite_person_number(),home_page_holder.dp);
        }

        if(chat_list.getSend_receive()!=null) {
            if (chat_list.getSend_receive().equals("0")) {
                home_page_holder.read.setVisibility(View.GONE);
                if (chat_list.getStatus().equals("0")) {
                    home_page_holder.iv_status.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_action_clock));
                } else if (chat_list.getStatus().equals("1")) {
                    home_page_holder.iv_status.setImageDrawable(ctx.getResources().getDrawable(R.drawable.done_black_18x18));

                } else if (chat_list.getStatus().equals("2")) {
                    home_page_holder.iv_status.setImageDrawable(ctx.getResources().getDrawable(R.drawable.done_all_black_18x18));

                } else //if(chat_list.getStatus().equals("3"))
                {
                    home_page_holder.read.setVisibility(View.VISIBLE);
                    home_page_holder.iv_status.setImageDrawable(ctx.getResources().getDrawable(R.drawable.done_all_black_18x18));

                }
            } else if (chat_list.getSend_receive().equals("1")) {
                home_page_holder.read.setVisibility(View.GONE);
                home_page_holder.iv_status.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.addRule(RelativeLayout.BELOW, R.id.tv_chat_page_row_name);
                params.addRule(RelativeLayout.ALIGN_LEFT, R.id.tv_chat_page_row_name);
                home_page_holder.message.setLayoutParams(params);
            }
        }
        home_page_holder.name.setText(chat_list.get_name());
        home_page_holder.message.setText(chat_list.get_messaage());
        home_page_holder.count.setText(chat_list.getMessage_count());
        String incoming_date= getdatemilli(chat_list.getmillisec());
        String today_date=gettoday();
        String yesterday =getyesterday();
        home_page_holder.time.setText(incoming_date);
        if(incoming_date.equals(today_date))
        {
            home_page_holder.time.setText(getdatemilli_hour(chat_list.getmillisec()));
        }
        else if(incoming_date.equals(yesterday))
        {
            home_page_holder.time.setText("yesterday");
        }
        else
        {
            home_page_holder.time.setText(incoming_date);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    void fetch_single_profile_pic(final String num,final CircleImageView profile_pic)
    {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");
        File mediaFile;
        String mImageName=num +".png";

        Log.d("h1","h1");
        File file = new File(mediaStorageDir.getPath()+File.separator+mImageName);
        Log.d("h1","h2");
        if(file.exists()){
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    String number_trunc = num.substring(1);
                    Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_number=" + number_trunc).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                            if(profile_pic!=null) {
                                profile_pic.buildDrawingCache();
                                Bitmap bmap = profile_pic.getDrawingCache();
                                if(bmap!=null)
                                    storeImageSingle(bmap, num);
                            }
                        }

                        @Override
                        public void onError() {

                            Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onError() {

                    Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("h1","h3");
        }
        else{
            String number_trunc = num.substring(1);
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_profile_pic_png_number.php?user_number=" + number_trunc).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                    if(profile_pic!=null) {
                        profile_pic.buildDrawingCache();
                        Bitmap bmap = profile_pic.getDrawingCache();
                        if(bmap!=null)
                            storeImageSingle(bmap, num);
                    }
                }

                @Override
                public void onError() {

                    Toast.makeText(ctx,"error picaso"+num,Toast.LENGTH_SHORT).show();
                }
            });

        }
        Log.d("h1","h4"+num);

    }
    void fetch_group_profile_pic(final String group_id,final CircleImageView profile_pic)
    {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Skim Whim");
        File mediaFile;
        String mImageName="group"+group_id +".png";

        Log.d("h1","h1");
        File file = new File(mediaStorageDir.getPath()+File.separator+mImageName);
        Log.d("h1","h2");
        if(file.exists()){
            Toast.makeText(ctx, "File exists in /mnt", Toast.LENGTH_SHORT);
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            Picasso.with(ctx).load(mediaFile).placeholder(profile_pic.getDrawable()).into(profile_pic,new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + group_id).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if(profile_pic!=null) {
                                profile_pic.buildDrawingCache();
                                Bitmap bmap = profile_pic.getDrawingCache();
                                if(bmap!=null)
                                    storeImageGroup(bmap, group_id);
                            }
                        }

                        @Override
                        public void onError() {

                            Toast.makeText(ctx,"error picaso"+group_id,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError() {


                }
            });
        }
        else
        {
            Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + group_id).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    if(profile_pic!=null) {
                        profile_pic.buildDrawingCache();
                        Bitmap bmap = profile_pic.getDrawingCache();
                        if(bmap!=null)
                            storeImageGroup(bmap, group_id);
                    }
                }

                @Override
                public void onError() {

                    Toast.makeText(ctx,"error picaso"+group_id,Toast.LENGTH_SHORT).show();
                }
            });
        }

        /*
        Picasso.with(ctx).load("http://scintillato.esy.es/fetch_group_profile_png_id.php?group_id=" + group_id).placeholder(profile_pic.getDrawable()).into(profile_pic, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                if(profile_pic!=null) {
                    profile_pic.buildDrawingCache();
                    Bitmap bmap = profile_pic.getDrawingCache();
                    if(bmap!=null)
                        storeImageGroup(bmap, group_id);
                }
            }

            @Override
            public void onError() {

                Toast.makeText(ctx,"error picaso"+group_id,Toast.LENGTH_SHORT).show();
            }
        });
*/
    }


    private void storeImageGroup(Bitmap image,String group_id) {
        File pictureFile = getOutputMediaFileGroup(group_id);

        if (pictureFile == null) {
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
    private  File getOutputMediaFileGroup(String group_id){
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
        String mImageName="group"+group_id +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    private void storeImageSingle(Bitmap image,String number) {
        File pictureFile = getOutputMediaFileSingle(number);

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
    private  File getOutputMediaFileSingle(String number){
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
    static class Home_Page_holder
    {
        TextView name,message,count,time;
        CircleImageView dp;

    }

    String getdatemilli_hour(long milli)
    {
        Date date= new Date(milli);

        DateFormat df;
        df = new SimpleDateFormat("HH:mm");
        String dat=df.format(date);
        return String.valueOf(dat);
    }

    String getdatemilli(long milli)
    {
        Date date= new Date(milli);

        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        String dat=df.format(date);
        return String.valueOf(dat);
    }
    String gettoday()
    {
        Date today = Calendar.getInstance().getTime();
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        String dat=df.format(today);
        return  String.valueOf(dat);
    }
    String getyesterday()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yyyy");
        String dat=df.format(yesterday);
        return  String.valueOf(dat);
    }

}
