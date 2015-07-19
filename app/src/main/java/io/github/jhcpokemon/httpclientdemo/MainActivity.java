package io.github.jhcpokemon.httpclientdemo;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static TextView textView1;
    private Button button1, button2, button3;
    private MyHandler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.text_view);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkThread thread = new NetworkThread();
                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                thread.start();
            }
        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetThread gt = new GetThread();
                try {
                    Thread.sleep(2 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                gt.start();
            }
        });
        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostThread pt = new PostThread();
                try {
                    Thread.sleep(2 * 1000);
                    pt.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class NetworkThread extends Thread {
        @Override
        public void run() {
            try {
                /**
                 * In Genymotion,use 10.0.3.2 to access 127.0.0.1
                 */
                URL url = new URL("http://10.0.3.2/test.txt");
                String result = getStringFromURL(url);
                Message msg = handler.obtainMessage();
                msg.obj = result;
                handler.sendMessage(msg);
                Log.i("Thread", Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class GetThread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL("http://10.0.3.2/test.php?data=100");
                String result = getStringFromURL(url);
                Log.i("GET", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private String getStringFromURL(URL url) {
        StringBuilder result = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    class PostThread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL("http://10.0.3.2/test.php?");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("data", "100");
                String query = builder.build().getEncodedQuery();
                OutputStream os = connection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(query);
                bw.flush();
                bw.close();
                os.close();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null){
                    sb.append(line);
                }
                Log.i("POST",sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    static final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            textView1.setText(msg.obj.toString());
        }
    }

}
