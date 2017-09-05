package abh.speedometers;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import static abh.speedometers.MainActivity.alert_on;
import static abh.speedometers.MainActivity.display_speed_limit;
import static abh.speedometers.MainActivity.set_speed_limit;
import static abh.speedometers.MainActivity.turnoff;

public class speedlimit extends WearableActivity {

    private TextView mTextView;
    public static int input_speed_limit;
    public ImageButton ok;
    public ImageButton cancel;
    public NumberPicker np;
    public TextView alert;
    public TextView turnoff_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAmbientEnabled();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedlimit);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                ok=(ImageButton)findViewById(R.id.ok);
                cancel=(ImageButton)findViewById(R.id.cancel);

                final String[] nums = new String[31];
                for (int i = 0; i < nums.length; i++) {
                    nums[i] = Integer.toString(i * 5);
                }

                // Set the max and min values of the numberpicker, and give it the
                // array of numbers created above to be the displayed numbers
                np = (NumberPicker) findViewById(R.id.np);
                np.setMaxValue(30);
                np.setMinValue(0);
                np.setWrapSelectorWheel(true);
                np.setDisplayedValues(nums);
                np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                np.setEnabled(true);

            }
        });
    }
    public  void Cancel(View view){
        finish();
    }
    public void Ok(View view){
        Log.e("this","value"+np.getValue());
        input_speed_limit=(np.getValue())*5;
        set_speed_limit.setVisibility(View.INVISIBLE);
        display_speed_limit.setText(String.valueOf(input_speed_limit));
        display_speed_limit.setVisibility(View.VISIBLE);
        alert_on.setVisibility(View.VISIBLE);
        turnoff.setVisibility(View.VISIBLE);
        finish();
    }
}