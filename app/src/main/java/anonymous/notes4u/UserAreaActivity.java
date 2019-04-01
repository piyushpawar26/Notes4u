package anonymous.notes4u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class UserAreaActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView profile_user;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ListView lv_user_recent;
    private ArrayAdapter adapter;
    private ArrayList topics;
    private DatabaseReference mDatabase;
    private DatabaseReference all;
    private FirebaseUser firebaseUser;
    private String room_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        room_name = getIntent().getExtras().get("group_name").toString();
        setTitle(room_name);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        all = mDatabase.child("users").child(firebaseUser.getUid()).child(room_name).child("all");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_user_recent = (ListView) findViewById(R.id.lv_user_recent);
        topics = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics);
        lv_user_recent.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_user_recent.setAdapter(adapter);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        View hView =  navigationView.getHeaderView(0);
        profile_user = (TextView) hView.findViewById(R.id.profile_user);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int item_id = item.getItemId();
                if(item_id == R.id.nav_menu_about){
                    Intent intent = new Intent(UserAreaActivity.this, AboutGroup.class);
                    intent.putExtra("group_name", room_name);
                    startActivity(intent);
                }

                return true;
            }
        });

        profile_user.setText(firebaseAuth.getCurrentUser().getEmail() + " ");

        all.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    adapter.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lv_user_recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_topic = (String) topics.get(i);
                Intent intent = new Intent(UserAreaActivity.this, TopicActivity.class);
                intent.putExtra("group_name", room_name);
                intent.putExtra("topic_name", selected_topic);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_content_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }else if(item.getItemId() == R.id.menu_add){
            finish();
            Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
            intent.putExtra("group_name", room_name);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
