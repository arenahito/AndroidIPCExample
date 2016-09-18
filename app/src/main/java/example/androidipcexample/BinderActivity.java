package example.androidipcexample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BinderActivity extends AppCompatActivity {

    private IBinderService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);
        bindService(new Intent(this, BinderService.class), serviceConnection, BIND_AUTO_CREATE);
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
            service = IBinderService.Stub.asInterface(binder);
            try {
                service.start(new IBinderServiceCallback.Stub() {
                    @Override
                    public void render(MetaData meta, byte[] jpeg) throws RemoteException {
                        if (jpeg.length != 500 * 1024) {
                            Log.e("test", "データ壊れてる疑惑あり");
                        }
                    }
                });
            } catch (RemoteException e) {
                // 虫
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}
