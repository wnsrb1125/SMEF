package com.shelper.overlay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class InformationPopupActivity extends Activity {

    String resultt;
    int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infopop);

        ImageView imageView = findViewById(R.id.img);
        Button execute_button = findViewById(R.id.execute);
        Button cancel_button = findViewById(R.id.cancel);
        Button share_button = findViewById(R.id.share);

        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("id",0);

        execute_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTodo task = new AsyncTodo();
                try {
                    resultt = task.execute("https://shelper3.azurewebsites.net/getjson.php?id="+id,"sound").get();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
                startServ(id);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DoDynamicLink();
            }
        });

    }

    private void startServ(int id) {
        Intent intent = new Intent(InformationPopupActivity.this, MyService.class);
        intent.putExtra("edu",resultt);
        intent.putExtra("id",id);
        startService(intent);

    }

    public void DoDynamicLink() {
        final String[] result = {""};
        Task<ShortDynamicLink> dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setDomainUriPrefix("https://overlay.page.link")
                .setLink(Uri.parse("https://blog.naver.com/wnsrb0147/segment?tt="+id))
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("DD").build())
                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setContent("sdd").build())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,shortLink.toString());
                            intent.setType("type/plain");
                            startActivity(Intent.createChooser(intent,"Share"));
                            result[0] = shortLink.toString();
                        }
                    }
                });
    }
}
