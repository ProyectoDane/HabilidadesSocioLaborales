package com.belatrix.habilidadessociolaborales.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserEditActivity extends BaseActivity {

    private EditText mUserNameEditText;
    private EditText mLastNameEditText;
    private EditText mDateEditText;

    private TextView mTitleTextView;

    private ImageView mProfileImageView;

    private boolean mIsNewUser;

    private User mUserToEdit;

    private static final int CAMERA_REQUEST = 1888;

    private int mYear,mMonth,mDay;

    private String mFinalDateToSave;

    private byte[] bArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        initializeUI();
        checkNewUserData();
    }

    private void checkNewUserData() {
        Intent intent = getIntent();
        String sourceActivity = intent.getExtras().getString("SourceActivity");

        if (sourceActivity.equals("ListViewUsersAdapter")) {

            Long userId = intent.getExtras().getLong("userId");

            User user = UserManager.getInstance(UserEditActivity.this).getUser(userId);

            mUserToEdit = user;

            if (user != null) {

                mIsNewUser = false;

                mTitleTextView.setText(getResources().getString(R.string.edit_user));

                mUserNameEditText.setText(user.getName());
                mLastNameEditText.setText(user.getLastname());

                if (user.getImage() != null) {
                    Bitmap profileImageBitmap = ImageUtils.getInstance(UserEditActivity.this).byteArrayToBitmap(user.getImage());
                    mProfileImageView.setImageBitmap(profileImageBitmap);
                }

                Date myDate = user.getBornDate();

                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(myDate);
                    mYear = cal.get(Calendar.YEAR);
                    mMonth = cal.get(Calendar.MONTH);
                    mDay = cal.get(Calendar.DAY_OF_MONTH);

                    mDateEditText.setText(new SimpleDateFormat("dd/MM/yyyy").format(myDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mIsNewUser = true;
            if (sourceActivity.equals("LoginActivity")) {
                mTitleTextView.setText(getResources().getString(R.string.create_user));
                mUserNameEditText.setText("");
                mLastNameEditText.setText("");

                try {
                    Calendar cal = Calendar.getInstance();
                    mYear = cal.get(Calendar.YEAR);
                    mMonth = cal.get(Calendar.MONTH);
                    mDay = cal.get(Calendar.DAY_OF_MONTH);

                    mDateEditText.setText(new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeUI() {
        mUserNameEditText = (EditText) findViewById(R.id.userNameEditText);
        mLastNameEditText = (EditText) findViewById(R.id.lastNameEditText);

        mTitleTextView = (TextView) findViewById(R.id.titleTextView);

        mProfileImageView = (ImageView) findViewById(R.id.profileImageView);

        Button mLoadPictureButton = (Button) findViewById(R.id.loadPictureButton);
        Button mSaveUserButton = (Button) findViewById(R.id.saveUserButton);

        mSaveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {

                    try {
                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(mDateEditText.getText().toString());
                        if (date.after(new Date(System.currentTimeMillis()))) {
                            Toast.makeText(UserEditActivity.this,"La fecha de nacimiento no es válida",Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (Exception e) {
                        Toast.makeText(UserEditActivity.this,"La fecha de nacimiento no es válida",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (mIsNewUser) {
                        User user = new User();
                        user.setName(mUserNameEditText.getText().toString());
                        user.setLastname(mLastNameEditText.getText().toString());

                        if (mProfileImageView.getDrawable() != null && mProfileImageView.getDrawable() instanceof BitmapDrawable) {
                            Bitmap profileImageBitmap = ((BitmapDrawable) mProfileImageView.getDrawable()).getBitmap();
                            user.setImage(ImageUtils.getInstance(UserEditActivity.this).bitmapToByteArray(profileImageBitmap));
                        }

                        String startDateString = mFinalDateToSave;
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        Date startDate;
                        try {
                            startDate = df.parse(startDateString);
                            String newDateString = df.format(startDate);

                            user.setBornDate(startDate);
                            System.out.println(newDateString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (bArray != null) {
                            user.setImage(bArray);
                        }
                        user.setSound(false);
                        if (UserManager.getInstance(UserEditActivity.this).saveUser(user)) {
                            Toast.makeText(UserEditActivity.this, "Usuario creado con éxito", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(UserEditActivity.this, "Error al crear usuario", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mUserToEdit.setName(mUserNameEditText.getText().toString());
                        mUserToEdit.setLastname(mLastNameEditText.getText().toString());

                        if (mProfileImageView.getDrawable() != null) {
                            Bitmap profileImageBitmap = ((BitmapDrawable) mProfileImageView.getDrawable()).getBitmap();
                            mUserToEdit.setImage(ImageUtils.getInstance(UserEditActivity.this).bitmapToByteArray(profileImageBitmap));
                        }

                        String startDateString = mFinalDateToSave;
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                        Date startDate;
                        try {
                            startDate = df.parse(startDateString);
                            String newDateString = df.format(startDate);

                            mUserToEdit.setBornDate(startDate); // set the date to the current userEdit.
                            System.out.println(newDateString);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (bArray != null) {
                            mUserToEdit.setImage(bArray);
                        }

                        try {
                            UserManager.getInstance(UserEditActivity.this).updateUser(mUserToEdit);
                            Toast.makeText(UserEditActivity.this, "Los cambios fueron guardados con éxito", Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(UserEditActivity.this, "Error al guardar datos", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(UserEditActivity.this, "Datos incompletos.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mDateEditText = (EditText) findViewById(R.id.dateEditText);
        mDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) mDateEditText.performClick();
            }
        });
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(UserEditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);

                            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                            mDateEditText.setText(dateFormatter.format(newDate.getTime()));

                            String finalFormattedDay = formatDay(dayOfMonth);
                            String finalFormattedMonth = formatDay(monthOfYear);

                            mFinalDateToSave = finalFormattedDay + "-" + finalFormattedMonth + "-" + Integer.toString(year);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                dialog.show();
            }
        });

        mLoadPictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startDialog();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mProfileImageView.setImageBitmap(photo);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bArray = bos.toByteArray();
        }else if (requestCode == 1) {
            try {
                Uri selectedImage = data.getData();
                Bitmap yourSelectedImage = decodeUri(selectedImage);

                try {
                    mProfileImageView.setImageBitmap(yourSelectedImage);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String formatDay(int day) {
        String formattedDay;
        switch (day) {
            case 1:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 2:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 3:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 4:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 5:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 6:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 7:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 8:
                formattedDay = "0" + Integer.toString(day);
                break;
            case 9:
                formattedDay = "0" + Integer.toString(day);
                break;
            default:
                formattedDay = Integer.toString(day);
        }
        return formattedDay;
    }

    public boolean validateFields () {
        return mUserNameEditText.getText().length() > 0 && mLastNameEditText.getText().length() > 0;
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setMessage("Seleccionar imagen desde...");
        myAlertDialog.setPositiveButton("Galería",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                });
        myAlertDialog.setNegativeButton("Cámara",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });
        myAlertDialog.show();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 3;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }
}
