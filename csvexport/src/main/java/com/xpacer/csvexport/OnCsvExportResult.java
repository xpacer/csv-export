package com.xpacer.csvexport;

public interface OnCsvExportResult {
    void onExportSuccess();

    void onExportError(String errorMessage);
}
