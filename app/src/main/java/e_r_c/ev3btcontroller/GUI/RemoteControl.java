package e_r_c.ev3btcontroller.GUI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;

import e_r_c.ev3btcontroller.R;
import e_r_c.ev3btcontroller.net.ActionSelector.ActionSelectorReply;
import e_r_c.ev3btcontroller.net.ActionSelector.DateTimeInt;
import e_r_c.ev3btcontroller.net.ActionSelector.UpdateListener;

public class RemoteControl extends AppCompatActivity implements UpdateListener<ActionSelectorReply>{
    Button left, right, forward, reverse, stop, livePrevButton, modeButton, BSOCButton;
    ClientSenderWrapper pHandler;
    String delayInmilliValue = "0";
    TextView delayInmilli, currentDirectionText, averageDelayValue;
    private int sentMilli, recievedMilli, allMilli = 0;
    ArrayList<DateTimeInt> times = new ArrayList<>();
    AlertDialog ad;
    ArrayList<Integer> delayCollection = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupButtons();
        pHandler = new ClientSenderWrapper();
        pHandler.addUpdateListener(this);
        Bundle extras = getIntent().getExtras();
        int bsocNum = extras.getInt("BSOCNum");
        int shrinkNum = extras.getInt("shrinkNum");
        pHandler.sendFirstMessage(bsocNum, shrinkNum);

    }

    private void popUpList(ArrayList<DateTimeInt> list){
        System.out.println("Entered popup");
        if (times != null && times.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.bsoc_popup, list);
            builder.setTitle(R.string.BSOC_popUp_title)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            DateTimeInt chosenItem = (DateTimeInt) lw.getAdapter().getItem(i);
                            pHandler.sendBSOC(chosenItem);
                        }
                    });
            ad = builder.create();
            ad.show();
        } else {
            toaster("No saved BSOCS found!");
        }

    }
    private void toaster(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    private void setupButtons(){
        left = (Button) findViewById(R.id.LeftButton);
        right = (Button) findViewById(R.id.RightButton);
        forward = (Button) findViewById(R.id.ForwardButton);
        reverse = (Button) findViewById(R.id.ReverseButton);
        modeButton = (Button) findViewById(R.id.SwitchModeButton);
        stop = (Button) findViewById(R.id.StopButton);
        delayInmilli = (TextView) findViewById(R.id.deleayInMilliTXT);
        livePrevButton = (Button) findViewById(R.id.LivePreview);
        currentDirectionText = (TextView) findViewById(R.id.currentDirectionText);
        averageDelayValue = (TextView) findViewById(R.id.averageDelayValue);
        left.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendLearnMove(left.getText().toString());
            }
        });
        right.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendLearnMove(right.getText().toString());
            }
        });
        forward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendLearnMove(forward.getText().toString());
            }
        });
        reverse.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendLearnMove(reverse.getText().toString());
            }
        });
        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendLearnMove(stop.getText().toString());
            }
        });
        modeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendEV3ToReceiving();
                new populateTimes(RemoteControl.this).execute();
            }
        });
        livePrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pHandler.sendToLivePreview();
            }
        });
    }

    @Override
    public void report(ActionSelectorReply reply) {
        final ActionSelectorReply reply1 = reply;
        sentMilli = pHandler.getSentTime();
        if (reply.isPulse()){
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    int received = LocalDateTime.now().getMillisOfDay();
                    int diff = received - sentMilli;
                    System.out.println(diff);
                    if (diff > 0){
                        delayInmilli.setText(msFormatter(diff));
                        allMilli += diff;
                        delayCollection.add(diff);
                        if (delayCollection.size() > 1){
                            int avgDelay = (allMilli - delayCollection.get(0))/delayCollection.size()-1;
                            averageDelayValue.setText(msFormatter(avgDelay));
                        }
                    }

                }
            };
            runOnUiThread(r);
        }
        if (reply.getPulseMove() != null){
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    currentDirectionText.setText(reply1.getPulseMove().uiName());
                }
            };
            runOnUiThread(r);
        }
    }

    private static String msFormatter(int ms){
        StringBuilder sb = new StringBuilder();
        if(ms > 0){
            if (ms > 1000){
                sb.append(ms/1000);
                sb.append(" sec ");
            }
            sb.append(ms%1000);
            sb.append(" ms");
        }
        return sb.toString();
    }

    public class populateTimes extends AsyncTask<Activity, Void, Void>{
        Context context;
        public populateTimes(Activity act){
            context = act;
        }
        @Override
        protected Void doInBackground(Activity... activities) {
            getBSOCS();
            return null;
        }

        @Override
        protected void onPreExecute() {
            ProgressDialog d = new ProgressDialog(context);
            d.setMessage("Retrieving BSOC");
            d.setTitle("Retrieving BSOCS");
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            System.out.println("PostExecute");
            popUpList(times);

        }
    }
    public boolean getBSOCS(){
        int startseconds = Calendar.getInstance().get(Calendar.SECOND);
        times = pHandler.getStoredBSOCs();
        while (Calendar.getInstance().get(Calendar.SECOND) - startseconds < 10 && times.size() == 0){
            times = pHandler.getStoredBSOCs();
        }
        return times.size() > 0;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK  && (ad == null || !ad.isShowing())) {
            pHandler.quit();
            System.out.println("Server has quit");
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remote_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
