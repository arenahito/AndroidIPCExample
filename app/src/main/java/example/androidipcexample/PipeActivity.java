package example.androidipcexample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class PipeActivity extends AppCompatActivity {

    private IPipeService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe);
        bindService(new Intent(this, PipeService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (service != null) {
            try {
                service.stop();
            } catch (RemoteException e) {
                // 無視
            }
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = IPipeService.Stub.asInterface(binder);
            try {
                final ParcelFileDescriptor fd = service.start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try (ObjectInputStream is = new ObjectInputStream(
                                new BufferedInputStream(
                                        new ParcelFileDescriptor.AutoCloseInputStream(fd)
                                )
                        )) {
                            byte[] jpeg = new byte[500 * 1024];

                            while (true) {
                                MetaData meta = (MetaData) is.readObject();

                                int jpegSize = is.readInt();
                                if (jpegSize != 500 * 1024) {
                                    Log.e("test", "データ壊れてる疑惑あり");
                                }

                                is.readFully(jpeg, 0, jpegSize);
                            }

                        } catch (IOException | ClassNotFoundException e) {
                            // 無視
                        }
                    }
                }).start();

            } catch (RemoteException e) {
                // 虫
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
