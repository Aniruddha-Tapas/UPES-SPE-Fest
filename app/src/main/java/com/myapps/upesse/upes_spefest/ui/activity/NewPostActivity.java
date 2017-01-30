package com.myapps.upessefest2017.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fenchtose.nocropper.CropperView;
import com.myapps.upessefest2017.R;
import com.myapps.upessefest2017.camera.AlbumStorageDirFactory;
import com.myapps.upessefest2017.camera.BaseAlbumDirFactory;
import com.myapps.upessefest2017.camera.FroyoAlbumDirFactory;
import com.myapps.upessefest2017.ui.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NewPostActivity extends BaseActivity implements
        NewPostUploadTaskFragment.TaskCallbacks {

    public static final String TAG = "NewPostActivity";
    public static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";
    private static final int THUMBNAIL_MAX_DIMENSION = 640;
    private static final int FULL_SIZE_MAX_DIMENSION = 1280;
    private Button mSubmitButton;

    public static Uri getmFileUri() {
        return mFileUri;
    }

    public static void setmFileUri(Uri mFileUri) {
        NewPostActivity.mFileUri = mFileUri;
    }

    private static Uri mFileUri;
    private Bitmap mResizedBitmap;
    private Bitmap mThumbnail;

    private NewPostUploadTaskFragment mTaskFragment;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 456;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    CropperView mImageView;
    ImageView snap_button, rotate_button, new_post_picture;
    ViewSwitcher switcher;

    ImageButton gallery_button, next_button;
    Button camera_button;

    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;

    private String userChoosenTask;

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private boolean pendingIntroAnimation;
    private boolean cam;

    //camera

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int GALLERY = 2;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";

    //private ImageView mImageView;
    //private Bitmap mImageBitmap;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private static WeakReference<NewPostActivity> wrActivity = null;


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.app_name);
    }

    /* Photo album Directory for this application */
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.e("Camera New Post", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.e(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /* Create image file */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    /* Set up Image File */
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    /* Load image */
    private void setPic() {


		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        //mImageView.setVisibility(View.VISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }


    private void handleBigCameraPhoto() {

        try {
            if (mCurrentPhotoPath != null) {
                //setPic();
                loadNewImage(mCurrentPhotoPath);
                galleryAddPic();
                mCurrentPhotoPath = null;
            }
        } catch (Exception e) {
            Toast.makeText(this, "There seems to be an error. Please try again later", Toast.LENGTH_LONG).show();
        }

    }

    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean storage = checkPermission(NewPostActivity.this);
                    boolean cam = checkCamPermission(NewPostActivity.this);
                    boolean result = (cam && storage);
                    userChoosenTask = "Take Photo";
                    if (result)
                        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wrActivity = new WeakReference<NewPostActivity>(this);
        setContentView(R.layout.activity_main_cropper);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.camera_burst);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        switcher = (ViewSwitcher) findViewById(R.id.switcher);
        new_post_picture = (ImageView) findViewById(R.id.new_post_picture);
        mImageView = (CropperView) findViewById(R.id.imageview);
        mImageView.setGestureEnabled(true);

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (NewPostUploadTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // create the fragment and data the first time
        if (mTaskFragment == null) {
            // add the fragment
            mTaskFragment = new NewPostUploadTaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }

        //Cropper
        gallery_button = (ImageButton) findViewById(R.id.gallery_button);
        gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = checkPermission(NewPostActivity.this);
                userChoosenTask = "Choose from Library";
                if (result)
                    galleryIntent();
            }
        });

        next_button = (ImageButton) findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImage();
            }
        });

        snap_button = (ImageView) findViewById(R.id.snap_button);
        snap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snapImage();
            }
        });
        rotate_button = (ImageView) findViewById(R.id.rotate_button);
        rotate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateImage();
            }
        });

        camera_button = (Button) findViewById(R.id.camera_button);
        /*
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean storage = checkPermission(NewPostActivity.this);
                boolean cam = checkCamPermission(NewPostActivity.this);
                boolean result = (cam && storage);
                userChoosenTask = "Take Photo";
                if (result)
                    cameraIntent();
            }

        });
        */
        setBtnListenerOrDisable(
                camera_button,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        //ButterKnife.bind(this);
        mImageView.setDebug(true);

        Bitmap selectedBitmap = mTaskFragment.getSelectedBitmap();
        final Bitmap thumbnail = mTaskFragment.getThumbnail();
        if (selectedBitmap != null) {
            mImageView.setImageBitmap(selectedBitmap);
            mResizedBitmap = selectedBitmap;
        }
        if (thumbnail != null) {
            mThumbnail = thumbnail;
        }

        final EditText descriptionText = (EditText) findViewById(R.id.new_post_text);

        mSubmitButton = (Button) findViewById(R.id.new_post_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                try {
                    if (mResizedBitmap == null) {
                        Toast.makeText(NewPostActivity.this, "Please describe your image...",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String postText = descriptionText.getText().toString();
                    if (TextUtils.isEmpty(postText)) {
                        descriptionText.setError(getString(R.string.error_required_field));
                        return;
                    }
                    showProgressDialog(getString(R.string.post_upload_progress_message));
                    mSubmitButton.setEnabled(false);

                    Long timestamp = System.currentTimeMillis();

                    String bitmapPath = "/" + FirebaseUtil.getCurrentUserId() + "/full/" + timestamp.toString() + "/";
                    String thumbnailPath = "/" + FirebaseUtil.getCurrentUserId() + "/thumb/" + timestamp.toString() + "/";
                    mTaskFragment.uploadPost(mResizedBitmap, bitmapPath, mThumbnail, thumbnailPath, getmFileUri().getLastPathSegment(),
                            postText);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mBitmap);
        outState.putString("Current_Path", mCurrentPhotoPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mCurrentPhotoPath = savedInstanceState.getString("Current_Path");
        mImageView.setImageBitmap(mBitmap);
        loadNewImage(mCurrentPhotoPath);

    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText("Cannot " + btn.getText());
            btn.setClickable(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);

    }

    @Override
    public void onPostUploaded(final String error) {
        NewPostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSubmitButton.setEnabled(true);
                final Activity activity = wrActivity.get();
                if (activity != null && !activity.isFinishing()) {
                    dismissProgressDialog();
                    if (error == null) {
                        Toast.makeText(NewPostActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewPostActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    //Cropper
    private void loadNewImage(String filePath) {
        Log.i(TAG, "load image: " + filePath);
        mBitmap = BitmapFactory.decodeFile(filePath);
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if (orientation == 6) {
                mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
            } else if (orientation == 3) {
                mBitmap = BitmapUtils.rotateBitmap(mBitmap, 180);
            } else if (orientation == 8) {
                mBitmap = BitmapUtils.rotateBitmap(mBitmap, 270);
            }
        } catch (Exception e) {
            Log.e("Orientation", e.getStackTrace().toString());
        }

        Log.i(TAG, "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float) maxP / 1280;

        if (mImageView.getWidth() != 0) {
            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
                    return true;
                }
            });

        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                (int) (mBitmap.getHeight() / scale1280), true);
        mImageView.setImageBitmap(mBitmap);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkCamPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((android.app.Activity) context, Manifest.permission.CAMERA)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder1 = new android.support.v7.app.AlertDialog.Builder(context);
                    alertBuilder1.setCancelable(true);
                    alertBuilder1.setTitle("Camera Permission necessary");
                    alertBuilder1.setMessage("Camera permission is necessary");
                    alertBuilder1.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((android.app.Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    });
                    android.support.v7.app.AlertDialog alert1 = alertBuilder1.create();
                    alert1.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((android.app.Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((android.app.Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (userChoosenTask.equals("Take Photo"))

                        cam = checkCamPermission(NewPostActivity.this);
                    if (cam)
                        try {
                            //cameraIntent();
                            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);

                        } catch (Exception e) {
                            //Toast.makeText(this, "Camera Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "There seems to be an error with the Camera. Please use Gallery instead  to select image.", Toast.LENGTH_LONG).show();
                        }
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;

            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        try {
                            //cameraIntent();
                            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                        } catch (Exception e) {
                            //Toast.makeText(this, "Camera Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "There seems to be an error with the Camera. Please use Gallery instead  to select image.", Toast.LENGTH_LONG).show();
                        }
                    else if (userChoosenTask.equals("Choose from Library"))
                        try {
                            galleryIntent();
                        } catch (Exception e) {
                            Toast.makeText(this, "There seems to be an error. Please try again later", Toast.LENGTH_LONG).show();
                        }
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    try {
                        handleBigCameraPhoto();
                        Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "There seems to be an error with the Camera. Please use Gallery instead  to select image.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case GALLERY: {
                if (resultCode == RESULT_OK) {
                    try {

                        String absPath = BitmapUtils.getFilePathFromUri(this, data.getData());
                        loadNewImage(absPath);

                    } catch (Exception e) {
                        Toast.makeText(this, "There seems to be an error. Please try again later", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            } // ACTION_TAKE_PHOTO_S

        } // switch
    }


    private void cropImage() {

        Bitmap bitmap = mImageView.getCroppedBitmap();
        final String fileName;

        if (bitmap != null) {

            try {

                /*
                File folder = new File(Environment.getExternalStorageDirectory().toString()
                        + "/UPES-SPE-Fest-2017");
                if (!folder.exists()) {
                    folder.mkdir();
                    Log.d(TAG, "wrote: created folder " + folder.getPath());
                }
                fileName = Environment.getExternalStorageDirectory().toString()
                        + "/UPES-SPE-Fest-2017/IMG-"
                        + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
                        + ".jpg";


                File imageFile = new File(fileName);
                BitmapUtils.writeBitmapToFile(bitmap, imageFile, 90);
                Toast.makeText(this, "Wrote " + fileName, Toast.LENGTH_LONG).show();

                try {
                    ExifInterface exif = new ExifInterface(fileName);
                    exif.setAttribute("UserComment", "Generated using UPES SPE Fest App");
                    String nowFormatted = mDateFormat.format(new Date().getTime());
                    exif.setAttribute(ExifInterface.TAG_DATETIME, nowFormatted);
                    exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, nowFormatted);
                    exif.setAttribute("Software", "UPES SPE Fest 2017");
                    exif.saveAttributes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */

                //Saving bitmap to file
                fileName = "IMG-"
                        + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
                        + ".jpg";

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/UPES-SPE-Fest-2017/app_data/directory
                File directory = cw.getDir("UPES-SPE-Fest-2017", Context.MODE_PRIVATE);
                // Create directory
                File mypath = new File(directory, fileName);

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert fos != null;
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error Saving Bitmap" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                //Loading bitmap
                try {

                    String path = directory.getAbsolutePath();
                    File f = new File(path, fileName);
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                    //ImageView img = (ImageView) findViewById(R.id.imgPicker);
                    //img.setImageBitmap(b);

                    new_post_picture.setImageBitmap(b);
                    //mFileUri = Uri.fromFile(f);
                    setmFileUri(Uri.fromFile(f));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error Loading Bitmap" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                //new_post_picture.setImageBitmap(bitmap);

                //mFileUri = Uri.fromFile(imageFile);

                new AnimationUtils();
                switcher.setAnimation(AnimationUtils.makeInAnimation(this, true));
                switcher.showNext();

                //mFileUri = data.getData();
                //Log.d(TAG, "Received file uri: " + mFileUri.getPath());

                mTaskFragment.resizeBitmap(getmFileUri(), THUMBNAIL_MAX_DIMENSION);
                mTaskFragment.resizeBitmap(getmFileUri(), FULL_SIZE_MAX_DIMENSION);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
    }

    private void snapImage() {
        if (isSnappedToCenter) {
            mImageView.cropToCenter();
        } else {
            mImageView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }


    @Override
    public void onDestroy() {
        // store the data in the fragment
        if (mResizedBitmap != null) {
            mTaskFragment.setSelectedBitmap(mResizedBitmap);
        }
        if (mThumbnail != null) {
            mTaskFragment.setThumbnail(mThumbnail);
        }
        super.onDestroy();
    }

    @Override
    public void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension) {
        if (resizedBitmap == null) {
            Log.e(TAG, "Couldn't resize bitmap in background task.");
            Toast.makeText(getApplicationContext(), "Couldn't resize bitmap.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMaxDimension == THUMBNAIL_MAX_DIMENSION) {
            mThumbnail = resizedBitmap;
        } else if (mMaxDimension == FULL_SIZE_MAX_DIMENSION) {
            mResizedBitmap = resizedBitmap;
            mImageView.setImageBitmap(mResizedBitmap);
        }

        if (mThumbnail != null && mResizedBitmap != null) {
            mSubmitButton.setEnabled(true);
        }
    }
}
