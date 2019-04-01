package anonymous.notes4u;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AboutGroup extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ListView lv_aboutgroup_addmembers;
    private ArrayList<String> members_add;
    private ArrayAdapter adapter;
    private ImageButton ib_aboutgroup;
    private EditText et_aboutgroup_add;
    private LinearLayout ll_aboutgroup_members;
    private Switch switch1;
    private Button btn_aboutgroup_add;
    private DatabaseReference temp;
    private DatabaseReference members;
    private Map<String, String> users;
    private String[] mems_add;
    private String uid;
    private String[] new_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_group);

        final String room_name = getIntent().getExtras().get("group_name").toString();
        setTitle(room_name);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        members = mDatabase.child("groups").child(room_name);
        temp = mDatabase.child("database");

        temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ll_aboutgroup_members = (LinearLayout) findViewById(R.id.ll_aboutgroup_members);
        lv_aboutgroup_addmembers = (ListView) findViewById(R.id.lv_aboutgroup_addmembers);
        members_add = new ArrayList<>();
        adapter = new ArrayAdapter(AboutGroup.this, android.R.layout.simple_list_item_1, members_add);
        lv_aboutgroup_addmembers.setAdapter(adapter);
        ib_aboutgroup = (ImageButton) findViewById(R.id.ib_aboutgroup);
        et_aboutgroup_add = (EditText) findViewById(R.id.et_aboutgroup_add);
        switch1 = (Switch) findViewById(R.id.switch1);
        btn_aboutgroup_add = (Button) findViewById(R.id.btn_aboutgroup_add);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Toast.makeText(AboutGroup.this, "Muted!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AboutGroup.this, "Unmuted!", Toast.LENGTH_LONG).show();
                }
            }
        });


        ib_aboutgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_aboutgroup_add.getText().toString();

                if(!TextUtils.isEmpty(email) && email.length() > 0){
                    adapter.add(email);
                    adapter.notifyDataSetChanged();
                    et_aboutgroup_add.setText("");
                }

            }
        });

        members.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    String item = String.valueOf(dsp.getKey());
                    TextView temp_mems = new TextView(AboutGroup.this);
                    temp_mems.setText(item);
                    temp_mems.setPadding(25, 25, 25, 25);
                    temp_mems.setTextSize(23);
                    View v = new View(AboutGroup.this);
                    v.setMinimumHeight(1);
                    v.setBackgroundColor(getResources().getColor(R.color.grayish));
                    ll_aboutgroup_members.addView(v);
                    ll_aboutgroup_members.addView(temp_mems);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_aboutgroup_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mems_add = members_add.toArray(new String[members_add.size()]);
                String registered = null;

                if(mems_add != null){

                    for(int i=0; i<mems_add.length; i++){

                        new_email = mems_add[i].split("@");
                        for(Map.Entry<String, String> entry : users.entrySet()){
                            if(entry.getKey().equals(new_email[0])){
                                registered = new_email[0];
                                uid = entry.getValue();
                            }
                        }

                        if(uid != null && registered != null){

                            DatabaseReference users_entry_to_group = mDatabase.child("groups").child(room_name);
                            users_entry_to_group.child(new_email[0]).setValue(uid);

                            DatabaseReference group_entry_to_users = mDatabase.child("users").child(uid).child(room_name);
                            group_entry_to_users.child("all").setValue("all");
                            group_entry_to_users.child("important").setValue("important");
                            group_entry_to_users.child("sent").setValue("sent");

                        }

                        registered = null;
                        uid = null;

                    }

                }
            }
        });


    }

}
