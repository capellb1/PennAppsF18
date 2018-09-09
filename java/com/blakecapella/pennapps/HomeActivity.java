package com.blakecapella.pennapps;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.Pwm;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;


/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";
    private static final String A_BUTTON_PIN_NAME = "GPIO6_IO14"; //declare the address of the button being listened to
    private static final String B_BUTTON_PIN_NAME = "GPIO6_IO15"; //declare the address of the button being listened to
    private static final String C_BUTTON_PIN_NAME = "GPIO2_IO07"; //declare the address of the button being listened to
    private static final String R_LED_NAME = "GPIO2_IO02";
    private static final String G_LED_NAME = "GPIO2_IO00";
    private static final String B_LED_NAME = "GPIO2_IO05";
    private static final String SPEAKER_NAME = "PWM2";

    private Gpio a_button; //define the button as a gpio obj
    private Gpio b_button;
    private Gpio c_button;
    private Gpio red_led;
    private Gpio green_led;
    private Gpio blue_led;
    private Pwm speaker;

    public int path = 0;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Happens at boot, initial setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PeripheralManager manager = PeripheralManager.getInstance();
        Log.d(TAG, "Available GPIO: " + manager.getGpioList());

        try{
            //use the peripheral manager to open a connection to the button on startup
            a_button = manager.openGpio(A_BUTTON_PIN_NAME);
            a_button.setDirection(Gpio.DIRECTION_IN); //declare it is an input
            a_button.setEdgeTriggerType(Gpio.EDGE_FALLING); //watch for both rising and falling edges
            a_button.setActiveType(Gpio.ACTIVE_LOW); //set a button push = true
            a_button.registerGpioCallback(a_callback);

            b_button = manager.openGpio(B_BUTTON_PIN_NAME);
            b_button.setDirection(Gpio.DIRECTION_IN); //declare it is an input
            b_button.setEdgeTriggerType(Gpio.EDGE_FALLING); //watch for both rising and falling edges
            b_button.setActiveType(Gpio.ACTIVE_LOW); //set a button push = true
            b_button.registerGpioCallback(b_callback);

            c_button = manager.openGpio(C_BUTTON_PIN_NAME);
            c_button.setDirection(Gpio.DIRECTION_IN); //declare it is an input
            c_button.setEdgeTriggerType(Gpio.EDGE_FALLING); //watch for both rising and falling edges
            c_button.setActiveType(Gpio.ACTIVE_LOW); //set a button push = true
            c_button.registerGpioCallback(c_callback);

            red_led = manager.openGpio(R_LED_NAME);
            red_led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            green_led = manager.openGpio(G_LED_NAME);
            green_led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            blue_led = manager.openGpio(B_LED_NAME);
            blue_led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            speaker = manager.openPwm(SPEAKER_NAME);



            try {
                speaker.setPwmDutyCycle(50);
            } catch (IOException e) {
                throw new IllegalStateException(SPEAKER_NAME + " bus cannot be configured.", e);
            }




        }catch(IOException e){
            Log.w(TAG, "Error Creating GPIO", e);
        }
    }

    private GpioCallback a_callback = new GpioCallback() {
        @Override
        //****runs below method every time that the described edge trigger is detected***
        public boolean onGpioEdge(Gpio gpio) {
            try{
                Log.i(TAG,"GPIO changed, button" + gpio.getValue());
                boolean buttonValue = gpio.getValue();
                red_led.setValue(buttonValue);
                try {
                    speaker.setPwmFrequencyHz(261.63); // C
                    speaker.setEnabled(true);
                    SystemClock.sleep(15);
                    speaker.setEnabled(false);
                } catch (IOException e) {
                    throw new IllegalStateException(SPEAKER_NAME + " bus cannot play note.", e);
                }

                proceed('a');

            }catch(IOException e){
                Log.w(TAG,"Error Reading GPIO");
            }

            return true;//keep callback active after for repeated use
        }
    };

    private GpioCallback b_callback = new GpioCallback() {
        @Override
        //****runs below method every time that the described edge trigger is detected***
        public boolean onGpioEdge(Gpio gpio) {
            try{
                Log.i(TAG,"GPIO changed, button" + gpio.getValue());
                boolean buttonValue = gpio.getValue();
                green_led.setValue(buttonValue);
                try {
                    speaker.setPwmFrequencyHz(293.66); // D
                    speaker.setEnabled(true);
                    SystemClock.sleep(15);
                    speaker.setEnabled(false);
                } catch (IOException e) {
                    throw new IllegalStateException(SPEAKER_NAME + " bus cannot play note.", e);
                }
            }catch(IOException e){
                Log.w(TAG,"Error Reading GPIO");
            }

            proceed('b');

            return true;//keep callback active after for repeated use
        }
    };

    private GpioCallback c_callback = new GpioCallback() {
        @Override
        //****runs below method every time that the described edge trigger is detected***
        public boolean onGpioEdge(Gpio gpio) {
            try{
                Log.i(TAG,"GPIO changed, button" + gpio.getValue());
                boolean buttonValue = gpio.getValue();
                blue_led.setValue(buttonValue);
                try {
                    speaker.setPwmFrequencyHz(329.62); // E
                    speaker.setEnabled(true);
                    SystemClock.sleep(15);
                    speaker.setEnabled(false);
                } catch (IOException e) {
                    throw new IllegalStateException(SPEAKER_NAME + " bus cannot play note.", e);
                }
            }catch(IOException e){
                Log.w(TAG,"Error Reading GPIO");
            }

            proceed('c');

            return true;//keep callback active after for repeated use
        }
    };

    //clean up
    protected void onDestroy(){
        super.onDestroy();
        //close the button
        if(a_button != null){ //if button was initiated
            a_button.unregisterGpioCallback(a_callback); //??
            try{
                a_button.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing GPIO");
            }
        }

        if(b_button != null){ //if button was initiated
            b_button.unregisterGpioCallback(b_callback); //??
            try{
                b_button.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing GPIO");
            }
        }

        if(c_button != null){ //if button was initiated
            c_button.unregisterGpioCallback(c_callback); //??
            try{
                c_button.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing GPIO");
            }
        }

        if(red_led != null){
            try{
                red_led.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing GPIO");
            }
        }

        if(green_led != null){
            try{
                green_led.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing GPIO");
            }
        }

        if(blue_led != null){
            try{
                blue_led.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing GPIO");
            }
        }

        if(speaker != null){
            try{
                speaker.close();
            }catch (IOException e){
                Log.w(TAG,"Error Closing PWM");
            }
        }
    }

    protected void proceed(char key) {
        if (path == 0) {
            TextView textView = (TextView) findViewById(R.id.textView2);
            textView.setText(""); //set text for text view
            TextView textView2 = (TextView) findViewById(R.id.textView3);
            textView2.setText(""); //set text for text view

            TextView textView3 = (TextView) findViewById(R.id.textView4);
            textView3.setText(R.string.prompt); //set text for text view

            TextView textView1 = (TextView) findViewById(R.id.textView5);
            textView1.setText(R.string.option1); //set text for text view

            TextView textView4 = (TextView) findViewById(R.id.textView6);
            textView4.setText(""); //set text for text view
            path++;
        }

        else if (path == 1) {
            if (key == 'a') {
                //yourself
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.phone);
                TextView textView = (TextView) findViewById(R.id.textView4);
                textView.setText(R.string.self_alert); //set text for text view
                TextView textView2 = (TextView) findViewById(R.id.textView5);
                textView2.setText(R.string.called911); //set text for text view
                final TextView textView3 = (TextView) findViewById(R.id.textView6);
                new CountDownTimer(420000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long minutes = millisUntilFinished / 60000;
                        long sec = (millisUntilFinished - (minutes*60000))/1000;
                        textView3.setText("Time remaining: " + minutes + "min " + sec + "s");

                    }

                    public void onFinish() {
                        textView3.setText("EMS Should Arrive Momentarily");
                    }
                }.start();

                path=8;
            }

            if (key == 'b') {
                //a friend
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.friend);
                TextView textView = (TextView) findViewById(R.id.textView4);
                textView.setText(R.string.prompt2); //set text for text view
                TextView textView2 = (TextView) findViewById(R.id.textView5);
                textView2.setText(R.string.option2); //set text for text view
                TextView textView3 = (TextView) findViewById(R.id.textView6);
                textView3.setText(""); //set text for text view
                path++;
            }

            if (key == 'c') {
                //cancel
                path = 0;
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.alfred);

                TextView textView = (TextView) findViewById(R.id.textView3);
                textView.setText(R.string.alfred); //set text for text view

                TextView textView1 = (TextView) findViewById(R.id.textView2);
                textView1.setText(R.string.medical_assist); //set text for text view

                TextView textView2 = (TextView) findViewById(R.id.textView4);
                textView2.setText(""); //set text for text view

                TextView textView3 = (TextView) findViewById(R.id.textView5);
                textView3.setText(""); //set text for text view

                TextView textView4 = (TextView) findViewById(R.id.textView6);
                textView4.setText(""); //set text for text view

            }

        }

        else if (path == 2) {
            if (key == 'a') {
                //alchohol
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.listen_breath);
                TextView textView = (TextView) findViewById(R.id.textView4);
                textView.setText(R.string.called911); //set text for text view
                TextView textView2 = (TextView) findViewById(R.id.textView5);
                textView2.setText(R.string.responsiveness); //set text for text view
                TextView textView3 = (TextView) findViewById(R.id.textView6);
                textView3.setText(R.string.option3); //set text for text view
                path++;
            }

            if (key == 'b') {
                //seizure
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.seize);
                TextView textView = (TextView) findViewById(R.id.textView4);
                textView.setText(R.string.called911); //set text for text view
                TextView textView2 = (TextView) findViewById(R.id.textView5);
                textView2.setText(R.string.clear_area); //set text for text view
                final TextView textView3 = (TextView) findViewById(R.id.textView6);
                new CountDownTimer(420000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long minutes = millisUntilFinished / 60000;
                        long sec = (millisUntilFinished - (minutes*60000))/1000;
                        textView3.setText("Time remaining: " + minutes + "min " + sec + "s");

                    }

                    public void onFinish() {
                        textView3.setText("EMS Should Arrive Momentarily");
                    }
                }.start();

                path = 8; //no further progress
            }

            if (key == 'c') {
                //cancel
                path = 0;
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.alfred);

                TextView textView = (TextView) findViewById(R.id.textView3);
                textView.setText(R.string.alfred); //set text for text view

                TextView textView1 = (TextView) findViewById(R.id.textView2);
                textView1.setText(R.string.medical_assist); //set text for text view

                TextView textView2 = (TextView) findViewById(R.id.textView4);
                textView2.setText(""); //set text for text view

                TextView textView3 = (TextView) findViewById(R.id.textView5);
                textView3.setText(""); //set text for text view

                TextView textView4 = (TextView) findViewById(R.id.textView6);
                textView4.setText(""); //set text for text view

            }

        }

        else if (path == 3) {
            if (key == 'a') {
                //unconscious
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.recov);
                TextView textView = (TextView) findViewById(R.id.textView4);
                textView.setText(R.string.position_body); //set text for text view
                TextView textView2 = (TextView) findViewById(R.id.textView5);
                textView2.setText(R.string.press_any); //set text for text view
                TextView textView3 = (TextView) findViewById(R.id.textView6);
                textView3.setText(""); //set text for text view
                path++;
            }

            if (key == 'b') {
                //no breath
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.breath);
                TextView textView = (TextView) findViewById(R.id.textView4);
                textView.setText(R.string.mouth); //set text for text view
                TextView textView2 = (TextView) findViewById(R.id.textView5);
                textView2.setText(R.string.called911); //set text for text view
                final TextView textView3 = (TextView) findViewById(R.id.textView6);
                new CountDownTimer(420000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long minutes = millisUntilFinished / 60000;
                        long sec = (millisUntilFinished - (minutes*60000))/1000;
                        textView3.setText("Time remaining: " + minutes + "min " + sec + "s");

                    }

                    public void onFinish() {
                        textView3.setText("EMS Should Arrive Momentarily");
                    }
                }.start();
                path=8;
            }

            if (key == 'c') {
                //cancel
                path = 0;
                ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
                myImage.setImageResource(R.drawable.alfred);

                TextView textView = (TextView) findViewById(R.id.textView3);
                textView.setText(R.string.alfred); //set text for text view

                TextView textView1 = (TextView) findViewById(R.id.textView2);
                textView1.setText(R.string.medical_assist); //set text for text view

                TextView textView2 = (TextView) findViewById(R.id.textView4);
                textView2.setText(""); //set text for text view

                TextView textView3 = (TextView) findViewById(R.id.textView5);
                textView3.setText(""); //set text for text view

                TextView textView4 = (TextView) findViewById(R.id.textView6);
                textView4.setText(""); //set text for text view

            }

        }

        else if(path ==4){
            //continue from unconscious
            //heat
            ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
            myImage.setImageResource(R.drawable.cold_warm);
            TextView textView = (TextView) findViewById(R.id.textView4);
            textView.setText(R.string.heat_body); //set text for text view
            TextView textView2 = (TextView) findViewById(R.id.textView5);
            textView2.setText(R.string.press_any); //set text for text view
            TextView textView3 = (TextView) findViewById(R.id.textView6);
            textView3.setText(""); //set text for text view
            path++;
        }

        else if(path ==5){
            //continue from heat
            //monitor
            ImageView myImage = (ImageView) findViewById(R.id.my_image_view);
            myImage.setImageResource(R.drawable.pulse);
            TextView textView = (TextView) findViewById(R.id.textView4);
            textView.setText(R.string.monitor_body); //set text for text view
            TextView textView2 = (TextView) findViewById(R.id.textView5);
            textView2.setText(""); //set text for text view
            final TextView textView3 = (TextView) findViewById(R.id.textView6);
            new CountDownTimer(420000, 1000) {

                public void onTick(long millisUntilFinished) {
                   long minutes = millisUntilFinished / 60000;
                   long sec = (millisUntilFinished - (minutes*60000))/1000;
                    textView3.setText("Time remaining: " + minutes + "min " + sec + "s");

                }

                public void onFinish() {
                    textView3.setText("EMS Should Arrive Momentarily");
                }
            }.start();


            path=8;
        }

    }
}




