package abh.speedometers;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

public class MainActivity extends WearableActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private TextView speed_textview;
    NodeApi.GetConnectedNodesResult nodes;
    public TextView units_text;
    public CircleMenu circleMenu;
    public ImageView img;
    public static Button set_speed_limit;
    public static TextView display_speed_limit;
    public static TextView turnoff;
    public static TextView alert_on;
    public GoogleApiClient googleApiClient;
    public RelativeLayout loading;
    Vibrator vibrator;
    Boolean flag = true;
    LinearLayout linearLayout;
    public long result_speed;
    public String first_unit;
    public String current_unit;
    public long reminder;
    public int miles;
    public int speedlimit_alert;
    public boolean r=false;
    int nsize;
    BoxInsetLayout black_am;
    public TextView speedtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        Permissions permissions = new Permissions(this);
        if (!permissions.checkPermissionForCoarseLocation()) {
            permissions.requestPermissionForCoarseLocation();
        }
        if (!permissions.checkPermissionForFineLocation()) {
            permissions.requestPermissionForFineLocation();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        CheckWearableConnected x =new CheckWearableConnected();
        x.execute();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                speedtext= (TextView) findViewById(R.id.text);
                units_text = (TextView) findViewById(R.id.textkm);
                speed_textview = (TextView) findViewById(R.id.displayspeed);
                linearLayout = (LinearLayout) findViewById(R.id.overlay);
                set_speed_limit = (Button) findViewById(R.id.button3);
                display_speed_limit = (TextView) findViewById(R.id.speed_limit);
                turnoff = (TextView) findViewById(R.id.off);
                alert_on = (TextView) findViewById(R.id.alert);
                black_am= (BoxInsetLayout) findViewById(R.id.black);
                circleMenu = (CircleMenu) findViewById(R.id.circle_menu);
                circleMenu.setMainMenu(Color.parseColor("#FF0000"), R.mipmap.icon_setting, R.mipmap.icon_cancel);
                circleMenu.addSubMenu(Color.parseColor("#258CFF"), R.mipmap.mm)
                        .addSubMenu(Color.parseColor("#30A400"), R.mipmap.km)
                        .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.kn);
                first_unit = units_text.getText().toString();
                Log.e("unit value", "" + first_unit);
                circleMenu.setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        //current_unit=units_text.getText().toString();
                        switch (i) {
                            case 0:
                                units_text.setText("Miles/hr");
                                current_unit = units_text.getText().toString();
                                Log.e("current unit", current_unit);
                                if (first_unit.equals("Miles/hr")) {
                                    miles=speedlimit.input_speed_limit;
                                    display_speed_limit.setText(String.valueOf(speedlimit.input_speed_limit));
                                } else if (first_unit.equals("Kms/hr") && current_unit.equals("Miles/hr")) {
                                    miles = (int) (speedlimit.input_speed_limit * 0.621371);
                                    display_speed_limit.setText(String.valueOf(miles));
                                } else if (first_unit.equals("Knots/hr") && current_unit.equals("Miles/hr")) {
                                    miles = (int) (speedlimit.input_speed_limit * 1.15078);
                                    display_speed_limit.setText(String.valueOf(miles));
                                } else {
                                    miles = (int) (speedlimit.input_speed_limit * 2.236936);
                                    display_speed_limit.setText(String.valueOf(miles));
                                }

                                break;
                            case 1:
                                units_text.setText("Kms/hr");
                                current_unit = units_text.getText().toString();
                                Log.e("current unit", "" + current_unit);
                                if (first_unit.equals("Kms/hr")) {
                                    miles=speedlimit.input_speed_limit;
                                    display_speed_limit.setText(String.valueOf(speedlimit.input_speed_limit));
                                } else if (first_unit.equals("Miles/hr") && current_unit.equals("Kms/hr")) {

                                    miles = (int) (speedlimit.input_speed_limit * 1.60934);
                                    display_speed_limit.setText(String.valueOf(miles));
                                } else if (first_unit.equals("Knots/hr") && current_unit.equals("Kms/hr")) {

                                    miles = (int) (speedlimit.input_speed_limit * 1.852);
                                    display_speed_limit.setText(String.valueOf(miles));
                                } else {
                                    miles = (int) (speedlimit.input_speed_limit * 1.609344);
                                    display_speed_limit.setText(String.valueOf(miles));
                                }
                                break;
                            case 2:
                                units_text.setText("Knots/hr");
                                current_unit = units_text.getText().toString();
                                if (first_unit.equals("Knots/hr")) {
                                    miles=speedlimit.input_speed_limit;
                                    display_speed_limit.setText(String.valueOf(speedlimit.input_speed_limit));
                                } else if (first_unit.equals("Miles/hr") && current_unit.equals("Knots/hr")) {
                                    miles = (int) (speedlimit.input_speed_limit * 0.868976);
                                    display_speed_limit.setText(String.valueOf(miles));
                                } else if (first_unit.equals("Kms/hr") && current_unit.equals("Knots/hr")) {

                                    miles = (int) (speedlimit.input_speed_limit * 0.5399568);
                                    display_speed_limit.setText(String.valueOf(miles));
                                }
                                break;


                        }
                    }
                });

                circleMenu.setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {
                    @Override
                    public void onMenuOpened() {
                        set_speed_limit.setClickable(false);
                        if ((alert_on.getVisibility() == View.VISIBLE) && (turnoff.getVisibility() == View.VISIBLE)) {
                            alert_on.setVisibility(View.GONE);
                            turnoff.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onMenuClosed() {
                        set_speed_limit.setClickable(true);
                        if ((alert_on.getVisibility() == View.GONE) && (turnoff.getVisibility() == View.GONE)) {
                            alert_on.setVisibility(View.VISIBLE);
                            turnoff.setVisibility(View.VISIBLE);
                        }


                    }
                });
            }
        });
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentationl
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

    }
    public void setspeedlimit(View v) {
        flag=true;
        first_unit=units_text.getText().toString();
        Intent i = new Intent(MainActivity.this, speedlimit.class);
        startActivity(i);

    }
    public void alert_off(View v) {
        speedlimit.input_speed_limit=0;
        miles=0;
        flag=true;
        set_speed_limit.setVisibility(View.VISIBLE);
        display_speed_limit.setVisibility(View.INVISIBLE);
        alert_on.setVisibility(View.INVISIBLE);
        turnoff.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
       // Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
       googleApiClient.disconnect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speedlimit.input_speed_limit=0;
       finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        linearLayout.setVisibility(View.INVISIBLE);
        String speed_unit=units_text.getText().toString();
        if(speed_unit.equals("Miles/hr")){
            result_speed= Math.round(location.getSpeed()*2.236936);
            if(result_speed>35){
                result_speed+=3;
            }
        }
        else if (speed_unit.equals("Kms/hr")){
            result_speed=Math.round(location.getSpeed()*3.6);
            if(result_speed>50){
                result_speed+=3;
            }
        }
        else{
            result_speed=Math.round(location.getSpeed()*1.94384449244);
            if(result_speed>30){
                result_speed+=3;
            }
        }
        speed_textview.setText(String.valueOf(result_speed));
        if(miles==0){
         speedlimit_alert= abh.speedometers.speedlimit.input_speed_limit;
        }
        else {
            speedlimit_alert=miles;
        }


        if(speedlimit_alert!=0){
          if(flag){
            if(result_speed>speedlimit_alert){
                vibrator.vibrate(4000);
                Toast.makeText(this,"Go Slow",Toast.LENGTH_SHORT).show();
                reminder=result_speed;
                flag=false;
            }
        }}

        if (result_speed==reminder+5){
            flag=true;
        }
        if(result_speed<speedlimit_alert){
            flag=true;
        }
    }
    private class CheckWearableConnected extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
             nodes =
                     Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
            nsize=nodes.getNodes().size();

            if (nodes != null && nodes.getNodes().size() > 0 ) {
                if(nodes.getNodes().get(0).isNearby()){
               r=true;}
            }
           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!r){
                linearLayout.setVisibility(View.VISIBLE);
                linearLayout.bringToFront();
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        linearLayout.setVisibility(View.INVISIBLE);
                    }
                });
                }
            r=false;



            }
        }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        black_am.setBackgroundColor(Color.BLACK);
        speed_textview.setTextColor(Color.WHITE);
        units_text.setTextColor(Color.WHITE);
        speedtext.setTextColor(Color.WHITE);
        if (display_speed_limit.getVisibility()==View.VISIBLE){
            display_speed_limit.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        black_am.setBackgroundColor(Color.WHITE);
        speed_textview.setTextColor(Color.parseColor("#0000ff"));
        display_speed_limit.setTextColor(Color.RED);
        speedtext.setTextColor(Color.parseColor("#0000ff"));
        units_text.setTextColor(Color.parseColor("#0000ff"));
    }
}


