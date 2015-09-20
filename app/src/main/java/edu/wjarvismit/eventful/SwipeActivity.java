package edu.wjarvismit.eventful;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;
/*
used code from StackOverflow. http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
 */
public class SwipeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    TextView event_Title;
    TextView event_location;
    TextView event_time;
    TextView event_description;
    Date start_Date;
    Date end_Date;
    Integer index;
    int lengthOfEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        event_Title=(TextView)findViewById(R.id.event_title);
        event_location=(TextView)findViewById(R.id.event_location);
        event_time=(TextView)findViewById(R.id.event_time);
        event_description=(TextView)findViewById(R.id.event_one_line_description);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);
        Intent mIntent = getIntent();
        index = mIntent.getIntExtra("index", 0);
        getDataFromParse();
    }
    public void getDataFromParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> markers, ParseException e) {

                if (e == null) {
                    // your logic here
                    int lengthOfEvents=markers.size();
                    ParseObject object = markers.get(index.intValue());
                    String eventTitle = object.getString("eventTitle");
                    String eventDescription = object.getString("eventDescription");
                    //ParseGeoPoint eventLocation=object.getParseGeoPoint("eventDescription");
                    String location = object.getString("locationInString");
                    //ParseFile image = object.getParseFile("eventImage");
                    Date startDate = object.getDate("startTime");
                    start_Date = startDate;
                    Date endDate = object.getDate("endTime");
                    end_Date = endDate;
                    Log.d(DEBUG_TAG,""+eventTitle);
                    event_Title.setText(eventTitle);
                    event_location.setText(location);
                    event_time.setText(startDate.toString() + " - " + endDate.toString());
                    event_description.setText(eventDescription);

                } else {
                    // handle Parse Exception here
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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
    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        final int SWIPE_THRESHOLD = 100;
        final int SWIPE_VELOCITY_THRESHOLD = 100;
        boolean result = false;
        try {
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
    public void onSwipeRight() {
        Log.d(DEBUG_TAG,"onSwipeRight ");
        String e_title=(String)event_Title.getText();
        String e_location=(String)event_location.getText();
        String e_time=(String)event_time.getText();
        //insertEventIntoCalendar(start_Date,end_Date,e_title,e_location)
        goToNextEvent();
    }

    public void onSwipeLeft() {
        //Log.d(DEBUG_TAG,"onSwipeLeft ");
        goToNextEvent();
    }

    public void onSwipeTop() {
        //Log.d(DEBUG_TAG,"onSwipeTop ");
        goToNextEvent();
    }

    public void onSwipeBottom() {
        goToNextEvent();
        //Log.d(DEBUG_TAG,"onSwipeBottom ");
    }
    public void goToNextEvent(){
        if (lengthOfEvents>=this.index){

        }else{
        Intent intent = new Intent(this, SwipeActivity.class);
        int nextIndex=this.index+1;
        Log.d(DEBUG_TAG,""+nextIndex);
        intent.putExtra("index",(Integer)nextIndex);
        startActivity(intent);}
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        //Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }
}
