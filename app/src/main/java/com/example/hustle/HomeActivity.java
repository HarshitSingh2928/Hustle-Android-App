package com.example.hustle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class HomeActivity extends AppCompatActivity{ //implements DailyTaskAdapter.onItemClick {

    ImageView logout,profilePic,newTaskButton;
    RecyclerView taskRecyclerView;
    TextView welcome, hourText, minuteText, secondText, notificationTime,targetTime;
    ArrayList<TaskModelClass> taskModelClassArrayList;
    String task="";
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ActivityResultLauncher<String> launcher;
    FirebaseStorage storage;
    String imageUrl;
    ArrayList<String> arrayList;
    long hourMilli;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    MaterialTimePicker picker;
    Calendar calendarNotification,  targetCalender;
    private String EVENT_DATE_TIME;
    Handler handler;
    Runnable runnable;
    String TestTime,pickerMinute;
    RecyclerView.Adapter adapter;
    SharedPreferences sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        createNotificationChannel();

        countDownStart();
        sharedPreferences=getSharedPreferences("taskSharedPreferenceForNotification",MODE_PRIVATE);

        //firebase
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference=firebaseDatabase.getReference("Users");
        storage=FirebaseStorage.getInstance();
        if(!firebaseAuth.getCurrentUser().isEmailVerified()){
            HomeActivity.this.finish();
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }
        String uid=firebaseUser.getUid();

        //Layout Views
        logout=findViewById(R.id.logout_image_button);
        profilePic=findViewById(R.id.profile_pic);
        taskRecyclerView=findViewById(R.id.daily_task_recycler_view);
        welcome=findViewById(R.id.welcome_text);
        newTaskButton=findViewById(R.id.add_new_task_button);
        hourText =findViewById(R.id.hourTime);
        minuteText =findViewById(R.id.minuteTime);
        secondText =findViewById(R.id.secondTime);

        //arraylist initialization
        taskModelClassArrayList=new ArrayList<TaskModelClass>();
        arrayList=new ArrayList<>();

        //RecyclerView
        adapter=new DailyTaskAdapter(this,arrayList);//,this::onClick);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(layoutManager);


        //Retrieving Users Task from firebase
        databaseReference.child(firebaseAuth.getUid().toString()).child("Tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    String retrieve=ds.getValue().toString();
                    arrayList.add(retrieve);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //On click of this button an alert dialog will open to enable user to schedule his/her daily tasks in string format
        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                View inputLayout= LayoutInflater.from(getApplicationContext()).inflate(R.layout.input_text_layout,(ViewGroup) ((ViewGroup) HomeActivity.this.findViewById(android.R.id.content)).getChildAt(0),false);
                EditText input=inputLayout.findViewById(R.id.task_input_frame_layout);
                targetTime=inputLayout.findViewById(R.id.targetTimeButton);
                notificationTime=inputLayout.findViewById(R.id.notificationTimeButton);


                //Target time-picker
                targetTime.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        showTimePickerTargetTime();
                    }
                });

                //Notification time-picker
                notificationTime.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        showTimePicker();
                        }
                });

                builder.setView(inputLayout);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(input.getText().toString().isEmpty()
                                ||notificationTime.getText().toString().isEmpty()
                                ||targetTime.getText().toString().isEmpty()){
                                Toast.makeText(builder.getContext(),"Some fields are missing",Toast.LENGTH_SHORT).show();
                                newTaskButton.callOnClick();

                        }
                        else{
                            dialog.dismiss();
                            task=input.getText().toString();
                            TaskModelClass taskModelClass=new TaskModelClass();
                            taskModelClass.setTask(task);
                            taskModelClass.setnTime(notificationTime.getText().toString());
                            taskModelClass.settTime(targetTime.getText().toString());
                            taskModelClass.setHourMilli(hourMilli);
                            setAlarm(calendarNotification.getTimeInMillis());
                            taskModelClassArrayList.add(taskModelClass);
                            firebaseDatabase.getReference().child("Users")
                                    .child(firebaseAuth.getUid().toString())
                                    .child("Tasks")
                                    .push().setValue(task+" by "+targetTime.getText().toString()+"\n start Time: "+notificationTime.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create();
              builder.show();
            }

        });
        //end of new task button





        //This the logout Button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            }
        });

        //profile pic selection button
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");
            }
        });


        //Getting username from firebase
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcome.setText("Welcome "+snapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Getting User profile picture from firebase
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    imageUrl=snapshot.child("image").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //selection of profile picture from phone's storage
        launcher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result==null && imageUrl.isEmpty()){
                    result=Uri.parse("android.resource://com.example.hustle/drawable/defaultpic");
                }
                else if(result==null && imageUrl.length()>0){
                    Picasso.get().load(imageUrl).transform(new CropCircleTransformation()).placeholder(R.drawable.defaultpic).into(profilePic);
                    return;
                }
                profilePic.setImageURI(result);
                final StorageReference storageReference=storage.getReference().child(firebaseAuth.getUid().toString()).child("image");
                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid().toString()).child("image").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(HomeActivity.this,"Profile pic updated",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        //End of selection of profile picture from phone's storage


        //loading user profile picture into imageView
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid().toString()).child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img=snapshot.getValue(String.class);
                Picasso.get().load(img).transform(new CropCircleTransformation()).placeholder(R.drawable.defaultpic).into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("myid", name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    //end of onCreate()


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        HomeActivity.this.finish();
        moveTaskToBack(true);
    }


    //Recycler view item on-long-press: open a delete(Tasks) warning for user
//    @Override
//    public void onClick(int position, View v) {
//        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
//        View view=LayoutInflater.from(HomeActivity.this).inflate(R.layout.delete_alert_box,(ViewGroup)HomeActivity.this.findViewById(R.id.content),false );
//        builder.setView(view);
//        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid().toString()).child("tasks").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                            if(dataSnapshot.getValue()==arrayList.get(position)){
//                                Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT);
//                                dataSnapshot.getRef().removeValue();
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                dialog.dismiss();
//                //Toast.makeText(getApplicationContext(),"Deleted "+arrayList.get(position),Toast.LENGTH_LONG).show();
//
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.create();
//        builder.show();
//    }
    // end of: Recycler view item on-long-press: open a delete(Tasks) warning for user


    public void setAlarm(long TotalMilli){
        alarmManager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(HomeActivity.this,Notification.class);
        pendingIntent=PendingIntent.getBroadcast(getApplicationContext(),taskModelClassArrayList.size(),intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,TotalMilli ,pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTimePicker(){
        picker=new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(12).setMinute(0).setTitleText("test").build();
        picker.show(getSupportFragmentManager(),"myid");
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(picker.getMinute()<10){
                    pickerMinute="0"+Integer.toString(picker.getMinute());
                }
                else {
                    pickerMinute=Integer.toString(picker.getMinute());
                }
                if(picker.getHour()>12){
                    TestTime =(Integer.toString(picker.getHour())+" : "+pickerMinute+" PM");
                }
                else{
                    if(picker.getHour()<10){
                        TestTime=("0"+Integer.toString(picker.getHour())+" : "+pickerMinute+" AM");
                    }
                    else{
                        TestTime=(Integer.toString(picker.getHour())+" : "+pickerMinute+" AM");
                    }

                }

               calendarNotification=Calendar.getInstance();
               calendarNotification.set(Calendar.HOUR_OF_DAY,picker.getHour());
               calendarNotification.set(Calendar.MINUTE,picker.getMinute());
               calendarNotification.set(Calendar.SECOND,0);
               calendarNotification.set(Calendar.MILLISECOND,0);
               notificationTime.setText(TestTime);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showTimePickerTargetTime(){
        picker=new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setHour(12).setMinute(0).setTitleText("test").build();
        picker.show(getSupportFragmentManager(),"myid");
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(picker.getMinute()<10){
                    pickerMinute="0"+Integer.toString(picker.getMinute());
                }
                else {
                    pickerMinute=Integer.toString(picker.getMinute());
                }
                if(picker.getHour()>12){
                    TestTime =(Integer.toString(picker.getHour())+" : "+pickerMinute+" PM");
                }
                else{
                    if(picker.getHour()<10){
                        TestTime=("0"+Integer.toString(picker.getHour())+" : "+pickerMinute+" AM");
                    }
                    else{
                        TestTime=(Integer.toString(picker.getHour())+" : "+pickerMinute+" AM");
                    }
                }

                targetCalender=Calendar.getInstance();
                targetCalender.set(Calendar.HOUR_OF_DAY,picker.getHour());
                targetCalender.set(Calendar.MINUTE,picker.getMinute());
                targetCalender.set(Calendar.SECOND,0);
                targetCalender.set(Calendar.MILLISECOND,0);
                targetTime.setText(TestTime);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void countDownStart() {
        Calendar backWardCalender=Calendar.getInstance();
        backWardCalender.add(Calendar.DAY_OF_YEAR,1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        EVENT_DATE_TIME=dateFormat.format(backWardCalender.getTime());
        handler=new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        hourText.setText(String.format("%02d", Hours).toString());
                        minuteText.setText(String.format("%02d", Minutes).toString());
                        secondText.setText(String.format("%02d", Seconds).toString());
                    } else {
                        databaseReference.child(firebaseAuth.getUid()).child("Tasks").removeValue();
                        adapter.notifyDataSetChanged();
                        onPause();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1*1000);
    }




    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
        countDownStart();
    }
}
//End of HomeActivity