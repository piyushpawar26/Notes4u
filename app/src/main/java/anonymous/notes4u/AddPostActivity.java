package anonymous.notes4u;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class AddPostActivity extends AppCompatActivity {

    private Button btn_post_post;
    private EditText et_add_title;
    private EditText et_add_desc;
    private LinearLayout ll_post_images;
    private StorageReference mStorage;
    private static final int pick_intent = 45;
    private static final int pdf_intent = 15;
    private ArrayList<Uri> mArrayUri = null;
    private String room_name;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private static final int capture_intent = 5;
    private Random random;
    private int rand_num;
    private DatabaseReference group_mems;
    private Map<String, String> users;
    private File cam_file;
    private ArrayList<String> downloadUrl;
    private int itr = 0;
    private StorageReference filepath;
    private String post_title;
    private String post_desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        setTitle("Add Post");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        room_name = getIntent().getExtras().get("group_name").toString();
        group_mems = mDatabase.child("groups").child(room_name);

        btn_post_post = (Button) findViewById(R.id.btn_post_post);
        et_add_title = (EditText) findViewById(R.id.et_add_title);
        et_add_desc = (EditText) findViewById(R.id.et_add_desc);
        ll_post_images = (LinearLayout) findViewById(R.id.ll_post_images);
        mArrayUri = new ArrayList<>();
        users = Collections.emptyMap();
        random = new Random();
        downloadUrl = new ArrayList<>();

        group_mems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = (Map<String, String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btn_post_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_title = et_add_title.getText().toString();
                post_desc = et_add_desc.getText().toString();

                if(!TextUtils.isEmpty(post_title) && (!TextUtils.isEmpty(post_desc) || !mArrayUri.isEmpty())){

                    MyTask mysTask = new MyTask();
                    mysTask.execute();

                }else{
                    Toast.makeText(AddPostActivity.this, "Please fill all fields!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getFile(){
        File appFile = new File(Environment.getExternalStorageDirectory() + File.separator + "notesforu");
        if(!appFile.exists()){
            appFile.mkdirs();
        }
        rand_num = random.nextInt(100000);
        cam_file = new File(appFile, rand_num + ".jpg");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_post_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_img_add){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), pick_intent);
        }else if(item.getItemId() == R.id.menu_img_cam){
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getFile();
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cam_file));
            startActivityForResult(i, capture_intent);
        }else if(item.getItemId() == R.id.menu_img_pdf){
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), pdf_intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == pick_intent && resultCode == RESULT_OK && null != data) {

            if(data.getData()!=null){

                Uri uri1 = data.getData();
                mArrayUri.add(uri1);
                ImageView image = new ImageView(AddPostActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 400);
                image.setLayoutParams(layoutParams);
                image.setPadding(10, 10, 10, 10);
                Glide.with(AddPostActivity.this).load(uri1).into(image);
                ll_post_images.addView(image);

            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        ImageView image = new ImageView(AddPostActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 400);
                        image.setLayoutParams(layoutParams);
                        image.setPadding(10, 10, 10, 10);
                        Glide.with(AddPostActivity.this).load(uri).into(image);
                        ll_post_images.addView(image);

                    }
                }
            }else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }

        }else if (requestCode == capture_intent && resultCode == RESULT_OK) {

            mArrayUri.add(Uri.fromFile(cam_file));
            String path = cam_file.getAbsolutePath();
            Toast.makeText(this, path + " ", Toast.LENGTH_LONG).show();
            ImageView image = new ImageView(AddPostActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 400);
            image.setLayoutParams(layoutParams);
            image.setPadding(10, 10, 10, 10);
            Glide.with(AddPostActivity.this).load(Uri.fromFile(cam_file)).into(image);
            ll_post_images.addView(image);

        }

    }

    private class MyTask extends AsyncTask<Void, Uri, Void>{

        private ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... voids) {
            for(Uri x : mArrayUri){
                publishProgress(x);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPreExecute() {
            filepath = mStorage.child("images").child(room_name).child(post_title);
            progressDialog = new ProgressDialog(AddPostActivity.this);
            progressDialog.setMessage("uploading...");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Uri... values) {
            UploadTask uploadTask = filepath.child("image" + itr + ".jpg").putFile(values[0]);
            uploadTask.addOnSuccessListener(AddPostActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl.add(taskSnapshot.getDownloadUrl().toString());
                    ++itr;
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDatabase.child("users").child(firebaseUser.getUid()).child(room_name).child("sent").child(post_title).setValue(post_title);

            for(Map.Entry<String, String> entry : users.entrySet()){
                DatabaseReference user = mDatabase.child("users").child(entry.getValue()).child(room_name).child("all").child(post_title);
                for(int z=0; z<itr; z++){
                    user.child("image" + z).setValue(downloadUrl.get(z));
                }
            }

            progressDialog.dismiss();
            Toast.makeText(AddPostActivity.this, "All files are uploaded successfully!", Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(AddPostActivity.this, UserAreaActivity.class);
            intent.putExtra("group_name", room_name);
            startActivity(intent);

        }
    }

}
