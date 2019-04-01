package anonymous.notes4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsList extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private ListView lv_list_list;
    private ArrayList group_names;
    private ArrayAdapter adapter;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        setTitle("Groups");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null){

            GroupsList.this.finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }else{

            String uid = firebaseUser.getUid();
            lv_list_list = (ListView) findViewById(R.id.lv_list_list);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            group_names = new ArrayList<>();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, group_names);
            lv_list_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lv_list_list.setAdapter(adapter);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dsp : dataSnapshot.getChildren()){
                        group_names.add(String.valueOf(dsp.getKey()));
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            lv_list_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String selected_group = (String) group_names.get(i);
                    Intent intent = new Intent(GroupsList.this, UserAreaActivity.class);
                    intent.putExtra("group_name", selected_group);
                    startActivity(intent);
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_content_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_add){
            Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.menu_logout){
            firebaseAuth.signOut();
            finish();
            Intent intent = new Intent(GroupsList.this, LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
