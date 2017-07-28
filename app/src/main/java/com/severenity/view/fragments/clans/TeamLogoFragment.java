package com.severenity.view.fragments.clans;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.content.ContextCompat;
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
public class TeamLogoFragment extends DialogFragment {

    private TeamFragment     mTeamFragment;
    private Button           mAddCustomIconBtn,mSaveCustomIconBtn;
    private ImageButton      mCurrentTeamLogo,mFirstImageButton,mSecondImageButton,
                             mThirdImageButton,mFourthImageButton,mFifthImageButton;
    private int              mPermissionCheck;

    private static final int RESULT_LOAD_IMAGE      = 1;
    private static final int PERMISSION_RESULT_ADD  = 5831;
    private static final int PERMISSION_RESULT_SAVE = 8814;



    public TeamLogoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team_logo, container, false);

        getDialog().setTitle("Select image");

        mTeamFragment      = new TeamFragment();
        mPermissionCheck   = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mCurrentTeamLogo   = view.findViewById(R.id.currentTeamLogo);
        mAddCustomIconBtn  = view.findViewById(R.id.addCustomIconBtn);
        mSaveCustomIconBtn = view.findViewById(R.id.saveCustomIconBtn);
        mFirstImageButton  = view.findViewById(R.id.ic_team_1);
        mSecondImageButton = view.findViewById(R.id.ic_team_2);
        mThirdImageButton  = view.findViewById(R.id.ic_team_3);
        mFourthImageButton = view.findViewById(R.id.ic_team_4);
        mFifthImageButton  = view.findViewById(R.id.ic_team_5);

        mFirstImageButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentTeamLogo.setImageDrawable(mFirstImageButton.getDrawable());
            }
        });
        mSecondImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentTeamLogo.setImageDrawable(mSecondImageButton.getDrawable());
            }
        });
        mThirdImageButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentTeamLogo.setImageDrawable(mThirdImageButton.getDrawable());
            }
        });
        mFourthImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentTeamLogo.setImageDrawable(mFourthImageButton.getDrawable());
            }
        });
        mFifthImageButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentTeamLogo.setImageDrawable(mFifthImageButton.getDrawable());
            }
        });
        mAddCustomIconBtn .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_RESULT_ADD);
            }
        });
        mSaveCustomIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_RESULT_SAVE);
            }
        });

        mTeamFragment.loadImageFromStorage(mCurrentTeamLogo,mPermissionCheck);

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

    @Override
    public void onRequestPermissionsResult (
            int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_RESULT_SAVE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveTeamLogoAsFile();
            Toast.makeText(getActivity(),"Done!",Toast.LENGTH_LONG).show();
        } else if (requestCode == PERMISSION_RESULT_ADD
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        } else {
            Toast.makeText(getContext(),"hui",Toast.LENGTH_LONG).show();
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
}
