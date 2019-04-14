package com.xpacer.csvexport;

import android.app.Activity;

import java.io.IOException;
import java.util.List;

public class CsvExport<T> {

    /**
     * List of objects to be exported
     */
    private List<T> list;

    private Activity activity;

    /**
     * Android directory for the file export
     */
    private String directory;

    /**
     * Name of csv file exported
     */
    private String fileName;

    /**
     * Csv file delimiter. Default is ","
     */
    private String delimiter = ",";

    /**
     * Flag to indicate if Csv file should include object property names as title
     */
    private boolean includeTitle = true;

    /**
     * List of properties to be excluded from the export
     */
    private List<String> excludedProperties;

    /**
     * Request Code for Storage Write if Permission has not been granted. Default is 23908
     */
    private int storageWritePermissionRequestCode;

    private OnCsvExportResult exportResult;

    public CsvExport() {

    }

    public CsvExport(List<T> list, Activity activity, String directory, String fileName) {
        this.list = list;
        this.activity = activity;
        this.directory = directory;
        this.fileName = fileName;
    }

    public String getDirectory() {
        return directory;
    }

    public CsvExport<T> setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    public CsvExport<T> with(Activity activityContext) {
        this.activity = activityContext;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public CsvExport<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public CsvExport<T> setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public CsvExport<T> setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public boolean isIncludeTitle() {
        return includeTitle;
    }

    public CsvExport<T> setIncludeTitle(boolean includeTitle) {
        this.includeTitle = includeTitle;
        return this;
    }

    public List<String> getExcludedProperties() {
        return excludedProperties;
    }

    public CsvExport<T> setExcludedProperties(List<String> excludedProperties) {
        this.excludedProperties = excludedProperties;
        return this;
    }

    public int getStorageWritePermissionRequestCode() {
        return storageWritePermissionRequestCode;
    }

    public CsvExport<T> setStorageWritePermissionRequestCode(int storageWritePermissionRequestCode) {
        this.storageWritePermissionRequestCode = storageWritePermissionRequestCode;
        return this;
    }

    public OnCsvExportResult getExportResult() {
        return exportResult;
    }

    public CsvExport<T> setExportResult(OnCsvExportResult exportResult) {
        this.exportResult = exportResult;
        return this;
    }

    /**
     * Method to export list of objects to csv file
     *
     * @throws IOException
     */
    public void export() throws IOException {
        CsvExportManager<T> exportManager = new CsvExportManager<>(this);
        exportManager.export();
    }
}
