package com.dreamlabs.smarttracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * This is the Help activity for the application.
 * It displays some basic help information. Clicking on the icons takes the user to a detailed description.
 *
 */

public class HelpActivity extends Activity 
{

static public final String ARG_TEXT_ID = "text_id";

/**
 * onCreate - called when the activity is first created.
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);

    // Set up so that formatted text can be in the help_page_intro text and so that html links are handled.
   /* TextView textView = (TextView) findViewById (R.id.help_page_intro);
    if (textView != null) {
       textView.setMovementMethod (LinkMovementMethod.getInstance());
       textView.setText (Html.fromHtml (getString (R.string.help_page_intro_html)));
    }*/
}
    

/**
 */
// Methods

/**
 * Handle the click of one of the help buttons on the page.
 * Start an activity to display the help text for the topic selected.
 * 
 * @param v View
 * @return void
 */

public void onClickHelp (View v)
{
    int id = v.getId ();
    int textId = -1;
    switch (id) {
      case R.id.help_button1 :
           textId = R.string.broadcast;
           break;
      case R.id.help_button2 :
           textId = R.string.topic_section2;
           break;
      case R.id.help_button3 :
           textId = R.string.topic_section3;
           break;
      case R.id.help_button4 :
           textId = R.string.topic_section4;
           break;
      default: 
    	   break;
    }

    if (textId >= 0) startInfoActivity (textId);
    else toast ("Detailed Help for that topic is not available.", true);
}

/**
 * Start a TopicActivity and show the text indicated by argument 1.
 * 
 * @param textId int - resource id of the text to show
 * @return void
 */

public void startInfoActivity (int textId)
{
    if (textId >= 0) {
       Intent intent = (new Intent(this, TopicActivity.class));
       intent.putExtra (ARG_TEXT_ID, textId);
       startActivity (intent);
    } else {
      toast ("No information is available for topic: " + textId, true);
    }
} // end startInfoActivity

/**
 * Show a string on the screen via Toast.
 * 
 * @param msg String
 * @param longLength boolean - show message a long time
 * @return void
 */

public void toast (String msg, boolean longLength)
{
    Toast.makeText (getApplicationContext(), msg, 
                    (longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)
                   ).show ();
}

} // end class
