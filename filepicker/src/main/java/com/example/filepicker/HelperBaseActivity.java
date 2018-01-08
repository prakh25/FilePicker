package com.example.filepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatActivity;

import com.example.filepicker.util.PreConditions;

/**
 * Created by prakh on 07-01-2018.
 */
@SuppressWarnings("Registered")
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class HelperBaseActivity extends AppCompatActivity {

    private IntentParameters intentParameters;

    public static Intent createBaseIntent(
            @NonNull Context context,
            @NonNull Class<? extends Activity> target,
            @NonNull IntentParameters intentParameters) {
        return new Intent(
                PreConditions.checkNotNull(context, "context cannot be null"),
                PreConditions.checkNotNull(target, "target activity cannot be null"))
                .putExtra(Constants.EXTRA_INTENT_PARAMS,
                        PreConditions.checkNotNull(intentParameters,
                                "intent params cannot be null"));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public IntentParameters getIntentParameters() {
        if(intentParameters == null) {
            intentParameters = IntentParameters.fromIntent(getIntent());
        }
        return intentParameters;
    }

    public void setResultAndFinish(FilePickerResponse response) {
        finish(Activity.RESULT_OK, response.toIntent());
    }

    public void finish(int resultCode, Intent intent) {
        setResult(resultCode, intent);
        finish();
    }
}
