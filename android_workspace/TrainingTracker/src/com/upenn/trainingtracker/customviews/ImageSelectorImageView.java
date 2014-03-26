package com.upenn.trainingtracker.customviews;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.upenn.trainingtracker.DogSelectorActivity;
import com.upenn.trainingtracker.R;

import eu.janmuller.android.simplecropimage.CropImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ImageSelectorImageView extends ImageView
{
	private Activity activity;
	private Uri fileUri;
	private Bitmap img;
	
	public ImageSelectorImageView(Context context)
	{
		super(context);
		this.init();
	}
	public ImageSelectorImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}
	public ImageSelectorImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}
	public Bitmap getBitmap()
	{
		return this.img;
	}
	private Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	public void setParentActivity(Activity activity)
	{
		this.activity = activity;
	}
	public void cropCameraResult()
	{
		File file = new File(fileUri.getPath());
		if (!file.exists()) return;
		Log.i("TAG","Cropping and setting camera result");
		Intent intent = new Intent(activity, CropImage.class);
		intent.putExtra("uri", fileUri);
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		ImageSelectorImageView.this.activity.startActivityForResult(intent, DogSelectorActivity.CROP_INTENT_RESULT_CODE);
	}
	public void cropGalleryResult(Intent data)
	{
		if (data == null) return;
		fileUri = data.getData();

		Bitmap img = null;
        try {
        	ContentResolver cr = this.activity.getContentResolver();
			img = android.provider.MediaStore.Images.Media.getBitmap(cr, fileUri);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
        // Make a copy of the image so the original won't be cropped
        File newFile = this.getOutputMediaFile();
        
        FileOutputStream out = null;
        try
        {
        	out = new FileOutputStream(newFile);
        	img.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch(Exception e)
        {
        	e.printStackTrace();
        } finally {
        	try {
        		out.close();
        	} catch (Throwable ignore) {}
        }
        fileUri = Uri.fromFile(newFile);
		Intent intent = new Intent(activity, CropImage.class);
		intent.putExtra("uri", fileUri);
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);
		ImageSelectorImageView.this.activity.startActivityForResult(intent, DogSelectorActivity.CROP_INTENT_RESULT_CODE_FROM_GALLERY);
	}
	public void setCropResult()
	{
		Bitmap receivedImage = BitmapFactory.decodeFile(fileUri.getPath());
		if (receivedImage != null)
		{
			this.img = receivedImage;
			this.setImageBitmap(img);
		}
	}
	public void setImageSelectorImage(Bitmap img)
	{
		this.img = img;
		this.setImageBitmap(img);
	}
	
	public void setCropGalleryResult()
	{
        try {
        	ContentResolver cr = this.activity.getContentResolver();
        	this.img = android.provider.MediaStore.Images.Media.getBitmap(cr, fileUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setImageBitmap(this.img);

	}
	public void init()
	{
		this.img = this.drawableToBitmap(this.getDrawable());
		// Set listener for imageview
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				String[] items = new String[] {"Take from camera", "Select from gallery"};
				ArrayAdapter<String> adapter = new ArrayAdapter<String> (ImageSelectorImageView.this.activity, android.R.layout.select_dialog_item, items);
				AlertDialog.Builder builder = new AlertDialog.Builder(ImageSelectorImageView.this.activity);
				builder.setTitle("Select Image");
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int item) 
					{
						if (item == 0) // Pick from camera
						{
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							ImageSelectorImageView.this.fileUri = getOutputMediaFileUri();
							intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
							ImageSelectorImageView.this.activity.startActivityForResult(intent, DogSelectorActivity.CAMERA_INTENT_RESULT_CODE);

						}
						else if (item == 1) // From gallery
						{
							Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							ImageSelectorImageView.this.activity.startActivityForResult(intent, DogSelectorActivity.GALLERY_INTENT_RESULT_CODE);
						}
					}
				});
				builder.create().show();	
			}
			public Uri getOutputMediaFileUri()
			{
				return Uri.fromFile(getOutputMediaFile());
			}

		});
	}
	
	public File getOutputMediaFile()
	{
		Log.i("TAG","A");
		// TODO: Check that the SDCard is mounted
		// using Environment.getExternalStorageState()
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "TrainingTracker");
		Log.i("TAG",mediaStorageDir.getPath());
		if (!mediaStorageDir.exists())
		{
			boolean sf = mediaStorageDir.mkdirs();
			if (!sf)
			{
				Log.i("TAG", "Failed to create directory");
				return null;	
			}
		}
		Log.i("TAG","here");
		// Create media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String fullFilePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
		Log.i("TAG",fullFilePath);
		File mediaFile = new File(fullFilePath);
		return mediaFile;
	}
	
}
