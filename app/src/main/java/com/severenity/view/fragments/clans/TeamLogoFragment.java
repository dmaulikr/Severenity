package com.severenity.view.fragments.clans;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.severenity.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeamLogoFragment extends DialogFragment{

    private TeamFragment     mTeamFragment;
    private Button           mAddCustomIconBtn,mSaveCustomIconBtn;
    private ImageButton      mCurrentTeamLogo,mLogosImageButton;

    private static final int RESULT_LOAD_IMAGE = 1;

    public TeamLogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team_logo, container, false);

        getDialog().setTitle("Select image");

        mTeamFragment = new TeamFragment();
        mCurrentTeamLogo   = view.findViewById(R.id.currentTeamLogo);
        mLogosImageButton  = view.findViewById(R.id.ic_team_1);
        mAddCustomIconBtn  = view.findViewById(R.id.addCustomIconBtn);
        mSaveCustomIconBtn = view.findViewById(R.id.saveCustomIconBtn);

        mLogosImageButton. setOnClickListener(btnSelectTeamLogo);
        mAddCustomIconBtn. setOnClickListener(btnAddCustomLogo);
        mSaveCustomIconBtn.setOnClickListener(btnSaveCustomLogo);

        mTeamFragment.loadImageFromStorage(mCurrentTeamLogo);

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bm = BitmapFactory.decodeFile(picturePath);
            mCurrentTeamLogo.setImageBitmap(bm);
        }
    }

    protected void saveTeamLogoAsFile() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) mCurrentTeamLogo.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        File sdCardDirectory = new File(Environment.getExternalStorageDirectory().toString() +
                "/logos_directory");
        boolean isDirectoryCreated = sdCardDirectory.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = sdCardDirectory.mkdir();
        } if (isDirectoryCreated) {
            File teamIconDirectory = new File(sdCardDirectory + "/teamLogo.png");
            FileOutputStream outStream;
            try {
                outStream = new FileOutputStream(teamIconDirectory);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(getActivity(),"File not found",Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("EXCEPTION", "IOException", e);
            }
        }
    }

    View.OnClickListener btnSelectTeamLogo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentTeamLogo.setImageDrawable(mLogosImageButton.getDrawable());
        }
    };

    View.OnClickListener btnAddCustomLogo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    };

    View.OnClickListener btnSaveCustomLogo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveTeamLogoAsFile();
            Toast.makeText(getActivity(),"Done!",Toast.LENGTH_LONG).show();
        }
    };

}
