package anonymous.notes4u;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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


public class CreateGroup extends AppCompatActivity {

    private ListView lv_create_list;
    private Button btn_create_create;
    private EditText et_create_name;
    private EditText et_create_email;
    private ImageButton ib_create_add;
    private ArrayAdapter adapter;
    private ArrayList<String> emails_to_add;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String uid = null;
    private int i;
    private String[] members;
    private DatabaseReference check_if_group_present;
    private String[] new_email;
    private DatabaseReference temp;
    private Map<String, String> users;
    private Map<String, String> if_group_present;

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        temp = mDatabase.child("database");
        check_if_group_present = mDatabase.child("groups");

        temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        check_if_group_present.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if_group_present = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setTitle("Create Group");

        firebaseAuth = FirebaseAuth.getInstance();
        btn_create_create = (Button) findViewById(R.id.btn_create_create);
        et_create_name = (EditText) findViewById(R.id.et_create_name);
        et_create_email = (EditText) findViewById(R.id.et_create_email);
        lv_create_list = (ListView) findViewById(R.id.lv_create_list);
        ib_create_add = (ImageButton) findViewById(R.id.ib_create_add);
        emails_to_add = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emails_to_add);
        lv_create_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv_create_list.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);
        firebaseUser = firebaseAuth.getCurrentUser();

        ib_create_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_create_email.getText().toString();

                if(!TextUtils.isEmpty(email) && email.length() > 0){
                    adapter.add(email);
                    adapter.notifyDataSetChanged();
                    et_create_email.setText("");
                }

            }
        });

        lv_create_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateGroup.this);
                alertDialogBuilder.setTitle("Alert!");
                alertDialogBuilder
                        .setMessage("Remove?")
                        .setCancelable(false)
                        .setPositiveButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })

                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if(i > -1){
                                    adapter.remove(emails_to_add.get(i));
                                    adapter.notifyDataSetChanged();
                                    et_create_email.setText("");
                                }
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


        btn_create_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                members = emails_to_add.toArray(new String[emails_to_add.size()]);
                String group_name = et_create_name.getText().toString();
                String registered = null;

                if(!TextUtils.isEmpty(group_name) && members != null){

                    if(check_if_group_present.equals(null)){

                        for(Map.Entry<String, String> entry : if_group_present.entrySet()){
                            if(entry.getKey().equals(group_name)){
                                Toast.makeText(CreateGroup.this, "Group name has already been used!", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    for(i=0; i<members.length; i++){

                        new_email = members[i].split("@");
                        for(Map.Entry<String, String> entry : users.entrySet()){
                            if(entry.getKey().equals(new_email[0])){
                                registered = new_email[0];
                                uid = entry.getValue();
                            }
                        }

                        if(uid != null){

                            DatabaseReference groups = mDatabase.child("groups").child(group_name);
                            groups.child(registered).setValue(uid);
                            String[] owner = firebaseUser.getEmail().split("@");
                            groups.child(owner[0]).setValue(firebaseUser.getUid());

                            DatabaseReference me = mDatabase.child("users").child(firebaseUser.getUid()).child(group_name);
                            me.child("sent").setValue("sent");
                            me.child("important").setValue("important");
                            me.child("all").setValue("all");

                            DatabaseReference new_group = mDatabase.child("users").child(uid).child(group_name);
                            new_group.child("sent").setValue("sent");
                            new_group.child("important").setValue("important");
                            new_group.child("all").setValue("all");

                        }

                        registered = null;
                        uid = null;

                    }
                    CreateGroup.this.finish();
                    Intent intent = new Intent(getApplicationContext(), GroupsList.class);
                    startActivity(intent);
                }
            }
        });
    }
}
