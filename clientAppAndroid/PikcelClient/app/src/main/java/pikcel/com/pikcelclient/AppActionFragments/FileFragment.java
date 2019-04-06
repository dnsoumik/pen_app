package pikcel.com.pikcelclient.AppActionFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import pikcel.com.pikcelclient.FontManager;
import pikcel.com.pikcelclient.R;
import pikcel.com.pikcelclient.RealPathUtil;

import static android.content.ContentValues.TAG;

public class FileFragment extends Fragment {

    View parentHolder;
    Button selectFileButton;
    TextView filePath;
    private static final int READ_REQUEST_CODE = 42;
    FloatingActionButton uploadFab;
    String uploadFilePath;
    Uri uploadFileUri;
    ProgressDialog progressDialog = null;
    FontManager fontManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        super.onCreate(savedInstanceState);

        parentHolder = inflater.inflate(R.layout.file_fragment_layout, container, false);

        fontManager = new FontManager(getActivity());

        /**
         * In the following part we are parsing the root layout of the FileFragment and
         * replacing all the Typeface (fonts) for all views inside the group.
         * */
//        ConstraintLayout rootLayout = parentHolder.findViewById(R.id.file_fragment_root);
//        fontManager.replaceFonts(rootLayout, fontManager.getLight());
        /**
         * End of comment
         * */

        selectFileButton = parentHolder.findViewById(R.id.select_file_button);
        filePath = parentHolder.findViewById(R.id.file_path);

        uploadFab = parentHolder.findViewById(R.id.upload_btn);

        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = ProgressDialog.show(getActivity(), "Upload In Progress", "Please wait....", true);

                new Thread(new Runnable() {
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                uploadFab.setVisibility(View.GONE);
                            }
                        });

                        HashMap<String, String> fileInfo = dumpFileMetaData(uploadFileUri);
                        uploadFile(uploadFilePath, fileInfo.get("DispalyName"), getMimeType(uploadFileUri));

                    }
                }).start();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        return parentHolder;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                dumpFileMetaData(uri);
                String realPath;
                // SDK < API11
                if (Build.VERSION.SDK_INT < 11)
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(getContext(), resultData.getData());

                    // SDK >= 11 && SDK < 19
                else if (Build.VERSION.SDK_INT < 19)
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(getActivity(), resultData.getData());

                    // SDK > 19 (Android 4.4)
                else
                    realPath = RealPathUtil.getRealPathFromURI_API19(getActivity(), resultData.getData());

                Log.d("Real Path", realPath);
                Log.d("File Path", uri.getPath());
                Log.d("File Encoded Path", uri.getEncodedPath());

//                try {
//                    File file = new File(realPath);
//                    byte bytes[] = FileUtils.readFileToByteArray(file);
//                    String a  = bytes.toString();
//                    Log.d("BYTE Array", a);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                uploadFilePath = realPath;
                uploadFileUri = uri;
                uploadFab.setVisibility(View.VISIBLE);
                HashMap<String, String> fileInfo = dumpFileMetaData(uploadFileUri);
                filePath.setText(fileInfo.get("DisplayName") + " : " + fileInfo.get("Size"));
            }
        }
    }



    public HashMap<String, String> dumpFileMetaData(Uri uri) {

        HashMap<String, String> fileInfo = new HashMap<>();

        Cursor cursor = getActivity().getContentResolver()
                .query(uri, null, null, null, null, null);

        try {

            if (cursor != null && cursor.moveToFirst()) {


                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i(TAG, "Display Name: " + displayName);
                fileInfo.put("DisplayName", displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                fileInfo.put("Size",size);
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i(TAG, "Size: " + size);
            }
        } finally {
            cursor.close();
        }
        return fileInfo;
    }

    public String getMimeType(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        return contentResolver.getType(uri);
    }

    public int uploadFile(String sourceFilePath, String sourceFileName, String mimeType) {

        HttpsURLConnection conn = null;
        DataOutputStream dos = null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFilePath);
        int serverResponseCode = 0;

        if (!sourceFile.isFile()) {

            progressDialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath);

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Error")
                            .setMessage("File does not exist")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Do Nothing
                                }
                            }).create().show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("https://dev.trakiga.com/sample/api/files");

                // Open a HTTP  connection to  the URL
                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", mimeType);
                conn.setRequestProperty("uploaded_file", sourceFileName);

                dos = new DataOutputStream(conn.getOutputStream());

//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name="uploaded_file";filename=""
//                                + fileName + """ + lineEnd);
//
//                        dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

//                // send multipart form data necesssary after file data...
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed";
                            Toast.makeText(getActivity(), msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                progressDialog.dismiss();
                ex.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Error")
                                .setMessage("Malformed URL caused connection to fail")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Do Nothing
                                    }
                                }).create().show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                progressDialog.dismiss();
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Error")
                                .setMessage("Something went wrong")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // DO Nothing
                                    }
                                }).create().show();
                    }
                });
                Log.e("Upload server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            progressDialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
}

