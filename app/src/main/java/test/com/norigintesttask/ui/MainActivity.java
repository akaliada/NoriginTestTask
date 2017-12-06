package test.com.norigintesttask.ui;

import android.os.Bundle;

import test.com.norigintesttask.R;
import test.com.norigintesttask.ui.common.BaseActivity;
import test.com.norigintesttask.ui.table.view.TableFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            addFragment(R.id.activity_screen_container, new TableFragment());
        }

    }
}
