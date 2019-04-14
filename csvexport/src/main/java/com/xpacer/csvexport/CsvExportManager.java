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
import java.util.ArrayList;
import java.util.List;

class CsvExportManager<T> {

    private CsvExport<T> csvExport;
    private static final int DEFAULT_WRITE_EXTERNAL_STORAGE_RQ_CODE = 23908;

    CsvExportManager(CsvExport<T> csvExport) {
        this.csvExport = csvExport;
    }

    void export() throws IOException {

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
        dir.mkdirs();

        File file = new File(dir, fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fo);
            BufferedWriter bw = new BufferedWriter(outputStreamWriter);

            List<String> titleLineList = getCsvTitles(csvExport.getList().get(0));

            if (csvExport.isIncludeTitle()) {
                String titleLine = TextUtils.join(csvExport.getDelimiter(), titleLineList);
                bw.write(titleLine);
                bw.newLine();
            }

            for (T object : csvExport.getList()) {

                StringBuilder oneLine = new StringBuilder();

                for (String property : titleLineList) {
                    Object fieldValue = object.getClass().getField(property).get(object);
                    oneLine.append(fieldValue).append(csvExport.getDelimiter());
                }

                bw.write(oneLine.toString());
                bw.newLine();
            }

            bw.flush();
            bw.close();
            if (csvExport.getExportResult() != null)
                csvExport.getExportResult().onExportSuccess();
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            if (csvExport.getExportResult() != null)
                csvExport.getExportResult().onExportError(e.getMessage());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            if (csvExport.getExportResult() != null)
                csvExport.getExportResult().onExportError(e.getMessage());
        }
    }

    private List<String> getCsvTitles(@NonNull T fObject) {
        List<String> csvTitles = new ArrayList<>();

        for (Field f : fObject.getClass().getDeclaredFields()) {
            String fieldName = f.getName();

            if (fieldName.equalsIgnoreCase("$change") ||
                    fieldName.equalsIgnoreCase("serialversionUID")) {
                continue;
            }

            if (csvExport.getExcludedProperties() == null ||
                    !csvExport.getExcludedProperties().contains(f.getName())) {
                csvTitles.add(f.getName());
            }
        }

        return csvTitles;
    }


}
