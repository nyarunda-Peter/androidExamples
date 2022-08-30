package com.example.jetpackcomposebasic

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposebasic.ui.theme.JetPackComposeBasicTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetPackComposeBasicTheme {
                Conversation(messages = SampleData.conversationSample)
            }
        }
    }

    data class  Message(val author: String, val body: String)

    @Composable
    fun MessageCard(msg: Message){
        // Add padding around our message
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(R.drawable.profile_picture),
                contentDescription = null,
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    // Styling a profile border
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
                    )

            // Add a horizontal space between the image and the column
            Spacer(modifier = Modifier.width(8.dp))

            // We keep track if the message is expanded or not in this variable
            // Uses remember and mutableStateOf functions to store local state in memory
            // as well as track changes to the value
            var isExpanded by remember { mutableStateOf(false) }

            // surfaceColor will be updated gradually from one color to the other
            // the gradual animation is managed using animateColorAsState function
            val surfaceColor by animateColorAsState(if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
            )
            // We toggle the isExpanded variable when we click on this column
            Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                Text(
                    text = msg.author,
                    // Styling the title's color
                    color = MaterialTheme.colors.secondaryVariant,
                    // Styling title's typography
                    style = MaterialTheme.typography.subtitle2
                    )
                // Add a vertical space between the image and the column
                Spacer(modifier = Modifier.height(4.dp))

                // The message body text is wrapped around a Surface composable, with shape
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    elevation = 1.dp,
                    // surfaceColor color will be changing gradually from primary to surface
                    color = surfaceColor,
                    // animateContentSize will change the Surface size gradually
                    modifier = Modifier.animateContentSize().padding(1.dp)
                ) {
                    Text(
                        text = msg.body,
                        // Padding
                        modifier = Modifier.padding(all = 4.dp),
                        // Typography styling
                        style = MaterialTheme.typography.body2,
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1
                    )
                }
            }
        }
    }

    // Adding dark and light mode options
    @Preview(name = "Light Mode")
    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showBackground = true,
        name = "Dark Mode"
    )

    @Preview
    @Composable
    fun PreviewMessageCard() {
        JetPackComposeBasicTheme {
            Surface {
                MessageCard(
                    msg = Message("Colleague", "Hey, take a look at JetPack compose, it's great")
                )
            }
        }
    }

    // using Compose function LazyColumn
    @Composable
    fun Conversation(messages: List<Message>) {
        LazyColumn {
            items(messages) { message ->
                MessageCard(message)
            }
        }
    }

    @Preview
    @Composable
    fun PreviewConversation(){
        JetPackComposeBasicTheme{
            Conversation(messages = SampleData.conversationSample)
        }
    }
}

