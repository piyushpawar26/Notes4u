package anonymous.notes4u;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TopicActivity extends AppCompatActivity {

    private String room_name;
    private String topic_name;
    private DatabaseReference mDatabase;
    private DatabaseReference topic;
    private LinearLayout lv_topic_images;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private int counter = 0;
    private EditText et_topic_comment;
    private ImageButton ib_topic_add_comment;
    private View.OnClickListener image_viewer;
    private View.OnClickListener click_comment;
    private LinearLayout ll_topic;
    private String[] username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        room_name = getIntent().getExtras().get("group_name").toString();
        topic_name = getIntent().getExtras().get("topic_name").toString();
        setTitle(topic_name);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        topic = mDatabase.child("users").child(firebaseUser.getUid()).child(room_name).child("all").child(topic_name);

        lv_topic_images = (LinearLayout) findViewById(R.id.lv_topic_images);
        ib_topic_add_comment = (ImageButton) findViewById(R.id.ib_topic_add_comment);
        et_topic_comment = (EditText) findViewById(R.id.et_topic_comment);
        ll_topic = (LinearLayout) findViewById(R.id.ll_topic);

        username = firebaseUser.getEmail().split("@");

        topic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    String item = String.valueOf(dsp.getValue());
                    ImageView image = new ImageView(TopicActivity.this);
                    image.setTag(counter);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 400);
                    image.setLayoutParams(layoutParams);
                    image.setPadding(10, 10, 10, 10);
                    Glide.with(TopicActivity.this).load(Uri.parse(item)).into(image);
                    image.setOnClickListener(image_viewer);
                    lv_topic_images.addView(image);
                    ++counter;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        image_viewer = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object id = view.getTag();
                Toast.makeText(TopicActivity.this, "Position: "+id.toString(), Toast.LENGTH_LONG).show();

                finish();
                Intent intent = new Intent(TopicActivity.this, ViewPicActivity.class);
                intent.putExtra("group_name", room_name);
                intent.putExtra("topic_name", topic_name);
                intent.putExtra("position", id.toString());
                startActivity(intent);
            }
        };

        ib_topic_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment = et_topic_comment.getText().toString();
                CardView temp_card = new CardView(TopicActivity.this);
                TextView temp = new TextView(TopicActivity.this);
                temp.setText(username[0] + ": " + comment);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                params.setMargins(25,0,25,25);
                temp_card.setLayoutParams(params);
                temp.setPadding(25, 25, 25, 25);
                temp.setTextSize(20);
                temp_card.addView(temp);
                temp_card.setOnClickListener(click_comment);
                ll_topic.addView(temp_card);
                et_topic_comment.setText("");

            }
        });

        click_comment = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(TopicActivity.this, "Comment clicked!", Toast.LENGTH_LONG).show();

            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.topic_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
