package e_r_c.ev3btcontroller.GUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import e_r_c.ev3btcontroller.R;

public class ConnectPage extends AppCompatActivity {
    Button toControl, connectButton;
    EditText ev3NameTxt, ipAddressTxt, bsocNumberTxt, shrinkFactorTxt;

    ClientSenderWrapper handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_page);
        setupUI();
        toControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RemoteControl.class);
                startActivity(i);
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!ClientSenderWrapper.pingTest(getIpAddress())){
                    toaster("Unable to connect" + " to " + getIpAddress());
                }

                else {
                    Intent i = new Intent(getApplicationContext(), RemoteControl.class);
                    int shrink = 0;
                    int BSOC = 0;
                    if (bsocNumberTxt.getText().toString().isEmpty()){
                        BSOC = 64;
                    } else {
                        BSOC = Integer.parseInt(bsocNumberTxt.getText().toString());
                    }
                    if (!shrinkFactorTxt.getText().toString().isEmpty() &&
                            (Integer.parseInt(shrinkFactorTxt.getText().toString()) % 4 == 0 ||
                            Integer.parseInt(shrinkFactorTxt.getText().toString()) == 2 ||
                                    Integer.parseInt(shrinkFactorTxt.getText().toString()) == 1)){
                        shrink = Integer.parseInt(shrinkFactorTxt.getText().toString());
                    } else {
                        shrink = 8;
                        toaster("Invalid shrink (must be 2 or divisible by 4) Value Automatically set to "
                                + shrink);
                    }
                    i.putExtra("shrinkNum",shrink);
                    i.putExtra("BSOCNum",BSOC);
                    startActivity(i);
                }
            }
        } );

    }
    private void toaster(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    private void setupUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toControl = (Button) findViewById(R.id.toControlPage);
        connectButton = (Button) findViewById(R.id.ConnectButton);
        ev3NameTxt = (EditText) findViewById(R.id.Ev3Name);
        ipAddressTxt = (EditText) findViewById(R.id.ipAddress);
        bsocNumberTxt = (EditText) findViewById(R.id.BSOCNumber);
        shrinkFactorTxt = (EditText) findViewById(R.id.shrinkFactorNumber);
    }
    private String getIpAddress(){
        if (ipAddressTxt.getText().length() == 0){
            return "10.0.1.1";
        }
        return ipAddressTxt.getText().toString();
    }
}
