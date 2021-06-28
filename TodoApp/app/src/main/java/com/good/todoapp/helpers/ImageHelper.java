package com.good.todoapp.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageHelper {

    public static Uri saveImage(Context context, Bitmap bitmap, String folderName, String fileName) throws IOException {

        OutputStream fos;
        File imageFile = null;
        Uri imageUri = null;

        // Q 버전 이상과 미만의 이미지 저장 방식이 다르다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            // Q 버전 이상은 ContentResolver를 이용하여 MediaStore에 이미지 비트맵을 출력한다.
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + folderName);
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {

            // Q 버전 미만은 ExternalStorage를 이용하여 이미지 비트맵을 출력한다.
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + folderName;
            imageFile = new File(imagesDir);
            if (!imageFile.exists()) {
                imageFile.mkdir();
            }
            imageFile = new File(imagesDir, fileName + ".png");
            fos = new FileOutputStream(imageFile);
        }

        // 비트맵을 압축하고 파일을 닫는다.
        boolean saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        if (imageFile != null) {
            // 이미지 Uri 를 얻는다.
            MediaScannerConnection.scanFile(context, new String[]{imageFile.toString()}, null, null);
            imageUri = Uri.fromFile(imageFile);
        }

        return imageUri;
    }

}
