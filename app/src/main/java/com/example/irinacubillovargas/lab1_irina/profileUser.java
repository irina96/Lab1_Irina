package com.example.irinacubillovargas.lab1_irina;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class profileUser extends AppCompatActivity {

    List<user> model=new ArrayList<>();
    userAdapter adapter = null;

    private static final int REQUEST_CODE = 1234;
    EditText startButton;

    Dialog matchTextDialog;
    ArrayList<String> matchesText;
    private static final int REQUEST_IMAGE_CAPTURE  = 101;

    Button btnPhoto;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_user);

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(onSave);

        adapter = new userAdapter();
        ListView list = (ListView) findViewById(R.id.users);
        list.setAdapter(adapter);

        startButton = (EditText) findViewById(R.id.description);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-Es");
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(profileUser.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (! hasCamera () )
            btnPhoto.setEnabled ( false ) ;

        Button btnPhoto = (Button) findViewById(R.id.photo);

        btnPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startRecording();
            }
        });

    }//dentro del onCreate

    private boolean hasCamera () {
        return ( getPackageManager () . hasSystemFeature (PackageManager.FEATURE_CAMERA_ANY ) ) ;
    }

    public void startRecording ()
    {
        Intent takePictureIntent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE ) ;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            imageUri = data.getData();
        }
        if(requestCode == REQUEST_CODE){
            super.onActivityResult(requestCode, resultCode, data);
            matchesText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); //Get data of data
            startButton.setText(matchesText.get(0));
        }
    }


    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!= null && net.isAvailable() && net.isConnected()){
            return true;
        }   else {
            return false;
        }
    }

    private View.OnClickListener onSave = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            user r=new user();

            EditText name=(EditText)findViewById(R.id.name);
            EditText description=(EditText)findViewById(R.id.description);

            r.setName(name.getText().toString());
            r.setDescription(description.getText().toString());
            r.setImage(imageUri.toString());


            RadioGroup types=(RadioGroup)findViewById(R.id.sexo);
            switch(types.getCheckedRadioButtonId()){
                case R.id.femele:
                    r.setSexo("Femele");
                    break;
                case R.id.male:
                    r.setSexo("Male");
                    break;
            }
            adapter.add(r);

            name.setText("");
            description.setText("");
        }
    };


    class userAdapter extends ArrayAdapter<user> {

        userAdapter() {
            super(profileUser.this, R.layout.row, model);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View row=convertView;
            userHolder holder=null;
            if(row==null){
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.row, parent,false);
                holder=new userHolder(row);
                row.setTag(holder);
            }
            else{
                holder=(userHolder)row.getTag();
            }
            holder.populateFrom(model.get(position));
            //Hay que modificar el model
            return (row);
        }
    }

    static class userHolder{
        private TextView name=null;
        private TextView description=null;
        private ImageView image=null;
        private TextView sexo=null;
        userHolder(View row){
            name=(TextView)row.findViewById(R.id.title);
            description=(TextView)row.findViewById(R.id.profile);
            sexo=(TextView)row.findViewById(R.id.sexo1);
            image=(ImageView)row.findViewById(R.id.icon);
        }

        void populateFrom(user r){
            name.setText(r.getName());
            description.setText(r.getDescription());
            image.setImageURI(Uri.parse(r.getImage()));
            image.setRotation(90);

            if(r.getSexo().equals("Femele")){
                sexo.setText("Femele");
            }
            else{
                sexo.setText("Male");
            }

        }

    }
}


