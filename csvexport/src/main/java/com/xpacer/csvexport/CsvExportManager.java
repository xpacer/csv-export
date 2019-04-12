package com.xpacer.csvexport;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;

class CsvExportManager<T> {

    private CsvExport<T> csvExport;
    private static final int DEFAULT_WRITE_EXTERNAL_STORAGE_RQ_CODE = 23908;

    CsvExportManager(CsvExport<T> csvExport) {
        this.csvExport = csvExport;
    }

    void export() throws IOException, IllegalAccessException {

        if (csvExport.getList() == null || csvExport.getList().isEmpty())
            throw new IllegalArgumentException("Passed list argument cannot be null or empty");

        if (csvExport.getActivity() == null)
            throw new IllegalArgumentException("Passed context argument cannot be null");

        if (TextUtils.isEmpty(csvExport.getFileName()))
            throw new IllegalArgumentException("File name cannot be null or empty");

        if (TextUtils.isEmpty(csvExport.getDirectory()))
            throw new IllegalArgumentException("File directory cannot be null or empty");


        boolean hasPermission = (ContextCompat.checkSelfPermission(csvExport.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        int requestCode = csvExport.getStorageWritePermissionRequestCode() != 0 ?
                csvExport.getStorageWritePermissionRequestCode() : DEFAULT_WRITE_EXTERNAL_STORAGE_RQ_CODE;

        if (!hasPermission) {
            ActivityCompat.requestPermissions(csvExport.getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
            return;
        }

        String fileName = csvExport.getFileName();
        File dir = new File(csvExport.getDirectory());

        if (!dir.mkdirs()) {
            throw new IOException("Unable to create file at the specified directory");
        }

        File file = new File(dir, fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fo);
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);

            if (csvExport.isIncludeTitle()) {
                String titleLine = getCsvTitle(csvExport.getList().get(0));
                bw.write(titleLine);
                bw.newLine();
            }

            for (T object : csvExport.getList()) {
                StringBuilder oneLine = new StringBuilder();
                for (Field f : object.getClass().getDeclaredFields()) {
                    oneLine.append(f.get(object)).append(csvExport.getDelimiter());
                }

                bw.write(oneLine.toString());
                bw.newLine();
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException(e.getMessage());
        }
    }


    private String getCsvTitle(@NonNull T fObject) {
        StringBuilder oneLine = new StringBuilder();

        for (Field f : fObject.getClass().getDeclaredFields()) {
            if (csvExport.getExcludedProperties() != null &&
                    !csvExport.getExcludedProperties().contains(f.getName())) {
                oneLine.append(f.getName()).append(csvExport.getDelimiter());
            }
        }

        return oneLine.toString();
    }


}
