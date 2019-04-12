package com.xpacer.csvexporter;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xpacer.csvexport.CsvExport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button exportBtn = findViewById(R.id.btn);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                export();
            }
        });


    }

    void export() {
        List<Foo> foos = new ArrayList<>();
        Foo foo1 = new Foo();
        foo1.bar = "Bar 1";
        foo1.man = "Man 1";

        Foo foo2 = new Foo();
        foo2.bar = "Bar 2";
        foo2.man = "Man 2";

        foos.add(foo1);
        foos.add(foo2);

        String directory = Environment.getExternalStorageDirectory() + "/downloads";

        try {
            CsvExport<Foo> export = new CsvExport<>();
            export.with(this)
                    .setDirectory(directory)
                    .setFileName("testcsv.csv")
                    .setList(foos)
                    .setStorageWritePermissionRequestCode(1000)
                    .export();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            export();
        }
    }
}
