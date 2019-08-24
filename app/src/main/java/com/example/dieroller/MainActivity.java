package com.example.dieroller;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements SensorListener {

    private Button dieRollBtn, dieAddBtn, dieRemoveBtn, dieReplayBtn;
    private TextView pointView, totalView, overAllScoreView;
    private ImageView[] dies = new ImageView[6];

    private int rollPoints[] = {0, 0, 0, 0, 0, 0};
    private boolean isDieLastRoll[] = {false, false, false, false, false, false};
    private int rollTotal = 0;
    private int numberofDiesInView = 0;
    private int overall = 0;
    private String rollPointStr = "";

    SensorManager sensorManager;
    Sensor rotationSensor;
    SensorEventListener rotationListener;
    private long lastUpdate = 0;
    private float last_x = 0, last_y = 0, last_z = 0, x = 0, y = 0, z = 0;

    public static final Random ROLL = new Random();
    public static final Random ROLL_COUNT = new Random();
    private static final int ALLOWED_DIES = 6;
    private static final int SHAKE_THRESHOLD = 600;
    private static final int SHAKE_TIME_INTERVAL = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        intializeLayoutAndListener();
        intializeSensor();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rotationListener);

    }

    public void onAccuracyChanged(int sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        // Reference : https://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            // Interval between device shake is checked.
            if ((curTime - lastUpdate) > SHAKE_TIME_INTERVAL) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    playGame();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }


    /*Intializes the layout and binds the listener*/
    protected void intializeLayoutAndListener() {
        dieRollBtn = (Button) findViewById(R.id.rollDices);
        dieAddBtn = (Button) findViewById(R.id.addDie);
        dieRemoveBtn = (Button) findViewById(R.id.removeDie);
        dieReplayBtn = (Button) findViewById(R.id.replay);

        // The app launches with two dice
        ImageView newDie = createImageView();
        setDieValue(newDie, numberofDiesInView);
        newDie = createImageView();
        setDieValue(newDie, numberofDiesInView);

        dieRollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playGame();
            }
        });
        dieAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createImageView();
            }
        });
        dieRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeDie();
            }
        });
        dieReplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rePlay();
            }
        });
    }

    /* Intialize accelerometer sensor*/
    protected void intializeSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public ImageView createImageView() {
        if (numberofDiesInView < ALLOWED_DIES) {
            ImageView newDie = new ImageView(this);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams((int) getResources().getDimension(R.dimen.die_height), (int) getResources().getDimension(R.dimen.die_height));

            // Set Margin for first two child in the flex view
            if ((numberofDiesInView + 1) % 3 != 0) {
                params.rightMargin = (int) getResources().getDimension(R.dimen.die_margin);
            }
            params.topMargin = (int) getResources().getDimension(R.dimen.die_marginTop);
            newDie.setLayoutParams(params);
            newDie.setImageResource(getResources().getIdentifier("die_face_" + randomDiceValue(), "drawable", "com.example.dieroller"));

            newDie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playGame();
                }
            });

            // Adds the newly created imageview to the flex layout
            FlexboxLayout dieLayout = findViewById(R.id.die_layout);
            dieLayout.addView(newDie);
            dies[numberofDiesInView] = newDie;
            numberofDiesInView++;

            return newDie;
        }

        Toast.makeText(getApplicationContext(),getResources().getString(R.string.max_err),Toast.LENGTH_SHORT).show();
        return null;
    }

    public void removeDie() {
        if(numberofDiesInView == 1) {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.min_err),Toast.LENGTH_SHORT).show();
            return;
        }

        FlexboxLayout dieLayout = findViewById(R.id.die_layout);
        dieLayout.removeViewAt(numberofDiesInView-1);
        numberofDiesInView--;
    }

    public void playGame() {
        for (int i = 0; i < numberofDiesInView; i++) {
            rollPoints[i] = 0;
            isDieLastRoll[i] = false;
        }

        for (int i = 0; i < numberofDiesInView; i++) {
            animateDieRoll(dies[i], i);
        }

    }

    public void rePlay() {
        for (int i = 0; i < numberofDiesInView; i++) {
            rollPoints[i] = 0;
        }
        rollPointStr = joinPoints();
        rollTotal = 0;
        overall = 0;
        updateScoreBoard();
    }

    public String joinPoints() {
        StringBuilder sb = new StringBuilder(256);
        rollTotal = 0;

        for (int i = 0; i < numberofDiesInView; i++) {
            sb.append(" ").append(rollPoints[i]);
            rollTotal = rollTotal + rollPoints[i];
        }

        return sb.toString();
    }

    public void updateScore(int index, int score) {
        rollPoints[index] = score;
        rollPointStr = joinPoints();
        overall = overall + score;
        updateScoreBoard();

        if(index == (numberofDiesInView -1)) {
            double scoreWeight = (double) rollTotal / (numberofDiesInView * 6);
            if (scoreWeight > 0.9) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_90), Toast.LENGTH_SHORT).show();

            } else if (scoreWeight > 0.7) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_70), Toast.LENGTH_SHORT).show();

            } else if (scoreWeight > 0.4) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_gt_40), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_lt_40), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void updateScoreBoard() {
        overAllScoreView = (TextView) findViewById(R.id.overall_score);
        overAllScoreView.setText(Integer.toString(overall));
        pointView = (TextView) findViewById(R.id.points);
        pointView.setText(rollPointStr);
        totalView = (TextView) findViewById(R.id.total_points);
        totalView.setText(Integer.toString(rollTotal));
    }

    public void animateDieRoll(ImageView dieImg, int index) {

        final ImageView animDie = dieImg;
        final int rollIndex = index;
        final Animation rollAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.roll_effect);

        final Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            final MediaPlayer roll= MediaPlayer.create(MainActivity.this,R.raw.roll);
            @Override
            public void onAnimationStart(Animation animation) {
                roll.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setDieValue(animDie, rollIndex);
                roll.stop();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        };

        rollAnimation.setAnimationListener(animationListener);
        animDie.startAnimation(rollAnimation);
        int count = ROLL_COUNT.nextInt(3);
        for (int i = 0; i <= count; i++) {
            final boolean isLastRoll = i == count ? true : false;
            // Set delay for each roll
            int delay =  600 * (i+1);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animDie.startAnimation(rollAnimation);
                    isDieLastRoll[rollIndex] = isLastRoll;
                }
            }, delay);
        }
    }

    public void setDieValue(ImageView img, int index) {
        int random = randomDiceValue();
        int res = getResources().getIdentifier("die_face_" + random, "drawable", "com.example.dieroller");
        img.setImageResource(res);

        if (isDieLastRoll[index] == true) {
            updateScore(index, random);
        }
    }

    public static int randomDiceValue() {
        return ROLL.nextInt(6) + 1;
    }
}
