/**
 * Copyright (C) 2012 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dreamlabs.smarttracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * This activity displays some help information on a topic.
 * It displays some text and provides a way to get back to the home activity.
 * The text to be displayed is determined by the text id argument passed to the activity.
 *
 */

public class TopicActivity extends Activity 
{

int mTextResourceId = 0;

/**
 * onCreate
 *
 * @param savedInstanceState Bundle
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    setContentView (R.layout.topic);

    // Read the arguments from the Intent object.
    Intent in = getIntent ();
    mTextResourceId = in.getIntExtra (HelpActivity.ARG_TEXT_ID, 0);
    if (mTextResourceId <= 0) mTextResourceId = R.string.no_help_available;

    TextView textView = (TextView) findViewById (R.id.topic_text);
    textView.setMovementMethod (LinkMovementMethod.getInstance());
    textView.setText (Html.fromHtml (getString (mTextResourceId)));
}
    
} // end class
