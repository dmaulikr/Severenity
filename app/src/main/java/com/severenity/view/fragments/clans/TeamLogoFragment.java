package com.severenity.view.fragments.clans;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.severenity.utils.common.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

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
    private ProgressDialog   mProgressDialog;
    private Handler          mHandler;

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

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getResources().getString(R.string.saving_logo));

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
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_RESULT_ADD);
                }else {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
        mSaveCustomIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_RESULT_SAVE);
                }else {
                    saveTeamLogoAsFile();
                }
            }
        });

        mTeamFragment.loadImageFromStorage(mCurrentTeamLogo,mPermissionCheck);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(),R.string.done,Toast.LENGTH_SHORT).show();
                sendBroadcast();
            }
        };

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContext().getContentResolver()
                    .query(selectedImage,filePathColumn,null,null,null);
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
        } else if (requestCode == PERMISSION_RESULT_ADD
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        } else {
            Toast.makeText(getContext(),R.string.permission_denied,Toast.LENGTH_LONG).show();
        }
    }

    protected void saveTeamLogoAsFile() {
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mCurrentTeamLogo.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                File sdCardDirectory = new File(Environment.getExternalStorageDirectory().toString() +
                        "/logos_directory");
                boolean isDirectoryCreated = sdCardDirectory.exists();
                if (!isDirectoryCreated) {
                    isDirectoryCreated = sdCardDirectory.mkdir();
                }
                if (isDirectoryCreated) {
                    File teamIconDirectory = new File(sdCardDirectory + "/teamLogo.png");
                    FileOutputStream outStream;
                    try {
                        outStream = new FileOutputStream(teamIconDirectory);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                        new Timer().schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        mHandler.sendEmptyMessage(0);
                                    }
                                },
                                1000
                        );
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getActivity(), R.string.file_not_found, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e("EXCEPTION", "IOException", e);
                    }
                }
            }
        }).start();
    }

    private void sendBroadcast(){
        Intent intent = new Intent();
        intent.setAction(Constants.INTENT_FILTER_RELOAD);
        intent.putExtra("com.severenity.broadcaster", Constants.INTENT_EXTRA_RELOAD);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        getActivity().sendBroadcast(intent);
    }

}
